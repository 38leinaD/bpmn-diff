package de.dplatz.bpmndiff.boundary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;

@Controller("/files")
public class FilesResource {
	static List<String> files = new ArrayList<>();
	static {
		//files.add("/home/daniel/junk/bpmn/bpmn-js-diffing/resources/pizza-collaboration/old.bpmn");
		//files.add("/home/daniel/junk/bpmn/bpmn-js-diffing/resources/pizza-collaboration/new.bpmn");
	}
	
	@Put
	public void add(Map<String, String> file) {
		System.out.println("PUT: " + file.get("path"));
		files.add(file.get("path"));
	}
	
	@Get
	public List<String> files() {
		return files;
	}
	
	@Get(value = "/{index}", produces = MediaType.TEXT_PLAIN)
	public String file(Integer index) throws IOException {
		return Files
				.readAllLines(
						Paths.get(files.get(index)))
				.stream().collect(Collectors.joining("\n"));
	}
}