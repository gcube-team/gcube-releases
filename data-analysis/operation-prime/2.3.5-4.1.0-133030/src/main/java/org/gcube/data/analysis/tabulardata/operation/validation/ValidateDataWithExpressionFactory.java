package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ExpressionParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

@Singleton
public class ValidateDataWithExpressionFactory extends TableValidatorFactory {

	private static final OperationId OPERATION_ID = new OperationId(5006);
	
	public static final ExpressionParameter EXPRESSION_PARAMETER = new ExpressionParameter("expression", "Expression",
			"Expression to validate", Cardinality.ONE);

	public static final SimpleStringParameter DESCRIPTION_PARAMETER = new SimpleStringParameter("description", "Description",
			"Description of the evaluated expression", Cardinality.OPTIONAL);
	
	public static final SimpleStringParameter VALIDATION_TITLE_PARAMETER = new SimpleStringParameter("title", "title",
			"Title of the evaluated expression", Cardinality.OPTIONAL);
	
	public static final SimpleStringParameter VALIDATION_CODE_PARAMETER = new SimpleStringParameter("code", "code",
			"Validation code", Cardinality.OPTIONAL);
	
	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	private DescriptionExpressionEvaluatorFactory descriptionEvaluatorFactory;

	@Inject
	public ValidateDataWithExpressionFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory,
			DescriptionExpressionEvaluatorFactory descriptionEvaluatorFactory) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
		this.descriptionEvaluatorFactory = descriptionEvaluatorFactory;
	}

	@Override
	public ValidationWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		return new ValidateDataWithExpression(invocation, cubeManager, connectionProvider, sqlEvaluatorFactory,
				descriptionEvaluatorFactory);
	}

	@Override
	protected String getOperationName() {
		return "Expression validation";
	}

	@Override
	protected String getOperationDescription() {
		return "Validate table data against an expression";
	}

	@Override
	protected List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(EXPRESSION_PARAMETER);
		parameters.add(DESCRIPTION_PARAMETER);
		parameters.add(VALIDATION_TITLE_PARAMETER);
		parameters.add(VALIDATION_CODE_PARAMETER);
		return parameters;
	}

	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		if(invocation.getParameterInstances().containsKey(DESCRIPTION_PARAMETER.getIdentifier()))
			return (String) OperationHelper.getParameter(DESCRIPTION_PARAMETER, invocation);
		else if(invocation.getParameterInstances().containsKey(VALIDATION_TITLE_PARAMETER.getIdentifier()))
			return (String) OperationHelper.getParameter(VALIDATION_TITLE_PARAMETER, invocation);
		else{
			Expression expr=OperationHelper.getParameter(EXPRESSION_PARAMETER, invocation);
			return String.format("Check condition %s ",descriptionEvaluatorFactory.getEvaluator(expr).evaluate());
		}
	}
}
