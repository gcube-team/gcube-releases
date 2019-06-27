package org.gcube.dataanalysis.geo.algorithms;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.gcube.dataanalysis.geo.connectors.asc.AscRaster;
import org.gcube.dataanalysis.geo.connectors.asc.AscRasterReader;
import org.gcube.dataanalysis.geo.matrixmodel.ASCConverter;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;
import org.gcube.dataanalysis.geo.matrixmodel.XYExtractor;
import org.gcube.dataanalysis.geo.utils.MapUtils;
import org.hibernate.SessionFactory;

import density.Maxent;

public class MaxEnt4NicheModellingTransducer implements Transducerer {

	static String t = "TimeIndex";
	static String z = "Z";
	static String yLL = "BBox_LowerLeftLat";
	static String xLL = "BBox_LowerLeftLong";
	static String yUR = "BBox_UpperRightLat";
	static String xUR = "BBox_UpperRightLong";
	static String xRes = "XResolution";
	static String yRes = "YResolution";
	static String tableLabel = "OutputTableLabel";
	static String speciesLabel = "SpeciesName";
	static String OccurrencesTableNameParameter = "OccurrencesTable";
	static String LongitudeColumn = "LongitudeColumn";
	static String LatitudeColumn = "LatitudeColumn";
	static String Layers = "Layers";
	static String maxIterations = "MaxIterations";
	static String prevalence = "DefaultPrevalence";
	
	AlgorithmConfiguration config;

	float status;

	public int time;
	public double zValue;
	public double xResValue;
	public double yResValue;
	public String tableNameValue="";
	public String tableLabelValue="";
	public double BBxLL = -180;
	public double BBxUR = 180;
	public double BByLL = -90;
	public double BByUR = 90;
	private int maxIterationsValue;
	private double prevalenceValue;
	private double bestThreshold=0;
	private double prevalenceVal=0;
	private String variablesContributions = "";
	private String variablesPermutationsImportance = "";
	private String warnings = "";
	private String layerIdentities = "";
	private File warningsFile=null;
	private File projectionFile=null;
	private File asciimapsFile=null;
	
	LinkedHashMap<String, Image> producedImages = new LinkedHashMap<String, Image>();
	
	public List<StatisticalType> inputs = new ArrayList<StatisticalType>();
	public LinkedHashMap<String, String> outputParameters = new LinkedHashMap<String, String>();

	private String[] layers;
	private String occurrencesTableName;
	private String speciesName;
	private String longitudeColumn;
	private String latitudeColumn;
	
	
	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("MaxEnt: Initialization");

	}

	@Override
	public String getDescription() {
		return "A Maximum-Entropy model for species habitat modeling, based on the implementation by Shapire et al. v 3.3.3k, Princeton University, http://www.cs.princeton.edu/schapire/maxent/. " +
				"In this adaptation for the D4Science infrastructure, the software accepts a table produced by the Species Product Discovery service and a set of environmental layers in various formats (NetCDF, WFS, WCS, ASC, GeoTiff) via direct links or GeoExplorer UUIDs. " +
				"The user can also establish the bounding box and the spatial resolution (in decimal deg.) of the training and the projection. The application will adapt the layers to that resolution if this is higher than the native one." +
				"The output contains: a thumbnail map of the projected model, the ROC curve, the Omission/Commission chart, a table containing the raw assigned values, a threshold to transform the table into a 0-1 probability distribution, a report of the importance of the used layers in the model, ASCII representations of the input layers to check their alignment." +
				"Other processes can be later applied to the raw values to produce a GIS map (e.g. the Statistical Manager Points-to-Map process) and results can be shared. Demo video: http://goo.gl/TYYnTO and instructions http://wiki.i-marine.eu/index.php/MaxEnt";
	}

	@Override
	public List<StatisticalType> getInputParameters() {

		// output table parameter
		IOHelper.addStringInput(inputs, tableLabel, "The name of the table to produce", "maxent_");
		IOHelper.addStringInput(inputs, speciesLabel, "The name of the species to model and the occurrence records refer to", "generic_species");
		IOHelper.addIntegerInput(inputs, maxIterations, "The number of learning iterations of the MaxEnt algorithm", "1000");
		IOHelper.addDoubleInput(inputs, prevalence, "A priori probability of presence at ordinary occurrence points", "0.5");
		
		// table parameters
		List<TableTemplates> template = new ArrayList<TableTemplates>();
		template.add(TableTemplates.OCCURRENCE_SPECIES);
		InputTable table = new InputTable(template, OccurrencesTableNameParameter, "A geospatial table containing occurrence records, following the template of the Species Products Discovery datasets", "");
		inputs.add(table);
		ColumnType p1 = new ColumnType(OccurrencesTableNameParameter, LongitudeColumn, "The column containing longitude values", "decimallongitude", false);
		inputs.add(p1);
		ColumnType p2 = new ColumnType(OccurrencesTableNameParameter, LatitudeColumn, "The column containing latitude values", "decimallatitude", false);
		inputs.add(p2);

		IOHelper.addDoubleInput(inputs, xRes, "Model projection resolution on the X axis in decimal degrees", "1");
		IOHelper.addDoubleInput(inputs, yRes, "Model projection resolution on the Y axis in decimal degrees", "1");

		// layers to use in the model
		PrimitiveTypesList listEnvLayers = new PrimitiveTypesList(String.class.getName(), PrimitiveTypes.STRING, Layers, "The list of environmental layers to use for enriching the points. Each entry is a layer Title or UUID or HTTP link. E.g. the title or the UUID (preferred) of a layer indexed in the e-Infrastructure on GeoNetwork - You can retrieve it from GeoExplorer. Otherwise you can supply the direct HTTP link of the layer. The format will be guessed from the link. The default is GeoTiff. Supports several standards (NETCDF, WFS, WCS, ASC, GeoTiff ). E.g. https://dl.dropboxusercontent.com/u/12809149/wind1.tif", false);
		inputs.add(listEnvLayers);

		IOHelper.addDoubleInput(inputs, z, "Value of Z. Default is 0, that means environmental layers processing will be at surface level or at the first avaliable Z value in the layer", "0");
		IOHelper.addIntegerInput(inputs, t, "Time Index. The default is the first time indexed in the input environmental datasets", "0");

		DatabaseType.addDefaultDBPars(inputs);
		return inputs;
	}

	protected void getParameters() {

		String scope = config.getGcubeScope();
		AnalysisLogger.getLogger().debug("MaxEnt: : Externally set scope " + scope);
		if (scope == null) {
			scope = ScopeProvider.instance.get();
			AnalysisLogger.getLogger().debug("MaxEnt: : Internally set scope " + scope);
			config.setGcubeScope(scope);
		}

		// get input name and list of fields
		occurrencesTableName = IOHelper.getInputParameter(config, OccurrencesTableNameParameter);
		longitudeColumn = IOHelper.getInputParameter(config, LongitudeColumn);
		latitudeColumn = IOHelper.getInputParameter(config, LatitudeColumn);
		speciesName = IOHelper.getInputParameter(config, speciesLabel);

		// get environmental layers
		layers = IOHelper.getInputParameter(config, Layers).trim().split(AlgorithmConfiguration.getListSeparator());
		AnalysisLogger.getLogger().debug("MaxEnt: N. of Layers to take " + layers.length);

		// get time and z values
		String z$ = IOHelper.getInputParameter(config, z);
		String time$ = IOHelper.getInputParameter(config, t);

		time = ((time$ != null) && (time$.trim().length() > 0)) ? Integer.parseInt(time$) : 0;
		if (time < 0)
			time = 0;

		AnalysisLogger.getLogger().debug("MaxEnt: : Time " + time);
		zValue = 0;
		if ((z$ != null) && (z$.trim().length() > 0))
			try {
				zValue = Double.parseDouble(z$);
			} catch (Exception ee) {
			}

		AnalysisLogger.getLogger().debug("MaxEnt: : Z " + zValue);

		// get Bounding Box for the projection
		BByLL = -90;
		BBxLL = -180;
		BByUR = 90;
		BBxUR = 180;

		AnalysisLogger.getLogger().debug("MaxEnt: : yLL " + BByLL);
		AnalysisLogger.getLogger().debug("MaxEnt: : xLL " + BBxLL);
		AnalysisLogger.getLogger().debug("MaxEnt: : yUR " + BByUR);
		AnalysisLogger.getLogger().debug("MaxEnt: : xUR " + BBxUR);

		// get y and x resolutions
		yResValue = Double.parseDouble(IOHelper.getInputParameter(config, yRes));
		AnalysisLogger.getLogger().debug("MaxEnt: : yRes " + yResValue);
		xResValue = Double.parseDouble(IOHelper.getInputParameter(config, xRes));
		AnalysisLogger.getLogger().debug("MaxEnt: : xRes " + xResValue);

		// get output table value
		tableLabelValue = IOHelper.getInputParameter(config, tableLabel);
		AnalysisLogger.getLogger().debug("MaxEnt: : tableName " + tableNameValue);
		AnalysisLogger.getLogger().debug("MaxEnt: : tableLabel " + tableLabelValue);
		
		prevalenceValue = Double.parseDouble(IOHelper.getInputParameter(config, prevalence));
		maxIterationsValue = Integer.parseInt(IOHelper.getInputParameter(config, maxIterations)); 
		
	}

	@Override
	public void compute() throws Exception {

		Maxent me = null; 
		try {
			status = 10;
			AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
			getParameters();
			AnalysisLogger.getLogger().debug("MaxEnt: parameters initialized");
			long t0 = System.currentTimeMillis();
			String localtempFolder = new File(config.getPersistencePath(), "maxent" + UUID.randomUUID()).getAbsolutePath();
			
					
			if (!new File(localtempFolder).exists())
				new File(localtempFolder).mkdir();

			String localOccurrencesFile = new File(localtempFolder, occurrencesTableName).getAbsolutePath();
			String localFinalOccurrencesFile = localOccurrencesFile + "_occ.csv";
			String localAsciiMapsFile = localOccurrencesFile + "_maps.txt";
			
			AnalysisLogger.getLogger().debug("MaxEnt: local occurrence file to produce "+localFinalOccurrencesFile);
			AnalysisLogger.getLogger().debug("MaxEnt: initializing connection");	
			
			// prepare input data
			AnalysisLogger.getLogger().debug("MaxEnt: creating local file from remote table "+occurrencesTableName);
			
			DatabaseUtils.createLocalFileFromRemoteTable(
					localOccurrencesFile, "(select " + longitudeColumn + " as longitude," + latitudeColumn + " as latitude from " + occurrencesTableName + ")", ",", 
					config.getParam("DatabaseUserName"),
					config.getParam("DatabasePassword"), 
					config.getParam("DatabaseURL"));
			
			AnalysisLogger.getLogger().debug("MaxEnt: table "+occurrencesTableName+" was dumped in file: " + localOccurrencesFile);
			// write an input file for maxent
			AnalysisLogger.getLogger().debug("MaxEnt: preparing input for maxent in file "+localFinalOccurrencesFile);
			prepareInputForMaxEnt(speciesName, localOccurrencesFile, localFinalOccurrencesFile);
			AnalysisLogger.getLogger().debug("MaxEnt: converting layers ... ");
			// convert all the layers into file
			int layersCount = 1;
			status = 30;
			BufferedWriter mapwriter = new BufferedWriter(new FileWriter(new File(localAsciiMapsFile)));
			
			for (String layer : layers) {
				ASCConverter converter = new ASCConverter(config);
				String layerfile = new File(localtempFolder, "layer"+layersCount+ ".asc").getAbsolutePath();
				
				AnalysisLogger.getLogger().debug("MaxEnt: converting " + layer +" into "+layerfile);
				XYExtractor extractor = new XYExtractor(config);
				double[][] values = extractor.extractXYGrid(layer, time, BBxLL, BBxUR, BByLL, BByUR, zValue, xResValue, yResValue);
				mapwriter.write(MapUtils.globalASCIIMap(values));
				
				AnalysisLogger.getLogger().debug("MaxEnt: layer name " + extractor.layerName);
				layerIdentities+=layer+"="+extractor.layerName+" ";
				
				String converted = converter.convertToASC(layerfile, values,  BBxLL,BByLL,xResValue,yResValue);
				AnalysisLogger.getLogger().debug("MaxEnt: converted into ASC file " + converted + " check: " + new File(converted).exists());
				layersCount++;
			}
			
			mapwriter.close();
			status = 70;
			
			AnalysisLogger.getLogger().debug("MaxEnt: executing MaxEnt");
			
			//call MaxEnt
			me = new Maxent(localFinalOccurrencesFile,localtempFolder,localtempFolder, maxIterationsValue, prevalenceValue, -9999);
			me.executeMaxEnt();
			
			AnalysisLogger.getLogger().debug("MaxEnt: OK MaxEnt!");
			try{
			AnalysisLogger.getLogger().debug("MaxEnt: Result: "+me.getResult());
			}catch(Exception e){
				AnalysisLogger.getLogger().debug("MaxEnt: error in retrieving the result "+e.getLocalizedMessage());
			}
			
			bestThreshold = me.getBestThr();
			prevalenceVal = me.getPrevalence();
			variablesContributions = me.getVariablesContributions().toString().replace("{", "").replace("}","");
			variablesPermutationsImportance = me.getVariablesPermutationsImportance().toString().replace("{", "").replace("}","");
			warnings = me.getWarnings();
			
			String worldFile = me.getWorldPlot();
			String rocFile = me.getROCPlot();
			String omissionsFile = me.getOmissionPlot();
	
			AnalysisLogger.getLogger().debug("MaxEnt: ROC plot: "+worldFile);
			AnalysisLogger.getLogger().debug("MaxEnt: World plot: "+rocFile);
			AnalysisLogger.getLogger().debug("MaxEnt: Omission/Commission Plot: "+omissionsFile);
			
			producedImages.put("World Thumbnail",ImageTools.toImage(ImageIO.read(new File(worldFile))));
			producedImages.put("ROC Curve",ImageTools.toImage(ImageIO.read(new File(rocFile))));
			producedImages.put("Omission-Commission Curve",ImageTools.toImage(ImageIO.read(new File(omissionsFile))));
			
			if (warnings!=null && warnings.trim().length()>0){
				warningsFile = new File(localtempFolder, "Warnings_"+tableLabelValue+".txt");
				FileTools.saveString(warningsFile.getAbsolutePath(),warnings,true,"UTF-8");
			}
			
			projectionFile = new File(me.getResult());
			asciimapsFile = new File(localAsciiMapsFile);
			AnalysisLogger.getLogger().debug("MaxEnt: Best Threshold: "+bestThreshold);
			AnalysisLogger.getLogger().debug("MaxEnt: Prevalence: "+prevalenceVal);
			AnalysisLogger.getLogger().debug("MaxEnt: Variables Contribution: "+variablesContributions);
			AnalysisLogger.getLogger().debug("MaxEnt: Variables Permutations: "+variablesPermutationsImportance);
			if (warningsFile!=null)
				AnalysisLogger.getLogger().debug("MaxEnt: Warnings file: "+warningsFile.getAbsolutePath() +" exists " + warningsFile.exists() );
			AnalysisLogger.getLogger().debug("MaxEnt: Projection file: "+projectionFile.getAbsolutePath()+" exists " + projectionFile.exists() );
			status = 80;
			AnalysisLogger.getLogger().debug("MaxEnt: Generating table");
			
			//write a table
			AscRasterReader reader = new AscRasterReader();
			AscRaster ascFile = reader.readRaster(projectionFile.getAbsolutePath());
			RasterTable table = new RasterTable(BBxLL, BBxUR, BByLL, BByUR, zValue, xResValue, yResValue, ascFile.getInvertedAxisData(), config);
			table.dumpGeoTable();
			tableNameValue = table.getTablename();
			AnalysisLogger.getLogger().debug("MaxEnt: Generated table "+tableNameValue);
//			me.clean();	
			status = 90;
			AnalysisLogger.getLogger().debug("MaxEnt: Elapsed: Whole operation completed in " + ((double) (System.currentTimeMillis() - t0) / 1000d) + "s");
		} catch (Throwable e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("MaxEnt: ERROR!: " + e.getLocalizedMessage());
			throw new Exception(e.getLocalizedMessage());
		} finally {
			shutdown();
//			if (me != null)
				//me.clean();
			status = 100;
		}

	}

	// from lon lat to species, lon, lat
	private void prepareInputForMaxEnt(String speciesname, String lonlatfile, String outputFile) throws Exception {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(new File(lonlatfile)));
			bw = new BufferedWriter(new FileWriter(new File(outputFile)));
			bw.write("species,longitude,latitude\n");
			String line = br.readLine();
			while (line != null) {
				bw.write(speciesname + "," + line + "\n");
				line = br.readLine();
			}

		} catch (Exception e) {
			AnalysisLogger.getLogger().debug(e);
		} finally {
			if (br != null)
				br.close();
			if (bw != null)
				bw.close();
		}
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("MaxEnt: Shutdown");
	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> templateHspec = new ArrayList<TableTemplates>();
		templateHspec.add(TableTemplates.GENERIC);
		OutputTable p = new OutputTable(templateHspec, tableLabelValue, tableNameValue, "Output table");
		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();

		map.put("Best Threshold", new PrimitiveType(String.class.getName(), "" + bestThreshold, PrimitiveTypes.STRING, "Best Threshold", "Best threshold for transforming MaxEnt values into 0/1 probability assignments"));
		map.put("Estimated Prevalence", new PrimitiveType(String.class.getName(), "" + prevalenceVal, PrimitiveTypes.STRING, "Estimated Prevalence", "The a posteriori estimated prevalence of the species"));
//		map.put("Variables names in the layers", new PrimitiveType(String.class.getName(), layerIdentities, PrimitiveTypes.STRING, "Variables names in the layers", "Variables names in the layers"));
		map.put("Variables Contributions", new PrimitiveType(String.class.getName(), variablesContributions, PrimitiveTypes.STRING, "Variables contributions", "The contribution of each variable to the MaxEnt values estimates"));
		map.put("Variables Permutations Importance", new PrimitiveType(String.class.getName(), variablesPermutationsImportance, PrimitiveTypes.STRING, "Variables Permutations Importance", "The importance of the permutations of the variables during the training"));
		
		if (warningsFile!=null)
			map.put("Warnings generated by the MaxEnt process", 	new PrimitiveType(File.class.getName(), warningsFile, PrimitiveTypes.FILE, "Warnings generated by the MaxEnt process", "The warnings from the underlying MaxEnt model"));
		
		map.put("ASCII Maps of the environmental layers for checking features aligments", new PrimitiveType(File.class.getName(), asciimapsFile, PrimitiveTypes.FILE, "ASCII Maps of the environmental layers for checking features aligments", "ASCII Maps of the environmental layers for checking features aligments"));
		
		PrimitiveType images = new PrimitiveType(LinkedHashMap.class.getName(), producedImages, PrimitiveTypes.IMAGES, "Model performance", "Model performance and projection");
		
		map.put("Images", images);
		
		map.put("OutputTable", p);
		PrimitiveType outputm = new PrimitiveType(LinkedHashMap.class.getName(), map, PrimitiveTypes.MAP, "ResultsMap", "Results Map");
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
