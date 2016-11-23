package it.eng.rdlab.soa3.authn.rest.jaxrs;


import it.eng.rdlab.soa3.assertion.manager.AssertionValidationFactory;
import it.eng.rdlab.soa3.assertion.manager.SAMLUtils;
import it.eng.rdlab.soa3.assertion.manager.SamlConstants;
import it.eng.rdlab.soa3.assertion.validation.IAssertionValidator;
import it.eng.rdlab.soa3.authn.rest.IAuthenticationService;
import it.eng.rdlab.soa3.authn.rest.bean.AuthenticateResponseBean;
import it.eng.rdlab.soa3.authn.rest.exceptions.JSONParserException;
import it.eng.rdlab.soa3.authn.rest.impl.AuthenticationServiceImpl;
import it.eng.rdlab.soa3.authn.rest.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.w3c.dom.Element;

import com.sun.jersey.api.core.HttpContext;

/**
 * 
 * @author Kanchanna Ramasamy Balraj
 * @author Ermanno Travaglino
 * 
 */

@Path("/authenticate")
public class AuthenticationService 
{
	private  Log logger = LogFactory.getLog(AuthenticationService.class);

	public AuthenticationService()
	{
		this.logger = LogFactory.getLog(this.getClass());

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String isUserAuthenticated(@Context HttpContext context) 
	{

		String authHeader = context.getRequest().getHeaderValue("Authorization");
		String organizationHeader = context.getRequest().getHeaderValue("Organization");
		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
		mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
		mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
		IAssertionValidator validator = AssertionValidationFactory.getCurrentInstance();

		if (authHeader != null) 
		{
			logger.debug("Auth header = "+authHeader);
			
			if(!authHeader.contains(" "))
			{
				logger.error("check the if the \"Authorization\" header has a value of the format : Basic Base64encoded{username:password}");
				throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(" Missing or invalid request contents ").build());
			}
			String encodedValue [] = authHeader.split(" ");
			String decodedValue = new String(Base64.decodeBase64(encodedValue[1].getBytes()));
			
			if (!encodedValue [0].contains("FED"))
			{
				if (organizationHeader != null) organizationHeader = organizationHeader.trim();
				
				return usernamePasswordAuthentication(decodedValue,organizationHeader, mapper);
			}
			else
			{
				logger.debug("The Authorization string is a SAML Assertion id");
				logger.debug("Loading the assertion");
				String assertionString = loadSAMLAssertion(decodedValue);
				
				if (assertionString == null)
				{
					logger.error ("Unable to find a valid saml assertion associated with provided ID");
					throw new WebApplicationException(
							Response.status(Status.UNAUTHORIZED)
									.entity("Wrong credentials: invalid assertion ")
									.build());
				}
				else
				{
				
					logger.debug("Assertion loaded");
					Assertion assertion = validator.getAssertionObject(assertionString);				
					return samlAssertionAuthentication(assertion, validator,mapper);
				}
			}
		}
		else 
		{
			logger.error("authentication unsuccessful for user as the authorization header is null  ");
			throw new WebApplicationException(Response
					.status(Status.BAD_REQUEST)
					.entity(" Missing or invalid request contents ").build());
		}
	}


	private String loadSAMLAssertion (String assertionId)
	{
		logger.debug("Loading actual assertion from Service Provider");
		assertionId = assertionId.substring(1);
		String [] keyId = assertionId.split(":");
		String assertion = null;
		
		if (keyId.length==2)
		{
			String url = SAMLUtils.generateAssertionUrl(keyId[0], keyId[1]);
			HttpClient client = new HttpClient();
			HttpMethod getAssertion = new GetMethod(url);
			try 
			{
				int responseCode = client.executeMethod(getAssertion);
				
				if (responseCode >= 200 && responseCode<300)
				{
					assertion = getAssertion.getResponseBodyAsString();
					logger.debug("Assertion = "+assertion);
				}
				else 
				{
					logger.error("Received a response code "+responseCode);
					logger.error("The operation cannot be completed");
				}
				
			} 
			catch (Exception e) 
			{
				logger.error("Unable to get the assertion",e);
			}
			finally 
			{
				getAssertion.releaseConnection();
			}
		}
		else
		{
			logger.error("Invalid information sent");
	
		}
		
		return assertion;
	}
		
	private String samlAssertionAuthentication (Assertion assertion, IAssertionValidator validator,ObjectMapper mapper)
	{
		logger.debug("Validating assertion...");
		logger.debug("Time interval validation");
		boolean result = validator.validateTimeInterval(assertion);
		logger.debug("Time interval validation result = "+result);
		
		if (result)
		{
			try
			{
				result = validator.validateSignature(assertion);
				logger.debug("Signature validation result = "+result);
			} catch (ConfigurationException e)
			{
				logger.error("Unable to validate assertion signature",e);
				throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unable to validate the assertion due to an internal error").build());
	
			}
				
			if (result)
			{
				String username = null;
				Map<String,List<String>> attributes = parseAttributes(assertion);
				List<String> userNameList = attributes.remove(SamlConstants.USERNAME_ATTRIBUTE);
				List<String> roles = attributes.remove(SamlConstants.ROLE_ATTRIBUTE);
				
				if (userNameList != null && userNameList.size()>0)
				{
					username = userNameList.get(0);
				}
				else username = " ";
				
//				if (roles != null && roles.size()>0)
//				{
//					logger.debug("Adding roles");
//					AttributeCache.getAssertionPersistenceManager().getRoles(username).addAll(roles);
//				}
//				else
//				{
//					logger.debug("No roles found: using default external role");
//					AttributeCache.getAssertionPersistenceManager().getRoles(username).add(SamlConstants.EXTERNAL_ROLE_NAME);
//				}
				
				try 
				{
					AuthenticateResponseBean response = new AuthenticateResponseBean();
					response.setUserName(username);
					
					if (roles != null && roles.size()>0) response.getRoles().addAll(roles);
					else response.getRoles().add(SamlConstants.EXTERNAL_ROLE_NAME);
					
					return mapper.writeValueAsString(response);
				}
				catch (Exception e)
				{
					logger.error("Unable to send the correct response");
					throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unable to send the correct response").build());
				}				
				
			}
			else
			{
				logger.debug("Invalid assertion: invalid signature");
				throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity("Invalid assertion: invalid signature").build());
			}
				
			
	
		}
		else
		{
			logger.debug("Invalid time interval");
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity("Invalid assertion: the time interval doesn't match").build());
	
		}
	
	}
	
	private String usernamePasswordAuthentication (String decodedValue, String organization, ObjectMapper mapper)
	{
		IAuthenticationService authService = new AuthenticationServiceImpl();
		
		if(!decodedValue.contains(Constants.PASSWORD_SEPARATOR))
		{
			logger.error("check the if the \"Authorization\" header has a value of the format : Basic Base64encoded{username:password}");
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(" Missing or invalid request contents ").build());
		}

		String userName = decodedValue.split(Constants.PASSWORD_SEPARATOR)[0];
		String password = null;


		logger.debug("UserName = "+userName);
		logger.debug("Organization Name = "+organization);
		
		try
		{
			password = decodedValue.split(Constants.PASSWORD_SEPARATOR)[1];
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error("Password field is empty, please provide a password");
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(" Password field is empty, please provide a password ")
					.build());
		}


		logger.error("user to be authenticated is " + userName);
		boolean isAuthenticated = false;

		try 
		{

			isAuthenticated = authService.isUserAuthenticated(userName,organization, password);
			logger.debug("Adding ldap information...");
			logger.debug("Ldap information added");
		} 
		catch (Exception e) 
		{
			logger.error(" user does not exist ");
			throw new WebApplicationException(Response
					.status(Status.BAD_REQUEST)
					.entity("user "+ userName+"  does not exist")
					.build());
		}

		if (!isAuthenticated) 
		{
			logger.error("authentication unsuccessful for user " + userName);
			throw new WebApplicationException(
					Response.status(Status.UNAUTHORIZED)
					.entity("Wrong credentials, check username and password ")
					.build());
		} else {
			try {
				logger.debug("authentication successful for user "
						+ userName);
				
				AuthenticateResponseBean responseBean = new AuthenticateResponseBean();
				responseBean.setUserName(userName);
				String value = mapper.writeValueAsString(responseBean);
				return value;
			} catch (JsonGenerationException e) 
			{
				logger.error("get data unsuccessful due to json parse error  ");
				throw new JSONParserException(
						"Unable to generate JSON ", e);
			} catch (JsonMappingException e) {
				logger.error("get data unsuccessful due to json parse error  ");
				throw new JSONParserException("Unable to map JSON ", e);
			} catch (IOException e) {
				logger.error("get data unsuccessful due to json parse error  ");
				throw new JSONParserException(
						"IO Exception while parsing JSON ", e);
			}

		}
	}

	private Map<String,List<String>> parseAttributes (Assertion assertion)
	{
		logger.debug("Getting  attributes...");
		Map<String, List<String>> response = new HashMap<String, List<String>> ();

		try
		{
			List<AttributeStatement> attributeStatements = assertion.getAttributeStatements();
			
			for (AttributeStatement attributeStatement : attributeStatements)
			{
				List<Attribute> attributes = attributeStatement.getAttributes();

				
				for (Attribute attribute : attributes)
				{
					String attributeName = attribute.getFriendlyName();
					logger.debug("Attribute Name = "+attributeName);
					
					List<XMLObject> attributeValues = attribute.getAttributeValues();
					List<String> values = new ArrayList<String>();
					
					for (XMLObject value : attributeValues)
					{
						Element domElement = value.getDOM();
						String valueString = domElement.getTextContent();
						logger.debug("Value = "+valueString);
						values.add(valueString);
					}
		
					response.put(attributeName, values);

				}
			}
			
		} catch (Exception e)
		{
			logger.warn("No attributes found", e);
			logger.warn("No tenant name found, using default");
		}
		
		return response;

	}
	

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
		mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
		mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
		AuthenticateResponseBean bean = new AuthenticateResponseBean();
		bean.setUserName("ciro");
		System.out.println(mapper.writeValueAsString(bean));
		
	}
	

}
