package de.dplatz.bpmndiff.browsers;

import java.net.URI;

public abstract class Browser {
	abstract public boolean open(URI open);
	
	protected boolean runExecutable(String cmd) {
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
			return true;
			
		} catch (Exception e) {
			return false;
		}
	}
}
