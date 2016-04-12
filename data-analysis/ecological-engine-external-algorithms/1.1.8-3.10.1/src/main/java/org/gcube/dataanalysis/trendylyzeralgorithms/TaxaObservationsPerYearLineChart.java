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
import org.gcube.contentmanagement.graphtools.plotting.graphs.TimeSeriesGraph;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.postgresql.Driver;

public class TaxaObservationsPerYearLineChart 
	extends StandardLocalExternalAlgorithm {
		static String databaseName = "DatabaseName";
		static String userParameterName = "DatabaseUserName";
		static String passwordParameterName = "DatabasePassword";
		static String urlParameterName = "DatabaseURL";
		private String yearStart = "Start_year";
		private String yearEnd = "End_year";
		private String taxa = "Level";
		private String tax;
	
		private String[] taxaNames;
		private TimeSeriesCollection dataset;
		protected String fileName;
		BufferedWriter out;

	
		
		
		@Override
		public void init() throws Exception {
			AnalysisLogger.getLogger().debug(
					"Initialization TaxaObservationsPerYearLineChart");

		}

		@Override
		public String getDescription() {
			return "Algorithm returning most observations taxonomy trend in a specific years range (with respect to the OBIS database)";

		}

		

		@Override
		protected void process() throws Exception {
			String driverName = "org.postgresql.Driver";
			dataset = new TimeSeriesCollection();

			Class driverClass = Class.forName(driverName);
			Driver driver = (Driver) driverClass.newInstance();
			String databaseJdbc = getInputParameter(urlParameterName);
			String year_start = getInputParameter(yearStart);
			String year_end = getInputParameter(yearEnd);
			fileName = super.config.getPersistencePath() + "results.csv";
			out = new BufferedWriter(new FileWriter(fileName));
			tax = getInputParameter(taxa);
			String table="genus_table_per_year";
			String column_name="genus";
			if (tax.equals("GENUS")) {
				table = "genus_table_per_year";
				column_name = "genus";
			} else if (tax.equals("CLASS")) {
				table = "class_table_per_year";
				column_name = "class";
			} else if (tax.equals("FAMILY")) {
				table = "family_table_per_year";
				column_name = "family";
			} else if (tax.equals("ORDER")) {
				table = "order_table_per_year";
				column_name = "order";
				
			}

		
//			AnalysisLogger.getLogger().debug("Taxonomy found: " + taxonomy.size());


			String databaseUser = getInputParameter(userParameterName);
			String databasePwd = getInputParameter(passwordParameterName);
			Connection connection = null;
			connection = DriverManager.getConnection(databaseJdbc, databaseUser,
					databasePwd);
			Statement stmt = connection.createStatement();
			taxaNames = config.getParam("Selected taxonomy").split(AlgorithmConfiguration.getListSeparator());
			for (String tx : taxaNames) {
				AnalysisLogger.getLogger().debug("Species: " + tx);

				String query = "select \""+ column_name+"\",year,count from "+table+" where upper("+column_name+") like upper('"
						+ tx + "') and year::integer >="
					+ year_start
					+ "AND year::integer <="
					+ year_end +" order by year;";
				AnalysisLogger.getLogger().debug(
						query);
				ResultSet rs = stmt.executeQuery(query);
				
				final TimeSeries series = new TimeSeries(tx);

				while (rs.next()) {
					if (rs.getString("year") != null) {

						int year = Integer.parseInt(rs.getString("year"));
						int count = Integer.parseInt(rs.getString("count"));
						AnalysisLogger.getLogger().debug("year: " + year+ "  count : "+ count);
						out.write(tx+","+year+","+count);
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
		protected void setInputParameters() {
			addEnumerateInput(TaxaEnum.values(), taxa, "Choose the taxonomy level",
					TaxaEnum.GENUS.name());
			addStringInput(yearStart, "Starting year of the analysis", "1800");
			addStringInput(yearEnd, "Ending year of the analysis", "2020");
			PrimitiveTypesList taxaSelected = new PrimitiveTypesList(String.class.getName(),PrimitiveTypes.STRING, "Selected taxonomy", "List of taxa to analyze", false);
			super.inputs.add(taxaSelected);
			
			// addStringInput("Species", "The species", config.getParam("Species"));
			addRemoteDatabaseInput("TrendyLyzerObis", urlParameterName,
					userParameterName, passwordParameterName, "driver", "dialect");

		}

		@Override
		public void shutdown() {
			// TODO Auto-generated method stub

		}
	
		@Override
		public StatisticalType getOutput() {
			PrimitiveType p = new PrimitiveType(Map.class.getName(),
					PrimitiveType.stringMap2StatisticalMap(outputParameters),
					PrimitiveTypes.MAP, "Discrepancy Analysis", "");
			AnalysisLogger
					.getLogger()
					.debug("MapsComparator: Producing Line Chart for the errors");
			// build image:
			HashMap<String, Image> producedImages = new HashMap<String, Image>();

			JFreeChart chart = TimeSeriesGraph.createStaticChart(dataset, "yyyy");
			Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));
			producedImages.put("Taxa observations tends per year ("+tax+")", image);

			PrimitiveType images = new PrimitiveType(HashMap.class.getName(),
					producedImages, PrimitiveTypes.IMAGES, "ErrorRepresentation",
					"Taxa observations per year ("+tax+")");

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
		


}
