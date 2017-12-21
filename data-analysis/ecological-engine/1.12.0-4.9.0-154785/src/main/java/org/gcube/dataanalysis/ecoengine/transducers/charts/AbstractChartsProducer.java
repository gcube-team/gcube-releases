package org.gcube.dataanalysis.ecoengine.transducers.charts;

import java.awt.Image;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.hibernate.SessionFactory;

public abstract class AbstractChartsProducer extends StandardLocalExternalAlgorithm {

	protected static String inputTableParameter = "InputTable";
	protected static String attributesParameter = "Attributes";
	protected static String quantitiesParameter = "Quantities";
	protected static String timeParameter = "Time";
	protected static String topElementsNumber = "TopElementsNumber";
	
	public LinkedHashMap<String, Image> producedImages = new LinkedHashMap<String, Image>();
	public LinkedHashMap<String, File> producedFiles = new LinkedHashMap<String, File>();
	public boolean displaycharts = false;
	protected int maxElements = 10;

	protected SessionFactory connection = null;

	@Override
	protected abstract void setInputParameters();
	
	@Override
	public StatisticalType getOutput() {
		LinkedHashMap<String, StatisticalType> outputmap = new LinkedHashMap<String, StatisticalType>();
		if (producedImages.size()>0){
			PrimitiveType images = new PrimitiveType(HashMap.class.getName(), producedImages, PrimitiveTypes.IMAGES, "images", "Charts");
			outputmap.put("Images", images);
		}
		if (producedFiles.size()>0){
			for (String file:producedFiles.keySet()){
				File f = producedFiles.get(file);
				PrimitiveType p = new PrimitiveType(File.class.getName(), f, PrimitiveTypes.FILE, f.getName(), file);
				outputmap.put(file, p);
			}
		}
		PrimitiveType output = new PrimitiveType(HashMap.class.getName(), outputmap, PrimitiveTypes.MAP, "ResultsMap", "Results Map");
		return output;
	}

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("ChartsProducer Initialized");
	}

	@Override
	public abstract String getDescription();

	public String[] getDimensions() {
		String dimensionS = IOHelper.getInputParameter(config, attributesParameter);
		String[] dimensions = null;
		try{dimensions = dimensionS.split(AlgorithmConfiguration.getListSeparator());}catch(Exception e){}
		if (dimensions!=null && dimensions.length==1 && dimensions[0].trim().length()==0)
			dimensions = null;
		return dimensions;
	}

	public String[] getQuantities() {
		String quantitieS = IOHelper.getInputParameter(config, quantitiesParameter);
		String[] quantities = {""};
		if (quantitieS!=null){
			quantities = quantitieS.split(AlgorithmConfiguration.getListSeparator());
		}
		return quantities;
	}

	public String getTimeDimension(){
		String timeS = IOHelper.getInputParameter(config, timeParameter);
		return timeS;
	}
	
	public String InfoRetrievalQuery(String table, String[] dimensions, String quantity,String time) {
		if (time!=null){
			if (dimensions!=null && dimensions.length>0)
				return "select distinct " + Arrays.toString(dimensions).replace("[", "").replace("]", "") + " , " + quantity +" as qa123a,"+time+" as timea123a from "+table+" order by timea123a";
			else
				return "select distinct " +quantity +","+time+" from "+table+" order by "+time;
		}
		else{
			try{maxElements = Integer.parseInt(IOHelper.getInputParameter(config, topElementsNumber));}catch(Exception e){}
			if (dimensions!=null && dimensions.length>0){
				String field = Arrays.toString(dimensions).replace("[", "").replace("]", "");
				return "select distinct " +field  + " , sum(CAST (" + quantity +" as real))  as qa123a from "+table+" where CAST("+quantity+" as character varying) <>'' "+" group by "+field+" order by qa123a DESC limit " + maxElements;
			}
			else
				return "select distinct row_number() over(), " + quantity +" from "+table+" order by " + quantity + " DESC limit " + maxElements;
		}
	}
	
	@Override
	protected void process() throws Exception {
		status = 10;
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		try {
			AnalysisLogger.getLogger().debug("ChartsProducer started");
			String driver = config.getParam("DatabaseDriver");
			String username = config.getParam("DatabaseUserName");
			String pwd = config.getParam("DatabasePassword");
			String url = config.getParam("DatabaseURL");
			String table = IOHelper.getInputParameter(config, inputTableParameter);

			AnalysisLogger.getLogger().debug("ChartsProducer: Driver: " + driver + " User " + username + " URL " + url + " Table: " + table);
			connection = DatabaseUtils.initDBSession(config);

			AnalysisLogger.getLogger().debug("ChartsProducer: Connection initialized");

			LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
			conf.setDatabaseUserName(username);
			conf.setDatabasePassword(pwd);
			conf.setDatabaseDriver(driver);
			conf.setDatabaseURL(url);
			conf.setDatabaseDialect("org.hibernate.dialect.PostgreSQLDialect");

			String[] dimensions = getDimensions();
			String[] quantities = getQuantities();
			String time = getTimeDimension();
			
			//one quantity for each chart
			boolean noCharts = true;
			status = 40;
			float step = (90-status)/(float)quantities.length;
			for (String quantity : quantities) {
				//produce chart with dimensions,quantity, time
				String query = InfoRetrievalQuery(table,dimensions,quantity,time);
				AnalysisLogger.getLogger().debug("ChartsProducer: Query for retrieving information "+query);
				List<Object> values = DatabaseFactory.executeSQLQuery(query, connection);
				if (values==null)
					throw new Exception("There are issued in managing selected attributes and quantities");
				else if (values.size()==0)
					throw new Exception("There are no viable values to be processed");
					
				LinkedHashMap<String,Object> charts= createCharts(dimensions,quantity,time,values,displaycharts);
				
				for (String chartName:charts.keySet()){
					Object chart = charts.get(chartName);
					if (chart!=null){
						noCharts=false;
						//patch for current SM visualization
						if (chart instanceof File)
							producedFiles.put("Chart focused on "+quantity+" - "+chartName, (File)chart);
						else
							producedImages.put("Chart focused on "+quantity+" - "+chartName, (Image)chart);
					}
				}
				
				status+=step;
			}
			if (noCharts)
				throw new Exception("Error - no chart was produced because of incompatibility with the selected input parameters");
			
			AnalysisLogger.getLogger().debug("ChartsProducer: finished");

		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception("Error during the computation: "+e.getMessage());
		} finally {
			shutdown();
			status = 100;
		}
	}

	public abstract LinkedHashMap<String,Object> createCharts(String[] dimensions,String quantity,String time,List<Object> rows, boolean displaychart);
		
	
	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("ChartsProducer shutdown");
		DatabaseUtils.closeDBConnection(connection);
	}

}
