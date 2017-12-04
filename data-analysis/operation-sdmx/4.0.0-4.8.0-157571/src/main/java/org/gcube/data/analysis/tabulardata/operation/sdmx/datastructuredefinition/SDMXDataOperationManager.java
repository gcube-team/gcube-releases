package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.AttributeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.MeasureColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.relationship.ColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.codelist.SDMXCodelistGenerator;
import org.gcube.data.analysis.tabulardata.operation.sdmx.conceptscheme.SDMXConceptSchemeGenerator;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataResultBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataResultBean.RESULT;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.resource.SDMXDataStructureDefinitionResourceBuilder;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.sdmxsource.sdmx.api.constants.ATTRIBUTE_ATTACHMENT_LEVEL;
import org.sdmxsource.sdmx.api.constants.SDMX_STRUCTURE_TYPE;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DimensionBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.PrimaryMeasureBean;
import org.sdmxsource.sdmx.api.model.beans.reference.StructureReferenceBean;
import org.sdmxsource.sdmx.api.model.mutable.base.RepresentationMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.base.TextTypeWrapperMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.codelist.CodelistMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptSchemeMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.datastructure.AttributeMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.datastructure.DataStructureMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.datastructure.DataflowMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.datastructure.DimensionMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.datastructure.PrimaryMeasureMutableBean;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.RepresentationMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.TextTypeWrapperMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.datastructure.AttributeMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.datastructure.DataStructureMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.datastructure.DimensionMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.datastructure.PrimaryMeasureMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.metadatastructure.DataflowMutableBeanImpl;
import org.sdmxsource.sdmx.util.beans.reference.StructureReferenceBeanImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class SDMXDataOperationManager  extends ResourceCreatorWorker {

	private class Parameters
	{
		String 			targetAgency, 
							targetId,
							targetVersion;
		Column primaryMeasure;
	}

	
	
	private Logger log;
	private Table table;
	private OperationInvocation invocation;
	private CubeManager cubeManager;
//	private ;
	private final String ATTRIBUTE_ASSIGNMENT_STATUS_MANDATORY = "Mandatory";
	private static String errorMessage = "Unable to complete export procedure";;
	private DatabaseConnectionProvider connectionProvider;
	private SDMXConceptSchemeGenerator conceptSchemeGenerator;
	private List<SDMXDataOperationExecutor> executors;
	private SDMXDataStructureDefinitionResourceBuilder resourceBuilder;
	


	
	
	SDMXDataOperationManager(Table table, DatabaseConnectionProvider connectionProvider, OperationInvocation invocation,CubeManager cubeManager,  List<SDMXDataOperationExecutor> executors, SDMXDataStructureDefinitionResourceBuilder resourceBuilder) 
	{
		super(invocation);
		this.log = LoggerFactory.getLogger(this.getClass());
		this.table = table;
		this.invocation = invocation;
		this.connectionProvider = connectionProvider;
		this.cubeManager = cubeManager;
		this.executors = executors;
		this.resourceBuilder = resourceBuilder;

	}
	
	/**
	 * 
	 */
	@Override
	protected ResourcesResult execute() throws WorkerException 
	{		

			this.log.debug("Init parameters and functionalities");
			Parameters parameters = getParameters(this.invocation);
			this.conceptSchemeGenerator = new SDMXConceptSchemeGenerator(this.table, parameters.targetId,parameters.targetAgency, parameters.targetVersion);
			List<LocalizedText> tableNamesMetadata =loadMetadata ();
			SDMXDataBean dataBean = generateSDMXDataBean(this.table, parameters.targetAgency, parameters.targetVersion, parameters.targetId, tableNamesMetadata);
			this.log.debug("Parameters and functionalities initialized");
			try 
			{
				updateProgress(0.1f,"Creating beans");
				ConceptSchemeMutableBean conceptScheme = conceptSchemeGenerator.createConceptSchemeBean();
				dataBean.setConcepts(conceptScheme);
				DataflowMutableBean dataFlow = createDataFlowBean(dataBean.getDsd(),tableNamesMetadata,parameters );
				dataBean.setDataFlow(dataFlow);			
				updateProgress(0.2f,"Populating data structure");
				populateDataStructure(dataBean,parameters);
				SDMXDataResultBean currentResult = new SDMXDataResultBean();
				Iterator<SDMXDataOperationExecutor> executorsIterator = this.executors.iterator();
				List<String> warnings = new LinkedList<>();
				updateProgress(0.4f,"Executing requested operations");	
				float currentStep = 0.4f;
				float step = getUpdateProgress(currentStep,this.executors.size());
				
				
				while (executorsIterator.hasNext() && currentResult.getResult() == RESULT.OK)
				{

					SDMXDataOperationExecutor executor = executorsIterator.next();
					updateProgress(currentStep+=step, "Executing "+executor.getOperationName());
					currentResult = executor.executeOperation(dataBean, this.invocation);
					
					if (currentResult.getResult() == RESULT.WARNING) warnings.add(currentResult.getMessage());
					
				}

					
				if (currentResult.getResult() == RESULT.ERROR && currentResult.getException() != null)
				{	
					this.log.error("Unable to complete the operation",currentResult.getException());
					throw new WorkerException(currentResult.getMessage(),currentResult.getException());
				}
				else if (currentResult.getResult() == RESULT.ERROR)
				{
					this.log.error("Unable to complete the operation");						
					throw new WorkerException(currentResult.getMessage());
				}

				if (warnings.size()>0)
				{
					// Warning message: to be completed when warning messages will be supported
					for (String warning : warnings) this.log.warn(warning);
				}

				updateProgress(0.9f,"Finalizing");
				
				return this.resourceBuilder.buildResourceResult(this.invocation, dataBean);
	
			} catch (RuntimeException e) {
				log.error(errorMessage, e);
				throw new WorkerException(errorMessage, e);
			}
	}
	
	private float getUpdateProgress (float startingPoint, int size)
	{
		float remainingSteps = 0.8f-startingPoint;
		return remainingSteps/size;
	}
	
	
	private SDMXDataBean generateSDMXDataBean(Table table, String targetAgency, String targetVersion,
		String targetId, List<LocalizedText> tableNamesMetadata) 
	{
		SDMXDataBean dataBean = new SDMXDataBean(table);
		DataStructureMutableBean dataStructureDefinition = new DataStructureMutableBeanImpl();
		dataStructureDefinition.setAgencyId(targetAgency);
		dataStructureDefinition.setVersion(targetVersion);
		String dsdId = targetId+"_DSD";
		dataStructureDefinition.setId(dsdId);
		
		try {
			List<TextTypeWrapperMutableBean> names = Lists.newArrayList();
			
			for (LocalizedText text : table.getMetadata(NamesMetadata.class).getTexts()) 
			{
				String locale = text.getLocale();
				String value = text.getValue();
				this.log.debug("Locale = "+locale);
				this.log.debug("Value "+value);
				names.add(new TextTypeWrapperMutableBeanImpl(locale, value));
			}
			dataStructureDefinition.setNames(names);
			this.log.debug("Names set");
			
		} catch (NoSuchMetadataException e) 
		{
			this.log.warn("Names Metadata not found in the table");
			dataStructureDefinition.setNames(getNamesMetadata(tableNamesMetadata,targetId+" Data Structure Definition", "en"));
			
		}

		dataBean.setDsd(dataStructureDefinition);
		dataBean.setID(targetId+"_"+targetVersion);
		return dataBean;

	}

	/**
	 * 
	 * @param invocation
	 */
	private Parameters getParameters (OperationInvocation invocation) 
	{
		Parameters parameters = new Parameters();

		parameters.targetAgency = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.AGENCY);
		parameters.targetId = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.ID);
		parameters.targetVersion = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.VERSION);
		String observationValue = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.OBS_VALUE_COLUMN);
		parameters.primaryMeasure = this.table.getColumnById(new ColumnLocalId(observationValue));
		return parameters;

	}


	/**
	 * 
	 * @param dsd
	 * @param concepts
	 * @param dataFlow
	 * @throws WorkerException
	 */
	


	private void registerTimeDimensionColumn(Column column, ConceptMutableBean concept, SDMXDataBean dataStructure) 
	{
		this.log.debug("Adding time dimension to excel table");
		dataStructure.setTimeDimension(column, concept);
		this.log.debug("Time dimension added");

	}


	private void registerDimensionColumn(Column column, ConceptMutableBean concept, CodelistBean immutableCodelist,
			SDMXDataBean dataStructure) {
		this.log.debug("Adding generic dimension to excel table");
		((SDMXDataBean)dataStructure).addDimensionColumn(column, concept, immutableCodelist);
		this.log.debug("Generic dimension added");


	}

	/**
	 * 
	 * @param dataStructure
	 * @param conceptScheme
	 * @param dataSourceConfigurationBean
	 * @param codelists
	 * @throws WorkerException
	 */
	@SuppressWarnings("unchecked")
	private void populateDataStructure (SDMXDataBean dataStructure,Parameters parameters ) throws WorkerException 
	{
		List<Column> columnsList = new ArrayList<>();
		columnsList.add(parameters.primaryMeasure);
		this.log.debug("Pupulating data structure");
		List<Column> measureColumns = new ArrayList<>();
		getMeasures(measureColumns, parameters);
		columnsList.addAll(measureColumns);
		this.log.debug("Measure columns loaded");
		addPrimaryMeasure(dataStructure, parameters);
		this.log.debug("Primary measure added");
		addMeasureDimensions(dataStructure, measureColumns, parameters);
		this.log.debug("Measure dimensions added");
		this.log.debug("Adding generic dimension bean");
		List<Column> dimensionColumns = this.table.getColumnsByType(DimensionColumnType.class);
		addGenericDimensions(dataStructure,dimensionColumns,parameters);
		columnsList.addAll(dimensionColumns);
		this.log.debug("Dimension columns added");
		Column timeDimensionColumn = this.table.getColumnsByType(TimeDimensionColumnType.class).get(0);
		addTimeDimension(dataStructure,timeDimensionColumn,parameters);
		columnsList.add(timeDimensionColumn);
		List<Column> attributeColumns = this.table.getColumnsByType(AttributeColumnType.class);
		addAttributes(dataStructure,attributeColumns,parameters);
		columnsList.addAll(attributeColumns);
		registerData(dataStructure, table, columnsList, connectionProvider);

	}
	
	/**
	 * 
	 * @param dataStructure
	 */
	private void addTimeDimension (SDMXDataBean dataStructure,Column timeDimensionColumn,Parameters parameters )
	{
		this.log.debug("Adding time dimension");
		ConceptSchemeMutableBean concepts = dataStructure.getConcepts();
		DimensionMutableBean timeDimensionBean = new DimensionMutableBeanImpl();
		timeDimensionBean.setId(DimensionBean.TIME_DIMENSION_FIXED_ID);
		ConceptMutableBean timeDimensionConcept = this.conceptSchemeGenerator.createConceptBean(timeDimensionColumn);
		concepts.addItem(timeDimensionConcept);
		timeDimensionBean.setConceptRef(getConceptReference(concepts, timeDimensionConcept,parameters.targetVersion));
		timeDimensionBean.setTimeDimension(true);
		dataStructure.getDsd().addDimension(timeDimensionBean);
		registerTimeDimensionColumn(timeDimensionColumn, timeDimensionConcept,dataStructure);
	}
	

	/**
	 * 
	 * @param dimensionListBean
	 * @param dimensionColumns
	 * @param concepts
	 */

	private void addGenericDimensions (SDMXDataBean dataStructure,List<Column> dimensionColumns,Parameters parameters )
	{
		ConceptSchemeMutableBean concepts = dataStructure.getConcepts();
		
		for (Column column : dimensionColumns)
		{
			ConceptMutableBean columnConcept = this.conceptSchemeGenerator.createConceptBean(column);
			concepts.addItem(columnConcept);
			CodelistBean immutableCodelist = createCodeListRepresentation(column,parameters);
			
			if (immutableCodelist != null) columnConcept.setCoreRepresentation(getCoreRepresentation (immutableCodelist));

			StructureReferenceBean conceptsReference = getConceptReference(concepts, columnConcept,parameters.targetVersion);		
			DimensionMutableBean dimension = new DimensionMutableBeanImpl();
			dimension.setId(column.getName()+"_DSD");
			dimension.setConceptRef(conceptsReference);
			dataStructure.getDsd().addDimension(dimension);
			registerDimensionColumn(column, columnConcept, immutableCodelist,dataStructure);
		}	
	}
	
	
	/**
	 * 
	 * @param dataStructure
	 * @param attributeColumns
	 * @param concepts
	 */

	private void addAttributes (SDMXDataBean dataStructure,List<Column> attributeColumns,Parameters parameters )
	{
		log.debug("Adding attribute list bean");
		ConceptSchemeMutableBean concepts = dataStructure.getConcepts();
		
		for (Column column : attributeColumns)
		{
			ConceptMutableBean columnConcept = this.conceptSchemeGenerator.createConceptBean(column);
			concepts.addItem(columnConcept);
			CodelistBean immutableCodelist = createCodeListRepresentation(column,parameters);	
			
			if (immutableCodelist != null) columnConcept.setCoreRepresentation(getCoreRepresentation (immutableCodelist));
			
			StructureReferenceBean conceptsReference = getConceptReference(concepts, columnConcept,parameters.targetVersion);
			AttributeMutableBean attributeBean = new AttributeMutableBeanImpl();
			attributeBean.setAttachmentLevel(ATTRIBUTE_ATTACHMENT_LEVEL.OBSERVATION);
			attributeBean.setAssignmentStatus(ATTRIBUTE_ASSIGNMENT_STATUS_MANDATORY);
			attributeBean.setConceptRef(conceptsReference);
			dataStructure.getDsd().addAttribute(attributeBean);
			registerAttributeColumn(column, columnConcept, immutableCodelist,dataStructure);

		}	
	}

	private void registerAttributeColumn(Column column, ConceptMutableBean concept, CodelistBean immutableCodelist,
			SDMXDataBean dataStructure) {
		this.log.debug("Adding attribute to excel table");
		dataStructure.addAttributeColumn(column, concept, immutableCodelist);
		this.log.debug("Attribute dimension added");

	}
	
	private void registerPrimaryMeasure(Column column, ConceptMutableBean concept, SDMXDataBean dataStructure) {
		this.log.debug("Adding primary measure to excel table");
		dataStructure.setPrimaryMeasure(column, concept);
		this.log.debug("Primary measure added");
		
	}	
	
	private RepresentationMutableBean getCoreRepresentation (CodelistBean immutableCodelist)
	{
		RepresentationMutableBean coreRepresentation = new RepresentationMutableBeanImpl();
		coreRepresentation.setRepresentation(new StructureReferenceBeanImpl(immutableCodelist));
		log.debug("Found a code list representation");
		return coreRepresentation;
	}
	
	@SuppressWarnings("unchecked")
	private void getMeasures (List<Column> measures, Parameters parameters)
	{
		this.log.debug("Loading measures");
		measures.addAll(this.table.getColumnsByType(MeasureColumnType.class));
		measures.remove(parameters.primaryMeasure);
		
	}
	
	private void addPrimaryMeasure (SDMXDataBean dataStructure, Parameters parameters)
	{
		ConceptMutableBean primaryMeasureConcept = this.conceptSchemeGenerator.createConceptBean(parameters.primaryMeasure);
		dataStructure.getConcepts().addItem(primaryMeasureConcept);
		StructureReferenceBean conceptReferenceBean = new StructureReferenceBeanImpl (primaryMeasureConcept.getParentAgency(),dataStructure.getConcepts().getId(),
				parameters.targetVersion,SDMX_STRUCTURE_TYPE.CONCEPT,primaryMeasureConcept.getId());
		
		PrimaryMeasureMutableBean primaryMeasureBean = new PrimaryMeasureMutableBeanImpl();
		primaryMeasureBean.setConceptRef(conceptReferenceBean);
		primaryMeasureBean.setId(PrimaryMeasureBean.FIXED_ID);
		dataStructure.getDsd().setPrimaryMeasure(primaryMeasureBean);
		registerPrimaryMeasure(parameters.primaryMeasure, primaryMeasureConcept, dataStructure);
		this.log.debug("Primary measure added");

	}
	
	/**
	 * 
	 * @param dimensionListBean
	 * @param measureColumns
	 * @param concepts
	 */

	private void addMeasureDimensions (SDMXDataBean dataStructure,List<Column> measures,Parameters parameters )
	{

		log.debug("Adding measure dimension list bean");
		ConceptSchemeMutableBean concepts = dataStructure.getConcepts();
		
		for (Column column : measures)
		{
			ConceptMutableBean columnConcept = this.conceptSchemeGenerator.createConceptBean(column);
			concepts.addItem(columnConcept);
			DimensionMutableBean dimensionBean = new DimensionMutableBeanImpl();
			dimensionBean.setMeasureDimension(true);
			dimensionBean.setConceptRef(getConceptReference(concepts, columnConcept,parameters.targetVersion));
			dataStructure.getDsd().addDimension(dimensionBean);
			registerMeasureColumn(column, columnConcept,dataStructure);
		}	
	}

	
	private void registerMeasureColumn(Column column, ConceptMutableBean concept, SDMXDataBean dataStructure) {
		this.log.debug("Adding measure to excel table");
		dataStructure.addMeasureColumn(column, concept);
		this.log.debug("Measure added");

	}
	
	
	
	/**
	 * 
	 * @param conceptScheme
	 * @param concept
	 * @return
	 */
	private StructureReferenceBean getConceptReference (ConceptSchemeMutableBean conceptScheme, ConceptMutableBean concept,String conceptVersion)
	{
		log.debug("Creating reference for concept "+concept.getId());
		return new StructureReferenceBeanImpl (concept.getParentAgency(),conceptScheme.getId(),
				conceptVersion,SDMX_STRUCTURE_TYPE.CONCEPT,concept.getId());
	}

	/**
	 * 
	 * @param column
	 * @param codelists
	 * @return
	 */
	private CodelistBean createCodeListRepresentation (Column column,Parameters parameters )
	{
	
		log.debug("Creating codelist bean");
		CodelistBean response = null;
		Table codelist = getAssociatedCodelist(column);
		
		if (codelist != null)
		{
			log.debug("Table found "+codelist.getName());
			SDMXCodelistGenerator codeListGenerator = new SDMXCodelistGenerator(codelist, this.connectionProvider, parameters.targetAgency, codelist.getName()+"_CL", parameters.targetVersion);
			CodelistMutableBean codeListBean =codeListGenerator.createBaseCodelistBean();

			try
			{
				codeListGenerator.populateCodelistWithCodes(codeListBean);
				response = codeListBean.getImmutableInstance();
				
				
			} catch (Exception e)
			{
				log.warn("Codelist not loaded",e);
				
			}
		}
		
		return response;
	}
	

	
	
	/**
	 * 
	 * @param dataStructure
	 * @return
	 */
	private DataflowMutableBean createDataFlowBean (DataStructureMutableBean dataStructure,List<LocalizedText> tableNamesMetadata,Parameters parameters )
	{
		DataflowMutableBean dataFlow = new DataflowMutableBeanImpl();
		log.debug("Populating data flow bean");
		dataFlow.setAgencyId(parameters.targetAgency);
		dataFlow.setDataStructureRef(new StructureReferenceBeanImpl (dataStructure.getAgencyId(),dataStructure.getId(), parameters.targetVersion,SDMX_STRUCTURE_TYPE.DSD));
		dataFlow.setId(parameters.targetId+"_dataFlow");
		dataFlow.setVersion(parameters.targetVersion);
		dataFlow.setNames(getNamesMetadata(tableNamesMetadata,parameters.targetId+" Data Flow", "en"));
		return dataFlow;
	}



	/**
	 * 
	 */
	private List<LocalizedText>  loadMetadata ()
	{
		List<LocalizedText> tableNamesMetadata = null;
		
		try
		{
			tableNamesMetadata = this.table.getMetadata(NamesMetadata.class).getTexts();
		} catch (NoSuchMetadataException e)
		{
			tableNamesMetadata = Lists.newArrayList();
		}

		return tableNamesMetadata;
	}



	/**
	 * 
	 * @param metadataValues
	 * @param defaultValue
	 * @param defaultLocale
	 * @return
	 */
	protected List<TextTypeWrapperMutableBean> getNamesMetadata (final List<LocalizedText> metadataValues,String defaultValue, String defaultLocale)
	{
		List<TextTypeWrapperMutableBean> response = Lists.newArrayList();
		
		if (metadataValues.size() == 0 && defaultValue != null) 
		{
			log.warn("Names Metadata: using default value "+defaultValue);
			response.add(new TextTypeWrapperMutableBeanImpl(defaultLocale, defaultValue));
		}
		else
		{
			for (LocalizedText text : metadataValues) 
			{
				log.debug("Adding metadata value "+text.getValue()+" "+text.getLocale());
				response.add(new TextTypeWrapperMutableBeanImpl(text.getLocale(), text.getValue()));
			}
		}
		
		return response;

	}
	
	private Table getAssociatedCodelist (Column column)
	{

		log.debug("Looking for table associated to column "+column.getLocalId());
    	ColumnRelationship cr = column.getRelationship();
    	log.debug("Relationship " +cr);
    	Table response = null;
    	
    	if (cr != null)
    	{
    		log.debug("Loading referenced table...");
			Table relatedTable = this.cubeManager.getTable(cr.getTargetTableId());
			
			if (relatedTable.getTableType().getCode().equals("CODELIST"))
			{
				log.debug("Table found "+relatedTable.getName());
				response = relatedTable;
			}
			else
			{
				log.debug("Referenced table is not a codelist");
			}
	        
    	}
    	else log.debug("No related table found");
    	
    	return response;
	}
	
	
	private boolean dataAware ()
	{
		boolean dataAware = false;
		Iterator<SDMXDataOperationExecutor> executorIterator = this.executors.iterator();
		
		while (dataAware == false && executorIterator.hasNext())
		{
			dataAware = executorIterator.next().isDataAware();
		}
		
		return dataAware;
	}

	private void registerData(SDMXDataBean dataStructure, Table table, List<Column> columns, DatabaseConnectionProvider connectionProvider) {
		
		if (dataAware())
		{
		
			this.log.debug("Registering data for excel");
			
			try
			{
				Map<String, List<String>> data = getData(table, columns, connectionProvider);
				
				Iterator<String> columnNames = data.keySet().iterator();
				
				while (columnNames.hasNext())
				{
					String columnName = columnNames.next();
					this.log.debug("Adding data for column "+columnName);
					dataStructure.addData(columnName, data.get(columnName));
					this.log.debug("Data added");
				}
				
	
				
			} catch (SQLException e)
			{
				this.log.error("Unable to register excel data",e);
			}
		}
		else this.log.debug("No data requested");
		
	}	

	private Map<String, List<String>> getData(Table table, List<Column> columns,DatabaseConnectionProvider connectionProvider) throws SQLException{
		this.log.debug("Downloading data");
		String query = buildSqlQuery(table,columns);
		this.log.debug("Query "+query);
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = connectionProvider.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			
			HashMap<String, List<String>> response = new HashMap<>();
			
			while (rs.next()) {

				for (Column column : columns) 
				{
					String columnName = column.getName();
					this.log.debug("Adding data for column "+columnName);
					List<String> dataList = response.get(columnName);
						
					if (dataList == null)
					{
						dataList = new LinkedList<>();
						response.put(columnName, dataList);
					}
					
					dataList.add(rs.getString(columnName));

				}

			}

			return response;
			
		} catch (SQLException e) 
		{

			this.log.error("Unable to execute database query.", e);
			if (e.getNextException() != null)
				this.log.error("Inner Exception: ", e.getNextException());
			throw e;
		} finally 
		{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}
	
	private String buildSqlQuery(Table table,List<Column> columns) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		for (Column column : columns) {
			sql.append(column.getName() + " , ");
		}	
		sql.delete(sql.length() - 2, sql.length()); // Delete comma
		sql.append(" FROM " + table.getName() + ";");
		return sql.toString();
	}


}