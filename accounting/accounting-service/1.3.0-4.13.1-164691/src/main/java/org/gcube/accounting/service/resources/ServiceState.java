package org.gcube.accounting.service.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.accounting.service.AccountingResource;
import org.gcube.accounting.service.AccountingServiceInitializer;
import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.smartgears.annotations.ManagedBy;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.research.ws.wadl.HTTPMethods;

/**
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 * @author Luca Frosini (ISTI - CNR)
 */
@ManagedBy(AccountingServiceInitializer.class)
@Path(ServiceState.STATE_PATH_PART)
public class ServiceState {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceState.class);
	
	public static final String STATE_PATH_PART = "state";
	
	public static final String SERVICE = "service";
	public static final String RUNNING = "running";
	public static final String CONTEXT = "context";
	public static final String QUERY_CONNECTION_UP = "queryConnection";
	public static final String INSERT_CONNECTION_UP = "insertConnection";
	
	@GET
	@Produces(AccountingResource.APPLICATION_JSON_CHARSET_UTF_8)
	public Response getState() throws JSONException {
		
		CalledMethodProvider.instance
		.set(HTTPMethods.GET.name() + " /" + STATE_PATH_PART);
		
		String context = AccountingServiceInitializer.getCurrentContext();
		logger.debug("Getting Service Status for context {}", context);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.append(SERVICE, RUNNING);
		jsonObject.append(CONTEXT, context);
		
		AccountingPersistence accountingPersistence = AccountingPersistenceFactory.getPersistence();
		try {
			jsonObject.append(INSERT_CONNECTION_UP, accountingPersistence.isConnectionActive());
		} catch(Exception e) {
			jsonObject.append(INSERT_CONNECTION_UP, false);
		}
		
		AccountingPersistenceQuery accountingPersistenceQuery = AccountingPersistenceQueryFactory.getInstance();
		try {
			jsonObject.append(QUERY_CONNECTION_UP, accountingPersistenceQuery.isConnectionActive());
		} catch(Exception e) {
			jsonObject.append(QUERY_CONNECTION_UP, false);
		}
		
		return Response.status(Status.OK).entity(jsonObject.toString()).build();
		
	}
	
}
