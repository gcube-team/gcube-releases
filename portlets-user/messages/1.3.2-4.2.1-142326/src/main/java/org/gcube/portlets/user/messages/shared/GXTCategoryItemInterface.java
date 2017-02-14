package org.gcube.portlets.user.messages.shared;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GXTCategoryItemInterface implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Smart Folder SMF
	public static final String SMF_BIODIVERSITY = "Biodiversity";
	public static final String SMF_DOCUMENTS = "Documents";
	public static final String SMF_IMAGES = "Images";
	public static final String SMF_REPORTS = "Reports";
	public static final String SMF_TIMESERIES = "Time Series";
	public static final String SMF_LINKS = "Links";
//	public static final String NONE = "None";
	public static final String SMF_UNKNOWN = "Unknown";
	public static final String SMF_GCUBE_ITEMS = "Gcube Items";
	
	GXTCategoryItemInterface(){}
	
	//Messages MS
	public static final String MS_MESSAGES = "Messages";
	public static final String MS_SENT = "Sent";
	public static final String MS_RECEIVED = "Received";

}
