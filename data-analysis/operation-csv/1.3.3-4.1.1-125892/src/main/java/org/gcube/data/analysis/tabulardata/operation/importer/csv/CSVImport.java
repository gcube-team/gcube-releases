package org.gcube.data.analysis.tabulardata.operation.importer.csv;

import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.ENCODING;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.FIELDMASK;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.HASHEADER;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.SEPARATOR;
import static org.gcube.data.analysis.tabulardata.operation.export.csv.Constants.URL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import net.sf.csv4j.CSVReaderProcessor;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.operation.export.Utils;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVImport extends DataWorker {

	private static Logger logger = LoggerFactory.getLogger(CSVImport.class);

	private String encoding;
	private Character separator;
	private Boolean hasHeader;
	private String url;
	private List<Boolean> fieldMask;


	private CubeManager cubeManager; 
	private DatabaseConnectionProvider connectionProvider;

	public CSVImport(OperationInvocation invocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider) {
		super(invocation);
		retrieveParameters();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;	
	}


	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.operation.worker.Worker#execute()
	 */
	@Override
	protected WorkerResult execute() throws WorkerException {
		logger.trace("starting import CSV");
		Table table =null;
		File csvFile = null ;
		InitializerProcessor initializerProcessor = null;
		try{
			updateProgress(0.1f, "Downloading csv file");
			try {
				csvFile= getInputFile(url);
			} catch (Exception e) {
				logger.error("failed downloading csv file",e);
				throw new WorkerException("failed downloading csv file",e);
			}
			updateProgress(0.3f,"Parsing the file");

			try {
				initializerProcessor = getInitializerProcessor(csvFile);
			}catch (Exception e) {
				logger.error("failed reading csv header",e);
				throw new WorkerException("failed reading csv header",e);
			}
			updateProgress(0.5f, "Creating table");


			try{
				table = createTable(initializerProcessor.getColumns());
			}catch (Exception e) {
				initializerProcessor.getOutputFile().delete();
				logger.error("failed creating table",e);
				throw new WorkerException("failed creating table",e);
			}
			updateProgress(0.6f,"Copying data to table");
			try{
				copy(initializerProcessor.getOutputFile(), table);
			}catch (Exception e) {
				logger.error("failed copying lines to table",e);
				if (table!=null)
					cubeManager.removeTable(table.getId());
				throw new WorkerException("failed copying lines to table",e);
			}

		}finally{
			if (initializerProcessor!=null && initializerProcessor.getOutputFile()!=null)
				try{
					initializerProcessor.getOutputFile().delete();
				}catch(Exception e){
					logger.warn("temporary file (%s) not deleted, check the temp dir",initializerProcessor.getOutputFile().getName(),e);
				}
			if (csvFile!=null)
				try{
					csvFile.delete();
				}catch(Exception e){
					logger.warn("temporary file (%s) not deleted, check the temp dir",csvFile.getName(),e);
				}
		}
		logger.trace("import CSV finished");
		return new ImmutableWorkerResult(table);
	}


	private Table createTable(List<Column> columns) throws Exception{
		TableCreator tableCreator = cubeManager.createTable(new GenericTableType());
		for (Column column : columns)
			tableCreator.addColumn(column);
		return tableCreator.create();
	}

	private InitializerProcessor getInitializerProcessor(File csvFile) throws Exception{
		InitializerProcessor initializer = new InitializerProcessor(fieldMask);

		//starting csv processing
		CSVReaderProcessor csvReaderProcessor = new CSVReaderProcessor();
		csvReaderProcessor.setDelimiter(separator);
		csvReaderProcessor.setHasHeader(hasHeader);
		InputStreamReader isrInitializer =  null;
		try{
			isrInitializer =  new InputStreamReader(new FileInputStream(csvFile), encoding); 
			csvReaderProcessor.processStream(isrInitializer, initializer);
			return initializer;
		}finally{
			if (isrInitializer!=null) isrInitializer.close(); 
			if (initializer!=null) initializer.close();
		}

	}

	private File getInputFile(String storageUrl) throws Exception{

		InputStream inputStream = getInputStreamFromUrl(storageUrl);

		if(inputStream==null) throw new MalformedURLException();
		
		File tempFile = File.createTempFile("import", ".csv");

		OutputStream outputStream = new FileOutputStream(tempFile);

		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = inputStream.read(bytes)) != -1) 
			outputStream.write(bytes, 0, read);

		outputStream.close();
		inputStream.close();
		return tempFile;
	}

	private long copy(File csvFile, Table table) throws Exception {
		PGConnection conn = connectionProvider.getPostgreSQLConnection();
		CopyManager cpManager = conn.getCopyAPI();
		StringBuilder columns = new StringBuilder();
		for (Column c : table.getColumns()) {
			if (!(c.getColumnType() instanceof IdColumnType))
				columns.append(c.getName()).append(",");
		}
		columns.deleteCharAt(columns.length() - 1);
		
		String sqlCmd = String.format("COPY %s ( %s ) FROM STDIN ( FORMAT CSV ,DELIMITER ',', HEADER %b, ENCODING 'UTF-8');", table.getName(),
				columns.toString(), false);
		logger.info("executing copy for csv import with query {}",sqlCmd);
		InputStreamReader inputStreamReader =null;
		try{
			inputStreamReader = new InputStreamReader(new FileInputStream(csvFile), "UTF-8");
			long lines = cpManager.copyIn(sqlCmd, inputStreamReader);
			inputStreamReader.close();
			return lines;
		}finally{
			if (inputStreamReader!=null)
				inputStreamReader.close();
		}
	}

	@SuppressWarnings("unchecked")
	private void retrieveParameters() {
		Map<String, Object> parameters = getSourceInvocation().getParameterInstances();
		url = (String) parameters.get(URL);
		separator = ((String) parameters.get(SEPARATOR)).charAt(0);
		hasHeader = (Boolean) parameters.get(HASHEADER);
		encoding = (String) parameters.get(ENCODING);
		fieldMask = (List<Boolean>) parameters.get(FIELDMASK);
	}

	private InputStream getInputStreamFromUrl(String id) throws Exception{
		IClient client = Utils.getStorageClient();
		return client.get().RFileAsInputStream(id);
	}

}
