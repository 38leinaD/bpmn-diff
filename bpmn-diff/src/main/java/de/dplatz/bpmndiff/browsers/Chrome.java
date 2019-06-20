package de.dplatz.bpmndiff.browsers;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chrome extends Browser {

	Logger logger = LoggerFactory.getLogger(Chrome.class);

	private EnvPath envPath;

	public Chrome(EnvPath envPath) {
		this.envPath = envPath;
	}
	
	@Override
	public boolean open(URI uri) {
		logger.debug("Checking for 'google-chrome' executable...");
		if (envPath.isExecutableOnPath("google-chrome")) {
			String cmd = "google-chrome --app=" + uri.toString();
			logger.debug("Present! Running '" + cmd + "'.");
			return runExecutable(cmd);
		}
		else {
			return false;
		}
	}

}
