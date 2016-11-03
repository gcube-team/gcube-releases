package org.gcube.dataanalysis.seadatanet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.hibernate.SessionFactory;

public class SeaDataNetConnector extends StandardLocalExternalAlgorithm {

	// Statistical result by DIVA
	LinkedHashMap<String, String> statResultMap = new LinkedHashMap<String, String>();

	// HashMap for
	LinkedHashMap<String, StatisticalType> outputDivaMap = new LinkedHashMap<String, StatisticalType>();
	SessionFactory dbconnection;

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescription() {
		//->adjusted description with proper citation
		return "A connector for the SeaDataNet infrastructure. This algorithms invokes the Data-Interpolating Variational Analysis (DIVA) SeaDataNet service to interpolate spatial data. " +
				"The model uses GEBCO bathymetry data and requires an estimate of the maximum spatial span of the correlation between points and the signal-to-noise ratio, among the other parameters. " +
				"It can interpolate up to 10,000 points randomly taken from the input table. " +
				"As output, it produces a NetCDF file with a uniform grid of values. " +
				"This powerful interpolation model is described in Troupin et al. 2012, 'Generation of analysis and consistent error ﬁelds using the Data Interpolating Variational Analysis (Diva)', Ocean Modelling, 52-53, 90-101.";
	}

	File outputfile;

	@Override
	protected void process() throws Exception {

		File neofile = null;
		File fileForDiva = null;
		try {
			String outpath = config.getPersistencePath();
			neofile = new File(outpath, "seadn_diva_" + UUID.randomUUID() + ".nc");
			AnalysisLogger.getLogger().debug("Input Parameters");
			AnalysisLogger.getLogger().debug("Input Table: " + config.getParam("InputTable"));
			AnalysisLogger.getLogger().debug("Input Long: " + config.getParam("Longitude"));
			AnalysisLogger.getLogger().debug("Input Lat: " + config.getParam("Latitude"));
			AnalysisLogger.getLogger().debug("Input Qt: " + config.getParam("Quantity"));

			AnalysisLogger.getLogger().debug("Longitude min X: " + config.getParam("LongitudeMinValue"));
			AnalysisLogger.getLogger().debug("Longitude max X: " + config.getParam("LongitudeMaxValue"));
			AnalysisLogger.getLogger().debug("Longitude resolution: " + config.getParam("LongitudeResolution"));

			AnalysisLogger.getLogger().debug("Latitude min Y: " + config.getParam("LatitudeMinValue"));
			AnalysisLogger.getLogger().debug("Latitude max Y: " + config.getParam("LatitudeMaxValue"));
			AnalysisLogger.getLogger().debug("Latitude resolution: " + config.getParam("LatitudeResolution"));

			AnalysisLogger.getLogger().debug("Correlation length: " + config.getParam("CorrelationLength"));
			AnalysisLogger.getLogger().debug("Signal noise value: " + config.getParam("SignalNoise"));
			AnalysisLogger.getLogger().debug("Depth Level: " + config.getParam("DepthLevel"));

			Double correlationVal = null;
			Double signalNoiseVal = null;
			Double longMinVal = null;
			Double longMaxVal = null;
			Double longResolutionVal = null;
			Double latMinVal = null;
			Double latMaxVal = null;
			Double latResolutionVal = null;
			Double depthLevelVal = null;
			AnalysisLogger.getLogger().debug("Checking parameters");
			//->try - catch to manage case of NULL values
			//->the check on the values has been put before the initialization of the connection
			try {
				// ANALYSIS
				correlationVal = Double.parseDouble(config.getParam("CorrelationLength"));
				if (correlationVal <0 )
					throw new Exception("Correlation span cannot be negative.");
				signalNoiseVal = Double.parseDouble(config.getParam("SignalNoise"));
				if (signalNoiseVal <0 )
					throw new Exception("Signal-to-noise ratio cannot be negative.");
				longMinVal = Double.parseDouble(config.getParam("LongitudeMinValue"));
				if (longMinVal < -180)
					throw new Exception("Longitudine minumum value is less than -180.");

				longMaxVal = Double.parseDouble(config.getParam("LongitudeMaxValue"));
				if (longMaxVal > 180)
					throw new Exception("Longitudine maximum value is more than 180.");

				longResolutionVal = Double.parseDouble(config.getParam("LongitudeResolution"));

				latMinVal = Double.parseDouble(config.getParam("LatitudeMinValue"));
				if (latMinVal < -85)
					throw new Exception("Latitude minumum value is less than -85.");

				latMaxVal = Double.parseDouble(config.getParam("LatitudeMaxValue"));
				if (latMaxVal > 85)
					throw new Exception("Latitude maximum value is more than 85.");

				latResolutionVal = Double.parseDouble(config.getParam("LatitudeResolution"));

				depthLevelVal = Double.parseDouble(config.getParam("DepthLevel"));
				if (depthLevelVal < 0)
					throw new Exception("Depth Level cannot be negative");

			} catch (NumberFormatException e) {
				throw new Exception("Parameter values are incomplete");
			}
			AnalysisLogger.getLogger().debug("Parameters are OK");
			AnalysisLogger.getLogger().debug("Initializing DB connection");
			dbconnection = DatabaseUtils.initDBSession(config);
			//->set limit to 100 000 - maximum allowed by DIVA
			String query = "select " 
					+ config.getParam("Longitude") + "," + config.getParam("Latitude") + "," + config.getParam("Quantity") + " From " + getInputParameter("InputTable") + 
					" ORDER BY RANDOM() limit 10000";
			//->indicate the status of the computation
			status = 10;
			AnalysisLogger.getLogger().debug("Query for extracting data from the DB: " + query);
			List<Object> dataList = DatabaseFactory.executeSQLQuery(query, dbconnection);
			int ndata = dataList.size();
			fileForDiva = new File(outpath, "file_for_diva_" + UUID.randomUUID() + ".txt");
			BufferedWriter fileWriterDiva = new BufferedWriter(new FileWriter(fileForDiva));
			AnalysisLogger.getLogger().debug("Writing input file in: " + fileForDiva.getAbsolutePath());
			for (Object o : dataList) {
				Object[] oarray = (Object[]) o;
				fileWriterDiva.write(" " + oarray[0] + " " + oarray[1] + " " + oarray[2] + "\n");
			}
			fileWriterDiva.close();

			AnalysisLogger.getLogger().debug("Sending data to DIVA: Uploading "+ndata+" records");
			// integration DivaHttpClient
			// UPLOADFILE for DIVA
			//->*use the HTTPClient Class methods
			DivaFilePostResponse response = DivaHTTPClient.uploadFile(fileForDiva);
			AnalysisLogger.getLogger().debug("DIVA Server Response for the Upload:\n" + response.getSessionid());
			status = 50;
			AnalysisLogger.getLogger().debug("Requesting analysis to DIVA...");
			long t0 = System.currentTimeMillis();
			DivaAnalysisGetResponse respAnalysis = DivaHTTPClient.getAnalysis(response.getSessionid(), correlationVal, signalNoiseVal, longMinVal, longMaxVal, longResolutionVal, latMinVal, latMaxVal, latResolutionVal, depthLevelVal);
			long t1 = System.currentTimeMillis();
			//->Record the time of the analysis
			AnalysisLogger.getLogger().debug("Analysis finished in "+(t1-t0)+" ms");
			status = 80;
			//->the number of observations is now an integer
			statResultMap.put("Minimum value estimated by the model", "" + respAnalysis.getVmin());
			statResultMap.put("Maximum value estimated by the model", "" + respAnalysis.getVmax());
			statResultMap.put("Number of observations used", "" + respAnalysis.getStat_obs_count_used());
			statResultMap.put("A posteriori estimate of signal-to-noise ratio", "" + respAnalysis.getStat_posteriori_stn());

			AnalysisLogger.getLogger().debug("Map of results to be returned: "+statResultMap);
			AnalysisLogger.getLogger().debug("Downloading result file in "+neofile.getAbsolutePath());
			// DOWNLOAD FILE
			DivaHTTPClient.downloadFileDiva(respAnalysis.getIdentifier(), neofile.getAbsolutePath());
			//->*put the output file in the output object
			outputfile=neofile;
			AnalysisLogger.getLogger().debug("Downloading finished");
		} catch (Exception e) {
			//->*ONLY in case of errors, delete the output file 
			if (neofile.exists()){
				neofile.delete();
				AnalysisLogger.getLogger().debug("Output file "+neofile.getAbsolutePath()+" deleted!");	
			}
			throw e;
		} finally {
			//->*in any case, delete the input file because we don't need it anymore
			if (fileForDiva.exists()){
				fileForDiva.delete();
				AnalysisLogger.getLogger().debug("Input file "+fileForDiva.getAbsolutePath()+" deleted!");
			}
			AnalysisLogger.getLogger().debug("DIVA process finished");
		}
	}

	@Override
	protected void setInputParameters() {

		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, "InputTable", "Input tabular resource. Up to 10,000 points will be randomly taken from this table.");

		inputs.add(tinput);

		ColumnType p1 = new ColumnType("InputTable", "Longitude", "The column containing longitude decimal values", "longitude", false);
		ColumnType p2 = new ColumnType("InputTable", "Latitude", "The column containing latitude decimal values", "latitude", false);
		ColumnType p3 = new ColumnType("InputTable", "Quantity", "The column containing quantity values", "quantity", false);

		inputs.add(p1);
		inputs.add(p2);
		inputs.add(p3);

		PrimitiveType p4 = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, "LongitudeMinValue", "Minimum deg. value of the longitude range (min -180)", "-180");

		PrimitiveType p5 = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, "LongitudeMaxValue", "Maximum deg. value of the longitude range (max 180)", "180");

		PrimitiveType p6 = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, "LongitudeResolution", "Longitude resolution (minimum 0.1 - maximum 10)", "1");

		PrimitiveType p7 = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, "LatitudeMinValue", "Minimum deg value of Latitude Range (min -85)", "-85");

		PrimitiveType p8 = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, "LatitudeMaxValue", "Maximum value of Latitude Range (max 85)", "85");

		PrimitiveType p9 = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, "LatitudeResolution", "Latitude resolution (minimum 0.1 - maximum 10)", "1");

		PrimitiveType p10 = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, "CorrelationLength", "Correlation length (arc degrees)", "10.35");

		PrimitiveType p11 = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, "SignalNoise", "Signal to noise ratio", "1.08");

		PrimitiveType p12 = new PrimitiveType(Double.class.getName(), null, PrimitiveTypes.NUMBER, "DepthLevel", "Depth level (meters)", "0");

		inputs.add(p4);
		inputs.add(p5);
		inputs.add(p6);
		inputs.add(p7);
		inputs.add(p8);
		inputs.add(p9);
		inputs.add(p10);
		inputs.add(p11);
		inputs.add(p12);

		DatabaseType.addDefaultDBPars(inputs);

	}

	@Override
	public void shutdown() {

		if (dbconnection != null)
			dbconnection.close();

	}

	public StatisticalType getOutput() {

		PrimitiveType file = new PrimitiveType(File.class.getName(), outputfile, PrimitiveTypes.FILE, "NetCDFOutputFile", "Output file in NetCDF format");

		for (String key : statResultMap.keySet()) {
			String value = statResultMap.get(key);
			PrimitiveType val = new PrimitiveType(String.class.getName(), value, PrimitiveTypes.STRING, key, key);
			outputDivaMap.put(key, val);
		}

		outputDivaMap.put("Netcdf output file", file);

		PrimitiveType hashma = new PrimitiveType(HashMap.class.getName(), outputDivaMap, PrimitiveTypes.MAP, "Diva results", "Output of DIVA fit");

		return hashma;
	}

}
