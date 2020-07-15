package io.kokuwa.micronaut.audit.rest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import io.kokuwa.micronaut.audit.domain.ModelRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;

/**
 * Controller for {@link ModelApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Transactional
@RequiredArgsConstructor
public class RestController implements ModelApi {

	private final ModelRepository repository;
	private final RestMapper mapper;

	@Override
	public HttpResponse<List<ModelDisplayVO>> getModels() {
		return HttpResponse.ok(repository
				.findOrderByUpdated()
				.map(mapper::toModelDisplay)
				.collect(Collectors.toList()));
	}

	@Override
	public HttpResponse<ModelDisplayVO> getModelByUuid(UUID uuid) {
		return repository.findByUuid(uuid)
				.map(mapper::toModelDisplay)
				.map(HttpResponse::ok)
				.orElseGet(HttpResponse::notFound);
	}

	@Override
	public HttpResponse<ModelDisplayVO> createModel(ModelCreateVO vo) {

		if (vo.getUuid() == null) {
			vo.setUuid(UUID.randomUUID());
		} else if (repository.existsByUuid(vo.getUuid())) {
			throw new HttpStatusException(HttpStatus.CONFLICT, null);
		}

		var model = mapper.create(vo);
		model = repository.save(model);

		return HttpResponse.created(mapper.toModelDisplay(model));
	}

	@Override
	public HttpResponse<ModelDisplayVO> updateModel(UUID uuid, ModelUpdateVO vo) {

		var model = repository
				.findByUuid(uuid)
				.orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, null));

		model = mapper.update(model, vo);
		model = repository.update(model);

		return HttpResponse.ok(mapper.toModelDisplay(model));
	}

	@Override
	public HttpResponse<?> deleteModel(UUID uuid) {
		return repository.deleteByUuid(uuid) == 0 ? HttpResponse.notFound() : HttpResponse.noContent();
	}
}
