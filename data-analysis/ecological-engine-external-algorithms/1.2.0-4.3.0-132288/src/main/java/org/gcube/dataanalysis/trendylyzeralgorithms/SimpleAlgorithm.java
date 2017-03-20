package org.gcube.dataanalysis.trendylyzeralgorithms;

import java.awt.Image;
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

public class SimpleAlgorithm extends StandardLocalInfraAlgorithm{

	//case of db used
	static String urlParameterName = "DatabaseURL";
	static String userParameterName = "DatabaseUserName";
	static String passwordParameterName = "DatabasePassword";
	Class driverClass ;
	Driver driver ;
	Connection connection = null;
	
	LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
	private DefaultCategoryDataset defaultcategorydataset;
	
	private String species="Species";
	
	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("Initialization");
		String driverName = "org.postgresql.Driver";
		driverClass = Class.forName(driverName);
	  driver = (Driver) driverClass.newInstance();

		
		
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

@Override
	protected void process() throws Exception {
		String userSelectedSp = getInputParameter(species);
		
		String databaseJdbc = getInputParameter(urlParameterName);
		String databaseUser = getInputParameter(userParameterName);
		String databasePwd = getInputParameter(passwordParameterName);
		
		connection = DriverManager.getConnection(databaseJdbc, databaseUser,
				databasePwd);
		Statement stmt = connection.createStatement();
		String query = "SELECT numberOfObservation FROM public.count_species_per_year WHERE tname ="
				+ species;
		ResultSet rs = stmt.executeQuery(query);
		int i =0;
		String s = "Species";
			while (rs.next()) {
				
				
				String count = rs.getString("numberOfObservation");
				int countOcc=Integer.parseInt(count);
				PrimitiveType val = new PrimitiveType(String.class.getName(), count, PrimitiveTypes.STRING, species, species);
				map.put(species, val);	
				if(i<16)
				defaultcategorydataset.addValue(countOcc,s,species);	
				else
					break;
				i++;
				
		}
		connection.close();
		
	}

	@Override
	protected void setInputParameters() {
		addStringInput(species, "Slected species", "Solea solea");
		addRemoteDatabaseInput("TrendyLyzerObis", urlParameterName,
				userParameterName, passwordParameterName, "driver", "dialect");

		
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shutdown");	
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(Map.class.getName(), PrimitiveType.stringMap2StatisticalMap(outputParameters), PrimitiveTypes.MAP, "Discrepancy Analysis","");
		AnalysisLogger.getLogger().debug("MapsComparator: Producing Gaussian Distribution for the errors");	
		//build image:
		HashMap<String, Image> producedImages = new HashMap<String, Image>();
		
		JFreeChart chart = HistogramGraph.createStaticChart(defaultcategorydataset);
	     Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));
	     producedImages.put("Species Observations", image);
	     
		PrimitiveType images = new PrimitiveType(HashMap.class.getName(), producedImages, PrimitiveTypes.IMAGES, "ErrorRepresentation", "Graphical representation of the error spread");
		
		//end build image
		AnalysisLogger.getLogger().debug("Bar Charts Species Occurrences Produced");
		//collect all the outputs
		
		map.put("Result", p);
		map.put("Images", images);
		
		//generate a primitive type for the collection
		PrimitiveType output = new PrimitiveType(HashMap.class.getName(), map, PrimitiveTypes.MAP, "ResultsMap", "Results Map");
		
		
		return output;
	}
	

}
