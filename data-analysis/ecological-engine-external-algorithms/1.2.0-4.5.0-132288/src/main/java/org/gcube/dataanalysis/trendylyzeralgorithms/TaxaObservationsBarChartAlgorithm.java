package org.gcube.dataanalysis.trendylyzeralgorithms;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.graphtools.plotting.graphs.HistogramGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.TimeSeriesGraph;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.postgresql.Driver;

public class TaxaObservationsBarChartAlgorithm  extends StandardLocalInfraAlgorithm{
	static String databaseName = "DatabaseName";
	static String userParameterName = "DatabaseUserName";
	static String passwordParameterName = "DatabasePassword";
	static String urlParameterName = "DatabaseURL";
	private String yearStart = "Start_year";
	private String yearEnd = "End_year";
	private String taxa = "Level";
	String tax;
	protected String fileName;
	BufferedWriter out;
	private String firstTaxaNumber = "Taxa_number";
	private int taxaNumber;
	LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
	private DefaultCategoryDataset dataset;

	@Override
	public void init() throws Exception {AnalysisLogger.getLogger().debug(
			"Initialization TaxaObservationsBarChartAlgorithm");
		
	}

	@Override
	public String getDescription() {
		return "An algorithm producing a bar chart for the most observed taxa in a certain years range (with respect to the OBIS database)";

	}

	@Override
	protected void process() throws Exception {
		dataset = new DefaultCategoryDataset();
		String driverName = "org.postgresql.Driver";
		String tmp=getInputParameter(firstTaxaNumber);
		
		taxaNumber = Integer.parseInt(tmp);
		Class driverClass = Class.forName(driverName);
		Driver driver = (Driver) driverClass.newInstance();
		String databaseJdbc = getInputParameter(urlParameterName);
		String year_start = getInputParameter(yearStart);
		String year_end = getInputParameter(yearEnd);
		 tax = getInputParameter(taxa);
		String databaseUser = getInputParameter(userParameterName);
		String databasePwd = getInputParameter(passwordParameterName);
		fileName = super.config.getPersistencePath() + "results.csv";
		out = new BufferedWriter(new FileWriter(fileName));
		Connection connection = null;
		connection = DriverManager.getConnection(databaseJdbc, databaseUser,
				databasePwd);
		String table="genus_table_per_year";
		String column_name="genus";
		if (tax.equals("LME")) {
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
		Statement stmt = connection.createStatement();
		String query = "SELECT \""+column_name+"\", sum(count)AS count FROM public."+ table+" WHERE year::integer >= "
				+ year_start
				+ "AND year::integer <= "
				+ year_end
				+ " GROUP BY \""+ column_name+"\" ORDER BY count desc;";
		ResultSet rs = stmt.executeQuery(query);
		int i =0;
		String s = column_name;
			while (rs.next()&& i<taxaNumber) {
				String tname = rs.getString(column_name);
				String count = rs.getString("count");
				out.write(column_name+","+count);
				out.newLine();
				if(i<100)
				{PrimitiveType val = new PrimitiveType(String.class.getName(), count, PrimitiveTypes.STRING, tname, tname);
				map.put(tname, val);}
				int countOcc=Integer.parseInt(count);
				if(i<16)
				dataset.addValue(countOcc,s,tname);	
			
				i++;
				
		}
			connection.close();
			out.close();
		
	}

	@Override
	protected void setInputParameters() {
		addStringInput(firstTaxaNumber, "Number of taxa to report", "10");
		addEnumerateInput(TaxaEnum.values(), taxa, "Choose the taxonomy level",
				TaxaEnum.GENUS.name());
		addStringInput(yearStart, "Starting year of the analysis", "1800");
		addStringInput(yearEnd, "Ending year of the analysis", "2020");
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
		PrimitiveType p = new PrimitiveType(Map.class.getName(), PrimitiveType.stringMap2StatisticalMap(outputParameters), PrimitiveTypes.MAP, "Discrepancy Analysis","");
		AnalysisLogger.getLogger().debug("MapsComparator: Producing Gaussian Distribution for the errors");	
		//build image:
		HashMap<String, Image> producedImages = new HashMap<String, Image>();
		
		JFreeChart chart = HistogramGraph.createStaticChart(dataset);
	     Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));
	     producedImages.put("Taxonomy observations per year ("+tax+")", image);
	     
		PrimitiveType images = new PrimitiveType(HashMap.class.getName(), producedImages, PrimitiveTypes.IMAGES, "ErrorRepresentation", "Graphical representation of the error spread");
		
		//end build image
		AnalysisLogger.getLogger().debug("Line Charts Species Occurrences Produced");
		//collect all the outputs
		PrimitiveType f = new PrimitiveType(File.class.getName(), new File(
				fileName), PrimitiveTypes.FILE, "Species observations per area", "ObsFile");
		map.put("Output",f);
		map.put("Result", p);
		map.put("Images", images);
		
		//generate a primitive type for the collection
		PrimitiveType output = new PrimitiveType(HashMap.class.getName(), map, PrimitiveTypes.MAP, "ResultsMap", "Results Map");
		
		
		return output;
	}

}
