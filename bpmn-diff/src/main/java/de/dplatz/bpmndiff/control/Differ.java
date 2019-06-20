package de.dplatz.bpmndiff.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import de.dplatz.bpmndiff.SharedConfig;
import de.dplatz.bpmndiff.entity.Diff;
import de.dplatz.bpmndiff.entity.Directory;

@Singleton
public class Differ {
	
	Object diffResult = null;
	
	public void reset() {
		diffResult = null;
	}
	
	public String file(String id, String side) throws IOException {
		Diff diff= Diff.DIFF_REGISTRY.get(id);
		Path left = diff.getLeftPath();
		Path right = diff.getRightPath();
		return Files
				.readAllLines(
						side.equals("left") ? left : right)
				.stream().collect(Collectors.joining("\n"));
	}
	
	public Object diff() throws IOException {
		SharedConfig config = SharedConfig.getInstance();
		if (diffResult == null) {
			diffResult = Diff.ofPaths(config.getLeft(), config.getRight());
			if (Directory.class.isInstance(diffResult)) {
				diffResult = Directory.class.cast(diffResult).getChildren();
			}
		}
		return diffResult;
	}
}
