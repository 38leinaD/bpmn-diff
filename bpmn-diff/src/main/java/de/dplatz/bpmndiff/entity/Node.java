package de.dplatz.bpmndiff.entity;

import java.nio.file.Path;

public class Node implements Comparable<Node> {
	private final Path leftPath, rightPath;

	public Node(Path left, Path right) {
		super();
		this.leftPath = left;
		this.rightPath = right;
	}

	public Path getLeftPath() {
		return leftPath;
	}

	public Path getRightPath() {
		return rightPath;
	}
	
	public String getLeftName() {
		return leftPath.getFileName().toString();
	}

	public String getRightName() {
		return rightPath.getFileName().toString();
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
