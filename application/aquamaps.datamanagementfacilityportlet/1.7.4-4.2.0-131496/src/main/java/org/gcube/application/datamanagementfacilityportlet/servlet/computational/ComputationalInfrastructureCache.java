package org.gcube.application.datamanagementfacilityportlet.servlet.computational;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.ComputationalInfrastructure;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComputationalInfrastructureCache {

	private static final Logger logger = LoggerFactory.getLogger(ComputationalInfrastructureCache.class);
	
	private static ConcurrentHashMap<String, ScopeCache> cache=new ConcurrentHashMap<String, ScopeCache>();
	private static TransformerFactory tf = TransformerFactory.newInstance();
	private static Transformer transformer;
	static {
		try{
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
		}catch(Exception e){
			logger.error("Unable to initialize transformer ",e);
		}
	}
	
	public static synchronized List<ComputationalInfrastructure> getEnvironments() throws Exception{
		String theScope=ScopeProvider.instance.get();
		if((!cache.containsKey(theScope))||(!cache.get(theScope).isValid()))
			cache.put(theScope, new ScopeCache(queryForEnvironments()));
		return cache.get(theScope).getInfras();
	}
	
	
	protected static List<ComputationalInfrastructure> queryForEnvironments()throws Exception{
		List<ComputationalInfrastructure> toReturn=new ArrayList<ComputationalInfrastructure>();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'ComputationalInfrastructure'");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		
		
		for(GenericResource resource : client.submit(query)){
			try{
				// parse body as a XML serialization of a Computational Infrastructure
				StringWriter writer = new StringWriter();
				transformer.transform(new DOMSource(resource.profile().body()), new StreamResult(writer));
				String theXML=writer.getBuffer().toString();
//				String theXML = writer.getBuffer().toString().replaceAll("\n|\r", "");
				ComputationalInfrastructure infrastructure=(ComputationalInfrastructure) AquaMapsXStream.getXMLInstance().fromXML(theXML);
				toReturn.add(infrastructure);
			}catch(Exception e){
				logger.warn("Unable to parse resource [ID :"+resource.id()+"]",e);
			}
		}
		return toReturn;
	}
	
	
	public static final String getBackendUrl(String serviceName){
		SimpleQuery query=queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceName/text() eq '"+serviceName+"'");
		DiscoveryClient <GCoreEndpoint> client=clientFor(GCoreEndpoint.class);
		return client.submit(query).get(0).profile().endpoints().iterator().next().toString();
	}
}
