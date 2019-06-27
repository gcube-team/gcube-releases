package org.gcube.portlets.user.performfishanalytics.server.util;
import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class GcoreEndpointReader.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 23, 2019
 */
public class GcoreEndpointReader {

	private String serviceName = "CkanConnector";
	private String serviceClass = "DataAccess";
	private String endpointEntryName;

	private static final Logger log = LoggerFactory.getLogger(GcoreEndpointReader.class);
	private String endpointValue;


	/**
	 * Instantiates a new gcore endpoint reader.
	 *
	 * @param scope the scope
	 * @param serviceName the service name
	 * @param serviceClass the service class
	 * @param endpointEntryName the endpoint entry name
	 * @throws Exception the exception
	 */
	public GcoreEndpointReader(String scope, String serviceName, String serviceClass, String endpointEntryName) throws Exception {
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.endpointEntryName = endpointEntryName;

		String callerProviderScope = ScopeProvider.instance.get();
		try{

			log.info("set scope "+scope);
			ScopeProvider.instance.set(scope);

			SimpleQuery query = queryFor(GCoreEndpoint.class);
			query.addCondition(String.format("$resource/Profile/ServiceClass/text() eq '%s'",serviceClass));
			//query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
			query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'",serviceName));

			if(this.endpointEntryName!=null)
				query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces//Endpoint[@EntryName/string() eq \""+endpointEntryName+"\"]/text()");

			log.debug("submitting quey "+query.toString());

			DiscoveryClient<String> client = client();
			List<String> endpoints = client.submit(query);
			if (endpoints == null || endpoints.isEmpty()) throw new Exception("Cannot retrieve the GCoreEndpoint serviceName: "+serviceName +", serviceClass: " +serviceClass +", endpointEntryName: "+endpointEntryName+", in scope: "+scope);


			this.endpointValue = endpoints.get(0);
			if(endpointValue==null)
				throw new Exception("Endpoint:"+endpointEntryName+", is null for serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope);

			log.info("found the GCoreEndpoint with serviceName: "+serviceName +", serviceClass: " +serviceClass +", endpointEntryName: "+endpointEntryName+", in the scope: "+scope);

			/*Group<Endpoint> accessPoints = se.profile().endpoints();
			if(accessPoints.size()==0) throw new Exception("Endpoint in serviceName serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope +" not found");

			Endpoint ep = accessPoints.iterator().next();

			String epName = ep.name();

			System.out.println(epName);*/

		}catch(Exception e){
			String error = "An error occurred during GCoreEndpoint discovery, serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope +".";
			log.error(error, e);
			throw new Exception(error);
		}finally{

			log.info("scope provider reset");
			ScopeProvider.instance.set(callerProviderScope);

		}
	}


	/**
	 * Gets the endpoint entry name.
	 *
	 * @return the endpointEntryName
	 */
	public String getEndpointEntryName() {

		return endpointEntryName;
	}


	/**
	 * Gets the endpoint value.
	 *
	 * @return the endpointValue
	 */
	public String getEndpointValue() {

		return endpointValue;
	}


	/**
	 * Gets the service class.
	 *
	 * @return the serviceClass
	 */
	public String getServiceClass() {

		return serviceClass;
	}


	/**
	 * Gets the service name.
	 *
	 * @return the serviceName
	 */
	public String getServiceName() {

		return serviceName;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GcoreEndpointReader [serviceName=");
		builder.append(serviceName);
		builder.append(", serviceClass=");
		builder.append(serviceClass);
		builder.append(", endpointEntryName=");
		builder.append(endpointEntryName);
		builder.append(", endpointValue=");
		builder.append(endpointValue);
		builder.append("]");
		return builder.toString();
	}

}
