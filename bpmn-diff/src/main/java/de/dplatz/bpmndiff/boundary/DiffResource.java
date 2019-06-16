package de.dplatz.bpmndiff.boundary;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.dplatz.bpmndiff.control.Differ;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Singleton
@Controller("/diff")
public class DiffResource {
	
	@Inject
	Differ differ;
	
	@Get(value = "/{id}/{side}", produces = MediaType.TEXT_PLAIN)
	public String file(String id, String side) throws IOException {
		return differ.file(id, side);
	}
	
	@Get(produces = MediaType.APPLICATION_JSON)
	public Object diff() throws IOException {
		return differ.diff();
	}
}