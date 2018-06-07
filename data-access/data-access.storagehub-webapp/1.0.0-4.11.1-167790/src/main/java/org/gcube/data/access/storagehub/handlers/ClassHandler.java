package org.gcube.data.access.storagehub.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gcube.common.storagehub.model.annotations.RootNode;
import org.gcube.common.storagehub.model.items.Item;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassHandler {
	
	private static Logger log = LoggerFactory.getLogger(ClassHandler.class);
	
	private Reflections reflection = new Reflections();
	
	private Map<String, Class<? extends Item>> classMap = new HashMap<String, Class<? extends Item>>();
	private Map<Class<? extends Item>, String> typeMap = new HashMap<Class<? extends Item>, String>();
	
	
	public ClassHandler() {
		
		Set<Class<?>> classesAnnotated = reflection.getTypesAnnotatedWith(RootNode.class);
		for (Class<?> clazz: classesAnnotated ){
			if (Item.class.isAssignableFrom(clazz))
				for (String value: clazz.getAnnotation(RootNode.class).value()){
					log.debug("loading class {} with value {} ", clazz, value );
					classMap.put(value, (Class<? extends Item>) clazz);
					typeMap.put((Class<? extends Item>) clazz, value);
				}
		}
	}
	
	public Class<? extends Item> get(String nodeType){
		if (classMap.containsKey(nodeType)) return classMap.get(nodeType);
		throw new RuntimeException("mapping not found for nodetype "+ nodeType);
	}
	
	public String getNodeType(Class<? extends Item> clazz){
		if (typeMap.containsKey(clazz)) return typeMap.get(clazz);
		throw new RuntimeException("mapping not found for nodetype "+ clazz.getSimpleName());
	}
	
	
}
