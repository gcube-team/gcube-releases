package org.gcube.application.perform.service.rest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.gcube.application.perform.service.PerformServiceManager;
import org.gcube.application.perform.service.ServiceConstants;
import org.gcube.application.perform.service.engine.PerformanceManager;
import org.gcube.application.perform.service.engine.model.CSVExportRequest;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.model.InvalidRequestException;
import org.gcube.application.perform.service.engine.model.importer.AnalysisType;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(ServiceConstants.Performance.PATH)

@ManagedBy(PerformServiceManager.class)
public class Performance {

	private static final Logger log= LoggerFactory.getLogger(Performance.class);

	@Inject 
	private PerformanceManager perform;


	/**
	 * Creates CSV file representing a dataset with 
	 * 
	 * deanonimized labels
	 * filter based on selection
	 * 
	 * 
	 * @return Storage ID (Volatile)
	 */
	@GET	
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String,String> getPerformance(@Context UriInfo info){
		try {
			MultivaluedMap<String, String> parameters=info.getQueryParameters();
			log.info("Forming request from {}",parameters);
			String batchType=InterfaceCommons.getParameter(parameters, ServiceConstants.Performance.BATCH_TYPE_PARAMETER, true).get(0);

			CSVExportRequest request=new CSVExportRequest( new AnalysisType(batchType, batchType));

			request.addAreas(InterfaceCommons.getParameter(parameters, ServiceConstants.Performance.AREA_PARAMETER, false));
			request.addQuarters(InterfaceCommons.getParameter(parameters, ServiceConstants.Performance.QUARTER_PARAMETER, false));
			List<String> farmidsString=InterfaceCommons.getParameter(parameters, ServiceConstants.Performance.FARM_ID_PARAMETER, false);
			log.debug("FARMIDS : ",farmidsString);
			for(String s:farmidsString) { 
				log.debug("Parsing {} ",s);
				request.addFarmId(Long.parseLong(s));
			}

			request.addSpecies(InterfaceCommons.getParameter(parameters, ServiceConstants.Performance.SPECIES_ID_PARAMETER, false));
			request.addPeriods(InterfaceCommons.getParameter(parameters, ServiceConstants.Performance.PERIOD_PARAMETER, false));
			
			
			log.debug("Export request : {} ",request);

			return perform.generateCSV(request);
		
			
			
		}catch(NumberFormatException e) {
			throw new WebApplicationException(String.format("Unable to parse parameters."),Response.Status.BAD_REQUEST);
		}catch(SQLException e) {
			log.debug("Exception while getting Batch",e);
			throw new WebApplicationException("Unexpected Exception occurred while dealing with database.", e,Response.Status.INTERNAL_SERVER_ERROR);
		} catch (InvalidRequestException e) {
			log.debug("Exception while getting Batch",e);
			throw new WebApplicationException("Unable to search for Batch. ",e,Response.Status.BAD_REQUEST);
		} catch (InternalException e) {			
			log.warn("Unexpected Exception while getting Batch",e);
			throw new WebApplicationException("Unexpected Exception.", e,Response.Status.INTERNAL_SERVER_ERROR); 
		}catch(Throwable t) {
			log.warn("Unexpected Exception while getting Batch",t);
			throw new WebApplicationException("Unexpected Exception.", t,Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path(ServiceConstants.Performance.STATISTICS_PATH+"/{"+ServiceConstants.Performance.BATCH_TYPE_PARAMETER+"}"+"/{"+ServiceConstants.Performance.FARM_ID_PARAMETER+"}")
//	public Map<String,String> getStatistics(@PathParam(ServiceConstants.Performance.BATCH_TYPE_PARAMETER) String batchType,
//			@PathParam(ServiceConstants.Performance.BATCH_TYPE_PARAMETER) Long farmid){
//		
//	}

}
