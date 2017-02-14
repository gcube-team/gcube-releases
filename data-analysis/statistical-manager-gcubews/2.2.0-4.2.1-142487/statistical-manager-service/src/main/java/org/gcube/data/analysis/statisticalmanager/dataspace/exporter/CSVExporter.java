package org.gcube.data.analysis.statisticalmanager.dataspace.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.gcube.data.analysis.statisticalmanager.exception.StatisticalManagerException;
import org.gcube.data.analysis.statisticalmanager.persistence.DataBaseManager;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVExporter {

	private static Logger logger = LoggerFactory.getLogger(CSVExporter.class);
	private File file;
	private String tableId;
	
	public CSVExporter(String tableId, File file) {
		this.file = file;
		this.tableId = tableId;
	}
	
	public long exporterToFile() throws StatisticalManagerException, FileNotFoundException,
	SQLException, IOException,Exception {
		
		Connection conn = null;
		CopyManager copyManager = null;
		DataBaseManager db=null;
		try {
			db=DataBaseManager.get();
			conn = DriverManager.getConnection(db.getUrlDB(),
					db.getUsername(), db.getPassword());
			copyManager = new CopyManager((BaseConnection) conn);

			logger.debug("Try to retrieve resource");
			long result = copyManager.copyOut(String.format("COPY %s TO STDOUT CSV HEADER",
					tableId), new FileOutputStream(file));
			logger.debug("Resource retrieved with successful");
			return result;
			
		} catch (SQLException e) {		
			logger.error("Unexpected Error ",e);
			throw new StatisticalManagerException();
		}finally {
			if(db!=null)db.closeConn(conn);
		}
		
	}
	
}
