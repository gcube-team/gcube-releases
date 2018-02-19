package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.DatasetTableType;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public class ValidateDataset extends ValidationWorker {

	private CubeManager cube;
	
	private List<ColumnType> allowedColumnTypes =new DatasetTableType().getAllowedColumnTypes();
	
	private HashMap<ColumnLocalId,List<Validation>> validationMap=new HashMap<>();
	
		
	public ValidateDataset(OperationInvocation sourceInvocation,
			CubeManager cube) {
		super(sourceInvocation);
		this.cube = cube;
	}

	Table targetTable;


	@Override
	protected ValidityResult execute() throws WorkerException {
		Table targetTable=cube.getTable(getSourceInvocation().getTargetTableId());
		
		updateProgress(0.1f,"Checking allowed column types");
		
		List<ValidationDescriptor> validationDescriptors = new ArrayList<>();
		
		boolean allowedGlobalValid=true;
		for(Column col:targetTable.getColumns()){
			boolean allowed=allowedColumnTypes.contains(col.getColumnType());
			if(!allowed)allowedGlobalValid=false;
			addValidation(col,new Validation("Allowed column type ",allowed, 4));
		}
		
		Validation allowed=new Validation("Allowed column types", allowedGlobalValid, 4);
		validationDescriptors.add(new ValidationDescriptor(allowedGlobalValid, "Allowed column types", 4));
		
		updateProgress(0.5f,"Checking mandatory columns");
		boolean mandatoryDimension=targetTable.getColumnsByType(TimeDimensionColumnType.class,DimensionColumnType.class).size()>0;
		Validation mandatoryDim=new Validation("Must contain at least one Dimension",mandatoryDimension, 6);
		validationDescriptors.add(new ValidationDescriptor(mandatoryDimension, "Must contain at least one Dimension", 6));
		
		boolean mandatoryMeasure=targetTable.getColumnsByType(MeasureColumnType.class).size()>0;
		Validation mandatoryMeas=new Validation("Must contain at least one Measure",mandatoryMeasure,7);
		validationDescriptors.add(new ValidationDescriptor(mandatoryMeasure, "Must contain at least one Measure", 7));
		
		updateProgress(0.8f,"Finalizing validations");
		
		TableMetaCreator creator=cube.modifyTableMeta(targetTable.getId());
		for(Entry<ColumnLocalId,List<Validation>> entry: validationMap.entrySet())
			creator.setColumnMetadata(entry.getKey(), new ValidationsMetadata(entry.getValue()));
		
		creator.setTableMetadata(new ValidationsMetadata(Arrays.asList(new Validation[]{
				allowed,mandatoryDim,mandatoryMeas
		})));		
		creator.create();
		return new ValidityResult(allowedGlobalValid && mandatoryDimension && mandatoryMeasure, validationDescriptors);		
	}

	
	private void addValidation(Column col,Validation toAdd){
		if(!validationMap.containsKey(col.getLocalId())){
			ArrayList<Validation> validations=new ArrayList<>();
			try{
				validations.addAll(col.getMetadata(ValidationsMetadata.class).getValidations());
			}catch(NoSuchMetadataException e){}
			validationMap.put(col.getLocalId(), validations);
		}
		validationMap.get(col.getLocalId()).add(toAdd);
	}
}
