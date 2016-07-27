/**
 * 
 */
package org.gcube.portlets.user.uriresolvermanager.readers;

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
import org.gcube.portlets.user.uriresolvermanager.entity.ServiceAccessPoint;
import org.gcube.portlets.user.uriresolvermanager.entity.ServiceParameter;
import org.gcube.portlets.user.uriresolvermanager.util.ScopeUtil;
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
			
			String infraName = ScopeUtil.getInfrastructureNameFromScope(scope);
			
			logger.info("Instancing root scope: "+infraName);
			ScopeProvider.instance.set(infraName);
			
			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/string() eq '"+resourceName+"'");
			
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
	
			List<ServiceEndpoint> r = client.submit(query);
			if (r == null || r.isEmpty()) throw new Exception("Cannot retrieve the runtime resource: "+resourceName);
	
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
			
					List<ServiceParameter> serviceParameters = new ArrayList<ServiceParameter>(properties.size());
					
					Iterator<Property> iter = properties.iterator();
				
					while (iter.hasNext()) {
						
						Property prop = iter.next();
						
						serviceParameters.add(new ServiceParameter(prop.value(), true));
					}
					
					serviceAccessPoints.add(new ServiceAccessPoint(ap.name(), ap.address(), serviceParameters));
				}
			}
	//			parameters.setUser(ap.username()); //username
	//			
	//			String decryptedPassword = StringEncrypter.getEncrypter().decrypt(ap.password());
	//			 
	//			parameters.setPassword(decryptedPassword); //password
	//			Group<Property> properties = ap.properties();
			
		}catch (Exception e) {
			logger.error("Sorry, an error occurred on reading the resource "+resourceName+ " Runtime Reosurce",e);
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
	
//	public static void main(String[] args) {
//		try {
//			RuntimeResourceReader rr = new RuntimeResourceReader("/gcube", "Gis-Resolver");
//			System.out.println(rr);
//			
//			System.out.println(rr.getServiceAccessPointForEntryName("gis"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
