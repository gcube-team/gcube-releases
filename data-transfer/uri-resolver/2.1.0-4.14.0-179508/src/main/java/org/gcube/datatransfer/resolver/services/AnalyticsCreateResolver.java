/**
 *
 */
package org.gcube.datatransfer.resolver.services;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.data.analysis.dminvocation.ActionType;
import org.gcube.data.analysis.dminvocation.DataMinerInvocationManager;
import org.gcube.data.analysis.dminvocation.model.DataMinerInvocation;
import org.gcube.datatransfer.resolver.requesthandler.RequestHandler;
import org.gcube.datatransfer.resolver.services.error.ExceptionManager;
import org.gcube.datatransfer.resolver.services.exceptions.InternalServerException;
import org.gcube.datatransfer.resolver.util.ScopeUtil;
import org.gcube.datatransfer.resolver.util.Util;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.storagehub.ApplicationMode;
import org.gcube.storagehub.StorageHubManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * The Class AnalyticsCreateResolver.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 12, 2018
 */
@Path("analytics")
public class AnalyticsCreateResolver {

	/**
	 *
	 */
	protected static final String GCUBE_TOKEN = "gcube-token";
	/**
	 *
	 */
	private static final String ANALYTICS_RESOLVER_SERVICE_ENDPOINT_NAME = "Analytics-Resolver";
	private static final String DATAMINER_INVOCATION_MODEL = "dim";
	private static Logger logger = LoggerFactory.getLogger(AnalyticsCreateResolver.class);
	private static String helpURI = "https://gcube.wiki.gcube-system.org/gcube/URI_Resolver#Analytics_Resolver";


	/**
	 * Creates the analytics url.
	 *
	 * @param req the req
	 * @param body the body
	 * @return the response
	 * @throws WebApplicationException the web application exception
	 */
	@POST
	@Path("/create")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createAnalyticsURL(@Context HttpServletRequest req, String body) throws WebApplicationException{
		logger.info(this.getClass().getSimpleName()+" POST starts...");

		try{
			logger.info("body is: "+body);

			DataMinerInvocation jsonRequest = null;
			try {

				jsonRequest = DataMinerInvocationManager.getInstance().unmarshalingJSON(IOUtils.toInputStream(body), true);
			}
			catch (IOException | JAXBException | SAXException e1) {
				logger.error("The body is not a valid DataMinerInvocation JSON request",e1);
				throw ExceptionManager.badRequestException(req, "Bad 'dataminer-invocation' JSON request: \n"+e1.getCause().getMessage(), this.getClass(), helpURI);
			}

			logger.debug("The body contains the request: "+jsonRequest.toString());

			String contextToken = SecurityTokenProvider.instance.get();
			String scope = ScopeProvider.instance.get();
	//		logger.info("SecurityTokenProvider contextToken: "+contextToken);
			logger.info("ScopeProvider has scope: "+scope);

			String appToken = req.getServletContext().getInitParameter(RequestHandler.ROOT_APP_TOKEN);

			if(contextToken.compareTo(appToken)==0){
				logger.error("Token not passed, SecurityTokenProvider contains the root app token: "+appToken.substring(0,10)+"...");
				throw ExceptionManager.unauthorizedException(req, "You are not authorized. You must pass a token of VRE", this.getClass(), helpURI);
			}

			String operatorID = jsonRequest.getOperatorId();

			if(scope==null || scope.isEmpty()){
				logger.error("The parameter 'scope' not found or empty in the JSON object");
				throw ExceptionManager.badRequestException(req, "Mandatory body parameter 'scope' not found or empty in the JSON object", this.getClass(), helpURI);
			}


			if(operatorID==null || operatorID.isEmpty()){
				logger.error("The parameter 'operatorId' not found or empty in the JSON object");
				throw ExceptionManager.badRequestException(req, "Mandatory body parameter 'operatorId' not found or empty in the JSON object", this.getClass(), helpURI);
			}

			ScopeBean scopeBean = new ScopeBean(scope);
			String publicLinkToDMInvFile = "";

			if(scopeBean.is(Type.VRE)){
				String vreName = scopeBean.name();

				String analyticsGetResolverURL = String.format("%s/%s",  Util.getServerURL(req), "analytics/get");
				//Creating DM invocation file
				if(jsonRequest.getActionType()==null)
					jsonRequest.setActionType(ActionType.RUN);

				File tempInvocationFile = null;
				try {

					String xmlRequest = DataMinerInvocationManager.getInstance().marshalingXML(jsonRequest, true, true);
					String uniqueName = createDMInvocationFileName(jsonRequest.getOperatorId());
					tempInvocationFile = createTempFile(uniqueName, ".xml", xmlRequest.getBytes());
					logger.info("Created StorageHubClient Instance, uploading file: "+tempInvocationFile.getName());

					AuthorizationEntry entry = authorizationService().get(contextToken);
					//retrieve the info of the token owner
					ClientInfo clientInfo = entry.getClientInfo();
					String owner = clientInfo.getId(); //IS THIS THE USERNAME?

					String infra = ScopeUtil.getInfrastructureNameFromScope(ScopeProvider.instance.get());
					String theAppToken = readApplicationTokenFromSE(req, infra);

					logger.info("By using infra scope: "+infra +" and the Application Token: "+theAppToken.substring(0,theAppToken.length()/2)+"... of "+ANALYTICS_RESOLVER_SERVICE_ENDPOINT_NAME+" to instance the "+StorageHubManagement.class.getSimpleName());
					ScopeProvider.instance.set(infra);
					ApplicationMode applicationMode = new ApplicationMode(theAppToken);
					applicationMode.start();

					StorageHubManagement storageHubManagement = new StorageHubManagement();
					Metadata metadata = new Metadata();
					Map<String, String> theMap = new HashMap<String, String>();
					theMap.put("owner", owner);
					URL thePublicLink = null;
					try{

						logger.info("Saving dataminer-invocation file for the user: "+owner);
						thePublicLink = storageHubManagement.persistFile(new FileInputStream(tempInvocationFile), tempInvocationFile.getName(), "application/xml", metadata);
						logger.info("Saved dataminer-invocation file at: "+thePublicLink);
					}catch(Exception e){
						logger.error("Error when storing your 'dataminer-invocation':", e);
						throw ExceptionManager.internalErrorException(req, "Error when storing your 'dataminer-invocation' request with "+jsonRequest+". \nPlease contact the support", this.getClass(), helpURI);
					}

//					FileContainer fileContainer = shc.getWSRoot().uploadFile(new FileInputStream(tempInvocationFile), tempInvocationFile.getName(), "DataMinerInvocation Request created by "+this.getClass().getSimpleName());
//					logger.info("UPLOADED FILE at: "+fileContainer.getPublicLink());
//					URL thePublicLink = fileContainer.getPublicLink();
					publicLinkToDMInvFile = thePublicLink!=null?thePublicLink.toString():null;
				}
				catch (Exception e) {

					if(e instanceof InternalServerException){
						//error during storing the file via StorageHubManagent
						throw e;
					}
					logger.error("Error on creating 'dataminer-invocation:", e);
					throw ExceptionManager.badRequestException(req, "Error on creating your 'dataminer-invocation' request with "+jsonRequest+". \nPlease contact the support", this.getClass(), helpURI);
				}finally{
					//No needed to reset the scope, it is provided by TokenSetter
					try{
					//DELETING THE TEMP FILE
					if(tempInvocationFile!=null && tempInvocationFile.exists())
						tempInvocationFile.delete();
					}catch(Exception e){
						//silent
					}
				}

				if(publicLinkToDMInvFile==null){
					logger.error("Error on creating the public link to file");
					throw ExceptionManager.badRequestException(req, "Error on getting link to your 'dataminer-invocation' request. Plese contact the support "+jsonRequest, this.getClass(), helpURI);
				}

				String dataMinerURL = String.format("%s/%s?%s=%s", analyticsGetResolverURL, vreName, DATAMINER_INVOCATION_MODEL, publicLinkToDMInvFile);
				logger.info("Returning Analytics URL: "+dataMinerURL);
				return Response.ok(dataMinerURL).header("Location", dataMinerURL).build();

			}else{
				logger.error("The input scope "+scope+" is not a VRE");
				throw ExceptionManager.badRequestException(req, "Working in the "+scope+" scope that is not a VRE. Use a token of VRE", this.getClass(), helpURI);
			}

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error occurred on creating the Analytics for the request "+body+". Please, contact the support!";
				if(e.getCause()!=null)
					error+="\n\nCaused: "+e.getCause().getMessage();
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), helpURI);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}
	}


	/**
	 * Reads the Application Token from Service Endpoint {@link AnalyticsCreateResolver#ANALYTICS_RESOLVER_SERVICE_ENDPOINT_NAME}
	 *
	 * @param req the req
	 * @param scope the scope
	 * @return the string
	 */
	private static String readApplicationTokenFromSE(HttpServletRequest req, String scope){

		String callerScope = null;
		String gCubeAppToken = null;
		try{
			callerScope = ScopeProvider.instance.get();
			ScopeProvider.instance.set(scope);
			logger.info("Searching SE "+ANALYTICS_RESOLVER_SERVICE_ENDPOINT_NAME+" configurations in the scope: "+ScopeProvider.instance.get());

			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/text() eq '"+ ANALYTICS_RESOLVER_SERVICE_ENDPOINT_NAME +"'");
			query.addCondition("$resource/Profile/Category/text() eq 'Service'");

			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			List<ServiceEndpoint> toReturn = client.submit(query);

			logger.info("The query returned "+toReturn.size()+ " ServiceEndpoint/s");

			if(toReturn.size()==0){
				String errorMessage = "No "+ANALYTICS_RESOLVER_SERVICE_ENDPOINT_NAME+" registered in the scope: "+ScopeProvider.instance.get();
				logger.error(errorMessage);
				throw ExceptionManager.internalErrorException(req, errorMessage, AnalyticsCreateResolver.class, helpURI);
			}


			ServiceEndpoint se = toReturn.get(0);
			Collection<AccessPoint> theAccessPoints = se.profile().accessPoints().asCollection();
			for (AccessPoint accessPoint : theAccessPoints) {
				Collection<Property> properties = accessPoint.properties().asCollection();
				for (Property property : properties) {
					if(property.name().equalsIgnoreCase(GCUBE_TOKEN)){
						logger.info("gcube-token as property was found, returning it");
						gCubeAppToken = property.value();
						break;
					}
				}

				if(gCubeAppToken!=null)
					break;
			}

			if(gCubeAppToken!=null){
				String decryptedPassword = StringEncrypter.getEncrypter().decrypt(gCubeAppToken);
				logger.info("Returning decrypted Application Token registered into "+ANALYTICS_RESOLVER_SERVICE_ENDPOINT_NAME +" SE: "+decryptedPassword.substring(0,decryptedPassword.length()/2)+"....");
				return decryptedPassword;
			}


			String errorMessage = "No "+GCUBE_TOKEN+" as Property saved in the "+ANALYTICS_RESOLVER_SERVICE_ENDPOINT_NAME+" SE registered in the scope: "+ScopeProvider.instance.get();
			logger.error(errorMessage);
			throw ExceptionManager.internalErrorException(req, errorMessage, AnalyticsCreateResolver.class, helpURI);


		}catch(Exception e){
			String errorMessage = "Error occurred on reading the "+ANALYTICS_RESOLVER_SERVICE_ENDPOINT_NAME+" SE registered in the scope: "+ScopeProvider.instance.get();
			logger.error(errorMessage, e);
			throw ExceptionManager.internalErrorException(req, errorMessage, AnalyticsCreateResolver.class, helpURI);

		}finally{
			if(callerScope!=null){
				logger.info("Setting to the callerScope scope: "+callerScope);
				ScopeProvider.instance.set(callerScope);
			}else{
				logger.info("Reset scope");
				ScopeProvider.instance.reset();
			}
		}

	}


    /**
     * Creates the temp file.
     *
     * @param fileName the file name
     * @param extension the extension
     * @param data the data
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static File createTempFile(String fileName, String extension, byte[] data) throws IOException {
        // Since Java 1.7 Files and Path API simplify operations on files
    	java.nio.file.Path path = Files.createTempFile(fileName, extension);
        File file = path.toFile();
        // writing sample data
        Files.write(path, data);
        logger.info("Created the Temp File: "+file.getAbsolutePath());
        return file;
    }


    /**
     * Creates the dm invocation file name.
     *
     * @param operatorId the operator id
     * @return the string
     */
    private static String createDMInvocationFileName(String operatorId){
    	String fileName = "dim";
    	int index = operatorId.lastIndexOf(".");
    	if(index>0 && index<operatorId.length()){
    		fileName+="-"+operatorId.substring(index+1,operatorId.length());
    	}
		fileName+="-"+System.currentTimeMillis();
		return fileName;
    }


//    public static void main(String[] args) {
//
//    	System.out.println(readApplicationTokenFromSE(null, "/gcube"));
//
//	}

}
