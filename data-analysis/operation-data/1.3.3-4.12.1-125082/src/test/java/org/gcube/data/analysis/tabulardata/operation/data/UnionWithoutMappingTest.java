package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.data.add.UnionFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class UnionWithoutMappingTest  extends OperationTester<UnionFactory>{


	@Inject
	UnionFactory factory;

	@Inject 
	GenericHelper helper;

	@Inject
	CubeManager cm;

	private Table targetTable;
	private Table sourceTable;

	@Before
	public void initTables(){
		targetTable=helper.createSpeciesGenericTable();;
		sourceTable=helper.createSpeciesGenericTable();
	}

	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		return Collections.singletonMap(UnionFactory.TABLE_PARAMETER.getIdentifier(), (Object)sourceTable.getId());
	}

	@Override
	protected TableId getTargetTableId() {
		return targetTable.getId();
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		// TODO Auto-generated method stub
		return null;
	}

}
