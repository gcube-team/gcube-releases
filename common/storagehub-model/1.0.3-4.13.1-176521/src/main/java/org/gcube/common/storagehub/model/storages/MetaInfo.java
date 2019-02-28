package org.gcube.common.storagehub.model.storages;

import lombok.Data;

@Data
public class MetaInfo {
	
	long size;

	String storageId;
	
	String remotePath;
}
