package org.gcube.data.publishing.accounting.service.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.data.publishing.accounting.service.AccountingInitializer;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBy(AccountingInitializer.class)
@Path("status")
public class ResourceStatus {

	private static final Logger log = LoggerFactory.getLogger(ResourceStatus.class);

	private AccountingPersistence accountingPersistence;
	private AccountingInitializer appManager = (AccountingInitializer)ApplicationManagerProvider.get(AccountingInitializer.class);


	@GET
	@Path("/getStatus")
	public Response getStatus() {
		log.debug("call getStatus");	
		String output = "Accounting Service is UP";
		return Response.status(200).entity(output).build();
	}
	
	
	
	
	

	
	
}
