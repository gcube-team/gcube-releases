package org.gcube.common.calls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Interceptors {

	private static final Logger log = LoggerFactory.getLogger(Interceptors.class);
	
	private final static List<Interceptor> interceptors = new ArrayList<Interceptor>();
	
	static {
		ServiceLoader<Interceptor> loader = ServiceLoader.load(Interceptor.class);
		Iterator<Interceptor> it = loader.iterator();
		while (it.hasNext()) 
			try {
				Interceptor handler = it.next();
				log.info("loaded interceptor {}",handler);
				interceptors.add(handler);
			}
			catch(Error e) {
				log.error("could not load interceptors",e);
			}
	}
	
	public static List<Interceptor> getInterceptors(){
		return interceptors;
	}
	
	public static Request executeRequestChain(Call call){
		log.trace("executing request chain");
		Request context = new Request();
		for (Interceptor interceptor : interceptors)
			interceptor.handleRequest(context, call);
		return context;
	}
	
	public static Response executeResponseChain(Call call){
		Response context = new Response();
		for (Interceptor interceptor : interceptors)
			interceptor.handleResponse(context, call);
		return context;
	}
}
