package org.gcube.application.framework.core.session;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.application.framework.accesslogger.library.impl.AccessLogger;
import org.gcube.application.framework.accesslogger.model.LoginToVreAccessLogEntry;
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
 *  ASLSession is deprecated. Do all handling through HttpSession instead
 *   
 * @author Valia Tsagkalidou (NKUA)
 */
@Deprecated
public class ASLSession{

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Object> innerSession;
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
	
	// ACCESS LOGGER
	AccessLogger accessLogger = AccessLogger.getAccessLogger();
	
	/**
	 * A constructor based on the user and an external ID
	 * @param externalSessionId the external id
	 * @param user the username
	 */
	@Deprecated
	ASLSession(String externalSessionId, String user)
	{
		innerSession = new HashMap<String, Object>();
		notifiers = new HashMap<String, Notifier>();
		lastUsedTime = System.currentTimeMillis();
		username = user;
		externalSessionID = externalSessionId;
		groupModel = new ASLGroupModel();
	}

	private void initializeAttributes() {
		for (String key:innerSession.keySet()) {
			if (key.equals("collectionsPresentableFields") || key.equals(SessionConstants.collectionsHierarchy)) {
				innerSession.remove(key);
				break;
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

	/**
	 * Increases the current session timeout value by the given milliseconds. If negative input, it decreases the timeout.
	 * @param milliseconds
	 * @param resetCounting if true, it resets (=0) the counting towards the timeout
	 * @return the new timeout in milliseconds
	 */
	public long increaseSessionTimeout(long milliseconds, boolean resetCounting){
		if(resetCounting)
			lastUsedTime = System.currentTimeMillis();
		sessionTimeout += milliseconds;
		return sessionTimeout;
	}
	
	
	/**
	 * @return whether the session is still valid or not 
	 */
	public boolean isValid()
	{
		long maxTime = -1; //it will never be -1
		try {
			maxTime = getSessionTimeoutMillis();
		} catch (Exception e) {	e.printStackTrace();}
		
		if((System.currentTimeMillis() - lastUsedTime) > maxTime)
			return false;
		return true;
	}
	
	/**
	 * @return whether the session is empty or not
	 */
	public boolean isEmpty()
	{
		lastUsedTime = System.currentTimeMillis();
		return innerSession.isEmpty();
	}
	
	/**
	 * @param name the name of the attribute
	 * @return whether the name attribute exists in the session
	 */
	public boolean hasAttribute(String name)
	{
		lastUsedTime = System.currentTimeMillis();
		return innerSession.containsKey(name);
	}
	
	/**
	 * @return a set of all the attributes in the session 
	 */
	public Set<String> getAttributeNames()
	{
		lastUsedTime = System.currentTimeMillis();
		return innerSession.keySet();
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
		lastUsedTime = System.currentTimeMillis();
		return innerSession.get(name);
	}
	
	/**
	 * <b>ASLSession is deprecated. Should not store attributes here.</b>
	 * 
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 */
	@Deprecated
	public void setAttribute(String name, Object value)
	{
		lastUsedTime = System.currentTimeMillis();
		innerSession.put(name, value);
	}
	
	public String getOriginalScopeName() {
		return scopeName;
	}
	
	/**
	 * 
	 * <b>ASLSession is deprecated. Should not store attributes here.</b>
	 * 
	 *
	 * @param name the name of the attribute
	 * @return the removed object
	 */
	@Deprecated
	public Object removeAttribute(String name)
	{
		lastUsedTime = System.currentTimeMillis();
		return innerSession.remove(name);
	}
	
	/**
	 * Removes all the attributes from the session
	 */
	public void removeAll()
	{
		lastUsedTime = System.currentTimeMillis();
		innerSession.clear();
	}
	
	public String getParentScope(){
		ScopeBean bean = new ScopeBean(getScope());
		parentScope = bean.enclosingScope().toString();
		return parentScope;
	}

	/**
	 * invalidates the session
	 */
	public void invalidate()
	{
		long maxTime = -1; //it will never be -1
		try {
			maxTime = getSessionTimeoutMillis();
		} catch (Exception e) {	e.printStackTrace();}
		
		lastUsedTime = System.currentTimeMillis() - maxTime - 120000; // 2 minutes excessive
	}

	/**
	 * @return the credential
	 */
/*	
	DO NOT FORGET TO COMMENT OUT THIS WHEN THE NEW SECURITY MODEL IS AVAILABLE
	
	public ExtendedGSSCredential getCredential() {
		return credential;
	}
	
*/	

	/**
	 * @return the external session id (passed to the constructor)
	 */
	public String getExternalSessionID() {
		return externalSessionID;
	}

	/**
	 * @return  the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		if(scope==null)
			logger.debug("Scope is null, returning null");
		return scope;
	}
	
	/**
	 * @return the name of the scope (VRE)
	 */
	public String getScopeName(){
		if(scope==null)
			logger.debug("Scope is null, returning null");
		return scope;
	}

	/**
	 * @param scope the scope name (VRE)
	 */
	public void setScope(String scope) {
		logger.info("The scope about to set is: " + scope);
		lastUsedTime = System.currentTimeMillis();
		/*
		String[] split = scope.trim().substring(1).split("/",2);
		//Uncomment this and comment the line bellow for devsec
		//String vo = "/" + split[0].toLowerCase();
		String vo = "/" + split[0];
		if(split.length > 1)
			vo += "/" + split[1];
		*/
//		String currentScope = ScopeProvider.instance.get();
//		logger.info("GCube scope returns: " + currentScope);
		String previousScopeName = this.scopeName;
		this.scope = scope;
		this.scopeName = scope;
		ScopeProvider.instance.set(scope);
		
/*   DO NOT FORGET TO ADD THIS WHEN THE NEW SECURITY MODEL IS AVAILABLE !
		
		if(new PortalSecurityManager(this.scope).isSecurityEnabled())
			this.credential = UserCredential.getCredential(username, scope);
	
*/
		// get the attribute that indicates of log in has been done from the login portlet - or if the user logs in from a bookmark
		if (loggedIn == true) {
			// don't log
			initializeAttributes();
			// clear the attribute
			loggedIn = false;
			logger.debug("Passing the logging because the variable was set");
			return;
		}
		if ((previousScopeName != null && !previousScopeName.equals(scopeName)) || previousScopeName == null) {
			logger.info("Logging the entrance");
			innerSession.clear();
			// ACCESS LOGGER
			LoginToVreAccessLogEntry loginEntry = new LoginToVreAccessLogEntry();
			accessLogger.logEntry(username, scope, loginEntry);
		} else
			logger.debug("Passing the logging because the scope was the same");
		initializeAttributes();
		
		
	}
	
	
	public void setSecurityToken(String token){
		SecurityTokenProvider.instance.set(token);
		this.securityToken = token;
	}
	
	public void logUserLogin(String scope) {
		innerSession.clear();
		loggedIn = true;
		// ACCESS LOGGER
		LoginToVreAccessLogEntry loginEntry = new LoginToVreAccessLogEntry();
		accessLogger.logEntry(username, scope, loginEntry);
	}
	
	/**
	 * @param notification the name of the notification to wait for
	 * @throws InterruptedException when the thread is interrupted
	 */
	public void waitNotification(String notification) throws InterruptedException
	{
		Notifier notifier = notifiers.get(notification);
		if(notifier == null)
		{
			notifier = new Notifier();
			notifiers.put(notification, notifier);
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
		Notifier notifier = notifiers.get(notification);
		if(notifier == null)
		{
			notifier = new Notifier();
			notifiers.put(notification, notifier);
		}

		lastUsedTime = System.currentTimeMillis();
		notifier.notifyAllWaiting();
	}
	
	public void setGroupModelInfos(String groupName, long groupId) {
		groupModel.setGroupName(groupName);
		groupModel.setGroupId(groupId);
	}
	
	public long getGroupId() {
		return groupModel.getGroupId();
	}
	
	public String getGroupName() {
		return groupModel.getGroupName();
	}
	
	public void setUserEmailAddress(String email) {
		this.userEmailAddress = email;
	}
	
	public String getUserEmailAddress() {
		return this.userEmailAddress;
	}
	
	public void setUserFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getUserFullName() {
		return this.fullName;
	}
	
	public void setUserAvatarId(String avatarId) {
		this.avatarId = avatarId;
	}
	
	public String getUserAvatarId() {
		return this.avatarId;
	}
	
	public void setUserGender(GenderType gender) {
		this.gender = gender;
	}
	
	public GenderType getUserGender() {
		return this.gender;
	}

	public String getSecurityToken() {
		logger.debug("Getting security token: " + securityToken+" in thread "+Thread.currentThread().getId());
		return securityToken;
	}
}
