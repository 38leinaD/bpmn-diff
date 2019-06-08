package de.dplatz.bpmndiff;

import java.awt.Desktop;
import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserDetection {

	Logger logger = LoggerFactory.getLogger(BrowserDetection.class);
	
	boolean open(URI uri) {
		
		logger.debug("Checking for google-chrome executable...");
		if (isExecutableOnPath(getExecutablePath(), "google-chrome")) {
			String cmd = "google-chrome --app=" + uri.toString();
			logger.debug("Present! Running '" + cmd + "'.");
			try {
				Process p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
				return true;
				
			} catch (Exception e) {
				return false;
			}
		}
		
		// fallback
		logger.info("Unable to find browser executable. Trying to open with default browser.");
		try {
			Desktop.getDesktop().browse(uri);
		} catch (Exception e) {
			logger.error("Error opening URL in default browser.", e);
			return false;
		}
		
		return false;
	}

	List<Path> getExecutablePath() {
		return Arrays.asList(System.getenv("PATH").split(File.pathSeparator)).stream()
				.map(s -> Paths.get(s))
				.filter(p -> p.toFile().isDirectory())
				.collect(Collectors.toList());
	}

	boolean isExecutableOnPath(List<Path> pathVar, String exe) {
		return pathVar.stream()
				.filter(p -> this.isExecutableInDir(p, exe))
				.findAny()
				.isPresent();
	}

	boolean isExecutableInDir(Path dir, String exe) {
		return dir.toFile().listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().equals(exe) && file.canExecute();
			}
		}).length > 0;
	}
}
