package org.gcube.data.analysis.tabulardata.operation.sdmx.template;

import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.factories.AnnotationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeDescriptionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.CodeNameColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.exceptions.NoSuchColumnException;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.DescriptionsMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ImportMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.VersionMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.type.CodelistTableType;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.WorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.sdmx.codelist.SDMXBaseCodelistImporter;
import org.gcube.data.analysis.tabulardata.operation.sdmx.template.bean.SDMXTemplateBean;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.datapublishing.sdmx.RegistryInformationProvider;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.Detail;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.References;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.gcube.datapublishing.sdmx.security.model.impl.BasicCredentials;
import org.sdmxsource.sdmx.api.constants.ATTRIBUTE_ATTACHMENT_LEVEL;
import org.sdmxsource.sdmx.api.constants.SDMX_STRUCTURE_TYPE;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.base.AnnotationBean;
import org.sdmxsource.sdmx.api.model.beans.base.IdentifiableBean;
import org.sdmxsource.sdmx.api.model.beans.base.MaintainableBean;
import org.sdmxsource.sdmx.api.model.beans.base.TextTypeWrapper;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodeBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.beans.conceptscheme.ConceptBean;
import org.sdmxsource.sdmx.api.model.beans.conceptscheme.ConceptSchemeBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.AttributeBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.AttributeListBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataStructureBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DimensionBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DimensionListBean;
import org.sdmxsource.sdmx.api.model.beans.reference.CrossReferenceBean;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptMutableBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/*
 * NO javadoc: incomplete code
 */
public class SDMXTemplateImporter extends DataWorker {

	private CubeManager cubeManager;
	
	private DatabaseConnectionProvider connectionProvider;

	private Logger log;

	private OperationInvocation operationInvocation;

	private String url;
	private String agency;
	private String id;
	private String version;

	private Map<String, ColumnLocalId> namesMapping;

	private Map<String, ColumnLocalId> descriptionsMapping;

	private Map<String, ColumnLocalId> annotationsMapping;
	
	private String username;
	
	private String password;



	public SDMXTemplateImporter(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider,
			OperationInvocation operationInvocation) {
		super(operationInvocation);
		this.log = LoggerFactory.getLogger(this.getClass());
		this.connectionProvider = connectionProvider;
		this.operationInvocation = operationInvocation;
		this.username = null;
		this.password = null;
		this.cubeManager = cubeManager;
	}
	
	@Override
	protected WorkerResult execute() throws WorkerException {
		retrieveParameters();
		updateProgress(0.1f,"Connecting to repository");
		try {
			SDMXRegistryClient registryClient = WorkerUtils.initSDMXClient(url,this.username, this.password);
			updateProgress(0.2f,"Getting beans");
			DataStructureBean dataStructure = getDataStructureBean(registryClient);
			log.debug("Getting dimension list...");
			SDMXTemplateBean template = new SDMXTemplateBean();
			DimensionListBean dimensionList = dataStructure.getDimensionList();
			parseDimensions(registryClient,dimensionList, template);
			AttributeListBean attributeList = dataStructure.getAttributeList();
			parseAttributes(registryClient, attributeList, template);
			updateProgress(0.3f,"Importing codelists");
			importCodelists (template.getAllCodelists());
			
			
			updateProgress(0.4f,"Importing data");
//			Table table = importCodelist(codelist);
//			updateProgress(0.8f,"Creating resource");
//			Table resultTable = addImportMetadata(table.getId(),codelist);
//			return new ImmutableWorkerResult(resultTable);
			return new ImmutableWorkerResult(null);
		} catch (RuntimeException e) {
			log.error("Unable to complete import procedure", e);
			throw new WorkerException("Unable to complete import procedure", e);
			
		}
	}
	
	private void importCodelists (List<CodelistBean> codelists) throws WorkerException
	{
		this.log.debug("Importing codelists...");
		
		for (CodelistBean codelist : codelists)
		{
			this.log.debug("Importing "+codelist.getId());
			SDMXBaseCodelistImporter codelistImporter = new SDMXBaseCodelistImporter(cubeManager, codelist, connectionProvider, this.url);
			Table table = codelistImporter.importCodelist();
			codelistImporter.addImportMetadata(table.getId());
			this.log.debug("Codelist imported");
		}
	}
	
	private void parseDimensions (SDMXRegistryClient registryClient, DimensionListBean dimensionList,SDMXTemplateBean template)
	{
		log.debug("Parsing dimensions...");
		List<DimensionBean> dimensions = dimensionList.getDimensions();
		
		for (DimensionBean dimension : dimensions)
		{
			String id = dimension.getId();
			log.debug("Dimension id "+id);
			CrossReferenceBean conceptReference = dimension.getConceptRef();
			String conceptSchemeID = conceptReference.getMaintainableId();
			log.debug("Concept scheme "+conceptSchemeID);
			ConceptSchemeBean conceptScheme = getConceptSchemeBean(registryClient, conceptSchemeID, template.getConceptSchemes());
			log.debug("Concept scheme acquired");
			String conceptID = conceptReference.getIdentifiableIds()[0];
			log.debug("Concept id = "+conceptID);
			ConceptBean concept = getConcept(conceptID, conceptScheme);
			
			if (dimension.isTimeDimension())
			{
				template.setTimeDimension(concept);
			}
			else 
			{
				CodelistBean codelist = getCodelistFromConcept(registryClient, concept);
			
				if (dimension.isMeasureDimension()) template.addMeasureDimension(conceptSchemeID, concept, codelist);
				
				else template.addNormalDimension(conceptSchemeID, concept, codelist);
			}
		}
	}

	private void parseAttributes (SDMXRegistryClient registryClient, AttributeListBean attributeList,SDMXTemplateBean template)
	{
		log.debug("Parsing attributes...");
		List<AttributeBean> attributes = attributeList.getAttributes();
		
		for (AttributeBean attribute : attributes)
		{
			String id = attribute.getId();
			log.debug("Attribute id "+id);

			ATTRIBUTE_ATTACHMENT_LEVEL attachmentLevel = attribute.getAttachmentLevel();
			log.debug("Attribute attachment level "+attachmentLevel);
			
			if (attachmentLevel == ATTRIBUTE_ATTACHMENT_LEVEL.OBSERVATION)
			{
				CrossReferenceBean conceptReference = attribute.getConceptRef();
				String conceptSchemeID = conceptReference.getMaintainableId();
				log.debug("Concept scheme "+conceptSchemeID);
				ConceptSchemeBean conceptScheme = getConceptSchemeBean(registryClient, conceptSchemeID, template.getConceptSchemes());
				log.debug("Concept scheme acquired");
				String conceptID = conceptReference.getIdentifiableIds()[0];
				log.debug("Concept id = "+conceptID);
				ConceptBean concept = getConcept(conceptID, conceptScheme);
				CodelistBean codelist = getCodelistFromConcept(registryClient, concept);
				template.addObservationAttributes(conceptSchemeID, concept, codelist);
			}

		}
	}

	
	private Table addImportMetadata(TableId tableId, CodelistBean codelist) throws WorkerException {
		try {
			TableMetaCreator tmc = cubeManager.modifyTableMeta(tableId);
			
			
			tmc.setTableMetadata(new ImportMetadata("SDMX", url, new Date()));
			
			List<LocalizedText> texts = new ArrayList<LocalizedText>();
			for ( TextTypeWrapper text : codelist.getNames())
				texts.add(new ImmutableLocalizedText(text.getValue(), text.getLocale()));
			NamesMetadata namesMetadata = new NamesMetadata(texts);
			tmc.setTableMetadata(namesMetadata);
			if (!codelist.getDescriptions().isEmpty()){
				texts = new ArrayList<LocalizedText>();
				for (TextTypeWrapper text : codelist.getDescriptions()) {
					texts.add(new ImmutableLocalizedText(text.getValue(), text.getLocale()));
				}
				DescriptionsMetadata descriptionsMetadata = new DescriptionsMetadata(texts);
				tmc.setTableMetadata(descriptionsMetadata);
			}
			tmc.setTableMetadata(new VersionMetadata(codelist.getVersion()));
			return tmc.create();
		} catch (Exception e) {
			throw new WorkerException("Unable to modify table metadata.", e);
		}
	}

	private void retrieveParameters() 
	{
		Map<String, Object> parameters = operationInvocation.getParameterInstances();
		url = (String) parameters.get(WorkerUtils.REGISTRY_BASE_URL);
		agency = (String) parameters.get(WorkerUtils.AGENCY);
		id = (String) parameters.get(WorkerUtils.ID);
		version = (String) parameters.get(WorkerUtils.VERSION);
		BasicCredentials credentials = RegistryInformationProvider.retrieveCredentials(url);
		username = credentials.getUsername();
		password = credentials.getPassword();
	}

	
	private <T extends MaintainableBean> T getMaintainableFromBeans(Set<T> maintainableSet, String agencyId, String id, String version) {

		log.debug("Received set: " + maintainableSet);

		if (maintainableSet.size() < 1)
			throw new RuntimeException(String.format(
					"Unable to find a beans with the given coordinates: [%s,%s,%s]", agencyId, id, version));

		if (maintainableSet.size() > 1)
			throw new RuntimeException(String.format(
					"Found too many beans for the given coordinates: [%s,%s,%s]", agencyId, id, version));

		return maintainableSet.iterator().next();
	}

	
	private DataStructureBean getDataStructureBean(SDMXRegistryClient registryClient) {
		SdmxBeans sdmxBeans = null;
		try {
			sdmxBeans = registryClient.getDataStructure(agency, id, version, Detail.full, References.none);
			log.debug("Retrieved beans: " + sdmxBeans);

		} catch (SDMXRegistryClientException e) {
			String msg = "Error occurred while retrieving data structure.";
			log.error(msg, e);
			throw new RuntimeException(msg);
		}

		DataStructureBean dataStructure = getMaintainableFromBeans(sdmxBeans.getDataStructures(),agency, id, version);
		return dataStructure;
	}
	
	private CodelistBean getCodelistBean(SDMXRegistryClient registryClient, String codeListId) {
		
		if (codeListId != null)
		{
			SdmxBeans sdmxBeans = null;
			try {
				sdmxBeans = registryClient.getCodelist(agency, codeListId, version, Detail.full, References.none);
				log.debug("Retrieved beans: " + sdmxBeans);

			} catch (SDMXRegistryClientException e) {
				String msg = "Error occurred while retrieving codelist.";
				log.error(msg, e);
				throw new RuntimeException(msg);
			}

			CodelistBean codelist = getMaintainableFromBeans(sdmxBeans.getCodelists(),agency, id, version); 
			return codelist;
		}
		else return null;
		

	}

	
	
	private ConceptSchemeBean getConceptSchemeBean(SDMXRegistryClient registryClient, String conceptSchemeId, Map<String, ConceptSchemeBean> conceptSchemes) {
		
		ConceptSchemeBean response = conceptSchemes.get(conceptSchemeId);
		
		if (response == null)
		{
			SdmxBeans sdmxBeans = null;
			try {
				sdmxBeans = registryClient.getConceptScheme(this.agency, conceptSchemeId, version,  Detail.full, References.none);
				log.debug("Retrieved beans: " + sdmxBeans);

			} catch (SDMXRegistryClientException e) {
				String msg = "Error occurred while retrieving concepts.";
				log.error(msg, e);
				throw new RuntimeException(msg);
			}
			
			response = getMaintainableFromBeans(sdmxBeans.getConceptSchemes(),agency, id, version);
			conceptSchemes.put(conceptSchemeId, response); 
		}
		else
		{
			log.debug("Concept scheme bean already present");

		}

		return response;
	}
	
	private CodelistBean getCodelistFromConcept (SDMXRegistryClient registryClient, IdentifiableBean concept)
	{
		log.debug("Getting associated codelist...");
		Set<CrossReferenceBean> crossReferences = concept.getCrossReferences();
		CodelistBean response = null;
		
		if (crossReferences != null)
		{
			String codelistID = null;
			Iterator<CrossReferenceBean> crossReferencesIterator = crossReferences.iterator();
			
			while (crossReferencesIterator.hasNext() && codelistID == null)
			{
				CrossReferenceBean reference = crossReferencesIterator.next();
				SDMX_STRUCTURE_TYPE type = reference.getMaintainableStructureType();
				log.debug("Reference type = "+type);
				
				if (type == SDMX_STRUCTURE_TYPE.CODE_LIST)
				{
					log.debug("Code List found");
					codelistID = reference.getMaintainableId();
					log.debug("Id "+codelistID);
				}				
			}
			
			response = getCodelistBean(registryClient, codelistID);
		}
		
		return response;
		
	}
	

	private ConceptBean getConcept (String id, ConceptSchemeBean scheme)
	{
		
		
		log.debug("Getting concept "+id);
		Iterator<IdentifiableBean> conceptsIterator = scheme.getIdentifiableComposites().iterator();
		ConceptBean response = null;
		
		while (conceptsIterator.hasNext() && response == null)
		{
			IdentifiableBean identifiable = conceptsIterator.next();
			String conceptId = identifiable.getId();
			log.debug("Concept id "+conceptId);
			
			if (id.equals(conceptId))
			{
				log.debug("Concept found");
				response = (ConceptBean) identifiable;
			}
			
		}
		
		
		return response;
		
	}
	

	@SuppressWarnings("unchecked")
	private Table importCodelist(CodelistBean codelist) throws WorkerException  {
		List<CodeBean> codes = codelist.getItems();
		checkCodes(codes);
		Table table = createBaseTable(codes);
		log.debug("Created table: " + table);

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			conn = connectionProvider.getConnection();
			stmt = conn.createStatement();
			for (CodeBean code : codes) {
				

				List<String> columnNames = Lists.newArrayList();
				List<String> columnValues = Lists.newArrayList();
				
				columnNames.add(table.getColumnsByType(CodeColumnType.class).get(0).getName());
				columnValues.add(code.getId());

				for (TextTypeWrapper codename : code.getNames()) {
					Column relatedColumn = table.getColumnById(namesMapping.get(codename.getLocale()));
					columnNames.add(relatedColumn.getName());
					columnValues.add(codename.getValue().replaceAll("'", "''"));
				}

				for (TextTypeWrapper codeDescription : code.getDescriptions()) {
					Column relatedColumn = table
							.getColumnById(descriptionsMapping.get(codeDescription.getLocale()));
					columnNames.add(relatedColumn.getName());
					columnValues.add(codeDescription.getValue().replaceAll("'", "''"));
				}

				for (AnnotationBean annotation : code.getAnnotations()) {
					if (annotation.getTitle() == null || annotation.getText().isEmpty()) continue;
					Column relatedColumn = table.getColumnById(annotationsMapping.get(annotation.getTitle()));
					columnNames.add(relatedColumn.getName());
					columnValues.add(annotation.getText().get(0).getValue().replaceAll("'", "''"));
				}
				
				StringBuilder sql = createSQLStatement(table.getName(), columnNames, columnValues);
				log.trace("Appending SQL query to batch command:\n" + sql.toString());
				stmt.addBatch(sql.toString());
			}
			log.debug("Executing query...");
			stmt.executeBatch();
			rs = stmt.getResultSet();
		} catch (SQLException e) {
			String msg = "Unable to execute database query.";
			log.error(msg, e);
			if (e.getNextException() != null)
				log.error("Inner Exception: ", e.getNextException());
			throw new WorkerException (msg, e);
		} catch (NoSuchColumnException e) {
			throw new WorkerException ("Unable to build SQL statement",e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
		log.debug("Table has been populated");

		return table;
	}

	private Table createBaseTable(List<CodeBean> codes) throws WorkerException  {
		TableCreator tc = cubeManager.createTable(new CodelistTableType());
		tc.addColumn(new CodeColumnFactory().createDefault());

		namesMapping = new HashMap<String, ColumnLocalId>();
		descriptionsMapping = new HashMap<String, ColumnLocalId>();
		annotationsMapping = new HashMap<String, ColumnLocalId>();

		// Collect locales
		List<String> nameLocales = new ArrayList<String>();
		List<String> descriptionLocales = new ArrayList<String>();
		List<String> annotationTitles = new ArrayList<String>();

		collectLocalesAndAnnotationTitles(codes, nameLocales, descriptionLocales, annotationTitles);

		log.debug("Collected name locales: " + nameLocales);
		log.debug("Collected description locales: " + descriptionLocales);
		log.debug("Collected annotation titles: " + annotationTitles);

		for (String nameLocale : nameLocales) {
			Column column = new CodeNameColumnFactory().create(nameLocale);
			tc.addColumn(column);
			if (!namesMapping.containsKey(nameLocale))
				namesMapping.put(nameLocale, column.getLocalId());
		}
		for (String descriptionLocale : descriptionLocales) {
			Column column = new CodeDescriptionColumnFactory().create(descriptionLocale);
			tc.addColumn(column);
			if (!descriptionsMapping.containsKey(descriptionLocale))
				descriptionsMapping.put(descriptionLocale, column.getLocalId());
		}
		//TODO-LF: deal with nested annotation values
		for (String annotationTitle : annotationTitles) {
			if (!annotationsMapping.containsKey(annotationTitle)) {
				Column column = new AnnotationColumnFactory().create(new ImmutableLocalizedText(annotationTitle),new DataLocaleMetadata("en"));
				tc.addColumn(column);
				annotationsMapping.put(annotationTitle, column.getLocalId());
			}
		}

		Table table = null;
		try {
			table = tc.create();
		} catch (TableCreationException e) {
			String msg = "Error occurred while trying to create the table.";
			log.error(msg, e);
			throw new WorkerException(msg);
		}
		return table;
	}

	private void collectLocalesAndAnnotationTitles(List<CodeBean> codes, List<String> nameLocales,
			List<String> descriptionLocales, List<String> annotationTitles) {
		for (CodeBean code : codes) {
			for (TextTypeWrapper textType : code.getNames()) {
				if (textType.getLocale() == null | textType.getLocale().isEmpty())
					continue;
				if (!nameLocales.contains(textType.getLocale()))
					nameLocales.add(textType.getLocale());
			}
			for (TextTypeWrapper textType : code.getDescriptions()) {
				if (textType.getLocale() == null | textType.getLocale().isEmpty())
					continue;
				if (!descriptionLocales.contains(textType.getLocale()))
					descriptionLocales.add(textType.getLocale());
			}
			for (AnnotationBean annotation : code.getAnnotations()) {
				if (annotation.getTitle() == null | annotation.getTitle().isEmpty())
					continue;
				String annotationTitle = annotation.getTitle();
				if (!annotationTitles.contains(annotationTitle))
					annotationTitles.add(annotationTitle);
			}
		}
	}

	private StringBuilder createSQLStatement(String tableName, List<String> columnNames, List<String> columnValues) {
		StringBuilder sql = new StringBuilder();
		sql.append(String.format("INSERT INTO %s ( ", tableName));

		for (String columnName : columnNames)
			sql.append(columnName + " , ");
		sql.delete(sql.length() - 2, sql.length()); // Delete comma
		sql.append(" ) VALUES (");
		for (String columnValue : columnValues)
			sql.append(String.format(" '%s' , ", columnValue));
		sql.delete(sql.length() - 2, sql.length()); // Delete comma
		sql.append(");");
		return sql;
	}

	private void checkCodes(List<CodeBean> codes) throws WorkerException {
		if (codes.isEmpty())
			throw new WorkerException("Codelist [%s,%s,%s] does not contain codes.");
	}

}