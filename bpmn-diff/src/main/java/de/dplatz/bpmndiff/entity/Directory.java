package de.dplatz.bpmndiff.entity;

import java.nio.file.Path;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Directory extends Node {
	private List<Node> children;

	public Directory(Path left, Path right, List<Node> children) {
		super(left, right);
		this.children = children;
	}

	public List<Node> getChildren() {
		return children;
	}
}
