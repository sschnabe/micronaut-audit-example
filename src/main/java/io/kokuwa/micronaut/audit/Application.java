package io.kokuwa.micronaut.audit;

import io.micronaut.runtime.Micronaut;

/**
 * Micronaut application.
 *
 * @author Stephan Schnabel
 */
public class Application {

	public static void main(String[] args) {
		Micronaut.run(Application.class, args);
	}
}
