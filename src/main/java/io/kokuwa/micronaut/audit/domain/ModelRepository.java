package io.kokuwa.micronaut.audit.domain;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import io.kokuwa.micronaut.audit.domain.audit.Audit;
import io.micronaut.context.annotation.Requires;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;

/**
 * Repository for {@link Model}.
 *
 * @author Stephan Schnabel
 */
@Audit
public interface ModelRepository extends GenericRepository<Model, Long> {

	Stream<Model> findOrderByUpdated();

	Optional<Model> findByUuid(UUID uuid);

	boolean existsByUuid(UUID uuid);

	Model save(Model model);

	Model update(Model model);

	int deleteByUuid(UUID uuid);
}

@Requires(property = "datasources.default.dialect", value = "H2")
@JdbcRepository(dialect = Dialect.H2)
interface ModelRepositoryH2 extends ModelRepository {}

@Requires(property = "datasources.default.dialect", value = "POSTGRES")
@JdbcRepository(dialect = Dialect.POSTGRES)
interface ModelRepositoryPostgres extends ModelRepository {}

@Requires(property = "datasources.default.dialect", value = "MYSQL")
@JdbcRepository(dialect = Dialect.MYSQL)
interface ModelRepositoryMysql extends ModelRepository {}
