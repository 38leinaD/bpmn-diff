package de.dplatz.bpmndiff.browsers;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnvPath {
	
	private List<Path> path;

	public EnvPath() {
		this.path = getExecutablePath();
	}
	
	private List<Path> getExecutablePath() {
		return Arrays.asList(System.getenv("PATH").split(File.pathSeparator)).stream()
				.map(s -> Paths.get(s))
				.filter(p -> p.toFile().isDirectory())
				.collect(Collectors.toList());
	}

	public boolean isExecutableOnPath(String exe) {
		return path.stream()
				.filter(p -> p.toFile().exists() && p.toFile().isDirectory())
				.filter(p -> this.isExecutableInDir(p, exe))
				.findAny()
				.isPresent();
	}

	private boolean isExecutableInDir(Path dir, String exe) {
		return dir.toFile().listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().equals(exe) && file.canExecute();
			}
		}).length > 0;
	}
}
