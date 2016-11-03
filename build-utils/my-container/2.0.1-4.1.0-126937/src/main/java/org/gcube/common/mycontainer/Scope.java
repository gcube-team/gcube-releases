package org.gcube.common.mycontainer;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Associates a test class or a single test method with a given scope.
 *  
 * @author Fabio Simeoni
 *
 */
@Qualifier
@Inherited
@Target({TYPE,METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
	String value();
}
