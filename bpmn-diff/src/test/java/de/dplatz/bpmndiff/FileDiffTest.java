package de.dplatz.bpmndiff;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import de.dplatz.bpmndiff.control.Differ;
import de.dplatz.bpmndiff.entity.Diff;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;

@MicronautTest
public class FileDiffTest {

	@Inject
    @Client("/diff")
    RxHttpClient client; 
	
	@Inject
	Differ differ;
	
	@Test
	void should_diff_bpmn_files() {

		// TODO
		differ.reset();
		SharedConfig.getInstance().setLeft(Paths.get("./src/test/resources/diffs/file-diff/a/flow.bpmn"));
		SharedConfig.getInstance().setRight(Paths.get("./src/test/resources/diffs/file-diff/b/flow.bpmn"));
		
		Diff diff = client.toBlocking().retrieve("/", Diff.class);

		assertEquals(Paths.get("./src/test/resources/diffs/file-diff/a/flow.bpmn").toAbsolutePath().normalize(), diff.getLeftPath());
		assertEquals(Paths.get("./src/test/resources/diffs/file-diff/b/flow.bpmn").toAbsolutePath().normalize(), diff.getRightPath());
		assertEquals(true, diff.isSupported());

		assertEquals(Diff.Type.Modified, diff.getType());
	}
	
	@Test
	void should_should_not_support_text_files() {
		differ.reset();
		SharedConfig.getInstance().setLeft(Paths.get("./src/test/resources/diffs/file-diff/a/justtext.txt"));
		SharedConfig.getInstance().setLeft(Paths.get("./src/test/resources/diffs/file-diff/b/justtext.txt"));
		
		Diff diff = client.toBlocking().retrieve("/", Diff.class);
		
		assertEquals(false, diff.isSupported());
	}
}
