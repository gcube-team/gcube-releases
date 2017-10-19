package org.gcube.data.transfer.model.utils;

import java.util.HashSet;
import java.util.Set;

import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.options.TransferOptions;

public class CapabilitiesUtils {

	
	
	
	public static Set<TransferOptions> scanSystem(){
		Set<TransferOptions> availableMeans=new HashSet<>();
		availableMeans.add(HttpDownloadOptions.DEFAULT);
		return availableMeans;
	}
}
