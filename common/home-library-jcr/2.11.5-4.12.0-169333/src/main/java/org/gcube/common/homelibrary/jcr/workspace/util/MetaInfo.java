package org.gcube.common.homelibrary.jcr.workspace.util;

import lombok.Data;

@Data
public class MetaInfo {
	
	long size;

	String mimeType;
	
	String storageId;
	
	String remotePath;
	
}
