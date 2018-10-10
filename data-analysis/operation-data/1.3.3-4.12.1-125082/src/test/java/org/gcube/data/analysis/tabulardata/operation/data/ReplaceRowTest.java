package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.data.replace.ReplaceRowByExpressionFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;
@RunWith(JeeunitRunner.class)
public class ReplaceRowTest extends OperationTester<ReplaceRowByExpressionFactory>{

	
	@Inject
	private GenericHelper genericHelper;
	
	@Inject
	private ReplaceRowByExpressionFactory factory;
	
	@Inject
	private SQLExpressionEvaluatorFactory evaluatorFactory;
	
	private Table testTable;
	
	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}
	
	@Override
	protected Map<String, Object> getParameterInstances() {
		ArrayList<Map<String,Object>> mapping=new ArrayList<>();
		for(Column col:testTable.getColumnsExceptTypes(IdColumnType.class,ValidationColumnType.class)){
			HashMap<String,Object> simpleMapping=new HashMap<>();
			simpleMapping.put(ReplaceRowByExpressionFactory.columnParam.getIdentifier(), testTable.getColumnReference(col));
			if (col.getDataType() instanceof TextType)
				simpleMapping.put(ReplaceRowByExpressionFactory.toSetValue.getIdentifier(), new TDText("--------SUBSTITUTION ----"));
			else 
				simpleMapping.put(ReplaceRowByExpressionFactory.toSetValue.getIdentifier(), col.getDataType().getDefaultValue());
			mapping.add(simpleMapping);
		}
		
		HashMap<String,Object> toReturn=new HashMap<>();
		toReturn.put(ReplaceRowByExpressionFactory.valueMapping.getIdentifier(), (Object)mapping);
		
		Column idCol=testTable.getColumnByName("id");
		
		toReturn.put(ReplaceRowByExpressionFactory.CONDITION_PARAMETER.getIdentifier(), new Equals(testTable.getColumnReference(idCol), new TDInteger(1)));
		return toReturn;
	}
	
	@Override
	protected ColumnLocalId getTargetColumnId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}
	
	@Before
	public void init(){
		testTable=genericHelper.createSpeciesGenericTable();
	}
}
