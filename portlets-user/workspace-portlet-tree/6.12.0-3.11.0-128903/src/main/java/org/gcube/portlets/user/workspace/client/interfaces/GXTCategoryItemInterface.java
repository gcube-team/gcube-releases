package org.gcube.portlets.user.workspace.client.interfaces;
import java.io.Serializable;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GXTCategoryItemInterface implements Serializable {

	
	private static final long serialVersionUID = -9099563454980400252L;
	
	//Smart Folder SMF
	//VALUE = KEY - THE KEY TO SEARCH BY CATEORY IN HOME LIBRARY
	public static final String SMF_BIODIVERSITY = "Biodiversity";
	public static final String SMF_DOCUMENTS = "Documents";
	public static final String SMF_IMAGES = "Images";
	public static final String SMF_REPORTS = "Reports";
	public static final String SMF_TIMESERIES = "Time Series";
	public static final String SMF_LINKS = "Links";
	public static final String SMF_UNKNOWN = "Unknown";
	public static final String SMF_FOLDERS = "Folders";
	public static final String SMF_SHARED_FOLDERS = "Shared Folders";
	public static final String SMF_GCUBE_ITEMS= "Gcube Items";
//	public static final String SMF_VRE_FOLDERS = "VRE Folders";
	
	public GXTCategoryItemInterface(){}
	
	//Messages MS
	public static final String MS_MESSAGES = "Messages";
	public static final String MS_SENT = "Sent";
	public static final String MS_RECEIVED = "Received";


}
