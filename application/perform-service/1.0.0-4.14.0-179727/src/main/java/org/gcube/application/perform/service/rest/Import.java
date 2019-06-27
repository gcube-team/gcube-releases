package org.gcube.application.perform.service.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.application.perform.service.PerformServiceManager;
import org.gcube.application.perform.service.ServiceConstants;
import org.gcube.application.perform.service.engine.Importer;
import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.DBQueryDescriptor;
import org.gcube.application.perform.service.engine.model.importer.ImportRequest;
import org.gcube.application.perform.service.engine.model.importer.ImportRoutineDescriptor;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(ServiceConstants.Import.PATH)

@ManagedBy(PerformServiceManager.class)
public class Import {

	private static final Logger log= LoggerFactory.getLogger(Import.class);
	
	@Inject
	private Importer importer;
	
	
	/**
	 * Launches DM Import
	 * 
	 * 
	 * @param batchType
	 * @param farmid
	 * @param sourceFile
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public ImportRoutineDescriptor importExcel(@QueryParam(ServiceConstants.Import.BATCH_TYPE_PARAMETER)String batchType,
			@QueryParam(ServiceConstants.Import.FARM_ID_PARAMETER)Long farmid,
			@QueryParam(ServiceConstants.Import.EXCEL_FILE_PARAMETER)String sourceFile,
			@QueryParam(ServiceConstants.Import.EXCEL_FILE_VERSION_PARAMETER)String sourceVersion) {
		
		InterfaceCommons.checkMandatory(batchType, ServiceConstants.Import.BATCH_TYPE_PARAMETER);
		InterfaceCommons.checkMandatory(farmid, ServiceConstants.Import.FARM_ID_PARAMETER);
		InterfaceCommons.checkMandatory(sourceFile, ServiceConstants.Import.EXCEL_FILE_PARAMETER);
		InterfaceCommons.checkMandatory(sourceVersion, ServiceConstants.Import.EXCEL_FILE_VERSION_PARAMETER);
		
		ImportRequest req=new ImportRequest(sourceFile, sourceVersion, batchType, farmid);
		try {
			return importer.importExcel(req);		
		}catch(Throwable t) {
			log.warn("Unexpected Exception on IMPORT "+req,t);
			throw new WebApplicationException("Unexpected Exception.", t,Response.Status.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
	/**
	 * Returns all Import routine per farm id
	 * 
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{"+ServiceConstants.Import.FARM_ID_PARAMETER+"}")
	public Response getAll(@PathParam(ServiceConstants.Import.FARM_ID_PARAMETER) Long farmid){
		try {
			List<ImportRoutineDescriptor> toReturn=importer.getDescriptors(
					new DBQueryDescriptor().
					add(DBField.ImportRoutine.fields.get(DBField.ImportRoutine.FARM_ID), farmid));
			
			GenericEntity<List<ImportRoutineDescriptor>> entity=new GenericEntity<List<ImportRoutineDescriptor>>(toReturn) {};
			return Response.ok(entity).build();			
		}catch(Throwable t) {
			log.warn("Unexpected Exception ",t);
			throw new WebApplicationException("Unexpected Exception.", t,Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Returns all Import routine per farm id
	 * 
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ServiceConstants.Import.LAST_METHOD+"/{"+ServiceConstants.Import.FARM_ID_PARAMETER+"}")
	public Response getGrouped(@PathParam(ServiceConstants.Import.FARM_ID_PARAMETER) Long farmid,
			@DefaultValue("COMPLETE") @QueryParam(ServiceConstants.Import.STATUS_PARAMETER) String status){
		try {
			
			List<ImportRoutineDescriptor> toReturn=importer.getGroupedDescriptors(
					new DBQueryDescriptor().
					add(DBField.ImportRoutine.fields.get(DBField.ImportRoutine.FARM_ID), farmid).
					add(DBField.ImportRoutine.fields.get(DBField.ImportRoutine.STATUS), status));
			
			GenericEntity<List<ImportRoutineDescriptor>> entity=new GenericEntity<List<ImportRoutineDescriptor>>(toReturn) {};
			return Response.ok(entity).build();			
		}catch(Throwable t) {
			log.warn("Unexpected Exception ",t);
			throw new WebApplicationException("Unexpected Exception.", t,Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
}
