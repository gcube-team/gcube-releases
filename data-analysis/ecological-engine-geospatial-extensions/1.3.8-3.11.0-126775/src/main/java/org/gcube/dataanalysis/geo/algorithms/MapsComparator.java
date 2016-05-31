package org.gcube.dataanalysis.geo.algorithms;

import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.graphtools.plotting.graphs.GaussianDistributionGraph;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.evaluation.DiscrepancyAnalysis;
import org.gcube.dataanalysis.ecoengine.interfaces.DataAnalysis;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.gcube.dataanalysis.geo.connectors.netcdf.NetCDFDataExplorer;
import org.gcube.dataanalysis.geo.connectors.wfs.WFS;
import org.gcube.dataanalysis.geo.infrastructure.GeoNetworkInspector;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;
import org.hibernate.SessionFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeriesCollection;
import org.opengis.metadata.Metadata;

import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;

public class MapsComparator extends DataAnalysis {

	static String layer1 = "Layer_1";
	static String layer2 = "Layer_2";
	static String zString = "Z";
	static String t1 = "TimeIndex_1";
	static String t2 = "TimeIndex_2";
	static String valuesThr = "ValuesComparisonThreshold";
	float status;
	
	public List<StatisticalType> inputs = new ArrayList<StatisticalType>();
	public LinkedHashMap<String, String> outputParameters = new LinkedHashMap<String, String>();
	
	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("Maps Comparator Initialization");
		
	}

	@Override
	public String getDescription() {
		return "An algorithm for comparing two OGC/NetCDF maps in seamless way to the user. The algorithm assesses the similarities between two geospatial maps by comparing them in a point-to-point fashion. It accepts as input the two geospatial maps (via their UUIDs in the infrastructure spatial data repository - recoverable through the Geoexplorer portlet) and some parameters affecting the comparison such as the z-index, the time index, the comparison threshold. Note: in the case of WFS layers it makes comparisons on the last feature column.";
	}

	public double BBxLL = -180;
	public double BBxUR = 180;
	public double BByLL = -90;
	public double BByUR = 90;
	
	@Override
	public void compute() throws Exception{
		status = 0;
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		long t0 = System.currentTimeMillis();
		String layerT1 = IOHelper.getInputParameter(config,layer1);
		String layerT2 = IOHelper.getInputParameter(config,layer2);
		
		if (layerT1==null || layerT1.trim().length()==0 || layerT2==null || layerT2.trim().length()==0)
			throw new Exception("Error please provide information for the input layers");
			
		String z$ = IOHelper.getInputParameter(config,zString);
		String valuesthr$ = IOHelper.getInputParameter(config,valuesThr);
		String time1$ = IOHelper.getInputParameter(config,t1);
		String time2$ = IOHelper.getInputParameter(config,t2);
		int time1 = ((time1$ != null) && (time1$.trim().length() > 0)) ? Integer.parseInt(time1$) : 0;
		int time2 = ((time2$ != null) && (time2$.trim().length() > 0)) ? Integer.parseInt(time2$) : 0;

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
		try {

			// delete this force
			String scope = config.getGcubeScope();
			AnalysisLogger.getLogger().debug("MapsComparator: Externally set scope "+scope);
			if (scope == null){
				scope = ScopeProvider.instance.get();
				config.setGcubeScope(scope);
			}
//			scope = "/gcube";
//			String scope = null;
			AnalysisLogger.getLogger().debug("MapsComparator: Using Scope: " + scope + " Z: " + z + " Values Threshold: " + valuesthreshold + " Layer1: " + layerT1 + " vs " + layerT2);
			XYExtractor  intersector = new XYExtractor (config);
			AnalysisLogger.getLogger().debug("MapsComparator: MatrixExtractor initialized");
			
			status = 10;
			
			double resolution = getBestComparisonResolution(intersector, layerT1, layerT2);
			
			AnalysisLogger.getLogger().debug("MapsComparator: Evaluation Indeed at Resolution: " + resolution);

			AnalysisLogger.getLogger().debug("MapsComparator: ****Rasterizing map 1****");
			double[][] slice1 = intersector.extractXYGrid(layerT1, time1, BBxLL, BBxUR, BByLL, BByUR, z, resolution, resolution);
			HashMap<Double,Map<String, String>> polygonsFeatures = null;
			if (intersector.currentconnector instanceof WFS)
				polygonsFeatures = ((WFS) intersector.currentconnector).getPolygonsFeatures();
			
			AnalysisLogger.getLogger().debug("MapsComparator: Dumping map 1");
			status = 30;
			RasterTable raster1 = new RasterTable(BBxLL, BBxUR, BByLL, BByUR, z, resolution, resolution, slice1, polygonsFeatures,config);
			raster1.dumpGeoTable();
			String rastertable1 = raster1.getTablename();
			AnalysisLogger.getLogger().debug("MapsComparator: Map 1 was dumped in table: " + rastertable1);
			status = 40;
			String columnToCompare1 = assessComparisonColumn(intersector, raster1);
			intersector = new XYExtractor (config);
			AnalysisLogger.getLogger().debug("MapsComparator: ****Rasterizing map 2****");
			double[][] slice2 = intersector.extractXYGrid(layerT2, time2, BBxLL, BBxUR, BByLL, BByUR, z, resolution, resolution);
			polygonsFeatures = null;
			if (intersector.currentconnector instanceof WFS)
				polygonsFeatures = ((WFS) intersector.currentconnector).getPolygonsFeatures();
			AnalysisLogger.getLogger().debug("MapsComparator: Dumping map 2");
			status = 50;
			RasterTable raster2 = new RasterTable(BBxLL, BBxUR, BByLL, BByUR, z, resolution, resolution, slice2, polygonsFeatures,config);
			raster2.dumpGeoTable();
			String rastertable2 = raster2.getTablename();
			AnalysisLogger.getLogger().debug("MapsComparator: Map 2 was dumped in table: " + rastertable2);
			status = 60;
			String columnToCompare2 = assessComparisonColumn(intersector, raster2);
			
			AnalysisLogger.getLogger().debug("MapsComparator: Comparing on the following features : " + columnToCompare1+" vs "+columnToCompare2);
						
			config.setNumberOfResources(1);
			config.setParam("FirstTable", rastertable1);
			config.setParam("SecondTable", rastertable2);
			config.setParam("FirstTableCsquareColumn", RasterTable.csquareColumn);
			config.setParam("SecondTableCsquareColumn", RasterTable.csquareColumn);
			config.setParam("FirstTableProbabilityColumn", columnToCompare1);
			config.setParam("SecondTableProbabilityColumn", columnToCompare2);
			config.setParam("ComparisonThreshold", "" + valuesthreshold);
			AnalysisLogger.getLogger().debug("MapsComparator: Analyzing discrepancy between maps: " + rastertable1 + " and " + rastertable2);
			DiscrepancyAnalysis da = new DiscrepancyAnalysis();
			da.setConfiguration(config);
			da.init(false);
			outputParameters = da.analyze();
			outputParameters.put("RESOLUTION", "" + MathFunctions.roundDecimal(resolution,4));
			status = 80;
			AnalysisLogger.getLogger().debug("MapsComparator: Output: " + outputParameters);

			// delete the tables

			connection = DatabaseUtils.initDBSession(config);
			AnalysisLogger.getLogger().debug("MapsComparator: Deleting table " + rastertable1);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(rastertable1), connection);
			status = 90;
			AnalysisLogger.getLogger().debug("MapsComparator: Deleting table " + rastertable2);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(rastertable2), connection);
			AnalysisLogger.getLogger().debug("MapsComparator: Elapsed: Whole operation completed in " + ((double) (System.currentTimeMillis() - t0) / 1000d) + "s");
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("MapsComparator: ERROR!: " + e.getLocalizedMessage());
		} finally {
			DatabaseUtils.closeDBConnection(connection);
			status = 100;
			
		}

	}

	private String assessComparisonColumn(XYExtractor intersector, RasterTable raster) throws Exception{
		
		String columnToCompare = RasterTable.valuesColumn;
		try{
		if (intersector.currentconnector instanceof WFS){
			String[] columns = RasterTable.propertiesMapToColumnString(raster.valuesPropertiesMap.values().iterator().next(),false).split(",");
			//take the last value as comparison column!
			columnToCompare = columns[columns.length-1];
			AnalysisLogger.getLogger().debug("Mapscomparator: Preparing column "+columnToCompare);
			org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory.executeUpdateNoTransaction("ALTER TABLE "+raster.getTablename()+" ALTER COLUMN "+columnToCompare+" TYPE real USING "+columnToCompare+"::real", 
					config.getDatabaseDriver(),config.getDatabaseUserName(),config.getDatabasePassword(),config.getDatabaseURL(), true);
		}
		}catch(Exception e){
			throw new Exception ("Cannot compare the maps: the column "+columnToCompare+" is not real valued");
		}
		finally{
		}
		return columnToCompare;
	}
	
	public double getBestComparisonResolution(MatrixExtractor intersector, String layerID1, String layerID2) throws Exception{
		
		GeoNetworkInspector fm = intersector.getFeaturer();
		AnalysisLogger.getLogger().debug("MapsComparator: Taking info for the layer: " + layerID1);
		Metadata meta1 = fm.getGNInfobyUUIDorName(layerID1);
		//patch to manage also external links
		if (meta1==null) {return 0.5;}
		//{throw new Exception("No Correspondence with Layer 1");}
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
			BBxLL = NetCDFDataExplorer.getMinX(gdt.getCoordinateSystem());
			BBxUR = NetCDFDataExplorer.getMaxX(gdt.getCoordinateSystem());
			BByLL = NetCDFDataExplorer.getMinY(gdt.getCoordinateSystem());
			BByUR = NetCDFDataExplorer.getMaxY(gdt.getCoordinateSystem());
		}
		
		AnalysisLogger.getLogger().debug("MapsComparator: Spatial extent of the comparison: x1: "+BBxLL+" x2: "+BBxUR+" y1: "+BByLL+" y2: "+BByUR);
		
		AnalysisLogger.getLogger().debug("MapsComparator: Taking info for the layer: " + layerID2);
		AnalysisLogger.getLogger().debug("MapsComparator: Trying with UUID..." + layerID2);
		Metadata meta2 = fm.getGNInfobyUUIDorName(layerID2);
		if (meta2==null) {return 0.5;}//{throw new Exception("No Correspondence with Layer 2");}
		
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
	
		return resolution;
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
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shutdown");

	}

	protected Image generateGaussian(double mean, double variance){
		// gaussian
				XYSeriesCollection xyseriescollection = new XYSeriesCollection();
				if (variance == 0)
					variance = 0.01;
				AnalysisLogger.getLogger().debug("MapsComparator: Adopting mean:" + mean + " and variance:" + variance);
				NormalDistributionFunction2D normaldistributionfunction2d = new NormalDistributionFunction2D(mean, variance);
				org.jfree.data.xy.XYSeries xyseries = DatasetUtilities.sampleFunction2DToSeries(normaldistributionfunction2d, (mean - (2 * variance)), (mean + (2 * variance)), 121, "Distribution of the Error");
				xyseriescollection.addSeries(xyseries);
				// end gaussian
				
				JFreeChart chart = GaussianDistributionGraph.createStaticChart(xyseriescollection, mean, variance);
				Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));
				/*
				 * GaussianDistributionGraph graph = new GaussianDistributionGraph("Error Distribution"); graph.mean=mean;graph.variance=variance; graph.render(xyseriescollection);
				 */
				// end build image
				AnalysisLogger.getLogger().debug("MapsComparator: Gaussian Distribution Produced");
				return image;
	}
	
	@Override
	public StatisticalType getOutput() {
		
		// set the output map containing values
		AnalysisLogger.getLogger().debug("MapsComparator: Producing Gaussian Distribution for the errors");
		
		// build image:
		HashMap<String, Image> producedImages = new HashMap<String, Image>();
		double mean = 0;
		try{mean = Double.parseDouble(outputParameters.get("MEAN"));}catch(Exception e){}
		double variance = 0;
		try{variance = Double.parseDouble(outputParameters.get("VARIANCE"));
		variance = MathFunctions.roundDecimal(Math.sqrt(variance),2);//use std deviation
		}catch(Exception e){}
		producedImages.put("Error Distribution", generateGaussian(mean, variance));
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
	public float getStatus() {
		AnalysisLogger.getLogger().debug("Maps Comparator Status: "+status);
		return status;
	}

	@Override
	public LinkedHashMap<String, String> analyze() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	

}
