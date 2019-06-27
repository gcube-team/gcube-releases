package org.gcube.dataanalysis.geothermal;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.gcube.dataanalysis.geo.algorithms.XYExtraction;
import org.hibernate.SessionFactory;
import org.jfree.chart.JFreeChart;

public abstract class AbstractProcess implements Transducerer {

	static String geothermalData = "GeothermalManagementArea_ERANET";
	String featuresTable = "gth"+UUID.randomUUID().toString().replace("_", "").replace("-","");
	
//	String getTable = "select distinct   f_name,  f_area,  f_f1990_mwe,  f_f1995_mwt,  f_f1995_h_tjy,  f_f1995_mwe,  f_f2000_mwt,  f_f2000_h_tjy,  f_f2000_mwe,  f_f2005_mwt,  f_f2005_h_tjy,  f_f2005_mwe,  f_f2010_mwt,  f_f2010_h_tjy,  f_f2010_mwe,  f_f2013_mwe from "+featuresTable;
	
	String getTable = "select distinct   f_NAME,f_AREA,f_MWe_1990,f_MWth_1995,f_H_TJy_1995,f_MWe_1995,f_MWth_2000,f_H_TJy_2000,f_MWe_2000,f_MWth_2005,f_H_TJy_2005,f_MWe_2005,f_MWth_2010,f_H_TJy_2010,f_MWe_2010,f_MWe_2013 from "+featuresTable;
	
	AlgorithmConfiguration config;
	float status;
	public static boolean display = false;
	static String MWE = "MegaWatt Electrical";
	static String MWT = "MegaWatt Thermal";
	static String TJY = "Heat TeraJoule per Year";
	protected static String countryParam = "CountryName";
	protected static String yearStartParam = "StartYear";
	protected static String yearEndParam = "EndYear";
		
	public List<StatisticalType> inputs = new ArrayList<StatisticalType>();
	public HashMap<String, Image> producedImages = new HashMap<String, Image>();
	
	protected void extractElements() throws Exception{
		AnalysisLogger.getLogger().debug("General Processing: Extracting features ");
		
		config.setParam("Layer",geothermalData);
		config.setParam("Z","0");
		config.setParam("TimeIndex","0");
		config.setParam("BBox_LowerLeftLat","-90");
		config.setParam("BBox_LowerLeftLong","-180");
		config.setParam("BBox_UpperRightLat","90");
		config.setParam("BBox_UpperRightLong","180");
		config.setParam("XResolution","1");
		config.setParam("YResolution","1");
		config.setParam("OutputTableName",featuresTable);
		config.setParam("OutputTableLabel",featuresTable);
		
		XYExtraction extractor = new XYExtraction();
		extractor.setConfiguration(config);
		extractor.init();
		extractor.compute();
		AnalysisLogger.getLogger().debug("General Processing: All features extracted in the table: "+featuresTable);
	}
	
	abstract void initDatasets();
	abstract void fulfillDataset(String f_name, String f_area, double f_f1990_mwe, double f_f1995_mwt, double f_f1995_h_tjy, double f_f1995_mwe, double f_f2000_mwt, double f_f2000_h_tjy, 
			double f_f2000_mwe, double f_f2005_mwt, double f_f2005_h_tjy, double f_f2005_mwe, double f_f2010_mwt, double f_f2010_h_tjy, double f_f2010_mwe, double f_f2013_mwe, int startYear, int endYear);
	abstract JFreeChart createChartForMWE();
	abstract JFreeChart createChartForMWT();
	abstract JFreeChart createChartForTJY();
	
	abstract void renderChartForMWE();
	abstract void renderChartForMWT();
	abstract void renderChartForTJY();
	
	@Override
	public void compute() throws Exception {
		SessionFactory session = null;
		try {
				extractElements();
				status = 30;
				session = DatabaseUtils.initDBSession(config);
				List<Object> rows = DatabaseFactory.executeSQLQuery(getTable, session);
				
				initDatasets();
				String countryFilter = config.getParam(countryParam);
				int startYearFilter = 0; 
				int endYearFilter = 3000;
				try{startYearFilter  = Integer.parseInt(config.getParam(yearStartParam));}catch(Exception e1){}
				
				try{endYearFilter   = Integer.parseInt(config.getParam(yearEndParam));}catch(Exception e1){}
				
				if (endYearFilter<startYearFilter)
					throw new Exception("Invalid Years Range");
				
				int nrows = rows.size();
				
				float step = (float)MathFunctions.roundDecimal((90d-status)/(double)nrows,2);
				
				for (Object row : rows) {
					Object[] rowA = (Object[]) row;
					String country = ""+rowA[0];
					
					if (countryFilter==null  || countryFilter.length()==0 || countryFilter.equalsIgnoreCase("ALL") || countryFilter.equalsIgnoreCase(country)){
						
						// f_name, f_area, f_f1990_mwe, f_f1995_mwt, f_f1995_h_tjy, f_f1995_mwe, f_f2000_mwt, f_f2000_h_tjy, f_f2000_mwe, f_f2005_mwt, f_f2005_h_tjy, f_f2005_mwe, f_f2010_mwt, f_f2010_h_tjy, f_f2010_mwe, f_f2013_mwe
						fulfillDataset(""+rowA[0], ""+rowA[1], Double.parseDouble("" + rowA[2]),Double.parseDouble("" + rowA[3]),Double.parseDouble("" + rowA[4]),Double.parseDouble("" + rowA[5]),
							Double.parseDouble("" + rowA[6]),Double.parseDouble("" + rowA[7]),Double.parseDouble("" + rowA[8]),Double.parseDouble("" + rowA[9]),Double.parseDouble("" + rowA[10]),
							Double.parseDouble("" + rowA[11]),Double.parseDouble("" + rowA[12]),Double.parseDouble("" + rowA[13]),Double.parseDouble("" + rowA[14]),Double.parseDouble("" + rowA[15]), startYearFilter, endYearFilter);
					}
					
					status=Math.min(status+step,90);
				}
				
				try{
				JFreeChart chartMWE = createChartForMWE();
				Image imageMWE = ImageTools.toImage(chartMWE.createBufferedImage(680, 420));
				producedImages.put(MWE, imageMWE);
				if (display)renderChartForMWE();
				}catch(Exception e){AnalysisLogger.getLogger().debug("Could not produce chart for MWE");}
				
				try{
				JFreeChart chartMWT = createChartForMWT();
				Image imageMWT = ImageTools.toImage(chartMWT.createBufferedImage(680, 420));
				producedImages.put(MWT, imageMWT);
				if (display)renderChartForMWT();
				}catch(Exception e){AnalysisLogger.getLogger().debug("Could not produce chart for MWT");}
				try{
				JFreeChart chartTJY = createChartForTJY();
				Image imageTJY = ImageTools.toImage(chartTJY.createBufferedImage(680, 420));
				producedImages.put(TJY, imageTJY);
				if (display)renderChartForTJY();
				}catch(Exception e){AnalysisLogger.getLogger().debug("Could not produce chart for TJY");}

		} catch (Exception e) {
				throw e;
			} finally {
				if (session != null) {
					try {
						AnalysisLogger.getLogger().debug("Dropping table "+featuresTable);
						DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(featuresTable), session);
						AnalysisLogger.getLogger().debug("Table "+featuresTable+" dropped");
					} catch (Exception ee) {
						AnalysisLogger.getLogger().debug("Could not drop table "+featuresTable+": "+ee.getLocalizedMessage());
					}
					session.close();
				}
				status = 100;
			}
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		
		IOHelper.addStringInput(inputs, countryParam, "The country on which the analysis will focus (ALL to get statistics about all countries)", "ALL");
		IOHelper.addIntegerInput(inputs, yearStartParam, "The starting year of the analysis", "1990");
		IOHelper.addIntegerInput(inputs, yearEndParam, "The final year of the analysis", "2013");
		
		DatabaseType.addDefaultDBPars(inputs);
		return inputs;
		
	}

	@Override
	public StatisticalType getOutput() {
		PrimitiveType images = new PrimitiveType(HashMap.class.getName(), producedImages, PrimitiveTypes.IMAGES, "images", "Trends");
		LinkedHashMap<String, StatisticalType> outputmap = new LinkedHashMap<String, StatisticalType>();
		outputmap.put("Images", images);

		PrimitiveType output = new PrimitiveType(HashMap.class.getName(), outputmap, PrimitiveTypes.MAP, "ResultsMap", "Results Map");
		return output;
	}

	protected ResourceFactory resourceManager;
	public String getResourceLoad() {
		if (resourceManager == null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}

	public String getResources() {
		return ResourceFactory.getResources(100f);
	}

	@Override
	public float getStatus() {
		return status;
	}

	@Override
	public void init() throws Exception {
		
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration arg0) {
		config = arg0;
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shutdown");
	}

}
