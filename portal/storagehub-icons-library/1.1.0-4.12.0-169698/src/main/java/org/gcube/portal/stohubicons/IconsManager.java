package org.gcube.portal.stohubicons;

import org.gcube.portal.stohubicons.shared.MDIcon;

/**
 * 
 * @author M. Assante CNR
 * <p>
 * makes it easy to incorporate icons into your web page. Here’s a small example: &lt;i class="material-icons"&gt;face&lt;/i&gt; <br><br>
 * <b>Please note: for make this work you must install material-design-icons</b>:<br>
 * The easiest way to set up icon fonts for use in any web page is through Google Web Fonts. All you need to do is include a single line of HTML: 
 * &lt;link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"&gt;
 * </p>
 * More information and othere ways to setup MD Icons @see <a href="http://google.github.io/material-design-icons/">http://google.github.io/material-design-icons/</a>
 * 
 */
public class IconsManager {
	/**
	 * 
	 * @param isShared true is the folder is shared
	 * @return the html related to the private or shared Folders
	 */
	public static MDIcon getIconFolder(boolean isShared) {
		if (isShared)
			return new MDIcon("folder_shared", "#8F8F8F"); //darker gray
		else
			return new MDIcon("folder", "#8F8F8F"); //darker gray
	}
	/**
	 * 
	 * @return the html related to the VRE Folders
	 */
	public static MDIcon getIconVREFolder() {
		return new MDIcon("folder_special", "#8F8F8F"); //darker gray
	}
	
	public static MDIcon getIconTypeLink() {
		return new MDIcon("link",  "#0277bd"); // light-blue darken-3
	}
	
	public static MDIcon getXMLTypeLink() {
		return new MDIcon("assignment", "#f44336"); //red
	}
	
	public static MDIcon getDefault() {
		return new MDIcon("insert_drive_file", "#CCC"); //red
	}
	/**
	 * 
	 * easy to incorporate icons into your web page. Here’s a small example: &lt;i class="material-icons"&gt;face&lt;/i&gt;
	 * @see <a href="http://google.github.io/material-design-icons/">http://google.github.io/material-design-icons/</a>
	 * @param filenameWithExtension the file name with extension
	 * @return the Material Design Icon textual name to be placed within any &lt;i class="material-icons"&gt; $The Returned Material Design Icon Textual Name &lt;/i&gt;
	 */
	public static MDIcon getMDIconTextualName(String filenameWithExtension) {
		String[] splits =  filenameWithExtension.split("\\.");
		String extension = "";
		if (splits.length > 0) {
			extension = splits[splits.length-1];
		}
		if (extension == null || extension.compareTo("") == 0)
			return new MDIcon("insert_drive_file", "#CCC"); //gray

		extension = extension.toLowerCase();
		if (extension.equals( "doc") ||extension.equals( "docx"))
			return new MDIcon("description", "#0277bd"); // light-blue darken-3
		if (extension.equals( "xls") ||extension.equals( "xlsx"))
			return new MDIcon("description", "#4caf50"); //green
		if (extension.equals( "rtf") ||extension.equals( "txt"))
			return new MDIcon("description", "#CCC"); //gray		
		if (extension.equals( "csv"))
			return new MDIcon("assessment", "#283593"); //blue
		if (extension.equals( "ics"))
			return new MDIcon("calendar_today", "#f44336");
		if (extension.equals( "ppt") ||extension.equals( "pptx"))
			return new MDIcon("description", "#fb8c00"); //orange
		if (extension.equals( "pdf"))
			return new MDIcon("picture_as_pdf", "#f44336"); //red
		if (extension.equals( "jpg") ||extension.equals( "jpeg") 
				|| extension.equals( "gif") 
				|| extension.equals( "bmp") 
				|| extension.equals( "png") 
				|| extension.equals( "tif") 
				||extension.equals( "tiff")
				)
			return new MDIcon("panorama", "#d81b60"); //fuxia
		if (extension.equals( "avi") ||extension.equals( "mp4") || extension.equals( "mpeg") || extension.equals( "mkv"))
			return new MDIcon("movie_creation", "#90caf9"); 
		if (extension.equals( "html") ||extension.equals( "htm") || extension.equals( "jsp") || extension.equals( "asp") || extension.equals( "php"))
			return new MDIcon("web", "#0277bd"); 
		if (extension.equals( "java") 
				|| extension.equals( "r"))
			return new MDIcon("code", "#f44336");  // red
		if (extension.endsWith("xml"))
			return new MDIcon("assignment", "#f44336"); //red
		if (extension.equals("sh"))
			return new MDIcon("developer_mode", "#f44336"); //red
		if (extension.equals( "rar") 
				|| extension.equals( "zip") 
				|| extension.equals( "tar") 
				|| extension.equals( "tar.gz") 
				|| extension.equals( "cpgz") 
				|| extension.equals( "gz")
				|| extension.equals( "jar")
				)
			return new MDIcon("archive", "#ffc107");  //amber
		
		return new MDIcon("insert_drive_file", "#CCC"); //gray
	}
}
