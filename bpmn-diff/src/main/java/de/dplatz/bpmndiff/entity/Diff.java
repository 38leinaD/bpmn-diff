package de.dplatz.bpmndiff.entity;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class Diff extends Node {
	
	Logger logger = LoggerFactory.getLogger(Diff.class);

	
	public enum Type {
		Added,
		Removed,
		Modified
	}
	
	public Diff() {
		super();
	}

	public static Map<String, Diff> DIFF_REGISTRY = new HashMap<String, Diff>();
	
	private Type type;
	private String id;
	private boolean supported;
	
	private Diff(Path left, Path right, Type type) {
		super(left, right);
		this.type = type;
		this.id = UUID.randomUUID().toString();
		DIFF_REGISTRY.put(this.id, this);
	}

	public static Node ofPaths(Path left, Path right) {
		if (isDir(left, right)) {
			
			List<Node> children = new LinkedList<>();
			
			File[] leftDirectoryContents = new File[0];
			File[] rightDirectoryContents = new File[0];

			if (left != null) {
				leftDirectoryContents = left.toFile().listFiles();
			}
			if (right != null) {
				rightDirectoryContents = right.toFile().listFiles();
			}
			
			// left - here we catch modified and removed
			for (int i=0; i<leftDirectoryContents.length; i++) {
				Path leftFile = leftDirectoryContents[i].toPath();
				Path rightFile = dirContainsFile(rightDirectoryContents, leftFile.getFileName().toString());
				
				children.add(Diff.ofPaths(leftFile, rightFile));
			}
			
			// right - here we catch added
			for (int i=0; i<rightDirectoryContents.length; i++) {
				Path rightFile = rightDirectoryContents[i].toPath();
				Path leftFile = dirContainsFile(leftDirectoryContents, rightFile.getFileName().toString());
				
				if (leftFile == null) {
					// added
					children.add(Diff.ofPaths(null, rightFile));
				}
			}
			children = children.stream().sorted().collect(Collectors.toList());
			return new Directory(left, right, children);
		}
		else {
			Type type = Type.Modified;
			if (left == null) type = Type.Added;
			if (right == null) type = Type.Removed;
			Diff diff = new Diff(left, right, type);
			
			diff.determineIfSupported();
			
			return diff;
		}
	}
	
	private static Path dirContainsFile(File[] files, String filename) {
		for (int i=0; i<files.length; i++) {
			if (files[i].getName().equals(filename)) return files[i].toPath();
		}
		return null;
	}
	
	public static boolean isDir(Path left, Path right) {
		return (left != null && left.toFile().isDirectory()) || (right != null && right.toFile().isDirectory());
	}
	
	private void determineIfSupported() {
		supported = this.isValidXmlFile(this.getLeftPath()) || this.isValidXmlFile(this.getRightPath());
	}
	
	private boolean isValidXmlFile(Path p) {

		try {
			if (p == null) return false;
			if (!p.toFile().exists()) return false;
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
	
			SAXParser parser = factory.newSAXParser();
	
			XMLReader reader = parser.getXMLReader();
			reader.parse(new InputSource(new FileInputStream(p.toFile())));
			
			return true;
		}
		catch (SAXParseException spe) {
			return false;
		}
		catch (Exception e) {
			// TODO: these errors should probably be forwarded to the UI and displayed instead of printed to the log.
			logger.error(String.format("Error while determining if file (%s) is a valid BPMN-file.",  p.getFileName().toString()), e);
			return false;
		}
	}

	public Type getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public boolean isSupported() {
		return supported;
	}
}
