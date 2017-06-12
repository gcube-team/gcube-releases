package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.Map;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterThan;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Length;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextContains;
import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.google.common.collect.Maps;
import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ValidateDataWithExpressionTest extends OperationTester<ValidateDataWithExpressionFactory> {

	@Inject
	ValidateDataWithExpressionFactory factory;

	@Inject
	CodelistHelper codelistHelper;

	private Table testCodelist;

	@BeforeClass
	public static void beforeClass() {
		ScopeProvider.instance.set("/gcube/devsec");
	}
	
	@Before
	public void initCodelist() {
		testCodelist = codelistHelper.createSpeciesCodelist();
	}

	@Override
	protected ValidateDataWithExpressionFactory getFactory() {
		return factory;
	}
	
	@Override
	protected TableId getTargetTableId() {
		return testCodelist.getId();				
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String, Object> parameters = Maps.newHashMap();
		ColumnReference columnToCheck = new ColumnReference(testCodelist.getId(), testCodelist.getColumns().get(5)
				.getLocalId());
		Expression expr = new Or(new TextContains(columnToCheck, new TDText("Trachinus")),new TextMatchSQLRegexp(columnToCheck, new TDText("[LEST]")),new GreaterThan(new Length(new TDText("ohmy")), new TDInteger(0)));
		
		
		parameters.put(ValidateDataWithExpressionFactory.EXPRESSION_PARAMETER.getIdentifier(), expr);
		parameters.put(ValidateDataWithExpressionFactory.VALIDATION_TITLE_PARAMETER.getIdentifier(), "NOOOOOO");
		parameters.put(ValidateDataWithExpressionFactory.DESCRIPTION_PARAMETER.getIdentifier(), "Ya know..");
		
		return parameters;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

}
