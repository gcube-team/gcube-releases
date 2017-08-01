package org.gcube.application.framework.core.session;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.application.framework.core.util.ASLGroupModel;
import org.gcube.application.framework.core.util.GenderType;
import org.gcube.application.framework.core.util.SessionConstants;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
//import org.gcube.application.framework.core.security.PortalSecurityManager;
//import org.gcube.application.framework.core.util.UserCredential;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gridforum.jgss.ExtendedGSSCredential;

/**
 *   
 * @author Valia Tsagkalidou (NKUA)
 */
@Deprecated
public class ASLSession{

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Object> innerSession;
	private HttpSession session;
	
	private long lastUsedTime;
	private String externalSessionID;
	private String username;
	private String parentScope;
	private String scope;
	private String securityToken;
	private HashMap<String, Notifier> notifiers;
	String scopeName;
	private ASLGroupModel groupModel;
	private boolean loggedIn = false;
	
	private String userEmailAddress;
	private String fullName;
	private String avatarId;
	private GenderType gender;
	
	private long sessionTimeout = -1; //if < 0, not set
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ASLSession.class);
	
	
	/**
	 * A constructor based on the user and a HttpSession
	 * @param session the HttpSession
	 * @param user the username
	 */
	ASLSession(String externalSessionId, String user)
	{
		//OLD, for backwards compatibility
		innerSession = new HashMap<String, Object>();
		notifiers = new HashMap<String, Notifier>();
		lastUsedTime = System.currentTimeMillis();
		username = user;
		this.externalSessionID = externalSessionId;
		groupModel = new ASLGroupModel();
	}
	
	
	/**
	 * A constructor based on the user and a HttpSession
	 * @param session the HttpSession
	 * @param user the username
	 */
	ASLSession(HttpSession session, String user)
	{
		this(session.getId(), user);
		//NEW
		session.setAttribute("notifiers", new HashMap<String, Notifier>());
		session.setAttribute("lastUsedTime", System.currentTimeMillis());
		session.setAttribute("username", user);
		session.setAttribute("groupModel", new ASLGroupModel());
		this.session = session;
	}

	private void initializeAttributes() {
		if(session != null) {
			//NEW WAY
			Enumeration <String> sessAttrNames = session.getAttributeNames();
			while(sessAttrNames.hasMoreElements()){
				String key = sessAttrNames.nextElement();
				if (key.equals("collectionsPresentableFields") || key.equals(SessionConstants.collectionsHierarchy)) {
					session.removeAttribute(key);
					break;
				}
			}
		}
		else {
			//for backwards compatibility
			for (String key:innerSession.keySet()) {
				if (key.equals("collectionsPresentableFields") || key.equals(SessionConstants.collectionsHierarchy)) {
					innerSession.remove(key);
					break;
				}
			}
		}
	}

	/**
	 * It looks into tomcat's web.xml file for session-timeout value.
	 * if not available, it looks into the properties.xml of this JAR
	 * 
	 * @return timeout in milliseconds
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public long getSessionTimeoutMillis() throws IOException, ParserConfigurationException {
		
		//NEW WAY, BASED ON HTTPSESSION
		if(session != null)
			return session.getMaxInactiveInterval() * 1000;
		else{
			//OLD WAY TO CHECK THE SESSION TIMEOUT (WILL BECOME DEPRECATED).
			if(sessionTimeout > 0) //means that is already set to a value !
				return sessionTimeout;
			int timeoutMins;
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = null;
			try {
				dBuilder = dbFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e1) {e1.printStackTrace();}
	        String tomcatBasePath = System.getProperty("catalina.base");
	        String fullFilePath = tomcatBasePath + "/webapps/ROOT/WEB-INF/web.xml";
	        Document webXMLDoc = null;
			try {
		        File webXML = new File(fullFilePath);
				webXMLDoc = dBuilder.parse(webXML);
				webXMLDoc.getDocumentElement().normalize();
				NodeList sessionTimeouts = webXMLDoc.getElementsByTagName("session-timeout"); //this NodeList contains all the <session-timeout> elements - should by only one
				String timeoutString = sessionTimeouts.item(0).getTextContent(); //timeout now contains the timeout value in string. eg "400"
				if( (timeoutString==null) || (timeoutString=="")){
					logger.debug("No property session-timeout in file, setting it to default");
					timeoutMins = 30;
				}
				else{
					timeoutMins = Integer.parseInt(timeoutString) + 5;
				}
			} 
			catch (Exception e) { //case tomcat properties file could not be found
				logger.debug("Could not parse file " + fullFilePath + " for session-timeout property. Parsing from jar.");
				try {//try getting it from the local file
					ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
					URL url = classLoader.getResource("/properties.xml");
					webXMLDoc = dBuilder.parse(new File(url.toURI()));
					webXMLDoc.getDocumentElement().normalize();
					NodeList sessionTimeouts = webXMLDoc.getElementsByTagName("session-timeout"); //this NodeList contains all the <session-timeout> elements - should by only one
					String timeoutString = sessionTimeouts.item(0).getTextContent();
					if( (timeoutString==null) || (timeoutString=="")){
						logger.debug("No property session-timeout in local file, setting it to default");
						timeoutMins = 30;
					}
					else{
						timeoutMins = Integer.parseInt(timeoutString);
					}
				} catch (Exception e1) {
					logger.debug("Could not parse file properties.xml for property. Setting it to default.");
					timeoutMins = 30;
				}
			}
			//At this point, in all cases "timeoutSecs" will have a valid timeout value. If not from the two xml files, then a default one
	        sessionTimeout = timeoutMins * 60000; //in milliseconds
	        logger.info("Session Timeout is: " + sessionTimeout);
			return sessionTimeout;
		}
	}

	/**
	 * Increases the current session timeout value by the given milliseconds. If negative input, it decreases the timeout.
	 * @param milliseconds
	 * @param resetCounting if true, it resets (=0) the counting towards the timeout
	 * @return the new timeout in milliseconds
	 */
	public long increaseSessionTimeout(long milliseconds, boolean resetCounting){
		
		if(session != null){
			//NEW WAY
			int secs = (int)milliseconds/1000;
			session.setMaxInactiveInterval(session.getMaxInactiveInterval() + secs);
			return session.getMaxInactiveInterval()*1000;
		}
		else{
			//OLD WAY, for backwards compatibility
			if(resetCounting)
				lastUsedTime = System.currentTimeMillis();
			sessionTimeout += milliseconds;
			return sessionTimeout;
		}
	}
	
	
	/**
	 * @return whether the session is still valid or not 
	 */
	public boolean isValid()
	{
		if(session != null){
			//NEW WAY
			try {
			  session.getCreationTime();
			} catch (IllegalStateException ise) {
			  return false;
			}
			return true;
		}
		else{
			//OLD WAY
			long maxTime = -1; //it will never be -1
			try {
				maxTime = getSessionTimeoutMillis();
			} catch (Exception e) {	e.printStackTrace();}
			
			if((System.currentTimeMillis() - lastUsedTime) > maxTime)
				return false;
			return true;
		}
	}
	
	/**
	 * SHOULD NOT BE USED
	 * 
	 * @return whether the session is empty or not
	 */
	@Deprecated
	public boolean isEmpty()
	{
		if(session != null){
			//NEW WAY
			return getAttributeNames().isEmpty();
		}
		else{
			//OLD WAY
			lastUsedTime = System.currentTimeMillis();
			return innerSession.isEmpty();
		}
	}
	
	/**
	 * @param name the name of the attribute
	 * @return whether the name attribute exists in the session
	 */
	public boolean hasAttribute(String name)
	{
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			lastUsedTime = System.currentTimeMillis();
			if(innerSession.containsKey(name))
				return true;
			if(!innerSession.containsKey(name) && (session.getAttribute(name)==null)) 
				return false;
			if(!innerSession.containsKey(name) && (session.getAttribute(name)!=null)){
				innerSession.put(name, session.getAttribute(name));
				return true;
			}
			return false;
		}
		else {
			//OLD WAY, for backwards compatibility
			lastUsedTime = System.currentTimeMillis();
			return innerSession.containsKey(name);
		}
	}
	
	/**
	 * @return a set of all the attributes in the session 
	 */
	public Set<String> getAttributeNames()
	{
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			Enumeration<String> names = session.getAttributeNames();
			Set<String> output = new HashSet<String>();
			while(names.hasMoreElements())
				output.add(names.nextElement());
			output.addAll(innerSession.keySet());
			return output;
		}
		else{
			//OLD WAY, for backwards compatibility
			lastUsedTime = System.currentTimeMillis();
			return innerSession.keySet();
		}
		
		//NEW WAY
//		Enumeration<String> names = session.getAttributeNames();
//		Set<String> output = new HashSet<String>();
//		while(names.hasMoreElements())
//			output.add(names.nextElement());
//		return output;
		
	}
	
	/**
	 * <b>ASLSession is deprecated. Should not store attributes here.</b>
	 * 
	 * @param name the name of the attribute
	 * @return the value of the named attribute
	 */
	@Deprecated
	public Object getAttribute(String name)
	{
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			lastUsedTime = System.currentTimeMillis();
			Object innerObj = innerSession.get(name);
			Object httpObj = session.getAttribute(name);
			if(innerObj != null)
				return innerObj;
			else if((httpObj!=null)){
				innerSession.put(name, httpObj);
				return httpObj;
			}
			return null;
		}
		else {
			//OLD WAY, for backwards compatibility
			lastUsedTime = System.currentTimeMillis();
			return innerSession.get(name);
		}
		//NEW WAY
//		return session.getAttribute(name);
	}
	
	/**
	 * <b>ASLSession is deprecated.</b>
	 * 
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	@Deprecated
	public void setAttribute(String name, Object value)
	{
		if(session != null) {
			//NEW WAY
			session.setAttribute(name, value);
		}
//		else{
			//OLD WAY, for backwards compatibility
			lastUsedTime = System.currentTimeMillis();
			innerSession.put(name, value);
//		}
	}
	
	public String getOriginalScopeName() {
		
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			String httpSessionScope = (String) session.getAttribute("scopeName");
			if(scopeName != null)
				return scopeName;
			else if(httpSessionScope != null){
				scopeName = httpSessionScope;
				return httpSessionScope;
			}
			return null;
		}
		else {
			//OLD WAY, for backwards compatibility
			return scopeName;
		}
		//NEW WAY
		//return (String) session.getAttribute("scopeName");
	}
	
	/**
	 * 
	 * <b>ASLSession is deprecated. </b>
	 * 
	 *
	 * @param name the name of the attribute
	 * @return the removed object
	 */
	@Deprecated
	public Object removeAttribute(String name)
	{
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			lastUsedTime = System.currentTimeMillis();
			Object httpAttrib = session.getAttribute(name);
			session.removeAttribute(name);
			Object innerAttrib = innerSession.remove(name);
			return (httpAttrib == null) ? innerAttrib : httpAttrib;
		}
		else {
			//OLD WAY, for backwards compatibility
			lastUsedTime = System.currentTimeMillis();
			return innerSession.remove(name);
		}
		//NEW WAY
//		Object attr = session.getAttribute(name);
//		session.removeAttribute(name);
//		return attr;
	}
	
	/**
	 * DO NOT USE THIS
	 * 
	 * Removes all the attributes from the session
	 */
	@Deprecated
	public void removeAll()
	{
		if(session != null) {
			//NEW WAY
			for(String name : getAttributeNames())
				session.removeAttribute(name);
		}
//		else{
			//OLD WAY, for backwards compatibility
			lastUsedTime = System.currentTimeMillis();
			innerSession.clear();
//		}
		
	}
	
	public String getParentScope(){
		ScopeBean bean = new ScopeBean(getScope());
		return bean.enclosingScope().toString();
	}

	/**
	 * invalidates the session
	 */
	public void invalidate()
	{
		if(session != null) {
			//NEW WAY
			session.setMaxInactiveInterval(0);
		}
//		else {
			//OLD WAY, for backwards compatibility
			long maxTime = -1; //it will never be -1
			try {
				maxTime = getSessionTimeoutMillis();
			} catch (Exception e) {	e.printStackTrace();}
			
			lastUsedTime = System.currentTimeMillis() - maxTime - 120000; // 2 minutes excessive
//		}
	}


	/**
	 * @return the session id
	 */
	public String getExternalSessionID() {
		if(session != null) {
			//NEW WAY
			return session.getId();
		}
		else{
			//OLD WAY
			return externalSessionID;
		}
	}

	/**
	 * @return  the username
	 */
	public String getUsername() {
		
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			String httpUN = (String)session.getAttribute("username");
			if(username != null)
				return username;
			else if(httpUN != null){
				username = httpUN;
				return httpUN;
			}
			return null;
		}
		else{
			//OLD WAY, for backwards compatibility
			return username;
		}
		//NEW WAY
//		return (String)session.getAttribute("username");
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			String httpScope = (String)session.getAttribute("scope");
			if(scope!=null)
				return scope;
			else if(httpScope != null){
				scope = httpScope;
				return httpScope;
			}
			return null;
		}
		else {
			//OLD WAY, for backwards compatibility
			if(scope==null)
				logger.debug("Scope is null, returning null");
			return scope;
		}
		//NEW WAY
//		String scp = (String)session.getAttribute("scope");
//		if(scp==null)
//			logger.debug("Scope is null, returning null");
//		return scp;
	}
	
	/**
	 * @return the name of the scope (VRE)
	 */
	public String getScopeName(){
		
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			String httpScope = (String)session.getAttribute("scope");
			if(scope!=null)
				return scope;
			else if(httpScope != null){
				scope = httpScope;
				return httpScope;
			}
			return null;
		}
		else {
			//OLD WAY, for backwards compatibility
			if(scope==null)
				logger.debug("Scope is null, returning null");
			return scope;
		}
		//NEW WAY
//		String scp = (String)session.getAttribute("scope");
//		if(scp==null)
//			logger.debug("Scope is null, returning null");
//		return scp;
	}

	/**
	 * @param scope the scope name (VRE)
	 */
	public void setScope(String scope) {
		String previousScopeName = null;
		if(session != null) {
			//NEW WAY
			logger.info("The scope about to set is: " + scope);
			previousScopeName = (String)session.getAttribute("scope");
			session.setAttribute("scope", scope);
			session.setAttribute("scopeName", scope);
			ScopeProvider.instance.set(scope);
		}
//		else { 
			//OLD WAY, for backwards compatibility
			logger.info("The scope about to set is: " + scope);
			lastUsedTime = System.currentTimeMillis();
	//		String currentScope = ScopeProvider.instance.get();
	//		logger.info("GCube scope returns: " + currentScope);
			previousScopeName = this.scopeName;
			this.scope = scope;
			this.scopeName = scope;
			ScopeProvider.instance.set(scope);
//		}
		
		//THE BELOW PART REMAINS THE SAME FOR BOTH NEW AND OLD
		// get the attribute that indicates of log in has been done from the login portlet - or if the user logs in from a bookmark
		if (loggedIn == true) {
			// don't log
			initializeAttributes();
			// clear the attribute
			loggedIn = false;
			logger.debug("Passing the logging because the variable was set");
			return;
		}
		if ((previousScopeName != null && !previousScopeName.equals(getScope())) || previousScopeName == null) {
			logger.info("Logging the entrance");
			//TODO: Should do something with the below line
//			innerSession.clear();
			
		} else
			logger.debug("Passing the logging because the scope was the same");
		initializeAttributes();
		

	}
	
	@SuppressWarnings("unchecked")
	private void addNotifier(String key, Notifier value){
		if(session != null)
			((HashMap<String, Notifier>)session.getAttribute("notifiers")).put(key, value);
		else
			logger.error("Could not set notifier for "+key);
	}
	
	@SuppressWarnings("unchecked")
	private Notifier getNotifier(String key){
		if(session != null)
			return ((HashMap<String, Notifier>)session.getAttribute("notifiers")).get(key);
		else{
			logger.error("Could not get notifier for " + key +" because HTTPSession was null. WILL RETURN NULL NOTIFIER");
			return null;
		}
	}
	
	
	public void setSecurityToken(String token){
		if(session != null) {
			//NEW WAY
			SecurityTokenProvider.instance.set(token);
			session.setAttribute("securityToken", token);
		}
//		else{
			//OLD WAY, for backwards compatibility
			this.securityToken = token;
//		}
	}
	
	public void logUserLogin(String scope) {
		//innerSession.clear();
		loggedIn = true;
		// ACCESS LOGGER
	
	}
	
	/**
	 * @param notification the name of the notification to wait for
	 * @throws InterruptedException when the thread is interrupted
	 */
	public void waitNotification(String notification) throws InterruptedException
	{
		Notifier notifier = getNotifier(notification);
		if(notifier == null)
		{
			notifier = new Notifier();
			addNotifier(notification, notifier);
		}

		lastUsedTime = System.currentTimeMillis();
		notifier.waitNotification();
	}
	
	/**
	 * @param notification the name of the notification to send notification
	 * @throws InterruptedException when the thread is interrupted
	 */
	public void notifyAllWaiting(String notification) throws InterruptedException
	{
		Notifier notifier = getNotifier(notification);
		if(notifier == null)
		{
			notifier = new Notifier();
			addNotifier(notification, notifier);
		}

		lastUsedTime = System.currentTimeMillis();
		notifier.notifyAllWaiting();
	}
	
	public void setGroupModelInfos(String groupName, long groupId) {
		
		if(session != null) {
			//NEW WAY
			ASLGroupModel aslGM = ((ASLGroupModel)session.getAttribute("groupModel"));
			aslGM.setGroupName(groupName);
			aslGM.setGroupId(groupId);
			session.setAttribute("groupModel", aslGM);
		}
		else{
			//OLD WAY, for backwards compatibility
			groupModel.setGroupName(groupName);
			groupModel.setGroupId(groupId);
		}
	}
	
	public long getGroupId() {
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			ASLGroupModel agmHttp = (ASLGroupModel) session.getAttribute("groupModel");
			if(groupModel != null)
				return groupModel.getGroupId();
			else if(agmHttp != null){
				groupModel = agmHttp;
				return agmHttp.getGroupId();
			}
			return Long.MIN_VALUE; //should throw an exception instead... but long's primitive and we need not to change the api
		}
		else {
			//OLD WAY, for backwards compatibility
			return groupModel.getGroupId();
		}
		//NEW WAY
//		return ((ASLGroupModel)session.getAttribute("groupModel")).getGroupId();
	}
	
	public String getGroupName() {
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			ASLGroupModel agmHttp = (ASLGroupModel) session.getAttribute("groupModel");
			if(groupModel != null)
				return groupModel.getGroupName();
			else if(agmHttp != null){
				groupModel = agmHttp;
				return agmHttp.getGroupName();
			}
			return null; 
		}
		else {
			//OLD WAY, for backwards compatibility
			return groupModel.getGroupName();
		}
		//NEW WAY
//		return ((ASLGroupModel)session.getAttribute("groupModel")).getGroupName();
	}
	
	public void setUserEmailAddress(String email) {
		//LET"S SET IT ON BOTH...
		
		//NEW WAY
		if(session != null)
			session.setAttribute("userEmailAddress", email);
		//OLD WAY, for backwards compatibility
		this.userEmailAddress = email;
	}
	
	public String getUserEmailAddress() {
		
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			String httpUEA = (String)session.getAttribute("userEmailAddress");
			if(userEmailAddress!=null)
				return userEmailAddress;
			else if(httpUEA != null){
				userEmailAddress = httpUEA;
				return httpUEA;
			}
			return null;
		}
		else {
			//OLD WAY, for backwards compatibility
			return this.userEmailAddress;
		}
		//NEW WAY
//		return (String)session.getAttribute("userEmailAddress");
	}
	
	public void setUserFullName(String fullName) {
		//Let's set it on both
		
		//NEW WAY
		if(session != null)
			session.setAttribute("fullName", fullName);
		//OLD WAY, for backwards compatibility
		this.fullName = fullName;
	}
	
	public String getUserFullName() {
		
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			String httpFN = (String)session.getAttribute("fullName");
			if(fullName != null)
				return this.fullName;
			else if(httpFN != null){
				this.fullName = httpFN;
				return httpFN;
			}
			return null;
		}
		else
			//OLD WAY, for backwards compatibility
			return this.fullName;
		
		//NEW WAY
//		return (String)session.getAttribute("fullName");
	}
	
	public void setUserAvatarId(String avatarId) {
		//Let's set it on both
		//NEW WAY
		if(session != null)
			session.setAttribute("avatarId", avatarId);
		//OLD WAY, for backwards compatibility
		this.avatarId = avatarId;
	}
	
	public String getUserAvatarId() {
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			String httpUAid = (String)session.getAttribute("avatarId");
			if(this.avatarId != null)
				return this.avatarId;
			else if(httpUAid != null){
				this.avatarId = httpUAid;
				return httpUAid;
			}
			return null;
		}
		else
			//OLD WAY, for backwards compatibility
			return this.avatarId;
		//NEW WAY
//		return (String)session.getAttribute("avatarId");
	}
	
	public void setUserGender(GenderType gender) {
		//Let's set it on both		
		//NEW WAY
		if(session != null)
			session.setAttribute("gender", gender);
		//OLD WAY, for backwards compatibility
		this.gender = gender;
	}
	
	
	public GenderType getUserGender() {
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			Object httpGObj = session.getAttribute("gender");
			if(this.gender != null)
				return this.gender;
			else if(httpGObj != null){
				this.gender = (GenderType) httpGObj;
				return (GenderType) httpGObj;
			}
			return null;
		}
		else
			//OLD WAY, for backwards compatibility
			return this.gender;
		//NEW WAY
//		return (GenderType)session.getAttribute("gender");
	}

	public String getSecurityToken() {
		if(session != null) {
			//HYBRID, TEMPORARY SOLUTION FOR FEATURE #5033
			String httpSecurityToken = (String)session.getAttribute("securityToken");
			if(this.securityToken != null){
				logger.debug("Getting security token: " + this.securityToken + " in thread "+Thread.currentThread().getId());
				return this.securityToken;
			}
			else if(httpSecurityToken != null){
				logger.debug("Getting security token: " + httpSecurityToken + " in thread "+Thread.currentThread().getId());
				this.securityToken = httpSecurityToken;
				return httpSecurityToken;
			}
			return null; //if reached this point, means that all security tokens are null
		}
		else {
			//OLD WAY, for backwards compatibility
			logger.debug("Getting security token: " + securityToken+" in thread "+Thread.currentThread().getId());
			return securityToken;
		}
		//NEW WAY
//		String securityToken = (String)session.getAttribute("securityToken");
//		logger.debug("Getting security token: " + securityToken+" in thread "+Thread.currentThread().getId());
//		return securityToken;
		
	}
}
