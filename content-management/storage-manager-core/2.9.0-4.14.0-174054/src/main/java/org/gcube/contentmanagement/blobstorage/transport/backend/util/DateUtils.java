package org.gcube.contentmanagement.blobstorage.transport.backend.util;

import java.util.Calendar;
import java.text.SimpleDateFormat;
/**
 * Calculates the current Date
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class DateUtils {

	
/**
 * usage: DateUtils.now("dd MMMMM yyyy")
 * 
 * @param dateFormat "dd MMMMM yyyy"
 * @return the converted string 
 */
  public static String now(String dateFormat) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    return sdf.format(cal.getTime());

  }
  
}
