package org.gcube.data.access.storagehub.fs;

import java.nio.file.Paths;

public class StorageHubFuseLauncher {

	
	public static void main(String ...args) {
		String token = args[0];
		String scope = args[1];
		String path = args[2];
		
		StorageHubFS shFS= new StorageHubFS(token, scope);
		shFS.mount(Paths.get(path), true, true);
	}
	
}
