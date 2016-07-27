package org.gcube.dataanalysis.geo.algorithms;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.gcube.dataanalysis.geo.connectors.wfs.WFS;
import org.gcube.dataanalysis.geo.interfaces.GISDataConnector;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;
import org.gcube.dataanalysis.geo.utils.MapUtils;

public class XYExtraction implements Transducerer {

	static String layerName = "Layer";
	static String t = "TimeIndex";
	static String z = "Z";
	static String yLL = "BBox_LowerLeftLat";
	static String xLL = "BBox_LowerLeftLong";
	static String yUR = "BBox_UpperRightLat";
	static String xUR = "BBox_UpperRightLong";
	static String xRes = "XResolution";
	static String yRes = "YResolution";
	static String tableName = "OutputTableName";
	static String tableLabel = "OutputTableLabel";

	AlgorithmConfiguration config;

	float status;

	public String layerNameValue;
	public int time;
	public double zValue;
	public double xResValue;
	public double yResValue;
	public String tableNameValue;
	public String tableLabelValue;
	public double BBxLL = -180;
	public double BBxUR = 180;
	public double BByLL = -90;
	public double BByUR = 90;

	public List<StatisticalType> inputs = new ArrayList<StatisticalType>();
	public LinkedHashMap<String, String> outputParameters = new LinkedHashMap<String, String>();

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("Extraction Initialization");

	}

	@Override
	public String getDescription() {
		return "An algorithm to extract values associated to an environmental feature repository (e.g. NETCDF, ASC, GeoTiff files etc. ).  A grid of points at a certain resolution is specified by the user and values are associated to the points from the environmental repository. " + "It accepts as one  geospatial repository ID (via their UUIDs in the infrastructure spatial data repository - recoverable through the Geoexplorer portlet) or a direct link to a file " + "and the specification about time and space. The algorithm produces one table containing the values associated to the selected bounding box.";
	}

	@Override
	public List<StatisticalType> getInputParameters() {

		IOHelper.addStringInput(inputs, layerName, "Layer Title or UUID or HTTP link. E.g. the title or the UUID (preferred) of a layer indexed in the e-Infrastructure on GeoNetwork - You can retrieve it from GeoExplorer. Otherwise you can supply the direct HTTP link of the layer. The format will be guessed from the link. The default is GeoTiff. Supports several standards (NETCDF, WFS, WCS ASC, GeoTiff )", "");
		IOHelper.addDoubleInput(inputs, yLL, "Lower Left Latitute of the Bounding Box", "-60");
		IOHelper.addDoubleInput(inputs, xLL, "Lower Left Longitude of the Bounding Box", "-50");
		IOHelper.addDoubleInput(inputs, yUR, "Upper Right Latitute of the Bounding Box", "60");
		IOHelper.addDoubleInput(inputs, xUR, "Upper Right Longitude of the Bounding Box", "50");
		IOHelper.addRandomStringInput(inputs, tableName, "The db name of the table to produce", "extr_");
		IOHelper.addStringInput(inputs, tableLabel, "The name of the table to produce", "extr_");

		IOHelper.addDoubleInput(inputs, z, "Value of Z. Default is 0, that means processing will be at surface level or at the first avaliable Z value in the layer", "0");
		IOHelper.addIntegerInput(inputs, t, "Time Index. The default is the first time indexed dataset", "0");

		IOHelper.addDoubleInput(inputs, xRes, "Projection resolution on the X axis", "0.5");
		IOHelper.addDoubleInput(inputs, yRes, "Projection resolution on the Y axis", "0.5");

		DatabaseType.addDefaultDBPars(inputs);
		return inputs;
	}

	protected void getParameters() {
		layerNameValue = IOHelper.getInputParameter(config, layerName);
		AnalysisLogger.getLogger().debug("Extraction: Layer " + layerNameValue);
		String z$ = IOHelper.getInputParameter(config, z);
		String time$ = IOHelper.getInputParameter(config, t);

		time = ((time$ != null) && (time$.trim().length() > 0)) ? Integer.parseInt(time$) : 0;
		if (time < 0)
			time = 0;

		AnalysisLogger.getLogger().debug("Extraction: Time " + time);
		zValue = 0;
		if ((z$ != null) && (z$.trim().length() > 0))
			try {
				zValue = Double.parseDouble(z$);
			} catch (Exception ee) {
			}

		AnalysisLogger.getLogger().debug("Extraction: Z " + zValue);

		BByLL = Double.parseDouble(IOHelper.getInputParameter(config, yLL));
		BBxLL = Double.parseDouble(IOHelper.getInputParameter(config, xLL));
		BByUR = Double.parseDouble(IOHelper.getInputParameter(config, yUR));
		BBxUR = Double.parseDouble(IOHelper.getInputParameter(config, xUR));

		AnalysisLogger.getLogger().debug("Extraction: yLL " + BByLL);
		AnalysisLogger.getLogger().debug("Extraction: xLL " + BBxLL);
		AnalysisLogger.getLogger().debug("Extraction: yUR " + BByUR);
		AnalysisLogger.getLogger().debug("Extraction: xUR " + BBxUR);

		yResValue = Double.parseDouble(IOHelper.getInputParameter(config, yRes));
		AnalysisLogger.getLogger().debug("Extraction: yRes " + yResValue);
		xResValue = Double.parseDouble(IOHelper.getInputParameter(config, xRes));
		AnalysisLogger.getLogger().debug("Extraction: xRes " + xResValue);

		tableNameValue = IOHelper.getInputParameter(config, tableName);
		tableLabelValue = IOHelper.getInputParameter(config, tableLabel);
		AnalysisLogger.getLogger().debug("Extraction: tableName " + tableNameValue);
		AnalysisLogger.getLogger().debug("Extraction: tableLabel " + tableLabelValue);

		String scope = config.getGcubeScope();
		AnalysisLogger.getLogger().debug("Extraction: Externally set scope " + scope);
		if (scope == null) {
			scope = ScopeProvider.instance.get();
			AnalysisLogger.getLogger().debug("Extraction: Internally set scope " + scope);
			config.setGcubeScope(scope);
		}

	}

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
			
			
			HashMap<Double,Map<String, String>> polygonsFeatures = null;
			if (extractor.currentconnector instanceof WFS)
				polygonsFeatures = ((WFS) extractor.currentconnector).getPolygonsFeatures();
			
			AnalysisLogger.getLogger().debug("ELAPSED TIME: " + (System.currentTimeMillis() - t0));
			AnalysisLogger.getLogger().debug("Extractor: Matrix Extracted");
			AnalysisLogger.getLogger().debug("Extractor: ****Rasterizing grid into table****");
			
			
			//TODO: Check the Raster Table to avoid writing blanks and y flipping
			status = 30;
			RasterTable raster = new RasterTable(BBxLL, BBxUR, BByLL, BByUR, zValue, time, xResValue, yResValue, matrix, polygonsFeatures,config);
			raster.setTablename(tableNameValue);
			raster.deleteTable();
			raster.dumpGeoTable();
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
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shutdown");

	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> templateHspec = new ArrayList<TableTemplates>();
		templateHspec.add(TableTemplates.TIMESERIES);
		OutputTable p = new OutputTable(templateHspec, tableLabelValue, tableNameValue, "Output table");
		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
		
		for (String key:outputParameters.keySet()){
			String value = outputParameters.get(key);
			PrimitiveType val = new PrimitiveType(String.class.getName(), "" + value, PrimitiveTypes.STRING, key, key);
			map.put(key, val);	
		}
		
		map.put("OutputTable", p);
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
