/**
 *
 */
package org.gcube.datatransfer.resolver.gis;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Iterator;
import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.gis.entity.ServerParameters;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class GeoRuntimeReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 16, 2017
 */
public class GeoRuntimeReader {

	public static final String GEOSERVER_RESOURCE_NAME = "GeoServer";
	public static final String GEONETWORK_RESOURCE_NAME = "GeoNetwork";
	public static final String WORKSPACES_PROPERTY_NAME = "workspaces";

	/**
	 * The Enum GEO_SERVICE.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * May 16, 2017
	 */
	public static enum GEO_SERVICE{GEOSERVER, GEONETWORK};

	public static final Logger logger = LoggerFactory.getLogger(GeoRuntimeReader.class);

	/**
	 * Gets the parameters.
	 *
	 * @param scope the scope
	 * @param geoservice the geoservice
	 * @return the parameters
	 * @throws Exception the exception
	 */
	private ServerParameters getParameters(String scope, GEO_SERVICE geoservice) throws Exception{
		String originalScope =  ScopeProvider.instance.get();
		ServerParameters parameters = new ServerParameters();
		try{

			boolean isGeoserver = geoservice.equals(GEO_SERVICE.GEOSERVER);
			String resourceName = isGeoserver ? GEOSERVER_RESOURCE_NAME : GEONETWORK_RESOURCE_NAME;
			ScopeProvider.instance.set(scope);

			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/string() eq '"+resourceName+"'");

			logger.info("GeoRuntimeReader, using scope: "+scope + ", to get resource: "+resourceName);

			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

			List<ServiceEndpoint> r = client.submit(query);
			if (r == null || r.isEmpty()) throw new Exception("Cannot retrieve the runtime resource: "+resourceName);

			ServiceEndpoint se = r.get(0);
			if(se.profile()==null)
				throw new Exception("IS profile is null for resource: "+resourceName);

			Group<AccessPoint> accessPoints = se.profile().accessPoints();
			if(accessPoints.size()==0) throw new Exception("Accesspoint in resource "+resourceName+" not found");

			AccessPoint ap = accessPoints.iterator().next();
			parameters.setUrl(ap.address());
			parameters.setUser(ap.username()); //username

			String decryptedPassword = StringEncrypter.getEncrypter().decrypt(ap.password());
			parameters.setPassword(decryptedPassword); //password

			if (isGeoserver){
				Group<Property> properties = ap.properties();
				if(properties.size()==0) throw new Exception("Properties in resource "+resourceName+" not found");
				Iterator<Property> iter = properties.iterator();

				while (iter.hasNext()) {

					Property prop = iter.next();

					if(prop.name().compareTo(WORKSPACES_PROPERTY_NAME)==0){
//						logger.trace("Property "+WORKSPACES_PROPERTY_NAME+" found, setting value: "+prop.value());
//						parameters.setWorkspaces(prop.value());
//						break;
					}
				}
			}

		}catch (Exception e) {
			logger.error("Sorry, an error occurred on reading parameters in Runtime Resources",e);
		}finally{
			if(originalScope!=null){
				ScopeProvider.instance.set(originalScope);
				logger.info("scope provider set to orginal scope: "+originalScope);
			}else{
				ScopeProvider.instance.reset();
				logger.info("scope provider reset");
			}
		}

		return parameters;
	}

	/**
	 * Retrieve gis parameters.
	 *
	 * @param scope the scope
	 * @param geoservice the geoservice
	 * @return the server parameters
	 * @throws Exception the exception
	 */
	public ServerParameters retrieveGisParameters(String scope, GEO_SERVICE geoservice) throws Exception
	{
		if(geoservice==null)
			return null;

		try {
			return getParameters(scope, geoservice);

		} catch (Exception e){
			logger.error("Error retrieving the "+geoservice+" parameters", e);
			throw new Exception("Error retrieving the "+geoservice+" parameters", e);
		}
	}

}
