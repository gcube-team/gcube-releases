package org.gcube.dataanalysis.geo.algorithms;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.gcube.dataanalysis.geo.connectors.asc.AscRaster;
import org.gcube.dataanalysis.geo.connectors.asc.AscRasterWriter;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;

public class ESRIGRIDExtraction extends XYExtraction {

	@Override
	public List<StatisticalType> getInputParameters() {

		IOHelper.addStringInput(inputs, layerName, "Layer Title or UUID or HTTP link. E.g. the title or the UUID (preferred) of a layer indexed in the e-Infrastructure on GeoNetwork - You can retrieve it from GeoExplorer. Otherwise you can supply the direct HTTP link of the layer. The format will be guessed from the link. The default is GeoTiff. Supports several standards (NETCDF, WFS, WCS ASC, GeoTiff )", "");
		IOHelper.addDoubleInput(inputs, yLL, "Lower Left Latitute of the Bounding Box", "-60");
		IOHelper.addDoubleInput(inputs, xLL, "Lower Left Longitude of the Bounding Box", "-50");
		IOHelper.addDoubleInput(inputs, yUR, "Upper Right Latitute of the Bounding Box", "60");
		IOHelper.addDoubleInput(inputs, xUR, "Upper Right Longitude of the Bounding Box", "50");

		IOHelper.addDoubleInput(inputs, z, "Value of Z. Default is 0, that means processing will be at surface level or at the first avaliable Z value in the layer", "0");
		IOHelper.addIntegerInput(inputs, t, "Time Index. The default is the first time indexed dataset", "0");

		IOHelper.addDoubleInput(inputs, xRes, "Projection resolution on the X axis", "0.5");
		IOHelper.addDoubleInput(inputs, yRes, "Projection resolution on the Y axis", "0.5");

		DatabaseType.addDefaultDBPars(inputs);
		return inputs;
	}
	
	@Override
	public String getDescription() {
		return "An algorithm to extract values associated to an environmental feature repository (e.g. NETCDF, ASC, GeoTiff files etc. ).  A grid of points at a certain resolution is specified by the user and values are associated to the points from the environmental repository. " + "It accepts as one  geospatial repository ID (via their UUIDs in the infrastructure spatial data repository - recoverable through the Geoexplorer portlet) or a direct link to a file " + "and the specification about time and space. The algorithm produces one ESRI GRID ASCII file containing the values associated to the selected bounding box.";
	}

	private File outputfile;
	@Override
	public void compute() throws Exception {

		try {
			status = 10;
			AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
			getParameters();

			AnalysisLogger.getLogger().debug("Extractor: MatrixExtractor initialized");
			long t0 = System.currentTimeMillis();
			XYExtractor extractor = new XYExtractor(config);
			zValue = extractor.correctZ(zValue, layerNameValue, xResValue);
					
			AnalysisLogger.getLogger().debug("XYExtraction->Best Z for this reference layer: " + zValue);
			
			outputParameters.put("Matching Z value in the layer", ""+zValue);
			outputParameters.put("Min Z value in the Layer", ""+extractor.zmin);
			outputParameters.put("Max Z value in the Layer", ""+extractor.zmax);
			
			double[][] matrix = extractor.extractXYGrid(layerNameValue, time, BBxLL, BBxUR, BByLL, BByUR, zValue, xResValue, yResValue);
		
			AnalysisLogger.getLogger().debug("ELAPSED TIME: " + (System.currentTimeMillis() - t0));
			AnalysisLogger.getLogger().debug("Extractor: Matrix Extracted");
			AnalysisLogger.getLogger().debug("Extractor: ****Rasterizing grid into table****");
			
			status = 30;
			outputfile = new File(config.getPersistencePath(),"asc_"+UUID.randomUUID()+".asc");
			AnalysisLogger.getLogger().debug("Extractor: Building raster object");
			AscRaster raster = new AscRaster(matrix, xResValue, xResValue, yResValue, BBxLL, BByLL);
			AnalysisLogger.getLogger().debug("Extractor: Writing raster file " + outputfile.getAbsolutePath());
			AscRasterWriter writer = new AscRasterWriter();
			writer.writeRaster(outputfile.getAbsolutePath(), raster);
			AnalysisLogger.getLogger().debug("Extractor: Map was dumped in file: " + outputfile);
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
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shutdown");

	}

	@Override
	public StatisticalType getOutput() {
		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
		
		for (String key:outputParameters.keySet()){
			String value = outputParameters.get(key);
			PrimitiveType val = new PrimitiveType(String.class.getName(), "" + value, PrimitiveTypes.STRING, key, key);
			map.put(key, val);	
		}
		
		PrimitiveType file = new PrimitiveType(File.class.getName(), outputfile, PrimitiveTypes.FILE, "Output ESRI GRID ASCII FILE", "Output ESRI GRID ASCII FILE"); 
		map.put("OutputFile", file);
		PrimitiveType outputm = new PrimitiveType(HashMap.class.getName(), map, PrimitiveTypes.MAP, "ResultsMap", "Results Map");
		return outputm;
	}

	@Override
	public float getStatus() {
		return status;
	}

	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		this.config = config;
	}

	protected ResourceFactory resourceManager;

	public String getResourceLoad() {
		if (resourceManager == null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}

	public String getResources() {
		return ResourceFactory.getResources(100f);
	}
}
