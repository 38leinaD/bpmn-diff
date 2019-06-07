package de.dplatz.bpmndiff;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;import org.reactivestreams.Subscriber;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ui", description = "...", mixinStandardHelpOptions = true)
public class UICommand implements Runnable {

	@Option(names = { "-v", "--verbose" }, description = "...")
	boolean verbose;

	@Option(names = { "-b", "--open-browser" }, description = "...")
	boolean openBrowser = true;

	@Parameters(arity = "2", paramLabel = "FILE", description = "File(s) to diff.")
	private File[] inputFiles;

	public static void main(String[] args) throws Exception {
		PicocliRunner.run(UICommand.class, args);
	}

	@Client("https://api.github.com")
    @Inject RxHttpClient client; 
	
	public void run() {
		// business logic here
		System.out.println("FIRST HELLO " + inputFiles[0]);

		EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class);
		
		try {
			RxHttpClient client = RxHttpClient.create(server.getURI().toURL());
			Map<String, String> file = new HashMap<>();
			file.put("path", inputFiles[0].getPath().toString());
			HttpStatus status;
			status = client.exchange(
					HttpRequest.PUT("/files", file)
					.contentType(MediaType.APPLICATION_JSON_TYPE)
					).blockingFirst().getStatus();
			
			file.put("path", inputFiles[1].getPath().toString());
			
			status = client.exchange(
					HttpRequest.PUT("/files", file)
					.contentType(MediaType.APPLICATION_JSON_TYPE)
					).blockingFirst().getStatus();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		
		if (openBrowser) {
			try {
				Desktop.getDesktop().browse(new URI("http://localhost:8080/ui/index.html"));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println("Hi!" + server.getPort());
		System.out.println("===================STARTED===============");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("===================SHUTDOWN===============");

		System.out.println("Bye");
	}
}
