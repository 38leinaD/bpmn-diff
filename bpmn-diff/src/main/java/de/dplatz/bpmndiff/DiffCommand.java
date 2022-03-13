package de.dplatz.bpmndiff;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dplatz.bpmndiff.boundary.DiffResource;
import de.dplatz.bpmndiff.browsers.BrowserDetection;
import de.dplatz.bpmndiff.browsers.BrowserDetection.Strategy;
import de.dplatz.bpmndiff.entity.Diff;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@QuarkusMain
@Command(name = "diff", description = "...", mixinStandardHelpOptions = true)
public class DiffCommand implements Callable<Integer>, QuarkusApplication {
	static Logger logger = LoggerFactory.getLogger(DiffCommand.class);

	@Option(names = { "-b", "--browser" }, description = "Browser to use: ${COMPLETION-CANDIDATES}", showDefaultValue = Visibility.ALWAYS)
	BrowserDetection.Strategy strategy = Strategy.BestFit;
	
	@Option(names = { "-o", "--open-browser" }, description = "Open a browser-window.")
	boolean openBrowser = true;

	@Parameters(arity = "2", paramLabel = "FILE", description = "File(s) to diff.")
	private File[] inputFiles;

	@ConfigProperty(name = "quarkus.http.port")
	Integer assignedPort;
	
	@Inject
	SharedConfig sharedConfig;
	
	@Inject
	DiffResource differ;

	
    public static void main(String[] args) {
        Quarkus.run(DiffCommand.class, args);
    }
	
    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this).execute(args);
    }

	@Override
    public Integer call() throws Exception {
        
		sharedConfig.setLeft(inputFiles[0].toPath());
		sharedConfig.setRight(inputFiles[1].toPath());

		try {
			Object diff = differ.diff();
			
			if (diff instanceof Diff) {
				Diff fileDiff = Diff.class.cast(diff);
				if (!fileDiff.isSupported()) {
					logger.error(String.format("File-diff on unsupported file %s. Exiting.", fileDiff.toString()));
					return -1;
				}
			}
			
		} catch (IOException e) {
			logger.error("Error while diffing.", e);
			return -1;
		}

		//URI webappUri = resolveWebapp(server);
		URI webappUri = new URI("http://localhost:" + assignedPort + "/index.html");
		
		if (openBrowser) {
			if (!strategy.open(webappUri)) {
				logger.error("Unable to open browser at '" + webappUri + "'. Please open URL manually.");
			}
		}
		else {
			System.out.println("Please manually open '" + webappUri + "'.");
			sharedConfig.exitOnBeacon(false);
		}
		Quarkus.waitForExit();
		
        return 0;
    }
    
	/*
	private URI resolveWebapp(EmbeddedServer server) {
		URI webappUri;
		try {
			webappUri = new URI(server.getURI().toString() + "/ui/index.html");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return webappUri;
	}
	*/
}
