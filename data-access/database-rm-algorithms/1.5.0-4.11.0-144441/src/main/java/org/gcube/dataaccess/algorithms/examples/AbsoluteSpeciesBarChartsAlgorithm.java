package org.gcube.dataaccess.algorithms.examples;

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
import java.util.List;
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

public class AbsoluteSpeciesBarChartsAlgorithm extends
		StandardLocalExternalAlgorithm {

	LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();

	static String databaseName = "DatabaseName";
	static String userParameterName = "DatabaseUserName";
	static String passwordParameterName = "DatabasePassword";
	static String urlParameterName = "DatabaseURL";

	// static String databaseName = "Obis2Repository";
	// static String userParameterName = "postgres";
	// static String passwordParameterName = "0b1s@d4sc13nc3";
	// static String urlParameterName =
	// "jdbc:postgresql://obis2.i-marine.research-infrastructures.eu:5432/obis";

	protected String fileName;
	BufferedWriter out;
	private String firstSpeciesNumber = " SpeciesNumber :";
	private String yearStart = "Start year :";
	private String yearEnd = "End year :";
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
		return ": A transducer algorithm that produces the list of top n most observed taxa, i.e. the species taxa having the largest number of occurrence records, in the OBIS database in a given time interval";
	}

	public void fulfilParameters() {

		AnalysisLogger.getLogger().debug("fulfilParameters method");

		// String tmp = getInputParameter(firstSpeciesNumber);

		List<StatisticalType> list = getInputParameters();

		System.out.println("size: " + list.size());

		for (int i = 0; i < list.size(); i++) {

			System.out.println(list.get(i).getName()+" "+list.get(i).getDefaultValue());

			if (list.get(i).getName().equals(firstSpeciesNumber)) {
				// System.out.println(list.get(i).getName());
				String tmp = list.get(i).getDefaultValue();
				speciesNumber = Integer.parseInt(tmp);

			}

			if (list.get(i).getName().equals(yearStart)) {

				year_start = list.get(i).getDefaultValue();

			}

			if (list.get(i).getName().equals(yearEnd)) {

				year_end = list.get(i).getDefaultValue();

			}
//			if (list.get(i).getName().equals(urlParameterName)) {
//
//				databaseJdbc = list.get(i).getDefaultValue();
//
//			}
//			if (list.get(i).getName().equals(userParameterName)) {
//
//				databaseUser = list.get(i).getDefaultValue();
//
//			}
//			if (list.get(i).getName().equals(passwordParameterName)) {
//
//				databasePwd = list.get(i).getDefaultValue();
//
//			}
			
			databaseJdbc = getInputParameter("DatabaseURL");
			databaseUser= getInputParameter("DatabaseUserName");
			databasePwd= getInputParameter("DatabasePassword");
					

		}

		// System.out.println(tmp);

//		databaseJdbc = getInputParameter(urlParameterName);
		// year_start = getInputParameter(yearStart);
//		year_end = getInputParameter(yearEnd);
//		databaseUser = getInputParameter(userParameterName);
//		databasePwd = getInputParameter(passwordParameterName);

//		fileName = super.config.getPersistencePath() + "results.csv";
		
		fileName = config.getConfigPath() + "results.csv";
		
//		fileName = "./cfg/" + "results.csv";
		
		
		AnalysisLogger.getLogger().debug("Percorso file: " + fileName);

		AnalysisLogger.getLogger().debug("fulfilParameters method");
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

		

		System.out.println("In the process");

		defaultcategorydataset = new DefaultCategoryDataset();
//		String driverName = "org.postgresql.Driver";
//		Class driverClass = Class.forName(driverName);
//		Driver driver = (Driver) driverClass.newInstance();

		System.out.println("pre fulfill");
		fulfilParameters();
		System.out.println("post fulfill");

//		String tmp = getInputParameter(firstSpeciesNumber);
//		System.out.println("process-> speciesnumber value: " + tmp);

		// String tmp="10";
//		speciesNumber = Integer.parseInt(tmp);

//		year_start = getInputParameter(yearStart);
		// year_start="1800";

//		year_end = getInputParameter(yearEnd);
		// year_end="2020";

		// fileName = super.config.getPersistencePath() + "results.csv";

//		fileName = "results.csv";

		out = new BufferedWriter(new FileWriter(fileName));

		System.out.println("pre query");
		ResultSet rs = performeQuery();
		System.out.println("post query");

		// connection =
		// DriverManager.getConnection("jdbc:postgresql://obis2.i-marine.research-infrastructures.eu:5432/obis",
		// "postgres",
		// "0b1s@d4sc13nc3");
		// Statement stmt = connection.createStatement();
		// String query =
		// "SELECT  tname, sum(count)AS count FROM public.count_species_per_year WHERE year::integer >="
		// + year_start
		// + "AND year::integer <="
		// + year_end
		// + "GROUP BY tname ORDER BY count desc;";

		//
		// System.out.println("pre query");
		// ResultSet rs=stmt.executeQuery(query);
		//
		// System.out.println("post query");
		//
		int i = 0;
		String s = "Species";
		while (rs.next() && i < speciesNumber) {

			System.out.println(rs.toString());

			String tname = rs.getString("tname");
			String count = rs.getString("count");

			System.out.println("tname:" + tname);

			System.out.println("count:" + count);

			write(tname + "," + count);
			int countOcc = Integer.parseInt(count);

			PrimitiveType val = new PrimitiveType(String.class.getName(),
					count, PrimitiveTypes.STRING, tname, tname);
			
			
			if (i < 100)
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

		System.out.println("Sono in SetParameters");

		addStringInput(
				firstSpeciesNumber,
				"Number of species to report (max 17 will be visualized on the chart)",
				"10");

		// System.out.println(firstSpeciesNumber);

		addStringInput(yearStart, "Starting year of the analysis", "1800");
		// System.out.println(yearStart);

		addStringInput(yearEnd, "Ending year of the analysis", "2020");
		// System.out.println(yearEnd);

		// addRemoteDatabaseInput("Obis2Repository", urlParameterName,
		// userParameterName, passwordParameterName, "driver", "dialect");

		// addRemoteDatabaseInput("Obis2Repository", urlParameterName,
		// userParameterName, passwordParameterName, "org.postgresql.Driver",
		// "org.hibernate.dialect.PostgreSQLDialect");

		System.out.println("pre addRemoteDB");
//		addRemoteDatabaseInput(
//				"Obis2Repository",
//				"jdbc:postgresql://obis2.i-marine.research-infrastructures.eu:5432/obis",
//				"postgres", "0b1s@d4sc13nc3", "org.postgresql.Driver",
//				"org.hibernate.dialect.PostgreSQLDialect");
		
//		 addRemoteDatabaseInput("Obis2Repository", urlParameterName,
//		 userParameterName, passwordParameterName, "driver", "dialect");
		
		
		System.out.println("post addRemoteDB");

		// super.config.setConfigPath("./cfg/");
		// config.setConfigPath();

		// super.config.setParam("DatabaseUserName","gcube");
		// super.config.setParam("DatabasePassword","d4science2");
		// super.config.setParam("DatabaseURL","jdbc:postgresql://obis2.i-marine.research-infrastructures.eu:5432/obis");
		// super.config.setParam("DatabaseDriver","org.postgresql.Driver");

		// System.out.println("URL: "+ super.config.getDatabaseURL());

	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shutdown");
	}

	@Override
	public StatisticalType getOutput() {
		
		AnalysisLogger.getLogger().debug("In getOutput");
		
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
		
//		PrimitiveType output=null;

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
