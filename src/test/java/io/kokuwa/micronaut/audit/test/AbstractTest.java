package io.kokuwa.micronaut.audit.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;

import io.kokuwa.micronaut.audit.HttpResponseAssertions;
import io.kokuwa.micronaut.audit.JwtBuilder;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;

@MicronautTest(transactional = false)
public abstract class AbstractTest {

	@Inject
	public DataProvider data;
	@Inject
	public Validator validator;
	@Inject
	public SignatureGeneratorConfiguration signature;

	@BeforeEach
	void reset() {
		data.deletAll();
	}

	public String auth() {
		return auth("meh");
	}

	public String auth(String subject) {
		return new JwtBuilder(signature).subject(subject).toBearer();
	}

	public <T> T assertValid(T object) {
		assertNotNull(object, "response was empty");
		var violations = validator.validate(object);
		assertTrue(violations.isEmpty(), () -> "bean validation failed with:" + violations.stream()
				.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage())
				.collect(Collectors.joining()));
		return object;
	}

	public <T> T assert200(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert200(executeable).body());
	}

	public <T> T assert201(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert201(executeable).body());
	}

	public static <T> void assert400(Supplier<HttpResponse<T>> executeable, String expectedMessage) {
		var response = HttpResponseAssertions.assert400(executeable);
		var error = response.getBody(JsonError.class).get();
		assertEquals(expectedMessage, error.getMessage());
	}
}
