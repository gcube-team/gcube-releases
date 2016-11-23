package org.apache.jackrabbit.j2ee.workspacemanager.util;

import lombok.Data;

@Data
public class MetaInfo {
	
	int size;

	String mimeType;
	
	String storageId;
	
	String remotePath;
}
