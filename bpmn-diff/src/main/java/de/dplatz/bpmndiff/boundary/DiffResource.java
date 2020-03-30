package de.dplatz.bpmndiff.boundary;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.dplatz.bpmndiff.control.Differ;

@ApplicationScoped
@Path("/diff")
public class DiffResource {
	
	@Inject
	Differ differ;
	
	@GET
	@Path("/{id}/{side}")
	@Produces("text/plain")
	public String file(@PathParam("id") String id, @PathParam("side") String side) throws IOException {
		return differ.file(id, side);
	}
	
	@GET
	@Produces("application/json")
	public Object diff() throws IOException {
		return differ.diff();
	}
}