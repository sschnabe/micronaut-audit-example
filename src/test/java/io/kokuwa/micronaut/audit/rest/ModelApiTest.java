package io.kokuwa.micronaut.audit.rest;

import static io.kokuwa.micronaut.audit.HttpResponseAssertions.assert204;
import static io.kokuwa.micronaut.audit.HttpResponseAssertions.assert401;
import static io.kokuwa.micronaut.audit.HttpResponseAssertions.assert404;
import static io.kokuwa.micronaut.audit.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.micronaut.audit.domain.ModelRepository;
import io.kokuwa.micronaut.audit.test.AbstractTest;

/**
 * Test for {@link ModelApi}.
 *
 * @author Stephan Schnabel
 */
public class ModelApiTest extends AbstractTest implements ModelApiTestSpec {

	@Inject
	ModelApiTestClient client;
	@Inject
	ModelRepository repository;

	@Test
	@DisplayName("getModels(200): get model list")
	@Override
	public void getModels200() {
		data.model();
		data.model();
		data.model();
		var models = assert200(() -> client.getModels(auth()));
		assertEquals(3, models.size(), "size of model list is invalid");
	}

	@Test
	@DisplayName("getModels(401): no token")
	@Override
	public void getModels401() {
		assert401(() -> client.getModels(null));
	}

	@Test
	@DisplayName("getModelByUuid(200): success")
	@Override
	public void getModelByUuid200() {
		var model = data.model();
		var vo = assert200(() -> client.getModelByUuid(auth(), model.getUuid()));
		assertAll("vo",
				() -> assertEquals(model.getUuid(), vo.getUuid(), "uuid"),
				() -> assertEquals(model.getName(), vo.getName(), "name"),
				() -> assertEquals(model.getAuthor(), vo.getAuthor(), "author"));
	}

	@Test
	@DisplayName("getModelByUuid(401): no token")
	@Override
	public void getModelByUuid401() {
		assert401(() -> client.getModelByUuid(null, UUID.randomUUID()));
	}

	@Test
	@DisplayName("getModelByUuid(404): model not exists")
	@Override
	public void getModelByUuid404() {
		assert404(() -> client.getModelByUuid(auth(), UUID.randomUUID()));
	}

	@Test
	@DisplayName("createModel(201): without uuid")
	@Override
	public void createModel201() {
		var vo = assert201(() -> client.createModel(auth("moo"), new ModelCreateVO().setName("foo")));
		assertAll("vo",
				() -> assertNotNull(vo.getUuid(), "uuid"),
				() -> assertEquals("foo", vo.getName(), "name"),
				() -> assertEquals("moo", vo.getAuthor(), "author"));
		var entity = repository.findByUuid(vo.getUuid()).get();
		assertAll("entity",
				() -> assertEquals(vo.getUuid(), entity.getUuid(), "uuid"),
				() -> assertEquals("foo", entity.getName(), "name"),
				() -> assertEquals("moo", entity.getAuthor(), "author"));
	}

	@Test
	@DisplayName("createModel(201): with uuid")
	public void createModel201WithUuid() {
		var uuid = UUID.randomUUID();
		var vo = assert201(() -> client.createModel(auth("maa"), new ModelCreateVO().setUuid(uuid).setName("foo")));
		assertAll("vo",
				() -> assertEquals(uuid, vo.getUuid(), "uuid"),
				() -> assertEquals("foo", vo.getName(), "name"),
				() -> assertEquals("maa", vo.getAuthor(), "author"));
		var entity = repository.findByUuid(uuid).get();
		assertAll("entity",
				() -> assertEquals(uuid, entity.getUuid(), "uuid"),
				() -> assertEquals("foo", entity.getName(), "name"),
				() -> assertEquals("maa", entity.getAuthor(), "author"));
	}

	@Test
	@DisplayName("createModel(400): bean validation")
	@Override
	public void createModel400() {
		assert400(() -> client.createModel(auth(), new ModelCreateVO()), "vo.name: must not be null");
	}

	@Test
	@DisplayName("createModel(401): no token")
	@Override
	public void createModel401() {
		var uuid = UUID.randomUUID();
		assert401(() -> client.createModel(null, new ModelCreateVO().setUuid(uuid).setName("foo")));
		assertFalse(repository.existsByUuid(uuid), "created");
	}

	@Test
	@DisplayName("createModel(409): uuid in use")
	@Override
	public void createModel409() {
		var uuid = data.model().getUuid();
		assert409(() -> client.createModel(auth(), new ModelCreateVO().setUuid(uuid).setName("foo")));
	}

	@Test
	@DisplayName("updateModel(200): success")
	@Override
	public void updateModel200() {
		var model = data.model("foo");
		var vo = assert200(() -> client.updateModel(auth("muu"), model.getUuid(), new ModelUpdateVO().setName("bar")));
		assertAll("vo",
				() -> assertEquals(model.getUuid(), vo.getUuid(), "uuid"),
				() -> assertEquals("bar", vo.getName(), "name"),
				() -> assertEquals("muu", vo.getAuthor(), "author"));
		var entity = repository.findByUuid(model.getUuid()).get();
		assertAll("entity",
				() -> assertEquals(model.getUuid(), entity.getUuid(), "uuid"),
				() -> assertEquals("bar", entity.getName(), "name"),
				() -> assertEquals("muu", vo.getAuthor(), "author"));
	}

	@Test
	@DisplayName("updateModel(400): bean validation")
	@Override
	public void updateModel400() {
		var uuid = data.model().getUuid();
		assert400(() -> client.updateModel(auth(), uuid, new ModelUpdateVO()), "vo.name: must not be null");
	}

	@Test
	@DisplayName("updateModel(401): no token")
	@Override
	public void updateModel401() {
		var uuid = data.model("foo").getUuid();
		assert401(() -> client.updateModel(null, uuid, new ModelUpdateVO().setName("bar")));
		assertEquals("foo", repository.findByUuid(uuid).get().getName(), "updated");
	}

	@Test
	@DisplayName("updateModel(404): model not exists")
	@Override
	public void updateModel404() {
		assert404(() -> client.updateModel(auth(), UUID.randomUUID(), new ModelUpdateVO().setName("meh")));
	}

	@Test
	@DisplayName("deleteModel(200): success")
	@Override
	public void deleteModel204() {
		var uuid = data.model().getUuid();
		assert204(() -> client.deleteModel(auth(), uuid));
		assertFalse(repository.existsByUuid(uuid), "not deleted");
	}

	@Test
	@DisplayName("deleteModel(401): no token")
	@Override
	public void deleteModel401() {
		var uuid = data.model().getUuid();
		assert401(() -> client.deleteModel(null, UUID.randomUUID()));
		assertTrue(repository.existsByUuid(uuid), "deleted");
	}

	@Test
	@DisplayName("deleteModel(404): model not exists")
	@Override
	public void deleteModel404() {
		assert404(() -> client.deleteModel(auth(), UUID.randomUUID()));
	}
}
