package io.kokuwa.micronaut.audit.domain.audit;

import java.util.stream.Stream;

import javax.inject.Singleton;

import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.security.utils.SecurityService;
import lombok.RequiredArgsConstructor;

/**
 * Interceptor to set current subject into auditable.
 *
 * @author Stephan Schnabel
 */
@Singleton
@RequiredArgsConstructor
class AuditInterceptor<T, R> implements MethodInterceptor<T, R> {

	private final SecurityService security;

	@Override
	public R intercept(MethodInvocationContext<T, R> context) {
		var username = security.username();
		if (username.isPresent()) {
			Stream.of(context.getParameterValues())
					.filter(Auditable.class::isInstance)
					.map(Auditable.class::cast)
					.forEach(auditable -> auditable.setAuthor(username.get()));
		}
		return context.proceed();
	}
}