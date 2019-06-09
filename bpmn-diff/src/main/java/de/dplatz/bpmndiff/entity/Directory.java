package de.dplatz.bpmndiff.entity;

import java.nio.file.Path;
import java.util.List;

public class Directory extends Node {
	private final List<Node> children;

	public Directory(Path left, Path right, List<Node> children) {
		super(left, right);
		this.children = children;
	}

	public List<Node> getChildren() {
		return children;
	}
}
