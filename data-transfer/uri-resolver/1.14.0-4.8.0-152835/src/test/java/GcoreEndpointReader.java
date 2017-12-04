import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.Serializable;
import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 10, 2016
 */
public class GcoreEndpointReader implements Serializable{

	private static final long serialVersionUID = 7631710353375893823L;
	private static final String ckanResource = "org.gcube.data.access.ckanconnector.CkanConnector";
	private static final String serviceName = "CkanConnector";
	private static final String serviceClass = "DataAccess";

	private static final Logger logger = LoggerFactory.getLogger(GcoreEndpointReader.class);
	private String ckanResourceEntyName;

	/**
	 * Instantiates a new gcore endpoint reader.
	 *
	 * @param scope the scope
	 * @throws Exception the exception
	 */
	public GcoreEndpointReader(String scope) throws Exception {

		String currentScope = ScopeProvider.instance.get();
		try{

			logger.info("set scope "+scope);
			ScopeProvider.instance.set(scope);

			SimpleQuery query = queryFor(GCoreEndpoint.class);
			query.addCondition(String.format("$resource/Profile/ServiceClass/text() eq '%s'",serviceClass));
			query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
			query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'",serviceName));
			query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces//Endpoint[@EntryName/string() eq \""+ckanResource+"\"]/text()");

			logger.debug("submitting quey "+query.toString());

			DiscoveryClient<String> client = client();
			List<String> endpoints = client.submit(query);
			if (endpoints == null || endpoints.isEmpty()) throw new Exception("Cannot retrieve the GCoreEndpoint serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope);


			this.ckanResourceEntyName = endpoints.get(0);
			if(ckanResourceEntyName==null)
				throw new Exception("Endpoint:"+ckanResource+", is null for serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope);

			logger.info("found entyname "+ckanResourceEntyName+" for ckanResource: "+ckanResource);

			/*Group<Endpoint> accessPoints = se.profile().endpoints();
			if(accessPoints.size()==0) throw new Exception("Endpoint in serviceName serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope +" not found");

			Endpoint ep = accessPoints.iterator().next();

			String epName = ep.name();

			System.out.println(epName);*/

		}catch(Exception e){
			String error = "An error occurred during GCoreEndpoint discovery, serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope +".";
			logger.error(error, e);
			throw new Exception(error);
		}finally{
			logger.info("scope provider reset");
			ScopeProvider.instance.set(currentScope);
		}
	}

	/**
	 * @return the ckanResourceEntyName
	 */
	public String getCkanResourceEntyName() {

		return ckanResourceEntyName;
	}

	private static String scope = "/d4science.research-infrastructures.eu";


	public static void main(String[] args) {

		try {

			GcoreEndpointReader reader = new GcoreEndpointReader(scope);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
