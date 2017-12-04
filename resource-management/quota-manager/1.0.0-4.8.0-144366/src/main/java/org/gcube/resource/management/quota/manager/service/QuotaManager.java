package org.gcube.resource.management.quota.manager.service;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resource.management.quota.library.QuotaList;
import org.gcube.resource.management.quota.library.quotalist.Quota;
import org.gcube.resource.management.quota.manager.check.MyAppManager;
import org.gcube.resource.management.quota.manager.persistence.QuotaDBPersistence;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage quota into persistence
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */

@Path("quotaManager")
public class QuotaManager {
	
	private static Logger log = LoggerFactory.getLogger(QuotaManager.class);
	QuotaDBPersistence quotaPersistence;	
		
	private MyAppManager appManager = (MyAppManager)ApplicationManagerProvider.get(MyAppManager.class);
	
	@POST
	@Consumes(MediaType.TEXT_XML)
	@Path("/insert")
	public Response insertQuote(QuotaList quote) {
		try{
			log.info("insert quote init");
			log.info("insert quote: {}", quote.getQuotaList().toString());
			quotaPersistence =appManager.getQuotaDbPersistence();
			
			quotaPersistence.addQuote(quote.getQuotaList());
		}catch(Exception e){
			log.error("error insert quote",e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
		             .entity("Error insert quote: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		}
		return Response.ok().build();
	}	
	
	@GET
	@Path("/detail/{quota_id}")	
	@Produces(MediaType.TEXT_XML)
	public Quota getDetailQuota(@NotNull @PathParam("quota_id") long quotaId) {
		try{
			log.info("retrieving detail quote {}",quotaId);
			quotaPersistence =appManager.getQuotaDbPersistence();
			Quota quota =quotaPersistence.getQuota(quotaId);
			log.info("quota {}",quota);
			return quota;
		}catch(Exception e){
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error : "+e.getMessage()).type(MediaType.TEXT_PLAIN).build()); 
		}
	}

	@DELETE		
	@Path("/remove/{quota_id}")
	public Response removeQuota(@Null @PathParam("quota_id") long quotaId) {
		try{
			log.info("removing quota with id {}", quotaId);
			quotaPersistence =appManager.getQuotaDbPersistence();
			quotaPersistence.removeQuota(quotaId);			
		}catch(Exception e){
			log.error("error removing quota", e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
		             .entity("Error removing quota: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		}
		return Response.ok().build();
	}
	
	
	
	@GET	
	@Produces(MediaType.APPLICATION_XML)
	@Path("/list")
	public QuotaList getQuotePerContext() {
		String context = ScopeProvider.instance.get();
		try{
			log.info("retrieving quote in context {}", context);
			quotaPersistence =appManager.getQuotaDbPersistence();
			return new QuotaList(quotaPersistence.getQuote(context));
		}catch(Exception e){
			log.error("error retrieving quote per context {}", context, e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
		             .entity("Error retrieving quote: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		}
	}
	
}


