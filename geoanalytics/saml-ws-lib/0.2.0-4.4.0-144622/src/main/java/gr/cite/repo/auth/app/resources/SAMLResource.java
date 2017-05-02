package gr.cite.repo.auth.app.resources;

import gr.cite.repo.auth.app.cookies.CookieFactory;
import gr.cite.repo.auth.app.entities.SAMLResourceHelpers;
import gr.cite.repo.auth.app.entities.SamlAuthRequestFactory;
import gr.cite.repo.auth.app.entities.SamlLogoutResponseFactory;
import gr.cite.repo.auth.app.entities.SamlResponseFactory;
import gr.cite.repo.auth.app.utils.LocationResolver;
import gr.cite.repo.auth.app.utils.UserInfo;
import gr.cite.repo.auth.app.utils.UserInfo.USER_ROLE;
import gr.cite.repo.auth.app.views.HomeView;
import gr.cite.repo.auth.app.views.LoginView;
import gr.cite.repo.auth.filters.SessionAttributes;
import gr.cite.repo.auth.saml.messages.SamlAuthRequest;
import gr.cite.repo.auth.saml.messages.SamlIDPMetadata;
import gr.cite.repo.auth.saml.messages.SamlLogoutRequest;
import gr.cite.repo.auth.saml.messages.SamlLogoutResponse;
import gr.cite.repo.auth.saml.messages.SamlResponse;
import gr.cite.repo.auth.saml.messages.SamlSPMetadata;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

@Path("/saml")
@Produces(MediaType.APPLICATION_JSON)
public class SAMLResource {

	private static final Logger logger = LoggerFactory
			.getLogger(SAMLResource.class);

	private final String spHost;
	private final String idpMetadataLocation;
	private final byte[] privateKey;
	private final String certificate;
	private final SamlResponseFactory samlResponseFactory;
	private final SamlAuthRequestFactory samlAuthRequestFactory;
	private final CookieFactory cookieFactory;
	
	private final boolean invalidateLocalSessionOnSamlError;
	
	@SuppressWarnings("unused")
	private final boolean tryRenewSessionOnLogout;
	
	private LocationResolver locationResolver;
	
	private SamlIDPMetadata idpMetadata;
	private String idpHost;
	
	private boolean bulkLogout;
	
	@SuppressWarnings("unused")
	Cache<String, List<HttpSession>> cacheSessions = CacheBuilder.<String, List<HttpSession>>newBuilder()
			.maximumSize(1000)
		    //.expireAfterWrite(10, TimeUnit.MINUTES)
			.build();
	
	public SAMLResource(String spHost, 
			String idpMetadataLocation,
			String privateKeyFileName, 
			String certificateFilename, 
			SamlResponseFactory samlResponseFactory,
			SamlAuthRequestFactory samlAuthRequestFactory,
			LocationResolver locationResolver,
			CookieFactory cookieFactory,
			boolean invalidateLocalSessionOnSamlError,
			boolean tryRenewSessionOnLogout, 
			boolean bulkLogout)
			throws IOException, ConfigurationException, XMLParserException, UnmarshallingException {
		logger.debug("Initializing SAMLResource...");
		this.spHost = spHost;
		this.idpMetadataLocation = idpMetadataLocation;
		this.privateKey = SAMLResourceHelpers
				.readPrivateKey(privateKeyFileName);
		this.certificate = SAMLResourceHelpers
				.readCertificate(certificateFilename);
		this.samlResponseFactory = samlResponseFactory;
		this.samlAuthRequestFactory = samlAuthRequestFactory;
		
		this.cookieFactory = cookieFactory;
		this.invalidateLocalSessionOnSamlError = invalidateLocalSessionOnSamlError;
		this.tryRenewSessionOnLogout = tryRenewSessionOnLogout;
		this.bulkLogout = bulkLogout;
		this.locationResolver = locationResolver;
		
		initFromMeta();
		logger.debug("Initialized SAMLResource");
	}
	
	final void initFromMeta() throws MalformedURLException, IOException, ConfigurationException, XMLParserException, UnmarshallingException{
		logger.info("metadata location : " + this.idpMetadataLocation);
		
		URL metadataUrl = new URL(this.idpMetadataLocation);
		
		String metadataXML = locationResolver.getContents(this.idpMetadataLocation);
		
		this.idpHost = String.format("%s://%s:%d", metadataUrl.getProtocol(), metadataUrl.getHost(), metadataUrl.getPort()); 
		
		this.idpMetadata = new SamlIDPMetadata(metadataXML);
		
		logger.info("idp host :" + idpHost);
		
		logger.info("SLS HTTP-POST     : " + this.idpMetadata.getSLSHttpPostEndpoint());
		logger.info("SLS SOAP          : " + this.idpMetadata.getSLSSoapEndpoint());
		logger.info("SLS HTTP-Redirect : " + this.idpMetadata.getSLSHttpRedirectEndpoint());
		
		logger.info("SSO AuthRequest          : " + this.idpMetadata.getSSOAuthRequestLocation());
		logger.info("SSO HTTP-POST            : " + this.idpMetadata.getSSOHttpPostEndpoint());
		logger.info("SSO HTTP-POST SimpleSign : " + this.idpMetadata.getSSOHttpPostSimpleSignEndpoint());
		logger.info("SSO HTTP-Redirect        : " + this.idpMetadata.getSSOHttpRedirectEndpoint());
		
	}
	
	@GET
	@Path("/simpleLogout")
	public Response simpleLogout(
			@Context HttpServletRequest httpServletRequest,
			@QueryParam("target") String target){
		HttpSession httpSession = httpServletRequest.getSession();
		
		httpSession.invalidate();
		
		if (target != null) {
			String relayStateURL = target;

			logger.info("relaystate was given. redirecting to  : "
					+ relayStateURL);

			return Response.seeOther(URI.create(relayStateURL)).build();
		} else {
			logger.info("relaystate was not given");
			
			return Response.ok("you have logged out!").build();
		}
	}
	
	@POST
	@Path("/logoutConsumer")
	public Response logoutConsumer(
			@Context HttpServletRequest httpServletRequest,
			@FormParam("SAMLResponse") String samlResponseString,
			@FormParam("RelayState") String relayState) throws UnsupportedEncodingException, MarshallingException, ConfigurationException {
		
		HttpSession httpSession = httpServletRequest.getSession();
		
		SamlLogoutResponse samlResponse = null;

		logger.debug("sessionID : + " + httpSession.getId());
		
		try {
			samlResponse = new SamlLogoutResponseFactory().create(privateKey, samlResponseString);
		} catch (Exception e) {
			logger.warn("error while reading samlResponse : "
					+ samlResponseString, e);
			return Response.serverError().build();
		}

		boolean validation = false;
		try {
			validation = samlResponse.validate();
		} catch (Exception e) {
			logger.warn("saml response validation failed : "
					+ samlResponseString, e);
			
			return checkInvalidateSession(httpSession);
		}
		
		logger.info("validation : " + validation);
		
		String nameId = (String) httpSession.getAttribute(SessionAttributes.SAML_NAME_ID_ATTRNAME);
		if (bulkLogout)
			this.invalidateLocalSessions(nameId);
		else
			httpSession.invalidate();
		
		
		
		
		/*//if response is not valid and no we will not try to renew the session then fail
		if (!validation && !tryRenewSessionOnLogout){
			return checkInvalidateSession(httpsession);
		}
		
		// if response is not valid and we will try to renew the session 
		if (!validation && tryRenewSessionOnLogout){
			Boolean isRenew = (Boolean) httpsession.getAttribute("isRenew");
			logger.info("   => is renew : " + (isRenew == Boolean.TRUE));
			
			//to avoid stackoverflow. if validation has failed after 1 successful renew then fail
			if (isRenew == Boolean.TRUE){
				logger.warn("session couldn't be renewed!");
				
				return checkInvalidateSession(httpsession);
			}
			
			logger.info("   => setting renew");
			httpsession.setAttribute("isRenew", Boolean.TRUE);
			
			String redUrl = renewSession(SP_HOST + "/saml/sendLogoutRequest", httpsession.getId());
			
			return Response.seeOther(URI.create(redUrl)).build();
			
			
//			if (!renewSession(SP_HOST + "/saml/sendLogoutRequest", httpsession.getId())){
//				logger.info("   => renew failed");
//				logger.warn("saml response validation failed");
//				
//				if (invalidateLocalSessionOnSamlError)
//					httpsession.invalidate();
//				
//				return Response.serverError().entity("SAML logout error").build();
//			} else {
//				logger.info("   => renew succeded");
//			}
		}
		
		httpsession.invalidate();
		*/
		
		if (relayState != null) {
			String relayStateURL = relayState;

			logger.info("relaystate was given. redirecting to  : "
					+ relayStateURL);

			return Response.seeOther(URI.create(relayStateURL)).build();
		} else {

			logger.info("relaystate was not given");
			
			return Response.ok("you have logged out!").build();
		}
	}

	
	private void saveIdpSession(String nameId, List<String> samlSessionIds, HttpSession httpSession){
		List<HttpSession> sessions = cacheSessions.getIfPresent(nameId);
		
		if (sessions != null){
			sessions.add(httpSession);
		} else {
			cacheSessions.put(nameId, Lists.newArrayList(httpSession));
		}
	}
	
	private void invalidateLocalSessions(String nameId){
		List<HttpSession> sessions = cacheSessions.getIfPresent(nameId);
		
		if (sessions == null)
			return;

		for (HttpSession session : sessions){
			session.invalidate();
		}
		cacheSessions.invalidate(nameId);
	}
	
	@POST
	@Path("/consumer")
	public Response consumer(
			@Context HttpHeaders headers,
			@Context HttpServletRequest httpServletRequest,
			@FormParam("SAMLResponse") String samlResponseString,
			@FormParam("RelayState") String relayState) {

		HttpSession httpSession = httpServletRequest.getSession();
		
		for (Entry<String, Cookie> c : headers.getCookies().entrySet()){
			logger.info(" ~> cookie " + c.getKey() + " : " + c.getValue().getName() + " : " + c.getValue().getValue());
		}
		
		SamlResponse samlResponse = null;

		logger.info(" consume sessionID : + " + httpSession.getId());
		
		try {
			samlResponse = samlResponseFactory.create(privateKey, samlResponseString);
		} catch (Exception e) {
			logger.warn("error while reading samlResponse : "
					+ samlResponseString, e);
			return Response.serverError().build();
		}

		try {
			samlResponse.validate();
		} catch (Exception e) {
			logger.warn("saml response validation failed : "
					+ samlResponseString, e);
			
			return Response.serverError().build();
		}
		
		logger.info("saml response is valid");

		String username = (String)samlResponse.getAttributes().get("cn");
		String email = (String)samlResponse.getAttributes().get("mail");
		
		String samlNameId = samlResponse.getNameId();
		List<String> samlSessionIds = samlResponse.getSessionIds();
		
		logger.info(" -> samlSessionIds : " + samlSessionIds);
		logger.info(" -> samlNameID     : " + samlNameId);
		
		httpSession.setAttribute(SessionAttributes.LOGGED_IN_ATTRNAME,
				Boolean.TRUE);
		httpSession.setAttribute(SessionAttributes.SAML_NAME_ID_ATTRNAME,
				samlNameId);
		httpSession.setAttribute(SessionAttributes.SAML_SESSION_IDS_ATTRNAME,
				samlSessionIds);
		httpSession.setAttribute(SessionAttributes.USERNAME_IN_ATTRNAME,
				username);
		httpSession.setAttribute(SessionAttributes.EMAIL_IN_ATTRNAME, email);
		
		
		///////
		// Shibboleth IDP bug fix
		// 
		if (bulkLogout)
			this.saveIdpSession(samlNameId, samlSessionIds, httpSession);
		///////

		Cookie cookie = cookieFactory.createCookie(httpSession.getId());
		
		if (relayState != null) {
			String relayStateURL = relayState;

			logger.info("relaystate was given. redirecting to  : "
					+ relayStateURL);

			
			return Response.seeOther(URI.create(relayStateURL)).cookie(new NewCookie(cookie)).build();
		} else {

			logger.info("relaystate was not given");

			return Response.ok("Welcome : " + username).cookie(new NewCookie(cookie)).build();
		}
	}
	
	@GET
	@Path("/sendLogoutRequest")
	public Response sendLogoutRequest(
			@Context HttpHeaders headers,
			@Context HttpServletRequest httpServletRequest,
			@QueryParam("target") String target) throws UnsupportedEncodingException, MarshallingException, ConfigurationException {
		
		HttpSession httpSession = httpServletRequest.getSession();
		
		String issuer = spHost + "/saml/metadata";

		// the accSettings object contains settings specific to the users
		// account.
		// At this point, your application must have identified the users origin

		// Generate an AuthRequest and send it to the identity provider
		
		SamlLogoutRequest logoutReq = new SamlLogoutRequest(issuer);

		logger.info("creating request");

		@SuppressWarnings("unchecked")
		List<String> samlSessionIds = (List<String>) httpSession.getAttribute(SessionAttributes.SAML_SESSION_IDS_ATTRNAME);
		//String destinationUrl = IDP_HOST + "/idp/profile/SAML2/Redirect/SLO";
		String destinationUrl = idpMetadata.getSLSHttpRedirectEndpoint();
		
		
		//destinationUrl = SP_HOST + "/saml/logout"; 
		String samlNameID = (String) httpSession.getAttribute(SessionAttributes.SAML_NAME_ID_ATTRNAME);

		/// Shibboleth bug
		//logger.info("sesssion ids was : " + samlSessionIds + " is : " + this.getFirstSamlSessionId(samlNameID));
		//samlSessionIds = this.getFirstSamlSessionId(samlNameID);
		//////
		//samlSessionIds  = Lists.newArrayList();
		
		logger.info(" ~> samlSessionIds : " + samlSessionIds);
		logger.info(" ~> samlNameID     : " + samlNameID);
		
		
		//String nameIdNameQualifier = IDP_HOST + "/idp/shibboleth";
		String nameIdNameQualifier = idpMetadata.getEntityId();
		
		String base64 = logoutReq.getLogoutRequest(samlNameID, samlSessionIds, destinationUrl, nameIdNameQualifier);

		logger.info("base 64 : " + base64);

		String relayStateValue = target;
		String relayState = relayStateValue;

//		String reqString = IDP_HOST + "/idp/profile/SAML2/Redirect/SLO"
//				+ "?SAMLRequest=" + URLEncoder.encode(base64, "UTF-8");
		
		StringBuilder reqString = new StringBuilder(idpMetadata.getSLSHttpRedirectEndpoint());
		reqString
			.append("?SAMLRequest=")
			.append(URLEncoder.encode(base64, "UTF-8"));
		

		if (relayState != null)
			reqString
				.append("&RelayState=")
				.append(URLEncoder.encode(relayState, "UTF-8"));

		logger.info("request : " + reqString);

		return Response.seeOther(URI.create(reqString.toString())).build();
		
	}

	@GET
	@Path("/sendLoginRequest")
	public Response sendRequest(@QueryParam("target") String target)
			throws URISyntaxException, MarshallingException,
			ConfigurationException, UnsupportedEncodingException {
		// the appSettings object contain application specific settings used by
		// the SAML library
		// set the URL of the consume.jsp (or similar) file for this app. The
		// SAML Response will be posted to this URL

		String assertionConsumerServiceUrl = spHost + "/saml/consumer";
		// set the issuer of the authentication request. This would usually be
		// the URL of the issuing web application
		String issuer = spHost + "/saml/metadata";

		// the accSettings object contains settings specific to the users
		// account.
		// At this point, your application must have identified the users origin

		// Generate an AuthRequest and send it to the identity provider
		SamlAuthRequest authReq = samlAuthRequestFactory.create(issuer,
				assertionConsumerServiceUrl);

		logger.info("creating request");

		String base64 = authReq.getAuthReq();

		logger.info("base 64 : " + base64);

		String relayStateValue = target;
		String relayState = relayStateValue;// AuthRequest.encode(relayStateValue);

//		String reqString = IDP_HOST + "/idp/profile/SAML2/Redirect/SSO"
//				+ "?SAMLRequest=" + URLEncoder.encode(base64, "UTF-8");

		StringBuilder reqString = new StringBuilder(idpMetadata.getSSOHttpRedirectEndpoint());
		
		reqString
			.append("?SAMLRequest=")
			.append(URLEncoder.encode(base64, "UTF-8"));
			
		
		
		if (relayState != null)
			reqString
				.append("&RelayState=")
				.append(URLEncoder.encode(relayState, "UTF-8"));

		logger.info("request : " + reqString);

		return Response.seeOther(URI.create(reqString.toString())).build();
		// return Response.ok(reqString).build();
	}

	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	public LoginView login() throws Exception {
		return new LoginView(idpHost, spHost, spHost + "/saml/metadata");
	}
	
	@GET
	@Path("/home")
	@Produces(MediaType.TEXT_HTML)
	public HomeView home(@Context HttpServletRequest httpServletRequest) throws Exception {
		HttpSession httpSession = httpServletRequest.getSession();
		Boolean logged = (Boolean) httpSession.getAttribute(SessionAttributes.LOGGED_IN_ATTRNAME);
		String name = null;
		if (logged != Boolean.TRUE){
			name = "anonymous";
		} else name = (String) httpSession.getAttribute(SessionAttributes.USERNAME_IN_ATTRNAME);
		
		return new HomeView(spHost, name);
	}
	
	@GET
	@Path("/metadata")
	@Produces(MediaType.APPLICATION_XML)
	public Response metadata() throws Exception {
		String entityID = spHost + "/saml/metadata";
		
		SamlSPMetadata meta = new SamlSPMetadata(entityID, this.certificate, spHost);
		String xml = meta.getMetadata();

		return Response.ok(xml).build();
	}

	@GET
	@Path("infoP")
	@Produces("application/x-javascript")
	public JSONPObject infoP(
			@Context HttpServletRequest httpServletRequest,
			@QueryParam("callback") String callback) {
		
		return new JSONPObject(callback, info(httpServletRequest));
	}

	@GET
	@Path("info")
	public UserInfo info(@Context HttpServletRequest httpServletRequest) {
		HttpSession httpSession = httpServletRequest.getSession();
		
		String username = (String) httpSession
				.getAttribute(SessionAttributes.USERNAME_IN_ATTRNAME);
		String email = (String) httpSession
				.getAttribute(SessionAttributes.EMAIL_IN_ATTRNAME);
		USER_ROLE role;
		if (httpSession.getAttribute(SessionAttributes.USER_ROLE) != null) {
			role = USER_ROLE.valueOf((int) httpSession
					.getAttribute(SessionAttributes.USER_ROLE));
		} else {
			role = USER_ROLE.VISITOR;
		}

		UserInfo userInfo = new UserInfo();
		userInfo.setRole(role);
		userInfo.setMail(email);
		userInfo.setUsername(username);

		return userInfo;
	}
	
	
	
	
	
	
	
	/*@GET
	void query(){
		String assertionConsumerServiceUrl = SP_HOST + "/saml/consumer";
		// set the issuer of the authentication request. This would usually be
		// the URL of the issuing web application
		String issuer = SP_HOST + "/saml/metadata";

		// the accSettings object contains settings specific to the users
		// account.
		// At this point, your application must have identified the users origin

		// Generate an AuthRequest and send it to the identity provider
		SamlQueryRequest req = new SamlQueryRequest(issuer);
		
		
		
	}*/
	
	private Response checkInvalidateSession(HttpSession httpsession){
		if (invalidateLocalSessionOnSamlError){
			httpsession.invalidate();
			return Response.ok().entity("local logout ok. saml logout error").build();
		} else {
			return Response.serverError().entity("SAML logout error").build();
		}
	}
	
	
//	private String renewSession(String backlink, String sessionId){
//		String url = spHost + "/saml/sendLoginRequest?target=" + backlink;
//		return url;
//		
//		Client client = Client.create();
//		client.setFollowRedirects(true);
//		
//		 ClientResponse response = client
//				 	.resource(url)
//				 	.cookie(cookieFactory.createCookie(sessionId))
//				 	.header("jsessionid", sessionId)
//				 	.get(ClientResponse.class);
		 
//		 HttpMethod method = new GetMethod(url);
//		 method.setRequestHeader("Cookie", "jsessionid=" + sessionId);	 
//		 method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
//		 
//		 HttpClient client = new HttpClient();
//		 try {
//			int statusCode = client.executeMethod(method);
//			byte[] responseBody = method.getResponseBody();
//		      // Deal with the response.
//		      // Use caution: ensure correct character encoding and is not binary data
//
//			return statusCode == 200;
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
		 
		 
//		 String answ = response.getEntity(String.class);
//		 return (response.getStatus() == Status.OK.getStatusCode());
//	}
	
	
	
}
