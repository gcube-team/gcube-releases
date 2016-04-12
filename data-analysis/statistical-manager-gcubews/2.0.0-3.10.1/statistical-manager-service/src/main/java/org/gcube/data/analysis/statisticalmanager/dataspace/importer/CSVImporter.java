package org.gcube.data.analysis.statisticalmanager.dataspace.importer;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.csv4j.CSVFileProcessor;

import org.gcube.data.analysis.statisticalmanager.persistence.DataBaseManager;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVImporter {
	
	private static Logger logger = LoggerFactory.getLogger(CSVImporter.class);
	
	private File file;
	private boolean hasHeader;
//	private String tableLabel;
	private String tableTemplate;
	private String delimiter;
	private char comment;
	
	
	public CSVImporter(File file, boolean hasHeader, String tableLabel,
			String tableTemplate, String delimiter, String comment) {
		this.file = file;
		this.hasHeader = hasHeader;
//		this.tableLabel = tableLabel;
		this.tableTemplate = tableTemplate;
		this.delimiter = delimiter;
		if (comment != null && !comment.isEmpty())
			this.comment = comment.charAt(0);
		else
			this.comment = '#';
		logger.debug(String.format("Import %s [hasHeader %s, label %s, template %s, delimiter %s, comment %s]",
				file.getAbsoluteFile(),hasHeader,tableLabel,tableTemplate,this.delimiter,this.comment));
	}

	public String toTabularData() throws Exception {
		Connection conn = null;
		PreparedStatement createTable = null;
		String dbTableName = null;
		DataBaseManager db=DataBaseManager.get();
		try {

			conn = DriverManager.getConnection(db.getUrlDB(),
					db.getUsername(),
					db.getPassword());
			conn.setAutoCommit(false);

			dbTableName = tableTemplate + "_ID"
					+ UUID.randomUUID().toString().replace("-", "_");
			dbTableName = dbTableName.toLowerCase();

			String sql = null;

			if (tableTemplate.equals(TableTemplates.GENERIC.toString()))
				sql = getSqlStatmentCreateGeneric(dbTableName);
			else if (tableTemplate.equals(TableTemplates.TIMESERIES.toString())) {
				sql = getSqlStatmentCreateTimeSeriesSecondEdition(dbTableName);
			} else
				if(tableTemplate.equals(TableTemplates.OCCURRENCE_SPECIES.toString()))
				sql=getSqlStatmentCreateOther(dbTableName);
				else
				sql = String.format("create table %s (like %s)", dbTableName,
						tableTemplate);

			logger.debug("SQL create table: " + sql);

			createTable = conn.prepareStatement(sql);
			createTable.executeUpdate();

			
			String sqlCopy=String
					.format("COPY %s FROM STDIN WITH DELIMITER '%s' %s QUOTE '\"' ESCAPE '\\' ",
							dbTableName, delimiter,
							(hasHeader) ? "CSV HEADER" : "CSV");
			logger.debug("SQL IS : "+sqlCopy);
			
			CopyManager copyManager = new CopyManager((BaseConnection) conn);
			copyManager
					.copyIn(sqlCopy,
							new FileInputStream(file));

			conn.commit();

		} catch (Exception e) {
			logger.error("CSV importer error :", e);
			e.printStackTrace();
			db.rollback(conn);
			throw new Exception(e);
		} finally {
			db.closeStatement(createTable);
			db.closeConn(conn);
		}

		return dbTableName;
	}

	@SuppressWarnings("finally")
	private String getSqlStatmentCreateGeneric(String dbTableName)
			throws Exception {

		CSVFileProcessor fp = new CSVFileProcessor();
		CSVLineProcessorGeneric lineProcessor = new CSVLineProcessorGeneric(
				delimiter);
		fp.setHasHeader(hasHeader);
		fp.setComment(comment);
		try {
			fp.processFile(file.getAbsolutePath(), lineProcessor);
		} catch (NullPointerException e) {			
			logger.warn("file "+(file!=null?file.getAbsolutePath():"NULL FILE")+"finished before counted");
		} finally {
			logger.debug("hasHeader " + hasHeader);
			logger.debug("comment" + comment);
			ArrayList<String> sqltypes = new ArrayList<String>();
			logger.debug("Size column" + lineProcessor.firstColumns.size());
			for (ArrayList<String> column : lineProcessor.firstColumns) {
				logger.debug("Print conlumn");

				// printArray(column);
				sqltypes.add(CSVLineProcessorGeneric.getSqlType(column));
				logger.debug("add type ");

			}
			lineProcessor.addSqlType(sqltypes);
			List<String> cols = lineProcessor.getColsName();
			logger.debug("cols.size:" + cols.size());
			logger.debug("addSqlType.size:" + lineProcessor.getSqlType().size());
			logger.debug("Types:");

			// printArray(lineProcessor.getSqlType());
			String args = "";

			// int i = 0;
			// for (String colName : cols) {
			// args += String.format("%s %s,", colName,
			// lineProcessor.getSqlType()
			// .get(i++));
			// }
			int i = 0;
			for (String colName : cols) {

				if (i >= lineProcessor.getSqlType().size())
					args += String.format("%s %s,", colName, "varchar");
				else
					args += String.format("%s %s,", colName, lineProcessor
							.getSqlType().get(i++));
				logger.debug("args" + args);
			}
			String sql = String.format("CREATE TABLE %s ( %s )", dbTableName,
					args.substring(0, args.length() - 1));

			return sql;
		}
	}

	@SuppressWarnings("finally")
	private String getSqlStatmentCreateOther(String dbTableName)
			throws Exception {

		CSVFileProcessor fp = new CSVFileProcessor();
		CSVLineProcessorOcc lineProcessor = new CSVLineProcessorOcc(
				delimiter);
		fp.setHasHeader(hasHeader);
		fp.setComment(comment);
		try {
			fp.processFile(file.getAbsolutePath(), lineProcessor);
		} catch (NullPointerException e) {			
			logger.warn("file "+(file!=null?file.getAbsolutePath():"NULL FILE")+"finished before counted");
		} finally {
			logger.debug("hasHeader " + hasHeader);
			logger.debug("comment" + comment);
			ArrayList<String> sqltypes = new ArrayList<String>();
			logger.debug("Size column" + lineProcessor.firstColumns.size());
			for (ArrayList<String> column : lineProcessor.firstColumns) {
				logger.debug("Print conlumn");

				// printArray(column);
				sqltypes.add(CSVLineProcessorGeneric.getSqlType(column));
				logger.debug("add type ");

			}
			lineProcessor.addSqlType(sqltypes);
			List<String> cols = lineProcessor.getColsName();
			logger.debug("cols.size:" + cols.size());
			logger.debug("addSqlType.size:" + lineProcessor.getSqlType().size());
			logger.debug("Types:");

			// printArray(lineProcessor.getSqlType());
			String args = "";

			// int i = 0;
			// for (String colName : cols) {
			// args += String.format("%s %s,", colName,
			// lineProcessor.getSqlType()
			// .get(i++));
			// }
			int i = 0;
			for (String colName : cols) {

				if (i >= lineProcessor.getSqlType().size())
					args += String.format("%s %s,", colName, "varchar");
				else
					args += String.format("%s %s,", colName, lineProcessor
							.getSqlType().get(i++));
				logger.debug("args" + args);
			}
			lineProcessor.getWellFormaFile(file);
			file = lineProcessor.getFile();
			logger.debug("***reassingned File path is "
					+ file.getAbsolutePath());
//			String sql = String.format("CREATE TABLE %s ( %s )", dbTableName,
//					args.substring(0, args.length() - 1));
//			String sql = String.format("create table %s (like %s)", dbTableName,
//					tableTemplate);
			String sql = "CREATE TABLE " + dbTableName + 
					"(institutionCode VARCHAR,"+
					"collectionCode VARCHAR," +
					"catalogueNumber VARCHAR," +
					"dataSet VARCHAR," +
					"dataProvider VARCHAR," +
					"dataSource VARCHAR," +
					"scientificNameAuthorship VARCHAR," +
					"identifiedBy VARCHAR,"+
					"credits VARCHAR," +
					"recordedBy VARCHAR," +
					"eventDate TIMESTAMP," +
					"modified TIMESTAMP," +
					"scientificName VARCHAR," +	
					"kingdom VARCHAR," +
					"family VARCHAR," +
					"locality VARCHAR," +
					"country VARCHAR," +
					"citation VARCHAR," +
					"decimalLatitude double precision," +
					"decimalLongitude double precision," +
					"coordinateUncertaintyInMeters VARCHAR," +
					"maxDepth double precision," +
					"minDepth double precision," +
					"basisOfRecord VARCHAR)";

			return sql;
		}
	}
	
	
	@SuppressWarnings("finally")
	private String getSqlStatmentCreateTimeSeriesSecondEdition(
			String dbTableName) throws Exception {

		CSVFileProcessor fp = new CSVFileProcessor();
		CSVLineProcessorTimeSeries lineProcessor = new CSVLineProcessorTimeSeries(
				delimiter);
		fp.setHasHeader(hasHeader);
		fp.setComment(comment);

		try {
			fp.processFile(file.getAbsolutePath(), lineProcessor);
		} catch (NullPointerException e) {
			//e.printStackTrace();
			logger.debug("file finished before counted");
		} finally {

			logger.debug("hasHeader " + hasHeader);
			logger.debug("comment" + comment);
			ArrayList<String> sqltypes = new ArrayList<String>();
			logger.debug("Size column" + lineProcessor.firstColumns.size());
			for (ArrayList<String> column : lineProcessor.firstColumns) {
				logger.debug("Print conlumn");

				// printArray(column);
				sqltypes.add(CSVLineProcessorTimeSeries.getSqlType(column));
				logger.debug("added type ");

			}
			lineProcessor.addSqlType(sqltypes);
			List<String> cols = lineProcessor.getColsName();
			logger.debug("cols.size:" + cols.size());
			logger.debug("addSqlType.size:" + lineProcessor.getSqlType().size());
			logger.debug("Types:");

			// printArray(lineProcessor.getSqlType());

			// file = lineProcessor.getFile();

			logger.debug("hasHeader " + hasHeader);
			logger.debug("comment" + comment);

			String args = "";
			int i = 0;
			int indexTime = 0;
			boolean thereIstTimeColumn = false;
			for (String colName : cols) {

				logger.debug("***COL name ***" + cols);

				if (i >= lineProcessor.getSqlType().size())
					args += String.format("%s %s,", colName, "varchar");
				else {
					if (colName.equals("time")) {
						logger.debug("***IS TIME ***");

						args += String.format("%s %s,", colName,
								"timestamp without time zone");
						thereIstTimeColumn = true;
						indexTime = i;
						i++;
					}

					else
						args += String.format("%s %s,", colName, lineProcessor
								.getSqlType().get(i++));
				}
				logger.debug("args" + args);
			}
			if (!thereIstTimeColumn)
				throw new Exception("There isn't the \"Time\" column in Timeseries dataset.");

			lineProcessor.getWellFormaFile(file, indexTime);
			file = lineProcessor.getFile();
			logger.debug("***reassingned File path is "
					+ file.getAbsolutePath());
			String sql = String.format("CREATE TABLE %s ( %s )", dbTableName,
					args.substring(0, args.length() - 1));

			return sql;
		}
	}

//	private String getSqlStatmentCreateTimeSeries(String dbTableName)
//			throws Exception {
//
//		CSVFileProcessor fp = new CSVFileProcessor();
//		CSVLineProcessorTimeSeries lineProcessor = new CSVLineProcessorTimeSeries(
//				delimiter);
//		fp.setHasHeader(hasHeader);
//		fp.setComment(comment);
//		try {
//			fp.processFile(file.getAbsolutePath(), lineProcessor);
//		} catch (Exception e) {
//
//		}
//
//		if (!lineProcessor.isTimeFound())
//			throw new Exception();
//
//		file = lineProcessor.getFile();
//
//		logger.debug("hasHeader " + hasHeader);
//		logger.debug("comment" + comment);
//
//		List<String> cols = lineProcessor.getColsName();
//
//		String args = "";
//		int i = 0;
//		for (String colName : cols) {
//			if (i >= lineProcessor.getSqlType().size())
//				args += String.format("%s %s,", colName, "varchar");
//			else
//				args += String.format("%s %s,", colName, lineProcessor
//						.getSqlType().get(i++));
//			logger.debug("args" + args);
//		}
//
//		String sql = String.format("CREATE TABLE %s ( %s )", dbTableName,
//				args.substring(0, args.length() - 1));
//
//		return sql;
//	}
//
//	private void printArray(List<String> a) {
//
//		logger.debug(a.toString());
//
//	}
}
