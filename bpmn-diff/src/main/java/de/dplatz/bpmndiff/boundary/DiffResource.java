package de.dplatz.bpmndiff.boundary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import de.dplatz.bpmndiff.entity.Diff;
import de.dplatz.bpmndiff.entity.Directory;
import de.dplatz.bpmndiff.entity.Node;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;

@Singleton
@Controller("/diff")
public class DiffResource {
	Path left;
	Path right;
	
	@Put(uri = "/{side}")
	public void register(Map<String, String> file, @PathVariable String side) {
		if (side.equals("left")) {
			left = Paths.get(file.get("path")).normalize();
		}
		else {
			right = Paths.get(file.get("path")).normalize();
		}
	}
/*
	@Deprecated
	@Get
	public List<String> files() {
		List<String> l = new LinkedList<>();
		l.add(left.toString());
		l.add(right.toString());
		return l;
	}
	*/	
	@Get(value = "/{id}/{side}", produces = MediaType.TEXT_PLAIN)
	public String file(String id, String side) throws IOException {
		Diff diff= Diff.DIFF_REGISTRY.get(id);
		Path left = diff.getLeftPath();
		Path right = diff.getRightPath();
		return Files
				.readAllLines(
						side.equals("left") ? left : right)
				.stream().collect(Collectors.joining("\n"));
	}
	
	@Get(produces = MediaType.APPLICATION_JSON)
	public Object diff() throws IOException {
		Node diff = Diff.ofPaths(left, right);
		if (Directory.class.isInstance(diff)) {
			return Directory.class.cast(diff).getChildren();
		}
		else {
			return diff;
		}
	}
}