package org.gcube.data.access.sharelatex.connector.resources;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.access.sharelatex.connector.User;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Path("")
@Slf4j
public class Resource {

	private static final String LOGIN_URL_PARAM = "internalLoginUrl";
	private static final String HOST_NAME_PARAM = "hostName";
	private static final String MONGO_DATABASE_PARAM = "mongoDatabaseName";


	private static final String DOCS_COLLECTION_NAME = "docs";
	private static final String PROJECTS_COLLECTION_NAME = "projects";
	private static final String USER_COLLECTION_NAME = "users";
	private static final String GCUBE_LOGIN_FIELD = "gcube_login";
	private static final String EMAIL_FIELD = "email";
	private static final String SIGNUP_DATE_FIELD = "signUpDate";
	private static final String LAST_LOGGEDIN__FIELD = "lastLoggedIn";

	private static final String LAST_NAME__FIELD = "last_name";
	private static final String FIRST_NAME_FIELD = "first_name";

	private static final String PWD_FIELD = "password";
	private static final String CSRF_TOKEN_FIELD = "_csrf";

	private static final String SHARELATEX_COOKIE_NAME = "sharelatex.sid";
	private static final String USER_ID_COOKIE = "_ga";

	private static String FIRST_PROJECT_NAME ="First Example";

	private static final Map<String, ReentrantLock> justRegistered = Collections.synchronizedMap(new WeakHashMap<String, ReentrantLock>());

	@Context ServletContext context;

	@Path("connect")
	@GET
	public Response connect(@Context HttpServletRequest request, @Context HttpServletRequest response){
		ReentrantLock lock = null;
		try{
			String user =  AuthorizationProvider.instance.get().getClient().getId();
			log.info("request for connection from {}",user);
			synchronized (justRegistered) {
				if (justRegistered.containsKey(user))
					lock = justRegistered.get(user);
				else {
					lock = new ReentrantLock(true);
					justRegistered.put(user, lock);
				}
			}
			lock.lock();
			String userRealLogin = checkUser(user);
			if (userRealLogin==null){
				log.info("trying to register user {}",AuthorizationProvider.instance.get().getClient().getId());
				userRealLogin = registerUser();
			} else log.info("user {} already exists",AuthorizationProvider.instance.get().getClient().getId());
			return _connect(userRealLogin, request.isSecure());
		}catch(Throwable e){
			log.error("error trying to connect",e);
			return Response.serverError().build();
		} finally{
			lock.unlock();
		}

	}

	@Path("disconnect")
	@GET
	public Response disconnect(@Context HttpServletRequest request,@Context HttpServletRequest response){
		try{
			log.info("request for disconnection ");			
			Cookie[] cookies = request.getCookies();
			NewCookie[] newCookies = new NewCookie[2];
			int index=0;
			if (cookies!=null){
				for (Cookie cookie: cookies){
					String name = cookie.getName();

					if (name.trim().equals(SHARELATEX_COOKIE_NAME) || name.trim().equals(USER_ID_COOKIE)){
						newCookies[index++] = new NewCookie(name, null, cookie.getPath()  , cookie.getDomain(), cookie.getVersion(), 
								cookie.getComment(), 0, cookie.isHttpOnly());
						log.debug("SHARELATEX cookie found");
					} 

				}

				return Response.ok(context.getClassLoader().getResourceAsStream("logout.html"))
						.cookie(newCookies).build();
			} else
				return Response.ok(context.getClassLoader().getResourceAsStream("logout.html")).build();

		}catch(Throwable e){
			log.error("error disconnecting",e);
			return Response.serverError().build();
		}

	}


	private Response _connect(String userRealLogin, boolean isSecureRequest){
		String hostName = context.getInitParameter(HOST_NAME_PARAM);
		String loginUrl = context.getInitParameter(LOGIN_URL_PARAM);
		String setCookies ="";
		HttpClient httpClient = new HttpClient();
		String csrfToken;
		try{
			csrfToken = retrieveCsrfToken(httpClient, loginUrl);
			setCookies = retrieveSharelatexCookie(httpClient, loginUrl, csrfToken, userRealLogin);
		}catch(Exception e){
			log.error("cannot retrieve csrf token",e);
			return Response.serverError().build();
		}


		String redirectUrl = "http"+(isSecureRequest?"s":"")+"://"+hostName+"/project";

		List<NewCookie> newCookies = elaborateCookies(setCookies);

		try{	
			return Response.seeOther(new URI(redirectUrl)).cookie(newCookies.toArray(new NewCookie[newCookies.size()])).build();
		}catch(Exception e){
			log.error("error trying to login",e);
			return Response.serverError().build();
		}
	}

	/**
	 * retrieve and set the sharelatex.sid cookie used by sharelatex to authenticate the user
	 * 
	 * @param setCookies
	 * @return
	 */
	private List<NewCookie> elaborateCookies(String setCookies) {
		List<NewCookie> newCookies = new ArrayList<NewCookie>();
		String[] cookies = setCookies.split(",",2);
		for (String cookie: cookies){
			String[] singleValues = cookie.split(";");
			String[] nameAndValue = singleValues[0].split("=");
			if (nameAndValue[0].trim().equals(SHARELATEX_COOKIE_NAME)){
				String pathValue = singleValues[1].split("=")[1].trim();
				newCookies.add(new NewCookie(nameAndValue[0].trim(), nameAndValue[1].trim(),pathValue , null, null, -1, false));
			} 
		}
		return newCookies;

	}

	private String retrieveSharelatexCookie(HttpClient httpClient,
			String loginUrl, String csrfToken, String userRealLogin ) throws Exception{

		String commonPwd = getGeneratadePassword();

		PostMethod postMethod = new PostMethod(loginUrl);

		postMethod.setParameter(EMAIL_FIELD, userRealLogin);
		postMethod.setParameter(PWD_FIELD, commonPwd);
		postMethod.setParameter(CSRF_TOKEN_FIELD, csrfToken);

		log.info("try to login with user {} and password {}", userRealLogin, commonPwd);

		try {
			httpClient.executeMethod(postMethod);
		} catch (Exception e) {
			log.error("error trying to login",e);
			throw new Exception("error trying to login",e);
		} 

		log.info("returned status is {}",postMethod.getStatusText());
		if (postMethod.getStatusCode() != HttpStatus.SC_OK) {
			log.error("error on login response: error code is {} ",postMethod.getStatusCode());
			throw new Exception("error on login response: error code is "+postMethod.getStatusCode());
		}

		log.info("status text on login {} ",postMethod.getStatusText());
		try {
			log.info("response on login {} ",postMethod.getResponseBodyAsString());
		} catch (IOException e1) {
			log.warn("cannot log response body",e1);
		}
		return postMethod.getResponseHeader("Set-Cookie").getValue();
	}

	private String retrieveCsrfToken(HttpClient httpClient, String loginUrl) throws Exception{
		GetMethod getMethod = new GetMethod(loginUrl);
		httpClient.executeMethod(getMethod);
		if (getMethod.getStatusCode()==200){
			String response = getMethod.getResponseBodyAsString();
			Pattern pattern = Pattern.compile("input name=\""+CSRF_TOKEN_FIELD+"\" type=\"hidden\" value=\"([^\"]*)\"");
			Matcher matcher = pattern.matcher(response);
			if (matcher.find())
				return matcher.group(1);
			else throw new Exception("crsf not fount on the requeste login page");
		} else throw new Exception("the page "+loginUrl+" in not responding");


	}

	private String checkUser(String login){
		String mongoDB = context.getInitParameter(MONGO_DATABASE_PARAM);

		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(GCUBE_LOGIN_FIELD,login);
		log.info("checking for login {} in field {}",login, GCUBE_LOGIN_FIELD);
		MongoClient client = connectToMongo();
		try{
			MongoDatabase db = client.getDatabase(mongoDB);
			MongoCollection<Document> collection = db.getCollection(USER_COLLECTION_NAME);
			Iterator<Document> cursor = collection.find(whereQuery).iterator();
			if (!cursor.hasNext()){
				log.info("login {} not found",login);
				return null;
			} else{
				String emailField = (String)cursor.next().get(EMAIL_FIELD);
				log.info("login {} found with email field {}",login,emailField);
				return emailField;
			}
		}finally{
			client.close();
		}

	}

	private String registerUser() throws Exception{

		User user = retrieveUserInfo();

		log.trace("trying to register user {}",user.toString());

		String login =  AuthorizationProvider.instance.get().getClient().getId();
		String commonPwd = getGeneratadePassword();	
		String salt = BCrypt.gensalt(12);
		String hashedPwd = BCrypt.hashpw(commonPwd, salt);
		String mongoDB = context.getInitParameter(MONGO_DATABASE_PARAM);
		ObjectId userId = new ObjectId();

		MongoClient client = connectToMongo();
		try{
			MongoDatabase db = client.getDatabase(mongoDB);
			MongoCollection<Document> collection = db.getCollection(USER_COLLECTION_NAME);

			String json ="{ \"betaProgram\" : false, \"subscription\" : { \"hadFreeTrial\" : false }, "+ 
					"\"refered_user_count\" : 0, \"refered_users\" : [ ], \"referal_id\" : \"23bbb54a\", "+
					"\"features\" : { \"references\" : true, \"templates\" : true, \"compileGroup\" : \"standard\", "+
					"\"compileTimeout\" : 180, \"github\" : false, \"dropbox\" : true, \"versioning\" : true, \"collaborators\" : -1 }, "+
					"\"ace\" : { \"syntaxValidation\" : true, \"pdfViewer\" : \"pdfjs\", \"spellCheckLanguage\" : \"en\", \"autoComplete\" : true, "+
					"\"fontSize\" : 12, \"theme\" : \"textmate\", \"mode\" : \"none\" }, \"holdingAccount\" : false, \"loginCount\" : 0, "+
					"\"signUpDate\" : null, \"confirmed\" : false, \"isAdmin\" : false, "+
					"\"institution\" : \"\", \"role\" : \"\", \"last_name\" : \"\", \"first_name\" : \"\", \"email\" : \"\", \"__v\" : 1,"+ 
					"\"hashedPassword\" : null,"+ 
					"\"lastLoggedIn\" : null })";

			Document doc = Document.parse(json);

			doc.put("hashedPassword", hashedPwd);
			doc.put("_id",  userId);

			Calendar now = Calendar.getInstance();
			SimpleDateFormat sdfLogin = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

			String loginDate = sdfLogin.format(now.getTime());

			doc.put(SIGNUP_DATE_FIELD, "ISODate(\""+loginDate+"\")");
			doc.put(LAST_LOGGEDIN__FIELD, "ISODate(\""+loginDate+"\")");
			doc.put(EMAIL_FIELD, user.getEmail());
			doc.put(FIRST_NAME_FIELD, user.getFirstName() );
			doc.put(LAST_NAME__FIELD, user.getLastName());

			doc.put(GCUBE_LOGIN_FIELD, login);

			log.debug("inserting doc: {}",doc.toJson());

			collection.insertOne(doc);

			try{
				ObjectId projectId = new ObjectId();
				ObjectId docId = new ObjectId();
				
				log.debug("objectID is {}",userId);

				//insert project
				String projectJson = "{\"deletedDocs\" : [ ], \"description\" : \"\", \"deletedByExternalDataSource\" : false, "
						+ "\"spellCheckLanguage\" : \"en\", \"compiler\" : \"pdflatex\", \"publicAccesLevel\" : \"private\", "
						+ "\"rootFolder\" : [ { \"folders\" : [ ], \"fileRefs\" : [ ], "
						+ "\"docs\" : [ { \"_id\" : ObjectId(\""+docId+"\"), \"name\" : \"main.tex\" } ], "
						+ "\"name\" : \"rootFolder\" } ], \"readOnly_refs\" : [ ], \"collaberator_refs\" : [ ], \"active\" : true, "
						+ "\"lastUpdated\" : null , \"name\" : \""+FIRST_PROJECT_NAME+"\", \"__v\" : 0, \"rootDoc_id\" : ObjectId(\""+docId+"\"), "
						+ "\"lastOpened\" : null, \"archived\" : false }";

				MongoCollection<Document> projectCollection = db.getCollection(PROJECTS_COLLECTION_NAME);

				Document project = Document.parse(projectJson);
				project.put("_id", projectId);
				project.put("owner_ref", userId);
				project.put("lastUpdated", "ISODate("+loginDate+")");
				project.put("lastOpened", "ISODate("+loginDate+")");
				
				projectCollection.insertOne(project);

				MongoCollection<Document> docsCollection = db.getCollection(DOCS_COLLECTION_NAME);
				
				SimpleDateFormat sdfProject = new SimpleDateFormat("MMM yyyy");
				
				String docsJson = "{ \"lines\" : [ \"\\\\documentclass{article}\", "
						+ "\"\\\\usepackage[utf8]{inputenc}\", \"\", \"\\\\title{"+FIRST_PROJECT_NAME+"}\""
						+ ", \"\", \"\\\\author{"+user.getFirstName()+" "+user.getLastName()+"}\""
						+ ", \"\", \"\\\\date{"+sdfProject.format(now.getTime())+"}\""
						+ ", \"\", \"\\\\begin{document}\""
						+ ", \"\", \"\\\\maketitle\", \"\", \"\\\\section{Introduction}\", \"\", \"\\\\end{document}\", \"\" ], "
						+ "\"ranges\" : {  }, \"rev\" : 1 }";
				Document docs = Document.parse(docsJson);
				docs.put("project_id", projectId);
				docs.put("_id", docId);
				docsCollection.insertOne(docs);
				
			}catch(Exception e){
				log.warn("error creating first project",e);
			}

			return user.getEmail();
		}finally{
			client.close();
		}
	}

	private User retrieveUserInfo() throws Exception {
		//String socialServiceEnpoint = context.getInitParameter("socialServiceEnpoint");

		String socialServiceEnpoint = retrieveSocialServiceEnpoint();

		String name= "";
		String lastName = "unknown";
		String email = "";
		ObjectMapper mapper = new ObjectMapper();
		HttpClient httpClient = new HttpClient();
		GetMethod getProfile = new GetMethod(socialServiceEnpoint+"?gcube-token="+SecurityTokenProvider.instance.get());

		try {
			httpClient.executeMethod(getProfile);
			String profile = getProfile.getResponseBodyAsString();

			// convert JSON string to Map
			Map<String, Object> map = new HashMap<String, Object>();

			// convert JSON string to Map
			map = mapper.readValue(profile, new TypeReference<Map<String, Object>>(){});

			@SuppressWarnings("unchecked")
			Map<String, String> profileMap = (Map<String, String>)map.get("result");

			name = profileMap.get("first_name");

			if (profileMap.get("last_name")!=null && !profileMap.get("last_name").isEmpty())
				lastName = profileMap.get("last_name");

			email = profileMap.get("email");

			log.trace("found profile {}, {}, {}",name, lastName, email);

			return new User(name, lastName, email);
		} catch (Exception e) {
			log.warn("error getting profile from social service",e);
			throw e;
		} 
	}

	private String retrieveSocialServiceEnpoint() throws Exception {
		XQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq 'Portal'");
		query.addCondition("$resource/Profile/ServiceName/text() eq 'SocialNetworking'");
		query.addVariable("$entry","$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint");
		query.addCondition("$entry/@EntryName/string() eq 'jersey-servlet'");
		query.setResult("$entry/text()");
		DiscoveryClient<String> client = client();

		List<String> socialServiceEnpoints = client.submit(query);

		if (socialServiceEnpoints.size()==0) throw new Exception("Social servioce enpooint not found in the current scope "+ScopeProvider.instance.get()); 

		String socialServiceEnpoint = socialServiceEnpoints.get(0);

		return socialServiceEnpoint+"/2/users/get-profile";
	}

	private String getGeneratadePassword(){
		return "gcube_SL_"+Math.abs(AuthorizationProvider.instance.get().getClient().getId().hashCode());
	}

	private MongoClient connectToMongo(){
		String mongoHost = context.getInitParameter("mongoHost");
		int mongoPort = Integer.parseInt(context.getInitParameter("mongoPort"));

		/*String mongoLogin = context.getInitParameter("mongoLogin");
		String mongoPassword = context.getInitParameter("mongoPwd");
		String mongoDB = context.getInitParameter("mongoDb");*/

		//MongoClientOptions options = MongoClientOptions.builder().sslEnabled(true).build();
		MongoClient mongoClient = new MongoClient(new ServerAddress(mongoHost, mongoPort));

		return mongoClient;
	}
}
