package org.gcube.data.oai.tmplugin.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	private static Logger log = LoggerFactory.getLogger(Constants.class); 
			
	/** The plugin's namespace.*/
	public final static String NS = "http://gcube-system.org/namespaces/data/oaiplugin";
	
	/** The oai-dc format identifier.*/
	public final static String OAI_DC = "oai_dc";
	
	public static Calendar getDate(String datestamp) {
		try{
			DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			dateFormat.parse(datestamp);
			return dateFormat.getCalendar();
		}catch (Exception tolerate) {}
		
		//if the first fail try with second type
		try {
			DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			dateFormat.parse(datestamp);
			return dateFormat.getCalendar();

		}
		catch(Exception tolerate) {
			log.warn("could not parse "+datestamp);
			return null;
		}
		
	}
}
