/**
 * 
 */
package org.gcube.common.homelibrary.performance.tool;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class MeasurementCSVGenerator {

	protected static final String DELIMITER = ",";
	
	/**
	 * @param args not used.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * @param session the session to convert.
	 * @param fileName the destination file name.
	 * @throws IOException if an error occurs.
	 */
	public static void writeCSV(MeasurementSession session, String fileName) throws IOException
	{
		OutputStream os = new FileOutputStream(fileName);
		//header: ,v1,...,vn
		StringBuilder header = new StringBuilder();
		for (Long value:session.getValues()) header.append(DELIMITER+String.valueOf(value));
		header.append("\n");
		os.write(header.toString().getBytes());
		os.flush();
		
		//row values
		for (MeasurementChannel channel:session.getChannels()){
			StringBuilder row = new StringBuilder(channel.getName());
			for (MeasurementData data: channel.getData()) row.append(DELIMITER+String.valueOf(data.getTime()));
			row.append("\n");
			os.write(row.toString().getBytes());
			os.flush();
		}
		
		os.close();
		
	}

}
