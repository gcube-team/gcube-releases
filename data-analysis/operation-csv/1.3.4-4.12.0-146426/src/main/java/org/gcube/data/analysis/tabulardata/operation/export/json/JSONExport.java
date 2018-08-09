package org.gcube.data.analysis.tabulardata.operation.export.json;

import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.COLUMNS;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.RESOURCE_DESCRIPTION;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.RESOURCE_NAME;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.VIEW;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.ExportMetadata;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.export.Utils;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONExport extends ResourceCreatorWorker {

	private static Logger logger = LoggerFactory.getLogger(JSONExport.class);

	CubeManager cubeManager;

	DatabaseConnectionProvider connectionProvider;

	private List<String> selectedColumns;
	private boolean useView = false;

	private String resourceName = "JSonExport";
	private String resourceDescription = "TabularResource exported in JSon";
	
	private static int ELEMENTS_PER_QUERY = 300;

	public JSONExport(OperationInvocation invocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider) {
		super(invocation);
		retrieveParameters();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}

	public ResourcesResult execute() throws WorkerException {
		Table table = cubeManager.getTable(getSourceInvocation().getTargetTableId());
		updateProgress(0.1f,"Creating export file");
		File exportFile;
		try{
			exportFile = File.createTempFile("export", ".json");
		}catch (Exception e) {
			logger.error("error creating file", e);
			throw new WorkerException("error creating file", e);
		}
		updateProgress(0.2f,"Writing file with data");
		FileWriter writer = null;
		try{
			writer = new FileWriter(exportFile);
			write(writer, table);
		}catch (Exception e) {
			logger.error("error copying table to file", e);
			exportFile.delete();
			throw new WorkerException("error copying table to file", e);
		}finally{
			if (writer!=null)
				try {
					writer.close();
				} catch (IOException e) {logger.warn("error closing output stream",e);}
		}
		updateProgress(0.7f,"Storing file on storage");
		InternalURI url;
		try{
			url = createInternalURI(exportFile);
		}catch (Exception e) {
			logger.error("error storing file", e);
			throw new WorkerException("error storing file", new Exception(e.getMessage()));
		}finally{
			exportFile.delete();
		}
		updateProgress(0.9f,"Finalizing");
		
		createMetaTable(table, url.getUri().toString());
		
		return new ResourcesResult( createDescriptor(url));
	}

	private InternalURI createInternalURI(File exportFile) throws  URISyntaxException{
		IClient client = Utils.getStorageClient();
		String remotePath = "/JSONexport/"+exportFile.getName();
		String id = client.put(true).LFile(exportFile.getAbsolutePath()).RFile(remotePath);
		return new InternalURI(new URI(id), "text/json");
	}

	private Table createMetaTable(Table table, String url) {
		TableMetaCreator tmc = cubeManager.modifyTableMeta(table.getId());
		tmc.setTableMetadata(new ExportMetadata("JSON", url, new Date()));
		return tmc.create();
	}

	private ResourceDescriptorResult createDescriptor(InternalURI url) throws WorkerException {
		return new ImmutableURIResult(url, resourceName ,resourceDescription, ResourceType.JSON);
	}

	private void write(Writer writer, Table table) throws Exception {

		String preparedQuery = getPreparedQuery(table);

		HashMap<String, String> nameLabelMapping = getNameLabelmapping(table);
		int count = SQLHelper.getCount(connectionProvider, table.getName(), null);

		logger.trace("count for JSON export is {} ",count);

		JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
		JsonGenerator jg = factory.createGenerator(writer);

		logger.trace("executing query {} ",preparedQuery);

		jg.writeStartObject();
		jg.writeStartArray("rows");

		if (count>0){
			PreparedStatement prepSTM = connectionProvider.getConnection().prepareStatement(preparedQuery);

			int totalCycles = (count/ELEMENTS_PER_QUERY)+1;

			double progressIncrement = 0.5 / totalCycles; 

			for (int i =0; i<(totalCycles); i++){
				prepSTM.setInt(1, (i*ELEMENTS_PER_QUERY));
				ResultSet rs = prepSTM.executeQuery();
				while (rs.next())
					writeSingleObject(jg, rs, nameLabelMapping);
				rs.close();
				float progressReached = this.getProgress()+(float)progressIncrement;
				updateProgress(progressReached,"Writing file with data");
			}
			prepSTM.close();
		}
		jg.writeEnd();
		jg.writeEnd();
		jg.close();
	}

	private void writeSingleObject(JsonGenerator jg, ResultSet rs, HashMap<String, String> nameLabelMap) throws IOException, SQLException
	{
		ResultSetMetaData meta = rs.getMetaData();
		jg.writeStartObject();

		for (int i=1; i<=meta.getColumnCount(); i++){
			int columnType = meta.getColumnType(i);
			String label = nameLabelMap.get(meta.getColumnName(i));
			if (rs.getObject(i) == null)
				jg.write(label, JsonValue.NULL);
			else
				switch (columnType) {
				case Types.INTEGER:
				case Types.BIGINT:
					jg.write(label, rs.getInt(i));
					break;
				default:
					jg.write(label, rs.getObject(i).toString());
				}
		}
		jg.writeEnd();
	}

	private HashMap<String, String> getNameLabelmapping(Table table){
		HashMap<String , String> nameLabelMapping = new HashMap<>();

		Table toUseTable = table;

		if (useView && table.contains(DatasetViewTableMetadata.class)) {
			DatasetViewTableMetadata dsMeta = table.getMetadata(DatasetViewTableMetadata.class);
			toUseTable = cubeManager.getTable(dsMeta.getTargetDatasetViewTableId());
		}

		for (Column c : table.getColumns()) 
			if(selectedColumns.contains(c.getLocalId().getValue())){
				Column toUseColumn ;
				if (useView && c.getColumnType().equals(new DimensionColumnType())){
					ColumnLocalId columnTargetId = c.getRelationship().getTargetColumnId();
					toUseColumn = toUseTable.getColumnById(columnTargetId);
				} else toUseColumn = toUseTable.getColumnById(c.getLocalId());

				nameLabelMapping.put(toUseColumn.getName(), getColumnLabel(toUseColumn));
			}

		return nameLabelMapping;
	}

	private String getPreparedQuery(Table table){
		StringBuilder select = new StringBuilder("SELECT ");

		Table toUseTable = table;

		if (useView && table.contains(DatasetViewTableMetadata.class)) {
			DatasetViewTableMetadata dsMeta = table.getMetadata(DatasetViewTableMetadata.class);
			toUseTable = cubeManager.getTable(dsMeta.getTargetDatasetViewTableId());
		}

		for (Column c : table.getColumns()) 
			if(selectedColumns.contains(c.getLocalId().getValue())){
				Column toUseColumn;
				if (useView && c.getColumnType().equals(new DimensionColumnType())){
					ColumnLocalId columnTargetId = c.getRelationship().getTargetColumnId();
					toUseColumn = toUseTable.getColumnById(columnTargetId);
				} else toUseColumn = toUseTable.getColumnById(c.getLocalId());

				if (toUseColumn.getDataType() instanceof GeometryType)
					select.append("ST_AsText(").append(toUseColumn.getName()).append(")");
				if (toUseColumn.getDataType() instanceof DateType)
					select.append(toUseColumn.getName()).append("::text");
				else select.append(toUseColumn.getName());
				select.append(",");
			}

		select.deleteCharAt(select.length() - 1);
		select.append(" from ").append(toUseTable.getName());
		select.append(" LIMIT ").append(ELEMENTS_PER_QUERY).append(" OFFSET ? ");
		return select.toString();
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
