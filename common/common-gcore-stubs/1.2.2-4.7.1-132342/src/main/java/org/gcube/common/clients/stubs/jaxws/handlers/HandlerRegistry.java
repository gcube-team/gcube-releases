package org.gcube.common.clients.stubs.jaxws.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discovers available {@link CallHandler}s.
 *  
 * @author Fabio Simeoni
 *
 */
public class HandlerRegistry {
	
	private final static Logger log = LoggerFactory.getLogger(HandlerRegistry.class);

	private final static List<CallHandler> handlers = new ArrayList<CallHandler>();
	
	static {
	
		ServiceLoader<CallHandler> loader = ServiceLoader.load(CallHandler.class);
		Iterator<CallHandler> it = loader.iterator();
		while (it.hasNext()) 
			try {
				CallHandler handler = it.next();
				log.info("loaded call handler {}",handler);
				handlers.add(handler);
			}
			catch(Error e) {
				log.error("could not load call handler",e);
			}
	}
	
	/**
	 * Returns the discovered {@link CallHandler}s.
	 * @return the handlers
	 */
	public static List<CallHandler> handlers() {
		return handlers;
	}
}
