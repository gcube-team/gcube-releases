package org.gcube.data.analysis.tabulardata.statistical.specific;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.resources.TableResource;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.data.add.AddColumnFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerWrapper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.gcube.data.analysis.tabulardata.statistical.Common;
import org.gcube.data.analysis.tabulardata.statistical.Constants;
import org.gcube.data.analysis.tabulardata.statistical.ExportToStatisticalOperationFactory;
import org.gcube.data.analysis.tabulardata.statistical.StatisticalOperationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnhanceLatLong extends DataWorker {

	private static Logger logger = LoggerFactory.getLogger(EnhanceLatLong.class);

	//constructor
	private StatisticalOperationFactory statisticalFactory;
	private AddColumnFactory addColumnFactory;
	private CubeManager cubeManager;





	public EnhanceLatLong(OperationInvocation sourceInvocation,
			StatisticalOperationFactory statisticalFactory,
			AddColumnFactory addColumnFactory, CubeManager cubeManager) {
		super(sourceInvocation);
		this.statisticalFactory = statisticalFactory;
		this.addColumnFactory = addColumnFactory;
		this.cubeManager = cubeManager;
	}

	//from invocation
	private Table targetTable;
	private Column latColumn;
	private Column longColumn;
	private Double precision;
	private LatLongFeature feature;
	private String userName;
	private Column quadrant;

	private boolean deleteRemote=false;
	private boolean deleteGenerated=false;

	//generated
	private Map<String,Object> wrapperParameters=new HashMap<>();
	private Table generatedTable;
	private Table resultTable;
	private String exportedIdColumnLabel;
	private ColumnType importedColumnType;
	private Table enhancedTable;
	private String toEscapeColumnName=null;
	
	
	
	@Override
	protected WorkerResult execute() throws WorkerException,OperationAbortedException{
		try{
			updateProgress(0.1f, "Initializing");
			init();
			updateProgress(0.2f,"Preparing data");
			prepareData();
			checkAborted();
			prepareSMParameters();
			updateProgress(0.3f, "Executing staistical algorithm");
			checkAborted();
			submitExecution();
			updateProgress(0.8f, "Merging result..");
			checkAborted();
			addColumn();		
			return new ImmutableWorkerResult(resultTable);
		}catch(WorkerException e){
			throw e;
		}catch(OperationAbortedException e){
			throw e;
		}catch(Exception e){
			throw new WorkerException("Unexpected internal error. Please contact support",e);
		}	}

	private void init(){
		targetTable=cubeManager.getTable(getSourceInvocation().getTargetTableId());
		if(targetTable.getTableType().getAllowedColumnTypes().contains(new AnnotationColumnType()))
			importedColumnType=new AnnotationColumnType();
		else importedColumnType=new AttributeColumnType();

		latColumn=targetTable.getColumnById(OperationHelper.getParameter(EnhanceLatLonFactory.LATITUDE_COLUMN_PARAM, getSourceInvocation()).getColumnId());
		longColumn=targetTable.getColumnById(OperationHelper.getParameter(EnhanceLatLonFactory.LONGITUTE_COLUMN_PARAM, getSourceInvocation()).getColumnId());
		try{
			precision=((TDNumeric)OperationHelper.getParameter(EnhanceLatLonFactory.RESOLUTION_PARAM, getSourceInvocation())).getValue();
		}catch(Throwable t){
			precision=null;
		}

		try{
			quadrant=targetTable.getColumnById(OperationHelper.getParameter(EnhanceLatLonFactory.QUADRANT_COLUMN_PARAMETER, getSourceInvocation()).getColumnId());
		}catch(Throwable t){
			quadrant=null;
		}

		feature=LatLongFeature.valueOf(OperationHelper.getParameter(EnhanceLatLonFactory.TO_ADD_FEATURE_PARAM, getSourceInvocation()));
		userName=OperationHelper.getParameter(EnhanceLatLonFactory.USER, getSourceInvocation());

		try{
			deleteRemote=OperationHelper.getParameter(EnhanceLatLonFactory.DELETE_REMOTE, getSourceInvocation());
		}catch(Throwable t){
			//not specified
		}

		try{
			deleteGenerated=OperationHelper.getParameter(EnhanceLatLonFactory.DELETE_GENERATED, getSourceInvocation());
		}catch(Throwable t){
			//not specified
		}
		
		
		switch(feature){
		case CSQUARECODE : toEscapeColumnName=Constants.CSQUARE_CODE_COLUMN;
			break;
		case OCEANAREA : toEscapeColumnName=Constants.OCEAN_AREA_COLUMN;
		}
	}


	private void prepareData() throws OperationAbortedException, WorkerException, ParseException, IOException, ProcessingException{
		Column idColumn=targetTable.getColumnsByType(IdColumnType.class).get(0);
		exportedIdColumnLabel=Common.fixColumnName(idColumn.getLocalId().getValue());
		Map<String,Object> addColumnParameters=new HashMap<String,Object>();
		addColumnParameters.put(AddColumnFactory.COLUMN_TYPE.getIdentifier(), importedColumnType);
		addColumnParameters.put(AddColumnFactory.DATA_TYPE.getIdentifier(), idColumn.getDataType());
		addColumnParameters.put(AddColumnFactory.LABEL.getIdentifier(), new ImmutableLocalizedText(idColumn.getLocalId().getValue()) );
		addColumnParameters.put(AddColumnFactory.VALUE_PARAMETER.getIdentifier(), targetTable.getColumnReference(idColumn));		
		WorkerWrapper<DataWorker,WorkerResult> wrapper=this.createWorkerWrapper(addColumnFactory);
		try{
			WorkerStatus status=wrapper.execute(targetTable.getId(), null, addColumnParameters);
			if(status.equals(WorkerStatus.SUCCEDED)){
				enhancedTable=wrapper.getResult().getResultTable();
			}else throw new WorkerException("Failed to prepare data : "+status);
		}catch(InvalidInvocationException e){
			throw new WorkerException("Unable to prepare data",e);
		}
	}


	private void prepareSMParameters() throws ParseException, IOException, ProcessingException{
		wrapperParameters.put(StatisticalOperationFactory.USER.getIdentifier(), userName);
		wrapperParameters.put(StatisticalOperationFactory.CLEAR_DATASPACE.getIdentifier(), deleteGenerated);
		wrapperParameters.put(StatisticalOperationFactory.REMOVE_EXPORTED.getIdentifier(), deleteRemote);
		Map<String,Object> algorithmParameters=new HashMap<>();
		switch (feature) {
		case CSQUARECODE:
			wrapperParameters.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), Constants.CSQUARE_ALGORITHM);
			wrapperParameters.put(ExportToStatisticalOperationFactory.toEscapeFieldNamesParam.getIdentifier(), Constants.CSQUARE_CODE_COLUMN);
			algorithmParameters.put("InputTable", enhancedTable.getId().toString());
			algorithmParameters.put("Longitude_Column", Common.fixColumnName(longColumn));
			algorithmParameters.put("Latitude_Column",Common.fixColumnName(latColumn));
			algorithmParameters.put("CSquare_Resolution", (precision!=null?precision:new Float(0.1))+"");
			algorithmParameters.put("OutputTableName", "csquare_");
			break;

		case OCEANAREA:
			wrapperParameters.put(ExportToStatisticalOperationFactory.toEscapeFieldNamesParam.getIdentifier(), Constants.OCEAN_AREA_COLUMN);
			if(quadrant==null)wrapperParameters.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), Constants.OCEAN_AREA_ALGORITHM);
			else{
				wrapperParameters.put(StatisticalOperationFactory.ALGORITHM.getIdentifier(), Constants.OCEAN_AREA_QUADRANT_ALGORITHM);
				algorithmParameters.put("Quadrant_Column",Common.fixColumnName(quadrant));
			}
			algorithmParameters.put("InputTable", enhancedTable.getId().toString());
			algorithmParameters.put("Longitude_Column", Common.fixColumnName(longColumn));
			algorithmParameters.put("Latitude_Column",Common.fixColumnName(latColumn));
			algorithmParameters.put("OutputTableName", "ocean_");
			Integer resolution=(precision!=null)?(precision >0?precision.intValue():1):1;
			algorithmParameters.put("Resolution",resolution.toString());
		default:

			break;
		}

		wrapperParameters.put(StatisticalOperationFactory.SM_ENTRIES.getIdentifier(), algorithmParameters);

	}

	private void submitExecution() throws OperationAbortedException, WorkerException{
		WorkerWrapper<ResourceCreatorWorker, ResourcesResult> wrapper=this.createWorkerWrapper(statisticalFactory);		
		try{
			WorkerStatus status=wrapper.execute(enhancedTable.getId(), null, wrapperParameters);
			if(status.equals(WorkerStatus.SUCCEDED)){
				for(ResourceDescriptorResult result:wrapper.getResult().getResources()){
					if(result.getResourceType().equals(ResourceType.GENERIC_TABLE)) generatedTable=cubeManager.getTable(((TableResource)result.getResource()).getTableId());
				}
				if(generatedTable==null) throw new WorkerException("No tables were generated by submitted computation");
			}else throw new WorkerException("Failed algorithm execution, internal status is :"+status);
		}catch(InvalidInvocationException e){
			logger.debug("Unable to submit to SM : ",e);
			throw new WorkerException("Unable to submit computation, "+e.getMessage());
		}
		cubeManager.removeTable(enhancedTable.getId());
	}

	private void addColumn() throws WorkerException, OperationAbortedException, ParseException, IOException, ProcessingException{		
		Column generatedCol=null;
		Column externalIdColumn=null;
//		List<Column> existingColumns=targetTable.getColumnsExceptTypes(IdColumnType.class);
		Collection<String> curatedLabels=Common.curateLabels(targetTable, toEscapeColumnName).values();
		
		
		for(Column externalCol:generatedTable.getColumnsExceptTypes(IdColumnType.class)){
			boolean isGenerated=true;
			String externalLabel=OperationHelper.retrieveColumnLabel(externalCol);
			if(externalLabel.equalsIgnoreCase(exportedIdColumnLabel))
				externalIdColumn=externalCol;
			else{
				
				if(curatedLabels.contains(externalLabel))
					isGenerated=false;					
				else generatedCol=externalCol;
//				
//				for(Column internalCol:existingColumns){ // check if already present
//					if(Common.fixColumnName(internalCol,()).equalsIgnoreCase(externalLabel)){
//						// if found add to condition for join
//						isGenerated=false;					
//						//					andArguments.add(new Equals(targetTable.getColumnReference(internalCol), new Cast(generatedTable.getColumnReference(externalCol),internalCol.getDataType())));
//						break;
//					}
//				}
//				if(isGenerated)	generatedCol=externalCol;
			}
		}

		Column localId=targetTable.getColumnsByType(IdColumnType.class).get(0);
		Expression condition=new Equals(targetTable.getColumnReference(localId),new Cast(generatedTable.getColumnReference(externalIdColumn), localId.getDataType()));


		Map<String,Object> addColumnParameters=new HashMap<String,Object>();
		addColumnParameters.put(AddColumnFactory.COLUMN_TYPE.getIdentifier(), importedColumnType);
		addColumnParameters.put(AddColumnFactory.DATA_TYPE.getIdentifier(), generatedCol.getDataType());
		addColumnParameters.put(AddColumnFactory.LABEL.getIdentifier(), new ImmutableLocalizedText(OperationHelper.retrieveColumnLabel(generatedCol)+(precision!=null?precision:"")));
		addColumnParameters.put(AddColumnFactory.VALUE_PARAMETER.getIdentifier(), generatedTable.getColumnReference(generatedCol));
		addColumnParameters.put(AddColumnFactory.CONDITION_PARAMETER.getIdentifier(), condition);
		WorkerWrapper<DataWorker,WorkerResult> wrapper=this.createWorkerWrapper(addColumnFactory);
		try{
			WorkerStatus status=wrapper.execute(targetTable.getId(), null, addColumnParameters);
			if(status.equals(WorkerStatus.SUCCEDED)){
				resultTable=wrapper.getResult().getResultTable();
			}else throw new WorkerException("Unexpected worker status while adding column : "+status);
		}catch(InvalidInvocationException e){
			throw new WorkerException("Unable to add generatedColumn",e);
		}

		cubeManager.removeTable(generatedTable.getId());
	}

}
