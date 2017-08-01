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
import org.gcube.dataanalysis.ecoengine.signals.PeriodicityDetector;
import org.gcube.dataanalysis.ecoengine.signals.SignalConverter;
import org.gcube.dataanalysis.ecoengine.signals.SignalProcessing;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.wfs.WFS;
import org.gcube.dataanalysis.geo.matrixmodel.RasterTable;
import org.gcube.dataanalysis.geo.matrixmodel.TimeSeriesExtractor;

public class TimeExtraction extends XYExtraction{

	public static String x = "X";
	public static String y = "Y";
	public static String resolution = "Resolution";
	public static String samplingFrequency = "SamplingFreq";
	public static String minFrequency = "MinFrequency";
	public static String maxFrequency = "MaxFrequency";
	public static String expectedFrequencyError = "FrequencyError";
	public static String FFTSamplesParam = "FFTSamples";
	
	public  double xValue;
	public  double yValue;
	public  double resolutionValue;
	public  int samplingFrequencyValue;
	public  double minFrequencyValue;
	public  double maxFrequencyValue;
	public  double expectedFrequencyErrorValue;
	public  int FFTSamples;
	public PeriodicityDetector pd;
	public double signal[];
	public double timeline[];
	@Override
	public String getDescription() {
		return "An algorithm to extract a time series of values associated to a geospatial features repository (e.g. NETCDF, ASC, GeoTiff files etc. ). The algorithm analyses the time series and automatically searches for hidden periodicities. It produces one chart of the time series, one table containing the time series values and possibly the spectrogram.";
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		
		List<StatisticalType> previnputs = super.getInputParameters();
		inputs = new ArrayList<StatisticalType>();
		inputs.add(previnputs.get(0));
		inputs.add(previnputs.get(5));
		inputs.add(previnputs.get(6));
		IOHelper.addDoubleInput(inputs, x, "X coordinate", "0");
		IOHelper.addDoubleInput(inputs, y, "Y coordinate", "0");
		inputs.add(previnputs.get(7));
		IOHelper.addDoubleInput(inputs, resolution, "Extraction point resolution", "0.5");
		
		IOHelper.addIntegerInput(inputs, samplingFrequency, "Sampling frequency in Hz. Leave it to -1 if unknown or under 1", "-1");
//		IOHelper.addDoubleInput(inputs, minFrequency, "Minimum expected frequency in Hz. Can be decimal", "-1");
//		IOHelper.addDoubleInput(inputs, maxFrequency, "Maximum expected frequency in Hz. Can be decimal", "-1");
		//IOHelper.addDoubleInput(inputs, expectedFrequencyError, "Expected precision on periodicity detection in Hz or 1/samples. Can be decimal and depends on the signal length. Default is 0.1", "0.1");
//		IOHelper.addIntegerInput(inputs, FFTSamplesParam, "Number of samples to use in the Fourier Analysis. All samples will be used at maximum.", "100");
		
		DatabaseType.addDefaultDBPars(inputs);
		
		return inputs;
	}
	
	
	protected void getParameters() {
		layerNameValue = IOHelper.getInputParameter(config, layerName);
		AnalysisLogger.getLogger().debug("Extraction: Layer " + layerNameValue);
		zValue = Double.parseDouble(IOHelper.getInputParameter(config, z));
		xValue = Double.parseDouble(IOHelper.getInputParameter(config, x));
		yValue = Double.parseDouble(IOHelper.getInputParameter(config, y));
		resolutionValue=Double.parseDouble(IOHelper.getInputParameter(config, resolution));
		samplingFrequencyValue=Integer.parseInt(IOHelper.getInputParameter(config, samplingFrequency));
//		minFrequencyValue=Double.parseDouble(IOHelper.getInputParameter(config, minFrequency));
//		maxFrequencyValue=Double.parseDouble(IOHelper.getInputParameter(config, maxFrequency));
		expectedFrequencyErrorValue=-1;
		
		AnalysisLogger.getLogger().debug("Extraction: Z " + zValue);
		AnalysisLogger.getLogger().debug("Extraction: X " + xValue);
		AnalysisLogger.getLogger().debug("Extraction: Y " + yValue);
		AnalysisLogger.getLogger().debug("Extraction: Res " + resolutionValue);
		AnalysisLogger.getLogger().debug("Extraction: SamplingF " + samplingFrequency);
		AnalysisLogger.getLogger().debug("Extraction: minF " + minFrequencyValue);
		AnalysisLogger.getLogger().debug("Extraction: maxF " + maxFrequencyValue);
		AnalysisLogger.getLogger().debug("Extraction: expectedError " + expectedFrequencyErrorValue);
		
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
			status = 10;
			AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
			getParameters();

			AnalysisLogger.getLogger().debug("Extracting Time Series from layer");
			TimeSeriesExtractor intersector = new TimeSeriesExtractor(config);
			long t0 = System.currentTimeMillis();
			//take best z
			zValue = intersector.correctZ(zValue, layerNameValue, resolutionValue);
			AnalysisLogger.getLogger().debug("TimeExtraction->Best Z for this reference layer: " + zValue);
			outputParameters.put("Matching Z value in the layer", ""+zValue);
			outputParameters.put("Min Z value in the Layer", ""+intersector.zmin);
			outputParameters.put("Max Z value in the Layer", ""+intersector.zmax);
			
			AnalysisLogger.getLogger().debug("Z allowed to be: "+zValue);
			signal = intersector.extractT(layerNameValue, xValue,yValue, zValue, resolutionValue);
			
			AnalysisLogger.getLogger().debug("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
			AnalysisLogger.getLogger().debug("Signal: "+signal.length);
			status = 30;

			AnalysisLogger.getLogger().debug("Extractor: Matrix Extracted");
			AnalysisLogger.getLogger().debug("Extractor: ****Rasterizing grid into table****");
			
			double matrix[][] = new double[1][];
			matrix[0] = signal;
			HashMap<Double,Map<String, String>> polygonsFeatures = null;
			if (intersector.currentconnector instanceof WFS)
				polygonsFeatures = ((WFS) intersector.currentconnector).getPolygonsFeatures();
			
			RasterTable raster = new RasterTable(xValue, xValue, yValue, yValue, zValue, resolutionValue, resolutionValue, matrix, polygonsFeatures, config);
			
			int signalRate = 1;
			if (samplingFrequencyValue>0)
				signalRate=samplingFrequencyValue;
			
			timeline = SignalConverter.signalTimeLine(signal.length, signalRate);
			List<Tuple<Double>> coordinates=new ArrayList<Tuple<Double>>();
			for (int i=0;i<timeline.length;i++)
				coordinates.add(new Tuple<Double>(xValue,yValue,zValue,timeline[i]));
			
			raster.setTablename(tableNameValue);
			raster.setCoordinates(coordinates);
			
			raster.deleteTable();
			raster.dumpGeoTable();
			
			signalimage = SignalProcessing.renderSignalWithGenericTime(signal, timeline, "Signal");

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
		if (signal!=null && signal.length>0){
			HashMap<String, Image> producedImages = new HashMap<String, Image>();
			if (signalimage!=null)
			producedImages.put("Time Series Visualization", signalimage);
			if (spectrogramImage!=null)
				producedImages.put("Spectrogram", spectrogramImage);
			/*
			try {
				ImageIO.write(ImageTools.toBufferedImage(signalimage), "png", new File("signal.png"));
				ImageIO.write(ImageTools.toBufferedImage(spectrogramImage), "png", new File("spectrogram.png"));
				
			} catch (IOException e) {

				e.printStackTrace();
			}
			*/
			PrimitiveType images = new PrimitiveType("Images", producedImages, PrimitiveTypes.IMAGES, "Signal Processing", "Visualization of the signal and spectrogram");
			
			for (String key:outputParameters.keySet()){
				String value = outputParameters.get(key);
				PrimitiveType val = new PrimitiveType(String.class.getName(), "" + value, PrimitiveTypes.STRING, key, key);
				map.put(key, val);	
			}
			
			map.put("Images", images);
		}
		else
				map.put("Note", new PrimitiveType(String.class.getName(), "The signal contains only one point. The charts will not be displayed.", PrimitiveTypes.STRING,"Note","Note about the signal"));
		
		// generate a primitive type for the collection
		PrimitiveType outputm = new PrimitiveType(HashMap.class.getName(), map, PrimitiveTypes.MAP, "ResultsMap", "Results Map");
		
		return outputm;
	}

}
