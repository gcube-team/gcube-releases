package org.gcube.common.clients.config;

import java.util.concurrent.TimeUnit;

import org.gcube.common.clients.builders.AbstractStatefulBuilder;
import org.gcube.common.clients.builders.AbstractStatelessBuilder;


/**
 * A custom configuration property for proxies.
 * 
 * @author Fabio Simeoni
 *
 * @see AbstractStatefulBuilder
 * @see AbstractStatelessBuilder
 * 
 * @param <T> the type of the property value
 */
public class Property<T> {

	/**
	 * The name of the call timeout {@link Property}.
	 */
	public static final String timeout = "timeout";

	/**
	 * The name of the sticky session property {@link Property}.
	 */
	public static final String sticky_session = "sticky_session";
	
	/**
	 * Return the call timeout {@link Property}.
	 * @param value the property value
	 * @return the property
	 */
	public static Property<Long> timeout(long value) {
		return new Property<Long>(timeout,value);
	}
	
	/**
	 * Return the call timeout {@link Property}.
	 * @param duration the duration of the timeout
	 * @param unit the time unit of the timeout
	 * @return the property
	 */
	public static Property<Long> timeout(long duration, TimeUnit unit) {
		return new Property<Long>(timeout,unit.toMillis(duration));
	}
	
	/**
	 * Return the sticky session {@link Property}.
	 * @param value the property value
	 * @return the property
	 */
	public static Property<Boolean> sticky_session(boolean value) {
		return new Property<Boolean>(sticky_session,value);
	}
	
	private final String name;
	private final T value;
	
	/**
	 * Creates an instance with a name and a value. 
	 * @param name the name
	 * @param value the value
	 */
	public Property(String name, T value) {
		this.name=name;
		this.value=value;
	}
	
	/**
	 * Returns the name of the property.
	 * @return the name
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Returns the value of the property.
	 * @return the value
	 */
	public T value() {
		return value;
	}
}
