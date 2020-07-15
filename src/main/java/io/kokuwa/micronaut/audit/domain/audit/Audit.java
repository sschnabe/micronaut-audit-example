package io.kokuwa.micronaut.audit.domain.audit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.micronaut.aop.Around;
import io.micronaut.context.annotation.Type;


/**
 * Annotation to audit method.
 *
 * @author Stephan Schnabel
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Around
@Type(AuditInterceptor.class)
public @interface Audit {}
