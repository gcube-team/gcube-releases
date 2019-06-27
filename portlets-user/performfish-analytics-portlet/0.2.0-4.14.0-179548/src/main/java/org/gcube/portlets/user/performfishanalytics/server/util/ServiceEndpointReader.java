/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.common.encryption.StringEncrypter;
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
 * The Class ServiceEndpointReader.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 22, 2019
 */
public class ServiceEndpointReader {

	public static final Logger logger = LoggerFactory.getLogger(ServiceEndpointReader.class);

	public ServiceParameters serverParameters;

	private String scope;

	private String profileName;

	private String profileCategory;


	/**
	 * Instantiates a new service endpoint reader.
	 *
	 * @param scope the scope
	 * @param profileName the profile name
	 * @param profileCategory the profile category
	 */
	public ServiceEndpointReader(String scope, String profileName, String profileCategory) {
		this.scope = scope;
		this.profileName = profileName;
		this.profileCategory = profileCategory;
	}

	/**
	 * Read resource.
	 *
	 * @param decrypt the decrypt
	 * @return the server parameters
	 * @throws Exception the exception
	 */
	public ServiceParameters readResource(boolean decrypt) throws Exception {

		if(this.scope==null)
			throw new Exception("scope is null");

		if(this.profileName==null)
			throw new Exception("profileName is null");

		try{
			logger.info("Trying to read Service Endpoint with Profile/Name: "+profileName+", scope: "+scope);

			ScopeProvider.instance.set(this.scope);
			logger.info("scope provider set instance: "+this.scope);

			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/string() eq '"+profileName+"'");{

			if(profileCategory!=null)
				logger.info("Added condition Profile/Category: "+profileCategory);
				query.addCondition("$resource/Profile/Category/string() eq '"+profileCategory+"'");
			}

			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

			List<ServiceEndpoint> r = client.submit(query);
			if (r == null || r.isEmpty()) throw new Exception("Cannot retrieve the runtime resource: "+profileName);

			ServiceEndpoint se = r.get(0); //the first
			if(se.profile()==null)
				throw new Exception("IS profile is null for Profile/Name: "+profileName);

			Group<AccessPoint> accessPoints = se.profile().accessPoints();
			if(accessPoints.size()==0) throw new Exception("Accesspoint in resource Profile/Name: "+profileName+" not found");

			serverParameters = new ServiceParameters();

			AccessPoint ap = accessPoints.iterator().next();
			serverParameters.setUrl(ap.address());
			serverParameters.setUser(ap.username()); //username

			String decryptedPassword = "";

			if(decrypt)
				decryptedPassword = StringEncrypter.getEncrypter().decrypt(ap.password());
			else
				decryptedPassword = ap.password();

			serverParameters.setPassword(decryptedPassword); //password

			Group<Property> properties = ap.properties();

			if(properties.size()==0){
				logger.warn("Properties in resource Profile/Name "+profileName+" not found! returning");
				return serverParameters;
			}

			Iterator<Property> iter = properties.iterator();

			Map<String,  List<String>> mapProperties = new HashMap<String, List<String>>();
			while (iter.hasNext()) {
				Property prop = iter.next();
				mapProperties.put(prop.name(), Arrays.asList(prop.value()));
			}

			serverParameters.setProperties(mapProperties);

			return serverParameters;

		}catch (Exception e) {
			String error = "Sorry, an error occurred on reading parameters in Runtime Reosurces, resource Profile/Name "+profileName+" scope: "+scope;
			logger.error(error,e);
			throw new Exception(error);
		}

	}


	/**
	 * Gets the profile name.
	 *
	 * @return the profileName
	 */
	public String getProfileName() {

		return profileName;
	}


	/**
	 * Gets the profile category.
	 *
	 * @return the profileCategory
	 */
	public String getProfileCategory() {

		return profileCategory;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Gets the server parameters.
	 *
	 * @return the server parameters
	 */
	public ServiceParameters getServerParameters() {
		return serverParameters;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("ServiceEndpointReader [serverParameters=");
		builder.append(serverParameters);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", profileName=");
		builder.append(profileName);
		builder.append(", profileCategory=");
		builder.append(profileCategory);
		builder.append("]");
		return builder.toString();
	}


	public static String SERVICE_ENDPOINT_CATEGORY = "DataAnalysis";
	public static String SERVICE_ENDPOINT_NAME = "DataMiner";
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		ServiceEndpointReader reader = new ServiceEndpointReader("/gcube/preprod/preVRE", SERVICE_ENDPOINT_NAME, SERVICE_ENDPOINT_CATEGORY);

		try {
			System.out.println(reader.readResource(true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
