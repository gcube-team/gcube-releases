package org.gcube.common.events.impl;

import static java.util.Arrays.*;
import static org.gcube.common.events.Observes.*;
import static org.gcube.common.events.impl.TypeChecker.*;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Used internally to match events with observers. 
 * 
 * @author Fabio Simeoni
 *
 */
class Key {

	private final Type type;
	private Set<String> qualifiers; 
	
	public Key(Type type,Set<String> qualifiers) {
		this.type=type;
		this.qualifiers=qualifiers;
	}
	
	public Type type() {
		return type;
	}
	
	public Set<String> qualifiers() {
		return qualifiers;
	}
	

	public boolean matches(Type eventType,String ... qualifiers) {
		return matchTypes(type,eventType) && 
			   (qualifiers().contains(Any) || 
					 qualifiers().containsAll(new HashSet<String>(asList(qualifiers))));
	}

}
