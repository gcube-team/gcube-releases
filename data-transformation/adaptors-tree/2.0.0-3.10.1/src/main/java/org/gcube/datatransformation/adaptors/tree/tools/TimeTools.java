package org.gcube.datatransformation.adaptors.tree.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeTools {

	static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");

	
	public static String getCurrentTimestamp(){
		return sdf.format(new Date());
	}
	
	
}
