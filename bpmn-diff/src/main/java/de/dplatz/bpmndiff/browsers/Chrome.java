package de.dplatz.bpmndiff.browsers;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chrome extends Browser {

	Logger logger = LoggerFactory.getLogger(Chrome.class);

	private static final String[] CHROME_EXECUTABLES = { "google-chrome", "google" };
	
	private EnvPath envPath;

	public Chrome(EnvPath envPath) {
		this.envPath = envPath;
	}
	
	@Override
	public boolean open(URI uri) {
		for (String exe : CHROME_EXECUTABLES) {
		    logger.debug("Checking for '" + exe + "' executable...");

    		if (envPath.isExecutableOnPath(exe)) {
    			String cmd = "google-chrome --incognito --app=" + uri.toString();
    			logger.debug("Present! Running '" + cmd + "'.");
    			return runExecutable(cmd);
    		}
		}
		return false;
	}

}
