package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.codelist.SDMXCodelistGenerator;
import org.gcube.data.analysis.tabulardata.operation.sdmx.conceptscheme.SDMXConceptSchemeGenerator;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.ds.DataSourceConfigurationBean;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.sdmxsource.sdmx.api.constants.ATTRIBUTE_ATTACHMENT_LEVEL;
import org.sdmxsource.sdmx.api.constants.SDMX_STRUCTURE_TYPE;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
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

public class SDMXDataStructureDefinitionExporter extends ResourceCreatorWorker {

	private Logger log;
	private Table table;
	private OperationInvocation invocation;
	private Column primaryMeasure;
	private String registryUrl;
	private String targetAgency;
	private String targetId;
	private String targetVersion;
	private CubeManager cubeManager;
	private List<LocalizedText> tableNamesMetadata;
	private final String ATTRIBUTE_ASSIGNMENT_STATUS_MANDATORY = "Mandatory";
	private static String errorMessage = "Unable to complete export procedure";;
	private DatabaseConnectionProvider connectionProvider;
	private SDMXConceptSchemeGenerator conceptSchemeGenerator;

	
	
	public SDMXDataStructureDefinitionExporter(Table table, DatabaseConnectionProvider connectionProvider, OperationInvocation invocation,CubeManager cubeManager) {
		super(invocation);
		this.log = LoggerFactory.getLogger(this.getClass());
		this.table = table;
		this.invocation = invocation;
		this.connectionProvider = connectionProvider;
		this.cubeManager = cubeManager;

	}
	
	@Override
	protected ResourcesResult execute() throws WorkerException 
	{
		
		try 
		{
			DataSourceConfigurationBean datasourceConfigurationBean = new DataSourceConfigurationBean();
			datasourceConfigurationBean.setTable_id(this.table.getName());
			init(datasourceConfigurationBean);
			loadMetadata ();
			updateProgress(0.1f,"Creating beans");
			DataStructureMutableBean dataStructure = createDataStructureBean(datasourceConfigurationBean);
			ConceptSchemeMutableBean conceptScheme = conceptSchemeGenerator.createConceptSchemeBean();
			DataflowMutableBean dataFlow = createDataFlowBean(dataStructure);
			updateProgress(0.2f,"Populating data structure");
			Set<CodelistBean> codelists = new HashSet<>();
			populateDataStructure(dataStructure, conceptScheme,datasourceConfigurationBean,codelists);
			updateProgress(0.6f,"Publishing");
			publishData(dataStructure, conceptScheme,dataFlow,codelists);
			updateProgress(0.8f,"Finalizing");

			return new ResourcesResult(new ImmutableURIResult(new InternalURI(new URI(registryUrl)), "Dataset SDMX export" , 
					String.format("%s/%s/%s/%s/", registryUrl, targetAgency, targetId, targetVersion), ResourceType.SDMX));
		} catch (RuntimeException e) {
			log.error(errorMessage, e);
			throw new WorkerException(errorMessage, e);
		} catch (URISyntaxException e) {
			throw new WorkerException(String.format("exported url %s not valid",registryUrl),e);
		}
	}


	/**
	 * 
	 * @param datasourceConfigurationBean
	 */
	private void init (DataSourceConfigurationBean datasourceConfigurationBean) {
		registryUrl = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.REGISTRY_BASE_URL);
		targetAgency = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.AGENCY);
		targetId = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.ID);
		targetVersion = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.VERSION);
		String observationValue = (String) this.invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.OBS_VALUE_COLUMN);
		primaryMeasure = this.table.getColumnById(new ColumnLocalId(observationValue));
		datasourceConfigurationBean.setObservationValue(observationValue);
		this.conceptSchemeGenerator = new SDMXConceptSchemeGenerator(this.table, this.targetId,this.targetAgency, this.targetVersion);

	}


	/**
	 * 
	 * @param dsd
	 * @param concepts
	 * @param dataFlow
	 * @throws WorkerException
	 */
	
	private void publishData(DataStructureMutableBean dsd, ConceptSchemeMutableBean concepts,DataflowMutableBean dataFlow, Set<CodelistBean> associatedCodelists) throws WorkerException {
		String url = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.REGISTRY_BASE_URL);
		SDMXRegistryClient registryClient = DataStructureDefinitionWorkerUtils.initSDMXClient(url);

		try {
			
			log.debug("Publishing associated codelists...");
			
			for (CodelistBean codelist : associatedCodelists)
			{
				log.debug("Publishing codelist "+codelist.getId());
				registryClient.publish(codelist);
				log.debug("Codelist published");
			}
		
			log.debug("Codelists published");
			
			
		} catch (SDMXRegistryClientException e) {
			throw new WorkerException("Unable to publish dsd on registry: error in the codelists", e);
		}
		
		
		
		try {
			log.debug("Publishing concepts...");
			registryClient.publish(concepts.getImmutableInstance());
			log.debug("Concepts published");
		} catch (SDMXRegistryClientException e) {
			throw new WorkerException("Unable to publish concepts on registry: error in the concepts", e);
		}
		

		
		
		try {
		
			log.debug("Publishing dsd...");
			registryClient.publish(dsd.getImmutableInstance());
			
			log.debug("DSD published");
		} catch (SDMXRegistryClientException e) {
			throw new WorkerException("Unable to publish dsd on registry: error in the data structure definition", e);
		}
		
		try {
			
			log.debug("Publishing data flow...");
			registryClient.publish(dataFlow.getImmutableInstance());
			
			log.debug("Data flow published");
		} catch (SDMXRegistryClientException e) {
			throw new WorkerException("Unable to publish dsd on registry: error in the dataflow", e);
		}
		

	}
	

	@SuppressWarnings("unchecked")
	private void populateDataStructure (DataStructureMutableBean dataStructure,ConceptSchemeMutableBean conceptScheme, DataSourceConfigurationBean dataSourceConfigurationBean,Set<CodelistBean> codelists) throws WorkerException 
	{

		log.debug("Loading column data");
		List<Column> measureColumns = this.table.getColumnsByType(MeasureColumnType.class);
		List<Column> dimensionColumns = this.table.getColumnsByType(DimensionColumnType.class);
		List<Column> attributeColumns = this.table.getColumnsByType(AttributeColumnType.class);
		Column timeDimensionColumn = this.table.getColumnsByType(TimeDimensionColumnType.class).get(0);
		dataSourceConfigurationBean.setObservationTime(timeDimensionColumn.getLocalId().getValue());
		log.debug("Columns loaded");
		boolean found = false;
		Iterator<Column> measureColumnsIterator = measureColumns.iterator();
		log.debug("Looking for primary measure column");
		
		while (measureColumnsIterator.hasNext() && !found)
		{
			Column column = measureColumnsIterator.next();
			
			if (column.getLocalId().getValue().equals(this.primaryMeasure.getLocalId().getValue()))
			{
				measureColumns.remove(column);
				found = true;
			}
		}

		ConceptMutableBean primaryMeasureConcept = this.conceptSchemeGenerator.createConceptBean(this.primaryMeasure);

		conceptScheme.addItem(primaryMeasureConcept);
		StructureReferenceBean conceptReferenceBean = new StructureReferenceBeanImpl (primaryMeasureConcept.getParentAgency(),conceptScheme.getId(),
				this.targetVersion,SDMX_STRUCTURE_TYPE.CONCEPT,primaryMeasureConcept.getId());
		
		PrimaryMeasureMutableBean primaryMeasureBean = new PrimaryMeasureMutableBeanImpl();
	
		primaryMeasureBean.setConceptRef(conceptReferenceBean);
		primaryMeasureBean.setId("OBS_VALUE");
		
		dataStructure.setPrimaryMeasure(primaryMeasureBean);

		addMeasureDimensions(dataStructure, measureColumns,conceptScheme,dataSourceConfigurationBean);
		addGenericDimensions(dataStructure, dimensionColumns,conceptScheme,dataSourceConfigurationBean,codelists);
		DimensionMutableBean timeDimensionBean = new DimensionMutableBeanImpl();
		timeDimensionBean.setId("TIME_PERIOD");
		ConceptMutableBean timeDimensionConcept = this.conceptSchemeGenerator.createConceptBean(timeDimensionColumn);
		conceptScheme.addItem(timeDimensionConcept);
		timeDimensionBean.setConceptRef(getConceptReference(conceptScheme, timeDimensionConcept));
		timeDimensionBean.setTimeDimension(true);
		dataStructure.addDimension(timeDimensionBean);

		addAttributes(dataStructure, attributeColumns,conceptScheme,dataSourceConfigurationBean,codelists);


	}
	
	/**
	 * 
	 * @param dimensionListBean
	 * @param dimensionColumns
	 * @param concepts
	 */
	private void addGenericDimensions (DataStructureMutableBean dsd, List<Column> dimensionColumns,ConceptSchemeMutableBean concepts,DataSourceConfigurationBean dataSourceConfigurationBean, Set<CodelistBean> codelists)
	{
		log.debug("Adding dimension list bean");
		for (Column column : dimensionColumns)
		{
			ConceptMutableBean columnConcept = this.conceptSchemeGenerator.createConceptBean(column);
			concepts.addItem(columnConcept);
			RepresentationMutableBean codedRepresentation = createCodeListRepresentation(column,codelists);
			
			if (codedRepresentation != null)
			{
				log.debug("Found a code list representation");
				columnConcept.setCoreRepresentation(codedRepresentation);			
			}
		
			StructureReferenceBean conceptsReference = getConceptReference(concepts, columnConcept);		
			DimensionMutableBean dimension = new DimensionMutableBeanImpl();
			dimension.setId(column.getName()+"_DSD");
			dimension.setConceptRef(conceptsReference);
			dsd.addDimension(dimension);
			
			
			dataSourceConfigurationBean.addDimension(columnConcept.getId(), column.getLocalId().getValue());
		}	
	}
	
	
	/**
	 * 
	 * @param dataStructure
	 * @param attributeColumns
	 * @param concepts
	 */
	private void addAttributes (DataStructureMutableBean dataStructure, List<Column> attributeColumns,ConceptSchemeMutableBean concepts,DataSourceConfigurationBean dataSourceConfigurationBean,Set<CodelistBean> codelists)
	{
		log.debug("Adding attribute list bean");
		for (Column column : attributeColumns)
		{
			ConceptMutableBean columnConcept = this.conceptSchemeGenerator.createConceptBean(column);
			concepts.addItem(columnConcept);
			
			AttributeMutableBean attributeBean = new AttributeMutableBeanImpl();
			attributeBean.setAttachmentLevel(ATTRIBUTE_ATTACHMENT_LEVEL.OBSERVATION);
			attributeBean.setAssignmentStatus(ATTRIBUTE_ASSIGNMENT_STATUS_MANDATORY);
			RepresentationMutableBean codedRepresentation = createCodeListRepresentation(column,codelists);	
			
			
			if (codedRepresentation != null)
			{
				log.debug("Found a code list representation");
				columnConcept.setCoreRepresentation(codedRepresentation);
			}

			attributeBean.setConceptRef(getConceptReference(concepts, columnConcept));
			dataStructure.addAttribute(attributeBean);
			dataSourceConfigurationBean.addAttributes(columnConcept.getId(), column.getLocalId().getValue());
		}	
	}
	
	/**
	 * 
	 * @param dimensionListBean
	 * @param measureColumns
	 * @param concepts
	 */
	private void addMeasureDimensions (DataStructureMutableBean dsd, List<Column> measureColumns,ConceptSchemeMutableBean concepts, DataSourceConfigurationBean dataSourceConfigurationBean )
	{
		log.debug("Adding measure dimension list bean");
		for (Column column : measureColumns)
		{
			ConceptMutableBean columnConcept = this.conceptSchemeGenerator.createConceptBean(column);
			concepts.addItem(columnConcept);
			DimensionMutableBean dimensionBean = new DimensionMutableBeanImpl();
			dimensionBean.setMeasureDimension(true);
			dimensionBean.setConceptRef(getConceptReference(concepts, columnConcept));
			dsd.addDimension(dimensionBean);
			dataSourceConfigurationBean.addDimension(columnConcept.getId(), column.getLocalId().getValue());
		}	
	}
	
	/**
	 * 
	 * @param conceptScheme
	 * @param concept
	 * @return
	 */
	private StructureReferenceBean getConceptReference (ConceptSchemeMutableBean conceptScheme, ConceptMutableBean concept)
	{

		
		log.debug("Creating reference for concept "+concept.getId());
		return new StructureReferenceBeanImpl (concept.getParentAgency(),conceptScheme.getId(),
				this.targetVersion,SDMX_STRUCTURE_TYPE.CONCEPT,concept.getId());
	}

	/**
	 * 
	 * @param column
	 * @param codelists
	 * @return
	 */
	private RepresentationMutableBean createCodeListRepresentation (Column column,Set<CodelistBean> codelists)
	{
	
		log.debug("Creating codelist bean");
		RepresentationMutableBean response = null;
		Table codelist = getAssociatedCodelist(column);
		
		if (codelist != null)
		{
			log.debug("Table found "+codelist.getName());
			SDMXCodelistGenerator codeListGenerator = new SDMXCodelistGenerator(codelist, this.connectionProvider, this.targetAgency, codelist.getName()+"_CL", this.targetVersion);
			CodelistMutableBean codeListBean =codeListGenerator.createBaseCodelistBean();

			try
			{
				codeListGenerator.populateCodelistWithCodes(codeListBean);
				CodelistBean immutableCodelist = codeListBean.getImmutableInstance();
				codelists.add(immutableCodelist);
				response = new RepresentationMutableBeanImpl();
				response.setRepresentation(new StructureReferenceBeanImpl(immutableCodelist));
				
				
			} catch (Exception e)
			{
				log.warn("Codelist not loaded",e);
				
			}
		}
		
		return response;
	}
	
	
//	/**
//	 * 
//	 * @param column
//	 * @param componentBean
//	 * @param conceptRef
//	 */
//	private void setBeanData (Column column,ComponentMutableBean componentBean,StructureReferenceBean conceptRef)
//	{
//		componentBean.setId(column.getName()+"_DSD");
//		componentBean.setConceptRef(conceptRef);
//	
//	}

//	/**
//	 * 
//	 * @param column
//	 * @return
//	 */
//	private ConceptMutableBean createConceptBean (Column column)
//	{
//		log.debug("Generating concept mutable bean for column "+column.getName());
//		ConceptMutableBean concept = new ConceptMutableBeanImpl();
//		concept.setId(column.getName()+"_concept");
//		concept.setParentAgency(this.targetAgency);
//		
//		try {
//			List<LocalizedText> conceptNames = column.getMetadata(NamesMetadata.class).getTexts();
//			log.debug("Concept names size "+conceptNames.size());
//			concept.setNames(getNamesMetadata(conceptNames, null, null));
//		} catch (NoSuchMetadataException e) {
//			// TODO-LF: This exception should not occur
//		}
//		
//		return concept;
//	}
	
	/**
	 * 
	 * @return
	 */
	private DataStructureMutableBean createDataStructureBean(DataSourceConfigurationBean datasourceConfigurationBean) {
		DataStructureMutableBean dataStructure = new DataStructureMutableBeanImpl();
		dataStructure.setAgencyId(this.targetAgency);
		dataStructure.setVersion(this.targetVersion);
		String dsdId = this.targetId+"_DSD";
		dataStructure.setId(dsdId);
		dataStructure.setNames(getNamesMetadata(this.tableNamesMetadata,targetId+" Data Structure Definition", "en"));
		datasourceConfigurationBean.setDsdId(dsdId);
		return dataStructure;
	}
	
	/**
	 * 
	 * @param dataStructure
	 * @return
	 */
	private DataflowMutableBean createDataFlowBean (DataStructureMutableBean dataStructure)
	{
		DataflowMutableBean dataFlow = new DataflowMutableBeanImpl();
		log.debug("Populating data flow bean");
		dataFlow.setAgencyId(this.targetAgency);
		dataFlow.setDataStructureRef(new StructureReferenceBeanImpl (dataStructure.getAgencyId(),dataStructure.getId(), this.targetVersion,SDMX_STRUCTURE_TYPE.DSD));
		dataFlow.setId(this.targetId+"_dataFlow");
		dataFlow.setNames(getNamesMetadata(this.tableNamesMetadata,targetId+" Data Flow", "en"));
		return dataFlow;
	}



	/**
	 * 
	 */
	private void loadMetadata ()
	{
		try
		{
			this.tableNamesMetadata = this.table.getMetadata(NamesMetadata.class).getTexts();
		} catch (NoSuchMetadataException e)
		{
			this.tableNamesMetadata = Lists.newArrayList();
		}

		
	}



	/**
	 * 
	 * @param metadataValues
	 * @param defaultValue
	 * @param defaultLocale
	 * @return
	 */
	private List<TextTypeWrapperMutableBean> getNamesMetadata (List<LocalizedText> metadataValues,String defaultValue, String defaultLocale)
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
	
	

}