package org.gcube.data.analysis.tabulardata.operation.comet;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.harmonization.HarmonizationRule;
import org.gcube.data.analysis.tabulardata.model.metadata.table.HarmonizationRuleTable;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.comet.MappingParser.ParserConfiguration;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.EmptyType;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;

public class ImportCodelistMappingWorker extends MetadataWorker{


	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory evaluatorFactory;

	public ImportCodelistMappingWorker(OperationInvocation sourceInvocation,
			CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory evaluatorFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.evaluatorFactory = evaluatorFactory;
	}

	private Table targetCodelist;
	private Table previousVersionCodelist=null;
	private Column oldCodesColumn=null;
	private String mappingUri=null;


	
	@Override
	protected EmptyType execute() throws WorkerException {
		updateProgress(0.1f,"Initializing..");
		targetCodelist=cubeManager.getTable(getSourceInvocation().getTargetTableId());
		ColumnReference oldCodesReference= OperationHelper.getParameter(ImportCodeListMappingFactory.PREVIOUS_VERSION_CODELIST_PARAMETER, getSourceInvocation());
		previousVersionCodelist=cubeManager.getTable(oldCodesReference.getTableId());
		oldCodesColumn=previousVersionCodelist.getColumnById(oldCodesReference.getColumnId());
		
		mappingUri=OperationHelper.getParameter(ImportCodeListMappingFactory.ID_PARAMETER, getSourceInvocation());
		
		
	
		
		ParserConfiguration config=new ParserConfiguration(previousVersionCodelist, targetCodelist,oldCodesColumn);
		try{
			updateProgress(0.3f,"Parsing mapping..");
			MappingParser parser=new MappingParser(config, evaluatorFactory, cubeManager, connectionProvider);
			parser.parse(mappingUri);
			Table rulesTable=parser.getRulesTable();
			updateProgress(0.8f,"Attaching rules to codelist..");			
			try{
				//If already existing rules then insert generated one in existing
				HarmonizationRuleTable existingMeta=targetCodelist.getMetadata(HarmonizationRuleTable.class);
				
				Table existingRules=existingMeta.getRulesTable();
				String colSnippet=HarmonizationRule.ENABLED+","+
						HarmonizationRule.REFERRED_CODELIST_COLUMN+","+
						HarmonizationRule.TO_CHANGE_VALUE_DESCRIPTION+","+
						HarmonizationRule.TO_CHANGE_VALUE_FIELD+","+
						HarmonizationRule.TO_SET_VALUE_DESCRIPTION+","+
						HarmonizationRule.TO_SET_VALUE_FIELD;
				
				
				String sqlCommand=String.format("Insert into %1$s (%2$s) (SELECT %2$s FROM %3$s)",
						existingRules.getName(),colSnippet,rulesTable.getName());
				
				SQLHelper.executeSQLCommand(sqlCommand, connectionProvider);
			}catch(NoSuchMetadataException e){
				targetCodelist=cubeManager.modifyTableMeta(targetCodelist.getId()).setTableMetadata(new HarmonizationRuleTable(rulesTable)).create();
			}
			
			
			
			return EmptyType.instance();
		}catch(Exception e){
			throw new WorkerException("Processing failed "+e.getMessage());
		}
	}

}
