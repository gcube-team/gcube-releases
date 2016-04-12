package org.gcube.dataanalysis.geo.algorithms;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.ActorNode;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.gcube.dataanalysis.executor.job.management.QueueJobManager;
import org.gcube.dataanalysis.geo.connectors.netcdf.NetCDFDataExplorer;
import org.gcube.dataanalysis.geo.infrastructure.GeoNetworkInspector;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;
import org.opengis.metadata.Metadata;

import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;

import com.thoughtworks.xstream.XStream;

public class MapsComparatorNode extends ActorNode{

	static String layer1 = "Layer_1";
	static String layer2 = "Layer_2";
	static String resolutionS = "resolution";
	static String x1S = "x1";
	static String y1S = "y1";
	static String x2S = "x2";
	static String y2S = "y2";
	
	static String zString = "Z";
	static String t1 = "TimeIndex_1";
	static String t2 = "TimeIndex_2";
	static String valuesThr = "ValuesComparisonThreshold";
	float status = 0;
	AlgorithmConfiguration config;
	Tuple<Double> extent;
	public int prevbroadcastTimePeriod;
	public int prevmaxNumberOfStages;
	public int prevmaxMessages;
	int numberofslices = 0;
	
	public List<StatisticalType> inputs = new ArrayList<StatisticalType>();
	public LinkedHashMap<String, String> outputParameters = new LinkedHashMap<String, String>();
	
	@Override
	public String getDescription() {
		return "An algorithm for comparing two OGC/NetCDF maps in seamless way to the user. Supported maps can only be in WFS, Opendap or ASC formats. The algorithm assesses the similarities between two geospatial maps by comparing them in a point-to-point fashion. It accepts as input the two geospatial maps (via their UUIDs in the infrastructure spatial data repository - recoverable through the Geoexplorer portlet) and some parameters affecting the comparison such as the z-index, the time index, the comparison threshold.";
	}

	@Override
	public List<StatisticalType> getInputParameters(){

		IOHelper.addStringInput(inputs,layer1, "First Layer Title or UUID: The title or the UUID (preferred) of a layer indexed in the e-Infrastructure on GeoNetwork - You can retrieve it from GeoExplorer", "");//"Sarda australis");
		IOHelper.addStringInput(inputs, layer2, "Second Layer Title or UUID: The title or the UUID (preferred)  of a second layer indexed in the e-Infrastructure on GeoNetwork - You can retrieve it from GeoExplorer", "");//"Sarda orientalis");
		IOHelper.addIntegerInput(inputs, zString, "value of Z. Default is 0, that means comparison will be at surface level", "0");
		IOHelper.addDoubleInput(inputs, valuesThr, "A comparison threshold for the values in the map. Null equals to 0.1", "0.1");
		IOHelper.addIntegerInput(inputs, t1, "First Layer Time Index. The default is the first", "0");
		IOHelper.addIntegerInput(inputs, t2, "Second Layer Time Index. The default is the first", "0");
		IOHelper.addDoubleInput(inputs, "KThreshold", "Threshold for K-Statistic: over this threshold values will be considered 1 for agreement calculation. Default is 0.5","0.5");
		
		DatabaseType.addDefaultDBPars(inputs);
		
		return inputs;
	}

		@Override
	public StatisticalType getOutput() {
		
		// set the output map containing values
		AnalysisLogger.getLogger().debug("MapsComparator: Producing Gaussian Distribution for the errors");
		
		// build image:
		HashMap<String, Image> producedImages = new HashMap<String, Image>();
		double mean = Double.parseDouble(outputParameters.get("MEAN"));
		double variance = Double.parseDouble(outputParameters.get("VARIANCE"));
		PrimitiveType images = new PrimitiveType("Images", producedImages, PrimitiveTypes.IMAGES, "Distribution of the Error", "The distribution of the error along with variance");

		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
		
		for (String key:outputParameters.keySet()){
			String value = outputParameters.get(key);
			PrimitiveType val = new PrimitiveType(String.class.getName(), "" + value, PrimitiveTypes.STRING, key, key);
			map.put(key, val);	
		}

		// collect all the outputs
		map.put("Images", images);

		// generate a primitive type for the collection
		PrimitiveType output = new PrimitiveType(HashMap.class.getName(), map, PrimitiveTypes.MAP, "ResultsMap", "Results Map");

		return output;
	}

	@Override
	public ALG_PROPS[] getProperties() {
			ALG_PROPS[] p = { ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON };
			return p;
	}


	@Override
	public String getName() {
		return "MAPS_COMPARISON";
	}


	@Override
	public void initSingleNode(AlgorithmConfiguration config) {
		
	}


	@Override
	public float getInternalStatus() {
		
		return status;
	}


	
	private double calcResolution(String layerT1, String layerT2) throws Exception{
		
		String scope = config.getGcubeScope();
		if (scope == null){
			scope = ScopeProvider.instance.get();
			config.setGcubeScope(scope);
		}
		MatrixExtractor intersector = new MatrixExtractor(config);
		AnalysisLogger.getLogger().debug("MapsComparator: GeoIntersector initialized");
		
		double x1 = -180;
		double x2 = 180;
		double y1 = -90;
		double y2 = 90;
		
		status = 10;
		GeoNetworkInspector fm = intersector.getFeaturer();
		AnalysisLogger.getLogger().debug("MapsComparator: Taking info for the layer: " + layerT1);
		Metadata meta1 = fm.getGNInfobyUUIDorName(layerT1);
		if (meta1==null) throw new Exception("No Correspondence with Layer 1");
		double resolution1 = 0;
		try {
			resolution1 = GeoNetworkInspector.getResolution(meta1);
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("MapsComparator: Undefined resolution");
		}
		AnalysisLogger.getLogger().debug("MapsComparator: Resolution: " + resolution1);
		
		if (fm.isNetCDFFile(meta1)){
			AnalysisLogger.getLogger().debug("MapsComparator: recalculating the spatial extent of the comparison");
			String fileurl = fm.getOpenDapLink(meta1);
			GridDataset gds = ucar.nc2.dt.grid.GridDataset.open(fileurl);
			List<GridDatatype> gridTypes = gds.getGrids();
			GridDatatype gdt = gridTypes.get(0);
			x1 = NetCDFDataExplorer.getMinX(gdt.getCoordinateSystem());
			x2 = NetCDFDataExplorer.getMaxX(gdt.getCoordinateSystem());
			y1 = NetCDFDataExplorer.getMinY(gdt.getCoordinateSystem());
			y2 = NetCDFDataExplorer.getMaxY(gdt.getCoordinateSystem());
		}
		
		AnalysisLogger.getLogger().debug("MapsComparator: Spatial extent of the comparison: x1: "+x1+" x2: "+x2+" y1: "+y1+" y2: "+y2);
		
		AnalysisLogger.getLogger().debug("MapsComparator: Taking info for the layer: " + layerT2);
		AnalysisLogger.getLogger().debug("MapsComparator: Trying with UUID..." + layerT2);
		Metadata meta2 = fm.getGNInfobyUUIDorName(layerT2);
		if (meta2==null) throw new Exception("No Correspondence with Layer 2");
		
		double resolution2 = 0;
		try {
			resolution2 = GeoNetworkInspector.getResolution(meta2);
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("MapsComparator: Undefined resolution");
		}
		AnalysisLogger.getLogger().debug("MapsComparator: Resolution: " + resolution2);
		// take the lowest resolution to perform the comparison
		double resolution = Math.max(resolution1, resolution2);
		AnalysisLogger.getLogger().debug("MapsComparator: Theoretical Resolution: " + resolution);
		if (resolution == 0)
			resolution = 0.5d;
		// I added the following control to limit the amount of calculations 
		if (resolution<0.5 && resolution>0.01)
			resolution = 0.5d;
		else if (resolution<0.01)
			resolution = 0.01d;
		
		AnalysisLogger.getLogger().debug("MapsComparator: Evaluation Indeed at Resolution: " + resolution);
		extent = new Tuple<Double>(x1,y1,x2,y2);
		return resolution;
	}
	
	
	
	@Override
	public int executeNode(int leftStartIndex, int numberOfLeftElementsToProcess, int rightStartIndex, int numberOfRightElementsToProcess, boolean duplicate, String sandboxFolder, String nodeConfigurationFileObject, String logfileNameToProduce) {
		try {
		status = 0;
		System.out.println("Restoring configuration");
		AlgorithmConfiguration config = Transformations.restoreConfig(new File(sandboxFolder, nodeConfigurationFileObject).getAbsolutePath());
		config.setConfigPath(sandboxFolder);
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		long t0 = System.currentTimeMillis();
		String layerT1 = IOHelper.getInputParameter(config,layer1);
		String layerT2 = IOHelper.getInputParameter(config,layer2);
		String z$ = IOHelper.getInputParameter(config,zString);
		String valuesthr$ = IOHelper.getInputParameter(config,valuesThr);
		String time1$ = IOHelper.getInputParameter(config,t1);
		String time2$ = IOHelper.getInputParameter(config,t2);
		int time1 = ((time1$ != null) && (time1$.trim().length() > 0)) ? Integer.parseInt(time1$) : 0;
		int time2 = ((time2$ != null) && (time2$.trim().length() > 0)) ? Integer.parseInt(time2$) : 0;

		double resolution = Double.parseDouble(IOHelper.getInputParameter(config,resolutionS));
		double x1 = Double.parseDouble(IOHelper.getInputParameter(config,x1S));
		double y1 = Double.parseDouble(IOHelper.getInputParameter(config,y1S));
		double x2 = Double.parseDouble(IOHelper.getInputParameter(config,x2S));
		double y2 = Double.parseDouble(IOHelper.getInputParameter(config,y2S));
		
		if (time1 < 0)
			time1 = 0;

		if (time2 < 0)
			time2 = 0;

		double valuesthreshold = 0.1;
		if ((valuesthr$ != null) && (valuesthr$.trim().length() > 0))
			try {
				valuesthreshold = Double.parseDouble(valuesthr$);
			} catch (Exception ee) {
			}

		double z = 0;
		if ((z$ != null) && (z$.trim().length() > 0))
			try {
				z = Double.parseDouble(z$);
			} catch (Exception ee) {
			}
		
			String scope = config.getGcubeScope();
			double slicey1 = y1+(((float)rightStartIndex)*resolution);
			double slicey2 = slicey1;
			if (IOHelper.getInputParameter(config,"full")!=null){
				slicey1=y1;
				slicey2=y2;
			}
				
			int positivescount = 0;
			if (slicey1<=y2){
			XYExtractor intersector = new XYExtractor(config);
			AnalysisLogger.getLogger().debug("MapsComparator: GeoIntersector initialized");
			AnalysisLogger.getLogger().debug("MapsComparator: ****Rasterizing map 1 in the range: ("+x1+" , "+slicey1+"; "+x2+" , "+slicey2+") with res "+resolution);
			double[][] slice1 = intersector.extractXYGrid(layerT1, time1, x1, x2, slicey1, slicey2, z, resolution, resolution);
			AnalysisLogger.getLogger().debug("MapsComparator: ****Rasterizing map 2 in the range: ("+x1+" , "+slicey1+"; "+x2+" , "+slicey2+") with res "+resolution);
			double[][] slice2 = intersector.extractXYGrid(layerT2, time1, x1, x2, slicey1, slicey2, z, resolution, resolution);
			int xsize = slice1[0].length;
			int ysize = slice1.length;
			
			AnalysisLogger.getLogger().debug("Comparing maps...");
			for (int j=0;j<ysize;j++){
				for (int i=0;i<xsize;i++){
					double discrepancy = Math.abs(slice1[j][i]-slice2[j][i]);
					if (discrepancy<valuesthreshold)
						positivescount++;
				}
			}
			}else
				AnalysisLogger.getLogger().debug("MapsComparator: warning - Y out of range : "+slicey1+" max:"+y2);
			
			elapsedt += (System.currentTimeMillis()-t0);
			
			AnalysisLogger.getLogger().debug("MapsComparator: Finished: " + positivescount+" in "+(System.currentTimeMillis()-t0));
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("MapsComparator: ERROR!: " + e.getLocalizedMessage());
		} finally {
			status = 1;
		}
		return 0;
	}

	static long elapsedt = 0;
	@Override
	public void setup(AlgorithmConfiguration config) throws Exception {
		
		//calc resolution
		this.config = config;
		String layerT1 = IOHelper.getInputParameter(config,layer1);
		String layerT2 = IOHelper.getInputParameter(config,layer2);
		double resolution = calcResolution(layerT1, layerT2);
		
		config.setParam(resolutionS,""+resolution);
		config.setParam(x1S,""+extent.getElements().get(0));
		config.setParam(y1S,""+extent.getElements().get(1));
		config.setParam(x2S,""+extent.getElements().get(2));
		config.setParam(y2S,""+extent.getElements().get(3));
		double ymax  = extent.getElements().get(3);
		double ymin  = extent.getElements().get(1);
		int numberofslices = (int) Math.round(((ymax-ymin)/resolution)+1);
		
		prevmaxMessages=D4ScienceDistributedProcessing.maxMessagesAllowedPerJob;
		D4ScienceDistributedProcessing.maxMessagesAllowedPerJob=1;
		prevbroadcastTimePeriod = QueueJobManager.broadcastTimePeriod;
		QueueJobManager.broadcastTimePeriod=4*3600000;
		prevmaxNumberOfStages = QueueJobManager.maxNumberOfStages;
		QueueJobManager.maxNumberOfStages=10000;
		
		AnalysisLogger.getLogger().info("Destination Table Created! Addressing " + numberofslices + " slices");
	}


	@Override
	public int getNumberOfRightElements() {
		return numberofslices;
	}


	@Override
	public int getNumberOfLeftElements() {
		return numberofslices;
	}


	@Override
	public void stop() {
		AnalysisLogger.getLogger().debug("Shutdown");
	}


	@Override
	public void postProcess(boolean manageDuplicates, boolean manageFault) {
		
	}

	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		String sandbox = "./PARALLEL_PROCESSING";
		String configfile = "testconfig.cfg";
		config.setPersistencePath(sandbox);
		
		config.setParam(zString,"0");
		config.setParam(valuesThr,"0.1");
		config.setParam(t1,"0");
		config.setParam(t2,"0");
		config.setParam("KThreshold","0.5");
		config.setGcubeScope("/gcube");
		
		//config.setParam(layer1,"b040894b-c5db-47fc-ba9c-d4fafcdcf620"); //goblin shark
		//config.setParam(layer2,"c9a31223-cc00-4acd-bc5b-a0c76a7f79c7"); //humbolt squid
		
		config.setParam(layer1,"1265fce4-f331-4459-bed5-3747039c7bd9"); 
		config.setParam(layer2,"1265fce4-f331-4459-bed5-3747039c7bd9");
		
		config.setParam("full","true"); //full comparison
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		
		new MapsComparatorNode().setup(config);
		
		BufferedWriter oos = new BufferedWriter(new FileWriter(new File(sandbox, configfile)));
		oos.write(new XStream().toXML(config));
		oos.close();
		int ncomp = 1;
		int nslices = 361;
		for (int i=0;i<ncomp;i++){
			int randi = (int)(Math.random()*(float)nslices);
			System.out.println("->Comparing for index "+randi);
			new MapsComparatorNode().executeNode(0, 0, randi, randi, false, sandbox, configfile, "test.log");
		}
		float el = (float)(elapsedt/(float)ncomp);
		System.out.println("Mean time:" +el);
		System.out.println("Mean time on 21 nodes:" +(el*(float)nslices/21f));
	}
	
}
