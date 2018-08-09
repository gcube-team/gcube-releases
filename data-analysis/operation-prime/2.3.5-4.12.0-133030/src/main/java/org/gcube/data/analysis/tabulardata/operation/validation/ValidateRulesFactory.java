package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.CompositeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ExpressionParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

@Singleton
public class ValidateRulesFactory extends TableValidatorFactory {

	private static final OperationId OPERATION_ID = new OperationId(5009);
	
	public static final ExpressionParameter EXPRESSION_PARAMETER = new ExpressionParameter("expression", "Expression",
			"Expression to validate", Cardinality.ONE);
	
	public static final SimpleStringParameter NAME_PARAMETER = new SimpleStringParameter("name", "Name",
			"Rule name", Cardinality.ONE);

	
	public static CompositeParameter RULES_PARAMETER= new CompositeParameter("rules", "rules list", "List of rules to validate",
			new Cardinality(1, Integer.MAX_VALUE),
			Arrays.asList(new Parameter[]{
					NAME_PARAMETER,
					EXPRESSION_PARAMETER
			}));
	
	private ValidateDataWithExpressionFactory validateDataWithExpression;
	
	private CubeManager cubeManager;
	
	@Inject
	public ValidateRulesFactory(CubeManager cubeManager, ValidateDataWithExpressionFactory validateDataWithExpression) {
		this.cubeManager = cubeManager;
		this.validateDataWithExpression = validateDataWithExpression;
	}
	
	@Override
	public ValidationWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		return new ValidateRules(invocation, cubeManager, validateDataWithExpression);
	}

	
	@Override
	protected String getOperationName() {
		return "Rules validation";
	}

	@Override
	protected String getOperationDescription() {
		return "Validate applied rules";
	}

	@Override
	protected List<Parameter> getParameters() {
		return Collections.singletonList((Parameter)RULES_PARAMETER);
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
}
