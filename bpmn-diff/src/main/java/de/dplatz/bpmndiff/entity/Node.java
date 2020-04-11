package de.dplatz.bpmndiff.entity;

import java.nio.file.Path;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Node implements Comparable<Node> {
	private Path leftPath, rightPath;
	private String leftName, rightName;
	public Node() {
		super();
	}
	
	public Node(Path left, Path right) {
		super();
		this.leftPath = left;
		this.rightPath = right;
		
		if (this.leftPath != null) {
		    leftName = leftPath.getFileName().toString();
		}
		if (this.rightPath != null) {
		    rightName = rightPath.getFileName().toString();
		}
	}

	public Path getLeftPath() {
		return leftPath;
	}

	public Path getRightPath() {
		return rightPath;
	}

	@Override
	public int compareTo(Node o) {
		return _getFileName().compareTo(o._getFileName());
	}
	
	private String _getFileName() {
		if (leftPath != null) return leftPath.getFileName().toString();
		else return rightPath.getFileName().toString();
	}
}
