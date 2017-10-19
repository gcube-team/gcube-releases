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
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.sdmxsource.sdmx.api.model.beans.base.AnnotationBean;
import org.sdmxsource.sdmx.api.model.beans.base.TextTypeWrapper;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodeBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class SDMXBaseCodelistImporter  {


	private Logger log; 
	private CubeManager cubeManager;
	private String url;
	private CodelistBean codelist;
	private DatabaseConnectionProvider connectionProvider;
	private Map<String, ColumnLocalId> namesMapping;
	private Map<String, ColumnLocalId> descriptionsMapping;
	private Map<String, ColumnLocalId> annotationsMapping;
	
	public SDMXBaseCodelistImporter(CubeManager cubeManager, CodelistBean codelist,DatabaseConnectionProvider connectionProvider, String registryUrl) {
		this.log = LoggerFactory.getLogger(this.getClass());
		this.cubeManager = cubeManager;
		this.codelist = codelist;
		this.connectionProvider = connectionProvider;
		this.url = registryUrl;
	}
	


	public Table addImportMetadata(TableId tableId) throws WorkerException {
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


	




	@SuppressWarnings("unchecked")
	public Table importCodelist() throws WorkerException  {
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