package org.gcube.common.clients.delegates;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.common.clients.Call;

/**
 * Specified on {@link Call#call(Object)} to short-circuit endpoint iteration in the interaction strategy of {@link DiscoveryDelegate}s. 
 * 
 * @author Fabio Simeoni
 * @see Call
 * @see DiscoveryDelegate
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface Unrecoverable {}
