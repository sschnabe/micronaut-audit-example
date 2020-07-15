package io.kokuwa.micronaut.audit.rest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import io.kokuwa.micronaut.audit.domain.Model;

/**
 * Maps domain to rest objects.
 *
 * @author Stephan Schnabel
 */
@Mapper(componentModel = "jsr330")
public interface RestMapper {

	ModelDisplayVO toModelDisplay(Model model);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "created", ignore = true)
	@Mapping(target = "updated", ignore = true)
	@Mapping(target = "author", ignore = true)
	Model create(ModelCreateVO vo);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "uuid", ignore = true)
	@Mapping(target = "created", ignore = true)
	@Mapping(target = "updated", ignore = true)
	@Mapping(target = "author", ignore = true)
	Model update(@MappingTarget Model model, ModelUpdateVO vo);
}
