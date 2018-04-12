package org.gcube.data.streams.exceptions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Intended for {@link Exception} classes to mark them as unrecoverable for iteration purposes. 
 * 
 * @author Fabio Simeoni
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface StreamContingency {}
