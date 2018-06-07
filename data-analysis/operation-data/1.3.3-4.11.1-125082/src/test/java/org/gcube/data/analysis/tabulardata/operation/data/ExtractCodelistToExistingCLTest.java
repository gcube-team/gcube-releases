package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.ArrayList;
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
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class ExtractCodelistToExistingCLTest extends OperationTester<ExtractCodelistFactory>{

	@Inject
	private ExtractCodelistFactory factory;
	@Inject
	private GenericHelper genericHelper;
	@Inject
	private CodelistHelper codelistHelper;


	private Table sourceTable;
	private Table codelistTable;

	@Before
	public void setupTables(){
		sourceTable=genericHelper.createSpeciesGenericTable();
		codelistTable=codelistHelper.createSpeciesCodelist();
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
		ArrayList<HashMap<String,Object>> map=new ArrayList<>();

		int mappedColumns=2;
		for(int i=0;i<mappedColumns;i++){
			HashMap<String,Object> mapping=new HashMap<>();
			Column sourceColumn=sourceTable.getColumnsExceptTypes(DimensionColumnType.class,TimeDimensionColumnType.class,IdColumnType.class).get(i);
			mapping.put(ExtractCodelistFactory.SOURCE_COLUMN.getIdentifier(), sourceTable.getColumnReference(sourceColumn));
			Column targetCodeColumn=(i==0? // first column mapped to code
					codelistTable.getColumnsByType(CodeColumnType.class).get(i):
						codelistTable.getColumnsExceptTypes(CodeColumnType.class,IdColumnType.class).get(i-1));		
			mapping.put(ExtractCodelistFactory.TARGET_CODE_COLUMN.getIdentifier(), codelistTable.getColumnReference(targetCodeColumn));
			map.add(mapping);
		}



		return Collections.singletonMap(ExtractCodelistFactory.COLUMN_MAPPING.getIdentifier(), (Object)map);
	}
}
