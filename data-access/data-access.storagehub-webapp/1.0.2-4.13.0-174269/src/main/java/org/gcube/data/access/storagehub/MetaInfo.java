package org.gcube.data.access.storagehub;

import lombok.Data;

@Data
public class MetaInfo {
	
	long size;

	String storageId;
	
	String remotePath;
}
