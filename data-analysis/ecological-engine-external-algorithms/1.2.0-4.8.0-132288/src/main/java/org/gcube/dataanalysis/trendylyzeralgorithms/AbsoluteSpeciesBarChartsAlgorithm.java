package org.gcube.dataanalysis.trendylyzeralgorithms;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.postgresql.Driver;

public class AbsoluteSpeciesBarChartsAlgorithm extends
		StandardLocalInfraAlgorithm {
	
	LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
	static String databaseName = "DatabaseName";
	static String userParameterName = "DatabaseUserName";
	static String passwordParameterName = "DatabasePassword";
	static String urlParameterName = "DatabaseURL";
	protected String fileName;
	BufferedWriter out;
	private String firstSpeciesNumber = "Species_number";
	private String yearStart = "Start_year";
	private String yearEnd = "End_year";
	private int speciesNumber;
	String databaseJdbc;
	String year_start;
	String year_end;
	String databaseUser;
	String databasePwd;
	private Connection connection = null;
	private DefaultCategoryDataset defaultcategorydataset;

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("Initialization");
	}

	@Override
	public String getDescription() {
		return "An algorithm producing a bar chart for the most observed species in a certain years range (with respect to the OBIS database)";
	}

	public void fulfilParameters() {
		String tmp = getInputParameter(firstSpeciesNumber);
		speciesNumber = Integer.parseInt(tmp);
		databaseJdbc = getInputParameter(urlParameterName);
		year_start = getInputParameter(yearStart);
		year_end = getInputParameter(yearEnd);
		databaseUser = getInputParameter(userParameterName);
		databasePwd = getInputParameter(passwordParameterName);
		fileName = super.config.getPersistencePath() + "results.csv";
	}

	private ResultSet performeQuery() throws SQLException {

		connection = DriverManager.getConnection(databaseJdbc, databaseUser,
				databasePwd);
		Statement stmt = connection.createStatement();
		String query = "SELECT  tname, sum(count)AS count FROM public.count_species_per_year WHERE year::integer >="
				+ year_start
				+ "AND year::integer <="
				+ year_end
				+ "GROUP BY tname ORDER BY count desc;";
		return stmt.executeQuery(query);
	}

	@Override
	protected void process() throws Exception {
		defaultcategorydataset = new DefaultCategoryDataset();
		String driverName = "org.postgresql.Driver";
		Class driverClass = Class.forName(driverName);
		Driver driver = (Driver) driverClass.newInstance();
		fulfilParameters();
		out = new BufferedWriter(new FileWriter(fileName));

		ResultSet rs = performeQuery();
		int i = 0;
		String s = "Species";
		while (rs.next() && i < speciesNumber) {
			String tname = rs.getString("tname");
			String count = rs.getString("count");
			write(tname+","+count);
			int countOcc = Integer.parseInt(count);
			PrimitiveType val = new PrimitiveType(String.class.getName(),
					count, PrimitiveTypes.STRING, tname, tname);
			if(i<100)
			map.put(tname, val);
			if (i < 16)
				defaultcategorydataset.addValue(countOcc, s, tname);
			i++;

		}
		out.close();
		connection.close();

	}

	@Override
	protected void setInputParameters() {
		addStringInput(
				firstSpeciesNumber,
				"Number of species to report (max 17 will be visualized on the chart)",
				"10");
		addStringInput(yearStart, "Starting year of the analysis", "1800");
		addStringInput(yearEnd, "Ending year of the analysis", "2020");
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
				PrimitiveTypes.MAP, "Discrepancy Analysis", "");
		AnalysisLogger
				.getLogger()
				.debug("MapsComparator: Producing Gaussian Distribution for the errors");
		// build image:
		HashMap<String, Image> producedImages = new HashMap<String, Image>();

		JFreeChart chart = HistogramGraph
				.createStaticChart(defaultcategorydataset);
		Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));

		producedImages.put("Species Observations", image);

		PrimitiveType images = new PrimitiveType(HashMap.class.getName(),
				producedImages, PrimitiveTypes.IMAGES, "ErrorRepresentation",
				"Graphical representation of the error spread");
		// PrimitiveType images = new PrimitiveType("Species Observations",
		// producedImages, PrimitiveTypes.IMAGES, "ErrorRepresentation",
		// "Graphical representation of the error spread");
		PrimitiveType f = new PrimitiveType(File.class.getName(), new File(
				fileName), PrimitiveTypes.FILE, "OccFile", "OccFile");
		// end build image
		AnalysisLogger.getLogger().debug(
				"Bar Charts Species Occurrences Produced");
		// collect all the outputs
		map.put("File", f);
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
