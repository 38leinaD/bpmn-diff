package de.dplatz.bpmndiff;

import java.nio.file.Path;

public class SharedConfig {
	// TODO: Sharing config between Micronaut und picocli via @Singleton does not work...
	static SharedConfig INSTANCE = new SharedConfig();
	
	public static SharedConfig getInstance() {
		return INSTANCE;
	}
	
	private boolean exitOnBeacon = true;
	private Path left;
	private Path right;
	
	public boolean shouldExitOnBeacon() {
		return exitOnBeacon;
	}
	
	public void exitOnBeacon(boolean val) {
		this.exitOnBeacon = val;
	}

	public Path getLeft() {
		return left;
	}

	public void setLeft(Path left) {
		this.left = left.normalize();
	}

	public Path getRight() {
		return right;
	}

	public void setRight(Path right) {
		this.right = right.normalize();
	}
	
	
}
