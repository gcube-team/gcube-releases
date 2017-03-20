package gr.cite.bluebridge.analytics.endpoint;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.bluebridge.analytics.endpoint.exceptions.DatabaseDiscoveryException;

public class DatabaseDiscovery {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseDiscovery.class);

	public static DatabaseCredentials fetchDatabaseCredentials(String scope, DatabaseProfile databaseProfile) throws Exception {			
		ScopeProvider.instance.set(scope);			

		List<DatabaseCredentials> databaseCredentialsList = discoverDatabaseEndpoints( databaseProfile);
		
		if (databaseCredentialsList.isEmpty()){
			throw new DatabaseDiscoveryException("Failed to discover any " + "/" + databaseProfile.getName() + " endpoint for scope " + scope);
		}

		DatabaseCredentials databaseCredentials = databaseCredentialsList.get(0);	
		
		String decryptedPass = StringEncrypter.getEncrypter().decrypt(databaseCredentials.getDbpass());
		databaseCredentials.setDbpass(decryptedPass);
		
		logger.debug("Managed to find " + databaseProfile.getName() + " database endpoint");	
		
		return databaseCredentials;
	}

	public static List<DatabaseCredentials> discoverDatabaseEndpoints(DatabaseProfile databaseProfile) {			
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		 
		query.addCondition("$resource/Profile/Category/text() eq 'Database'")
			 .addCondition("$resource/Profile/Name/text() eq '" + databaseProfile.getName() + "'");
		 
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);		 
		List<ServiceEndpoint> serviceEndpoints = client.submit(query);
		List<DatabaseCredentials> databaseCredentialsList = new ArrayList<>();
			
		if(serviceEndpoints != null){		
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
		}
		
		return databaseCredentialsList ;
	}
}
