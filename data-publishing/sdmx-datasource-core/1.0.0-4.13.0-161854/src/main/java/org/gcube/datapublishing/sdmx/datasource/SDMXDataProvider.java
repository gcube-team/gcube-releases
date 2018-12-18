package org.gcube.datapublishing.sdmx.datasource;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.gcube.datapublishing.sdmx.datasource.config.ConfigurationManager;
import org.gcube.datapublishing.sdmx.datasource.config.DataSourceConfigurationException;
import org.gcube.datapublishing.sdmx.datasource.datatype.DataTypeBean;
import org.sdmxsource.sdmx.api.constants.STRUCTURE_OUTPUT_FORMAT;
import org.sdmxsource.sdmx.api.manager.retrieval.rest.RestDataQueryManager;
import org.sdmxsource.sdmx.api.manager.retrieval.rest.RestStructureQueryManager;
import org.sdmxsource.sdmx.api.model.data.DataFormat;
import org.sdmxsource.sdmx.api.model.format.StructureFormat;
import org.sdmxsource.sdmx.api.model.query.RESTDataQuery;
import org.sdmxsource.sdmx.api.model.query.RESTStructureQuery;
import org.sdmxsource.sdmx.sdmxbeans.model.SdmxStructureFormat;
import org.sdmxsource.sdmx.sdmxbeans.model.beans.reference.RESTDataQueryImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.beans.reference.RESTStructureQueryImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.data.SdmxDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/* This class defines the web API of the individual project resource. It may handle PUT, GET and/or DELETE requests 
   depending on the specific CIM of the service.*/



@Configurable
@Path("/ws")
public class SDMXDataProvider{
	private Logger logger;

	private ConfigurationManager configurationManager;
	
//	@Autowired
//	private DataWriterManager dataWriterManager;
	
	public SDMXDataProvider() {
		this.logger = LoggerFactory.getLogger(SDMXDataProvider.class);
	}
	
	@Autowired
	private RestDataQueryManager dataQueryManager;
	
	@Autowired
	private RestStructureQueryManager structureQueryManager;

	private Map<String, String> selectParameters (MultivaluedMap<String, String> queryParamsMap)
	{
		this.logger.debug("Selecting received parameters");
		Map<String, String> response = new HashMap<String, String>();
		Iterator<String> queryParams = queryParamsMap.keySet().iterator();
    	while (queryParams.hasNext())
    	{
    		String paramName = queryParams.next();
    	
    		if (!this.configurationManager.getExcludedQueryParameters().contains(paramName.toLowerCase()))
    		{
        		List<String> valueList = queryParamsMap.get(paramName);
    			String value = valueList.get(0);
    			this.logger.debug("Parameter name "+paramName+ " value "+value);
    			response.put(paramName, value);
    		}
    		

    	}
	
    	return response;
	}
	

	




	@GET
	@Path("/{path:.+}")
    public Response getData(@PathParam("path") String restQuery, @HeaderParam ("Accept") String accept,@Context UriInfo uri){
		
		
		this.logger.info("Received rest query get "+restQuery);
		this.logger.info("Requested data format "+accept);
		
		try
		{
			this.configurationManager.init();
		} catch (DataSourceConfigurationException e)
		{
			this.logger.error("Unable to complete the configuration",e);
			return Response.serverError().entity(new String ("Invalid internal configuration")).build();
		}
		

		Map<String, String> params = selectParameters(uri.getQueryParameters());
		
		if(restQuery.toLowerCase().startsWith("data/"))
		{
			RESTDataQuery dataQueryRest = new RESTDataQueryImpl(restQuery, params);
			DataTypeBean dataType = this.configurationManager.getDataTypeManager().getDataType(accept);
			DataFormat dataformat = new SdmxDataFormat(dataType.getSdmxDataType());
			this.logger.info("Data Query: " + dataQueryRest);
			this.logger.info("Response Format: " + dataformat);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			dataQueryManager.executeQuery(dataQueryRest, dataformat, outputStream);
			String responseData = new String (outputStream.toByteArray());
			this.logger.info(responseData);
			return Response.ok(responseData, dataType.getResponseDataType()).build();
			
		} else 
		{
			RESTStructureQuery structureQueryRest = new RESTStructureQueryImpl(restQuery, params);
			StructureFormat structureFormat = new SdmxStructureFormat(STRUCTURE_OUTPUT_FORMAT.SDMX_V21_STRUCTURE_DOCUMENT);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			structureQueryManager.getStructures(structureQueryRest, outputStream, structureFormat);
			return Response.ok(new String (outputStream.toByteArray())).build();
		}
	}
  

	

	
	





	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}
	
	

}

