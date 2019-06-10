package de.dplatz.bpmndiff;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.dplatz.bpmndiff.entity.Diff;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;

@MicronautTest
public class FileDiffTest {

	@Inject
    @Client("/diff")
    RxHttpClient client; 
	
	@Test
	void testit() {
		Assertions.assertEquals(client.exchange(HttpRequest.PUT("/left", "{ \"path\": \"./src/test/resources/diffs/file-diff/a/flow.bpmn\" }"))
			.blockingFirst()
			.getStatus()
			.getCode(), 200);
		Assertions.assertEquals(client.exchange(HttpRequest.PUT("/right", "{ \"path\": \"./src/test/resources/diffs/file-diff/b/flow.bpmn\" }"))
				.blockingFirst()
				.getStatus()
				.getCode(), 200);

		Diff diff = client.toBlocking().retrieve("/", Diff.class);

		assertEquals(Paths.get("./src/test/resources/diffs/file-diff/a/flow.bpmn").toAbsolutePath().normalize(), diff.getLeftPath());
		assertEquals(Paths.get("./src/test/resources/diffs/file-diff/b/flow.bpmn").toAbsolutePath().normalize(), diff.getRightPath());
		assertEquals(diff.getType(), Diff.Type.Modified);
	}
}
