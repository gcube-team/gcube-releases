package org.gcube.data.analysis.tabulardata.operation.sdmx.codelist;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
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
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.Detail;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.References;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.base.AnnotationBean;
import org.sdmxsource.sdmx.api.model.beans.base.TextTypeWrapper;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodeBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class SDMXCodelistImporter extends DataWorker {

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private static Logger log = LoggerFactory.getLogger(SDMXCodelistImporter.class);

	private OperationInvocation operationInvocation;

	private String url;
	private String agency;
	private String id;
	private String version;

	private Map<String, ColumnLocalId> namesMapping;

	private Map<String, ColumnLocalId> descriptionsMapping;

	private Map<String, ColumnLocalId> annotationsMapping;

	public SDMXCodelistImporter(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider,
			OperationInvocation operationInvocation) {
		super(operationInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.operationInvocation = operationInvocation;
		
	}
	
	@Override
	protected WorkerResult execute() throws WorkerException {
		retrieveParameters();
		updateProgress(0.1f,"Connecting to repository");
		try {
			SDMXRegistryClient registryClient = WorkerUtils.initSDMXClient(url);
			updateProgress(0.2f,"Getting beans");
			CodelistBean codelist = getCodelistBean(registryClient);
			updateProgress(0.4f,"Importing data");
			Table table = importCodelist(codelist);
			updateProgress(0.8f,"Creating resource");
			Table resultTable = addImportMetadata(table.getId(),codelist);
			return new ImmutableWorkerResult(resultTable);
		} catch (RuntimeException e) {
			log.error("Unable to complete import procedure", e);
			throw new WorkerException("Unable to complete import procedure", e);
			
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

	private void retrieveParameters() {
		Map<String, Object> parameters = operationInvocation.getParameterInstances();
		url = (String) parameters.get(WorkerUtils.REGISTRY_BASE_URL);
		agency = (String) parameters.get(WorkerUtils.AGENCY);
		id = (String) parameters.get(WorkerUtils.ID);
		version = (String) parameters.get(WorkerUtils.VERSION);
	}

	

	private CodelistBean getCodelistFromBeans(SdmxBeans sdmxBeans, String agencyId, String id, String version) {
		Set<CodelistBean> codelistBeans = sdmxBeans.getCodelists();
		log.debug("Retrieved codelists: " + codelistBeans);

		if (codelistBeans.size() < 1)
			throw new RuntimeException(String.format(
					"Unable to find a codelist with the given coordinates: [%s,%s,%s]", agencyId, id, version));

		if (codelistBeans.size() > 1)
			throw new RuntimeException(String.format(
					"Found too many codelists for the given coordinates: [%s,%s,%s]", agencyId, id, version));

		return codelistBeans.iterator().next();
	}

	private CodelistBean getCodelistBean(SDMXRegistryClient registryClient) {
		SdmxBeans sdmxBeans = null;
		try {
			sdmxBeans = registryClient.getCodelist(agency, id, version, Detail.full, References.none);
			log.debug("Retrieved beans: " + sdmxBeans);

		} catch (SDMXRegistryClientException e) {
			String msg = "Error occurred while retrieving codelist.";
			log.error(msg, e);
			throw new RuntimeException(msg);
		}

		CodelistBean codelist = getCodelistFromBeans(sdmxBeans, agency, id, version);
		return codelist;
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