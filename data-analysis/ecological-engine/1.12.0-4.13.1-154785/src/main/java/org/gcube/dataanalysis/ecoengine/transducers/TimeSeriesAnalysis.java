package org.gcube.dataanalysis.ecoengine.transducers;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.DateGuesser;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.signals.PeriodicityDetector;
import org.gcube.dataanalysis.ecoengine.signals.SignalProcessing;
import org.gcube.dataanalysis.ecoengine.signals.TimeSeries;
import org.gcube.dataanalysis.ecoengine.signals.ssa.SSADataset;
import org.gcube.dataanalysis.ecoengine.signals.ssa.SSAWorkflow;
import org.gcube.dataanalysis.ecoengine.utils.AggregationFunctions;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.hibernate.SessionFactory;

public class TimeSeriesAnalysis extends StandardLocalExternalAlgorithm {
	private static String timeSeriesTable = "TimeSeriesTable";
	private static String valuesColumn = "ValueColum";
	private static String timeColumn = "TimeColum";
	private static String fftwindowsamples = "FFT_Window_Samples";
	private static String aggregationFunction = "AggregationFunction";
	private static String sensitivityParam = "Sensitivity";
	private static String SSAAnalysisWindowSamples = "SSA_Window_in_Samples";
	private static String SSAEigenvaluesThreshold = "SSA_EigenvaluesThreshold";
	private static String SSAPointsToForecast = "SSA_Points_to_Forecast";
	private Image signalImg = null;
	private Image uniformSignalImg = null;
	private Image uniformSignalSamplesImg = null;
	private Image spectrogramImg = null;
	private Image forecastsignalImg = null;
	private Image eigenValuesImg = null;
	private File outputfilename = null;
	public static boolean display = false;
	private static int maxpoints = 10000;

	public enum Sensitivity {
		LOW, NORMAL, HIGH
	}

	@Override
	public void init() throws Exception {

	}

	@Override
	public String getDescription() {
		return "An algorithms applying signal processing to a non uniform time series. A maximum of " + maxpoints + " distinct points in time is allowed to be processed. The process uniformly samples the series, then extracts hidden periodicities and signal properties. The sampling period is the shortest time difference between two points. Finally, by using Caterpillar-SSA the algorithm forecasts the Time Series. The output shows the detected periodicity, the forecasted signal and the spectrogram.";
	}

	@Override
	protected void process() throws Exception {

		SessionFactory dbconnection = null;
		status = 0;
		try {
			dbconnection = DatabaseUtils.initDBSession(config);
			String tablename = config.getParam(timeSeriesTable);
			String valuescolum = config.getParam(valuesColumn);
			String timecolumn = config.getParam(timeColumn);
			String aggregationFunc = config.getParam(aggregationFunction);
			String fftwindowsamplesS = config.getParam(fftwindowsamples);
			int windowLength = Integer.parseInt(config.getParam(SSAAnalysisWindowSamples));
			float eigenvaluespercthr = Float.parseFloat(config.getParam(SSAEigenvaluesThreshold));
			int pointsToReconstruct = Integer.parseInt(config.getParam(SSAPointsToForecast));
			Sensitivity sensitivityP = Sensitivity.LOW;
			
			try{sensitivityP = Sensitivity.valueOf(config.getParam(sensitivityParam));}catch(Exception e){}

			float sensitivity = 9;
			switch (sensitivityP) {
			case LOW:
				sensitivity = 9;
				break;
			case NORMAL:
				sensitivity = 5;
				break;
			case HIGH:
				sensitivity = 1;
				break;
			}

			int fftWindowSamplesDouble = 1;
			if (timecolumn == null)
				timecolumn = "time";
			if (aggregationFunc == null)
				aggregationFunc = "SUM";
			if (fftwindowsamplesS != null) {
				try {
					fftWindowSamplesDouble = Integer.parseInt(fftwindowsamplesS);
				} catch (Exception e) {
				}
			}

			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Table Name: " + tablename);
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Time Column: " + timecolumn);
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Values Column: " + valuescolum);
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Aggregation: " + aggregationFunc);
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->FFT Window Samples: " + fftWindowSamplesDouble);
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->SSA Window Samples: " + windowLength);
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->SSA Eigenvalues threshold: " + eigenvaluespercthr);
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->SSA Points to Reconstruct: " + pointsToReconstruct);

			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Extracting Points...");
			String query = "select * from (select " + aggregationFunc + "( CAST ( " + valuescolum + " as real))," + timecolumn + " from " + tablename + " group by " + timecolumn + ") as a";
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Query to execute: " + query);
			List<Object> results = DatabaseFactory.executeSQLQuery(query, dbconnection);
			status = 10;

			if (results == null || results.size() == 0)
				throw new Exception("Error in retrieving values from the table: no time series found");
			else if (results.size() > maxpoints)
				throw new Exception("Too long Time Series: a maximum of distinct " + maxpoints + " in time is allowed");

			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Points Extracted!");
			// build signal
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Building signal");
			List<Tuple<String>> signal = new ArrayList<Tuple<String>>();
			int sizesignal = 0;
			for (Object row : results) {
				Object[] srow = (Object[]) row;
				String value = "" + srow[0];
				String time = "" + srow[1];
				signal.add(new Tuple<String>(time, value));
				sizesignal++;
			}
			status = 20;

			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Signal built with success. Size: " + sizesignal);
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Building Time Series");
			TimeSeries ts = TimeSeries.buildFromSignal(signal, config);
			String timepattern = ts.getTimepattern();
			String chartpattern = "MM-dd-yy";
			if (timepattern.equals("s") || (DateGuesser.isJavaDateOrigin(ts.getTime()[0]) && DateGuesser.isJavaDateOrigin(ts.getTime()[ts.getTime().length - 1]))) {
				AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Changing chart pattern to Seconds!");
				chartpattern = "HH:mm:ss:SS";
			} else
				AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Chart pattern remains " + chartpattern);

			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Uniformly sampling the signal");
			if (display)
				SignalProcessing.displaySignalWithTime(ts.getValues(), ts.getTime(), "Time Series", chartpattern);
			signalImg = SignalProcessing.renderSignalWithTime(ts.getValues(), ts.getTime(), "Original Time Series", chartpattern);
			int originalSignalLength = ts.getValues().length;
			ts.convertToUniformSignal(0);
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Uniform sampling finished");
			status = 30;

			// spectrum and signal processing
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Detecting periodicities");
			PeriodicityDetector pd = new PeriodicityDetector();
			LinkedHashMap<String, String> frequencies = pd.detectAllFrequencies(ts.getValues(), 1, 0.01f, 0.5f, -1, fftWindowSamplesDouble, sensitivity, display);
			outputParameters.put("Original Time Series Length", "" + originalSignalLength);
			outputParameters.put("Uniformly Samples Time Series Length", "" + ts.getValues().length);
			outputParameters.put("Spectral Analysis Window Length", "" + pd.currentWindowAnalysisSamples);
			outputParameters.put("Spectral Analysis Window Shift", "" + pd.currentWindowShiftSamples);
			outputParameters.put("Spectral Analysis Sampling Rate", "" + MathFunctions.roundDecimal(pd.currentSamplingRate, 2));
			outputParameters.put("Spectrogram Sections", "" + pd.currentspectrum.length);
			outputParameters.put("Range of frequencies (in samples^-1) represented in the Spectrogram:", "[" + MathFunctions.roundDecimal(pd.minFrequency, 2) + " ; " + MathFunctions.roundDecimal(pd.maxFrequency, 2) + "]");
			outputParameters.put("Unit of Measure of Frequency", "samples^-1");
			outputParameters.put("Unit of Measure of Time", "samples");

			for (String freqPar : frequencies.keySet()) {
				outputParameters.put(freqPar, frequencies.get(freqPar));
			}
			/*
			 * outputParameters.put("Detected Frequency (samples^-1)", ""+MathFunctions.roundDecimal(F,2)); outputParameters.put("Indecision on Frequency", "["+MathFunctions.roundDecimal(pd.lowermeanF,2)+" , "+MathFunctions.roundDecimal(pd.uppermeanF,2) + "]"); outputParameters.put("Average detected Period (samples)", ""+MathFunctions.roundDecimal(pd.meanPeriod,2)); outputParameters.put("Indecision on Average Period", "["+MathFunctions.roundDecimal(pd.lowermeanPeriod,2)+" , "+MathFunctions.roundDecimal(pd.uppermeanPeriod,2) + "]"); outputParameters.put("Samples range in which periodicity was detected", "from "+pd.startPeriodSampleIndex+"  to "+pd.endPeriodSampleIndex); outputParameters.put("Period Strength with interpretation", ""+MathFunctions.roundDecimal(pd.periodicityStrength,2)+" ("+pd.getPeriodicityStregthInterpretation()+")");
			 */

			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Periodicity Detected!");
			status = 60;

			System.gc();
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Executing SSA analysis");
			List<Double> values = new ArrayList<Double>();
			for (double v : ts.getValues()) {
				values.add(v);
			}

			Date[] newtimes = ts.extendTime(pointsToReconstruct);
			SSADataset ssa = null;
			if (windowLength < ts.getValues().length)
				ssa = SSAWorkflow.applyCompleteWorkflow(values, windowLength, eigenvaluespercthr, pointsToReconstruct, false);
			else {
				AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->SSA analysis impossible to complete");
				outputParameters.put("SSA Note:", "The window length is higher than the signal length. Please reduce the value to less than the signal length.");
				return;
			}
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->SSA analysis completed");
			status = 70;

			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Rendering Images");
			uniformSignalImg = SignalProcessing.renderSignalWithTime(ts.getValues(), ts.getTime(), "Uniformly Sampled Time Series", chartpattern);
			if (uniformSignalImg == null)
				outputParameters.put("Note:", "The charts for uniformly sampled and forecasted signals contain too many points and will not be displayed. The values will be only reported in the output file.");
			else
				outputParameters.put("Note:", "Details about the values are reported in the output file.");

			uniformSignalSamplesImg = SignalProcessing.renderSignalWithGenericTime(ts.getValues(), 0, 1, "Uniformly Sampled Time Series in Samples");
			spectrogramImg = SignalProcessing.renderSignalSpectrogram2(pd.currentspectrum);
			int timeseriesV = ts.getValues().length;
			double[] forecastedpiece = Arrays.copyOfRange(ssa.getForecastSignal(), timeseriesV, timeseriesV + pointsToReconstruct);
			List<String> tsnames = new ArrayList<String>();
			tsnames.add("Original Time Series");
			tsnames.add("Forecasted Time Series");
			List<double[]> signals = new ArrayList<double[]>();
			signals.add(ts.getValues());
			signals.add(forecastedpiece);
			forecastsignalImg = SignalProcessing.renderSignalsWithTime(signals, newtimes, tsnames, chartpattern);
			if (display) {
				SignalProcessing.displaySignalsWithTime(signals, newtimes, tsnames, chartpattern);
			}
			double[] eigenValues = new double[ssa.getPercentList().size()];
			for (int i = 0; i < eigenValues.length; i++) {
				eigenValues[i] = ssa.getPercentList().get(i);
			}
			eigenValuesImg = SignalProcessing.renderSignalWithGenericTime(eigenValues, 0f, 1, "SSA Eigenvalues");
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Images Rendered");
			System.gc();
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Producing Files");
			outputfilename = new File(config.getPersistencePath(), valuescolum + "_SignalProcessing.csv");
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputfilename));
			bw.write("Uniformly Sampled Time Series,Time Line,Forecasted Time Series,SSA Eigenvalues\n");
			int[] lengthsVector = { ts.getValues().length, newtimes.length, ssa.getForecastSignal().length, eigenValues.length };
			int maxLen = Operations.getMax(lengthsVector);
			for (int i = 0; i < maxLen; i++) {
				if (i < ts.getValues().length)
					bw.write("" + ts.getValues()[i] + ",");
				else
					bw.write(",");
				if (i < newtimes.length)
					bw.write("" + newtimes[i] + ",");
				else
					bw.write(",");
				if (i < ssa.getForecastSignal().length)
					bw.write("" + ssa.getForecastSignal()[i] + ",");
				else
					bw.write(",");
				if (i < eigenValues.length)
					bw.write("" + eigenValues[i] + ",");
				else
					bw.write(",");
				bw.write("\n");
			}
			bw.close();
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Files Produced");
			if (display) {
				SignalProcessing.displaySignalWithTime(ts.getValues(), ts.getTime(), "Uniformly Sampled Time Series", chartpattern);
				SignalProcessing.displaySignalWithGenericTime(ts.getValues(), 0, 1, "Uniformly Sampled Time Series in Samples");
				SignalProcessing.displaySignalWithTime(ssa.getForecastSignal(), newtimes, "Forecasted Time Series", chartpattern);
				SignalProcessing.displaySignalWithGenericTime(eigenValues, 0f, 1, "SSA Eigenvalues");
			}
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->" + outputParameters);
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Computation has finished");
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		} finally {
			if (dbconnection != null)
				dbconnection.close();
		}

	}

	@Override
	protected void setInputParameters() {
		// the time series table
		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.TIMESERIES);
		InputTable p = new InputTable(templates, timeSeriesTable, "The table containing the time series", "timeseries");
		inputs.add(p);
		ColumnType p1 = new ColumnType(timeSeriesTable, valuesColumn, "The column containing the values of the time series", "values", false);
		inputs.add(p1);
		// addDoubleInput(fftwindowsamples, "The number of samples precision in detecting the period. The lower this number the less the number of points in the Spectrogram (higher number of samples used at each step). Reducing this, the spectrogram will be finer and sharper, but you should tune it. Too many samples will make the Spectrogram noisy.", "1");
		addIntegerInput(fftwindowsamples, "The number of samples N on which the Fourier Transform (FFT) will be extracted. It should be a power of two and less than the signal length, otherwise it will be automatically recalculated. The FFT will be calculated every N/2 samples, taking N samples each time. The spectrogram will display the FFT on the slices of N samples.", "12");
		addEnumerateInput(AggregationFunctions.values(), aggregationFunction, "Function to apply to samples with the same time instant", AggregationFunctions.SUM.name());
		addEnumerateInput(Sensitivity.values(), sensitivityParam, "Sensitivity to the frequency components. High sensitivity will report all the frequency components, low sensitivity will report only the most distant ones.", Sensitivity.LOW.name());
		addIntegerInput(SSAAnalysisWindowSamples, "The number of samples in the produced uniformly sampled signal, to use in the SSA algorithm. Must be strictly less than the Time Series length. This number should identify a portion of the signal long enough to make the system guess the nature of the trend", "20");
		addDoubleInput(SSAEigenvaluesThreshold, "The threshold under which an SSA eigenvalue will be ignored, along with its eigenvector, for the reconstruction of the signal", "0.7");
		addIntegerInput(SSAPointsToForecast, "The number of points to forecast over the original length of the time series", "10");
		DatabaseType.addDefaultDBPars(inputs);
	}

	@Override
	public StatisticalType getOutput() {

		LinkedHashMap<String, StatisticalType> outMap = PrimitiveType.stringMap2StatisticalMap(outputParameters);
		LinkedHashMap<String, Image> producedImages = new LinkedHashMap<String, Image>();
		if (signalImg != null)
			producedImages.put("Original Time Series", signalImg);
		if (uniformSignalImg != null)
			producedImages.put("Uniformly Sampled Time Series", uniformSignalImg);
		if (uniformSignalSamplesImg != null)
			producedImages.put("Uniformly Sampled Time Series in Samples", uniformSignalSamplesImg);
		if (forecastsignalImg != null)
			producedImages.put("Forecasted Time Series", forecastsignalImg);
		if (spectrogramImg != null)
			producedImages.put("Spectrogram of the Uniformly Sampled Time Series", spectrogramImg);
		if (eigenValuesImg != null)
			producedImages.put("SSA Eigenvalues", eigenValuesImg);

		PrimitiveType images = new PrimitiveType(HashMap.class.getName(), producedImages, PrimitiveTypes.IMAGES, "Time Series Report", "Charts reporting the Time Series Analysis");
		outMap.put("Images", images);
		if (outputfilename != null) {
			PrimitiveType file = new PrimitiveType(File.class.getName(), outputfilename, PrimitiveTypes.FILE, "AnalysisReport", "AnalysisReport");
			outMap.put("Analysis Report", file);
		}
		PrimitiveType p = new PrimitiveType(LinkedHashMap.class.getName(), outMap, PrimitiveTypes.MAP, "Output", "");

		return p;
	}

	@Override
	public void shutdown() {

	}

}
