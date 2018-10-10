package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Collections;
import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfrastructureDialoguer {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfrastructureDialoguer.class);

	public String scope;

	public InfrastructureDialoguer(String scope){
		//ScopeProvider.instance.set(scope);
		this.scope = scope;
	}

	public DatabaseInfo getDatabaseInfo(String resourceName) throws Exception{
		DatabaseInfo dbi = new DatabaseInfo();

		LOGGER.debug("Searching for Database "+resourceName+" in scope "+scope);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		//		 query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq 'StatisticalManagerDataBase' ");
		//		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+resourceName+"' ");
		query.addCondition("$resource/Profile/Name eq '"+resourceName+"' ");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);
		if (resources==null || resources.size()==0){
			throw new Exception("No resource named "+resourceName+" available in scope "+scope);
		}
		else{
			AccessPoint ap = resources.get(0).profile().accessPoints().iterator().next();
			dbi.url = ap.address();
			dbi.username = ap.username();
			dbi.password = StringEncrypter.getEncrypter().decrypt(ap.password().trim());

			for (ServiceEndpoint.Property property:ap.properties()){
				if (property.name().equalsIgnoreCase("driver"))
					dbi.driver = property.value(); 
			}

			LOGGER.debug("Found Database : "+dbi);
		}

		if (dbi.url == null)
			throw new Exception("No database URL for resource "+resourceName+" available in scope "+scope);
		return dbi;

	}


	public List<String> getAlgorithmsInScope() throws Exception{

		LOGGER.debug("Searching for Algorithms in scope {} with classloader type {}",scope,Thread.currentThread().getContextClassLoader().getClass().getSimpleName());
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/string() eq 'StatisticalManagerAlgorithm' ");
		query.setResult("$resource/Profile/Name/text()");
		DiscoveryClient<String> client = client();
		List<String> resources = client.submit(query);
		if (resources==null || resources.size()==0){
			throw new Exception("No resource named StatisticalManagerAlgorithm available in scope "+scope);
		}
		LOGGER.debug("Found {} algorithms",resources.size());

		return resources;
	}
	
	public List<String> getPrivateAlgorithmsInScope(String user) throws Exception{

		LOGGER.debug("Searching for Algorithms in scope {} with classloader type {}",scope,Thread.currentThread().getContextClassLoader().getClass().getSimpleName());
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/string() eq 'StatisticalManagerAlgorithmPrivate' ");
		query.addCondition(String.format("contains($resource/Profile/Body/privateusers/string(),'%s')",user));
		query.setResult("$resource/Profile/Name/text()");
		DiscoveryClient<String> client = client();
		List<String> resources = client.submit(query);
		if (resources==null)
			resources = Collections.emptyList();
		LOGGER.debug("Found {} private algorithms",resources.size());

		return resources;


	}


}
