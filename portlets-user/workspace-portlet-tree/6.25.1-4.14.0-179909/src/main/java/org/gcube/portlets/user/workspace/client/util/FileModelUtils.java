/**
 * 
 */
package org.gcube.portlets.user.workspace.client.util;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.model.FileModel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Jan 23, 2014
 *
 */
public class FileModelUtils {
	
	
	public static List<String> convertFileModelsToIds(List<FileModel> filesModel){
		
		if(filesModel==null || filesModel.size()==0)
			return new ArrayList<String>(1);
		
			
		List<String> ids = new ArrayList<String>(filesModel.size());
		for (FileModel target : filesModel) {
			ids.add(target.getIdentifier());
		}
		return ids;
	}

}
