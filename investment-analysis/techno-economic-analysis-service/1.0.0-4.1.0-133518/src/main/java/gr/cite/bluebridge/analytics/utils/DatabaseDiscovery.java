package gr.cite.bluebridge.analytics.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
//import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

public class DatabaseDiscovery {
	public static DatabaseCredentials fetchDatabaseCredentials(String scope, ServiceProfile serviceProfile) throws Exception {
		List<DatabaseCredentials> databaseCredentialsList = discoverDatabaseEndpoints(scope, serviceProfile);
		
		if (databaseCredentialsList.isEmpty()){
			throw new Exception("Did not manage to discover any " + serviceProfile.getServiceClass() + "/" + serviceProfile.getServiceName() + " endpoint");
		}

		DatabaseCredentials databaseCredentials = databaseCredentialsList.get(0);	// TODO random or sequential
		
		String decryptedPass = StringEncrypter.getEncrypter().decrypt(databaseCredentials.getDbpass());
		databaseCredentials.setDbpass(decryptedPass);
		
		System.out.println("Managed to find " + serviceProfile.getServiceName() + " database endpoint ");
		
		System.out.println("Database Credentials");
		System.out.println("name = " +  databaseCredentials.getDbname());
		System.out.println("host = " +  databaseCredentials.getDbhost());
		System.out.println("user = " +  databaseCredentials.getDbuser());
		System.out.println("pass = " +  databaseCredentials.getDbpass());
		
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
