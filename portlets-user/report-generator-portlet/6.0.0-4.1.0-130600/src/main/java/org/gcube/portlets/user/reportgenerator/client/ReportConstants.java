package org.gcube.portlets.user.reportgenerator.client;

import com.google.gwt.core.client.GWT;
/**
 * Simply a class containing Static Constants 
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version november 2008 (0.1) 
 */
public class ReportConstants {
	
	/**
	 * tell if you running in eclipse or not
	 */
	public static final boolean isDeployed = true;
	
	/**
	 * 
	 */	
	public static final String LOADING_BAR = GWT.getModuleBaseURL() + "../images/loading-bar.gif";
	
	/**
	 * 
	 */	
	public static final String IMAGE_NEXT_PAGE = GWT.getModuleBaseURL() + "../images/next_p.gif";
	/**
	 * 
	 */
	public static final String IMAGE_PREV_PAGE = GWT.getModuleBaseURL() + "../images/prev_p.gif";
	
	public static final String REPORT_ICON = GWT.getModuleBaseURL() + "../images/iconReport.png";
	
	public static final String REPORT_REF_ICON = GWT.getModuleBaseURL() + "../images/reportRef.png";
}
