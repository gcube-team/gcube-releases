package org.gcube.data.publishing.accounting.service.resources;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.data.publishing.accounting.service.AccountingResource;
import org.gcube.data.publishing.accounting.service.AccountingServiceInitializer;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBy(AccountingServiceInitializer.class)
@Path(ResourceQuery.QUERY_PATH_PART)
public class ResourceQuery {

	private static final Logger log = LoggerFactory.getLogger(ResourceQuery.class);

	public static final String QUERY_PATH_PART = "query";
	public static final String RECORD_PATH_PART = "record";

	public static final String TYPE_PATH_PART = "type";
	public static final String RECORD_ID_PATH_PART = "recordID";

	@GET
	@Path(RECORD_PATH_PART + "/{" + TYPE_PATH_PART + "}/{" + RECORD_ID_PATH_PART + "}/")
	@Produces(AccountingResource.APPLICATION_JSON_CHARSET_UTF_8)
	public Response get(@NotNull @PathParam(TYPE_PATH_PART) String type,
			@NotNull @PathParam(RECORD_ID_PATH_PART) String recordId) throws Exception {

		log.debug("Requested {} having ID {}", type, recordId);

		AccountingServiceInitializer appManager = (AccountingServiceInitializer) ApplicationManagerProvider
				.get(AccountingServiceInitializer.class);
		AccountingPersistenceQuery accountingPersistenceQuery = appManager.getAccountingPersistenceQuery();

		String record = accountingPersistenceQuery.getRecord(recordId, type);
		return Response.status(200).entity(record).type(AccountingResource.APPLICATION_JSON_CHARSET_UTF_8).build();

	}

}
