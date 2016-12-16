package org.gcube.dataanalysis.geo.test;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.MathFunctions;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.asc.ASC;
import org.gcube.dataanalysis.geo.connectors.asc.AscDataExplorer;
import org.gcube.dataanalysis.geo.connectors.asc.AscRaster;
import org.gcube.dataanalysis.geo.connectors.asc.AscRasterWriter;
import org.gcube.dataanalysis.geo.matrixmodel.ASCConverter;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;

public class TestPointslice {

	static String cfg = "./cfg/";
	public static void main1(String[] args) throws Exception{
		String  layertitle = "Statistical Mean in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Temperature - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/temperature_annual_1deg_ENVIRONMENT_OCEANS_.nc}";
//		String  layertitle = "Mass Concentration of Chlorophyll in Sea Water in [03-30-13 01:00] (3D) {Mercator Ocean BIOMER1V1R1: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-analysis-bio-001-008-a}";
//		String  layertitle = "Objectively Analyzed Climatology in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Temperature - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/temperature_annual_1deg_ENVIRONMENT_OCEANS_.nc}";
//		String  layertitle = "be24800d-7583-4efa-b925-e0d8760e0fd3";
		
		long t0 = System.currentTimeMillis();
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setGcubeScope("/gcube");
		config.setConfigPath(cfg);
		XYExtractor  intersector = new XYExtractor (config);
//		intersector.takeTimeSlice(layertitle, 0, -180, 180, -10, 10, 0, 1, 1);
//		intersector.takeTimeSlice(layertitle, 0, -10, 10, -10, 10, 0,1, 1);
//		intersector.takeTimeInstantMatrix(layertitle, 0, -180, 180, -90, 90, 0, 0.5, 0.5);
		double output[][] = intersector.extractXYGrid(layertitle, 0, 0,0,0,0, 0, 0.5, 0.5);
		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
		System.out.println("Output: "+output[0][0]);
	}
	
	
	public static void main2(String[] args) throws Exception{
		
		List<Tuple<Double>> tuples = new ArrayList<Tuple<Double>>();
		for (int j=0;j<100;j++){
			double randomx = ((180) * Math.random()) -180;
			double randomy = ((90) * Math.random()) -90;
			tuples.add(new Tuple<Double>(randomx,randomy,0d));
		}
		
		
		AscDataExplorer ade1 = new AscDataExplorer("./maxentfd4c59b3-2c65-4c4e-a235-84093d58230d/layer1.asc");
//		AscDataExplorer ade1 = new AscDataExplorer("./maxenttestfolder/nitrate.asc");
		
		List<Double>features = ade1.retrieveDataFromAsc(tuples,0);
		
		AscDataExplorer ade2 = new AscDataExplorer("./maxentCompleteLayers/layer1.asc");
		List<Double>features2 = ade2.retrieveDataFromAsc(tuples,0);
		
		for (int i=0;i<features.size();i++){
			if (features.get(i)-features2.get(i)!=0)
				if	((features.get(i).isNaN() && !features2.get(i).isNaN()) || 	(!features.get(i).isNaN() && features2.get(i).isNaN()))
					System.out.println(tuples.get(i)+":"+features.get(i)+" vs "+features2.get(i)+" - "+(features.get(i)-features2.get(i)));
			
			
		}
		
		System.out.println("Finished");
	}
	
public static void main3(String[] args) throws Exception{
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setGcubeScope("/gcube/devsec/devVRE");
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		XYExtractor extractor = new XYExtractor(config);
		double x1 = -60d;
		double x2 = 60d;
		double y1 = -10d;
		double y2 = 10d;
		
		double[][] values = extractor.extractXYGrid("dfd1bad2-ab00-42ac-8bb2-46a17162f509", 0, x1,x2,y1,y2,0d, 0.08333333, 0.08333333);
		
		List<Double> currentTimeValues = extractor.currentTimeValues;
		
		AscRasterWriter writer = new AscRasterWriter();
		writer.writeRasterInvertYAxis("testwritten.asc", new AscRaster(values, 0.08333333, -1, -1, x1, y1));
		
		AscDataExplorer ade2 = new AscDataExplorer("./maxentCompleteLayers/layer1.asc");
		List<Double>features2 = ade2.retrieveDataFromAsc(extractor.currentTuples,0);
		int g = 0;
		int k = 0;
		
		for (int i=0;i<currentTimeValues.size();i++){
			System.out.println("1-"+extractor.currentTuples.get(i)+":"+currentTimeValues.get(i)+" vs "+features2.get(i)+" - "+(currentTimeValues.get(i)-features2.get(i)));
			System.out.println("2-"+extractor.currentTuples.get(i)+":"+values[k][g]+" vs "+currentTimeValues.get(i)+" - "+(values[k][g]-currentTimeValues.get(i)));

			g++;
			if (g>=values[0].length){
				g =0;
				k++;
			}
		}
	
		
		
		System.out.println("Finished");
	}
	

public static void main4(String[] args) throws Exception{
	
	AlgorithmConfiguration config = new AlgorithmConfiguration();
	config.setConfigPath("./cfg/");
	config.setPersistencePath("./");
	config.setGcubeScope("/gcube/devsec/devVRE");
	AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
	XYExtractor extractor = new XYExtractor(config);
	double x1 = -60d;
	double x2 = 60d;
	double y1 = -10d;
	double y2 = 10d;
	
	double[][] values = extractor.extractXYGrid("dfd1bad2-ab00-42ac-8bb2-46a17162f509", 0, x1,x2,y1,y2,0d, 0.08333333, 0.08333333);
	List<Double> currentTimeValues = extractor.currentTimeValues;
	
	AscDataExplorer ade1 = new AscDataExplorer("./testwritten.asc");
	List<Double>features1 = ade1.retrieveDataFromAsc(extractor.currentTuples,0);
	
	AscDataExplorer ade2 = new AscDataExplorer("./maxentCompleteLayers/layer1.asc");
	List<Double>features2 = ade2.retrieveDataFromAsc(extractor.currentTuples,0);
	
	for (int i=0;i<currentTimeValues.size();i++){
		System.out.println("1-"+extractor.currentTuples.get(i)+":"+features1.get(i)+" vs "+features2.get(i)+" - "+(features1.get(i)-features2.get(i)));
	}
	
	System.out.println("Finished");
}


public static void main(String[] args) throws Exception{
	
	AlgorithmConfiguration config = new AlgorithmConfiguration();
	config.setConfigPath("./cfg/");
	config.setPersistencePath("./");
	config.setGcubeScope("/gcube/devsec/devVRE");
	AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

	double x1 = -60d;
	double x2 = 60d;
	double y1 = -10d;
	double y2 = 10d;
	
	List<Tuple<Double>> tuples = new ArrayList<Tuple<Double>>();
	for (int j=0;j<100;j++){
		double randomx = ((x1-x2) * Math.random()) +x2;
		double randomy = ((y1-y2) * Math.random()) +y2;
		tuples.add(new Tuple<Double>(randomx,randomy,0d));
	}
	
//	AscDataExplorer ade1 = new AscDataExplorer("./testwritten.asc");
	AscDataExplorer ade1 = new AscDataExplorer("./maxent93db29d5-6a38-4598-9c66-5a814f4a9f36/layer1.asc");
	
	List<Double>features1 = ade1.retrieveDataFromAsc(tuples,0);
	
	//AscDataExplorer ade2 = new AscDataExplorer("./maxentCompleteLayers/layer1.asc");
	AscDataExplorer ade2 = new AscDataExplorer("./maxenttestfolder/nitrate.asc");
	
	List<Double>features2 = ade2.retrieveDataFromAsc(tuples,0);
	
	for (int i=0;i<tuples.size();i++){
		System.out.println("1-"+tuples.get(i)+":"+features1.get(i)+" vs "+features2.get(i)+" - "+(features1.get(i)-features2.get(i)));
	}
	
	System.out.println("Finished");
}

}
