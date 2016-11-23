package org.gcube.rest.index.service.accessors;

import java.io.Serializable;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.gcube.rest.index.service.cache.CacheConfig;
import org.gcube.rest.index.service.cache.IndexServiceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CacheAccessor implements Serializable, ServletContextListener {


	private static final long serialVersionUID = -8160723041293859654L;

	private static final Logger logger = LoggerFactory.getLogger(CacheAccessor.class);
	
	private static IndexServiceCache indexServiceCache;
	
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(CacheConfig.class);
		ctx.refresh();
		indexServiceCache = ctx.getBean(IndexServiceCache.class);
		ctx.close();
	}

	public static IndexServiceCache getIndexServiceCache(){
		return indexServiceCache;
	}
	
	
	
}
