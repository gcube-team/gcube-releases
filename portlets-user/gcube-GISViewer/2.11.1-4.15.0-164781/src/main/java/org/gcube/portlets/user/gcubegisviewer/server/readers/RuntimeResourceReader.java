/**
 *
 */
package org.gcube.portlets.user.gcubegisviewer.server.readers;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class RuntimeResourceReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 25, 2016
 */
public class RuntimeResourceReader {

	public static final Logger logger = LoggerFactory.getLogger(RuntimeResourceReader.class);

	public List<ServiceParameter> serviceParameters;

	private String resourceName;

	private String scope;

	private String serviceBaseURI;

	/**
	 * Instantiates a new runtime resource reader.
	 *
	 * @param scope the scope
	 * @param resourceName the resource name
	 * @throws Exception the exception
	 */
	public RuntimeResourceReader(String scope, String resourceName) throws Exception {
		this.scope = scope;
		this.resourceName = resourceName;
		readResource(scope, resourceName);
	}

	/**
	 * Read resource.
	 *
	 * @param scope the scope
	 * @param resourceName the resource name
	 * @return the application URI
	 * @throws Exception the exception
	 */
	protected String readResource(String scope, String resourceName) throws Exception {

		String orgininalScope = ScopeProvider.instance.get();
		try{
			logger.info("Tentative read resource: "+resourceName+", scope: "+scope);

			this.resourceName = resourceName;
			this.scope = scope;

			logger.info("Instancing input scope: "+scope);
			ScopeProvider.instance.set(scope);

			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/string() eq '"+resourceName+"'");

			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

			List<ServiceEndpoint> r = client.submit(query);
			if (r == null || r.isEmpty()) throw new Exception("Cannot retrieve the runtime resource: "+resourceName +" in scope: "+scope);

			ServiceEndpoint se = r.get(0);
			if(se.profile()==null)
				throw new Exception("IS profile is null for resource: "+resourceName);

			Group<AccessPoint> accessPoints = se.profile().accessPoints();
			if(accessPoints.size()==0) throw new Exception("Accesspoint in resource "+resourceName+" not found");

			AccessPoint ap = accessPoints.iterator().next();

			Group<Property> properties = ap.properties();

			if(properties.size()==0){
				logger.warn("Properties in resource "+resourceName+" not found");
			}else{
				serviceParameters = new ArrayList<ServiceParameter>(properties.size());
				Iterator<Property> iter = properties.iterator();
				while (iter.hasNext()) {
					Property prop = iter.next();
					serviceParameters.add(new ServiceParameter(prop.value(), true));
				}
			}

			logger.info("returning URI: "+ap.address());
			this.serviceBaseURI = ap.address();
			return serviceBaseURI;

		}catch (Exception e) {
			logger.error("Sorry, an error occurred on reading the resource "+resourceName+ "Runtime Reosurces",e);
			throw new Exception("Sorry, an error occurred on reading the resource "+resourceName+ "Runtime Reosurces");
		}finally{
			if(orgininalScope!=null && !orgininalScope.isEmpty()){
				logger.info("Setting scope used by caller: "+orgininalScope);
				ScopeProvider.instance.set(orgininalScope);
			}else{
				logger.info("Resetting scope...");
				ScopeProvider.instance.reset();
			}
		}
	}


	/**
	 * Gets the resource name.
	 *
	 * @return the resource name
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * Gets the service parameters.
	 *
	 * @return the service parameters
	 */
	public List<ServiceParameter> getServiceParameters() {
		return serviceParameters;
	}

	/**
	 * Gets the service base uri.
	 *
	 * @return the service base uri
	 */
	public String getServiceBaseURI() {
		return serviceBaseURI;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RuntimeResourceReader [serviceParameters=");
		builder.append(serviceParameters);
		builder.append(", resourceName=");
		builder.append(resourceName);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", serviceBaseURI=");
		builder.append(serviceBaseURI);
		builder.append("]");
		return builder.toString();
	}


	/*
	public static void main(String[] args) {
		try {
			RuntimeResourceReader resolver = new RuntimeResourceReader("/gcube", "Transect");
			System.out.println(resolver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
