package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringPosition;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.data.add.AddColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class AddColumnTest extends OperationTester<AddColumnFactory> {

	@Inject
	private AddColumnFactory factory;
	
	@Inject
	private GenericHelper helper;
	
	private Table table=null;
	
	
	private Table joinedTable=null;
	
	@Before
	public void initTable(){
		table=helper.createDatasetWithSpecies();
		joinedTable=helper.createDatasetWithSpecies();
	}
	
	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}

	@Override
	protected Map getParameterInstances() {
		Map<String,Object> map=new HashMap<String, Object>();
		
		map.put(AddColumnFactory.COLUMN_TYPE.getIdentifier(), new AttributeColumnType());
		map.put(AddColumnFactory.LABEL.getIdentifier(), new ImmutableLocalizedText("Splitted"));
		map.put(AddColumnFactory.DATA_TYPE.getIdentifier(), new TextType());
		Column firstColumn=table.getColumnsByType(AttributeColumnType.class).get(0);
		ColumnReference first=table.getColumnReference(firstColumn);
		
		
		
		Expression toSet=new SubstringByIndex(first, new TDInteger(0), new SubstringPosition(first, new SubstringByRegex(first, new TDText("\\s"))));
		
		
//		Column secondColumn=table.getColumnsByType(AttributeColumnType.class).get(1);
//		ColumnReference second=table.getColumnReference(secondColumn);
//		Expression toSet=new Concat(first,new Concat(new TDText("+"), second));
//		map.put(AddColumnFactory.VALUE_PARAMETER.getIdentifier(), toSet);
//		Expression condition=new Equals(table.getColumnReference(firstColumn), joinedTable.getColumnReference(joinedTable.getColumnsByType(AttributeColumnType.class).get(0)));
//		map.put(AddColumnFactory.CONDITION_PARAMETER.getIdentifier(), condition);
		map.put(AddColumnFactory.ADDITIONAL_META_PARAMETER.getIdentifier(), new DataLocaleMetadata("lt"));
		return map;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return table.getId();
	}

}
