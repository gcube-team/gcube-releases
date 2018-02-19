package org.gcube.data.analysis.tabulardata.operation.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.invocation.InvocationCreator;
import org.gcube.data.analysis.tabulardata.operation.table.metadata.ChangeColumnPositionFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.DatasetHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ChangeColumnPositionTest extends OperationTester<ChangeColumnPositionFactory> {

	@Inject
	CodelistHelper codelistHelper;
	
	@Inject
	DatasetHelper datasetHelper;
	
	
	@Inject
	CubeManager cm;
	@Inject 
	DatabaseConnectionProvider prov;
	@Inject
	CreateViewFactory createViewFactory;
	
	@Inject
	private ChangeColumnPositionFactory factory;

	private Table testTable;

	@Before
	public void before() throws WorkerException {
		Table testCodelist = codelistHelper.createSpeciesCodelist();
		testTable = datasetHelper.createSampleDataset(testCodelist);
		
		InvocationCreator creator=InvocationCreator.getCreator(createViewFactory.getOperationDescriptor()).setParameters(new HashMap<String,Object>());
		creator.setTargetTable(testTable.getId());
		CreateView viewCreator=new CreateView(cm, prov, creator.create());
		viewCreator.execute();		
	}

	@Override
	protected ChangeColumnPositionFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		List<Column> currentList=testTable.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class);
		Collections.shuffle(currentList);
		List<ColumnReference> refs=new ArrayList<>();
		for(Column col:currentList) refs.add(testTable.getColumnReference(col));
		
		System.err.println("SPECIFIED ORDER IS "+refs);
		return Collections.singletonMap(ChangeColumnPositionFactory.COLUMN_ORDER.getIdentifier(), (Object)refs);
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}

}
