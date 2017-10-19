package gr.cite.gos.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.Response.Status.*;

import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;
import gr.cite.geoanalytics.dataaccess.entities.coverage.dao.CoverageDao;

@Service
@Path("/RasterManagement")
public class RasterManagementResource {

	private CoverageDao coverageDao;

	private ObjectMapper mapper;

	private static Logger logger = LoggerFactory.getLogger(RasterManagementResource.class);

	@Inject
	public RasterManagementResource(CoverageDao coverageDao) {
		this.coverageDao = coverageDao;
		mapper = new ObjectMapper();
	}

	@POST
	@Path("coverage/create")
	@Transactional(rollbackFor = { Exception.class })
	public Response createCoverageInDatabase(@FormParam("coverage") String coverageJson) {
		logger.info("Inserting coverage request");

		Coverage coverage = null;

		try {
			coverage = mapper.readValue(coverageJson, Coverage.class);

			Assert.notNull(coverage, "No Coverage object was retrieved");
			Assert.isTrue(coverage.getImage().length > 0, "No geotiff image was found");

			coverageDao.create(coverage);
		} catch (IllegalArgumentException e) {
			return errorResponse(BAD_REQUEST, "Could not insert GeoTIFF image in database. " + e.getMessage(), e);
		} catch (Exception e) {
			return errorResponse(INTERNAL_SERVER_ERROR, "Could not insert GeoTIFF image in database", e);
		}

		return successResponse(CREATED, "GeoTIFF " + coverage.getName() + " has been inserted into database successfully!", null);
	}

	@GET
	@Path("coverage/get/{layerId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(rollbackFor = { Exception.class })
	public Response getCoverageFromDatabase(@PathParam("layerId") String layerId) {
		logger.info("Inserting coverage request");

		Coverage coverage = null;

		try {
			Assert.isTrue(layerId != null && layerId.length() > 0, "Layer ID cannot be empty");

			coverage = coverageDao.findCoverageByLayer(UUID.fromString(layerId));
			
			if(coverage == null){
				throw new Exception("Coverage of layer with id " + layerId + " was not found in database");
			}
		} catch (IllegalArgumentException e) {
			return errorResponse(BAD_REQUEST, "Could not retrieve GeoTIFF image in database. " + e.getMessage(), e);
		} catch (Exception e) {
			return errorResponse(INTERNAL_SERVER_ERROR, "Could not retrieve GeoTIFF image in database", e);
		}

		return successResponse(OK, "Coverage " + layerId + " has been retrieved successfully!", coverage);
	}
	
	@DELETE
	@Path("coverage/delete/{layerId}")
	@Transactional(rollbackFor = { Exception.class })
	public Response deleteGeoTIFFFromDatabase(@PathParam("layerId") String layerId) {
		logger.info("Deleting coverage request");

		Coverage coverage = null;

		try {
			Assert.isTrue(layerId != null && layerId.length() > 0, "Layer ID cannot be empty");

			coverage = coverageDao.findCoverageByLayer(UUID.fromString(layerId));
			
			if(coverage == null){
				throw new Exception("Coverage of layer with id " + layerId + " was not found in database");
			}
			
			coverageDao.delete(coverage);
		} catch (IllegalArgumentException e) {
			return errorResponse(BAD_REQUEST, "Could not retrieve GeoTIFF image in database. " + e.getMessage(), e);
		} catch (Exception e) {
			return errorResponse(INTERNAL_SERVER_ERROR, "Could not retrieve GeoTIFF image in database", e);
		}
		
		return successResponse(OK, "Coverage " + layerId + " has been deleted successfully!", null);
	}

	@GET
	@Path("ping")
	public String ping() {
		return "pong";
	}

	private Response errorResponse(Status status, String message, Exception e) {
		logger.error(null, e);
		return Response.status(status).entity(message).build();
	}

	private Response successResponse(Status status, String message, Object body) {
		logger.info(message);

		String responseJson = null;

		try {
			responseJson = mapper.writeValueAsString(body != null ? body : message);
		} catch (Exception e) {
			logger.error(null, e);
			status = INTERNAL_SERVER_ERROR;
			responseJson = "Failed to convert response to JSON";
		}

		return Response.status(status).entity(responseJson).build();
	}
}
