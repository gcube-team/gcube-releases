package org.gcube.portlets.user.transectgenerator.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.data.GraphSamplesTable;
import org.gcube.contentmanagement.graphtools.data.conversions.GraphConverter2D;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
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

public class AquamapsProcessor {

	private static final String LogFile = "ALog.properties";
	private static final String OperatorsFile = "operators.xml";
	DBAquamapsExtractor dbaqua; 
	DBPostGISExtractror dbGis;
	public void init(String cfg,LexicalEngineConfiguration aquamapsConfig, LexicalEngineConfiguration geoConfig){
		
		AnalysisLogger.setLogger(cfg + LogFile);
		System.setProperty("rapidminer.init.operators", cfg + OperatorsFile);

		dbaqua = new DBAquamapsExtractor(cfg,aquamapsConfig);
		dbGis = new DBPostGISExtractror(cfg,geoConfig);
		RapidMiner.init();
		
		AnalysisLogger.getLogger().info("AquamapsProcessor->Initialization Complete");
		
	}
	
	public void shutdown(){
		if (dbaqua!=null && dbaqua.getDbSession()!=null)
			dbaqua.getDbSession().close();
		if (dbGis!=null && dbGis.getDbSession()!=null)
			dbGis.getDbSession().close();
	}
	
	private ArrayList<String> longlatarray;
	private ArrayList<Double> biodivvalues;
	
	public GraphGroups calculateTransect(String x1,String y1,String x2,String y2,String SRID, String biodiversityTable,String biodiversityColumn,int maxElements) throws Exception{
		
		//set the resolution automatically
		int minimumGap = (maxElements/100);
		return calculateTransect(x1, y1, x2, y2, SRID, biodiversityTable, biodiversityColumn, maxElements, minimumGap);
		
	}
	public GraphGroups calculateTransect(String x1,String y1,String x2,String y2,String SRID, String biodiversityTable,String biodiversityColumn,int maxElements,int minimumGap) throws Exception{
		
		GraphGroups graphgroups = new GraphGroups();
		
		List<String> csquarecodes = dbGis.getAllInfo(x1, y1, x2, y2, SRID,biodiversityTable,biodiversityColumn);
		int numbOfCodes = csquarecodes.size();
		//if we found squares
		if (numbOfCodes>0){
//			Map<String,Double> biodiversityvalues = dbGis.getBioDiversity(tablename, biodiversityfield, csquarecodes);
			Map<String,Double> biodiversityvalues = dbGis.getCalculatedBioValues();
			if (biodiversityvalues.size()>0){
//				Map<String,String> longlat = dbaqua.getLongLatBioDiversity(csquarecodes,dbGis.countryNames);
				Map<String,String> longlat = dbaqua.getLongLatBioDiversity(csquarecodes,null);
				MapMerger<String,Double> mm = new MapMerger<String, Double>();
				mm.mergeMaps(longlat, biodiversityvalues);
				
				ArrayList<String> longlatarray = mm.extractFirstVector();
				ArrayList<Double> biodivvalues = mm.extractSecondVector();
				
				GraphSamplesTable gts = null;
				GraphData grd = null;
				// if there are too many samples, perform downsampling
				
			if (numbOfCodes>maxElements) {
				AnalysisLogger.getLogger().trace("AquamapsProcessor->Resampling");
				// setup the graph samples table to perform mining processing
				gts = new GraphSamplesTable("Biodiversity Transect",longlatarray,biodivvalues,false);
				
				
				// generate an Example Set for Rapid Miner
				ExampleSet es = gts.generateExampleSet();
				
				// setup Absolute Sampling operator
				AbsoluteSampling asop = (AbsoluteSampling) OperatorService.createOperator("AbsoluteSampling");
				asop.setParameter("sample_size", "" + maxElements);
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
			
			
			
			graphgroups.addGraph("Distribution for "+biodiversityTable, grd);
			//graphgroups = StatisticsGenerator.anotateStationaryPoints(graphgroups);
			
			//get stationary points
			List<Point> pl = GraphConverter2D.getStationaryPoints(grd);
			//get the lables of these points
			List<String> lables = GraphConverter2D.getLablesFromPoints(pl.get(0));
			//get the annotations for the above labels
			List<String> anotations = dbaqua.getAreaAnotations(lables);
			//anotate stationary points
			GraphConverter2D.anotateStationaryPoints(graphgroups,anotations);

			
			
			//sample labels on the basis of the previouslabel
			GraphConverter2D.sampleAnotationBySameFollower(grd.getData());
			
			//get zero points
			List<Integer> zerosIndexes = MathFunctions.findZeros(MathFunctions.points2Double(grd.getData(), 0, grd.getData().get(0).getEntries().size()));
			//get the labels for these points
			List<String> zeroslabels = GraphConverter2D.getLabelsfromIndexes(grd.getData(), zerosIndexes);
			//get the anotations for zeros labels
			List<String> zerosanotations = dbaqua.getAreaAnotations(zeroslabels);
			//anotate the graph with these labels
			GraphConverter2D.anotatePoints(graphgroups, zerosIndexes, zerosanotations);

			//sample labels on the basis of a gap between labels
			GraphConverter2D.sampleAnotationByRange(grd.getData(),minimumGap);
			
			}
			else
				AnalysisLogger.getLogger().info("AquamapsProcessor->Empty Set Found for Biodiversity");
		}
		else
			AnalysisLogger.getLogger().info("AquamapsProcessor->Empty Set Found for Cells");
		
		return graphgroups;
	}
	
}
