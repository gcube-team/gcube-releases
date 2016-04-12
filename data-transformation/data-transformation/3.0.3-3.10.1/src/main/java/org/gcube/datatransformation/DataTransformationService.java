package org.gcube.datatransformation;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GRS2ExceptionWrapper;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.codec.binary.Base64;
import org.gcube.datatransformation.client.library.exceptions.DTSException;
import org.gcube.datatransformation.client.library.exceptions.EmptySourceException;
import org.gcube.datatransformation.datatransformationlibrary.DTSCore;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.IOHandler;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Input;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.model.Output;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationProgram;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
import org.gcube.datatransformation.datatransformationlibrary.security.DTSSManager;
import org.gcube.datatransformation.datatransformationlibrary.statistics.StatisticsManager;
import org.gcube.datatransformation.datatransformationlibrary.transformation.model.TransformationDescription;
import org.gcube.datatransformation.datatransformationlibrary.utils.JSONConverter;
import org.gcube.datatransformation.rest.commons.DataTransformationServiceAPI;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Path("/")
public class DataTransformationService implements DataTransformationServiceAPI {
	public static final String SCOPE_HEADER = "gcube-scope";
	
	boolean initialized = false;

	private Gson gson = new Gson(); 
	
	private static final Logger logger = LoggerFactory.getLogger(DataTransformationService.class);
	private boolean isLocal = false;
	
	public DataTransformationService() throws Exception {
		initialize();
	}

	synchronized void initialize() throws Exception {
		if (!initialized) {
			System.out.println("Initializing Data Transformation Service...");
			LogManager.getLogManager().reset();
			ServiceContext sc = new ServiceContext();
			isLocal = sc.isLocal();
			System.out.println("Initializing Data Transformation Service...OK");
			initialized = true;
		}
	}
	
	private static String buildExceptionResponseString(Exception e) {
		JsonObject result = new JsonObject();
		
		result.add("exception", new JsonPrimitive(toString(e)));
		return JSONConverter.toJSON(result);
	}
	
	private static String toString(Serializable o) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.close();
			return new String(Base64.encodeBase64(baos.toByteArray()));
		} catch (IOException e) {
			return null;
		}
	}
	
	@GET
	@Path("/statistics")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	@GZIP
	public Response statistics(	@HeaderParam(SCOPE_HEADER) String scope) {
		String xml = StatisticsManager.toXML();
		
        Response.Status status = Response.Status.OK;
		return Response.status(status).entity(xml).build();
	}
	
	/**
	 * This method transforms the input data to the target {@link ContentType}
	 * and stores the results to the output. In this method the service by its
	 * self discovers the {@link TransformationProgram} to use.
	 * @param inputJSON  The input (type + value)
	 * @param targetContentTypeJSON the target {@link ContentType}
	 * @param outputJSON  The output (type + value)
	 * @param createReportJSON a boolean that denotes if the reporting mechanism shall be enabled
	 * @param all if results are printed
	 * @param pretty if pretty print is in use
	 * @param scope the scope of the request, if provided
	 * @return The output value as well as the identifier of the report (if requested)
	 */
	@POST
	@Path("/transformData")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response transformData(
			@HeaderParam(SCOPE_HEADER) String scope,
			@FormParam("input") String inputJSON,
			@FormParam("targetContentType") String targetContentTypeJSON,
			@FormParam("output") String outputJSON,
			@FormParam("createReport") String createReportJSON,
			@QueryParam("all") @DefaultValue("false") Boolean all,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty
			) {
		long starttime = System.currentTimeMillis();

		String strs[] = new String[]{inputJSON, targetContentTypeJSON, outputJSON, createReportJSON};
		logger.debug(Arrays.asList(strs).toString());
		
		// Parse input
		ContentType targetContentType;
		Input input;
		DataSource source;
		Output output;
		Boolean createReport = null;
		Boolean localExecution = isLocal;
		try {
			// Parse target content type
			try {
				targetContentType = gson.fromJson(targetContentTypeJSON, ContentType.class);
			} catch (Exception e){
				throw new Exception("Target content type not set properly: " + targetContentTypeJSON, e);
			}
			
			/* Creating the report */
			try {
				createReport = gson.fromJson(createReportJSON, Boolean.class);
				ReportManager.initializeReport(createReport);
			} catch (Exception e) {
				logger.warn("Could not create report but continuing with the transformation...", e);
			}

			/* Parse output*/
			try {
				output = gson.fromJson(outputJSON, Output.class);
				if (output.getOutputType().equalsIgnoreCase("push"))
					localExecution = true;
			} catch (Exception e) {
				throw new Exception("Output not set properly: " + outputJSON, e);
			}

			/* Getting the source */
			try {
				input = gson.fromJson(inputJSON, Input.class);
				source = IOHandler.getDataSource(input);
			} catch (Exception e) {
				throw new Exception("Could not create DataSource from the given Input: " + inputJSON, e);
			}

		} catch (Exception e) {
			logger.error("error while reading request", e);

			releaseResources();
			String msg = JSONConverter.toJSON("msg", e.getMessage());
			Response.Status status = Response.Status.BAD_REQUEST;
			return Response.status(status).entity(msg).build();
		}
		
		logger.trace("input: " + input + ", " + 
		"targetContentType: " + targetContentType + ", " +
		"output: " + output + ", " + 
		"createReport: " + createReport
		);
		
		TransformationDescription tDesc = new TransformationDescription(input, output);

		// Getting the dCore Instance and performing the requested transformation
		DTSCore dCore = null;
		try {
			dCore = DTSSManager.getDTSCore(scope);
	
			try {
					
				dCore.initializeAdaptor(tDesc, scope, localExecution);
			} catch (Exception e) {
				throw new Exception("Could not initialize workflow adaptor for the Data Transformation", e);
			}

			dCore.transformData(source, targetContentType);
			
			String retVal = tDesc.getReturnedValue();
			long endtime = System.currentTimeMillis();

			logger.info("Transformation submitted after : " + (endtime - starttime) + " ms. grs : " + retVal);

			return buildResponse(retVal, pretty, all, output.getOutputType());
		} catch (Exception e) {
			logger.error("error while submitting transformation: ", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		} finally {
			dCore.clean();
		}
	}
	
	/**
	 * This method transforms the input data to the target {@link ContentType}
	 * and stores the results to the output. The {@link TransformationProgram}
	 * which will be used by the service is indicated by the client.
	 * 
	 * @param inputJSON
	 *            The input (type + value)
	 * @param tpIDJSON
	 *            the transformation program id
	 * @param targetContentTypeJSON
	 *            the target {@link ContentType}
	 * @param tProgramUnboundParametersJSON
	 *            the transformation unbound parameters
	 * @param outputJSON
	 *            The output (type + value)
	 * @param createReportJSON
	 *            a boolean that denotes if the reporting mechanism shall be
	 *            enabled
	 * @param all
	 *            if results are printed
	 * @param pretty
	 *            if pretty print is in use
	 * @param scope
	 *            the scope of the request, if provided
	 * @return The output value as well as the identifier of the report (if
	 *         requested)
	 */
	@POST
	@Path("/transformDataWithTransformationProgram")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response transformDataWithTransformationProgram(
			@HeaderParam(SCOPE_HEADER) String scope,
			@FormParam("input") String inputJSON,
			@FormParam("tpID") String tpIDJSON,
			@FormParam("targetContentType") String targetContentTypeJSON,
			@FormParam("tProgramUnboundParameters") String tProgramUnboundParametersJSON,
			@FormParam("output") String outputJSON,
			@FormParam("createReport") String createReportJSON,
			@QueryParam("all") @DefaultValue("false") Boolean all,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty
			) {
		long starttime = System.currentTimeMillis();

		String strs[] = new String[]{inputJSON, tpIDJSON, targetContentTypeJSON, tProgramUnboundParametersJSON, outputJSON, createReportJSON};
		logger.debug(Arrays.asList(strs).toString());
		
		// Parse input
		String transformationProgramID = null;
		ContentType targetContentType;
		Input inputD;
		DataSource source;
		Output output;
		Boolean createReport = null;
		Parameter[] programUnboundParameters=null;
		boolean localExecution = isLocal;
		try {
			/* Just checking the parameters */
			transformationProgramID = gson.fromJson(tpIDJSON, String.class);
			if(transformationProgramID==null || transformationProgramID.trim().length()==0){
				throw new Exception("Transformation program ID not set");
			}
			
			// Parse target content type
			try {
				targetContentType = gson.fromJson(targetContentTypeJSON, ContentType.class);
			} catch (Exception e){
				throw new Exception("Target content type not set properly: " + targetContentTypeJSON, e);
			}
			
			// Parse transformation program unbound parameters
			try {
				programUnboundParameters = gson.fromJson(tProgramUnboundParametersJSON, Parameter[].class);
			} catch (Exception e){
				logger.warn("Undefined error when converting unbound parameters from stub to model parameter type", e);
			}
			
			/* Creating the report */
			try {
				createReport = gson.fromJson(createReportJSON, Boolean.class);
				ReportManager.initializeReport(createReport);
			} catch (Exception e) {
				logger.warn("Could not create report but continuing with the transformation...", e);
			}

			/* Parse output*/
			try {
				output = gson.fromJson(outputJSON, Output.class);
				if (output.getOutputType().equalsIgnoreCase("push"))
					localExecution = true;
			} catch (Exception e) {
				throw new Exception("Output not set properly: " + outputJSON, e);
			}

			/* Getting the source */
			try {
				inputD = gson.fromJson(inputJSON, Input.class);
				source = IOHandler.getDataSource(inputD);
			} catch (Exception e) {
				throw new Exception("Could not create DataSource from the given Input: " + inputJSON, e);
			}
		} catch (Exception e) {
			logger.error("error while reading request", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.BAD_REQUEST;
			return Response.status(status).entity(msg).build();
		}
		
		logger.trace("input: " + inputD + ", " + 
		"targetContentType: " + targetContentType + ", " +
		"output: " + output + ", " + 
		"createReport: " + createReport
		);
		
		TransformationDescription tDesc = new TransformationDescription(inputD, output);

		// Getting the dCore Instance and performing the requested transformation
		DTSCore dCore = null;
		try {
			dCore = DTSSManager.getDTSCore(scope);
	
			try {
				dCore.initializeAdaptor(tDesc, scope, localExecution);
			} catch (Exception e) {
				throw new Exception("Could not initialize workflow adaptor for the Data Transformation", e);
			}

			dCore.transformDataWithTransformationProgram(source, transformationProgramID, programUnboundParameters, targetContentType);
			
			String retVal = tDesc.getReturnedValue();
			long endtime = System.currentTimeMillis();

			logger.info("Transformation submitted after : " + (endtime - starttime) + " ms. grs : " + retVal);

			return buildResponse(retVal, pretty, all, output.getOutputType());
		} catch (Exception e) {
			logger.error("error while submitting transformation: ", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		} finally {
			dCore.clean();
		}
	}

	/**
	 * This method transforms the input data to the target {@link ContentType}
	 * and stores the results to the output. The {@link TransformationUnit}
	 * which will be used by the service is indicated by the client.
	 * 
	 * @param inputsJSON
	 *            The inputs (type + value)
	 * @param tpIDJSON
	 *            the transformation program id
	 * @param transformationUnitIDJSON
	 *            the unit id of the program
	 * @param targetContentTypeJSON
	 *            the target {@link ContentType}
	 * @param tProgramUnboundParametersJSON
	 *            the transformation unbound parameters
	 * @param outputJSON
	 *            The output (type + value)
	 * @param filterSourcesJSON
	 *            either to filter the sources or not
	 * @param createReportJSON
	 *            a boolean that denotes if the reporting mechanism shall be
	 *            enabled
	 * @param all
	 *            if results are printed
	 * @param pretty
	 *            if pretty print is in use
	 * @param scope
	 *            the scope of the request, if provided
	 * @return The output value as well as the identifier of the report (if
	 *         requested)
	 */
	@POST
	@Path("/transformDataWithTransformationUnit")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response transformDataWithTransformationUnit(
			@HeaderParam(SCOPE_HEADER) String scope,
			@FormParam("inputs") String inputsJSON,
			@FormParam("tpID") String tpIDJSON,
			@FormParam("transformationUnitID") String transformationUnitIDJSON,
			@FormParam("targetContentType") String targetContentTypeJSON,
			@FormParam("tProgramUnboundParameters") String tProgramUnboundParametersJSON,
			@FormParam("output") String outputJSON,
			@FormParam("filterSources") String filterSourcesJSON,
			@FormParam("createReport") String createReportJSON,
			@QueryParam("all") @DefaultValue("false") Boolean all,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty
			) {
		long starttime = System.currentTimeMillis();

		String strs[] = new String[]{inputsJSON, tpIDJSON, transformationUnitIDJSON, targetContentTypeJSON, tProgramUnboundParametersJSON, outputJSON, filterSourcesJSON, createReportJSON};
		logger.debug(Arrays.asList(strs).toString());
		
		// Parse input
		String transformationProgramID = null, transformationUnitID = null;
		ContentType targetContentType;
		Input inputD;
		Output output;
		Boolean filterSources = false;
		Boolean createReport = false;
		Parameter[] programUnboundParameters=null;
		boolean localExecution = isLocal;
		try {
			/* Just checking the parameters */
			transformationProgramID = gson.fromJson(tpIDJSON, String.class);
			if(transformationProgramID==null || transformationProgramID.trim().length()==0){
				throw new Exception("Transformation program ID not set");
			}
			
			transformationUnitID = gson.fromJson(transformationUnitIDJSON, String.class);
			if(transformationUnitID==null || transformationUnitID.trim().length()==0){
				throw new Exception("Transformation unit ID not set");
			}

			// Parse target content type
			try {
				targetContentType = gson.fromJson(targetContentTypeJSON, ContentType.class);
			} catch (Exception e){
				throw new Exception("Target content type not set properly: " + targetContentTypeJSON, e);
			}
			
			// Parse transformation program unbound parameters
			try {
				programUnboundParameters = gson.fromJson(tProgramUnboundParametersJSON, Parameter[].class);
			} catch (Exception e){
				logger.warn("Undefined error when converting unbound parameters from stub to model parameter type", e);
			}
			
			/* Creating the report */
			try {
				createReport = gson.fromJson(createReportJSON, Boolean.class);
				ReportManager.initializeReport(createReport);
			} catch (Exception e) {
				logger.warn("Could not create report but continuing with the transformation...", e);
			}
			
			/* filter */
			try {
				filterSources = gson.fromJson(filterSourcesJSON, Boolean.class);
			} catch (Exception e) {
				logger.warn("Could not get filterSources value. set to false", e);
			}

			/* Parse output*/
			try {
				output = gson.fromJson(outputJSON, Output.class);
				if (output.getOutputType().equalsIgnoreCase("push"))
					localExecution = true;
			} catch (Exception e) {
				throw new Exception("Output not set properly: " + outputJSON, e);
			}

			/* Getting the source */
			try {
				inputD = gson.fromJson(inputsJSON, Input[].class)[0];
			} catch (Exception e) {
				throw new Exception("Could not create DataSource from the given Input: " + inputsJSON, e);
			}
		} catch (Exception e) {
			logger.error("error while reading request", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.BAD_REQUEST;
			return Response.status(status).entity(msg).build();
		}
		
		logger.trace("input: " + inputD + ", " + 
		"targetContentType: " + targetContentType + ", " +
		"output: " + output + ", " + 
		"createReport: " + createReport
		);
		
		TransformationDescription tDesc = new TransformationDescription(inputD, output);

		// Getting the dCore Instance and performing the requested transformation
		DTSCore dCore = null;
		try {
			dCore = DTSSManager.getDTSCore(scope);
	
			try {
				dCore.initializeAdaptor(tDesc, scope, localExecution);
			} catch (Exception e) {
				throw new Exception("Could not initialize workflow adaptor for the Data Transformation", e);
			}

			dCore.transformDataWithTransformationUnit(transformationProgramID, transformationUnitID, programUnboundParameters, targetContentType, filterSources);
			
			String retVal = tDesc.getReturnedValue();
			long endtime = System.currentTimeMillis();
			
			if (retVal == null)
				throw new DTSException("Transformation error");
			else if (retVal.trim().isEmpty())
				throw new EmptySourceException("DataSource timeout after " + (endtime - starttime) + "ms");
			
			logger.info("Transformation submitted after : " + (endtime - starttime) + " ms. grs : " + retVal);

			return buildResponse(retVal, pretty, all, output.getOutputType());
		} catch (Exception e) {
			logger.error("error while submitting transformation: ", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		} finally {
			if (dCore != null)
				dCore.clean();
		}
	}

	/**
	 * Searches for {@link TransformationUnit}s that are able to perform a
	 * transformation from a source to a target {@link ContentType}.
	 * 
	 * @param sourceContentTypeJSON
	 *            source {@link ContentType}
	 * @param targetContentTypeJSON
	 *            target {@link ContentType}
	 * @param createAndPublishCompositeTPJSON
	 *            a boolean that denotes if the reporting mechanism shall be
	 *            enabled
	 * @param all
	 *            if results are printed
	 * @param pretty
	 *            if pretty print is in use
	 * @param scope
	 *            the scope of the request, if provided
	 * @return One or more available transformation units that can perform the
	 *         conversion from the source to the target {@link ContentType}.
	 */
	@POST
	@Path("/findApplicableTransformationUnits")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response findApplicableTransformationUnits(
			@HeaderParam(SCOPE_HEADER) String scope,
			@FormParam("sourceContentType") String sourceContentTypeJSON,
			@FormParam("targetContentType") String targetContentTypeJSON,
			@FormParam("createAndPublishCompositeTP") String createAndPublishCompositeTPJSON,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty
			) {
		long starttime = System.currentTimeMillis();

		String strs[] = new String[]{sourceContentTypeJSON, targetContentTypeJSON, createAndPublishCompositeTPJSON};
		logger.debug(Arrays.asList(strs).toString());
		
		// Parse input
		ContentType sourceContentType, targetContentType;
		Boolean createAndPublishCompositeTP = false;
		try {
			// Parse source content type
			try {
				sourceContentType = gson.fromJson(sourceContentTypeJSON, ContentType.class);
			} catch (Exception e){
				throw new Exception("Target content type not set properly: " + sourceContentTypeJSON, e);
			}

			// Parse target content type
			try {
				targetContentType = gson.fromJson(targetContentTypeJSON, ContentType.class);
			} catch (Exception e){
				throw new Exception("Target content type not set properly: " + targetContentTypeJSON, e);
			}

			/* Creating the report */
			try {
				createAndPublishCompositeTP = gson.fromJson(createAndPublishCompositeTPJSON, Boolean.class);
			} catch (Exception e) {
				logger.warn("Could not determine createAndPublishCompositeTP but continuing: " + createAndPublishCompositeTPJSON);
			}
		} catch (Exception e) {
			logger.error("error while reading request", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.BAD_REQUEST;
			return Response.status(status).entity(msg).build();
		}
		
		logger.trace("sourceContentType: " + sourceContentType + ", " + 
		"targetContentType: " + targetContentType + ", " +
		"createAndPublishCompositeTP: " + createAndPublishCompositeTP
		);
		
		// Getting the dCore Instance and performing the requested transformation
		try {
			DTSCore dCore;
			dCore = DTSSManager.getDTSCore(scope);
	
			ArrayList<TransformationUnit> transformations = dCore.findApplicableTransformationUnits(sourceContentType, targetContentType, createAndPublishCompositeTP);
			
			List<Map<String,String>> resp = new ArrayList<Map<String,String>>();
			for(TransformationUnit transformation: transformations){
				Map<String,String> map = new HashMap<>();
				map.put("transformationUnitID", transformation.getId());
				map.put("transformationProgramID", transformation.getTransformationProgram().getId());
				resp.add(map);
			}
			
			Map<String, List<Map<String,String>>> response = new HashMap<String, List<Map<String,String>>>();
			response.put("TPAndTransformationUnitIDs", resp);
			String msg = null;
			
			msg = JSONConverter.toJSON(response);
			logger.debug("Returning response: " + msg);
			
			long endtime = System.currentTimeMillis();

			logger.info("Transformation submitted after : " + (endtime - starttime) + " ms. grs : " + msg);


			Response.Status status = Response.Status.OK;
			return Response.status(status).entity(msg).build();
		} catch (Exception e) {
			logger.error("error while submitting transformation: ", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		}
	}

	/**
	 * Searches for {@link ContentType}s to which an object can be transformed.
	 * 
	 * @param sourceContentTypeJSON
	 *            source {@link ContentType}
	 * @param all
	 *            if results are printed
	 * @param pretty
	 *            if pretty print is in use
	 * @param scope
	 *            the scope of the request, if provided
	 * @return One or more available target {@link ContentType}s.
	 */
	@POST
	@Path("/findAvailableTargetContentTypes")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response findAvailableTargetContentTypes(
			@HeaderParam(SCOPE_HEADER) String scope,
			@FormParam("sourceContentType") String sourceContentTypeJSON,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty
			) {
		long starttime = System.currentTimeMillis();

		String strs[] = new String[]{sourceContentTypeJSON};
		logger.debug(Arrays.asList(strs).toString());
		
		// Parse input
		ContentType sourceContentType;
		try {
			// Parse source content type
			try {
				sourceContentType = gson.fromJson(sourceContentTypeJSON, ContentType.class);
				if (sourceContentType == null) throw new Exception("source content type is null");
			} catch (Exception e){
				throw new Exception("Target content type not set properly: " + sourceContentTypeJSON, e);
			}

		} catch (Exception e) {
			logger.error("error while reading request", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.BAD_REQUEST;
			return Response.status(status).entity(msg).build();
		}
		
		logger.trace("sourceContentType: " + sourceContentType);
		
		// Getting the dCore Instance and performing the requested transformation
		try {
			DTSCore dCore;
			dCore = DTSSManager.getDTSCore(scope);
	
			ArrayList<ContentType> targetContentTypes = dCore.getAvailableTargetContentTypes(sourceContentType);
			
			long endtime = System.currentTimeMillis();


			String msg = null;

			Map<String, ArrayList<ContentType>> response = new HashMap<String, ArrayList<ContentType>>();
			response.put("targetContentTypes", targetContentTypes);
			msg = JSONConverter.toJSON(response, pretty);

			logger.info("Transformation submitted after : " + (endtime - starttime) + " ms. grs : " + msg);
			
			Response.Status status = Response.Status.OK;
			return Response.status(status).entity(msg).build();
		} catch (Exception e) {
			logger.error("error while submitting transformation: ", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		}
	}

	/**
	 * Performs a query to get information about the transformation programs.
	 * 
	 * @param queryJSON The query about the transformation programs
	 * @param all
	 *            if results are printed
	 * @param pretty
	 *            if pretty print is in use
	 * @param scope
	 *            the scope of the request, if provided
	 * @return The result of the query in xml format.
	 */
	@POST
	@Path("/queryTransformationPrograms")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	@GZIP
	public Response queryTransformationPrograms(
			@HeaderParam(SCOPE_HEADER) String scope,
			@FormParam("query") String queryJSON,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty
			) {
		long starttime = System.currentTimeMillis();

		String strs[] = new String[]{queryJSON};
		logger.debug(Arrays.asList(strs).toString());
		
		// Parse input
		String query;
		try {
			// Parse query
			try {
				query = queryJSON;
				if (query == null || query.trim().length() == 0) throw new Exception("query is null");
			} catch (Exception e){
				throw new Exception("Could not query transfomration programs: " + queryJSON, e);
			}

		} catch (Exception e) {
			logger.error("error while reading request", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.BAD_REQUEST;
			return Response.status(status).entity(msg).build();
		}
		
		logger.trace("query: " + queryJSON);
		
		// Getting the dCore Instance and performing the requested transformation
		try {
			DTSCore dCore;
			dCore = DTSSManager.getDTSCore(scope);
	
			String resp = dCore.getIManager().queryTransformationPrograms(query);
			
			long endtime = System.currentTimeMillis();

			logger.info("Transformation submitted after : " + (endtime - starttime) + " response: " + resp);

			String msg = null;

			msg = resp;

			Response.Status status = Response.Status.OK;
			return Response.status(status).entity(msg).build();
		} catch (Exception e) {
			logger.error("error while submitting transformation: ", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		}
	}
	
	/**
	 * Releases any resources which were used for the transformation.
	 */
	private void releaseResources(){
		if(ReportManager.isReportingEnabled()){
			try {
				ReportManager.closeReport();
			} catch (Exception e) {
				logger.error("Could not close report", e);
			}
		}
	}
	
	private Response buildResponse(final String retVal, final boolean pretty, boolean all, String outputType) {
		try{
			String msg;
			if (all && (outputType.equals("GRS2") || outputType.equals("RS2"))) {
				StreamingOutput stream = new StreamingOutput() {
					@Override
					public void write(OutputStream os) throws IOException, WebApplicationException {
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
						try {
							readResults(writer, retVal, pretty);
						} catch (Exception e) {
							logger.error("error while retrieving results:", e);
							releaseResources();
							String msg = buildExceptionResponseString(e);
							writer.write(msg);
							writer.flush();
						}
					}
				};

				return Response.ok(stream).build();
			} else		{
				Map<String, String> response = new HashMap<String, String>();
				response.put("output", retVal);
				if(ReportManager.isReportingEnabled()){
					try {
						response.put("reportEPR", ReportManager.getReport().getReportEndpoint());
						logger.debug("Report RS EPR: " + ReportManager.getReport().getReportEndpoint());
					} catch (Exception e) {
						logger.warn("Could not get the RS EPR of the Report, but continuing nevertheless...", e);
					}
				}
				msg = JSONConverter.toJSON(response, pretty);
			}
			return Response.ok().entity(msg).build();
		} catch (Exception e) {
			logger.error("error while submitting transformation: ", e);

			releaseResources();
			String msg = buildExceptionResponseString(e);
			Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
			return Response.status(status).entity(msg).build();
		}
	}
	
	static void readResults(BufferedWriter writer, String rsLocator, Boolean pretty) throws Exception {
		ForwardReader<GenericRecord> reader = new ForwardReader<GenericRecord>(URI.create(rsLocator));
		writer.write("[");
		if (pretty)
			writer.newLine();
		boolean first = true;
		while (!(reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0))) {
			GenericRecord rec = reader.get(60, TimeUnit.SECONDS);
			if (rec == null)
				continue;
			if (!first) {
				writer.write(",");
				if (pretty)
					writer.newLine();
			}
			if(rec instanceof GRS2ExceptionWrapper) {
				writer.write(buildExceptionResponseString(new Exception(((GRS2ExceptionWrapper)rec).getEx())));
				continue;
			}
			first = false;
			JsonObject jo = new JsonObject();
			for (Field field : rec.getFields()) {
				String fieldID = field.getFieldDefinition().getName();

				String fieldName = null;
				fieldName = fieldID;

				String fieldValue = null;
				if (field instanceof FileField) {
					File f = ((FileField) field).getPayload();
					fieldValue = Charset.defaultCharset().decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get(f.getAbsolutePath())))).toString();
				} else if (field instanceof StringField)
					fieldValue = ((StringField) field).getPayload();
				
//				fieldValue = fieldValue.length() > 1024? fieldValue.substring(0, 1024) + "..." : fieldValue;
				
				jo.add(fieldName, new JsonPrimitive(fieldValue));
			}
			writer.write(JSONConverter.toJSON(jo));
			writer.flush();
		}
		if (pretty)
			writer.newLine();
		writer.write("]");
		writer.flush();
		}
}
