package de.dplatz.bpmndiff.boundary;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import de.dplatz.bpmndiff.SharedConfig;

@Path("/exit")
public class ExitResource {
	
	@Inject
	SharedConfig config;
	
	@POST
	@Consumes("text/plain")
	public void exit() {
		if (config.shouldExitOnBeacon()) {
			//config.exitLatch.countDown();
			System.out.println("EXIT REQUESTED BY BROWSER");
			System.exit(0);
		}
	}
}