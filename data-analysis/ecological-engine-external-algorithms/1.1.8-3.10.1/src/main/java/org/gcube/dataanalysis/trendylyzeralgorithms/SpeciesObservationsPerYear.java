package org.gcube.dataanalysis.trendylyzeralgorithms;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.graphtools.plotting.graphs.HistogramGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.TimeSeriesGraph;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.postgresql.Driver;

public class SpeciesObservationsPerYear extends StandardLocalExternalAlgorithm {
	static String databaseName = "DatabaseName";
	static String userParameterName = "DatabaseUserName";
	static String passwordParameterName = "DatabasePassword";
	static String urlParameterName = "DatabaseURL";
	private String yearStart = "Start_year";
	private String yearEnd = "End_year";
	private String[] speciesNames;
	private TimeSeriesCollection dataset;
	protected String fileName;
	BufferedWriter out;

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug(
				"Initialization SpeciesObservationsPerYear");

	}

	@Override
	public String getDescription() {
		return "An algorithm producing the trend of the observations for a certain species in a certain years range.";

	}

	@Override
	protected void process() throws Exception {
		dataset = new TimeSeriesCollection();
		String driverName = "org.postgresql.Driver";
		AnalysisLogger.getLogger().debug("Inside process ");
		fileName = super.config.getPersistencePath() + "results.csv";
		out = new BufferedWriter(new FileWriter(fileName));
		Class driverClass = Class.forName(driverName);
		Driver driver = (Driver) driverClass.newInstance();
		String databaseJdbc = getInputParameter(urlParameterName);
		String year_start = getInputParameter(yearStart);
		String year_end = getInputParameter(yearEnd);
		String databaseUser = getInputParameter(userParameterName);
		String databasePwd = getInputParameter(passwordParameterName);
		Connection connection = null;
		connection = DriverManager.getConnection(databaseJdbc, databaseUser,
				databasePwd);
		Statement stmt = connection.createStatement();


		
		speciesNames = config.getParam("Selected species").split(AlgorithmConfiguration.getListSeparator());
		for (String sp : speciesNames) {
			AnalysisLogger.getLogger().debug("Species: " + sp);

			String query = "select tname,year,count from public.count_species_per_year where upper(tname) like upper('"
					+ sp + "') and year::integer >"
				+ year_start
				+ "AND year::integer <"
				+ year_end +" order by year;";
			ResultSet rs = stmt.executeQuery(query);

			final TimeSeries series = new TimeSeries(sp);
			
			while (rs.next()) {
				if (rs.getString("year") != null) {

					int year = Integer.parseInt(rs.getString("year"));
					int count = Integer.parseInt(rs.getString("count"));
					out.write(sp+","+year+","+count);
					out.newLine();

					series.add(new Year(year), count);
				}
			}
			dataset.addSeries(series);

		}
		AnalysisLogger.getLogger().debug(dataset.toString());
		out.close();
		connection.close();

	}

	

	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(Map.class.getName(),
				PrimitiveType.stringMap2StatisticalMap(outputParameters),
				PrimitiveTypes.MAP, " ", "");
		AnalysisLogger
				.getLogger()
				.debug("MapsComparator: Producing Line Chart for the errors");
		// build image:
		HashMap<String, Image> producedImages = new HashMap<String, Image>();

		JFreeChart chart = TimeSeriesGraph.createStaticChart(dataset, "yyyy");
		Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));
		producedImages.put("Species observations per year", image);

		PrimitiveType images = new PrimitiveType(HashMap.class.getName(),
				producedImages, PrimitiveTypes.IMAGES, "ErrorRepresentation",
				"Species observations per year");

		// end build image
		AnalysisLogger.getLogger().debug(
				"Line Species Occurrences Produced");
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
	protected void setInputParameters() {
		
		addStringInput(yearStart, "Starting year of the analysis", "1800");
		addStringInput(yearEnd, "Ending year of the analysis", "2020");
		PrimitiveTypesList speciesSelected = new PrimitiveTypesList(String.class.getName(),PrimitiveTypes.STRING, "Selected species", "List of the species to analyze", false);
		super.inputs.add(speciesSelected);
		addRemoteDatabaseInput("TrendyLyzerObis", urlParameterName,
				userParameterName, passwordParameterName, "driver", "dialect");

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
