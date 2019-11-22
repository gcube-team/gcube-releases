package org.gcube.portlets.widgets.wsthreddssync.client.view;

import org.gcube.portlets.widgets.wsthreddssync.shared.GcubeScope;

// TODO: Auto-generated Javadoc
/**
 * The Class FormatUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 16, 2018
 */
public class FormatUtil {


	  /**
	   * Truncate a string and add an ellipsis ('...') to the end if it exceeds the
	   * specified length.
	   *
	   * @param value the string to truncate
	   * @param len the maximum length to allow before truncating
	   * @return the converted text
	   */
	  public static String ellipse(String value, int len) {
	    if (value != null && value.length() > len) {
	      return value.substring(0, len - 3) + "...";
	    }
	    return value;
	  }


	  /**
  	 * Gets the folder title.
  	 *
  	 * @param folderName the folder name
  	 * @param maxSize the max size
  	 * @return the folder title
  	 */
  	public static String getFolderTitle(String folderName, int maxSize){
		  String title = folderName!=null&&folderName.length()>0?folderName:"";
		  return FormatUtil.ellipse(title, maxSize);
	}


	/**
	 * To scope value.
	 *
	 * @param gcubeScope the gcube scope
	 * @return the string
	 */
	public static String toScopeValue(GcubeScope gcubeScope){

		if(gcubeScope==null)
			return null;

		return "("+gcubeScope.getScopeType()+") "+gcubeScope.getScopeTitle();
	}
}
