/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.client.uploader;

import java.util.HashMap;
import java.util.Map;


/**
 * The Class UploadingMessageBeforeCompleted.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 19, 2018
 */
public class UploadingMessageBeforeCompleted {

	public static String[] MESSAGES = new String[]{
		"Finalising the upload of $",
		"Setting the privacy policy for $",
		"Identifying devices where to persist $",
		"Replicating $ on multiple servers",
		"Setting the active replica of $",
		"Setting the access policy for $",
		"Checking the consistency of $",
		"Setting the business metadata for $",
		"Accounting the operation for $"
		};

	public static int OFFSET = 15;

	public static Map<String,Long> mapStartTime = new HashMap<String, Long>();


	/**
	 * Gets the message.
	 *
	 * @param fileUploadKey the file upload key
	 * @param fileName the file name
	 * @return the message
	 */
	public static String getMessage(String fileUploadKey, String fileName) {

		if(fileUploadKey==null)
			return MESSAGES[0].replace("$", fileName);

		Long uploadingStartTime = mapStartTime.get(fileUploadKey);

		if(uploadingStartTime==null){
			uploadingStartTime = System.currentTimeMillis();
			mapStartTime.put(fileUploadKey, uploadingStartTime);
		}

		long diff = System.currentTimeMillis() - uploadingStartTime;
		int index = 0;
		try{
			//TO SEC
			diff = diff/1000;
			//System.out.println("DIFF TO SEC: "+diff);
			int divResult = (int) (diff/OFFSET);
			index = divResult>=MESSAGES.length?MESSAGES.length-1:divResult;
			//MESSAGES.length
		}catch(Exception e){
			//silent
			index = 0;
		}

		return MESSAGES[index].replace("$", fileName);
	}


}

