package org.gcube.common.core.scope;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.kxml2.io.KXmlParser;

/**
 * A collection of endpoints to distinguished infrastructural services.
 * 
 * @author Manuele Simi (ISTI-CNR), Fabio Simeoni (University of Strathclyde)
 *
 */
public class ServiceMap {

	/**
	 * Predefined service names.
	 */
	public static enum ServiceType {
	    	ISICProfileRegistrationPT,ISICStateRegistrationPT,ISICAllRegistrationPT, //Sink - OBSOLETE
	    	ISICProfileCollectionPT,ISICStateCollectionPT,ISICWSDAIXCollectionPT, ISICAllCollectionPT, //XMLCollectionAccess
	    	ISICProfileStoragePT,ISICStateStoragetPT,ISICWSDAIXStoragePT, ISICAllStoragePT, //XMLStorageAccess
	    	ISICProfileQueryPT,ISICStateQueryPT,ISICWSDAIXQueryPT, ISICAllQueryPT, //XQueryAccess
	    	ISRegistry};
	
	/**
	 * Service endpoints, indexed by service name. 
	 */
	private Map<String,Set<EndpointReferenceType>> map = Collections.synchronizedMap(new HashMap<String, Set<EndpointReferenceType>>());
	
	/**
	 * Returns the endpoint of service from the service name.
	 * @param type the service name, type as a plain string or as a value of the {@link ServiceType} enumeration.
	 * @return the endpoint, or <code>null</code> if the service is unknown.
	 */
	public Set<EndpointReferenceType> getEndpoints(Object type) {
		return this.map.get(type.toString());
	}
	
	/**
	 * Loads service endpoints from a stream.
	 * @param reader the stream reader.
	 * @throws Exception if the endpoints could not be loaded.
	 */
	public synchronized void  load(Reader reader) throws Exception {
		
		KXmlParser parser = new KXmlParser(); // use a new parser for the task
		parser.setInput(new BufferedReader(reader)); //set its input
		//parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
		
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("Service")){
						EndpointReferenceType epr = new EndpointReferenceType(new AttributedURI(parser.getAttributeValue(null, "endpoint")));
						if (!this.map.containsKey(parser.getAttributeValue(null, "name")))
							this.map.put(parser.getAttributeValue(null, "name"), new HashSet<EndpointReferenceType>());
						this.map.get((parser.getAttributeValue(null, "name"))).add(epr);
					}
				break;	
				case KXmlParser.END_DOCUMENT :	break loop;
			}
		}
		reader.close();
	}
}
