package org.gcube.portal.social.networking.ws.methods.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.portal.social.networking.ws.filters.RequestsAuthFilter;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST interface for the social networking library (users).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("2/users")
@Api(tags={"/2/users"}, description="Endpoint for user resources", protocols="https", authorizations={@Authorization(value=RequestsAuthFilter.AUTH_TOKEN)})
public class Users {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Users.class);

	// user manager
	private UserManager userManager = new LiferayUserManager();

	@GET
	@Path("get-custom-attribute/")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Read a user's custom attribute", notes="Read a user's custom attribute. The user is the one owning the token", 
	response=ResponseBean.class, nickname="get-custom-attribute")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful read of the attribute, reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 404, message = "Such an attribute doesn't exist", response = ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response readCustomAttr(
			@QueryParam("attribute") 
			@NotNull(message="attribute name is missing") 
			@ApiParam(name="attribute", required=true, allowMultiple=false, value="The key of the attribute to read")
			String attributeKey
			) throws ValidationException {

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		try{

			GCubeUser user = userManager.getUserByUsername(username);
			String toReturn = (String)userManager.readCustomAttr(user.getUserId(), attributeKey);
			responseBean.setSuccess(true);
			responseBean.setResult(toReturn);

		}catch(Exception e){

			logger.error("Unable to retrieve attribute for user.", e);
			responseBean.setMessage(e.toString());
			responseBean.setSuccess(false);
			status = Status.NOT_FOUND;

		}

		return Response.status(status).entity(responseBean).build();
	}

	@PUT
	@Path("update-custom-attribute")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Update a user's custom attribute", notes="Update a user's custom attribute. The user is the one owning the token", 
	response=ResponseBean.class, nickname="update-custom-attribute")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful update of the attribute, the new value is reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 400, message = "Key or value for the new attribute missing", response=ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class),
			@ApiResponse(code = 304, message = "Attribute not modified", response=ResponseBean.class)})
	public Response updateCustomAttr(
			@NotNull(message="input is missing") 
			@ApiParam(name="input", required=true, allowMultiple=false, value="The object having an attribute key and a value key for the new value")
			String inputJsonObj 
			) throws ValidationException{

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.OK;

		try{

			// Jackson parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(inputJsonObj);
			String attributeKey = actualObj.get("attribute").asText();
			String newValue = actualObj.get("value").asText(); 

			if(attributeKey == null || attributeKey.isEmpty() || newValue == null){

				logger.error("Missing/wrong request parameters");
				status = Status.BAD_REQUEST;
				responseBean.setMessage(ErrorMessages.missingParameters);
				return Response.status(status).entity(responseBean).build();

			}

			GCubeUser user = userManager.getUserByUsername(username);
			userManager.saveCustomAttr(user.getUserId(), attributeKey, newValue);
			responseBean.setSuccess(true);
			responseBean.setResult(newValue);

		}catch(Exception e){

			logger.error("Unable to set attribute for user.", e);
			status = Status.NOT_MODIFIED;
			responseBean.setMessage(e.toString());

		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-fullname")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Read the user's fullname", notes="Read the user's fullname. The user is the one owning the token", 
	response=ResponseBean.class, nickname="get-fullname")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful update of the attribute, the value is reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response getUserUsername(){

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String fullName = null;
		ResponseBean responseBean = new ResponseBean();
		responseBean.setSuccess(false);
		Status status = Status.OK;

		try{

			GCubeUser user = userManager.getUserByUsername(username);
			fullName = user.getFullname();
			logger.info("Found fullname " + fullName + " for user " +  username);
			responseBean.setResult(fullName);
			responseBean.setSuccess(true);

		}catch(Exception e){

			logger.error("Unable to retrieve attribute for user.", e);
			status = Status.INTERNAL_SERVER_ERROR;

		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-email")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Read the user's email address", notes="Read the user's email address. The user is the one owning the token", 
	response=ResponseBean.class, nickname="get-email")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful update of the attribute, the value is reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 500, message = "The error is reported into the 'message' field of the returned object", response=ResponseBean.class)})
	public Response getUserEmail(){

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String email = null;
		ResponseBean responseBean = new ResponseBean();
		responseBean.setSuccess(false);
		Status status = Status.OK;
		try{

			GCubeUser user = userManager.getUserByUsername(username);
			email = user.getEmail();
			logger.info("Found email " + email + " for user " +  username);
			responseBean.setResult(email);
			responseBean.setSuccess(true);

		}catch(Exception e){

			logger.error("Unable to retrieve attribute for user.", e);
			status = Status.INTERNAL_SERVER_ERROR;

		}

		return Response.status(status).entity(responseBean).build();
	}

}
