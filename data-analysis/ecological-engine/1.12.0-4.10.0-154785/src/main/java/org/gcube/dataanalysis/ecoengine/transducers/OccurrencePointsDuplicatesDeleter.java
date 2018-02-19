package org.gcube.dataanalysis.ecoengine.transducers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;

public class OccurrencePointsDuplicatesDeleter extends OccurrencePointsMerger {

	String tableName;
	List<String> records = new ArrayList<String>();

	public OccurrencePointsDuplicatesDeleter() {

	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<TableTemplates> templatesOccurrence = new ArrayList<TableTemplates>();
		templatesOccurrence.add(TableTemplates.OCCURRENCE_SPECIES);
		// occurrence points tables
		PrimitiveType p0 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, finalTableNameL, "the name of the produced table", "DeletedOcc_");
		InputTable p1 = new InputTable(templatesOccurrence, tableNameF, "the table containing the occurrence points (up to 100 000 points)", "");
		ColumnType p3 = new ColumnType(tableNameF, longitudeColumn, "column with longitude values", "decimallongitude", false);
		ColumnType p4 = new ColumnType(tableNameF, latitudeColumn, "column with latitude values", "decimallatitude", false);
		ColumnType p5 = new ColumnType(tableNameF, recordedByColumn, "column with RecordedBy values", "recordedby", false);
		ColumnType p6 = new ColumnType(tableNameF, scientificNameColumn, "column with Scientific Names", "scientificname", false);
		ColumnType p7 = new ColumnType(tableNameF, eventDateColumn, "column with EventDate values", "eventdate", false);
		ColumnType p8 = new ColumnType(tableNameF, lastModificationColumn, "column with Modified values", "modified", false);
		ServiceType p9 = new ServiceType(ServiceParameters.RANDOMSTRING, finalTableNameF, "name of the resulting table", "processedOccurrences_");
		PrimitiveType p10 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, spatialTolerance, "the tolerance in degree for assessing that two points could be the same", "0.5");
		PrimitiveType p11 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, confidence, "the overall acceptance similarity threshold over which two points are the same - from 0 to 100", "80");

		List<StatisticalType> inputs = new ArrayList<StatisticalType>();
		inputs.add(p0);
		inputs.add(p1);
		inputs.add(p3);
		inputs.add(p4);
		inputs.add(p5);
		inputs.add(p6);
		inputs.add(p7);
		inputs.add(p8);
		inputs.add(p9);
		inputs.add(p10);
		inputs.add(p11);

		DatabaseType.addDefaultDBPars(inputs);
		return inputs;
	}

	@Override
	public String getDescription() {
		return "A transducer algorithm that produces a duplicate free table of species occurrence points where duplicates have been identified via user defined comparison thresholds. Works with up to 100 000 points";
	}

	@Override
	public void init() throws Exception {

		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		lonFld = config.getParam(longitudeColumn);
		latFld = config.getParam(latitudeColumn);
		recordedByFld = config.getParam(recordedByColumn);
		scientificNameFld = config.getParam(scientificNameColumn);
		eventDatFld = config.getParam(eventDateColumn);
		modifDatFld = config.getParam(lastModificationColumn);
		tableName = config.getParam(tableNameF);
		rightTableName = tableName;
		leftTableName = tableName;
		finalTableName = config.getParam(finalTableNameF);
		finalTableLabel = config.getParam(finalTableNameL);
		spatialToleranceValue = Float.parseFloat(config.getParam(spatialTolerance));
		confidenceValue = Float.parseFloat(config.getParam(confidence));

		objectstoinsert = new ArrayList<OccurrencePointsMerger.OccurrenceRecord>();
		objectstodelete = new ArrayList<OccurrencePointsMerger.OccurrenceRecord>();
		records = new ArrayList<String>();
		status = 0;
	}

	protected boolean isBetterThan(OccurrenceRecord leftOcc, OccurrenceRecord rightOcc) {
		if (((leftOcc.modifdate != null) && (rightOcc.modifdate != null) && leftOcc.modifdate.before(rightOcc.modifdate)) || (leftOcc.modifdate == null) && (rightOcc.modifdate != null))
			return false;
		else if ((leftOcc.modifdate != null) && (rightOcc.modifdate != null) && leftOcc.modifdate.after(rightOcc.modifdate) || (leftOcc.modifdate != null) && (rightOcc.modifdate == null))
			return true;
		else
			return false;
	}

	@Override
	protected void prepareFinalTable() throws Exception {
		DatabaseFactory.executeSQLUpdate(DatabaseUtils.createBlankTableFromAnotherStatement(tableName, finalTableName), dbconnection);
	}

	public void takeFullRanges() {
		// take the elements from sx table
		AnalysisLogger.getLogger().info("Taking elements from left table: " + leftTableName);
		leftRows = DatabaseFactory.executeSQLQuery(DatabaseUtils.getDinstictElements(tableName, columns.toString(),"")+" limit 100000", dbconnection);
	}

	public void takeRange(int offsetLeft, int numLeft, int offsetRight, int numRight) {
		// take the elements from sx table
		AnalysisLogger.getLogger().info("Taking elements from left table: " + leftTableName);
		leftRows = DatabaseFactory.executeSQLQuery(DatabaseUtils.getDinstictElements(leftTableName, columns.toString(), "offset " + offsetLeft + " limit " + numLeft), dbconnection);
	}

	public void computeRange() throws Exception {
		try {
			// for each element in dx
			AnalysisLogger.getLogger().trace("Processing");
			status = 10;
			int similaritiesCounter = 0;
			int allrows = 0;
			if (leftRows!=null)
				allrows = leftRows.size();
			int rowcounter = 0;
			if (allrows > 0) {
				for (Object row : leftRows) {
					// transform into an occurrence object
					OccurrenceRecord testOcc = row2OccurrenceRecord((Object[]) row);
					// for each element in the white list
					int k = 0;
					int insertedSize = objectstoinsert.size();
					boolean candidate = true;

					while (k < insertedSize) {
						OccurrenceRecord yetInserted = objectstoinsert.get(k);
						float prob = extProb(yetInserted, testOcc);
						// if the occurrence is better than the the yet inserted then delete the yet inserted and in the end insert the new occ
						if (prob >= confidenceValue) {
							similaritiesCounter++;
							if (isBetterThan(testOcc, yetInserted)) {
								AnalysisLogger.getLogger().trace("Found a similarity with P=" + prob + " between (" + "\"" + testOcc.scientificName + "\"" + "," + testOcc.x + "\"" + "," + "\"" + testOcc.y + "\"" + "," + "\"" + testOcc.recordedby + "\"" + "," + "\"" + convert2conventionalFormat(testOcc.eventdate) + "\"" + ") VS " + "(" + "\"" + yetInserted.scientificName + "\"" + "," + "\"" + yetInserted.x + "\"" + "," + "\"" + yetInserted.y + "\"" + "," + "\"" + yetInserted.recordedby + "\"" + "," + "\"" + convert2conventionalFormat(yetInserted.eventdate) + "\"" + ")");
								objectstoinsert.remove(k);
								k--;
								insertedSize--;

							}
							// if there is yet one better then discard the testOcc
							else {
								candidate = false;
								break;
							}
						}

						k++;
					}

					if (candidate)
						objectstoinsert.add(testOcc);

					status = Math.min(90, 10f + (80 * ((float) rowcounter) / ((float) allrows)));
					rowcounter++;
				}

				AnalysisLogger.getLogger().trace("Found " + similaritiesCounter + " similarities on " + allrows + " distinct elements");
				status = 90;
				// transform the complete list into a table
				persist();
				// close DB connection
			}
		} catch (Exception e) {
			AnalysisLogger.getLogger().error("error",e);
			throw e;
		} finally {
			shutdown();
			status = 100;
			AnalysisLogger.getLogger().trace("Occ Points Processing Finished and db closed");
		}

	}

	public void computeOLD() throws Exception {

		try {
			// init DB connection
			AnalysisLogger.getLogger().trace("Initializing DB Connection");
			dbconnection = DatabaseUtils.initDBSession(config);
			AnalysisLogger.getLogger().trace("Taking Table Description");
			AnalysisLogger.getLogger().trace("Creating final table: " + finalTableName);
			// create new merged table
			try {
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(finalTableName), dbconnection);
			} catch (Exception e1) {
			}
			AnalysisLogger.getLogger().trace("Preparing table: " + finalTableName);
			prepareFinalTable();
			AnalysisLogger.getLogger().trace("Extracting columns from: " + finalTableName);
			extractColumnNames();
			AnalysisLogger.getLogger().trace("Taken Table Description: " + columns);
			// take distinct elements from table
			AnalysisLogger.getLogger().trace("Taking elements from table: " + tableName);
			List<Object> rows = DatabaseFactory.executeSQLQuery(DatabaseUtils.getDinstictElements(tableName, columns.toString(), ""), dbconnection);
			// for each element in dx
			AnalysisLogger.getLogger().trace("Processing");
			status = 10;
			int similaritiesCounter = 0;
			int allrows = rows.size();
			int rowcounter = 0;
			;
			for (Object row : rows) {
				// transform into an occurrence object
				OccurrenceRecord testOcc = row2OccurrenceRecord((Object[]) row);
				// for each element in the white list
				int k = 0;
				int insertedSize = objectstoinsert.size();
				boolean candidate = true;

				while (k < insertedSize) {
					OccurrenceRecord yetInserted = objectstoinsert.get(k);
					float prob = extProb(yetInserted, testOcc);
					// if the occurrence is better than the the yet inserted then delete the yet inserted and in the end insert the new occ
					if (prob >= confidenceValue) {
						similaritiesCounter++;
						if (isBetterThan(testOcc, yetInserted)) {
							AnalysisLogger.getLogger().trace("Found a similarity with P=" + prob + " between (" + "\"" + testOcc.scientificName + "\"" + "," + testOcc.x + "\"" + "," + "\"" + testOcc.y + "\"" + "," + "\"" + testOcc.recordedby + "\"" + "," + "\"" + convert2conventionalFormat(testOcc.eventdate) + "\"" + ") VS " + "(" + "\"" + yetInserted.scientificName + "\"" + "," + "\"" + yetInserted.x + "\"" + "," + "\"" + yetInserted.y + "\"" + "," + "\"" + yetInserted.recordedby + "\"" + "," + "\"" + convert2conventionalFormat(yetInserted.eventdate) + "\"" + ")");
							objectstoinsert.remove(k);
							k--;
							insertedSize--;

						}
						// if there is yet one better then discard the testOcc
						else {
							candidate = false;
							break;
						}
					}

					k++;
				}

				if (candidate)
					objectstoinsert.add(testOcc);

				status = Math.min(90, 10f + (80 * ((float) rowcounter) / ((float) allrows)));
				rowcounter++;
			}

			AnalysisLogger.getLogger().trace("Found " + similaritiesCounter + " similarities on " + allrows + " distinct elements");
			status = 90;
			// transform the complete list into a table
			persist();
			// close DB connection
		} catch (Exception e) {
			AnalysisLogger.getLogger().trace("An error occurred " + e.getLocalizedMessage());
			throw e;
		} finally {
			if (dbconnection != null)
				dbconnection.close();
			status = 100;
			AnalysisLogger.getLogger().trace("Occ Points Processing Finished and db closed");
		}
	}

	public void postProcess() throws Exception {

	}
}
