package org.gcube.dataanalysis.trendylyzeralgorithms;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.graphtools.plotting.graphs.TimeSeriesGraph;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.DynamicEnum;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.postgresql.Driver;

public class SpeciesObservationsPerMEOWAreaPerYearLineChart extends StandardLocalExternalAlgorithm {
	
	static String databaseName = "DatabaseName";
	static String userParameterName = "DatabaseUserName";
	static String passwordParameterName = "DatabasePassword";
	static String urlParameterName = "DatabaseURL";
	private String yearStart = "Start_year";
	private String yearEnd = "End_year";
	private Hashtable areaTable= new Hashtable<String , String>();
	private static String area = "Area_type";
	private String choseArea = "Area_name";
	private String selectedAreaName;
	static int justcall=0;
	private String[] speciesNames;
	MEOWenum enuArea=new MEOWenum();
	private TimeSeriesCollection dataset;
	protected String fileName;
	BufferedWriter out;
	Connection connection = null;
	String year_start ;
	String year_end ;
	String table = "count_species_per_meow_per_year";
	String areaName = "ecoregion";
	String databaseJdbc;		
	String databaseUser ;
	String databasePwd;

	@Override
	protected void setInputParameters() {

		addRemoteDatabaseInput("TrendyLyzerObis", urlParameterName,
				userParameterName, passwordParameterName, "driver", "dialect");
		if(justcall==0)
		{ justcall=1;
		
		try {
			queryArea(getStaticConnection());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
		}
		
		addEnumerateInput(MEOWEnumType.values(), area, "Choose the area name",
				Util.formatAreaName("AGULHAS BANK"));
		
		//addStringInput(choseArea,"Choose the area name","");
		addStringInput(yearStart, "Starting year of the analysis", "1800");
		addStringInput(yearEnd, "Ending year of the analysis", "2020");
		PrimitiveTypesList speciesSelected = new PrimitiveTypesList(String.class.getName(),PrimitiveTypes.STRING, "Selected species", "List of the species to analyze", false);
		super.inputs.add(speciesSelected);

		

	}
	//to do: change 
	public void queryArea(Connection connection) throws SQLException
	{
		AnalysisLogger.getLogger().debug("call queryArea");
		String query= "select upper(ecoregion)as ecoregion from geo.meow order by ecoregion";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			
			String area=rs.getString("ecoregion");
			AnalysisLogger.getLogger().debug(area);
			//areaTable.put(Util.formatAreaName(area), area);
			enuArea.addEnum(MEOWEnumType.class, area);
		}
		connection.close();

		
	}
	
	public Connection getStaticConnection() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Class.forName("org.postgresql.Driver");
		Connection connection = null;
		connection = DriverManager.getConnection(
		   "jdbc:postgresql://obis2.i-marine.research-infrastructures.eu/obis","postgres", "0b1s@d4sc13nc3");
		
		return connection;
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}
	
	
	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(Map.class.getName(),
				PrimitiveType.stringMap2StatisticalMap(outputParameters),
				PrimitiveTypes.MAP, "", "");
		AnalysisLogger
				.getLogger()
				.debug("MapsComparator: Producing Line Chart for the errors");
		// build image:
		HashMap<String, Image> producedImages = new HashMap<String, Image>();

		JFreeChart chart = TimeSeriesGraph.createStaticChart(dataset, "yyyy");
		Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));
		producedImages.put("Selected species observations per MEOW area ("+ selectedAreaName+")", image);

		PrimitiveType images = new PrimitiveType(HashMap.class.getName(),
				producedImages, PrimitiveTypes.IMAGES, "ErrorRepresentation",
				"Selected species observations per MEOW area("+ selectedAreaName+")");

		// end build image
		AnalysisLogger.getLogger().debug(
				"Line Taxonomy Occurrences Produced");
		// collect all the outputs
		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
		PrimitiveType f = new PrimitiveType(File.class.getName(), new File(
				fileName), PrimitiveTypes.FILE, "Species observations per area", "ObsFile");
		map.put("Output",f);
		map.put("Result", p);
		map.put("Images", images);

		// generate a primitive type for the collection
		PrimitiveType output = new PrimitiveType(HashMap.class.getName(), map,
				PrimitiveTypes.MAP, "ResultsMap", "Results Map");

		return output;
	}
	




	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug(
				"Initialization SpeciesObservationsPerAreaPerYearLineChart");
		
	}
	@Override
	public String getDescription() {
		return "Algorithm returning most observed species in a specific years range (data collected from OBIS database).";

	}
	public void fulfilParameters() throws Exception {
		String driverName = "org.postgresql.Driver";
		Class driverClass = Class.forName(driverName);
		Driver driver = (Driver) driverClass.newInstance();
		dataset = new TimeSeriesCollection();

		 year_start = getInputParameter(yearStart);
		 year_end = getInputParameter(yearEnd);

		selectedAreaName=getInputParameter(area);
		AnalysisLogger.getLogger().debug("*********NAMEE*******"+selectedAreaName);

		 databaseJdbc = getInputParameter(urlParameterName);		
		 databaseUser = getInputParameter(userParameterName);
		 databasePwd = getInputParameter(passwordParameterName);
		 connection = DriverManager.getConnection(databaseJdbc, databaseUser,
					databasePwd);
		 fileName = super.config.getPersistencePath() + "results.csv";
			out = new BufferedWriter(new FileWriter(fileName));
	}
	@Override
	protected void process() throws Exception {
		AnalysisLogger.getLogger().debug("Start to process");
		fulfilParameters();
		
  		Statement stmt = connection.createStatement();
		speciesNames = config.getParam("Selected species").split(AlgorithmConfiguration.getListSeparator());
		for (String spe : speciesNames) {
			String query = "select ecoregion ,year,count from "+table+" where upper(tname) like upper('"+
					spe+ "') and upper(ecoregion) like '"+selectedAreaName+"' and year::integer >="
				+ year_start
				+ "AND year::integer <="
				+ year_end +" order by year;";
			AnalysisLogger.getLogger().debug(query);

			ResultSet rs = stmt.executeQuery(query);

			final TimeSeries series = new TimeSeries(spe);

			while (rs.next()) {
				if (rs.getString("year") != null) {
					AnalysisLogger.getLogger().debug(rs.getString("year")+" count "+ rs.getString("count"));
					int year = Integer.parseInt(rs.getString("year"));
					int count = Integer.parseInt(rs.getString("count"));
					write(spe+","+year+","+count);
					series.add(new Year(year), count);
				}

		}

			dataset.addSeries(series);

		}
		AnalysisLogger.getLogger().debug(dataset.toString());

		connection.close();
		out.close();
		
	}
	enum MEOWEnumType {}
	class MEOWenum extends DynamicEnum{	
		public Field[] getFields() {
			
			Field[] fields = MEOWEnumType.class.getDeclaredFields();
			return fields;
		}}
	 public void write(String writeSt) {
			try {
				out.write(writeSt);
				out.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	
}
	
	
