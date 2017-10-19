package org.gcube.dataanalysis.geo.algorithms;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.signals.SignalProcessing;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.wfs.WFS;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;
import org.gcube.dataanalysis.geo.matrixmodel.ZExtractor;

public class ZExtraction extends XYExtraction{

	@Override
	public String getDescription() {
		return "An algorithm to extract the Z values from a geospatial features repository (e.g. NETCDF, ASC, GeoTiff files etc. ). " +
				"The algorithm analyses the repository and automatically extracts the Z values according to the resolution wanted by the user. " +
				"It produces one chart of the Z values and one table containing the values.";
	}

	public static String x = "X";
	public static String y = "Y";
	public static String resolution = "Resolution";
	
	public  double xValue;
	public  double yValue;
	public  double resolutionValue;

	public double signal[];
	
	@Override
	public List<StatisticalType> getInputParameters() {
		
		List<StatisticalType> previnputs = super.getInputParameters();
		
		inputs = new ArrayList<StatisticalType>();
		//layername
		inputs.add(previnputs.get(0));
		inputs.add(previnputs.get(5));
		inputs.add(previnputs.get(6));
		IOHelper.addDoubleInput(inputs, x, "X coordinate", "0");
		IOHelper.addDoubleInput(inputs, y, "Y coordinate", "0");
		inputs.add(previnputs.get(8));
		IOHelper.addDoubleInput(inputs, resolution, "Step for Z values", "100");
		
		DatabaseType.addDefaultDBPars(inputs);
		
		return inputs;
	}
	
	
	protected void getParameters() {
		layerNameValue = IOHelper.getInputParameter(config, layerName);
		AnalysisLogger.getLogger().debug("Extraction: Layer " + layerNameValue);
		time = Integer.parseInt(IOHelper.getInputParameter(config, t));
		xValue = Double.parseDouble(IOHelper.getInputParameter(config, x));
		yValue = Double.parseDouble(IOHelper.getInputParameter(config, y));
		resolutionValue=Double.parseDouble(IOHelper.getInputParameter(config, resolution));
		
		AnalysisLogger.getLogger().debug("Extraction: T " + time);
		AnalysisLogger.getLogger().debug("Extraction: X " + xValue);
		AnalysisLogger.getLogger().debug("Extraction: Y " + yValue);
		AnalysisLogger.getLogger().debug("Extraction: Res " + resolutionValue);
		
		tableNameValue = IOHelper.getInputParameter(config, tableName);
		tableLabelValue = IOHelper.getInputParameter(config, tableLabel);
		AnalysisLogger.getLogger().debug("Extraction: tableName " + tableNameValue);
		AnalysisLogger.getLogger().debug("Extraction: tableLabel " + tableLabelValue);

		String scope = config.getGcubeScope();
		AnalysisLogger.getLogger().debug("Extraction: Externally set scope " + scope);
		if (scope == null) {
			scope = ScopeProvider.instance.get();
			config.setGcubeScope(scope);
		}
	}
	
	Image signalimage;
	Image spectrogramImage;
	@Override
	public void compute() throws Exception {

		try {
			status = 30;
			AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
			getParameters();

			AnalysisLogger.getLogger().debug("Extracting Time Series from layer");
			ZExtractor extractor = new ZExtractor(config);
			
			extractor.correctZ(0, layerNameValue,resolutionValue);
			
			long t0 = System.currentTimeMillis();
			signal = extractor.extractZ(layerNameValue, xValue,yValue, time, resolutionValue);
			
			AnalysisLogger.getLogger().debug("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
			AnalysisLogger.getLogger().debug("Signal: "+signal.length);
			status = 30;

			if (signal.length==1)
				AnalysisLogger.getLogger().debug("Extractor: Signal is only one point!");
			
			status = 70;
			
			AnalysisLogger.getLogger().debug("Extractor: Matrix Extracted");
			AnalysisLogger.getLogger().debug("Extractor: ****Rasterizing grid into table****");
			
			double matrix[][] = new double[1][];
			matrix[0] = signal;
			
			HashMap<Double,Map<String, String>> polygonsFeatures = null;
			if (extractor.currentconnector instanceof WFS)
				polygonsFeatures = ((WFS) extractor.currentconnector).getPolygonsFeatures();
			
			RasterTable raster = new RasterTable(xValue, xValue, yValue, yValue, zValue, time,resolutionValue, resolutionValue, matrix, polygonsFeatures,config);
			
			int signalRate = 1;
			
			double zline[] = new double[signal.length];
			int j=0;
			for (double z=extractor.zmin;z<=extractor.zmax;z=z+resolutionValue){
				zline[j]=z;
				j++;
			}
			
			List<Tuple<Double>> coordinates=new ArrayList<Tuple<Double>>();
			for (int i=0;i<zline.length;i++)
				coordinates.add(new Tuple<Double>(xValue,yValue,zline[i],(double)time));
			
			raster.setTablename(tableNameValue);
			raster.setCoordinates(coordinates);
			
			raster.deleteTable();
			raster.dumpGeoTable();
			
			signalimage = SignalProcessing.renderSignalWithGenericTime(signal, zline, "Z");
			
			AnalysisLogger.getLogger().debug("Extractor: Map was dumped in table: " + tableNameValue);
			status = 80;
			AnalysisLogger.getLogger().debug("Extractor: Elapsed: Whole operation completed in " + ((double) (System.currentTimeMillis() - t0) / 1000d) + "s");
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("Extractor: ERROR!: " + e.getLocalizedMessage());
			throw e;
		} finally {
			status = 100;
		}

	}
	
	
	
	@Override
	public StatisticalType getOutput() {
		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
		
		List<TableTemplates> templateHspec = new ArrayList<TableTemplates>();
		templateHspec.add(TableTemplates.TIMESERIES);
		
		OutputTable p = new OutputTable(templateHspec, tableLabelValue, tableNameValue, "Output table");
		map.put("OutputTable", p);
		if (signalimage!=null){
			HashMap<String, Image> producedImages = new HashMap<String, Image>();
			producedImages.put("Z Modulations Visualization", signalimage);
			PrimitiveType images = new PrimitiveType("Images", producedImages, PrimitiveTypes.IMAGES, "Modulations of Z", "The modulations of Z");
			map.put("Images", images);
		}
				
		// generate a primitive type for the collection
		PrimitiveType outputm = new PrimitiveType(HashMap.class.getName(), map, PrimitiveTypes.MAP, "ResultsMap", "Results Map");
		
		return outputm;
	}
	
	
	


}
