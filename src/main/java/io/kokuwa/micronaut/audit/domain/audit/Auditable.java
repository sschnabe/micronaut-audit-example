package io.kokuwa.micronaut.audit.domain.audit;

/**
 * Interface to implemented for auditing.
 *
 * @author Stephan Schnabel
 */
public interface Auditable {

	Object setAuthor(String author);
}
