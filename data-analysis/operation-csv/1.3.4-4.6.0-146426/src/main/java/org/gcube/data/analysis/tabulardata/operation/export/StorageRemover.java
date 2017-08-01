package org.gcube.data.analysis.tabulardata.operation.export;

import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.Resource;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover.ResourceRemover;

public class StorageRemover implements ResourceRemover {
	
	private static StorageRemover instance = new StorageRemover();
	
	public static StorageRemover getInstance(){
		return instance;
	}
	
	@Override
	public void onRemove(Resource resource) throws Exception {
		Utils.getStorageClient().remove().RFileById(((InternalURI)resource).getStringValue(), false);
	}

}
