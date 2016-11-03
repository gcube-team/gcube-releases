package org.gcube.data.analysis.tabulardata.operation.sdmx;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
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

public class SDMXCodelistExporter extends ResourceCreatorWorker {

	private static Logger log = LoggerFactory.getLogger(SDMXCodelistExporter.class);

	private Table table;

	private OperationInvocation invocation;

	private DatabaseConnectionProvider connectionProvider;

	private Column codeColumn;

	private List<Column> nameColumns;

	private List<Column> descriptionColumns;

	private List<Column> annotationColumns;

	private String targetUrl;

	private String targetAgency;

	private String targetId;

	private String targetVersion;

	private static String errorMessage = "Unable to complete export procedure";;

	public SDMXCodelistExporter(Table table, OperationInvocation invocation,
			DatabaseConnectionProvider connectionProvider) {
		super(invocation);
		this.table = table;
		this.invocation = invocation;
		this.connectionProvider = connectionProvider;
	}
	
	@Override
	protected ResourcesResult execute() throws WorkerException {
		try {
			retrieveNeededColumns();
			retrieveParameters();
			updateProgress(0.1f,"Creating beans");
			CodelistMutableBean codelist = createBaseCodelistBean();
			updateProgress(0.2f,"Populating codelist");
			populateCodelistWithCodes(codelist);
			updateProgress(0.6f,"Publishing");
			publishCodelist(codelist);
			updateProgress(0.8f,"Finalizing");
			
			return new ResourcesResult(new ImmutableURIResult(new InternalURI(new URI(targetUrl)), "Codelist SDMX export" , 
					String.format("%scodelist/%s/%s/%s/", targetUrl, targetAgency, targetId, targetVersion), ResourceType.SDMX));
		} catch (RuntimeException e) {
			log.error(errorMessage, e);
			throw new WorkerException(errorMessage, e);
		} catch (URISyntaxException e) {
			throw new WorkerException(String.format("exported url %s not valid",targetUrl),e);
		}
	}

	private void retrieveParameters() {
		targetUrl = (String) invocation.getParameterInstances().get(WorkerUtils.REGISTRY_BASE_URL);
		targetAgency = (String) invocation.getParameterInstances().get(WorkerUtils.AGENCY);
		targetId = (String) invocation.getParameterInstances().get(WorkerUtils.ID);
		targetVersion = (String) invocation.getParameterInstances().get(WorkerUtils.VERSION);
	}


	private void publishCodelist(CodelistMutableBean codelist) throws WorkerException {
		String url = (String) invocation.getParameterInstances().get(WorkerUtils.REGISTRY_BASE_URL);
		SDMXRegistryClient registryClient = WorkerUtils.initSDMXClient(url);

		try {
			registryClient.publish(codelist.getImmutableInstance());
		} catch (SDMXRegistryClientException e) {
			throw new WorkerException("Unable to publish codelist on registry.", e);
		}
	}

	private void populateCodelistWithCodes(CodelistMutableBean codelist) throws WorkerException {
		String query = buildSqlQuery(codeColumn, nameColumns, descriptionColumns, annotationColumns);
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = connectionProvider.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {

				CodeMutableBean code = new CodeMutableBeanImpl();
				code.setId(rs.getString(codeColumn.getName()));

				for (Column column : nameColumns) {
					try {
						String columnDataLocale = column.getMetadata(DataLocaleMetadata.class).getLocale();
						code.addName(columnDataLocale, rs.getString(column.getName()));
					} catch (NoSuchMetadataException e) {
						// This exception should not occur
					}
				}

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

		} catch (SQLException e) {
			String msg = "Unable to execute database query.";
			log.error(msg, e);
			if (e.getNextException() != null)
				log.error("Inner Exception: ", e.getNextException());
			throw new WorkerException(msg, e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
	}

	private CodelistMutableBean createBaseCodelistBean() {
		CodelistMutableBean codelist = new CodelistMutableBeanImpl();
		codelist.setAgencyId((String) invocation.getParameterInstances().get(WorkerUtils.AGENCY));
		codelist.setVersion((String) invocation.getParameterInstances().get(WorkerUtils.VERSION));
		codelist.setId((String) invocation.getParameterInstances().get(WorkerUtils.ID));

		try {
			List<TextTypeWrapperMutableBean> names = Lists.newArrayList();
			for (LocalizedText text : table.getMetadata(NamesMetadata.class).getTexts()) {
				names.add(new TextTypeWrapperMutableBeanImpl(text.getLocale(), text.getValue()));
			}
			codelist.setNames(names);
		} catch (NoSuchMetadataException e) {
			// TODO-LF: This exception should not occur
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

	@SuppressWarnings("unchecked")
	private void retrieveNeededColumns() {
		codeColumn = table.getColumnsByType(CodeColumnType.class).get(0);
		nameColumns = table.getColumnsByType(CodeNameColumnType.class);
		descriptionColumns = table.getColumnsByType(CodeDescriptionColumnType.class);
		annotationColumns = table.getColumnsByType(AnnotationColumnType.class);
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