/**
 * 
 */
package org.gcube.common.mycontainer;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the value of a static field as a {@link Gar} to deploy in {@link MyContainer}
 * 
 * @author Fabio Simeoni
 *
 */
@Inherited
@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Deployment {}
