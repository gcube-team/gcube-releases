package org.gcube.common.authorizationservice;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.common.authorization.library.Policies;
import org.gcube.common.authorizationservice.util.TokenPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("policyManager")
public class PolicyManager {

	private static Logger log = LoggerFactory.getLogger(PolicyManager.class);
	
	@Inject
	TokenPersistence tokenPersistence;
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response setPolices(Policies policies) {
		try{
			log.info("adding policies: {}", policies.getPolicies());
			tokenPersistence.addPolicies(policies.getPolicies());
		}catch(Exception e){
			log.error("error adding policies",e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
		             .entity("Error adding policies: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		}
		return Response.ok().build();
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_XML)
	@Path("{policy_id}")
	public Response remove(@Null @PathParam("policy_id") long policyId) {
		try{
			log.info("removing policy with id {}", policyId);
			tokenPersistence.removePolicy(policyId);
		}catch(Exception e){
			log.error("error removing policies", e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
		             .entity("Error removing policies: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		}
		return Response.ok().build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Policies getPoliciesPerContext(@NotNull @QueryParam("context") String context) {
		try{
			log.info("retrieving polices in context {}", context);
			Policies policies = new Policies(tokenPersistence.getPolices(context));
			log.info("returning {} policies from getPoliciesPerContext",policies.getPolicies().size());
			return policies;
		}catch(Exception e){
			log.error("error retrieving policies per context {}", context, e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
		             .entity("Error retrieving policies: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		}
	}
	
	
	
}
