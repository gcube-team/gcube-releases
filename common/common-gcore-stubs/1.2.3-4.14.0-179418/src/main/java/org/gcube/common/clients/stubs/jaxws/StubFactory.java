package org.gcube.common.clients.stubs.jaxws;

import static org.gcube.common.clients.stubs.jaxws.JAXWSUtils.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.gcube.common.clients.stubs.jaxws.StubFactoryDSL.AtClause;
import org.gcube.common.clients.stubs.jaxws.handlers.GCoreJAXWSHandler;
import org.gcube.common.clients.stubs.jaxws.proxies.GenericProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates JAXWS stubs for endpoints or instances of gCore service at given addresses.
 * 
 * <p>
 * Factories are instantiated with descriptions of target services (cf. {@link GCoreService}) and use them to perform the
 * following tasks:
 * 
 * <ul>
 * <li>interact with the JAXWS APIs to dynamically implement stub interfaces.
 * <li>resolve service WSDLs to enable the interactions above, as per JAXWS client model.
 * <li>avoid unnecessary WSDL resolution through a shared LRU cache.
 * <li>configure stub implementations with JAXWS handlers which turn outgoing service calls into gCore calls (i.e. add headers for current scope and service coordinates).
 * </ul>
 * 
 * Note that all factories can be configured to go through a proxy, e.g. for debugging purposes (cf.
 * {@link StubFactory#setProxy(String, int)}).
 * <p>
 * 
 * @author Fabio Simeoni
 * @see StubCache
 * @see GCoreJAXWSHandler
 */
public class StubFactory<T> implements StubFactoryDSL.AtClause<T> {

	private static final Logger log = LoggerFactory.getLogger(StubFactory.class);

	// note that using the standard http.hostProxy and http.hostPort would have been clearner, but it creates problems,
	// for example when wsdls with imports must be resolved. better to offer ad-hoc support with confined effect in the JVM.
	private static String proxyHost;
	private static int proxyPort;

	private static StubCache cache = new StubCache();

	private final GCoreService<T> target;

	/**
	 * Creates an instance for a given {@link GCoreService}.
	 * 
	 * @param target the service
	 */
	public StubFactory(GCoreService<T> target) {

		notNull("gCore Service", target);

		this.target = target;
	}

	public T at(EndpointReference reference) {

		notNull("instance reference", reference);

		GCoreEndpointReference epr = new GCoreEndpointReference(reference);

		String proxied  = setProxyOn(epr.address);

		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder().address(proxied);

		reference = epr.key==null? 
				builder.build():
					builder.referenceParameter(epr.key).build();

				return at(proxied,reference,new AddressingFeature());

	}

	public T at(URI address) {

		notNull("endpoint address", address);

		//build reference from address
		EndpointReference reference = new W3CEndpointReferenceBuilder().address(address.toString()).build();

		return at(reference);
	}

	private T at(String endpointAddress, EndpointReference reference, AddressingFeature ... features) {

		try {

			if (Thread.currentThread().getContextClassLoader()==null)
				Thread.currentThread().setContextClassLoader(StubFactory.class.getClassLoader());
			
			// get JAXWS service from endpoint address
			Service service = buildService(endpointAddress+"?wsdl", target.type(),target.qName());

			log.info("target type is {} ", target.type());

			T stub =service.getPort(reference,target.type(),features);	
			
			BindingProvider provider = (BindingProvider) stub;

			// configure stub for gCube calls
			registerHandler(provider, target);

			return GenericProxyFactory.getProxy(target.type(),endpointAddress, stub);

		} catch (Error e) { //bad stubs/wsdls cause java.lang.Errors

			cache.clear(target.type()); //clear cache

			throw new RuntimeException("could not configure discovery service", e);

		} catch (Exception e) { //bad stubs can cause java.lang.Errors (!) 
			throw new RuntimeException("could not configure discovery service", e);
		}
	}

	/**
	 * Creates a stub for a given {@link GCoreService}
	 * 
	 * @param service information about the service
	 * @return the next clause for the creation of the stub
	 */
	public static <T> AtClause<T> stubFor(GCoreService<T> service) {
		return new StubFactory<T>(service);
	}

	// helper
	private synchronized Service buildService(final String wsdlAddress, final Class<?> type, final QName name) throws Exception {

		Callable<Service> task = new Callable<Service>() {
			@Override
			public Service call() throws Exception {
				log.info("fetching wsdl for {} at {}", name.getLocalPart(),wsdlAddress);
				return Service.create(new URL(wsdlAddress), name);
			}
		};

		Service service = cache.get(type,task);

		return service;
	}

	// helper
	private void registerHandler(BindingProvider provider, GCoreService<?> context) {

		Binding binding = provider.getBinding();

		@SuppressWarnings("rawtypes")
		List<Handler> currentChain = binding.getHandlerChain();

		GCoreJAXWSHandler handler = new GCoreJAXWSHandler(context);

		currentChain.add(handler);

		binding.setHandlerChain(currentChain);

	}

	/**
	 * Configures a proxy for client interactions through this factory.
	 * 
	 * @param host the proxy host
	 * @return port the proxy port
	 */
	public static void setProxy(String host, int port) {
		StubFactory.proxyHost = host;
		StubFactory.proxyPort = port;
	}

	// helper
	private String setProxyOn(String address) {

		if (proxyHost != null)
			try {
				//pass through URI for replacing host and port with proxy's
				URI u = URI.create(address);
				return new URI(u.getScheme(), u.getUserInfo(), proxyHost, Integer.valueOf(proxyPort), u.getPath(),
						u.getQuery(), u.getFragment()).toString();
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}

		return address;
	}

		
}