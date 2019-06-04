package de.dplatz.bpmndiff.boundary;

import org.reactivestreams.Publisher;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.sse.Event;

@Controller("/logs")
public class LogsResource {

	@Get
	public Publisher<Event<String>> index() {
		return null;
	}
}