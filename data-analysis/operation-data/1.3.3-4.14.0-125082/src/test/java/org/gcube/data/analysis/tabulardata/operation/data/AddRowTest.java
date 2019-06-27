package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.data.add.AddRowFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class AddRowTest extends OperationTester<AddRowFactory> {

	@Inject
	AddRowFactory factory;
	
	@Inject 
	GenericHelper helper;
	
	private Table table;
	
	@Before
	public void getTable(){
		table=helper.createSpeciesGenericTable();
	}
	
	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}
	
	@Override
	protected Map<String, Object> getParameterInstances() {
		ArrayList<Map<String,Object>> mappings=new ArrayList<>();
		for(Column col:table.getColumnsExceptTypes(IdColumnType.class)){
			Map<String,Object> mapping=new HashMap<>();
			mapping.put(AddRowFactory.columnParam.getIdentifier(), table.getColumnReference(col));
			mapping.put(AddRowFactory.toSetValue.getIdentifier(),col.getColumnType().getDefaultDataType().getDefaultValue());
			mappings.add(mapping);
		}
		return Collections.singletonMap(AddRowFactory.valueMapping.getIdentifier(), (Object)mappings);
	}
	
	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}
	
	@Override
	protected TableId getTargetTableId() {
		return table.getId();
	}
	
	
}
