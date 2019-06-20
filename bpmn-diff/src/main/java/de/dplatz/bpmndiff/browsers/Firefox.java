package de.dplatz.bpmndiff.browsers;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Firefox extends Browser {
	Logger logger = LoggerFactory.getLogger(Firefox.class);

	private EnvPath envPath;

	public Firefox(EnvPath envPath) {
		this.envPath = envPath;
	}
	
	@Override
	public boolean open(URI uri) {
		logger.debug("Checking for 'firefox' executable...");
		if (envPath.isExecutableOnPath("firefox")) {
			String cmd = "firefox " + uri.toString();
			logger.debug("Present! Running '" + cmd + "'.");
			return runExecutable(cmd);
		}
		else {
			return false;
		}
	}

}
