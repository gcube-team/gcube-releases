package org.gcube.common.events.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * 
 * Used internally to perform subtype checks at runtime.
 * 
 * @author Fabio Simeoni
 *
 */
class TypeChecker {

	public static boolean matchTypes(Type paramType,Type eventType) {
		
		print(paramType,eventType);
		
		if (paramType instanceof Class)
			return matchClass(Class.class.cast(paramType),eventType); 
		
		if (paramType instanceof WildcardType)
			return matchWildcardType(WildcardType.class.cast(paramType),eventType);
		
		
		if (paramType instanceof ParameterizedType)
			return matchParameterizedType(ParameterizedType.class.cast(paramType),eventType);
		
		return false;
	}
	
	public static boolean matchWildcardType(WildcardType paramType, Type eventType) {
		
		//compare upper bounds
		if (eventType instanceof WildcardType)
			return matchTypes(paramType.getUpperBounds()[0], WildcardType.class.cast(eventType).getUpperBounds()[0]);
		
		//compare upper bound with event type
		return matchTypes(paramType.getUpperBounds()[0],eventType);
	}
	
	public static boolean matchParameterizedType(ParameterizedType paramType,Type eventType) {
	
		//compare type parameters
		if (eventType instanceof ParameterizedType)
			return matchParameterizedTypes(paramType,ParameterizedType.class.cast(eventType));
		
		//fails if event is raw or non-parametric
		return false;
	}
	
	public static boolean matchParameterizedTypes(ParameterizedType paramType,ParameterizedType eventType) {
		
		return matchTypes(paramType.getRawType(),eventType.getRawType())
						&& matchArguments(paramType.getActualTypeArguments(), eventType.getActualTypeArguments());
		
	}
	
	public static boolean matchArguments(Type[] paramArgumentTypes, Type[] eventArgumentTypes) {
		
		//no need to check lengths as we know raw type is the same
		for (int i=0; i<paramArgumentTypes.length;i++)
			if (!matchTypes(paramArgumentTypes[i],eventArgumentTypes[i]))
					return false;
			
		return true;
	}
	
	
	public static boolean matchClass(Class<?> paramType,Type eventType) {
		
		if (eventType instanceof Class)  
			return matchClasses(paramType,Class.class.cast(eventType));
		
		if (eventType instanceof WildcardType)
			return false;
		
		return matchClass(paramType,ParameterizedType.class.cast(eventType));
		
	}
	
	public static boolean matchClass(Class<?> paramType,ParameterizedType eventType) {
		
		print(paramType,eventType);
		//compare with raw type: equivalent to performing a cast
		return matchClass(paramType,ParameterizedType.class.cast(eventType).getRawType());
		
	}
	
	public static boolean matchClasses(Class<?> paramType,Class<?> eventType) {
		
		return paramType.isAssignableFrom(eventType);
		
	}
	
	public static void print(Type t1,Type t2) {
		//System.out.println(t1+" >? "+t2);
	}
}
