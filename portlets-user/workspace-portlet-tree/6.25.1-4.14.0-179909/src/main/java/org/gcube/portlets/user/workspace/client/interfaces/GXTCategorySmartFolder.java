package org.gcube.portlets.user.workspace.client.interfaces;


/**
 * The Enum GXTCategoryItemInterface.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Sep 29, 2016
 */
public enum GXTCategorySmartFolder {

	//Smart Folder for Folder ITEM
	//VALUE = KEY - THE KEY TO SEARCH BY CATEORY IN HOME LIBRARY
//	public static final String SMF_VRE_FOLDERS = "VRE Folders";
	SMF_BIODIVERSITY("Biodiversity", "Biodiversity"),
	SMF_DOCUMENTS("Documents","Documents"),
	SMF_IMAGES("Images","Images"),
	SMF_REPORTS("Reports", "Reports"),
	SMF_TIMESERIES("Time Series","Time Series"),
	SMF_LINKS("Links","Links"),
	SMF_FOLDERS("Folder","Folder"),
	SMF_SHARED_FOLDERS("Shared_Folders", "Shared Folders"),
	SMF_GCUBE_ITEMS("Gcube_Items","Gcube Items"),
	SMF_PUBLIC_FOLDERS("Public_Folders", "Public Folders"),
	SMF_UNKNOWN("Unknown", "Unknown");

	private String id;
	private String value;

	GXTCategorySmartFolder(String id, String value){
		this.id = id;
		this.value = value;
	}

	/**
	 * @return the id
	 */
	public String getId() {

		return id;
	}


	/**
	 * @return the value
	 */
	public String getValue() {

		return value;
	}
}
