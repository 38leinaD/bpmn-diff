package de.dplatz.bpmndiff;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.dplatz.bpmndiff.control.Differ;
import de.dplatz.bpmndiff.entity.Diff;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FileDiffTest {

	private WebTarget tut;

	@BeforeEach
	public void init() {
		tut = ClientBuilder.newClient().target("http://localhost:8081/diff");
	}
	
	@Inject
	Differ differ;
	
	@Inject
	SharedConfig config;
	
	@Test
	void should_diff_bpmn_files() {

		// TODO
		differ.reset();
		config.setLeft(Paths.get("./src/test/resources/diffs/file-diff/a/flow.bpmn"));
		config.setRight(Paths.get("./src/test/resources/diffs/file-diff/b/flow.bpmn"));
		
		Diff diff = tut.request().get(Diff.class);
		
		assertEquals(Paths.get("./src/test/resources/diffs/file-diff/a/flow.bpmn").toAbsolutePath().normalize(), diff.getLeftPath());
		assertEquals(Paths.get("./src/test/resources/diffs/file-diff/b/flow.bpmn").toAbsolutePath().normalize(), diff.getRightPath());
		assertEquals(true, diff.isSupported());

		assertEquals(Diff.Type.Modified, diff.getType());
	}
	
	@Test
	void should_should_not_support_text_files() {
		differ.reset();
		config.setLeft(Paths.get("./src/test/resources/diffs/file-diff/a/justtext.txt"));
		config.setLeft(Paths.get("./src/test/resources/diffs/file-diff/b/justtext.txt"));
		
		Diff diff = tut.request().get(Diff.class);
		
		assertEquals(false, diff.isSupported());
	}
}
