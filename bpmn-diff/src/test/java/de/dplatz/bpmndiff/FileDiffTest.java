package de.dplatz.bpmndiff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.dplatz.bpmndiff.boundary.JsonbConfiguration;
import de.dplatz.bpmndiff.control.Differ;
import de.dplatz.bpmndiff.entity.Diff;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FileDiffTest {

	private WebTarget tut;

	
	@TestHTTPResource("/diff") 
    URL url;
	
	@BeforeEach
	public void init() throws URISyntaxException {
		tut = ClientBuilder.newClient().target(url.toURI());
	}
	
	@Inject
	Differ differ;
	
	@Inject
	SharedConfig config;
	
	@Test
	void should_diff_bpmn_files() {
		// TODO
		differ.reset();
		config.setLeft(Paths.get("./src/test/resources/diffs/file-diff/a/flow.bpmn").toAbsolutePath());
		config.setRight(Paths.get("./src/test/resources/diffs/file-diff/b/flow.bpmn").toAbsolutePath());
		
		// TODO: Is there a better way???
		Diff diff = get(tut, Diff.class);
		
	    assertNotNull(diff.getId());

		assertEquals(Paths.get("./src/test/resources/diffs/file-diff/a/flow.bpmn").toAbsolutePath().normalize(), diff.getLeftPath());
		assertEquals(Paths.get("./src/test/resources/diffs/file-diff/b/flow.bpmn").toAbsolutePath().normalize(), diff.getRightPath());
		assertEquals(true, diff.isSupported());

		assertEquals(Diff.Type.Modified, diff.getType());
	}
	
	@Test
	void should_should_not_support_text_files() {
		differ.reset();
		config.setLeft(Paths.get("./src/test/resources/diffs/file-diff/a/justtext.txt").toAbsolutePath());
		config.setLeft(Paths.get("./src/test/resources/diffs/file-diff/b/justtext.txt").toAbsolutePath());
		
		Diff diff = get(tut, Diff.class);
        
		assertNotNull(diff.getId());

		assertEquals(false, diff.isSupported());
	}
	
	private <T> T get(WebTarget target, Class<T> clazz) {
	    String responseBody = tut.request().get(String.class);
        Jsonb jsonb = new JsonbConfiguration().getContext(null);
        return jsonb.fromJson(responseBody, clazz);
	}
}
