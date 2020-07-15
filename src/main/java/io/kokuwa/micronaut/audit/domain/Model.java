package io.kokuwa.micronaut.audit.domain;

import java.time.Instant;
import java.util.UUID;

import io.kokuwa.micronaut.audit.domain.audit.Auditable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Data;

/**
 * A model.
 *
 * @author Stephan Schnabel
 */
@Data
@MappedEntity
public class Model implements Auditable {

	@Id
	@GeneratedValue
	private Long id;

	@MappedProperty
	private UUID uuid;

	@MappedProperty
	private String name;

	@DateCreated
	private Instant created;

	@DateUpdated
	private Instant updated;

	@MappedProperty
	private String author;
}
