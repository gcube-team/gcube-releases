package org.gcube.portlets.user.transectgenerator.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.data.GraphSamplesTable;
import org.gcube.contentmanagement.graphtools.data.conversions.GraphConverter2D;
import org.gcube.contentmanagement.graphtools.plotting.graphs.TransectLineGraph;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphData;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.timeseries.charts.support.types.Point;
import org.gcube.portlets.user.transectgenerator.databases.DBAquamapsExtractor;
import org.gcube.portlets.user.transectgenerator.databases.DBPostGISExtractror;
import org.gcube.portlets.user.transectgenerator.databases.tools.MapMerger;

import com.rapidminer.RapidMiner;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.preprocessing.sampling.AbsoluteSampling;
import com.rapidminer.tools.OperatorService;
//import com.thoughtworks.xstream.XStream;
//import com.thoughtworks.xstream.io.xml.DomDriver;


public class ExampleDataBuiltGraph {
	private static final String LogFile = "ALog.properties";
	private static final String OperatorsFile = "operators.xml";
	
	public static void main(String[] args) throws Exception{
		
		String cfg = "./cfg/";
	
		String x1="0";
		String y1="0";
		String x2="+360";
		String y2="0";
		String SRID="4326";
		
		String biodiversityfield = "maxspeciescountinacell";
		String tablename = "default2010_06_03_19_03_24_582";
		
		int maxelements = 1000;
		
		
		AnalysisLogger.setLogger(cfg + LogFile);
		System.setProperty("rapidminer.init.operators", cfg + OperatorsFile);
		
		//************//
		//CODE FOR TRANSECT
		String dbusernameGeo = "postgres";
		String dbpasswordGeo = "d4science2";
		String dbdialectGeo = "org.hibernatespatial.postgis.PostgisDialect";
		String dbdriverGeo = "org.postgresql.Driver";
		String dburlGeo = "jdbc:postgresql://geoserver-dev4.d4science.org/aquamapsdb";
		
		LexicalEngineConfiguration geoserverconfig = new LexicalEngineConfiguration();
		geoserverconfig .setDatabaseUserName(dbusernameGeo);
		geoserverconfig .setDatabasePassword(dbpasswordGeo);
		geoserverconfig .setDatabaseDialect(dbdialectGeo);
		geoserverconfig .setDatabaseDriver(dbdriverGeo);
		geoserverconfig .setDatabaseURL(dburlGeo);
		DBPostGISExtractror dbGis = new DBPostGISExtractror(cfg,geoserverconfig);
		
		String dbusernameAquamaps = "postgres";
		String dbpasswordAquamaps = "d4science2";
		String dbdialectAquamaps = "org.hibernatespatial.postgis.PostgisDialect";
		String dbdriverAquamaps = "org.postgresql.Driver";
		String dburlAquamaps = "jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/essentialaquamaps";
		
		LexicalEngineConfiguration aquamapsconfig = new LexicalEngineConfiguration();
		aquamapsconfig .setDatabaseUserName(dbusernameAquamaps);
		aquamapsconfig .setDatabasePassword(dbpasswordAquamaps);
		aquamapsconfig .setDatabaseDialect(dbdialectAquamaps);
		aquamapsconfig .setDatabaseDriver(dbdriverAquamaps);
		aquamapsconfig .setDatabaseURL(dburlAquamaps);
		
		DBAquamapsExtractor dbaqua = new DBAquamapsExtractor(cfg,aquamapsconfig);
		//************//		
		RapidMiner.init();
		
		AnalysisLogger.getLogger().info("initialization complete");
		
//		List<String> csquarecodes = dbGis.getCSquareCodes(x1, y1, x2, y2, SRID);
		List<String> csquarecodes = dbGis.getAllInfo(x1, y1, x2, y2, SRID,tablename,biodiversityfield);
		int numbOfCodes = csquarecodes.size();
		//if we found squares
		if (numbOfCodes>0){
//			Map<String,Double> biodiversityvalues = dbGis.getBioDiversity(tablename, biodiversityfield, csquarecodes);
			Map<String,Double> biodiversityvalues = dbGis.getCalculatedBioValues();
			if (biodiversityvalues.size()>0){
				Map<String,String> longlat = dbaqua.getLongLatBioDiversity(csquarecodes,null);
				
				MapMerger<String,Double> mm = new MapMerger<String, Double>();
				mm.mergeMaps(longlat, biodiversityvalues);
				
				ArrayList<String> longlatarray = mm.extractFirstVector();
				ArrayList<Double> biodivvalues = mm.extractSecondVector();
				
				GraphSamplesTable gts = null;
				GraphData grd = null;
				// if there are too many samples, perform downsampling
				
			if (numbOfCodes>maxelements) {
				AnalysisLogger.getLogger().trace("Resampling");
				// setup the graph samples table to perform mining processing
				gts = new GraphSamplesTable("Biodiversity Transect",longlatarray,biodivvalues,false);
				
				
				// generate an Example Set for Rapid Miner
				ExampleSet es = gts.generateExampleSet();
				
				// setup Absolute Sampling operator
				AbsoluteSampling asop = (AbsoluteSampling) OperatorService.createOperator("AbsoluteSampling");
				asop.setParameter("sample_size", "" + maxelements);
				asop.setParameter("local_random_seed", "-1");

				// apply Sampling
				es = asop.apply(es);
				// generate a new graph samples table
				gts = new GraphSamplesTable();
				gts.generateSampleTable(es);
				
				List<Point<? extends Number, ? extends Number>> singlegraph = GraphConverter2D.reorder(gts.getGraph());
				
				grd = new GraphData(singlegraph, true);
			}
			else{
				gts = new GraphSamplesTable("Biodiversity Transect",longlatarray,biodivvalues,true);
				grd = new GraphData(gts.getGraph(), false);
			}
			
			GraphGroups graphgroups = new GraphGroups();
			graphgroups.addGraph("Distribution for "+tablename, grd);
			
			//graphgroups = StatisticsGenerator.anotateStationaryPoints(graphgroups);
			
			List<Point> pl = GraphConverter2D.getStationaryPoints(grd);
			
			List<String> lables = GraphConverter2D.getLablesFromPoints(pl.get(0));
			
			List<String> anotations = dbaqua.getAreaAnotations(lables); 
			
			GraphConverter2D.anotateStationaryPoints(graphgroups,anotations);
			
			TransectLineGraph series = new TransectLineGraph("");
			series.renderGraphGroup(graphgroups);
			}
			else
				AnalysisLogger.getLogger().info("Empty Set Found");
		}
		
	}

}
