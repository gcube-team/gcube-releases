package org.gcube.dataanalysis.ecoengine.evaluation.bioclimate;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.DataTypeRecognizer;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.hibernate.SessionFactory;

/**
 * checks if two tables are equal checks numbers at the second decimal position
 */
public class InterpolateTables {

	// connection setup

	protected String temporaryDirectory;
	// selection query
	public static String selectElementsQuery = "select %1$s from %2$s order by %3$s";
	public static String selectDestElementsQuery = "select %1$s from %2$s where %3$s";
	public float status;
	private String[] interpolatedTables;
	private LexicalEngineConfiguration config;
	// database connections
	protected SessionFactory referencedbConnection;
	private String configPath;
	private File[] producedfiles;

	public static enum INTERPOLATIONFUNCTIONS {
		LINEAR, PARABOLIC
	};

	public String[] getInterpolatedTables() {
		return interpolatedTables;
	}

	// init connections
	public InterpolateTables(String configPath, String persistencePath, String databaseURL, String databaseUserName, String databasePassword) throws Exception {
		this.configPath = configPath;
		this.temporaryDirectory = persistencePath;
		if (!configPath.endsWith("/"))
			configPath += "/";
		if (!persistencePath.endsWith("/"))
			this.temporaryDirectory += "/";

		AnalysisLogger.setLogger(configPath + AlgorithmConfiguration.defaultLoggerFile);

		AnalysisLogger.getLogger().debug("Initialization complete: persistence path " + persistencePath);

		config = new LexicalEngineConfiguration();
		config.setDatabaseURL(databaseURL);
		config.setDatabaseUserName(databaseUserName);
		config.setDatabasePassword(databasePassword);
	}

	// tables have to present the same structure
	public void interpolate(String table1, String table2, int intervals, INTERPOLATIONFUNCTIONS function, int startYear, int endYear) throws Exception {

		try {
			if (intervals == 1) {
				interpolatedTables = new String[2];
				interpolatedTables[0] = table1;
				interpolatedTables[1] = table2;
				AnalysisLogger.getLogger().debug("NO TABLES TO PRODUCE");
			} else {
				referencedbConnection = DatabaseFactory.initDBConnection(configPath + AlgorithmConfiguration.defaultConnectionFile, config);
				AnalysisLogger.getLogger().debug("ReferenceDB initialized");
				status = 0f;
				AnalysisLogger.getLogger().debug("Interpolating from " + table1 + " to " + table2);
				DatabaseUtils utils = new DatabaseUtils(referencedbConnection);
				// analyze table and take information about it
				String createTableStatement = utils.buildCreateStatement(table1, "%1$s");
				AnalysisLogger.getLogger().debug("Create Statement for table " + table1 + ": " + createTableStatement);
				int numberOfColumns = utils.getColumnDecriptions().size();
				// initialize the map of columns to write
				List<List<StringBuffer>> outputFiles = new ArrayList<List<StringBuffer>>();
				for (int g = 0; g < intervals - 2; g++) {
					outputFiles.add(new ArrayList<StringBuffer>());
				}
				float statusstep = 60f / (float) numberOfColumns;
				// DatabaseFactory.executeSQLUpdate(creationStatement, referencedbConnection);
				// take the columns
				for (int j = 0; j < numberOfColumns; j++) {
					// take column name
					String gotColumn = utils.getColumnName(j);
					String gotColumnType = utils.getColumnType(j);
					String javatype = DataTypeRecognizer.transformTypeFromDB(gotColumnType);
					String takeF = DatabaseUtils.getOrderedElements(table1, utils.getPrimaryKey(), gotColumn);
					String takeS = DatabaseUtils.getOrderedElements(table2, utils.getPrimaryKey(), gotColumn);
					AnalysisLogger.getLogger().debug("Taking First column->" + takeF);
					AnalysisLogger.getLogger().debug("Taking Second column->" + takeS);
					
					List<Object> takeFirstColumn = DatabaseFactory.executeSQLQuery(takeF, referencedbConnection);
					List<Object> takeSecondColumn = DatabaseFactory.executeSQLQuery(takeS, referencedbConnection);

					AnalysisLogger.getLogger().debug("First column elements size->" + takeFirstColumn.size());
					AnalysisLogger.getLogger().debug("Second column elements size->" + takeSecondColumn.size());
					
					
					// only if data are of numeric type, perform calculation
					if (javatype.equals(BigDecimal.class.getName())) {
						AnalysisLogger.getLogger().debug("interpolating -> " + gotColumn);

						List<List<Object>> interpolations = interpolateColumns(takeFirstColumn, takeSecondColumn, intervals, gotColumnType, function);

						for (int i = 1; i < intervals - 1; i++) {
							// create the interpolation table
							String tableInterp = table1 + "_" + (i);
							// for each column to substitute
							List<Object> columnToSub = interpolations.get(i);
							if (columnToSub.size() > 0) {
								AnalysisLogger.getLogger().debug("UPDATE TABLE " + tableInterp + " ON COLUMN " + gotColumn);
								addColumnToTable(outputFiles.get(i - 1), columnToSub, true);
							} else {
								AnalysisLogger.getLogger().debug("DOESN'T CHANGE TABLE " + tableInterp + " COLUMN " + gotColumn);
								addColumnToTable(outputFiles.get(i - 1), takeFirstColumn, true);
							}
						}
					}
					// else update all the tables
					else {
						for (int i = 0; i < intervals - 2; i++) {
							addColumnToTable(outputFiles.get(i), takeFirstColumn, false);
						}
					}

					status = status + statusstep;

				}
				status = 60f;
				AnalysisLogger.getLogger().debug("WRITING ALL THE BUFFERS");
				writeAllStringBuffersToFiles(table1, outputFiles, function, startYear, endYear);

				statusstep = 40f / (float) producedfiles.length;

				interpolatedTables = new String[producedfiles.length + 2];
				interpolatedTables[0] = table1;

				for (int i = 0; i < producedfiles.length; i++) {
					String filename = producedfiles[i].getName();
					filename = filename.substring(0, filename.lastIndexOf(".")).replace(" ", "");
					interpolatedTables[i + 1] = filename;
					/*create Table from file*/
					/* OLD CODE FOR LOCAL DB
					String copyFileQuery = DatabaseUtils.copyFileToTableStatement(temporaryDirectory + producedfiles[i].getName(), filename);
					AnalysisLogger.getLogger().debug("CREATING TABLE->" + filename);
					DatabaseFactory.executeSQLUpdate(String.format(createTableStatement, filename), referencedbConnection);
					AnalysisLogger.getLogger().debug("FULFILLING TABLE->" + filename + ": " + copyFileQuery);
					DatabaseFactory.executeSQLUpdate(copyFileQuery, referencedbConnection);
					*/
					AnalysisLogger.getLogger().debug("CREATING TABLE->" + filename);
					DatabaseFactory.executeSQLUpdate(String.format(createTableStatement, filename), referencedbConnection);
					AnalysisLogger.getLogger().debug("COPYING TABLE->" + filename);
					DatabaseUtils.createRemoteTableFromFile(producedfiles[i].getAbsolutePath(),filename,";",false,config.getDatabaseUserName(),config.getDatabasePassword(),config.getDatabaseURL());
					
					status = Math.min(status + statusstep, 99);
				}
				
				AnalysisLogger.getLogger().debug("DELETING ALL TEMPORARY FILES");
				for (int i = 0; i < producedfiles.length; i++) {
					producedfiles[i].delete();
				}
				
				interpolatedTables[interpolatedTables.length - 1] = table2;

				AnalysisLogger.getLogger().debug("ALL TABLES HAVE BEEN PRODUCED");
			}//end else control on the number of intervals
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			// close connections
			if (referencedbConnection!=null)
				try{
				referencedbConnection.close();
				}catch(Exception e){}
			status = 100f;
		}
	}

	public float getStatus() {
		return status;
	}

	private void addColumnToTable(List<StringBuffer> rows, List<Object> elements, boolean isNumber) {
		int size = elements.size();
		for (int i = 0; i < size; i++) {
			Object[] couple = (Object[]) elements.get(i);
			String value = "" + couple[1];
			StringBuffer buffer = null;
			if (i >= rows.size()) {
				buffer = new StringBuffer();
				if (isNumber && (value == null) || (value.length() == 0))
					buffer.append("0");
				else
					buffer.append(value);
				rows.add(buffer);
			} else {
				buffer = rows.get(i);
				buffer.append(";" + value);
			}

		}
	}

	private void writeAllStringBuffersToFiles(String initialFile, List<List<StringBuffer>> outputFiles, INTERPOLATIONFUNCTIONS function, int startYear, int endYear) throws Exception {
		int numOfFiles = outputFiles.size();
		int yearStep = (int) ((float) (endYear - startYear) / (float) (numOfFiles + 1));
		producedfiles = new File[numOfFiles];
		for (int i = 0; i < numOfFiles; i++) {
			List<StringBuffer> rows = outputFiles.get(i);
			StringBuffer completeFile = new StringBuffer();
			int nrows = rows.size();
			for (int k = 0; k < nrows; k++) {
				completeFile.append(rows.get(k) + "\n");
			}
			int yearCals = startYear + (i + 1) * yearStep;
			if (yearCals == endYear)
				yearCals = endYear - 1;

			String filename = temporaryDirectory  + "interp_" + (yearCals) + "_" + function.name() + "_" + i + System.currentTimeMillis() + ".csv";
			FileTools.saveString(filename, completeFile.toString(), true, "UTF-8");
			producedfiles[i] = new File(filename);
			System.out.println("PRODUCED FILE TO COPY "+producedfiles[i]);
		}
	}

	// interpolates parallel columns
	private List<List<Object>> interpolateColumns(List<Object> col1, List<Object> col2, int intervals, String type, INTERPOLATIONFUNCTIONS function) {
		int elements = col1.size();
		ArrayList<List<Object>> columns = new ArrayList<List<Object>>();
		for (int i = 0; i < intervals; i++) {
			columns.add(new ArrayList<Object>());
		}
		// produce a column couple for each interval
		boolean interping = true;
		for (int i = 0; i < elements; i++) {
			Object[] row1 = (Object[]) col1.get(i);
			Object[] row2 = (Object[]) col2.get(i);
			double firstNum = row1[1] != null ? Double.parseDouble("" + row1[1]) : 0d;
			double secondNum = row2[1] != null ? Double.parseDouble("" + row2[1]) : 0d;
			Object key = row1[0];
			double[] interpolation = null;
			if (firstNum != secondNum) {
				if (interping) {
					AnalysisLogger.getLogger().debug("Interpolating ... ");
					interping = false;
				}

				if (function == INTERPOLATIONFUNCTIONS.LINEAR)
					interpolation = Operations.linearInterpolation(firstNum, secondNum, intervals);
				else if (function == INTERPOLATIONFUNCTIONS.PARABOLIC)
					interpolation = Operations.parabolicInterpolation(firstNum, secondNum, intervals);
			}

			for (int j = 0; j < intervals; j++) {
				Object[] couple = new Object[2];
				couple[0] = key;
				double interp = firstNum;
				if (interpolation != null)
					interp = interpolation[j];

				if (type.equals("integer"))
					couple[1] = Math.round(interp);
				else {
					interp = MathFunctions.roundDecimal(interp, 2);
					couple[1] = interp;
				}
				columns.get(j).add(couple);
			}

		}

		return columns;
	}

	public static void main(String[] args) throws Exception {

		String configPath = "./cfg/";
		String persistencePath = "/win/";
		/*
		String databaseUrl = "jdbc:postgresql://localhost/testdb";
		String databaseUser = "gcube";
		String databasePassword = "d4science2";
		*/
		String databaseUrl = "jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated";
		String databaseUser = "utente";
		String databasePassword = "d4science";
		InterpolateTables interp = new InterpolateTables(configPath, persistencePath, databaseUrl, databaseUser, databasePassword);

		interp.interpolate("hcaf_d", "hcaf_d_2050", 7, INTERPOLATIONFUNCTIONS.LINEAR, 2012, 2050);

	}

}
