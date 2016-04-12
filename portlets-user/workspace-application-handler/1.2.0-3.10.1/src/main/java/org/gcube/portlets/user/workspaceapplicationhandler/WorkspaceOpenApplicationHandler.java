/**
 * 
 */
package org.gcube.portlets.user.workspaceapplicationhandler;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.workspaceapplicationhandler.entity.ApplicationEndPoint;
import org.gcube.portlets.user.workspaceapplicationhandler.exception.ApplicationEndPointFoundException;
import org.gcube.portlets.user.workspaceapplicationhandler.exception.ObjectTypeNotFoundException;
import org.gcube.portlets.user.workspaceapplicationhandler.exception.PropertyNotFoundException;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 1, 2013
 * 
 */
public class WorkspaceOpenApplicationHandler {

	public static Logger logger = Logger.getLogger(WorkspaceOpenApplicationHandler.class);
	
	/**
	 * This is a singleton
	 */
	private static WorkspaceOpenApplicationHandler instance;

	private Map<String, Map<String, ApplicationEndPoint>> applicationMap;

	public static synchronized WorkspaceOpenApplicationHandler getInstance() {
		if (instance == null)
			instance = new WorkspaceOpenApplicationHandler();
		return instance;
	}

	private WorkspaceOpenApplicationHandler() {
		applicationMap = new HashMap<String, Map<String, ApplicationEndPoint>>();
	}

	/**
	 * 
	 @param objectType
	 *            - the object type to manage
	 * @param scope
	 *            - the scope where the object is defined
	 * @param applicationUrl
	 *            - the relative (portal) url to redirect
	 * @return
	 * @throws PropertyNotFoundException
	 */
	public void addOpenObjectHandler(String objectType, String scope, String applicationUrl) throws PropertyNotFoundException {

		if(objectType == null || objectType.isEmpty())
			throw new PropertyNotFoundException("The object type parameter is empty");
		
		if(scope == null || scope.isEmpty())
			throw new PropertyNotFoundException("The scope parameter is empty");
		
		if(applicationUrl == null || applicationUrl.isEmpty())
			throw new PropertyNotFoundException("The applicationUrl parameter is empty");
		

		
		ApplicationEndPoint apEp = new ApplicationEndPoint(objectType, scope, applicationUrl);
		
		logger.trace("Create new application end point: "+apEp);
	
		Map<String, ApplicationEndPoint> mapScopes = applicationMap.get(objectType);

		logger.trace("Get application Map for object type: "+objectType +" is null"+ (mapScopes==null));
		
		if(mapScopes==null)
			mapScopes = new HashMap<String, ApplicationEndPoint>();
		
		logger.trace("Put object "+apEp+" into map scopes: "+scope);
		
		mapScopes.put(scope, apEp);
		
		logger.trace("Put object type "+objectType+" into application map");
		
		applicationMap.put(objectType, mapScopes);
		
		
	}
	
	public ApplicationEndPoint getUrlApplicationEndPoint(String objectType, String scope)  throws PropertyNotFoundException, ObjectTypeNotFoundException, ApplicationEndPointFoundException {
		
		if(objectType == null || objectType.isEmpty())
			throw new PropertyNotFoundException("The object type parameter is empty");
		
		if(scope == null || scope.isEmpty())
			throw new PropertyNotFoundException("The scope parameter is empty");
		
		Map<String, ApplicationEndPoint> mapScopes = applicationMap.get(objectType);
		
		if(mapScopes==null)
			throw new ObjectTypeNotFoundException("The object type: "+objectType +" does not found into application map");
		
		ApplicationEndPoint apEn = mapScopes.get(scope);
		
		if(apEn==null)
			throw new ApplicationEndPointFoundException("The ApplicationEndPoint does not found for scope: "+scope);
		
//		PortletURL myUrl = PortletURLFactoryUtil.create(request, "helloWorld_WAR_HelloWorldPortlet", "<plId-of-the-page-PQR>",  PortletRequest.RENDER_PHASE);
//		myUrl.setWindowState(WindowState.MAXIMIZED);
//		myUrl.setPortletMode(PortletMode.VIEW);
//		
		return apEn;
	}

}
