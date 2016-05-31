package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.expression.logical.IsNotNull;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerWrapper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodelistValidator extends ValidationWorker {

	private static final Logger log = LoggerFactory.getLogger(CodelistValidator.class);

	@SuppressWarnings("rawtypes")
	private static final Class[] validColumnTypes=new Class[]{
		CodeColumnType.class,		
		CodeNameColumnType.class,
		CodeDescriptionColumnType.class,
		AnnotationColumnType.class,
		IdColumnType.class,
		ValidationColumnType.class
	};


	// Factories


	private DuplicateValuesInColumnValidatorFactory duplicateInColumnFactory;
	private ValidateDataWithExpressionFactory validateDataWithExpressionFactory;
	private DuplicateRowValidatorFactory duplicateRowsFactory;

	private List<ValidationDescriptor> descriptors = new ArrayList<>();


	CubeManager cubeManager;

	DatabaseConnectionProvider connectionProvider;

	Table targetTable;


	List<Validation> toSetValidations;
	HashMap<ColumnLocalId,List<Validation>> columnValidations=new HashMap<ColumnLocalId, List<Validation>>();

	public CodelistValidator(OperationInvocation sourceInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			DuplicateValuesInColumnValidatorFactory duplicateInColumnFactory,
			ValidateDataWithExpressionFactory validateWithExpressionFactory,
			DuplicateRowValidatorFactory duplicateRowFactory) {
		super(sourceInvocation);		
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.duplicateInColumnFactory=duplicateInColumnFactory;
		this.validateDataWithExpressionFactory=validateWithExpressionFactory;
		this.duplicateRowsFactory=duplicateRowFactory;		
	}

	@Override
	protected ValidityResult execute() throws WorkerException, OperationAbortedException {	
		retrieveTargetTable();
		updateProgress(0.1f,"Checking schema constraints");
		
		
		//perform metadata checks
		log.debug("Checking metadata constraints for table "+targetTable.getId());
		initializeValidationsMetadata();
		boolean uniqueCodeColumn = checkExistingUniqueCodeColumn();
		boolean existingCodeName = checkExistingCodeNameColumn();
		boolean duplicateCodeNameLocales = checkDuplicateCodeNameDataLocales();
		boolean invalidColumnType = checkInvalidColumnTypes();
		boolean checkLocaleAndLabels = checkLocaleAndLabels();
		log.debug("Unique code column : "+uniqueCodeColumn);			
		log.debug("Existing code name column : "+existingCodeName);
		log.debug("Duplicate locales : "+duplicateCodeNameLocales);
		log.debug("Allowed column types only : "+invalidColumnType);
		log.debug("Needed locale and labels : "+checkLocaleAndLabels);

		updateProgress(0.4f,"Checking duplicates ind code columns");		
			
		boolean result = uniqueCodeColumn && existingCodeName && duplicateCodeNameLocales 
				&& invalidColumnType && checkLocaleAndLabels; 
		
		// Data validation
		long start = System.currentTimeMillis();
		log.debug("Validating duplicates in code columns..");
		checkAborted();
		result &= checkDuplicatesInCodeColumn();
		log.debug("Validated duplicates in code columns.. "+result+" "+(System.currentTimeMillis()-start)+" millis");
		updateProgress(0.5f,"Checking null values in code columns");
		log.debug("Validating not null in code columns ...");
		start = System.currentTimeMillis();
		checkAborted();
		result &= checkNullValuesInCodeColumn();
		log.debug("Validated not null in code columns ..."+(System.currentTimeMillis()-start)+" millis");
		updateProgress(0.6f,"Checkign name presence");
		log.debug("Validating name presence in each tuple...");
		start = System.currentTimeMillis();
		checkAborted();
		result &=  checkNamePresenceInEachTuple();
		log.debug("Validated name presence in each tuple..."+(System.currentTimeMillis()-start)+" millis");
		/*		updateProgress(0.7f,"Checking tuple uniqueness");
		log.debug("Validating tuple uniqueness... ");
		start = System.currentTimeMillis();
		checkDuplicates();
		log.debug("Validated tuple uniqueness... "+(System.currentTimeMillis()-start)+" millis");*/
		updateProgress(0.8f,"Evaluating result");

		log.debug("Evaluating global validation..");
		start = System.currentTimeMillis();
		checkAborted();
		result &=  evaluateGlobal();
		log.debug("Evaluated global validation.."+(System.currentTimeMillis()-start)+" millis");
		//TODO Not sure if to apply
		//checkDuplicatesInAnyNames();
		createMetaValidatedTable();

		return new ValidityResult(result, descriptors);
	}

	private void retrieveTargetTable() {
		targetTable = cubeManager.getTable(getSourceInvocation().getTargetTableId());
	}

	private void initializeValidationsMetadata(){
		toSetValidations=getToEnrichTableMetaValidations(targetTable);
	}

	//********************* Metadata Validation


	private boolean checkExistingUniqueCodeColumn(){
		List<Column> codeColumns=targetTable.getColumnsByType(CodeColumnType.class);
		int codeColumnCount=codeColumns.size();
		boolean valid= codeColumnCount==1;
		if(!valid){
			for(Column col:codeColumns)
				addColumnValidation("Duplicated Code column",valid,col,1);
		}
		toSetValidations.add(new Validation("One and only code column", valid, 1));
		descriptors.add(new ValidationDescriptor(valid, "Must have one and only code column",1));
		return valid;
	}

	private boolean checkExistingCodeNameColumn(){
		List<Column> codeNameColumns=targetTable.getColumnsByType(CodeNameColumnType.class);		
		boolean valid=codeNameColumns.size()>0;
		toSetValidations.add(new Validation("At least one codename columns", valid,2));
		descriptors.add(new ValidationDescriptor(valid, "Must have at least one codename columns",2));
		return valid;
	}

	private boolean checkDuplicateCodeNameDataLocales(){
		List<Column> codeNameColumns=targetTable.getColumnsByType(CodeNameColumnType.class);

		// Construct locale map
		HashMap<String,List<Column>> codeNamesLocales=new HashMap<String,List<Column>>();
		for(Column c:codeNameColumns)
			if (c.contains(DataLocaleMetadata.class)){
				String locale=c.getMetadata(DataLocaleMetadata.class).getLocale();
				if(!codeNamesLocales.containsKey(locale))
					codeNamesLocales.put(locale, new ArrayList<Column>());
				codeNamesLocales.get(locale).add(c);
			}


		//******** Check locale duplicates

		boolean valid=true;
		for(Entry<String,List<Column>> entry:codeNamesLocales.entrySet()){
			boolean duplicate=entry.getValue().size()>1;			
			if(duplicate)valid=false;			
			for(Column col:entry.getValue())
				addColumnValidation("Code column with unique locale",!duplicate,col,3);
		}

		toSetValidations.add(new Validation("At most one CodeName column for each data locale", valid,3));
		descriptors.add(new ValidationDescriptor(valid, "Must have at most one CodeName column for each data locale",3));
		return valid;
	}

	private boolean checkInvalidColumnTypes(){
		boolean globalValid=true;
		for(Column col:targetTable.getColumnsByType(validColumnTypes))
			addColumnValidation("Allowed column type ", true, col,2);

		List<Column> invalidCols=targetTable.getColumnsExceptTypes(validColumnTypes);
		globalValid=invalidCols.isEmpty();
		if(!globalValid)
			for(Column col:invalidCols) addColumnValidation("Allowed column type ", false, col,4);


		toSetValidations.add(new Validation("Contains only columns of type Code, CodeName, CodeDescription and Annotation", globalValid, 4));
		descriptors.add(new ValidationDescriptor(globalValid, "Contains only columns of type Code, CodeName, CodeDescription and Annotation",4));
		return globalValid;
	}



	private boolean checkLocaleAndLabels(){
		boolean globalValid=true;
		Class[] toCheckColumnTypes=new Class[]{
				CodeNameColumnType.class,
				CodeDescriptionColumnType.class,
				AnnotationColumnType.class,
		};

		for(Column col:targetTable.getColumnsByType(toCheckColumnTypes)){
			boolean validColumn=checkLocaleAndLabels(col);
			if(!validColumn) globalValid=false;
			addColumnValidation("Must have Data locale metadata and at least one label", validColumn, col,5);
		}

		toSetValidations.add(new Validation("Each CodeName, CodeDescription and Annotation column with DataLocale and at least one label",globalValid, 5));
		descriptors.add(new ValidationDescriptor(globalValid, "Each CodeName, CodeDescription and Annotation column with DataLocale and at least one label",5));
		return globalValid;
	}



	//**************** Data Validation

	private boolean checkDuplicatesInCodeColumn() throws WorkerException, OperationAbortedException{
		List<Column> toCheckColumns=targetTable.getColumnsByType(CodeColumnType.class);
		WorkerWrapper<ValidationWorker, ValidityResult> wrapper=this.createWorkerWrapper(duplicateInColumnFactory);
		boolean toReturn = true;
		for(Column col : toCheckColumns){
			try{
				WorkerStatus status=wrapper.execute(targetTable.getId(), col.getLocalId(), null);
				processStep(status);
				toReturn &= wrapper.getResult().isValid();
				descriptors.addAll(wrapper.getResult().getValidationDescriptors());
			}catch(InvalidInvocationException e){
				throw new WorkerException("Unable to execute wrapped worker ",e);
			}
		}
		return toReturn;
	}

	private boolean checkNullValuesInCodeColumn() throws WorkerException, OperationAbortedException{
		List<Column> toCheckColumns=targetTable.getColumnsByType(CodeColumnType.class);

		WorkerWrapper<ValidationWorker, ValidityResult> wrapper=this.createWorkerWrapper(validateDataWithExpressionFactory);
		boolean toReturn = true;
		for(Column col : toCheckColumns){
			try{
				//Form Expression
				ColumnReference targetColumnReference =  new ColumnReference(targetTable.getId(), col.getLocalId());
				IsNotNull condition=new IsNotNull(targetColumnReference);
				HashMap<String,Object> map=new HashMap<String,Object>();
				map.put(ValidateDataWithExpressionFactory.EXPRESSION_PARAMETER.getIdentifier(), condition);
				WorkerStatus status=wrapper.execute(targetTable.getId(), col.getLocalId(), map);
				processStep(status);
				toReturn &= wrapper.getResult().isValid();
				descriptors.addAll(wrapper.getResult().getValidationDescriptors());
			}catch(InvalidInvocationException e){
				throw new WorkerException("Unable to execute wrapped worker ",e);
			}
		}	
		return toReturn;
	}

	private boolean checkNamePresenceInEachTuple() throws WorkerException, OperationAbortedException{
		// Form Expression nameX isnot null || name Y is not null etc...

		List<Column> toCheckColumns=targetTable.getColumnsByType(CodeNameColumnType.class);
		List<Expression> orArguments=new ArrayList<Expression>();
		for(Column col : toCheckColumns){
			ColumnReference targetColumnReference =  new ColumnReference(targetTable.getId(), col.getLocalId());
			IsNotNull condition=new IsNotNull(targetColumnReference);
			orArguments.add(condition);
		}

		//Checking number of found or parameters
		Expression toApplyCondition=null;
		if(orArguments.size()==0){
			log.debug("The table hasn't codenames columns");
			return true;
		}
		else {
			if (orArguments.size()==1) toApplyCondition=orArguments.get(0);
			else toApplyCondition=new Or(orArguments);
			try{
				WorkerWrapper<ValidationWorker, ValidityResult> wrapper=this.createWorkerWrapper(validateDataWithExpressionFactory);
				HashMap<String,Object> map=new HashMap<String,Object>();
				map.put(ValidateDataWithExpressionFactory.EXPRESSION_PARAMETER.getIdentifier(), toApplyCondition);
				map.put(ValidateDataWithExpressionFactory.DESCRIPTION_PARAMETER.getIdentifier(), "Each tuple contains at least one code name");
				map.put(ValidateDataWithExpressionFactory.VALIDATION_TITLE_PARAMETER.getIdentifier(), "Code Name Presence");
				map.put(ValidateDataWithExpressionFactory.VALIDATION_CODE_PARAMETER.getIdentifier(), "105");
				WorkerStatus status=wrapper.execute(targetTable.getId(), null, map);
				processStep(status);
				descriptors.addAll(wrapper.getResult().getValidationDescriptors());
				return wrapper.getResult().isValid();
			}catch(InvalidInvocationException e){
				throw new WorkerException("Unable to execute wrapped worker ",e);
			}
		}
	}

	/*	private void checkDuplicates() throws WorkerException{
		try{
			WorkerWrapper<ValidationWorker, EmptyType> wrapper=new WorkerWrapper<ValidationWorker, EmptyType>(duplicateRowsFactory);
			WorkerStatus status=wrapper.execute(targetTable.getId(), null, null);
			processStep(status);
		}catch(InvalidInvocationException e){
			throw new WorkerException("Unable to execute wrapped worker ",e);
		}
	}

	private void checkDuplicatesInAnyNames() throws WorkerException{

	}
	 */

	private boolean evaluateGlobal()throws WorkerException, OperationAbortedException{
		List<Expression> andArguments=new ArrayList<>();
		if (targetTable.getColumnsByType(ValidationColumnType.class).size()>0){
			for(Column col:targetTable.getColumnsByType(ValidationColumnType.class))
				andArguments.add(new Equals(targetTable.getColumnReference(col), new TDBoolean(true)));
			try{
				WorkerWrapper<ValidationWorker, ValidityResult> wrapper=this.createWorkerWrapper(validateDataWithExpressionFactory);
				HashMap<String,Object> map=new HashMap<String,Object>();
				map.put(ValidateDataWithExpressionFactory.EXPRESSION_PARAMETER.getIdentifier(), new And(andArguments));
				map.put(ValidateDataWithExpressionFactory.DESCRIPTION_PARAMETER.getIdentifier(), "All tuple are valid");
				map.put(ValidateDataWithExpressionFactory.VALIDATION_TITLE_PARAMETER.getIdentifier(), "Global validation");
				map.put(ValidateDataWithExpressionFactory.VALIDATION_CODE_PARAMETER.getIdentifier(), "100");
				WorkerStatus status=wrapper.execute(targetTable.getId(), null, map);
				processStep(status);
				descriptors.addAll(wrapper.getResult().getValidationDescriptors());
				return wrapper.getResult().isValid();
			}catch(InvalidInvocationException e){
				throw new WorkerException("Unable to execute wrapped worker ",e);
			}
		}
		else return true;
	}

	//***************** Instance misc


	private void addColumnValidation(String msg, boolean valid, Column col, int code){

		ColumnLocalId id=col.getLocalId();
		if(!columnValidations.containsKey(id)){
			columnValidations.put(id, getToEnrichColumnMetaValidations((targetTable.getColumnById(id))));
		}
		columnValidations.get(id).add(new Validation(msg,valid, code));

	}



	private Table createMetaValidatedTable(){		
		TableMetaCreator tmc =cubeManager.modifyTableMeta(targetTable.getId());		
		tmc.setTableMetadata(new ValidationsMetadata(toSetValidations));
		for(Entry<ColumnLocalId,List<Validation>> entry:columnValidations.entrySet()){
			ValidationsMetadata columnValidMeta=new ValidationsMetadata(entry.getValue());			
			tmc.setColumnMetadata(entry.getKey(), columnValidMeta);
		}
		return tmc.create();
	}

	/**
	 * Updates operating table depending on wrapped worker
	 * 
	 */

	private void processStep(WorkerStatus status)throws WorkerException{
		if(!status.equals(WorkerStatus.SUCCEDED))
			throw new WorkerException("Wrapped step has failed, see previous log");	
		targetTable = cubeManager.getTable(targetTable.getId());
	}


	//************* Static misc

	private static List<Validation> getToEnrichTableMetaValidations(Table table){
		List<Validation> foundValidations=new ArrayList<Validation>();		
		try{
			ValidationsMetadata validationsMetadata = table.getMetadata(ValidationsMetadata.class);
			foundValidations.addAll(validationsMetadata.getValidations());			
		}catch(NoSuchMetadataException e){
			log.debug("No validation metadata found, returned empty List");
		}
		return foundValidations;
	}	

	private static List<Validation> getToEnrichColumnMetaValidations(Column column){
		List<Validation> foundValidations=new ArrayList<Validation>();
		try{
			ValidationsMetadata validationsMetadata = column.getMetadata(ValidationsMetadata.class);
			foundValidations.addAll(validationsMetadata.getValidations());			
		}catch(NoSuchMetadataException e){
			log.debug("No validation metadata found, returned empty List");
		}
		return foundValidations;
	}

	private static boolean checkLocaleAndLabels(Column col){
		try {
			col.getMetadata(DataLocaleMetadata.class);
			return !col.getMetadata(NamesMetadata.class).getTexts().isEmpty();
		} catch (Exception e) {
			return false;
		}
	}

}
