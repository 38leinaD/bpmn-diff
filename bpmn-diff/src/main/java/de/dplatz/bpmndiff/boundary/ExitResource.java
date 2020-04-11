package de.dplatz.bpmndiff.boundary;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import de.dplatz.bpmndiff.SharedConfig;
import io.quarkus.runtime.Quarkus;

@Path("/exit")
public class ExitResource {
	
	@Inject
	SharedConfig config;
	
	@POST
	@Consumes("text/plain")
	public void exit() {
		if (config.shouldExitOnBeacon()) {
		    Quarkus.asyncExit(0);
		}
	}
}