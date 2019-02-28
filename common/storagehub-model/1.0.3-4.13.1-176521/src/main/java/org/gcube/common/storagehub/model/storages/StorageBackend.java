package org.gcube.common.storagehub.model.storages;

import java.io.InputStream;

public interface StorageBackend {
	
	String getName();
	
	String copy(String idToCopy, String path);
	
	String move(String idToMove);
	
	MetaInfo upload(InputStream stream, String itemPath);
	
	InputStream getContent(String id);
	
	void delete(String id);
	
}
