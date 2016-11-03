package org.gcube.data.access.ckanconnector;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.python.core.PyLong;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpStatus;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.model.CkanUser;

@Path("/")
@Slf4j
public class ConnectorManager {

	@Context ServletContext context;

	RandomString randomString = new RandomString(12);


	@Path("disconnect")
	@GET
	public Response disconnect(@Context HttpServletRequest req) {
		log.info("disconnect called ");
		String hostname = context.getInitParameter("hostname");
		try{
				boolean found = false;
				for (Cookie cookie : req.getCookies())
					if (cookie.getName().equals("auth_tkt")){ 
						found=true;
						break;
					}
				if (found)
					return Response.ok(context.getClassLoader().getResourceAsStream("logout.html"))
							.header(
									"Set-Cookie",
									String.format("auth_tkt=deleted;Domain=%s;Path=/;Expires=Thu, 01-Jan-1970 00:00:01 GMT",hostname)
									)
							.header("Set-Cookie",
									String.format("ckan_hide_header=deleted;Domain=%s;Path=/;Expires=Thu, 01-Jan-1970 00:00:01 GMT",hostname)
									).build();
				else return Response.ok(context.getClassLoader().getResourceAsStream("inactivesession.html")).build();
			
		}catch(Exception e){
			log.error("error disconnecting ",e);
			return Response.serverError().build();
		}
	}


	@Path("connect{pathInfo:(/[^?$]+)?}")
	@GET
	public Response connect(@PathParam(value = "pathInfo") String path, @Context HttpServletRequest req, @QueryParam(value="listOfVres") String vres ) {
		try{
			if (AuthorizationProvider.instance.get()==null || AuthorizationProvider.instance.get().getClient() == null ) return Response.status(Status.UNAUTHORIZED).build();
			log.info("passed path is {}",path);
			String ckanKey = context.getInitParameter("ckanKey");
			String originalUserName = AuthorizationProvider.instance.get().getClient().getId();
			String changedUserName =  originalUserName.replace(".", "_");
			int internalPort = Integer.parseInt(context.getInitParameter("internalPort"));		
			String localhostName = "http://127.0.0.1:"+internalPort;
			CkanClient ckanClient = new CkanClient(localhostName, ckanKey);
			CkanUser user = null;
			try{
				user = ckanClient.getUser(changedUserName);				
			}catch(Exception e){
				log.warn("user {} doesn't exist, the system will create it",originalUserName, e);
			}	
			if (user==null)
				user = ckanClient.createUser(new CkanUser(changedUserName, originalUserName+"@gcube.ckan.org" , randomString.nextString() ));

			addUserToVres(vres, changedUserName, ckanClient, ckanKey, localhostName);

			log.info("logging {} in scope {}",originalUserName, ScopeProvider.instance.get());
			return createResponse(changedUserName, path, req.getQueryString());	
		}catch(Exception e){
			log.info("error trying to connect to CKAN",e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	private void addUserToVres(String vres, String changedUserName,
			CkanClient ckanClient, String ckanKey, String localhostName) {
		if (vres!=null && !vres.isEmpty())
			for (String vre: vres.split(",")){
				try{
					CkanOrganization org = ckanClient.getOrganization(vre);
					boolean userAlreadyRegistered = false;
					for (CkanUser vreUser : org.getUsers()){
						log.info("found user {} in organization {}",vreUser.getName(), org.getTitle());
						if (vreUser.getName().equals(changedUserName)){
							userAlreadyRegistered= true;
							break;
						}

					}
					if (userAlreadyRegistered){
						log.warn("user {} already registered to vre {} ",changedUserName, vre);
					} else{
						boolean added = addUserToOrganization(changedUserName, vre, ckanKey, localhostName);
						log.info("{} {} added to vre {}",changedUserName, added?"":"not", vre);
					}
				}catch(Exception e){
					log.warn("organization {} not found ",vre);
				}
			}

	}

	private Response createResponse(String userName, String path, String query){
		try{
			String secret = context.getInitParameter("secret");
			String hostIp = context.getInitParameter("hostIp");
			String hostname = context.getInitParameter("hostname");
			String fixedData = "userid_type:unicode";
			PythonInterpreter interpreter = new PythonInterpreter();
			interpreter.execfile(new FileInputStream(new File(this.getClass().getClassLoader().getResource("digest.py").getFile())));
			PyObject someFunc = interpreter.get("calculate_digest");
			long currentMillis = System.currentTimeMillis()/1000;
			PyObject ret = someFunc.__call__(new PyObject[]{new PyString(hostIp), new PyLong(currentMillis), new PyString(secret), 
					new PyString(userName), new PyString(""), new PyString(fixedData)} );
			String realResult = (String) ret.__tojava__(String.class);

			String timestamp16 = Long.toString(currentMillis, 16);

			String cookieValue = realResult+timestamp16+userName+"!"+fixedData;
			NewCookie cookie = new NewCookie("auth_tkt", 
					cookieValue,
					"/", hostname, "", -1, false, true );


			NewCookie cookieHideHeader = new NewCookie("ckan_hide_header", "true",
					"/", hostname, "", -1, false, true );


			String newQueryString = query.replaceFirst("&?gcube-token=[^&$]*&?", "").replace("&?listOfVres=[^&$]*&?", "");
			String baseUrl = "https://"+hostname;
			if (path!=null && !path.isEmpty())
				baseUrl+=path.startsWith("/")?path:"/"+path;  

			if (newQueryString!=null && !newQueryString.isEmpty())
				baseUrl+="?"+newQueryString;

			log.info("redirecting to "+baseUrl);

			return Response.seeOther(new URI(baseUrl))
					.cookie(cookie).cookie(cookieHideHeader).build();
		}catch(Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
	}


	private boolean addUserToOrganization(String ckanUsername, String organizationName, String ckanKey, String hostAddress ){
		// we need to use the apis to make it
		String path = "/api/3/action/organization_member_create";

		// Request parameters to be replaced
		String parameter = "{"
				+ "\"id\":\"ORGANIZATION_ID_NAME\","
				+ "\"username\":\"USERNAME_ID_NAME\","
				+ "\"role\":\"ROLE\""
				+ "}";

		// replace those values
		parameter = parameter.replace("ORGANIZATION_ID_NAME", organizationName.toLowerCase());
		parameter = parameter.replace("USERNAME_ID_NAME", ckanUsername);
		parameter = parameter.replace("ROLE", "member");

		log.debug("API request for organization membership is going to be " + parameter);

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			HttpPost request = new HttpPost(hostAddress + path);
			request.addHeader("Authorization", ckanKey); // sys token
			StringEntity params = new StringEntity(parameter);
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			log.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

			return (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);

		}catch (Exception ex) {
			log.error("Error while trying to change the role for this user ", ex);
			return false;
		}
	}

}
