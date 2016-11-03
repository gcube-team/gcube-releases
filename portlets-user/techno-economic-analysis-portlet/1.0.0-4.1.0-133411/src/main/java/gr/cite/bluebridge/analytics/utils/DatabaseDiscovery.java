package gr.cite.bluebridge.analytics.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.List;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import gr.cite.bluebridge.analytics.portlet.SpringPortal;

public class DatabaseDiscovery {
	private static Log logger = LogFactoryUtil.getLog(SpringPortal.class);

	public static DatabaseCredentials fetchDatabaseCredentials(String scope, ServiceProfile serviceProfile) throws Exception {
		List<DatabaseCredentials> databaseCredentialsList = discoverDatabaseEndpoints(scope, serviceProfile);
		
		if (databaseCredentialsList.isEmpty()){
			throw new Exception("Did not manage to discover any " + serviceProfile.getServiceClass() + "/" + serviceProfile.getServiceName() + " endpoint");
		}

		DatabaseCredentials databaseCredentials = databaseCredentialsList.get(0);	// TODO random or sequential
		
		String decryptedPass = StringEncrypter.getEncrypter().decrypt(databaseCredentials.getDbpass());
		databaseCredentials.setDbpass(decryptedPass);
		
		logger.info("Managed to find " + serviceProfile.getServiceName() + " database endpoint ");
		
		logger.info("Database Credentials");
		logger.info("name = " +  databaseCredentials.getDbname());
		logger.info("host = " +  databaseCredentials.getDbhost());
		logger.info("user = " +  databaseCredentials.getDbuser());
		logger.info("pass = " +  databaseCredentials.getDbpass());
		
		return databaseCredentials;
	}

	public static List<DatabaseCredentials> discoverDatabaseEndpoints(String scope, ServiceProfile serviceProfile) {
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		 
		query.addCondition("$resource/Profile/Category/text() eq 'Database'")
			 .addCondition("$resource/Profile/Name/text() eq '" + serviceProfile.getServiceName() + "'");
		 
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);		 
		List<ServiceEndpoint> serviceEndpoints = client.submit(query);
		List<DatabaseCredentials> databaseCredentialsList = new ArrayList<>();
				 
		for (ServiceEndpoint sep : serviceEndpoints) {			
			for (AccessPoint ap : sep.profile().accessPoints()){				
				DatabaseCredentials databaseCredentials = new DatabaseCredentials();
				databaseCredentials.setDbhost(ap.address());
				databaseCredentials.setDbname(ap.name());
				databaseCredentials.setDbuser(ap.username());
				databaseCredentials.setDbpass(ap.password());
				databaseCredentialsList.add(databaseCredentials);
			}
		}
		
		return databaseCredentialsList ;
	}
}
