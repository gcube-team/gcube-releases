package org.gcube.portal.stohubicons.shared.resources;

import com.google.gwt.resources.client.ImageResource;
/**
 * 
 * @author M. Assante, CNR-ISTI
 * class to be used in GWT for getting image resource
 */
public class GWTIconsManager {
	/**
	 * 
	 * @param isShared true is the folder is shared
	 * @return the ImageResource instance related to the private or shared Folders
	 */
	public static ImageResource getIconFolder(boolean isShared) {
		if (isShared)
			return StorageHubIconResources.INSTANCE.SHARED_FOLDER();
		else
			return StorageHubIconResources.INSTANCE.FOLDER();
	}
	/**
	 * 
	 * @return the ImageResource instance related to the VRE Folders
	 */
	public static ImageResource getIconVREFolder() {
		return StorageHubIconResources.INSTANCE.VRE_FOLDER();
	}
	/**
	 * 
	 * @return the ImageResource instance related to the VRE Folders
	 */
	public static ImageResource getIconTypeLink() {
		return StorageHubIconResources.INSTANCE.LINK();
	}
	/**
	 * 
	 * @return the ImageResource instance related to the VRE Folders
	 */
	public static ImageResource getXMLTypeLink() {
		return StorageHubIconResources.INSTANCE.XML();
	}
	/**
	 * 
	 * @param filenameWithExtension
	 * @return the ImageResource instance related to the file extension
	 * @throws IllegalArgumentException
	 */
	public static ImageResource getIconFile(String filenameWithExtension) throws IllegalArgumentException {
		if(filenameWithExtension==null || filenameWithExtension.compareTo("") == 0)
			throw new IllegalArgumentException("The file name is null or empty");

		String[] splits =  filenameWithExtension.split("\\.");
		String extension = "";
		if (splits.length > 0) {
			extension = splits[splits.length-1];
		}
		if (extension == null || extension.compareTo("") == 0)
			return StorageHubIconResources.INSTANCE.unknown();

		extension = extension.toLowerCase();
		if (extension.equals( "doc") ||extension.equals( "docx"))
			return StorageHubIconResources.INSTANCE.DOC();
		if (extension.equals( "rtf") ||extension.equals( "txt"))
			return StorageHubIconResources.INSTANCE.TXT();
		if (extension.equals( "xls") ||extension.equals( "xlsx"))
			return StorageHubIconResources.INSTANCE.XLS();
		if (extension.equals( "csv"))
			return StorageHubIconResources.INSTANCE.CSV();
		if (extension.equals( "ics"))
			return StorageHubIconResources.INSTANCE.CALENDAR();
		if (extension.equals( "ppt") ||extension.equals( "pptx"))
			return StorageHubIconResources.INSTANCE.PPT();
		if (extension.equals( "pdf"))
			return StorageHubIconResources.INSTANCE.PDF();
		if (extension.equals( "jpg") ||extension.equals( "jpeg") 
				|| extension.equals( "gif") 
				|| extension.equals( "bmp") 
				|| extension.equals( "png") 
				|| extension.equals( "tif") 
				||extension.equals( "tiff")
				)
			return StorageHubIconResources.INSTANCE.IMAGE();
		if (extension.equals( "avi") ||extension.equals( "mp4") || extension.equals( "mpeg") || extension.equals( "mkv"))
			return StorageHubIconResources.INSTANCE.MOVIE();
		if (extension.equals( "html") ||extension.equals( "htm") || extension.equals( "jsp") || extension.equals( "asp") || extension.equals( "php"))
			return StorageHubIconResources.INSTANCE.HTML();
		if (extension.equals( "java") 
				|| extension.equals( "r"))
			return StorageHubIconResources.INSTANCE.CODE();
		if (extension.equals( "sh"))
			return StorageHubIconResources.INSTANCE.SCRIPT();
		if (extension.equals( "rar") 
				|| extension.equals( "zip") 
				|| extension.equals( "tar") 
				|| extension.equals( "tar.gz") 
				|| extension.equals( "cpgz") 
				|| extension.equals( "gz")
				|| extension.equals( "jar")
				)
			return StorageHubIconResources.INSTANCE.ARCHIVE();
		else
			return StorageHubIconResources.INSTANCE.unknown();
	}
}

