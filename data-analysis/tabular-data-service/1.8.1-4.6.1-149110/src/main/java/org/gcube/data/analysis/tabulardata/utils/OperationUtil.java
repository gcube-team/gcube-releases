package org.gcube.data.analysis.tabulardata.utils;

import static org.gcube.data.analysis.tabulardata.expression.dsl.Logicals.and;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.OperationNotFoundException;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.metadata.StorableRule;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.RuleMapping;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.task.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OperationUtil {

	@Inject
	private Factories factories;

	@Inject
	private CubeManager cubeManager;

	private Logger logger = LoggerFactory.getLogger(OperationUtil.class);

	public void addPostValidations(TaskContext context, StorableTabularResource str) throws OperationNotFoundException{
		List<InternalInvocation> invocations = new ArrayList<>();
		if (!str.getRules().isEmpty() && str.getTableId()!=null)
			invocations.add(getRulesInvocation(str.getRules(), str.getTableId()));

		final long TABLETYPEVALIDATION_OP_ID = 5011;
		invocations.add(getInvocationById(TABLETYPEVALIDATION_OP_ID, new HashMap<String, Object>()));
		context.addPostValidations(invocations);
	}

	public void addPostOperations(TaskContext context)  throws OperationNotFoundException {
		final long CREATEVIEW_OP_ID = 1003;
		context.addPostOperations(Collections.singletonList(getInvocationById(CREATEVIEW_OP_ID, new HashMap<String, Object>())));
	}



	public InternalInvocation getRulesInvocation(List<RuleMapping> ruleMappings, long tableId) throws OperationNotFoundException{

		if (ruleMappings ==null || ruleMappings.isEmpty()) return null;

		final String EXPRESSION_PARAMETER= "expression";
		final String COMPOSITE_RULE_PARAMETER = "rules";
		final String NAME_PARAMETER ="name";
		final long RULE_VALIDATION_OP = 5009;

		WorkerFactory<?> factory = factories.get(new OperationId(RULE_VALIDATION_OP));
		if (factory==null){
			logger.error("operation with id {} not found", RULE_VALIDATION_OP);
			throw new OperationNotFoundException("operation with id "+RULE_VALIDATION_OP+" not found");
		}

		Table table = cubeManager.getTable(new TableId(tableId));
		List<Map<String, Object>> rulesParameterInstance = new ArrayList<Map<String, Object>>();
		for (RuleMapping ruleMapping : ruleMappings){
			StorableRule storableRule = ruleMapping.getStorableRule();
			logger.debug("rule found "+storableRule.getName());
			Map<String, Column> internalMapping = new HashMap<String, Column>();
			if (ruleMapping.getColumnLocalId()==null)
				for (Entry<String, String> entry: ruleMapping.getPlaceholderColumnMapping().entrySet())
					internalMapping.put(entry.getKey(), table.getColumnById(new ColumnLocalId(entry.getValue())));
			else
				internalMapping.put("placeholder", table.getColumnById(new ColumnLocalId(ruleMapping.getColumnLocalId())));

			Expression expression = storableRule.getRule().getExpression(table.getId(), internalMapping);


			Map<String, Object> singleRuleparameters = new HashMap<String, Object>();
			singleRuleparameters.put(EXPRESSION_PARAMETER, expression);
			singleRuleparameters.put(NAME_PARAMETER, storableRule.getName() );

			rulesParameterInstance.add(singleRuleparameters);
		}

		InternalInvocation internalInvocation = 
				new InternalInvocation(Collections.singletonMap(COMPOSITE_RULE_PARAMETER, (Object)rulesParameterInstance), factory);

		return internalInvocation;
	}

	public InternalInvocation getInvocationById(long opId, Map<String, Object> parameters) throws OperationNotFoundException{
		WorkerFactory<?> factory = factories.get(new OperationId(opId));
		if (factory==null){
			logger.error("operation with id {} not found", opId);
			throw new OperationNotFoundException("operation with id "+opId+" not found");
		}
		return new InternalInvocation(parameters, factory);
	}

	public InternalInvocation getRemoveInvalidEntryInvocation(Table currentTable, List<ColumnLocalId> validationColumns) {

		final String EXPRESSION_PARAMETER= "expression";
		final long FILTER_OP = 3201;

		WorkerFactory<?> factory = factories.get(new OperationId(FILTER_OP));
		if (factory==null){
			logger.error("operation with id {} not found", FILTER_OP);
			throw new RuntimeException("cannot remove Invalid entry (unexpected error)");
		}

		Expression expression;
		if (validationColumns.size()==1)
			expression = new ColumnReference(currentTable.getId(), validationColumns.get(0));
		else {
			List<Expression> expressions = new ArrayList<>();
			for (ColumnLocalId column: validationColumns)
				expressions.add(new ColumnReference(currentTable.getId(), column));
			expression = and(expressions.toArray(new Expression[expressions.size()]));
		}

		return new InternalInvocation(Collections.singletonMap(EXPRESSION_PARAMETER, (Object) expression) , factory);

	}



}
