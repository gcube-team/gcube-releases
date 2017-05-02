package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.logical.Between;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.data.remove.FilterByExpressionFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.google.common.collect.Maps;
import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class FilterByExpressionTest extends OperationTester<FilterByExpressionFactory>{

	@Inject
	FilterByExpressionFactory factory;
	
	@Inject
	CodelistHelper codelistHelper;

	private Table testCodelist;

	
	
	@Before
	public void initCodelist() {
		testCodelist = codelistHelper.createSpeciesCodelist();				
	}

	@Override
	protected FilterByExpressionFactory getFactory() {
		return factory;
	}
	
	@Override
	protected TableId getTargetTableId() {
		return testCodelist.getId();				
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String, Object> parameters = Maps.newHashMap();
//		ColumnReference columnToCheck = new ColumnReference(testCodelist.getId(), testCodelist.getColumns().get(5)
//				.getLocalId());
//		Expression expr = new Or(new TextContains(columnToCheck, new TDText("Trachinus")),new TextMatchSQLRegexp(columnToCheck, new TDText("[LEST]")));
//
		
		ColumnReference ref=new ColumnReference(testCodelist.getId(), testCodelist.getColumnsByType(CodeNameColumnType.class).get(0).getLocalId());
		//Expression expr=new TextBeginsWith(ref, new TDText("a"));
		Expression expr=new Between(ref,new TDText("0"), new TDText("abc"));
		parameters.put(FilterByExpressionFactory.EXPRESSION_PARAMETER.getIdentifier(), expr);
		return parameters;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}
}
