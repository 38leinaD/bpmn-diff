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

	@Parameters(arity = "2", paramLabel = "FILE", description = "File(s) to diff.")
	private File[] inputFiles;

	public static void main(String[] args) throws Exception {
		Map<String, Object> config = new HashMap<>();
		config.put("micronaut.server.port", System.getProperty("port", "-1"));
		try (ApplicationContext appContext = ApplicationContext.run(config)) {
			PicocliRunner.run(UICommand.class, appContext, args);
		}
	}
		
	@Inject
	ApplicationContext appContext;
	
	@Inject
	SharedConfig sharedConfig;
	
	@Inject
	DiffResource differ;

	public void run() {
		EmbeddedServer server = appContext.getBean(EmbeddedServer.class);
		
		if(!server.isRunning()) {
			server.start();
		}
		
		sharedConfig.setLeft(inputFiles[0].toPath());
		sharedConfig.setRight(inputFiles[1].toPath());

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
			sharedConfig.exitOnBeacon(false);
		}
		
		try {
			sharedConfig.exitLatch.await();
		} catch (InterruptedException e) {
			logger.error("Error while waiting on exit-latch", e);
		}
		System.out.println("Goodbye!");
		System.exit(0);
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
}
