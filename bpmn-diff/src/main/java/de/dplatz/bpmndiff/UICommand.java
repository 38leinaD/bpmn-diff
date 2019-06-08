package de.dplatz.bpmndiff;

import java.awt.Desktop;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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

	@Option(names = { "-b", "--open-browser" }, description = "...")
	boolean openBrowser = true;

	@Parameters(arity = "2", paramLabel = "FILE", description = "File(s) to diff.")
	private File[] inputFiles;

	public static void main(String[] args) throws Exception {
		PicocliRunner.run(UICommand.class, args);
	}

	private RxHttpClient serverConnector;

	public void run() {
		EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class);
		serverConnector = createServerConnector(server);

		configureServer();

		URI webappUri = resolveWebapp(server);

		if (openBrowser) {
			try {
				Desktop.getDesktop().browse(new URI(server.getURI().toString() + "/ui/index.html"));
			} catch (Exception e) {
				System.err.println("Unable to open the webbrowser at " + webappUri + ". Please open manually.");
			}
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
		configureFile(serverConnector, inputFiles[0].toPath());
		configureFile(serverConnector, inputFiles[1].toPath());

	}

	private void configureFile(RxHttpClient client, Path file) {
		Map<String, String> obj = new HashMap<>();
		obj.put("path", file.toString());
		HttpStatus status = client.exchange(HttpRequest.PUT("/files", obj).contentType(MediaType.APPLICATION_JSON_TYPE))
				.blockingFirst().getStatus();

		if (status.getCode() != 200) {
			System.err.println("Unable to configure file " + file + ". Response-code: " + status.getCode());
		}
	}
}
