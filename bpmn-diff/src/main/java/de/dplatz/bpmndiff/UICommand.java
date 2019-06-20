package de.dplatz.bpmndiff;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dplatz.bpmndiff.boundary.DiffResource;
import de.dplatz.bpmndiff.browsers.BrowserDetection;
import de.dplatz.bpmndiff.browsers.BrowserDetection.Strategy;
import de.dplatz.bpmndiff.entity.Diff;
import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ui", description = "...", mixinStandardHelpOptions = true)
public class UICommand implements Runnable {
	Logger logger = LoggerFactory.getLogger(UICommand.class);

	@Option(names = { "-b", "--browser" }, description = "Browser to use: ${COMPLETION-CANDIDATES}", showDefaultValue = Visibility.ALWAYS)
	BrowserDetection.Strategy strategy = Strategy.BestFit;
	
	@Option(names = { "-o", "--open-browser" }, description = "Open a browser-window.")
	boolean openBrowser = true;

	@Option(names = { "-p", "--port" }, description = "Port of the http-server.")
	int port = -1;
	
	@Parameters(arity = "2", paramLabel = "FILE", description = "File(s) to diff.")
	private File[] inputFiles;

	public static void main(String[] args) throws Exception {
		PicocliRunner.run(UICommand.class, args);
	}
		
	@Inject
	DiffResource differ;

	public void run() {
		Map<String, Object> config = new HashMap<>();
		config.put("micronaut.server.port", port);
		EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, config);
		
		configureServer();

		try {
			Object diff = differ.diff();
			
			if (diff instanceof Diff) {
				Diff fileDiff = Diff.class.cast(diff);
				if (!fileDiff.isSupported()) {
					logger.info(String.format("File-diff on unsupported file %s. Exiting.", fileDiff.toString()));
					System.exit(-1);
				}
			}
			
		} catch (IOException e) {
			logger.error("Error while diffing.", e);
			System.exit(-1);
		}
		
		URI webappUri = resolveWebapp(server);

		if (openBrowser) {
			if (!strategy.open(webappUri)) {
				logger.error("Unable to open browser at '" + webappUri + "'. Please open URL manually.");
			}
		}
		else {
			System.out.println("Please manually open '" + webappUri + "'.");
			SharedConfig.getInstance().exitOnBeacon(false);
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

	private void configureServer() {
		SharedConfig.getInstance().setLeft(inputFiles[0].toPath());
		SharedConfig.getInstance().setRight(inputFiles[1].toPath());
	}
}
