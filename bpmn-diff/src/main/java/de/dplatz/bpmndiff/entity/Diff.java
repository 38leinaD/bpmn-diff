package de.dplatz.bpmndiff.entity;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Diff extends Node {
	enum Type {
		Added,
		Removed,
		Modified
	}
	
	public static Map<String, Diff> DIFF_REGISTRY = new HashMap<String, Diff>();
	
	private final Type type;
	private final String id;
	private Diff(Path left, Path right, Type type) {
		super(left, right);
		this.type = type;
		this.id = UUID.randomUUID().toString();
		DIFF_REGISTRY.put(this.id, this);
	}
		
	public static Diff ofFiles(Path left, Path right) {
		if (!isDir(left, right)) {
			Type type = Type.Modified;
			if (left == null) type = Type.Added;
			if (right == null) type = Type.Removed;
			Diff diff = new Diff(left, right, type);
			
			return diff;
		}
		else {
			throw new AssertionError("Not a file. " + left + "," + right);
		}
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

	public Type getType() {
		return type;
	}

	public String getId() {
		return id;
	}
}
