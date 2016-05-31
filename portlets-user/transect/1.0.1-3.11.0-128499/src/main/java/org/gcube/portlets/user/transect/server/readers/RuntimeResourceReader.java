/**
 * 
 */
package org.gcube.portlets.user.transect.server.readers;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.transect.server.readers.entity.RuntimeProperty;
import org.gcube.portlets.user.transect.server.readers.entity.ServiceAccessPoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class RuntimeResourceReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 30, 2015
 */
public class RuntimeResourceReader {
	
	public static final Logger logger = LoggerFactory.getLogger(RuntimeResourceReader.class);
	
	public List<ServiceAccessPoint> serviceAccessPoints;

	private String resourceName;

	private String scope;

	private String entryName;

	private HashMap<String, RuntimeProperty> runtimeProperties;

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
	private void readResource(String scope, String resourceName) throws Exception {
		
		try{
			logger.info("Tentative read resource: "+resourceName+", scope: "+scope);
			
			this.resourceName = resourceName;
			this.scope = scope;
			
//			String infraName = ScopeUtil.getInfrastructureNameFromScope(scope);
			
			logger.info("Instancing scope: "+scope);
			ScopeProvider.instance.set(scope);
			
			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/string() eq '"+resourceName+"'");
			
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
	
			List<ServiceEndpoint> r = client.submit(query);
			if (r == null || r.isEmpty()) throw new Exception("Cannot retrieve the runtime resource: "+resourceName+" in scope: "+scope);
	
			ServiceEndpoint se = r.get(0);
			if(se.profile()==null)
				throw new Exception("IS profile is null for resource: "+resourceName);
			
			Group<AccessPoint> accessPoints = se.profile().accessPoints();
			if(accessPoints.size()==0) throw new Exception("Accesspoint in resource "+resourceName+" not found");
			
			Iterator<AccessPoint> acIt = accessPoints.iterator();
			serviceAccessPoints = new ArrayList<ServiceAccessPoint>(accessPoints.size());
			
			while(acIt.hasNext()){
				
				AccessPoint ap = acIt.next();
				
				Group<Property> properties = ap.properties();
				
				if(properties.size()==0){
					logger.warn("Properties in resource "+resourceName+" not found");
				}else{
			
					runtimeProperties = new HashMap<String, RuntimeProperty>(properties.size());
					
					Iterator<Property> iter = properties.iterator();
				
					while (iter.hasNext()) {
						
						Property prop = iter.next();
						runtimeProperties.put(prop.name(), new RuntimeProperty(prop.name(), prop.value(), true));
					}
					String decryptedPassword = StringEncrypter.getEncrypter().decrypt(ap.password());
					serviceAccessPoints.add(new ServiceAccessPoint(ap.name(), ap.address(), ap.username(), decryptedPassword, runtimeProperties));
				}
			}

		}catch (Exception e) {
			logger.error("Sorry, an error occurred on reading "+resourceName+ " Runtime Reosurce",e);
			throw new Exception("Sorry, an error occurred on reading the resource "+resourceName+ " Runtime Reosurce");
		}
	}

	/**
	 * 
	 * @param entryName
	 * @return
	 */
	public ServiceAccessPoint getServiceAccessPointForEntryName(String entryName){
		
		for (ServiceAccessPoint serviceAccessPoint : serviceAccessPoints) {
			if(serviceAccessPoint.getEntryName().equals(entryName))
				return serviceAccessPoint;
		}
		return null;
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
	 * Gets the entry name.
	 *
	 * @return the entryName
	 */
	public String getEntryName() {
		return entryName;
	}
	
	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}
	
	/**
	 * @return the serviceAccessPoints
	 */
	public List<ServiceAccessPoint> getServiceAccessPoints() {
		return serviceAccessPoints;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RuntimeResourceReader [serviceAccessPoints=");
		builder.append(serviceAccessPoints);
		builder.append(", resourceName=");
		builder.append(resourceName);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", entryName=");
		builder.append(entryName);
		builder.append(", runtimeProperties=");
		builder.append(runtimeProperties);
		builder.append("]");
		return builder.toString();
	}

	public static void main(String[] args) {
		try {
			RuntimeResourceReader rr = new RuntimeResourceReader("/gcube/devsec/devVRE", "TransectGeoDatabase");
			System.out.println(rr);
			
//			System.out.println(rr.getServiceAccessPointForEntryName("gis"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
