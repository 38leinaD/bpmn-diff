package de.dplatz.bpmndiff;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ui", description = "...", mixinStandardHelpOptions = true)
public class UICommand implements Runnable {
	Logger logger = LoggerFactory.getLogger(UICommand.class);

	@Option(names = { "-b", "--open-browser" }, description = "...")
	boolean openBrowser = true;

	@Parameters(arity = "2", paramLabel = "FILE", description = "File(s) to diff.")
	private File[] inputFiles;

	public static void main(String[] args) throws Exception {
		PicocliRunner.run(UICommand.class, args);
	}
	
	@Inject
	SharedConfig config;

	private RxHttpClient serverConnector;

	public void run() {
		EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class);
		serverConnector = createServerConnector(server);

		configureServer();

		URI webappUri = resolveWebapp(server);

		if (openBrowser) {
			if (!new BrowserDetection().open(webappUri)) {
				logger.error("Unable to open browser at '" + webappUri + "'. Please open URL manually.");
			}
		}
		else {
			config.exitOnBeacon(false);
		}
	}

	private URI resolveWebapp(EmbeddedServer server) {
		URI webappUri;
		try {
			webappUri = new URI(server.getURI().toString() + "/ui/index.html");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return webappUri;
	}

	private RxHttpClient createServerConnector(EmbeddedServer server) {
		try {
			return RxHttpClient.create(server.getURI().toURL());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private void configureServer() {
		configureFile(serverConnector, inputFiles[0].toPath(), "left");
		configureFile(serverConnector, inputFiles[1].toPath(), "right");

	}

	private void configureFile(RxHttpClient client, Path file, String side) {
		Map<String, String> obj = new HashMap<>();
		obj.put("path", file.toString());
		HttpStatus status = client.exchange(HttpRequest.PUT("/diff/" + side, obj).contentType(MediaType.APPLICATION_JSON_TYPE))
				.blockingFirst().getStatus();

		if (status.getCode() != 200) {
			System.err.println("Unable to configure file " + file + ". Response-code: " + status.getCode());
		}
	}
}
