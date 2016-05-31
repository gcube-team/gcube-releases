package org.gcube.data.analysis.tabulardata.operation.validation;

import java.sql.SQLException;
import java.util.HashMap;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.metadata.table.HarmonizationRuleTable;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.utils.Harmonizations;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerWrapper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateDimensionWorker extends ValidationWorker{

	private static Logger logger = LoggerFactory.getLogger(ValidateDimensionWorker.class);
	
	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	private ValidateDataWithExpressionFactory validatorFactory;

	public ValidateDimensionWorker(OperationInvocation sourceInvocation,
			CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory,
			ValidateDataWithExpressionFactory validatorFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
		this.validatorFactory=validatorFactory;
	}

	private Table targetTable;
	private Column dimensionCandidate;
	private Table codelistTable;
	private Column referredColumn;

	// if harmonization are applied, then this is the copy of the original values
	private Table appTable=null;
	private Column appColumn=null;


	@Override
	protected ValidityResult execute() throws WorkerException, OperationAbortedException {
		init();
		updateProgress(0.1f,"Checking harmonization rules");
		checkAborted();
		evaluateExistingHarmonizationRules();
		updateProgress(0.6f,"Validating dimension candidate");
		ValidityResult toReturn=wrapValidation();
		updateProgress(0.9f,"Finalizing validation");
		if(appTable!=null) {// need to clean up
			restoreHarmonizedValues();
			updateProgress(0.95f,"Cleaning up");
		}
		return toReturn;
	}


	private void evaluateExistingHarmonizationRules() throws WorkerException{
		try{
			logger.debug("target codelist is "+codelistTable);
		if(codelistTable.contains(HarmonizationRuleTable.class)){
			//existing rules
			HarmonizationRuleTable rules=codelistTable.getMetadata(HarmonizationRuleTable.class);
			if(Harmonizations.isColumnUnderRules(referredColumn.getLocalId(), rules,connectionProvider,sqlEvaluatorFactory)){
				updateProgress(0.2f,"evaluating harmonization rules");
				logger.debug("Preparing table with harmonizations");
				prepareTable(rules);
			}else logger.debug("No rule defined for col "+referredColumn.getLocalId());
		}else logger.debug("Harmonization rules not found");
		}catch(Exception e){
			throw new WorkerException("Unable to evaluate Harmonizations",e);
		}
		
	}
	
	private void init(){
		targetTable=cubeManager.getTable(getSourceInvocation().getTargetTableId());
		dimensionCandidate=targetTable.getColumnById(getSourceInvocation().getTargetColumnId());
		ColumnReference ref=OperationHelper.getParameter(ValidateDimensionColumnFactory.TARGET_COLUMN_PARAMETER, getSourceInvocation());
		codelistTable=cubeManager.getTable(ref.getTableId());
		referredColumn=codelistTable.getColumnById(ref.getColumnId());
	}



	private void prepareTable(HarmonizationRuleTable harmTable) throws SQLException{
		appColumn=BaseColumnFactory.getFactory(dimensionCandidate.getColumnType()).create(dimensionCandidate.getDataType());
		appTable=cubeManager.createTable(new GenericTableType()).addColumn(appColumn).create();
		String copyStatement=String.format("INSERT INTO %s (id,%s) (SELECT id,%s from %s)",
				appTable.getName(),
				appColumn.getName(),
				dimensionCandidate.getName(),
				targetTable.getName());
		logger.debug("Copying values before applying harmonization");
		
			// copy values in app table
			SQLHelper.executeSQLCommand(copyStatement, connectionProvider);

		logger.debug("Applying harmonizations..");
			// apply harmonizations
			Harmonizations.harmonizeTable(harmTable, 
					codelistTable.getColumnReference(referredColumn), targetTable.getColumnReference(dimensionCandidate), targetTable, connectionProvider, sqlEvaluatorFactory);
		
	}


	private ValidityResult wrapValidation() throws WorkerException, OperationAbortedException{
		try{
			WorkerWrapper<ValidationWorker, ValidityResult> wrapper=this.createWorkerWrapper(validatorFactory);
			HashMap<String,Object> map=new HashMap<String,Object>();

			Expression toCheck=ValidateDimensionColumnFactory.generateValidationExpression(getSourceInvocation(), cubeManager);
			map.put(ValidateDataWithExpressionFactory.EXPRESSION_PARAMETER.getIdentifier(), toCheck);
			map.put(ValidateDataWithExpressionFactory.DESCRIPTION_PARAMETER.getIdentifier(), "External reference check");
			map.put(ValidateDataWithExpressionFactory.VALIDATION_CODE_PARAMETER.getIdentifier(), "104");


			wrapper.execute(targetTable.getId(), null, map);
			return wrapper.getResult();
		}catch(InvalidInvocationException e){
			throw new WorkerException("Unable to wrap validator",e);
		}
	}

	private void restoreHarmonizedValues() throws WorkerException{
		String restoreCommand="DO $$DECLARE r record; " +
				"BEGIN" +
				" FOR r in SELECT * FROM "+appTable.getName()+
				"  LOOP " +
				"   UPDATE "+targetTable.getName()+" SET "+dimensionCandidate.getName()+" = r."+appColumn.getName()+" WHERE "+targetTable.getName()+".id = r.id;" +
				"   END LOOP;" +
				" END$$";
		try{
			SQLHelper.executeSQLCommand(restoreCommand, connectionProvider);
		}catch(SQLException e){
			throw new WorkerException("Unable to restore data",e);
		}
	}
}
