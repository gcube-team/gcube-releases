package org.gcube.portal.social.networking.ws.methods.v2;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.util.ArrayList;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.portal.social.networking.ws.filters.RequestsAuthFilter;
import org.gcube.portal.social.networking.ws.inputs.ApplicationId;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.gcube.portal.social.networking.ws.utils.ErrorMessages;
import org.gcube.portal.social.networking.ws.utils.Utils;
import org.slf4j.LoggerFactory;


/**
 * REST interface for the social networking library (tokens).
 * @author Costantino Perciante at ISTI-CNR
 */
@Path("2/tokens")
@Api(tags={"/2/tokens"}, description="Endpoint for tokens resources", protocols="https", authorizations={@Authorization(value=RequestsAuthFilter.AUTH_TOKEN)})
public class Tokens {

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Tokens.class);

	@POST
	@Path("generate-application-token/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Generate an application token", notes="Generate an application token for the application with id app_id", 
	response=ResponseBean.class, nickname="generate-application-token")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Successful creation of the token, reported in the 'result' field of the returned object", response = ResponseBean.class),
			@ApiResponse(code = 403, message = "There is no application profile with such id", response=ResponseBean.class),
			@ApiResponse(code = 500, message = ErrorMessages.errorMessageApiResult, response=ResponseBean.class)})
	public Response generateApplicationToken(
			@NotNull(message="Missing input parameter") 
			@Valid
			@ApiParam(name="input", required=true, allowMultiple=false, value="The bean containing the app_id field")
			ApplicationId input) throws ValidationException{

		logger.debug("Incoming request for app token generation.");
		String context = ScopeProvider.instance.get();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.CREATED;

		String appId = input.getAppId();

		try {

			// check if the token actually matches an application profile
			ApplicationProfile appProfile = Utils.getProfileFromInfrastrucure(appId, context);

			if(appProfile == null){

				logger.error("The given id doesn't belong to an application!!!");
				responseBean.setSuccess(false);
				responseBean.setMessage(ErrorMessages.idNotApp);
				status = Status.FORBIDDEN;
				return Response.status(status).entity(responseBean).build();

			}

			logger.info("Generating token for the application with id " + appId);

			// each token is related to an identifier and the context
			String appToken = authorizationService().generateUserToken(new UserInfo(appId, new ArrayList<String>()), context);
			responseBean.setSuccess(true);
			responseBean.setResult(appToken);

		} catch (Exception e) {

			logger.error("Unable to generate token for app " + appId + " and scope " + context);
			status = Status.INTERNAL_SERVER_ERROR;
			responseBean.setSuccess(false);
			responseBean.setMessage(ErrorMessages.tokenGenerationFailed);
			return Response.status(status).entity(responseBean).build();

		}

		return Response.status(status).entity(responseBean).build();
	}

}
