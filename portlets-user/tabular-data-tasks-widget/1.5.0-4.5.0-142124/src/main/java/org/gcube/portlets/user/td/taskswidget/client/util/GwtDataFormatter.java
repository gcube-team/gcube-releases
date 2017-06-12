/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Dec 2, 2013
 *
 */
public class GwtDataFormatter {
	
	  /**
	 * 
	 */
	public static final String YYYY_MMM_D_HH_MM_SS = "MMM d, yyyy 'at' HH:mm:ss";

	
	// A custom date format
	public static final DateTimeFormat fmt = DateTimeFormat.getFormat(YYYY_MMM_D_HH_MM_SS);

    
    public static String getDateFormat(Date date){
    	
    	if(date==null)
    		return null;
    	
    	return fmt.format(date);
    }
    
    
    public static  String formatDates(Date start, Date end){

		String formatter = "[";
		
		if(start!=null){
			formatter+= getDateFormat(start);
		}else
			formatter+="Not found";
		
		formatter+=" - ";
		
		if(end!=null){
			formatter+= getDateFormat(end);
		}else
			formatter+="Not found";

		return formatter+="]";
	}
    
	public static String fmtToInt(float d)
	{
	    return Math.round(d)+"";
	}
	

}
