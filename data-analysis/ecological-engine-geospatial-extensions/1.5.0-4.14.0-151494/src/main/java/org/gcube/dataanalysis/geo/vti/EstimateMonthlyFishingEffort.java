package org.gcube.dataanalysis.geo.vti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.contentmanagement.graphtools.utils.DateGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.geo.utils.CSquareCodesConverter;
import org.gcube.dataanalysis.geo.vti.vesselsprocessing.MonthlyFishingEffortCalculator;

public class EstimateMonthlyFishingEffort extends GridCWP2Coordinates{

	static String VesselsLatitudesColumn = "VesselsLatitudesColumn";
	static String VesselsLongitudesColumn = "VesselsLongitudesColumn";
	static String VesselsTimestampsColumn = "VesselsTimestampsColumn";
	static String VesselsHoursColumn = "VesselsActivityHoursColumn";
	static String VesselsActivityClassificationColumn = "VesselsActivityClassificationColumn";
	
	@Override
	protected void setInputParameters() {

		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, inputTableParameter, "The table to which the algorithm will add information");
		inputs.add(tinput);
		ColumnType Dimension1 = new ColumnType(inputTableParameter, VesselsLatitudesColumn, "The column containing vessels latitudes", "y", false);
		ColumnType Dimension2 = new ColumnType(inputTableParameter, VesselsLongitudesColumn, "The column containing vessels longitudes", "x", false);
		ColumnType Dimension3 = new ColumnType(inputTableParameter, VesselsTimestampsColumn, "The column containing the time stamp of the vessels transmitted information", "datetime", false);
		ColumnType Dimension4 = new ColumnType(inputTableParameter, VesselsHoursColumn, "The column containing the activity hours spent by the vessels in steaming, howling or fishing", "activity_hours", false);
		ColumnType Dimension5 = new ColumnType(inputTableParameter, VesselsActivityClassificationColumn, "The column containing the a simple point-by-point activity classification as Fishing, Steaming, Howling etc.", "activity_class_speed", false);
		
		inputs.add(Dimension1);
		inputs.add(Dimension2);
		inputs.add(Dimension3);
		inputs.add(Dimension4);
		inputs.add(Dimension5);
		
		IOHelper.addStringInput(inputs, outputTableParameter, "The name of the output table", "monthfish_");
		DatabaseType.addDefaultDBPars(inputs);

	}
	
	@Override
	public String getDescription() {
		return "An algorithm that estimates fishing exploitation at 0.5 degrees resolution from activity-classified vessels trajectories. " +
				"Produces a table with csquare codes, latitudes, longitudes and resolution and associated overall fishing hours in the time frame of the vessels activity. " +
				"Requires each activity point to be classified as Fishing or other. " +
				"This algorithm is based on the paper 'Deriving Fishing Monthly Effort and Caught Species' (Coro et al. 2013, in proc. of OCEANS - Bergen, 2013 MTS/IEEE). " +
				"Example of input table (NAFO anonymised data): http://goo.gl/3auJkM";
	} 
	
	@Override
	protected void process() throws Exception {
		status = 0;
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		long t0 = System.currentTimeMillis();
		String table = IOHelper.getInputParameter(config, inputTableParameter);
		outTable = ("mfe_" + UUID.randomUUID()).replace("-", "");
		outTableLabel = IOHelper.getInputParameter(config, outputTableParameter);
		
		AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: received parameters: " + config.getGeneralProperties());
		AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: input table: "+ table +" outputTable: " + outTable + " outLabel: " + outTableLabel);
		
		status = 10;
		try{
			addInformationColumsToTable(table);
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: finished in "+(System.currentTimeMillis()-t0)+" ms");
		} catch (Throwable e) {
			throw new Exception(e.getMessage());
		} finally {
			status = 100;
		}
	}
	
		public void addInformationColumsToTable(String table) throws Exception{
		AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: initializing connection");
		long t0 = System.currentTimeMillis();
		try {
			//error check
			if (config.getParam(VesselsLatitudesColumn)==null || config.getParam(VesselsLongitudesColumn)==null || config.getParam(VesselsTimestampsColumn)==null || config.getParam(VesselsHoursColumn)==null || config.getParam(VesselsActivityClassificationColumn)==null)
				throw new Exception ("Error with input parameters, please check that all the required inputs have been provided.");

			connection = DatabaseUtils.initDBSession(config);
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: database: " + config.getDatabaseURL());
			// create a new output table
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: dropping table " + outTable + " if exists");
			try {
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(outTable), connection);
			} catch (Exception e) {
				AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: table " + outTable + " does not exist yet");
			}
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: creating the new table " + outTable);
			String csquareCol = "csquares"; 
			String longitudeCol = "center_longitude";
			String latitudeCol = "center_latitude";
			String resolutionCol = "resolution";
			String fishingeffortCol = "fishing_effort";
			

			DatabaseFactory.executeSQLUpdate("create table "+outTable+" ("+csquareCol+" character varying, "+longitudeCol+" real, "+latitudeCol+" real, "+resolutionCol+" real, "+fishingeffortCol+" real)", connection);
			
			String selectVinfo = "select "+
			"\""+config.getParam(VesselsLongitudesColumn)+"\""+","+
			"\""+config.getParam(VesselsLatitudesColumn)+"\""+","+
			"\""+config.getParam(VesselsTimestampsColumn)+"\""+","+
			"\""+config.getParam(VesselsHoursColumn)+"\""+","+
			"\""+config.getParam(VesselsActivityClassificationColumn)+"\""+
			" from "+table;
			
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: select info: "+selectVinfo);
			//get all the information and calculate min and max date
			List<Object> vesselsInfo = DatabaseFactory.executeSQLQuery(selectVinfo, connection);
			int ncoords = vesselsInfo.size();
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: Rebuilding effort from n. coords: "+ncoords);
			//pass information to csquare aggregator
			Date minDate = null;
			Date maxDate = null;
			//x,y,date,hours,activity
			List<Object> infoRows= new ArrayList<Object>();
			for (Object vesselInfo:vesselsInfo){
				Object [] vesselrow = (Object[]) vesselInfo;
				Double x = Double.parseDouble(""+vesselrow[0]);
				Double y = Double.parseDouble(""+vesselrow[1]);
				Date d = DateGuesser.convertDate(""+vesselrow[2]).getTime();
				if (minDate==null)
				{
					String pattern = DateGuesser.getPattern(""+vesselrow[2]);
					AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: Time detected : "+d);
					AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: Time pattern : "+pattern+" estimated from "+vesselrow[2]);
				}
				if (minDate==null || minDate.after(d))
					minDate=d;
				if (maxDate==null || maxDate.before(d))
					maxDate=d;
				
				Double hours = Double.parseDouble(""+vesselrow[3]);
				String activity = ""+vesselrow[4];
				Object[] infoRow = new Object[5];
				infoRow [0] = x;infoRow [1] = y; infoRow [2] = d;infoRow [3] = hours;infoRow [4] = activity;
				infoRows.add(infoRow);
			}
			
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: Rebuilt effort:  minDate"+minDate+" maxDate "+maxDate);
			
			//build information with center long lat and resolution
			MonthlyFishingEffortCalculator mfec = new MonthlyFishingEffortCalculator();
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: Estimating monthly effort");
			Map<String,Double> fme = mfec.calculateMonthlyFishingEffort(infoRows, minDate, maxDate);
			List<String[]> values = new ArrayList<String[]>();
			//go through the map and produce output
			for (String csquare:fme.keySet()){
				String[] valuesRow = new String[5];
				valuesRow[0]=csquare;
				valuesRow[4]= ""+fme.get(csquare);
				CSquareCodesConverter converter = new CSquareCodesConverter();
				converter.parse(csquare);
				valuesRow[1] = ""+converter.getCurrentLong();
				valuesRow[2]=""+converter.getCurrentLat();
				valuesRow[3]=""+converter.getCurrentResolution();
				values.add(valuesRow);
			}
			
			int fmenum = fme.size();
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: number of csquares found "+fmenum);
			String colnames = csquareCol+","+longitudeCol+","+latitudeCol+","+resolutionCol+","+fishingeffortCol;
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: inserting chunks into the table "+outTable);
			// write the vector into the table
			DatabaseUtils.insertChunksIntoTable(outTable, colnames.toString(), values, fme.size(), connection, true);
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: inserting chunks done!");
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: finished");
		} catch (Throwable e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort : ERROR!: " + e.getLocalizedMessage());
			try {
				AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: dropping " + outTable);
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(outTable), connection);
			} catch (Exception e1) {
				AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort: could not drop " + outTable);
			}
			throw new Exception(e.getMessage());
		} finally {
			shutdown();
			AnalysisLogger.getLogger().debug("EstimateMonthlyFishingEffort finished in " + (System.currentTimeMillis() - t0) + " ms");
		}
	}
	
}

