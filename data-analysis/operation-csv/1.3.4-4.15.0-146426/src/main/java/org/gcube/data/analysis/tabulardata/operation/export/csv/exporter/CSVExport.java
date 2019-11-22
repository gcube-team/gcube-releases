package org.gcube.data.analysis.tabulardata.operation.export.csv.exporter;

import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.COLUMNS;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.ENCODING;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.RESOURCE_DESCRIPTION;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.RESOURCE_NAME;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.SEPARATOR;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.VIEW;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ExportMetadata;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.export.Utils;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVExport extends ResourceCreatorWorker {

	private static Logger logger = LoggerFactory.getLogger(CSVExport.class);

	CubeManager cubeManager;

	DatabaseConnectionProvider connectionProvider;

	private String encoding;
	private String resourceName = "CsvExport";
	private String resourceDescription = "TabularResource exported in CSV";
	private Character separator;
	private List<String> selectedColumns;
	private boolean useView = false;

	public CSVExport(OperationInvocation invocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider) {
		super(invocation);
		retrieveParameters();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}

	public ResourcesResult execute() throws WorkerException {
		logger.trace("STARTING EXPORT");
		Table table = cubeManager.getTable(getSourceInvocation().getTargetTableId());
		updateProgress(0.1f,"Creating export file");
		File exportFile;
		try{
			exportFile = File.createTempFile("export", ".csv");
		}catch (Exception e) {
			logger.error("error creating file", e);
			throw new WorkerException("error creating file", e);
		}
		updateProgress(0.3f,"Writing file with data");
		OutputStreamWriter outputStreamWriter = null;
		try{
			outputStreamWriter = new OutputStreamWriter(new FileOutputStream(exportFile));
			long updated = copy(outputStreamWriter, table, getSourceInvocation().getParameterInstances());
			logger.debug("exported "+updated+" entries");
		}catch (Exception e) {
			logger.error("error copying table to file", e);
			exportFile.delete();
			throw new WorkerException("error copying table to file", e);
		}finally{
			if (outputStreamWriter!=null)
				try {
					outputStreamWriter.close();
				} catch (IOException e) {logger.warn("error closing output stream",e);}
		}
		updateProgress(0.7f,"Storing file on storage");
		InternalURI internalURI;
		try{
			internalURI = getInternalURI(exportFile);
		}catch (Exception e) {
			logger.error("error storing file", e);
			throw new WorkerException("error storing file", new Exception(e.getMessage()));
		}finally{
			exportFile.delete();
		}
		updateProgress(0.9f,"Finalizing");
		createMetaTable(table, internalURI.getUri().toString());
		return  new ResourcesResult(createDescriptor(internalURI));
	}

	private InternalURI getInternalURI(File exportFile) throws URISyntaxException{
		IClient client = Utils.getStorageClient();
		String remotePath = "/CSVexport/"+exportFile.getName();
		String id = client.put(true).LFile(exportFile.getAbsolutePath()).RFile(remotePath);
		return new InternalURI(new URI(id), "text/csv");
	}

	private Table createMetaTable(Table table, String url) {
		TableMetaCreator tmc = cubeManager.modifyTableMeta(table.getId());
		tmc.setTableMetadata(new ExportMetadata("CSV", url, new Date()));
		return tmc.create();
	}

	private ResourceDescriptorResult createDescriptor(InternalURI url) throws WorkerException {
		return new ImmutableURIResult(url, resourceName ,resourceDescription, ResourceType.CSV);
		
	}

	private long copy(OutputStreamWriter outputStreamWriter, Table table, Map<String, Object> parameters) throws Exception {
		PGConnection conn = connectionProvider.getPostgreSQLConnection();
		CopyManager cpManager = conn.getCopyAPI();
		StringBuilder columns = new StringBuilder("SELECT ");

		Table toUseTable = table;

		if (useView && table.contains(DatasetViewTableMetadata.class)) {
			DatasetViewTableMetadata dsMeta = table.getMetadata(DatasetViewTableMetadata.class);
			toUseTable = cubeManager.getTable(dsMeta.getTargetDatasetViewTableId());
		}

		for (Column c : table.getColumns()) 
			if(selectedColumns.contains(c.getLocalId().getValue())){
				Column toUseColumn = c;
				if (useView && c.getColumnType() instanceof DimensionColumnType){
					ColumnLocalId columnTargetId = c.getRelationship().getTargetColumnId();
					toUseColumn = toUseTable.getColumnById(columnTargetId);
				} else toUseColumn = toUseTable.getColumnById(c.getLocalId());

				if (toUseColumn.getDataType() instanceof GeometryType)
					columns.append("ST_AsText(").append(toUseColumn.getName()).append(")");
				else columns.append(toUseColumn.getName());
				columns.append(" as ").append(String.format("\"%s\"", getColumnLabel(toUseColumn))).append(",");
			}

		columns.deleteCharAt(columns.length() - 1);
		columns.append(" from ").append(toUseTable.getName());
						

		String sqlCmd = String.format("COPY ( %s ) TO STDOUT ( FORMAT CSV ,DELIMITER '%c', HEADER %b, ENCODING '%s');",
				columns.toString(), separator, true, encoding);
		logger.info("executing copy for csv import with query {}",sqlCmd);

		return cpManager.copyOut(sqlCmd, outputStreamWriter);
	}

	private String getColumnLabel(Column column){
		String label = column.getName();
		try{
			NamesMetadata metadata = ((NamesMetadata)column.getMetadata(NamesMetadata.class));
			if (metadata.hasTextWithLocale("en"))
				label = metadata.getTextWithLocale("en").getValue();
		}catch (Exception e) {}
		if (label.isEmpty()) 
			label= column.getLocalId().getValue();
		return label;
	}

	@SuppressWarnings("unchecked")
	private void retrieveParameters() {
		Map<String, Object> parameters = getSourceInvocation().getParameterInstances();
		separator = ((String) parameters.get(SEPARATOR)).charAt(0);
		encoding = (String) parameters.get(ENCODING);
		selectedColumns =new ArrayList<String>();
		Object columnsParam=parameters.get(COLUMNS);
		if(columnsParam instanceof Iterable<?>)
			for(String col:(Iterable<String>)columnsParam)selectedColumns.add(col);
		else selectedColumns.add((String)columnsParam);

		if (parameters.containsKey(VIEW))
			useView = (Boolean)parameters.get(VIEW);
		
		if (parameters.containsKey(RESOURCE_NAME))
			resourceName = (String)parameters.get(RESOURCE_NAME);
		
		if (parameters.containsKey(RESOURCE_DESCRIPTION))
			resourceDescription = (String)parameters.get(RESOURCE_DESCRIPTION);
		
	}

	
}
