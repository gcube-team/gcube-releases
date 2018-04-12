package org.gcube.accounting.service.resources;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.accounting.service.AccountingResource;
import org.gcube.accounting.service.AccountingServiceInitializer;
import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.research.ws.wadl.HTTPMethods;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@ManagedBy(AccountingServiceInitializer.class)
@Path(RecordManagement.RECORD_PATH_PART)
public class RecordManagement {
	
	private static final Logger log = LoggerFactory.getLogger(RecordManagement.class);
	
	public static final String RECORD_PATH_PART = "record";
	public static final String TYPE_PATH_PART = "type";
	public static final String RECORD_ID_PATH_PART = "recordID";
	
	@POST
	@Consumes({MediaType.TEXT_PLAIN, AccountingResource.APPLICATION_JSON_CHARSET_UTF_8})
	public Response add(String json) throws Exception {
		CalledMethodProvider.instance
		.set(HTTPMethods.POST.name() + " /" + RecordManagement.RECORD_PATH_PART);
		
		log.trace("Going to account : {}", json);
		
		AccountingPersistence accountingPersistence = AccountingPersistenceFactory.getPersistence();
		
		List<Record> records = DSMapper.unmarshalList(json);
		for(Record record : records) {
			accountingPersistence.account(record);
		}
		
		log.trace("{} accounted successfully", json);
		
		return Response.status(Status.CREATED).build();
	}
	
	@GET
	@Path("/{" + TYPE_PATH_PART + "}/{" + RECORD_ID_PATH_PART + "}/")
	@Produces(AccountingResource.APPLICATION_JSON_CHARSET_UTF_8)
	public Response get(@NotNull @PathParam(TYPE_PATH_PART) String type,
			@NotNull @PathParam(RECORD_ID_PATH_PART) String recordId) throws Exception {

		CalledMethodProvider.instance
		.set(HTTPMethods.GET.name() + " /" + TYPE_PATH_PART + "/" + RECORD_ID_PATH_PART);
		
		log.debug("Requested {} having ID {}", type, recordId);
		AccountingPersistenceQuery accountingPersistenceQuery = AccountingPersistenceQueryFactory.getInstance();

		String record = accountingPersistenceQuery.getRecord(recordId, type);
		return Response.status(Status.OK).entity(record).type(AccountingResource.APPLICATION_JSON_CHARSET_UTF_8).build();

	}
	
}
