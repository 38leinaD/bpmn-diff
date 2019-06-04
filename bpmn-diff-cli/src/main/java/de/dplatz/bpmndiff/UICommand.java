package de.dplatz.bpmndiff;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ui", description = "...", mixinStandardHelpOptions = true)
public class UICommand implements Runnable {

	@Option(names = { "-v", "--verbose" }, description = "...")
	boolean verbose;

	@Option(names = { "-b", "--open-browser" }, description = "...")
	boolean openBrowser = true;

	public static void main(String[] args) throws Exception {
		PicocliRunner.run(UICommand.class, args);
	}

	public void run() {
		// business logic here
		System.out.println("FIRST HELLO");

		EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class);
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
