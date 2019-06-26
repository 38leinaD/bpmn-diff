package de.dplatz.bpmndiff;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

import javax.inject.Singleton;

@Singleton
public class SharedConfig {
	
	private boolean exitOnBeacon = true;
	private Path left;
	private Path right;
	
	public CountDownLatch exitLatch = new CountDownLatch(1);
	
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
