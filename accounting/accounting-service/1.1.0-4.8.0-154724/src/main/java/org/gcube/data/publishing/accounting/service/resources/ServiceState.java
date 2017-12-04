package org.gcube.data.publishing.accounting.service.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.data.publishing.accounting.service.AccountingResource;
import org.gcube.data.publishing.accounting.service.AccountingServiceInitializer;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	@Path("/")
	@Produces(AccountingResource.APPLICATION_JSON_CHARSET_UTF_8)
	public Response getState() throws JSONException {
		String context = AccountingServiceInitializer.getCurrentContext();
		logger.debug("Getting Service Status fro context {}", context);

		JSONObject jsonObject = new JSONObject();
		jsonObject.append(SERVICE, RUNNING);
		jsonObject.append(CONTEXT, context);
		
		Status responseStatus = Status.SERVICE_UNAVAILABLE;

		AccountingServiceInitializer appManager = (AccountingServiceInitializer) ApplicationManagerProvider
				.get(AccountingServiceInitializer.class);

		AccountingPersistence accountingPersistence = appManager.getAccountingPersistence();
		try {
			jsonObject.append(INSERT_CONNECTION_UP, accountingPersistence.isConnectionActive());
		}catch (Exception e) {
			jsonObject.append(INSERT_CONNECTION_UP, false);
		}
		
		AccountingPersistenceQuery accountingPersistenceQuery = appManager.getAccountingPersistenceQuery();
		try {
			jsonObject.append(QUERY_CONNECTION_UP, accountingPersistenceQuery.isConnectionActive());
		}catch (Exception e) {
			jsonObject.append(QUERY_CONNECTION_UP, false);
		}
		
		return Response.status(responseStatus).entity(jsonObject.toString()).build();

	}

}
