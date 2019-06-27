package org.gcube.dataaccess.databases.access;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.dataaccess.databases.resources.DBResource;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.api.DiscoveryException;
import org.gcube.resources.discovery.client.api.InvalidResultException;
import org.gcube.resources.discovery.client.queries.impl.XQuery;


/** Class that allows to discover databases submitting a query to the IS. 
 *  It retrieves a list of DBResource object */
public class DatabasesDiscoverer extends ResourceDiscoverer{
	
	public DatabasesDiscoverer(){
		
		
	}
	
    /** Method that performs the discovery process of database resources */
	public List<DBResource> discover() throws IllegalStateException, DiscoveryException, InvalidResultException{

		XQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq 'Database'");

		DiscoveryClient<DBResource> discovery = clientFor(DBResource.class);
		List<DBResource> resources = discovery.submit(query);

		return resources;
	}

}
