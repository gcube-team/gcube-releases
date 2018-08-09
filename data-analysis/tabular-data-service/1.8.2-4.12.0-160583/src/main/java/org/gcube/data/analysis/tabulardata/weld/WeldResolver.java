package org.gcube.data.analysis.tabulardata.weld;

import java.util.Iterator;

import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.servlet.ServletContext;
import javax.xml.ws.handler.MessageContext;

import lombok.extern.slf4j.Slf4j;

import org.apache.naming.resources.DirContextURLStreamHandlerFactory;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.environment.servlet.Listener;
import org.jboss.weld.injection.spi.ResourceInjectionServices;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;
import org.jboss.weld.manager.BeanManagerImpl;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.InstanceResolver;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;

@Slf4j
public class WeldResolver<T> extends InstanceResolver<T> {

	private BeanManager mgr;
	
	@SuppressWarnings("unused")
	private WSWebServiceContext context;
	
	@SuppressWarnings("rawtypes")
	private WSEndpoint endpoint;
	
	private static boolean handlerInitialized = false;
	
	private final Class<T> type;
	
	public WeldResolver(Class<T> type) {
		this.type=type;
	}
	
	@Override
	@SuppressWarnings("all")
	public T resolve(Packet request) {
		
		try {
			
			Iterator<Bean<?>> it = beanManager(request).getBeans(this.type).iterator();

			if (it.hasNext()) {

				Bean<T> endpointBean = (Bean<T>) it.next();
				
				Context ctx = beanManager(request).getContext(endpointBean.getScope());
				
				return ctx.get(endpointBean, beanManager(request).createCreationalContext(endpointBean));
								
			} 
			else
				throw new Exception("WELD listener is not configured?");
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void dispose() {
		log.info("disposing class "+endpoint.getImplementationClass().getSimpleName());
		super.dispose();
	}
	
	
	@Override @SuppressWarnings("rawtypes")
	public void start(WSWebServiceContext wsc, WSEndpoint endpoint) {
		
		super.start(wsc, endpoint);
		log.info("starting class "+endpoint.getImplementationClass().getSimpleName());
		
		if (!handlerInitialized){
			DirContextURLStreamHandlerFactory.addUserFactory(new ConfigurableStreamHandlerFactory("smp", new Handler()));
			handlerInitialized = true;
			log.info("SMP protocol initialized");
		}
			
			
		this.context=wsc;
		this.endpoint = endpoint;
		
		
	}
	
	private BeanManager beanManager(Packet request) {
		
		if (this.mgr==null) {
			init(request);
		}
		
		return mgr;
	}
	
	private void init(Packet request) {
		ServletContext cxt = (ServletContext) request.get(MessageContext.SERVLET_CONTEXT);
		this.mgr = (BeanManagerImpl) cxt.getAttribute(Listener.BEAN_MANAGER_ATTRIBUTE_NAME);
		installResourceInjectionService(this.mgr);
	}

	private void installResourceInjectionService(BeanManager mgr) {
		
		ServiceRegistry registry = ((BeanManagerImpl) mgr).getServices();
		
		final ResourceInjectionServices jsr250InjectionService = registry.get(ResourceInjectionServices.class);
		
		registry.add(ResourceInjectionServices.class,new ResourceInjectionServices() {
			
			@Override
			public void cleanup() {
				jsr250InjectionService.cleanup();
			}
			
			@Override
			@Deprecated
			public Object resolveResource(String arg0, String arg1) {
				return jsr250InjectionService.resolveResource(arg0, arg1);
			}
			
			@Override
			@Deprecated
			public Object resolveResource(InjectionPoint arg0) {
				return jsr250InjectionService.resolveResource(arg0);
			}
			
			@Override
			public ResourceReferenceFactory<Object> registerResourceInjectionPoint(
					String arg0, String arg1) {
				return jsr250InjectionService.registerResourceInjectionPoint(arg0, arg1);
			}
			
			@Override
			public ResourceReferenceFactory<Object> registerResourceInjectionPoint(
					InjectionPoint arg0) {
				return jsr250InjectionService.registerResourceInjectionPoint(arg0);
			}
		});
	}
}