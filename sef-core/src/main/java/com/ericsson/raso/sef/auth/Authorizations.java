package com.ericsson.raso.sef.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target ({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention (RetentionPolicy.RUNTIME)
public @interface Authorizations {
	public abstract AuthorizeIfAllowedFor[] value();

}
