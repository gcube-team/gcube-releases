package org.gcube.dataanalysis.geo.vti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.vti.vesselsprocessing.Bathymetry;
import org.gcube.dataanalysis.geo.vti.vesselsprocessing.FishingHoursCalculator;
import org.gcube.dataanalysis.geo.vti.vesselsprocessing.VTIClassificator;

public class EstimateFishingActivity extends GridCWP2Coordinates{

	static String VesselsIDColumn = "VesselsIDColumn";
	static String VesselsSpeedsColumn = "VesselsSpeedsColumn";
	static String VesselsTimestampsColumn = "VesselsTimestampsColumn";
	static String VesselsLatitudesColumn = "VesselsLatitudesColumn";
	static String VesselsLongitudesColumn = "VesselsLongitudesColumn";
	
	@Override
	protected void setInputParameters() {

		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, inputTableParameter, "The table to which the algorithm will add information");
		inputs.add(tinput);

		ColumnType Dimension1 = new ColumnType(inputTableParameter, VesselsIDColumn, "A column containing (anonymised) unique vessels identifiers", "vessel_id", false);
		ColumnType Dimension2 = new ColumnType(inputTableParameter, VesselsSpeedsColumn, "The column containing vessels speeds", "speed", false);
		ColumnType Dimension3 = new ColumnType(inputTableParameter, VesselsTimestampsColumn, "The column containing the time stamp of the vessels transmitted information (preferred in the following format: MM/dd/yyyy HH:mm:ss a)", "datetime", false);
		ColumnType Dimension4 = new ColumnType(inputTableParameter, VesselsLatitudesColumn, "The column containing vessels latitudes", "y", false);
		ColumnType Dimension5 = new ColumnType(inputTableParameter, VesselsLongitudesColumn, "The column containing vessels longitudes", "x", false);
		
		inputs.add(Dimension1);
		inputs.add(Dimension2);
		inputs.add(Dimension3);
		inputs.add(Dimension4);
		inputs.add(Dimension5);
		
		IOHelper.addStringInput(inputs, outputTableParameter, "The name of the output table", "fish_");
		DatabaseType.addDefaultDBPars(inputs);

	}
	
	@Override
	public String getDescription() {
		return "An algorithm that estimates activity hours (fishing or other) from vessels trajectories, " +
				"adds bathymetry information to the table and classifies (point-by-point) fishing activity of the involved vessels according to two algorithms: " +
				"one based on speed (activity_class_speed output column) and the other based on speed and bathymetry (activity_class_speed_bath output column). " +
				"The algorithm produces new columns containing this information. " +
				"This algorithm is based on the paper 'Deriving Fishing Monthly Effort and Caught Species' (Coro et al. 2013, in proc. of OCEANS - Bergen, 2013 MTS/IEEE). " +
				"Example of input table (NAFO anonymised data): http://goo.gl/3auJkM";
	} 
	
	@Override
	protected void process() throws Exception {
		status = 0;
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		long t0 = System.currentTimeMillis();
		String table = IOHelper.getInputParameter(config, inputTableParameter);
		outTable = ("fish_" + UUID.randomUUID()).replace("-", "");
		outTableLabel = IOHelper.getInputParameter(config, outputTableParameter);
		
		AnalysisLogger.getLogger().debug("EstimateFishingActivity: received parameters: " + config.getGeneralProperties());
		AnalysisLogger.getLogger().debug("EstimateFishingActivity: input table: "+ table +" outputTable: " + outTable + " outLabel: " + outTableLabel);
		
		status = 10;
		try{
			addInformationColumsToTable(table);
			AnalysisLogger.getLogger().debug("EstimateFishingActivity: finished in "+(System.currentTimeMillis()-t0)+" ms");
		} catch (Throwable e) {
			throw new Exception(e.getMessage());
		} finally {
			status = 100;
		}
	}
	
	public void addInformationColumsToTable(String table) throws Exception{
		AnalysisLogger.getLogger().debug("EstimateFishingActivity: initializing connection");
		long t0 = System.currentTimeMillis();
		try {
			connection = DatabaseUtils.initDBSession(config);
			AnalysisLogger.getLogger().debug("EstimateFishingActivity: database: " + config.getDatabaseURL());
			// create a new output table
			AnalysisLogger.getLogger().debug("EstimateFishingActivity: dropping table " + outTable + " if exists");
			try {
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(outTable), connection);
			} catch (Exception e) {
				AnalysisLogger.getLogger().debug("EstimateFishingActivity: table " + outTable + " does not exist yet");
			}
			AnalysisLogger.getLogger().debug("EstimateFishingActivity: creating the new table " + outTable);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.createBlankTableFromAnotherStatement(table, outTable), connection);
			AnalysisLogger.getLogger().debug("EstimateFishingActivity: adding new columns to " + outTable);
			
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(outTable, "activity_hours", "real"), connection);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(outTable, "bathymetry", "real"), connection);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(outTable, "activity_class_speed", "character varying"), connection);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(outTable, "activity_class_speed_bath", "character varying"), connection);
			
			AnalysisLogger.getLogger().debug("EstimateFishingActivity: getting columns from " + outTable);
			// get columns names
			List<Object> names = DatabaseFactory.executeSQLQuery(DatabaseUtils.getColumnsNamesStatement(outTable), connection);
			StringBuffer colnames = new StringBuffer();
			int nnames = names.size();
			for (int i = 0; i < nnames; i++) {
				colnames.append(names.get(i));
				if (i < nnames - 1)
					colnames.append(",");
			}
			AnalysisLogger.getLogger().debug("EstimateFishingActivity: columns are: " + colnames.toString());
			AnalysisLogger.getLogger().debug("EstimateFishingActivity: taking chunks ... ");
			
			List<Object> vessels = DatabaseFactory.executeSQLQuery("select distinct "+config.getParam(VesselsIDColumn)+" from "+table, connection);
			int nvessels = vessels.size();
			int k=0;
			
			//error check
			if (config.getParam(VesselsIDColumn)==null || config.getParam(VesselsSpeedsColumn)==null || config.getParam(VesselsTimestampsColumn)==null || config.getParam(VesselsLongitudesColumn)==null || config.getParam(VesselsLatitudesColumn)==null)
				throw new Exception ("Error with input parameters, please check that all the required inputs have been provided.");
			for (Object vesselrow:vessels){
				String vesselID = ""+vesselrow;
				AnalysisLogger.getLogger().debug("EstimateFishingActivity: Analysing vessel "+vesselID+" "+(k+1)+" of "+nvessels);
				//extract single vessel trajectory information
				String selectTrajectory = "select *, "+
				"\""+config.getParam(VesselsIDColumn)+"\""+" as fhv01,"+
				"\""+config.getParam(VesselsSpeedsColumn)+"\""+" as fhv02,"+
				"\""+config.getParam(VesselsTimestampsColumn)+"\""+" as fhv03,"+
				"\""+config.getParam(VesselsLongitudesColumn)+"\""+" as fhv04,"+
				"\""+config.getParam(VesselsLatitudesColumn)+"\""+" as fhv05 "+
				" from "+table+
				" where "+"\""+config.getParam(VesselsIDColumn)+"\""+" ="+vesselID+" order by CAST("+"\""+config.getParam(VesselsTimestampsColumn)+"\""+" as timestamp)";
				
				AnalysisLogger.getLogger().debug("EstimateFishingActivity: EstimateFishingActivity: Select trajectory: "+selectTrajectory);
				
				List<Object> vesselTrajectory = DatabaseFactory.executeSQLQuery(selectTrajectory, connection);
				
				int nvesselpoints = vesselTrajectory.size();
				String[] vesselIDs = new String[nvesselpoints];
				Date[] timeStamps = new Date[nvesselpoints];
				java.awt.geom.Point2D.Double [] coordinates = new  java.awt.geom.Point2D.Double[nvesselpoints];
				Tuple<String>[] bathspeedpairs = new Tuple [nvesselpoints];
				String[] speeds = new String[nvesselpoints];
				AnalysisLogger.getLogger().debug("EstimateFishingActivity: EstimateFishingActivity: building information: "+selectTrajectory);
				int i=0;
				for (Object trajectorrow:vesselTrajectory){
					Object[] trajectvector = (Object[])  trajectorrow;
					int lenvector = trajectvector.length;
					vesselIDs[i] = ""+trajectvector[lenvector-5];
					speeds[i] = ""+trajectvector[lenvector-4];
					timeStamps[i] = DateGuesser.convertDate(""+trajectvector[lenvector-3]).getTime();
					if (timeStamps[i]==null)
						throw new Exception ("Cannot parse time "+trajectvector[lenvector-3]+" for vessel "+vesselIDs[i]+". please try specifying time as MM/dd/yy KK:mm:ss a");
					if (i==0){
						String pattern = DateGuesser.getPattern(""+trajectvector[lenvector-3]);
						AnalysisLogger.getLogger().debug("EstimateFishingActivity: sample time conversion: original "+trajectvector[lenvector-3]+" guessed: "+timeStamps[i]+" pattern: "+pattern);
					}
					try{
						coordinates [i] = new java.awt.geom.Point2D.Double(Double.parseDouble(""+trajectvector[lenvector-2]),Double.parseDouble(""+trajectvector[lenvector-1]));
					}catch(Exception e){
						AnalysisLogger.getLogger().debug("EstimateFishingActivity: Warning - wrong coordinates: "+trajectvector[lenvector-2]+","+trajectvector[lenvector-1]);
						coordinates [i] = new java.awt.geom.Point2D.Double(0,0);
					}
					i++;
				}
				AnalysisLogger.getLogger().debug("EstimateFishingActivity: estimating fishing hours");
				double [] hours = FishingHoursCalculator.calculateFishingHours(vesselIDs, timeStamps);
				AnalysisLogger.getLogger().debug("EstimateFishingActivity: estimating bathymetry");

				short [] bathymetry = null;
				try{
					Bathymetry bathymetryprocessor = new Bathymetry(config.getConfigPath()+"gebco_08.nc");
					bathymetry =  bathymetryprocessor.compute(coordinates);
				}catch(Exception e){
					AnalysisLogger.getLogger().debug("EstimateFishingActivity: Error - Bathymetry resource not available for the service "+e.getLocalizedMessage());
					throw new Exception("Error - Bathymetry resource not available for the service");
				}
				for (int g=0;g<nvesselpoints;g++){
					bathspeedpairs[g]=new Tuple<String>(""+speeds[g],""+bathymetry[g]);
				}
				AnalysisLogger.getLogger().debug("EstimateFishingActivity: classifying routes");
				Tuple<Integer>[] classifications = VTIClassificator.classify(bathspeedpairs);		
				AnalysisLogger.getLogger().debug("EstimateFishingActivity:building rows for the final table");
				List<String[]> stringrows = new ArrayList<String[]>();
				i=0;
				for (Object trajectorrow:vesselTrajectory){
					Object[] trajectvector = (Object[])  trajectorrow;
					String[] extendedrow = new String[nnames]; 
					for(int j=0;j<nnames-4;j++){
						extendedrow[j]=""+trajectvector[j];
					}
					extendedrow[nnames-4] =  ""+hours[i];
					extendedrow[nnames-3] =  ""+bathymetry[i];
					extendedrow[nnames-2] =  ""+VTIClassificator.speedClassification(classifications[i].getElements().get(0));
					extendedrow[nnames-1] =  ""+VTIClassificator.bathymetryClassification(classifications[i].getElements().get(1));
					stringrows.add(extendedrow);
					i++;
				}
				AnalysisLogger.getLogger().debug("EstimateFishingActivity: inserting chunks into the table "+outTable);
				// write the vector into the table
				DatabaseUtils.insertChunksIntoTable(outTable, colnames.toString(), stringrows, nvesselpoints, connection, true);
				AnalysisLogger.getLogger().debug("EstimateFishingActivity: inserting chunks done!");
				k++;
			}
			
			AnalysisLogger.getLogger().debug("EstimateFishingActivity: finished");
		} catch (Throwable e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("EstimateFishingActivity : ERROR!: " + e.getLocalizedMessage());
			try {
				AnalysisLogger.getLogger().debug("EstimateFishingActivity: dropping " + outTable);
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(outTable), connection);
			} catch (Exception e1) {
				AnalysisLogger.getLogger().debug("EstimateFishingActivity: could not drop " + outTable);
			}
			throw new Exception(e.getMessage());
		} finally {
			shutdown();
			AnalysisLogger.getLogger().debug("EstimateFishingActivity finished in " + (System.currentTimeMillis() - t0) + " ms");
		}
	}
	
}

