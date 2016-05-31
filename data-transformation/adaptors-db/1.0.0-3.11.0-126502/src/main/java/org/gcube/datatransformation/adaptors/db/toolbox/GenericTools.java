package org.gcube.datatransformation.adaptors.db.toolbox;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBProps;
import org.gcube.datatransformation.adaptors.common.db.xmlobjects.DBSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericTools {

	private static final Logger logger = LoggerFactory.getLogger(GenericTools.class);
	
	static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");

//	static String hostnamefullpath = "";
	
	public static String getCurrentTimestamp(){
		return sdf.format(new Date());
	}
	

	public static boolean mergeableProperties(DBSource dbSource, DBProps dbProps){
		if(dbSource.getSourceName().equals(dbProps.getSourceName())){
			logger.debug("For DB source named: \""+dbSource.getSourceName()+"\" found configuration file named: \""+dbProps.getPropsName()+"\"");
			return true;
		}
		logger.debug("Merging failed ! DB source name is: \""+dbSource.getSourceName()+"\" while dbProps are for source: \""+dbProps.getSourceName()+"\"");
		return false;
	}
	
	
	
}
