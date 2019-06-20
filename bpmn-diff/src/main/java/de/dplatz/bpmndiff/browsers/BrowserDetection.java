package de.dplatz.bpmndiff.browsers;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserDetection {

	Logger logger = LoggerFactory.getLogger(BrowserDetection.class);

	static EnvPath ENV_PATH = new EnvPath();
	
	public enum Strategy {

		BestFit {
			@Override
			public boolean open(URI uri) {
				if (java.lang.System.getenv("BROWSER_COMMAND") != null) {
					String cmd = java.lang.System.getenv("BROWSER_COMMAND") + " " + uri.toString();
					logger.info("Detected BROWSER_COMMAND environment variable. Running '" + cmd + "'.");
					return runExecutable(cmd);
				}

				if (new Chrome(ENV_PATH).open(uri)) return true;
				if (new Firefox(ENV_PATH).open(uri)) return true;
				if (new SystemBrowser().open(uri)) return true;
				return false;
			}
		},
		Chrome {
			@Override
			public boolean open(URI uri) {
				return new Chrome(ENV_PATH).open(uri);
			}
		},
		Firefox {
			@Override
			public boolean open(URI uri) {
				return new Firefox(ENV_PATH).open(uri);
			}
		},
		System {
			@Override
			public boolean open(URI uri) {
				return new SystemBrowser().open(uri);
			}
		};
		
		public abstract boolean open(URI uri);
		Logger logger = LoggerFactory.getLogger(Strategy.class);
	}

	protected static boolean runExecutable(String cmd) {
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
			return true;

		} catch (Exception e) {
			return false;
		}
	}
}
