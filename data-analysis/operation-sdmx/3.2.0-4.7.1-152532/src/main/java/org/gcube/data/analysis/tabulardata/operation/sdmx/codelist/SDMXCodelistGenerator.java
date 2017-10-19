package org.gcube.data.analysis.tabulardata.operation.sdmx.codelist;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.AnnotationColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeDescriptionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeNameColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataLocaleMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.sdmxsource.sdmx.api.model.mutable.base.AnnotationMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.base.TextTypeWrapperMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.codelist.CodeMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.codelist.CodelistMutableBean;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.AnnotationMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.TextTypeWrapperMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.codelist.CodeMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.codelist.CodelistMutableBeanImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class SDMXCodelistGenerator
{

	private  Logger log;

	private Table table;

	private DatabaseConnectionProvider connectionProvider;

	private Column codeColumn;

	private List<Column> nameColumns;

	private List<Column> descriptionColumns;

	private List<Column> annotationColumns;

	private String targetAgency;

	private String targetId;

	private String targetVersion;



	public SDMXCodelistGenerator(Table table,
			DatabaseConnectionProvider connectionProvider, String targetAgency, String targetId,String targetVersion) {

		log = LoggerFactory.getLogger(this.getClass());
		this.table = table;
		this.connectionProvider = connectionProvider;
		this.targetAgency = targetAgency;
		this.targetId = targetId;
		this.targetVersion = targetVersion;
		log.debug("Agency ID = "+targetAgency);
		log.debug("ID = "+targetId);
		log.debug("Version = "+targetVersion);

	}
	


	@SuppressWarnings("unchecked")
	private void retrieveNeededColumns() {
		codeColumn = table.getColumnsByType(CodeColumnType.class).get(0);
		nameColumns = table.getColumnsByType(CodeNameColumnType.class);
		descriptionColumns = table.getColumnsByType(CodeDescriptionColumnType.class);
		annotationColumns = table.getColumnsByType(AnnotationColumnType.class);
	}




	
	public void populateCodelistWithCodes(CodelistMutableBean codelist) throws SQLException {
		retrieveNeededColumns ();
		String query = buildSqlQuery(codeColumn, nameColumns, descriptionColumns, annotationColumns);
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = connectionProvider.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			
			while (rs.next()) {

				boolean validCode = false;
				
				CodeMutableBean code = new CodeMutableBeanImpl();
				String id = rs.getString(codeColumn.getName());				

				
				for (Column column : nameColumns) {
					try {
						String columnDataLocale = column.getMetadata(DataLocaleMetadata.class).getLocale();
						String name = rs.getString(column.getName());
						
						if (name != null && name.trim().length()>0)
						{							
							code.addName(columnDataLocale, name);
							
							validCode = true;
						}
						else log.warn("Invalid column "+id+", name not found");
	
						
					} catch (NoSuchMetadataException e) {
						log.warn ("Invalid column "+id);
					}
				}
				
				
				
				if (validCode)
				{
					code.setId(id);
					

					for (Column column : descriptionColumns) {
						try {
							String columnDataLocale = column.getMetadata(DataLocaleMetadata.class).getLocale();
							code.addDescription(columnDataLocale, rs.getString(column.getName()));
						} catch (NoSuchMetadataException e) {
							// This exception should not occur
						}
					}

					// TODO-LF: Group annotation columns and create a single one
					for (Column column : annotationColumns) {
						try {
							AnnotationMutableBeanImpl annotation = new AnnotationMutableBeanImpl();
							LocalizedText text = column.getMetadata(NamesMetadata.class).getTextWithLocale("en");
							annotation.setTitle(text.getValue());
							List<TextTypeWrapperMutableBean> annotationValues = Lists.newArrayList();
							annotationValues.add(new TextTypeWrapperMutableBeanImpl(text.getLocale(), rs
									.getString(column.getName())));
							annotation.setText(annotationValues);
							code.addAnnotation(annotation);
						} catch (NoSuchMetadataException e) {
							// This exception should not occur
						}
					}

					codelist.addItem(code);
				}
				
				log.debug(codelist.getItems().size()+" codes");

			}

		} catch (SQLException e) {
			String msg = "Unable to execute database query.";
			log.error(msg, e);
			if (e.getNextException() != null)
				log.error("Inner Exception: ", e.getNextException());
			throw e;
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}

	public CodelistMutableBean createBaseCodelistBean() {
		CodelistMutableBean codelist = new CodelistMutableBeanImpl();
		codelist.setAgencyId(this.targetAgency);
		codelist.setVersion(this.targetVersion);
		codelist.setId(this.targetId);
		
		try {
			List<TextTypeWrapperMutableBean> names = Lists.newArrayList();
			
			for (LocalizedText text : table.getMetadata(NamesMetadata.class).getTexts()) 
			{
				String locale = text.getLocale();
				String value = text.getValue();
				
				log.debug("Locale = "+locale);
				log.debug("Value "+value);
				names.add(new TextTypeWrapperMutableBeanImpl(locale, value));
			}
			codelist.setNames(names);
			log.debug("Names set");
			
		} catch (NoSuchMetadataException e) 
		{
			log.warn("Names Metadata not found in the table");
			setTableDescriptionMetadata(codelist);
			
		}

		List<AnnotationMutableBean> codelistAnnotations = Lists.newArrayList();
		AnnotationMutableBean sourceAnnotation = new AnnotationMutableBeanImpl();
		sourceAnnotation.setTitle("Source");
		List<TextTypeWrapperMutableBean> textList = Lists.newArrayList();
		textList.add(new TextTypeWrapperMutableBeanImpl("en", "Tabular Data"));
		sourceAnnotation.setText(textList);
		codelistAnnotations.add(sourceAnnotation);
		codelist.setAnnotations(codelistAnnotations);
		return codelist;
	}
	
	
	private void setTableDescriptionMetadata (CodelistMutableBean codeList)
	{
		log.warn("Setting table description parameters");
		TableDescriptorMetadata tableDescriptionMetadata = this.table.getMetadata(TableDescriptorMetadata.class);
		String name = tableDescriptionMetadata.getName();
		codeList.addName("en", name);
	}


	private String buildSqlQuery(Column codeColumn, List<Column> nameColumns, List<Column> descriptionColumns,
			List<Column> annotationColumns) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT " + codeColumn.getName() + ", ");
		for (Column column : nameColumns) {
			sql.append(column.getName() + " , ");
		}
		for (Column column : descriptionColumns) {
			sql.append(column.getName() + " , ");
		}
		for (Column column : annotationColumns) {
			sql.append(column.getName() + " , ");
		}
		sql.delete(sql.length() - 2, sql.length()); // Delete comma
		sql.append(" FROM " + table.getName() + ";");
		return sql.toString();
	}

	

}