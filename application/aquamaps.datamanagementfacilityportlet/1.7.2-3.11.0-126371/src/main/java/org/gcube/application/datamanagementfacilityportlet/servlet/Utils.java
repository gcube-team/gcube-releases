package org.gcube.application.datamanagementfacilityportlet.servlet;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVTargetRegistry;
import org.json.JSONArray;
import org.json.JSONObject;



public class Utils {

	
	
	private static final String DEFAULT_USER="fabio.sinibaldi";
//	private static final String DEFAULT_SCOPE="/d4science.research-infrastructures.eu/gCubeApps";
	private static final String DEFAULT_SCOPE="/gcube/devsec";
	
	
	
	
	
	static{
		CSVTargetRegistry.getInstance().add(new CSVImporter());
	}
	
	public static synchronized ASLSession getSession(HttpSession httpSession)throws Exception
	{
		
		String user=(String) httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession toReturn=null;
		String sessionID = httpSession.getId();
		
		if(user==null) toReturn=getDefaultSession(httpSession.getId());
		else{
//			user = (String) httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
			toReturn=SessionManager.getInstance().getASLSession(sessionID, user);
		}
		
		return toReturn;
	}
	

	@Deprecated
	private static ASLSession getDefaultSession(String id)throws Exception{
		ASLSession toReturn=null;
		String sessionID = id;
		String user = DEFAULT_USER;
		toReturn=SessionManager.getInstance().getASLSession(sessionID, user);
		String scope=DEFAULT_SCOPE;
		toReturn.setScope(scope);
		return toReturn;
	}
	
	
	public static ArrayList<String> JSONFields(String JSONString)throws Exception{
		ArrayList<String> toReturn=new ArrayList<String>();
		JSONArray array=(new JSONObject(JSONString)).getJSONArray(Tags.DATA);
		for(String field:JSONObject.getNames(array.getJSONObject(0))) toReturn.add(field);
		return toReturn;
	}
	
	
}
