package de.dplatz.bpmndiff;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;

@ApplicationScoped
public class SharedConfig {
	
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
