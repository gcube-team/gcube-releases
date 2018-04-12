package org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover;

import org.gcube.data.analysis.tabulardata.model.resources.Resource;

public interface ResourceRemover {
	
	void onRemove(Resource resource) throws Exception;
}
