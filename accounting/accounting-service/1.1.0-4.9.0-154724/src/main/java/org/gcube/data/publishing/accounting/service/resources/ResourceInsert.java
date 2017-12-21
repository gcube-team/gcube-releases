package org.gcube.data.publishing.accounting.service.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.data.publishing.accounting.service.AccountingResource;
import org.gcube.data.publishing.accounting.service.AccountingServiceInitializer;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBy(AccountingServiceInitializer.class)
@Path(ResourceInsert.INSERT_PATH_PART)
public class ResourceInsert {

	private static final Logger log = LoggerFactory.getLogger(ResourceInsert.class);

	public static final String INSERT_PATH_PART = "insert";
	public static final String RECORD_PATH_PART = "record";

	@POST
	@Path(RECORD_PATH_PART)
	@Consumes({ MediaType.TEXT_PLAIN, AccountingResource.APPLICATION_JSON_CHARSET_UTF_8 })
	public Response add(String json) throws Exception {

		log.debug("Goign to account : {}", json);

		AccountingServiceInitializer appManager = (AccountingServiceInitializer) ApplicationManagerProvider
				.get(AccountingServiceInitializer.class);
		AccountingPersistence accountingPersistence = appManager.getAccountingPersistence();

		List<Record> records = DSMapper.unmarshalList(json);
		for (Record record : records) {
			accountingPersistence.account(record);
		}
		return Response.status(Status.CREATED).build();
	}

}
