package org.gcube.portlets.widgets.wstaskexecutor.client.view;

import org.gcube.portlets.widgets.wstaskexecutor.shared.GcubeScope;


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


	/**
	 * This method returns only the VRE name from the input scope if the input scope is a VRE. Otherwise it returns the input scope
	 *
	 * @param scope the vre full scope
	 * @param includeVO the include vo
	 * @return the string
	 */
	public static String toVREName(String scope){

		if(scope==null)
			return null;

		System.out.println(scope.split("/").length);

		if(scope.split("/").length<4)
			return scope;

		return scope.substring(scope.lastIndexOf("/")+1, scope.length());
	}

}
