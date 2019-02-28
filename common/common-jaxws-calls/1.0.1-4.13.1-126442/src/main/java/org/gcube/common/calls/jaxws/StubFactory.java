package org.gcube.common.calls.jaxws;

import java.net.URL;
import java.util.List;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;

import org.gcube.common.calls.jaxws.handlers.JaxWSHandler;
import org.gcube.common.calls.jaxws.proxies.GenericProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StubFactory<T> implements StubFactoryDSL.AtClause<T>{

	private static Logger log = LoggerFactory.getLogger(StubFactory.class);
	
	private GcubeService<T> target;
	
	public static <T> StubFactory<T> stubFor(GcubeService<T> target){
		return new StubFactory<T>(target);
	}
	
			
	private StubFactory(GcubeService<T> target) {
		this.target = target;
	}


	public T at(String address) {

		try{

			String endpointAddress = address+"?wsdl";

			log.debug("contcting endpoint "+endpointAddress);
			
			// get JAXWS service from endpoint address
			Service service = Service.create(new URL(endpointAddress), target.name());

			// get JAXWS stub
			T stub = service.getPort(target.type());
		
			BindingProvider provider = (BindingProvider) stub;

			// configure stub for gCube calls
			registerHandler(provider, target);

			return GenericProxyFactory.getProxy(target.type(), endpointAddress,  stub);

		}catch (Exception e) {
			log.error("error building service",e);
			throw new RuntimeException("error building service",e);
		}

	}

	
	public T at(EndpointReference endpoint){
		return at(new JaxWSEndpointReference(endpoint).address);
	}
	

	// helper
	private void registerHandler(BindingProvider provider, GcubeService<?> context) {

		Binding binding = provider.getBinding();

		@SuppressWarnings("rawtypes")
		List<Handler> currentChain = binding.getHandlerChain();

		JaxWSHandler handler = new JaxWSHandler(context);

		currentChain.add(handler);

		binding.setHandlerChain(currentChain);

	}

}
