package org.gcube.datatransformation.rest.commons;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;

public interface DataTransformationServiceAPI {
	public static String SCOPE_HEADER = "gcube-scope";

	@GET
	@Path("/statistics")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	@GZIP
	public Response statistics(
			@HeaderParam(SCOPE_HEADER) String scope
			);

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
			);
	
	@POST
	@Path("/transformDataWithTransformationProgram")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response transformDataWithTransformationProgram(
			@HeaderParam("gcube-scope") String scope,
			@FormParam("input") String inputJSON,
			@FormParam("tpID") String tpIDJSON,
			@FormParam("targetContentType") String targetContentTypeJSON,
			@FormParam("tProgramUnboundParameters") String tProgramUnboundParametersJSON,
			@FormParam("output") String outputJSON,
			@FormParam("createReport") String createReportJSON,
			@QueryParam("all") @DefaultValue("false") Boolean all,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty
			);

	@POST
	@Path("/transformDataWithTransformationUnit")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response transformDataWithTransformationUnit(
			@HeaderParam("gcube-scope") String scope,
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
			);

	@POST
	@Path("/findApplicableTransformationUnits")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response findApplicableTransformationUnits(
			@HeaderParam("gcube-scope") String scope,
			@FormParam("sourceContentType") String sourceContentTypeJSON,
			@FormParam("targetContentType") String targetContentTypeJSON,
			@FormParam("createAndPublishCompositeTP") String createAndPublishCompositeTPJSON,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty
			);
	
	@POST
	@Path("/findAvailableTargetContentTypes")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response findAvailableTargetContentTypes(
			@HeaderParam("gcube-scope") String scope,
			@FormParam("sourceContentType") String sourceContentTypeJSON,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty
			);

	@POST
	@Path("/queryTransformationPrograms")
	@Produces(MediaType.APPLICATION_XML + "; " + "charset=UTF-8")
	@GZIP
	public Response queryTransformationPrograms(
			@HeaderParam("gcube-scope") String scope,
			@FormParam("query") String queryJSON,
			@QueryParam("pretty") @DefaultValue("false") Boolean pretty
			);
}
