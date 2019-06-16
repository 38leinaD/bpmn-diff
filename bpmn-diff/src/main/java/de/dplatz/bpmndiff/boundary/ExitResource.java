package de.dplatz.bpmndiff.boundary;

import javax.inject.Inject;

import de.dplatz.bpmndiff.SharedConfig;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

@Controller("/exit")
public class ExitResource {
	
	@Post
	@Consumes("text/plain")
	public void exit() {
		if (SharedConfig.getInstance().shouldExitOnBeacon()) System.exit(0);
	}
}