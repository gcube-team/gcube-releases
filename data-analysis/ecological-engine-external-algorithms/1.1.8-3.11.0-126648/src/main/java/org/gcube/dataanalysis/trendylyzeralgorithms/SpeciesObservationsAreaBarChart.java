package org.gcube.dataanalysis.trendylyzeralgorithms;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.graphtools.plotting.graphs.HistogramGraph;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.postgresql.Driver;

public class SpeciesObservationsAreaBarChart extends
		StandardLocalExternalAlgorithm {
	static String databaseName = "DatabaseName";
	static String userParameterName = "DatabaseUserName";
	static String passwordParameterName = "DatabasePassword";
	static String urlParameterName = "DatabaseURL";
	private String species = "Species";
	private String yearStart = "Start_year";
	private String yearEnd = "End_year";
	private String area = "Area";
	private String selectedSpecies;
	private DefaultCategoryDataset defaultcategorydataset;
	private LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
	String databaseJdbc;

	
	String year_start;
	String year_end ;
	String databaseUser ;
	String chosenArea ;
	String databasePwd ;
	String table ;
	String areaName;
	Connection connection = null;
	protected String fileName;
	BufferedWriter out;

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug(
				"SpeciesObservationsAreaBarChart Initialization");
	}

	@Override
	public String getDescription() {
		return "An algorithm producing a bar chart for the distribution of a species along a certain type of marine area (e.g. LME or MEOW)";
	}
	
	
	public void fulfilParameters() throws IOException {
	
		 databaseJdbc = getInputParameter(urlParameterName);
		 year_start = getInputParameter(yearStart);
		 year_end = getInputParameter(yearEnd);
		selectedSpecies = getInputParameter(species);
		 databaseUser = getInputParameter(userParameterName);
		 chosenArea = getInputParameter(area);
		 databasePwd = getInputParameter(passwordParameterName);
		 table = "count_species_per_lme_per_year";
		 areaName = "lme_name";
		if (chosenArea.equals("LME")) {
			table = "count_species_per_lme_per_year";
			areaName = "lme_name";
		} else if (chosenArea.equals("MEOW")) {
			table = "count_species_per_meow_per_year";
			areaName = "ecoregion";
		}
		fileName = super.config.getPersistencePath() + "results.csv";
		out = new BufferedWriter(new FileWriter(fileName));
	
	}
	
	private ResultSet performeQuery() throws SQLException {
		connection = DriverManager.getConnection(databaseJdbc, databaseUser,
				databasePwd);
		Statement stmt = connection.createStatement();
		String query = "SELECT  tname," + areaName
				+ " ,sum(count) AS count FROM " + table + " WHERE upper(tname) like upper('"
				+ selectedSpecies + "') AND year::integer >=" + year_start
				+ "AND year::integer <=" + year_end
				+ "GROUP BY tname ,"+areaName+" ORDER BY count desc;";
		return stmt.executeQuery(query);
	}
	
	

	@Override
	protected void process() throws Exception {
		defaultcategorydataset = new DefaultCategoryDataset();
		String driverName = "org.postgresql.Driver";
		
		Class driverClass = Class.forName(driverName);
		Driver driver = (Driver) driverClass.newInstance();
		fulfilParameters() ;
		ResultSet rs = performeQuery() ;
		
		String s = selectedSpecies;
		while (rs.next()) {
			String ar = rs.getString(areaName);
			String count = rs.getString("count");
			PrimitiveType val = new PrimitiveType(String.class.getName(), count, PrimitiveTypes.STRING, ar, ar);
			write(ar+","+count);
			map.put(ar, val);
			int countOcc = Integer.parseInt(count);

			defaultcategorydataset.addValue(countOcc, s, ar);

		}
		connection.close();
		out.close();

	}

	@Override
	protected void setInputParameters() {
		addStringInput(species, "The species to analyze", "");
		addEnumerateInput(AreaEnum.values(), area, "Choose the area type",
				AreaEnum.LME.name());
		addStringInput(yearStart, "Starting year of the analysis", "1800");
		addStringInput(yearEnd, " Ending year of the analysis", "2020");
		addRemoteDatabaseInput("TrendyLyzerObis", urlParameterName,
				userParameterName, passwordParameterName, "driver", "dialect");

	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shutdown");
	}

	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(Map.class.getName(),
				PrimitiveType.stringMap2StatisticalMap(outputParameters),
				PrimitiveTypes.MAP, "", "");
		AnalysisLogger.getLogger().debug(
				"MapsComparator: Producing Bar Chart for the errors");
		// build image:
		HashMap<String, Image> producedImages = new HashMap<String, Image>();

		JFreeChart chart = HistogramGraph
				.createStaticChart(defaultcategorydataset);
		Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));
		producedImages.put("Most observed species", image);

		PrimitiveType images = new PrimitiveType(HashMap.class.getName(),
				producedImages, PrimitiveTypes.IMAGES, "Most observed species",
				"Most observed species");

		// end build image
		AnalysisLogger.getLogger().debug(
				"Bar Charts Species Occurrences Produced");
		// collect all the outputs
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
