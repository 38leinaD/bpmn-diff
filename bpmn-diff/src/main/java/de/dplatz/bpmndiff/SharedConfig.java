package de.dplatz.bpmndiff;

import javax.inject.Singleton;

@Singleton
public class SharedConfig {

	private boolean exitOnBeacon = true;
	
	public boolean shouldExitOnBeacon() {
		return exitOnBeacon;
	}
	
	public void exitOnBeacon(boolean val) {
		this.exitOnBeacon = val;
	}
}
