package io.kokuwa.micronaut.audit.test;

import java.util.UUID;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import io.kokuwa.micronaut.audit.domain.Model;
import io.kokuwa.micronaut.audit.domain.ModelRepository;
import lombok.RequiredArgsConstructor;

/**
 * Provider for data in tests.
 *
 * @author Stephan Schnabel
 */
@Singleton
@Transactional(TxType.REQUIRES_NEW)
@RequiredArgsConstructor
public class DataProvider {

	private final ModelRepository modelRepository;

	public Model model() {
		return model(UUID.randomUUID().toString());
	}

	public Model model(String name) {
		return modelRepository.save(new Model()
				.setUuid(UUID.randomUUID())
				.setName(name)
				.setAuthor(UUID.randomUUID().toString()));
	}

	public void deletAll() {
		modelRepository.findOrderByUpdated().map(Model::getUuid).forEach(modelRepository::deleteByUuid);
	}
}
