package org.gcube.data.publishing.accounting.service.resources;


import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.accounting.datamodel.BasicUsageRecord;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.data.publishing.accounting.service.AccountingInitializer;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.SerializableList;
import org.gcube.smartgears.ApplicationManagerProvider;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBy(AccountingInitializer.class)
@Path("insert")
public class ResourceInsert {

	private static final Logger log = LoggerFactory.getLogger(ResourceInsert.class);

	private AccountingPersistence accountingPersistence;
	private AccountingInitializer appManager = (AccountingInitializer)ApplicationManagerProvider.get(AccountingInitializer.class);


	@POST
	@Path("/record/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response consumeLine( String line ) throws Exception {
		try{
		
			log.debug("call consumeRecord:{}",line);
			//String scope = BasicUsageRecord.getScopeFromToken();
			//log.debug("call with scope:{}",scope);
			accountingPersistence=appManager.getAccountingPersistence();
			log.debug("get persistence");
			
			Record r= DSMapper.unmarshal(Record.class, line);
			//Record r = RecordUtility.getRecord( line);
			log.debug("transform into record:{}",r);
	
			accountingPersistence.account(r);
			accountingPersistence.flush(100, TimeUnit.MILLISECONDS);
			String output = "RESPONSE:"+r.toString();
			return Response.status(200).entity(output).build();
		
		}
		catch(Exception e){
			log.error("Error",e);
			return Response.status(500).entity("").build();
		}
	}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Path("records")
	public Response consumeRecords(@PathParam("records") String id,  SerializableList<String> input) throws Exception  {
		try{
			log.trace("call consumeRecords:{}");
			List<String> valueList=input.getValuesList();
			accountingPersistence=appManager.getAccountingPersistence();
			for(String value : valueList){
					Record r= DSMapper.unmarshal(Record.class, value);
					accountingPersistence.account(r);
	
			}
			//accountingPersistence.flush(1000, TimeUnit.MILLISECONDS);
			String output = "Ok";
			return Response.status(200).entity(output).build();
		}
		catch(Exception e){
			log.error("Error",e);
			return Response.status(500).entity("").build();
		}
		
		
		
	}
	
	

	
	
}
