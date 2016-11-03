package org.gcube.application.aquamaps.aquamapsspeciesview.servlet.utils;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SpeciesSearchDescriptor;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	public static final String xmlHeader="<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>";
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

	private static final String DEFAULT_USER="fabio.sinibaldi";
	private static final String DEFAULT_SCOPE="/gcube/devsec";


	//*************** ECLIPSE MODE
	
			private static boolean isEclipseModeOn=false;
	


	public static String dateFormatter(Date time){
		return sdf.format(time);

	}



	public synchronized static ASLSession getSession(HttpSession httpSession)throws Exception
	{

		String user = (String) httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession toReturn=null;
		if(isEclipseModeOn){
			logger.warn("AQUAMAPSPORTLET IN DEBUG MODE");
			toReturn=getDefaultSession(httpSession.getId());
		}else{
			String sessionID = httpSession.getId();
			
			toReturn=SessionManager.getInstance().getASLSession(sessionID, user);
		}


		try{
			logger.debug("Trying to initialize session for user : "+user);
			
			if(!toReturn.hasAttribute(Tags.SPECIES_SEARCH_FILTER)||toReturn.getAttribute(Tags.SPECIES_SEARCH_FILTER)==null)
				toReturn.setAttribute(Tags.SPECIES_SEARCH_FILTER, new SpeciesSearchDescriptor());
			
			if(!toReturn.hasAttribute(ResourceType.HCAF+"")||!toReturn.hasAttribute(ResourceType.HSPEC+"")||!toReturn.hasAttribute(ResourceType.HSPEN+"")){
				for(Field f:dataManagement().build().getDefaultSources()){
					if(f.name().equals(ResourceType.HCAF+""))toReturn.setAttribute(ResourceType.HCAF+"",f.getValueAsInteger());
					else if(f.name().equals(ResourceType.HSPEN+""))toReturn.setAttribute(ResourceType.HSPEN+"",f.getValueAsInteger());
					else if(f.name().equals(ResourceType.HSPEC+""))toReturn.setAttribute(ResourceType.HSPEC+"",f.getValueAsInteger());
				}	
			}
			
			
			logger.debug("Completed");
		}catch(Exception e){			
			logger.warn("Couldn't complete, service might be down",e);
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
		ScopeProvider.instance.set(DEFAULT_SCOPE);
		return toReturn;
	}

	public static Collection<String> getAvailableScopes() throws Exception {
		ArrayList<String> toReturn=new ArrayList<String>();
		try{
			String infrastructureScope="/"+PortalContext.getConfiguration().getInfrastructureName();
		toReturn.add(infrastructureScope);
		
		for(String vo:PortalContext.getConfiguration().getVOs())toReturn.add(infrastructureScope+"/"+vo);
		}catch(Throwable t){
			logger.warn("UNABLE TO INITIALIZE, ASSUMING DEV MODE..");
			toReturn.add(DEFAULT_SCOPE);
			isEclipseModeOn=true;
		}
		
		return toReturn;
	}

	/**
	 * Returns the enclosing VO scope in case currentScope is a VRE, otherwise the passed scope itself    
	 * 
	 * @param currentScope
	 * @return
	 */
	public static String removeVRE(String currentScope){
		if(currentScope.matches("/(.)*/(.)*/(.)*")) return currentScope.substring(0, currentScope.lastIndexOf('/'));
		return currentScope;
	}
	
	
}
