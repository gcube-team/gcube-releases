package org.gcube.application.aquamaps.aquamapsportlet.client.constants;

import java.util.HashMap;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientResourceType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

public class AquaMapsPortletCostants {

	public static final String 					AquaMapsPortletLocalImplUrl = GWT.getModuleBaseURL() + "AquaMapsPortletLocalService";
	public static final String 					AquaMapsPortletRemoteImplUrl = GWT.getModuleBaseURL() + "AquaMapsPortletRemoteService";
	public static final String 					GISServletImplUrl = GWT.getModuleBaseURL() + "gwtGeoExtServiceImpl";
	public static final String 					DIVNAME = "CommonGUIDIV_AM";
	/**
	 * 
	 */
	public static final int RECTANGULAR = 0;
	/**
	 * 
	 */
	public static final int POLYGON = 1;
	public static final int SELECTION_HEIGHT=600;	
	public static final int FILTERS_SECTION_WIDTH=400;
	public static final int FILTER_WIDTH=300;
	public static final int SELECTED_HEIGHT=200;
	
	public static final int Filter_Container_Width=FILTER_WIDTH+40;
	
	public static final int WIDTH=1200;
	public static final int HEIGHT=800;
	public static final int WORKSPACE_WIDTH=240;
	public static final int DETAILS_HEIGHT=220;
	public static final int DETAILS_WIDTH=650;	
	public static final int COMBOBOX_WIDTH=190;
	public static final int DESCRIPTION_WIDTH=500;
	public static final int DESCRIPTION_HEIGHT=150;
	public static final int EnvelopeGridsHeight=220;
	public static final Map<String,String> servletUrl=new HashMap<String, String>();
	public static final Map<ClientResourceType,String> resourceNames=new HashMap<ClientResourceType, String>();
	public static final int FILTERS_HEIGHT = 400;
	
	public static final String EMPTY_TEXT="Insert value";
	
	public static DateTimeFormat timeFormat=DateTimeFormat.getLongDateTimeFormat();
	
	
	public static void init()
	{
		servletUrl.put("species",GWT.getModuleBaseURL()+"SpeciesServlet"+";jsessionid=" + AquaMapsPortlet.get().getSessionID());
		servletUrl.put("phylogeny",GWT.getModuleBaseURL()+"PhylogenyServlet"+";jsessionid=" + AquaMapsPortlet.get().getSessionID());
		servletUrl.put("area",GWT.getModuleBaseURL()+"AreaServlet"+";jsessionid=" + AquaMapsPortlet.get().getSessionID());
		servletUrl.put("tree",GWT.getModuleBaseURL()+"TreeServlet"+";jsessionid=" + AquaMapsPortlet.get().getSessionID());
		servletUrl.put("selection", GWT.getModuleBaseURL()+"SelectionServlet"+";jsessionid=" + AquaMapsPortlet.get().getSessionID());
		servletUrl.put("jobs", GWT.getModuleBaseURL()+"JobServlet"+";jsessionid=" + AquaMapsPortlet.get().getSessionID());
		servletUrl.put("occurrenceCells", GWT.getModuleBaseURL()+"OccurrenceCellsServlet"+";jsessionid=" + AquaMapsPortlet.get().getSessionID());
		resourceNames.put(ClientResourceType.HCAF, "Environmental Data");
		resourceNames.put(ClientResourceType.HSPEC, "Simulation Data");
		resourceNames.put(ClientResourceType.HSPEN, "Species Environmental Data");
	}
	
	
	public static String getNewsApplicationID(){
		return "org.gcube.application.aquamaps.aquamapsspeciesview.client.AquaMapsSpeciesView";
	}
	
	public static String getQueryStringParameter(){
		return "mapId";
	}
	
	
}
