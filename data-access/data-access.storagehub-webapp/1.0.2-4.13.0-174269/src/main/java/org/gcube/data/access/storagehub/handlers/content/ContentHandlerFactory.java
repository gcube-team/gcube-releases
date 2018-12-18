package org.gcube.data.access.storagehub.handlers.content;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import javax.inject.Singleton;

import org.gcube.common.storagehub.model.annotations.MimeTypeHandler;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ContentHandlerFactory {

	private static Logger logger = LoggerFactory.getLogger(ContentHandlerFactory.class);
	
	private Reflections reflection = new Reflections();
	
	private static HashMap<String, Class<? extends ContentHandler>> handlerMap = new HashMap<String, Class<? extends ContentHandler>>();
		
	private Class<? extends ContentHandler> defaultHandler = GenericFileHandler.class;
	
	@SuppressWarnings("unchecked")
	public ContentHandlerFactory() {
		Set<Class<?>> classesAnnotated = reflection.getTypesAnnotatedWith(MimeTypeHandler.class);
		for (Class<?> clazz: classesAnnotated ){
			if (ContentHandler.class.isAssignableFrom(clazz)) {
				logger.debug("searching for mimetypes {} with values {}",clazz.getName(), Arrays.toString(clazz.getAnnotation(MimeTypeHandler.class).value()));
				for (String value: clazz.getAnnotation(MimeTypeHandler.class).value()){
					logger.debug("value for class {} is {}",clazz.getName(), value);
					handlerMap.put(value, (Class<? extends ContentHandler>) clazz);
				}
				
				
			}
		
		}
	}
	
	public ContentHandler create(String mimetype) throws Exception{
		Class<? extends ContentHandler> handlerClass = handlerMap.get(mimetype);
		if (handlerClass!=null) 
			return handlerClass.newInstance();
		else 
			return defaultHandler.newInstance();
	}
	
}
