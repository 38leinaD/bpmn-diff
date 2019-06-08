package de.dplatz.bpmndiff;

import io.micronaut.runtime.Micronaut;

public class Application {

	static {
		//System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
	}
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}