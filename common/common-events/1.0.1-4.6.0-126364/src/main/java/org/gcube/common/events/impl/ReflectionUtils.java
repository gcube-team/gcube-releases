package org.gcube.common.events.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;


/**
 * Library-wide reflection utilities.
 * 
 * @author Fabio Simeoni
 *
 */
public class ReflectionUtils {

	
	public static Type typeOf(Object event) {
		
		return event instanceof Event? Event.class.cast(event).type():event.getClass();
		
	}
	
	public static Object valueOf(Object event) {
		
		return event instanceof Event? Event.class.cast(event).event():event;
		
	}
	

	public static boolean isCollectionType(Type t) {
		
		if (t instanceof Class)
			return Collection.class.isAssignableFrom(Class.class.cast(t));
		
		if (t instanceof ParameterizedType)
			return isCollectionType(ParameterizedType.class.cast(t).getRawType());

		return false;
				
	}
	
	public static Type elementTypeOf(Type t) {
		
		if (!(t instanceof ParameterizedType))
				throw new RuntimeException("invoked with non-collection type, should have been previous ruled out");
		
		t = ParameterizedType.class.cast(t).getActualTypeArguments()[0];
				
		if (t instanceof Class)
			return Class.class.cast(t);
		
		if (t instanceof WildcardType)
			return WildcardType.class.cast(t).getUpperBounds()[0];
		
		throw new RuntimeException("invoked with type variable, should have been previous ruled out");
	}
	
	public static boolean containsVariable(Type t) {
		
		if (t instanceof TypeVariable<?>)
			return true;
		
		
		if (t instanceof ParameterizedType)
			for (Type arg : ParameterizedType.class.cast(t).getActualTypeArguments())
				if (containsVariable(arg))
					return true;
		
		if (t instanceof WildcardType)
			return containsVariable(WildcardType.class.cast(t).getUpperBounds()[0])
				&& containsVariable(WildcardType.class.cast(t).getLowerBounds()[0]);
		
		return false;
		
	}
}
