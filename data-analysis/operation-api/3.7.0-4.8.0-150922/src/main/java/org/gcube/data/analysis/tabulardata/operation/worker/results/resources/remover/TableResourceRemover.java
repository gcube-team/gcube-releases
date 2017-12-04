package org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.resources.Resource;
import org.gcube.data.analysis.tabulardata.model.resources.TableResource;

public class TableResourceRemover implements ResourceRemover {

	private CubeManager cm;
	
	public TableResourceRemover(CubeManager cm){
		this.cm = cm;
	}
	
	@Override
	public void onRemove(Resource resource) throws Exception {
		TableResource tableResurce = (TableResource) resource;
		cm.removeTable(tableResurce.getTableId());
	}

}
