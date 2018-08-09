package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNull;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.data.replace.ReplaceByExpressionFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ReplaceByExpressionTest extends OperationTester<ReplaceByExpressionFactory>{

	
	@Inject
	private GenericHelper genericHelper;
	
	@Inject
	private ReplaceByExpressionFactory factory;
	
	@Inject
	private SQLExpressionEvaluatorFactory evaluatorFactory;
	
	private Table testTable;
	private Table fromTable  ;
	
	@Before
	public void before() {
		testTable = genericHelper.createSpeciesGenericTable();
		fromTable = genericHelper.createNumbersGenericTable();
	}
	
	@Override
	protected ReplaceByExpressionFactory getFactory() {
		return factory;
	}
	
	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String,Object> toReturn= new HashMap<String, Object>();
		//Form condition expression
		List<Expression> conditionArguments=new ArrayList<Expression>();;
		for(Column col:testTable.getColumns()){
			conditionArguments.add(new IsNull(testTable.getColumnReference(col)));
		}
		Or condition=new Or(conditionArguments);
		System.out.println("Condition : "+evaluatorFactory.getEvaluator(condition).evaluate());
//		toReturn.put(ReplaceByExpressionFactory.CONDITION_PARAMETER.getIdentifier(), condition);
//		toReturn.put(ReplaceByExpressionFactory.CONDITION_PARAMETER.getIdentifier(), new TDBoolean(true));
		//Form to set value expression
//		toReturn.put(ReplaceByExpressionFactory.VALUE_PARAMETER.getIdentifier(), new TDText("MATCHED"));

		
		toReturn.put(ReplaceByExpressionFactory.VALUE_PARAMETER.getIdentifier(), new ColumnReference(fromTable.getId(), fromTable.getColumns().get(1).getLocalId()));
		
		return toReturn;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return testTable.getColumns().get(1).getLocalId();
	}

	@Override
	protected TableId getTargetTableId() {
		return testTable.getId();
	}
	
}
