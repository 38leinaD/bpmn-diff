package de.dplatz.bpmndiff.boundary;

import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

@Controller("/exit")
public class ExitResource {
	
	@Post
	@Consumes("text/plain")
	public void exit() {
		System.exit(0);
	}
}