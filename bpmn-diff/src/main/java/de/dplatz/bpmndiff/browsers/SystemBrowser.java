package de.dplatz.bpmndiff.browsers;

import java.awt.Desktop;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemBrowser extends Browser {
	Logger logger = LoggerFactory.getLogger(SystemBrowser.class);

	@Override
	public boolean open(URI uri) {
		logger.info("Trying to open with system browser.");
		try {
			Desktop.getDesktop().browse(uri);
			return true;
		} catch (Exception e) {
			logger.error("Error opening URL in default browser.", e);
			return false;
		}
	}

}
