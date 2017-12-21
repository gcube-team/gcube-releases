package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.data.transformation.ExtractCodelistFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class ExtractCodelistToNewCLTest extends OperationTester<ExtractCodelistFactory>{

	@Inject
	private ExtractCodelistFactory factory;
	@Inject
	private GenericHelper genericHelper;

	private Table sourceTable;

	@Before
	public void setupTables(){
		sourceTable=genericHelper.createSpeciesGenericTable();
	}

	@Override
	protected WorkerFactory getFactory() {
		System.err.print(factory.getOperationDescriptor());
		return factory;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return sourceTable.getId();
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		HashMap<String,Object> mapping=new HashMap<>();
		Column sourceColumn=sourceTable.getColumnsExceptTypes(DimensionColumnType.class,TimeDimensionColumnType.class,IdColumnType.class).get(0);
		mapping.put(ExtractCodelistFactory.SOURCE_COLUMN.getIdentifier(), sourceTable.getColumnReference(sourceColumn));

		HashMap<String,Object> newColumnDefinition=new HashMap<>();
		newColumnDefinition.put(ExtractCodelistFactory.COLUMN_TYPE_PARAMETER.getIdentifier(), new CodeColumnType());
		
		mapping.put(ExtractCodelistFactory.COLUMN_DEFINITION.getIdentifier(),newColumnDefinition);

		return Collections.singletonMap(ExtractCodelistFactory.COLUMN_MAPPING.getIdentifier(), (Object)mapping);
	}
}