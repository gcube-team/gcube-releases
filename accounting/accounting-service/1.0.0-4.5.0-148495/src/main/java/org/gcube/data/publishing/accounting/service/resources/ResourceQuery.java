package org.gcube.data.publishing.accounting.service.resources;


import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.gcube.accounting.analytics.persistence.couchbase.AccountingPersistenceQueryCouchBase;
import org.gcube.data.publishing.accounting.service.AccountingInitializer;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBy(AccountingInitializer.class)
@Path("query")
public class ResourceQuery {

	private static final Logger log = LoggerFactory.getLogger(ResourceQuery.class);

	private AccountingPersistenceQueryCouchBase accountingPersistenceQuery;
	private AccountingInitializer appManager = (AccountingInitializer)ApplicationManagerProvider.get(AccountingInitializer.class);

	
	@GET	
	@Path("/getRecord/{recordId}/{type}/")		
	public Response responseRecord(@NotNull @PathParam("recordId") String recordId,@NotNull @PathParam("type") String type)  throws Exception {
		log.debug("call responseRecord with recordID:{}, type:{}",recordId,type);	
		Response response = null;
		String result=null;
		try{
			accountingPersistenceQuery=appManager.getAccountingPersistenceQuery();
			result= accountingPersistenceQuery.getRecord(recordId,type);
			return Response.status(200).entity(result).build();
		}
		catch(Exception e){
			log.error("Error",e);
			return Response.status(500).entity("").build();
		}
		
	}
	
	
	

    

}
