package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.factories.AttributeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.data.add.UnionFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class UnionTest extends OperationTester<UnionFactory>{
	
	@Inject
	UnionFactory factory;
	
	@Inject 
	GenericHelper helper;
	
	@Inject
	CubeManager cm;
	
	private Table targetTable;
	private Table sourceTable;
	private ArrayList<Column> toMapColumns=new ArrayList<>();
	
	private int toMapColCount=2;
	
	@Before
	public void initTables(){
		for(int i=0;i<toMapColCount;i++)
			toMapColumns.add(new AttributeColumnFactory().create(new ImmutableLocalizedText("Column "+i)));
		
		TableCreator tCreator=cm.createTable(new GenericTableType());
		for(Column toAdd:toMapColumns)
			tCreator.addColumn(toAdd);
		targetTable=tCreator.addColumn(new AttributeColumnFactory().createDefault()).create();
		sourceTable=helper.createSpeciesGenericTable();
	}
	
	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		ArrayList<Map<String,Object>> paramInstance=new ArrayList<>();
		for(int i=0;i<toMapColCount;i++){
			Map<String,Object> mapping=new HashMap<>();
			Column sourceColumn=sourceTable.getColumnsByType(AttributeColumnType.class).get(i);
			mapping.put(UnionFactory.SOURCE_COLUMN_PARAMETER.getIdentifier(), sourceTable.getColumnReference(sourceColumn));
			mapping.put(UnionFactory.TARGET_COLUMN_PARAMETER.getIdentifier(), targetTable.getColumnReference(toMapColumns.get(i)));
			paramInstance.add(mapping);
		}
		
		return Collections.singletonMap(UnionFactory.MAPPINGS_PARAMETER.getIdentifier(), (Object)paramInstance);		
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
