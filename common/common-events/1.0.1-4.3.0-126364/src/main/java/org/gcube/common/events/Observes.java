package org.gcube.common.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies callback methods in event observers.
 * <p>
 * Optional attributes are used to indicate:
 * 
 * <li>event qualifiers ({@link Observes#Any} by default);
 * <li>whether the successful execution of the method is {@link Kind#critical} to the client. Non critical
 * methods may be {@link Kind#resilient} or {@link Kind#safe} (the default), depending on whether they should or should
 * not execute after previous critical failures;
 * <li>the delay during which events are accumulated before being delivered.
 * 
 * @author Fabio Simeoni
 * @see Hub
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Observes {

	static final String Any = "any";

	/**
	 * The kind of the observer, {@link #critical}, {@link #safe}, or {@link #resilient}.
	 * 
	 */
	public static enum Kind {
		critical, safe, resilient
	}

	/**
	 * The event qualifiers.
	 * 
	 * @return the qualifiers, {@link #Any} by default.
	 */
	String[] value() default { Any };

	/**
	 * The kind of the observer {@link Kind#safe} by default.
	 * 
	 * @return the kind
	 */
	Kind kind() default Kind.safe;

	/**
	 * The minimum duration in milliseconds between the delivery of two subsequent events.
	 * 
	 * @return the kind
	 */
	long every() default 0;
}
