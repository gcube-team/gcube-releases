package org.gcube.common.events.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Event wrapper that captures the parametric type of events.
 * 
 * @author Fabio Simeoni
 *
 * @param <T> the type of the event
 */
public abstract class Event<T> {
	
	private final T event;
	private final Type type;
	
	public Event(T event) {
		this.event=event;
		this.type = ParameterizedType.class.cast(this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	Type type() {
		return type;
	}
	
	T event() {
		return event;
	}
}
