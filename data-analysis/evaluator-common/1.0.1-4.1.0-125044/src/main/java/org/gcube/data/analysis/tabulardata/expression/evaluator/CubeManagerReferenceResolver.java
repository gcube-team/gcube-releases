package org.gcube.data.analysis.tabulardata.expression.evaluator;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.Table;

@Singleton
public class CubeManagerReferenceResolver implements ReferenceResolver {

	private CubeManager cubeManager;

	@Inject
	public CubeManagerReferenceResolver(CubeManager cubeManager) {
		this.cubeManager = cubeManager;
	}

	@Override
	public Column getColumn(ColumnReference columnRef) {
		return getTable(columnRef).getColumnById(columnRef.getColumnId());
	}

	@Override
	public Table getTable(ColumnReference columnRef) {
		return cubeManager.getTable(columnRef.getTableId());
	}

}
