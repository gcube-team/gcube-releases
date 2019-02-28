package org.gcube.contentmanagement.timeseries.geotools.vti.connectors;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.DateGuesser;

public class VTIDateFormatConverter {
	
		public static String VTIOfficialDateFormat = "yyyy/MM/dd HH:mm:ss";
		public static void main (String[] args){
			String dd = convert2VTIFormat("2/19/2011 1:17:00 AM");
//			String dd = convert2VTIFormat("20/2/2010 0:49:00");
			
			System.out.println("DD: "+dd);
		}
		
		public static String convert2VTIFormat(String date){
			
				Calendar c = DateGuesser.convertDate(date);
				/*
				System.out.print(date+" -> ");
				System.out.print(DateGuesser.getPattern(date)+" -> ");
				System.out.println("giorno " + c.get(Calendar.DAY_OF_MONTH) + " mese " + (c.get(Calendar.MONTH) + 1) + " anno " + c.get(Calendar.YEAR));
				 */
				//YYYY/MM/ddTHH:mm:ssZ
				SimpleDateFormat formatter = new SimpleDateFormat(VTIOfficialDateFormat);
				String formattedDate = formatter.format(new Date(c.getTimeInMillis()));
				return formattedDate;
				
		}
		
		public List<String> convert2VTIFormat(List<String> dates){
			List<String> outdates = new ArrayList<String> ();
			for (String date:dates){
				String formattedDate = convert2VTIFormat(date);
				outdates.add(formattedDate);
			}
			return outdates;
	}
		
		
}
