package org.gcube.dataanalysis.ecoengine.transducers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.DateGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.contentmanagement.lexicalmatcher.utils.DistanceCalculator;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.hibernate.SessionFactory;

public class OccurrencePointsMerger implements Transducerer {

	static protected String finalTableNameL = "final_Table_Name";
	static protected String longitudeColumn = "longitudeColumn";
	static protected String latitudeColumn = "latitudeColumn";
	static protected String recordedByColumn = "recordedByColumn";
	static protected String scientificNameColumn = "scientificNameColumn";
	static protected String eventDateColumn = "eventDateColumn";
	static protected String lastModificationColumn = "lastModificationColumn";
	static protected String rightTableNameF = "rightTableName";
	static protected String leftTableNameF = "leftTableName";
	static protected String finalTableNameF = "finalTableName";
	static protected String spatialTolerance = "spatialTolerance";
	static protected String confidence = "confidence";
	//NOTE: on local computer we should set SET datestyle = "ISO, MDY";
	static protected String sqlDateFormat = "MM/DD/YYYY HH24:MI:SS";
	static protected String javaDateFormat = "MM/dd/yyyy HH:mm:ss";
	static protected String tableNameF = "OccurrencePointsTableName";
	
	protected List<OccurrenceRecord> records_left;
	protected List<OccurrenceRecord> records_right;
	protected AlgorithmConfiguration config;

	protected String lonFld;
	protected String latFld;
	protected String recordedByFld;
	protected String scientificNameFld;
	protected String eventDatFld;
	protected String modifDatFld;
	protected String leftTableName;
	protected String rightTableName;
	protected String finalTableName;
	protected String finalTableLabel;
	protected float spatialToleranceValue;
	protected float confidenceValue;
	protected StringBuffer columns;
	protected List<OccurrenceRecord> objectstoinsert;
	protected List<OccurrenceRecord> objectstodelete;
	protected List<Object> columnsNames;
	protected SessionFactory dbconnection;
	protected float status;
	protected boolean firstbest;

	public OccurrencePointsMerger() {
		firstbest = true;
	}

	protected class OccurrenceRecord {

		public String scientificName;
		public String recordedby;
		public Calendar eventdate;
		public Calendar modifdate;
		// public String locality;
		// public String country;
		public double x;
		public double y;
		public String x$;
		public String y$;
		// Map<String,String> metadata;
		public List<String> otherValues;

		public OccurrenceRecord() {
			otherValues = new ArrayList<String>();
		}
	}

	public static String convert2conventionalFormat(Calendar date) {
		if (date == null)
			return "";
		SimpleDateFormat formatter = new SimpleDateFormat(javaDateFormat);
		String formattedDate = formatter.format(new Date(date.getTimeInMillis()));
		return formattedDate;
	}

	boolean displaydateconvert = true;

	public OccurrenceRecord row2OccurrenceRecord(Object[] row) {
		OccurrenceRecord record = new OccurrenceRecord();
		int index = 0;

		for (Object name : columnsNames) {
			String name$ = "" + name;
			String value$ = null;
			if (row[index]!=null)
				value$ = "" + row[index];
			if (name$.equalsIgnoreCase(lonFld)) {
				record.x = Double.parseDouble(value$);
				record.x$ = value$;
			} else if (name$.equalsIgnoreCase(latFld)) {
				record.y = Double.parseDouble(value$);
				record.y$ = value$;
			} else if (name$.equalsIgnoreCase(recordedByFld)) {
				record.recordedby = value$;
			} else if (name$.equalsIgnoreCase(scientificNameFld)) {
				record.scientificName = value$;
			} else if (name$.equalsIgnoreCase(eventDatFld)) {
				if ((value$ == null) || (value$.length() == 0)) {
					record.eventdate = null;
				} else {
					/*
					 * SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy KK:mm a",Locale.UK); try { Date d = (Date) formatter.parse(value$); Calendar cal = Calendar.getInstance(); cal.setTime(d); System.out.println("From "+value$+"->"+(cal.get(Calendar.MONTH)+1)+" "+cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.YEAR)+" "+cal.get(Calendar.HOUR)+" "+cal.get(Calendar.MINUTE)); // System.out.println("->"+cal.toString()); } catch (ParseException e) { // TODO Auto-generated catch block e.printStackTrace(); }
					 */
					record.eventdate = DateGuesser.convertDate(value$);
					if (displaydateconvert) {
						AnalysisLogger.getLogger().info("From " + value$ + "->" + convert2conventionalFormat(record.eventdate) + " pattern " + DateGuesser.getPattern(value$));
						displaydateconvert = false;
					}

				}
			} else if (name$.equalsIgnoreCase(modifDatFld)) {
				record.modifdate = DateGuesser.convertDate(value$);
			} else
				record.otherValues.add(value$);

			index++;
		}

		return record;
	}
	
	protected String takeEssential(OccurrenceRecord record) {
		OccurrenceRecord record2 = new OccurrenceRecord();
		record2.scientificName=record.scientificName;
		record2.recordedby=record.recordedby;
		record2.eventdate=record.eventdate;
		record2.modifdate=record.modifdate;
		record2.x=record.x;
		record2.y=record.y;
		record2.x$=record.x$;
		record2.y$=record.y$;
		return occurrenceRecord2String(record2);
	}
	
	public String occurrenceRecord2String(OccurrenceRecord record) {
		StringBuffer buffer = new StringBuffer();
		int index = 0;
		int k = 0;
		int nNames = columnsNames.size();
		for (Object name : columnsNames) {

			String name$ = "" + name;
			String value$ = "NULL";
			if (name$.equalsIgnoreCase(lonFld)) {
				value$ = "'" + record.x$ + "'";
			} else if (name$.equalsIgnoreCase(latFld)) {
				value$ = "'" + record.y$ + "'";
			} else if (name$.equalsIgnoreCase(recordedByFld)) {
				if (record.recordedby != null)
					value$ = "'" + record.recordedby.replace("'", "") + "'";
			} else if (name$.equalsIgnoreCase(scientificNameFld)) {
				if (record.scientificName != null)
					value$ = "'" + record.scientificName.replace("'", "") + "'";
			} else if (name$.equalsIgnoreCase(eventDatFld)) {
				if (record.eventdate != null) {
					String dat = convert2conventionalFormat(record.eventdate);
					if ((dat != null) && (dat.length() > 0))
						value$ = "'" + convert2conventionalFormat(record.eventdate) + "'";
					else
						value$ = "NULL";
					// value$="'"+record.eventdate.getTimeInMillis()+"'";
				}
			} else if (name$.equalsIgnoreCase(modifDatFld)) {
				if (record.modifdate != null) {
					String dat = convert2conventionalFormat(record.modifdate);
					if ((dat != null) && (dat.length() > 0))
						value$ = "'" + convert2conventionalFormat(record.modifdate) + "'";
					else
						value$ = "NULL";
					// value$="'"+record.modifdate.getTimeInMillis()+"'";
				}
			} else {
				if ((record.otherValues != null)&&(record.otherValues.size()>0)) {
					String v = record.otherValues.get(k);
					if ((v!=null)&&(v.length()>0))
							value$ = "'" + v.replace("'", "") + "'";
					
					k++;
				}
			}
			if (value$.equals("'null'"))
				value$ = "NULL";

			buffer.append(value$);
			if (index < nNames - 1) {
				buffer.append(",");
			}

			index++;
		}

		return buffer.toString();
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<TableTemplates> templatesOccurrence = new ArrayList<TableTemplates>();
		templatesOccurrence.add(TableTemplates.OCCURRENCE_SPECIES);
		// occurrence points tables
		PrimitiveType p0 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, finalTableNameL, "the name of the produced table", "Occ_");

		InputTable p1 = new InputTable(templatesOccurrence, leftTableNameF, "the First table containing the occurrence points (up to 10 000)", "");
		InputTable p2 = new InputTable(templatesOccurrence, rightTableNameF, "the Second table containing the occurrence points (up to 10 000)", "");

		// string parameters
		ColumnType p3 = new ColumnType(leftTableNameF, longitudeColumn, "column with longitude values", "decimallongitude", false);
		ColumnType p4 = new ColumnType(leftTableNameF, latitudeColumn, "column with latitude values", "decimallatitude", false);
		ColumnType p5 = new ColumnType(leftTableNameF, recordedByColumn, "column with RecordedBy values", "recordedby", false);
		ColumnType p6 = new ColumnType(leftTableNameF, scientificNameColumn, "column with Scientific Names", "scientificname", false);
		ColumnType p7 = new ColumnType(leftTableNameF, eventDateColumn, "column with EventDate values", "eventdate", false);
		ColumnType p8 = new ColumnType(leftTableNameF, lastModificationColumn, "column with Modified values", "modified", false);
		ServiceType p9 = new ServiceType(ServiceParameters.RANDOMSTRING, finalTableNameF, "name of the resulting table", "processedOccurrences_");
		PrimitiveType p10 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, spatialTolerance, "the tolerance in degree for assessing that two points could be the same", "0.5");
		PrimitiveType p11 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, confidence, "the overall acceptance similarity threshold over which two points are the same - from 0 to 100", "80");

		List<StatisticalType> inputs = new ArrayList<StatisticalType>();
		inputs.add(p0);
		inputs.add(p1);
		inputs.add(p2);
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
	public String getResources() {
		if ((status > 0) && (status < 100))
			return ResourceFactory.getResources(100f);
		else
			return ResourceFactory.getResources(0f);
	}

	ResourceFactory resourceManager;

	@Override
	public String getResourceLoad() {
		if (resourceManager == null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}

	@Override
	public float getStatus() {
		return status;
	}

	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> templatesOccurrence = new ArrayList<TableTemplates>();
		templatesOccurrence.add(TableTemplates.OCCURRENCE_SPECIES);
		// occurrence points tables
		OutputTable p = new OutputTable(templatesOccurrence, finalTableLabel, finalTableName, "The output table containing the processed points");

		return p;
	}

	@Override
	public void init() throws Exception {

		// AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		lonFld = config.getParam(longitudeColumn);
		latFld = config.getParam(latitudeColumn);
		recordedByFld = config.getParam(recordedByColumn);
		scientificNameFld = config.getParam(scientificNameColumn);
		eventDatFld = config.getParam(eventDateColumn);
		modifDatFld = config.getParam(lastModificationColumn);
		leftTableName = config.getParam(leftTableNameF);
		rightTableName = config.getParam(rightTableNameF);
		finalTableName = config.getParam(finalTableNameF);
		finalTableLabel = config.getParam(finalTableNameL);
		spatialToleranceValue = Float.parseFloat(config.getParam(spatialTolerance));
		confidenceValue = Float.parseFloat(config.getParam(confidence));
		
		config.setParam(tableNameF,finalTableName);
		
		objectstoinsert = new ArrayList<OccurrencePointsMerger.OccurrenceRecord>();
		objectstodelete = new ArrayList<OccurrencePointsMerger.OccurrenceRecord>();
		status = 0;
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		this.config = config;
	}

	@Override
	public String getDescription() {
		return "A transducer algorithm that produces a duplicate-free table resulting from the union of two occurrence points tables where points equivalence is identified via user defined comparison thresholds. Works with up to 10000 points per table. Between two Ocurrence Sets, enrichs the Left Set with the elements of the Right Set that are not in the Left Set. Updates the elements of the Left Set with more recent elements in the Right Set. If one element in the Left Set corresponds to several recent elements in the Right Set, these will be all substituted to the element of the Left Set.";
	}

	protected float probabilityStrings(String first, String second) {
		if ((first == null) || (second == null))
			return 1;

		return (float) new DistanceCalculator().CD(false, first, second);
	}

	protected float probabilityDates(Calendar first, Calendar second) {
		if ((first == null) || (second == null))
			return 1;
		if (first.compareTo(second) == 0)
			return 1;
		else
			return 0;
	}

	protected float extProb(OccurrenceRecord right, OccurrenceRecord left) {
		float probability = 0;
		float distance = (float) Math.sqrt(Math.abs(left.x - right.x) + Math.abs(left.y - right.y));
		if (distance > spatialToleranceValue)
			probability = -1;
		else {
			float pSpecies = probabilityStrings(right.scientificName, left.scientificName);
			float pRecordedBy = probabilityStrings(right.recordedby, left.recordedby);
			float pDates = probabilityDates(right.eventdate, left.eventdate);
			probability = pSpecies * pRecordedBy * pDates;
		}

		return probability * 100;
	}

	protected void manageHighProbability(float probability, OccurrenceRecord leftOcc, OccurrenceRecord rightOcc) {
		// insert the most recent:
		// if it is the left then leave it as is
		// otherwise put the left in the deletion list and the right in the insertion list

		if (((leftOcc.modifdate != null) && (rightOcc.modifdate != null) && leftOcc.modifdate.before(rightOcc.modifdate)) || (leftOcc.modifdate == null) && (rightOcc.modifdate != null)) {

			objectstodelete.add(leftOcc);
			objectstoinsert.add(rightOcc);
		}
	}

	protected void manageLowProbability(float probability, OccurrenceRecord leftOcc, OccurrenceRecord rightOcc) {
		// if over the threshold then add to the element
		objectstoinsert.add(rightOcc);
	}

	protected void persist() throws Exception {

		// DELETE ELEMENTS IN THE DELETION LIST
		int todel = objectstodelete.size();
		int counter = 0;
		StringBuffer buffer = new StringBuffer();
		AnalysisLogger.getLogger().info("Deleting " + todel + " objects");
		if (todel > 0) {
			for (OccurrenceRecord record : objectstodelete) {
				buffer.append("(");
				String rec = null;
				if ((record.recordedby !=null ) &&(record.recordedby.length()>0)){
					rec = recordedByFld + "='" + record.recordedby.replace("'", "") + "'";
					buffer.append(rec);
				}
				String sci = null;
				if ((record.scientificName !=null ) &&(record.scientificName.length()>0)){
					if (rec!=null)
						buffer.append(" AND ");
						sci = scientificNameFld + "='" + record.scientificName.replace("'", "") + "'";
						buffer.append(sci);
				}
				if ((rec!=null) || (sci!=null))
					buffer.append(" AND ");
				
				String x = null;
				if ((record.x$ != null ) && (record.x$.length()>0)) 
					x = lonFld + "='" + record.x$ + "'";
				
				String y = null;
				if ((record.y$ != null ) && (record.y$.length()>0))
					y = latFld + "='" + record.y$ + "'";
				
				if ((x!=null) && (y!=null))
					buffer.append(x + " AND " + y);
				
				String event = null;
				String modified = null;
				if (record.eventdate != null)
					// to_timestamp('09/30/56 11:00:00 PM', 'MM/DD/YY HH12:MI:SS a')
					event = eventDatFld + "=to_timestamp('" + convert2conventionalFormat(record.eventdate) + "','" + sqlDateFormat + "')";

				if (record.modifdate != null)
					modified = modifDatFld + "=to_timestamp('" + convert2conventionalFormat(record.modifdate) + "','" + sqlDateFormat + "')";

				//				buffer.append(rec + " AND " + sci + " AND " + x + " AND " + y);
				if (event != null)
					buffer.append(" AND " + event);
				if (modified != null)
					buffer.append(" AND " + modified);

				buffer.append(")");
				
				if ((counter>0)&&(counter%500==0)){
					String updateQ = DatabaseUtils.deleteFromBuffer(finalTableName, buffer);
//					 AnalysisLogger.getLogger().debug("Update:\n"+updateQ);
					DatabaseFactory.executeSQLUpdate(updateQ, dbconnection);
					AnalysisLogger.getLogger().info("Partial Objects deleted");
					buffer = new StringBuffer();
				}
				else
				
				if (counter < todel - 1)
					buffer.append(" OR ");

				counter++;
			}

			String updateQ = DatabaseUtils.deleteFromBuffer(finalTableName, buffer);
//			 AnalysisLogger.getLogger().debug("Update:\n"+updateQ);
			DatabaseFactory.executeSQLUpdate(updateQ, dbconnection);
			AnalysisLogger.getLogger().info("All Objects deleted");
		}

		buffer = new StringBuffer();
		ArrayList<String> insertedStrings = new ArrayList<String>();
		int toins = objectstoinsert.size();
		AnalysisLogger.getLogger().info("Inserting " + toins + " objects");
		counter = 0;
		if (toins > 0) {
			for (OccurrenceRecord record : objectstoinsert) {
				String toInsert=occurrenceRecord2String(record);
				String toInsertEssentials=takeEssential(record);
				if (!insertedStrings.contains(toInsertEssentials)){
				buffer.append("(");
				insertedStrings.add(toInsertEssentials);
				buffer.append(toInsert);
				buffer.append(")");
				
				if ((counter>0)&&(counter%500==0)){
					insertBuffer(buffer);
					AnalysisLogger.getLogger().info("Partial Objects inserted");
					buffer = new StringBuffer();
				}
				else
					buffer.append(",");
				
				counter++;
				}
			}

			insertBuffer(buffer);
			AnalysisLogger.getLogger().info("Objects inserted");
			
			AnalysisLogger.getLogger().info("Inserted " + counter + " objects");
		}

		objectstoinsert = null;
		objectstodelete = null;
		insertedStrings=null;
		objectstoinsert = new ArrayList<OccurrencePointsMerger.OccurrenceRecord>();
		objectstodelete = new ArrayList<OccurrencePointsMerger.OccurrenceRecord>();
		System.gc();
	}

	protected void insertBuffer(StringBuffer buffer) throws Exception{
		String subBuffer = "";
		if (buffer.charAt(buffer.length()-1)!=')')
			subBuffer = buffer.substring(0, buffer.length()-1);
		else
			subBuffer = buffer.toString();
		
		String updateQ = "SET datestyle = \"ISO, MDY\"; "+DatabaseUtils.insertFromString(finalTableName, columns.toString(), subBuffer);
		// System.out.println("Update:\n"+updateQ);
		 AnalysisLogger.getLogger().debug("Update:\n"+updateQ);
		DatabaseFactory.executeSQLUpdate(updateQ, dbconnection);
		
	}
	
	protected void prepareFinalTable() throws Exception {
		DatabaseFactory.executeSQLUpdate(DatabaseUtils.duplicateTableStatement(leftTableName, finalTableName), dbconnection);
	}

	public void extractColumnNames() throws Exception {
		// take the description of the table
		columnsNames = DatabaseFactory.executeSQLQuery(DatabaseUtils.getColumnsNamesStatement(rightTableName), dbconnection);

		int nCols = columnsNames.size();
		columns = new StringBuffer();
		for (int i = 0; i < nCols; i++) {
			columns.append("\"" + columnsNames.get(i) + "\"");
			if (i < nCols - 1)
				columns.append(",");
		}
	}

	public void initDB(boolean buildTable) throws Exception {
		// init DB connection
		AnalysisLogger.getLogger().info("Initializing DB Connection");
		dbconnection = DatabaseUtils.initDBSession(config);
		AnalysisLogger.getLogger().info("Taking Table Description");
		extractColumnNames();

		if (buildTable) {
			AnalysisLogger.getLogger().info("Taken Table Description: " + columns);
			AnalysisLogger.getLogger().info("Creating final table: " + finalTableName);

			// create new merged table
			try {
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(finalTableName), dbconnection);
			} catch (Exception e1) {

			}
			prepareFinalTable();
		}
	}

	@Override
	public void shutdown() {
		if (dbconnection != null)
			try {
				dbconnection.close();
			} catch (Exception e) {
			}
	}

	public List<Object> leftRows;
	public List<Object> rightRows;

	public int getNumLeftObjects() {
		if (leftRows != null)
			return leftRows.size();
		else
			return 0;
	}

	public int getNumRightObjects() {
		if (rightRows != null)
			return rightRows.size();
		else
			return 0;
	}

	public void takeFullRanges() {
		// take the elements from sx table
		AnalysisLogger.getLogger().info("Taking elements from left table: " + leftTableName);
		leftRows = DatabaseFactory.executeSQLQuery(DatabaseUtils.getColumnsElementsStatement(leftTableName, columns.toString(), " limit 10000"), dbconnection);
		// take the elements from dx table
		AnalysisLogger.getLogger().info("Taking elements from right table: " + rightTableName);
		rightRows = DatabaseFactory.executeSQLQuery(DatabaseUtils.getColumnsElementsStatement(rightTableName, columns.toString(), " limit 10000"), dbconnection);
	}

	public void takeRange(int offsetLeft, int numLeft, int offsetRight, int numRight) {
		// take the elements from sx table
		AnalysisLogger.getLogger().info("Taking elements from left table: " + leftTableName);
		leftRows = DatabaseFactory.executeSQLQuery(DatabaseUtils.getColumnsElementsStatement(leftTableName, columns.toString(), "order by "+columns.toString()+" offset " + offsetLeft + " limit " + numLeft), dbconnection);
		// take the elements from dx table
		AnalysisLogger.getLogger().info("Taking elements from right table: " + rightTableName);
		rightRows = DatabaseFactory.executeSQLQuery(DatabaseUtils.getColumnsElementsStatement(rightTableName, columns.toString(), "order by "+columns.toString()+" offset " + offsetRight + " limit " + numRight), dbconnection);
	}

	public void computeRange() throws Exception {
		try {
			AnalysisLogger.getLogger().info("Processing " + leftTableName + " vs " + rightTableName);
			// AnalysisLogger.getLogger().info("ELEMENTS " + getNumLeftObjects() + " vs " + getNumRightObjects());
			status = 10;
			int rightCounter = 0;
			int similaritiesCounter = 0;
			int allrightrows = rightRows.size();
			if ((allrightrows > 0) && (getNumLeftObjects() > 0)) {
				for (Object rRow : rightRows) {
					// AnalysisLogger.getLogger().info("RR CONV");
					// transform into an occurrence object
					OccurrenceRecord rightOcc = row2OccurrenceRecord((Object[]) rRow);
					// AnalysisLogger.getLogger().info("RR CONV - OK");
					// for each element in sx
					int k = 0;
					boolean found = false;
					float p = 0;
					OccurrenceRecord bestleftOcc = null;
					for (Object lRow : leftRows) {
						OccurrenceRecord leftOcc = null;
						// AnalysisLogger.getLogger().info("LL CONV");
						leftOcc = row2OccurrenceRecord((Object[]) lRow);
						p = extProb(leftOcc, rightOcc);
						// AnalysisLogger.getLogger().info("P");
						if (p >= confidenceValue) {
							bestleftOcc = leftOcc;
							found = true;
							similaritiesCounter++;
							AnalysisLogger.getLogger().info("Found a similarity with P=" + p + " between (" + "\"" + leftOcc.scientificName + "\"" + ",\"" + leftOcc.x + "\"" + "," + "\"" + leftOcc.y + "\"" + "," + "\"" + leftOcc.recordedby + "\"" + "," + "\"" + convert2conventionalFormat(leftOcc.eventdate) + "\"" + ") VS " + "(" + "\"" + rightOcc.scientificName + "\"" + "," + "\"" + rightOcc.x + "\"" + "," + "\"" + rightOcc.y + "\"" + "," + "\"" + rightOcc.recordedby + "\"" + "," + "\"" + convert2conventionalFormat(rightOcc.eventdate) + "\"" + ")");
							// break;
							if (!firstbest)
								manageHighProbability(p, bestleftOcc, rightOcc);
							else
								break;
						}
						//else if (!firstbest)
							//manageLowProbability(p, bestleftOcc, rightOcc);
						
						k++;
					}
					rightCounter++;

					if (firstbest) {
						if (found)
							manageHighProbability(p, bestleftOcc, rightOcc);
						else
							manageLowProbability(p, bestleftOcc, rightOcc);
					}
					else
						if (!found)
							manageLowProbability(p, bestleftOcc, rightOcc);
					
					status = Math.min(90, 10f + (80 * ((float) rightCounter) / ((float) allrightrows)));

					if (rightCounter % 500 == 0) {
						AnalysisLogger.getLogger().info("Persisting ... " + rightCounter + " over " + allrightrows);
						persist();
					}
				}
			}
			AnalysisLogger.getLogger().info("Found " + similaritiesCounter + " similarities on " + rightCounter + " elements");
			status = 90;
			// transform the complete list into a table
			persist();
			// close DB connection
		} catch (Exception e) {
			AnalysisLogger.getLogger().error("error in computation",e);
			throw e;
		} finally {
			shutdown();
			status = 100;
			AnalysisLogger.getLogger().info("Occ Points Processing Finished and db closed");
		}
	}

	@Override
	public void compute() throws Exception {

		initDB(true);
		takeFullRanges();
		computeRange();
		postProcess();
	}
	
	
	
	public void postProcess() throws Exception{
		/*
		AnalysisLogger.getLogger().info("Post processing ... Deleting duplicates");
		
		OccurrencePointsDuplicatesDeleter opdd = new OccurrencePointsDuplicatesDeleter();
		opdd.setConfiguration(config);
		opdd.init();
		opdd.initDB(false);
		opdd.takeFullRanges();
		opdd.computeRange();
		AnalysisLogger.getLogger().info("Post processing ... Finished");
		*/
	}

}
