package org.gcube.dataanalysis.ecoengine.signals;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import marytts.signalproc.display.SpectrogramCustom;
import marytts.signalproc.window.Window;

import org.gcube.contentmanagement.graphtools.data.BigSamplesTable;
import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.contentmanagement.graphtools.plotting.graphs.NumericSeriesGraph;
import org.gcube.contentmanagement.graphtools.plotting.graphs.SpectrumPlot2;
import org.gcube.contentmanagement.graphtools.plotting.graphs.TimeSeriesGraph;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.MathFunctions;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeriesCollection;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.preprocessing.sampling.AbsoluteSampling;
import com.rapidminer.operator.preprocessing.series.filter.SeriesMissingValueReplenishment;
import com.rapidminer.tools.OperatorService;

public class SignalProcessing {

	public static double[][] applyFilterBank(double[][] feature, int numCepstra, int numMelFilters, int samplingRate, int frameLength, float minCutFequency, float maxCutFrequency) throws Exception {
		// recalculate Mel filters on the basis of the maxFrequency
		int recalcMelFilters = SignalConverter.recalculateMaxMelFilters(samplingRate, numMelFilters, minCutFequency, maxCutFrequency);

		double[][] mels = new double[feature.length][numCepstra];
		int i = 0;
		for (double[] bin : feature) {
			int cbin[] = SignalConverter.fftBinIndices(samplingRate, frameLength, numMelFilters, recalcMelFilters, minCutFequency, maxCutFrequency);
			double f[] = SignalConverter.melFilter(bin, cbin, recalcMelFilters);
			double cepstra[] = SignalConverter.cepCoefficients(f, numCepstra, recalcMelFilters);
			mels[i] = cepstra;
			i++;
		}

		double[][] deltamels = new double[feature.length][numCepstra * 3];
		for (int k = 0; k < feature.length; k++) {
			for (int g = 0; g < mels[0].length; g++) {
				deltamels[k][g] = mels[k][g];
			}
		}

		Delta.calcDelta(deltamels, numCepstra);
		Delta.calcDoubleDelta(deltamels, numCepstra);

		return deltamels;
	}

	public static double[][] calculateSumSpectrum(List<double[]> signals, int windowShiftSamples, int frameLength, int samplingRate) throws Exception {

		int signalLenght = signals.get(0).length;
		AnalysisLogger.getLogger().debug("TRIALS LENGHT " + signalLenght);

		List<double[][]> spectrograms = new ArrayList<double[][]>();
		AnalysisLogger.getLogger().debug("Getting Spectra");
		int j = 0;
		// get all spectrograms
		for (double[] signal : signals) {
			double[][] spectro = SignalConverter.spectrogram("Spectrogram", signal, samplingRate, windowShiftSamples, frameLength, false);
			AnalysisLogger.getLogger().debug("Signal Number " + (j + 1) + " spectrum lenght " + ((spectro.length * windowShiftSamples) / samplingRate));
			spectrograms.add(spectro);
			j++;
		}

		AnalysisLogger.getLogger().debug("Summing Spectra");
		// sum all spectrograms
		double[][] sumSpectro = SignalProcessing.sumSpectra(spectrograms);
		spectrograms = null;

		return sumSpectro;

	}

	// concatenates several spectra
	public static double[][] concatenateSpectra(List<double[][]> spectra) {
		double[][] firstSpectrum = spectra.get(0);
		int mi = firstSpectrum.length;
		int mj = firstSpectrum[0].length;
		int nSpectra = spectra.size();
		double[][] concatenatedSpectrum = new double[mi][mj * nSpectra];
		int k = 0;

		for (double[][] spectrum : spectra) {

			for (int i = 0; i < mi; i++) {
				for (int j = 0; j < mj; j++)
					concatenatedSpectrum[i][j + (k * mj)] = spectrum[i][j];
			}
			k++;
		}

		return concatenatedSpectrum;
	}

	public static void displaySignalWithGenericTime(double[] signal, float t0, float timeshift, String name) {
		org.jfree.data.xy.XYSeries xyseries = new org.jfree.data.xy.XYSeries(name);
		float time = t0;
		for (int i = 0; i < signal.length; i++) {
			xyseries.add(time, signal[i]);
			time = time + timeshift;
		}
		XYSeriesCollection collection = new XYSeriesCollection(xyseries);
		NumericSeriesGraph nsg = new NumericSeriesGraph(name);
		nsg.render(collection);
	}

	public static Image renderSignalWithGenericTime(double[] signal, float t0, float timeshift, String name) {
		if (signal.length > 20000) {
			AnalysisLogger.getLogger().debug("Too many points to display: " + signal.length);
			return null;
		}
		org.jfree.data.xy.XYSeries xyseries = new org.jfree.data.xy.XYSeries(name);
		float time = t0;
		for (int i = 0; i < signal.length; i++) {
			xyseries.add(time, signal[i]);
			time = time + timeshift;
		}
		XYSeriesCollection collection = new XYSeriesCollection(xyseries);
		JFreeChart chart = NumericSeriesGraph.createStaticChart(collection);
		Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));
		return image;
	}

	public static Image renderSignalWithGenericTime(double[] signal, double[] timeline, String name) {
		if (signal.length > 20000) {
			AnalysisLogger.getLogger().debug("Too many points to display: " + signal.length);
			return null;
		}

		org.jfree.data.xy.XYSeries xyseries = new org.jfree.data.xy.XYSeries(name);

		for (int i = 0; i < signal.length; i++) {
			xyseries.add(timeline[i], signal[i]);
		}

		XYSeriesCollection collection = new XYSeriesCollection(xyseries);
		JFreeChart chart = NumericSeriesGraph.createStaticChart(collection);
		Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));
		return image;
	}

	public static Image renderSignalWithTime(double[] signal, Date[] dates, String name, String format) {
		org.jfree.data.time.TimeSeries series = new org.jfree.data.time.TimeSeries(name);
		if (signal.length > 20000) {
			AnalysisLogger.getLogger().debug("Too many points to display: " + signal.length);
			return null;
		}
		for (int i = 0; i < signal.length; i++) {
			try {
				FixedMillisecond ms = new FixedMillisecond(dates[i]);
				series.add(ms, signal[i]);
			} catch (Exception e) {
				AnalysisLogger.getLogger().debug("Skipping value yet present: " + dates[i]);
			}
		}
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(series);
		JFreeChart chart = TimeSeriesGraph.createStaticChart(dataset, format,name);
		Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));
		return image;
	}

	public static Image renderSignalsWithTime(List<double[]> signals, Date[] dates, List<String> names, String format) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		int nsignals = signals.size();
		for (int j = 0; j < nsignals; j++) {
			double[] signal = signals.get(j);
			String name = names.get(j);

			org.jfree.data.time.TimeSeries series = new org.jfree.data.time.TimeSeries(name);
			if (signal.length > 20000) {
				AnalysisLogger.getLogger().debug("Too many points to display: " + signal.length);
				return null;
			}
			int offset = 0;
			if (j>0)
				offset = signals.get(j-1).length;
			
			for (int i = offset; i < offset+signal.length; i++) {
				try {
					FixedMillisecond ms = new FixedMillisecond(dates[i]);
					series.add(ms, signal[i-offset]);
				} catch (Exception e) {
					AnalysisLogger.getLogger().debug("Skipping value yet present: " + dates[i]);
				}
			}
			dataset.addSeries(series);
		}

		JFreeChart chart = TimeSeriesGraph.createStaticChart(dataset, format);
		Image image = ImageTools.toImage(chart.createBufferedImage(680, 420));
		return image;
	}

	public static Image renderSignalSpectrogram(double[] signal, double[] timeline, int samplingRate, int frameslength, int windowshift) {
		SpectrogramCustom spec = new SpectrogramCustom(signal, samplingRate, Window.get(Window.HAMMING, frameslength), windowshift, frameslength, 640, 480);
		double[][] spectrum = spec.spectra.toArray(new double[spec.spectra.size()][]);
		spec.setZoomX(640d / (double) spectrum.length);
		BufferedImage image = SignalConverter.createImage(spec, 640, 480);
		return ImageTools.toImage(image);
	}

	public static Image renderSignalSpectrogram2(double[][] spectrogram) {
		SpectrumPlot2 spectrumPlot = new SpectrumPlot2(spectrogram);
		AnalysisLogger.getLogger().debug("Spectrum W:" + spectrumPlot.width);
		AnalysisLogger.getLogger().debug("Spectrum H:" + spectrumPlot.height);
		// spectrumPlot.hzoomSet(2f);
		spectrumPlot.hzoomSet(640f / (float) spectrumPlot.width);
		spectrumPlot.vzoomSet(480f / (float) spectrumPlot.height);
		/*
		 * ApplicationFrame app = new ApplicationFrame("Spectrogram "); app.setContentPane(spectrumPlot); app.pack(); app.setVisible(true);
		 */
		BufferedImage image = SignalConverter.createImage(spectrumPlot, 640, 480);
		return ImageTools.toImage(image);

	}

	public static void displaySignalWithTime(double[] signal, Date[] dates, String name, String format) {
		org.jfree.data.time.TimeSeries series = new org.jfree.data.time.TimeSeries(name);
		if (signal.length > 20000) {
			AnalysisLogger.getLogger().debug("Too many points to display: " + signal.length);
			return;
		}
		for (int i = 0; i < signal.length; i++) {
			try {
				FixedMillisecond ms = new FixedMillisecond(dates[i]);
				series.add(ms, signal[i]);
			} catch (Exception e) {
				AnalysisLogger.getLogger().debug("Skipping value yet present: " + dates[i]);
			}
		}
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(series);

		TimeSeriesGraph tsg = new TimeSeriesGraph(name);
		tsg.timeseriesformat = format;
		tsg.render(dataset);
	}

	public static void displaySignalsWithTime(List<double[]> signals, Date[] dates, List<String> names, String format) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		int nsignals = signals.size();
		for (int j = 0; j < nsignals; j++) {
			double[] signal = signals.get(j);
			String name = names.get(j);

			org.jfree.data.time.TimeSeries series = new org.jfree.data.time.TimeSeries(name);
			if (signal.length > 20000) {
				AnalysisLogger.getLogger().debug("Too many points to display: " + signal.length);
				return ;
			}
			int offset = 0;
			if (j>0)
				offset = signals.get(j-1).length;
			
			for (int i = offset; i < offset+signal.length; i++) {
				try {
					FixedMillisecond ms = new FixedMillisecond(dates[i]);
					series.add(ms, signal[i-offset]);
				} catch (Exception e) {
					AnalysisLogger.getLogger().debug("Skipping value yet present: " + dates[i]);
				}
			}
			dataset.addSeries(series);
		}

		TimeSeriesGraph tsg = new TimeSeriesGraph("Multiple Time Series");
		tsg.timeseriesformat = format;
		tsg.render(dataset);

	}
	
	public static double[] downSample(double[] signal, int numElements, AlgorithmConfiguration config) throws Exception {
		config.initRapidMiner();
		double[] rebuiltSignal = new double[signal.length];
		Arrays.fill(rebuiltSignal, Double.NaN);
		BigSamplesTable samples = new BigSamplesTable();
		for (int k = 0; k < signal.length; k++) {
			samples.addSampleRow("" + k, signal[k]);
		}
		ExampleSet es = samples.generateExampleSet();

		AnalysisLogger.getLogger().debug("Example Set Created");

		AbsoluteSampling sampler = (AbsoluteSampling) OperatorService.createOperator("AbsoluteSampling");
		sampler.setParameter("sample_size", "" + numElements);
		sampler.setParameter("local_random_seed", "-1");
		es = sampler.apply(es);
		AnalysisLogger.getLogger().debug("Finished");

		SignalConverter.exampleSet2Signal(rebuiltSignal, es);

		return rebuiltSignal;
	}

	public static double[][] extractSumSpectrum(String file, int windowShiftSamples, int frameLength, int samplingRate) throws Exception {
		List<double[]> signals = SignalProcessing.readSignalsFromCSV(file, ",");
		// int numSignals = signals.size();
		int signalLenght = signals.get(0).length;

		List<double[][]> sumspectrograms = new ArrayList<double[][]>();

		List<double[][]> spectrograms = new ArrayList<double[][]>();
		AnalysisLogger.getLogger().debug("Getting Spectra");
		int j = 0;
		// get all spectrograms
		for (double[] signal : signals) {
			AnalysisLogger.getLogger().debug("Signal Number " + (j + 1));
			double[][] spectro = SignalConverter.spectrogram("Spectrogram", signal, samplingRate, windowShiftSamples, frameLength, false);
			spectrograms.add(spectro);
			j++;
		}

		AnalysisLogger.getLogger().debug("Summing Spectra");
		// sum all spectrograms
		double[][] sumSpectro = SignalProcessing.sumSpectra(spectrograms);
		spectrograms = null;

		return sumSpectro;
	}

	public static Date[] fillTimeLine(double[] timemilliseconds, double samplingRate, AlgorithmConfiguration config) throws Exception {
		double[] milliseconds = fillTimeSeries(timemilliseconds, timemilliseconds, samplingRate, config);
		Date[] dates = new Date[milliseconds.length];
		for (int i = 0; i < milliseconds.length; i++)
			dates[i] = new Date((long) milliseconds[i]);

		return dates;
	}

	public static double[] fillSignal(double[] signal) throws Exception {
		ExampleSet es = SignalConverter.signal2ExampleSet(signal);
		SeriesMissingValueReplenishment sampler = (SeriesMissingValueReplenishment) OperatorService.createOperator("SeriesMissingValueReplenishment");
		sampler.setParameter("attribute_name", "att0");
		sampler.setParameter("replacement", "3");
		es = sampler.apply(es);
		AnalysisLogger.getLogger().debug("Finished");
		double[] rebuiltSignal = new double[signal.length];
		SignalConverter.exampleSet2Signal(rebuiltSignal, es, 0d);

		return rebuiltSignal;
	}

	public static double[] fillTimeSeries(double[] values, double[] timeseconds, double samplingRate, AlgorithmConfiguration config) throws Exception {

		double t0 = timeseconds[0];
		double t1 = timeseconds[timeseconds.length - 1];
		int signalength = Math.abs((int) ((t1 - t0) * samplingRate) + 1);
		AnalysisLogger.getLogger().debug("SignalProcessing->Old Time Series had: " + values.length + " samples. New Time Series will have: " + signalength + " samples");
		if (values.length == signalength)
			return values;

		config.initRapidMiner();
		double signal[] = new double[signalength];
		Arrays.fill(signal, Double.NaN);
		for (int i = 0; i < values.length; i++) {
			if (values[i] != Double.NaN) {
				int index = Math.abs((int) ((timeseconds[i] - t0) * samplingRate));
				signal[index] = values[i];
			}
		}
		double[] rebuiltSignal = new double[signal.length];

		BigSamplesTable samples = new BigSamplesTable();
		for (int k = 0; k < signal.length; k++) {
			samples.addSampleRow("" + k, signal[k]);
		}

		ExampleSet es = samples.generateExampleSet();
		AnalysisLogger.getLogger().debug("Example Set Created");

		SeriesMissingValueReplenishment sampler = (SeriesMissingValueReplenishment) OperatorService.createOperator("SeriesMissingValueReplenishment");
		sampler.setParameter("attribute_name", "att0");
		sampler.setParameter("replacement", "3");
		es = sampler.apply(es);
		AnalysisLogger.getLogger().debug("Finished");

		SignalConverter.exampleSet2Signal(rebuiltSignal, es);

		return rebuiltSignal;
	}

	public static double[][] multiSignalAnalysis(List<double[]> signals, int samplingRate, int windowshift, int frameslength, boolean display) throws Exception {

		List<double[][]> spectra = new ArrayList<double[][]>();

		for (double[] signal : signals)
			spectra.add(SignalConverter.spectrogram("Spectrogram", signal, samplingRate, windowshift, frameslength, display));

		double[][] sumSpec = sumSpectra(spectra);
		return sumSpec;

	}

	public static List<double[]> readSignalsFromCSV(String file, String delimiter) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = br.readLine();
		List<double[]> signals = new ArrayList<double[]>();
		while (line != null) {
			double[] signal = readSignalFromCSVLine(line, delimiter);
			signals.add(signal);
			line = br.readLine();
		}

		br.close();
		return signals;
	}

	public static double[] readSignalFromCSVLine(String line, String delimiter) throws Exception {
		String[] splitted = line.split(delimiter);
		double[] signal = new double[splitted.length];
		for (int i = 0; i < splitted.length; i++)
			signal[i] = Double.parseDouble(splitted[i]);
		return signal;
	}

	// sums several spectra of the same length
	public static double[][] sumSpectra(List<double[][]> spectra) {
		double[][] firstSpectrum = spectra.get(0);
		int mi = firstSpectrum.length;
		int mj = firstSpectrum[0].length;
		double[][] sumSpectrum = new double[mi][mj];
		int k = 0;
		for (double[][] spectrum : spectra) {
			for (int i = 0; i < mi; i++) {
				for (int j = 0; j < mj; j++)
					sumSpectrum[i][j] = MathFunctions.incrementAvg(sumSpectrum[i][j], spectrum[i][j], k);
			}
			k++;
		}
		return sumSpectrum;
	}

	public static double[][] takeCentralSpectrum(double[][] spectrum, float numOfCentralSeconds, float windowShiftTime, int sampleRate) {

		float maxTime = ((float) spectrum.length * (float) windowShiftTime);

		float centralTime = (maxTime / (2f * numOfCentralSeconds));

		AnalysisLogger.getLogger().debug("Max Time in the Spectrum " + maxTime + " Central time " + centralTime);

		int startIndex = (int) (centralTime / windowShiftTime);
		int endIndex = (int) ((centralTime + numOfCentralSeconds) / windowShiftTime);

		AnalysisLogger.getLogger().debug("Recalculated lenght " + maxTime + " sec");
		AnalysisLogger.getLogger().debug("Lenght " + spectrum.length);

		AnalysisLogger.getLogger().debug("Start " + startIndex + " End " + endIndex + " max " + spectrum.length + " Cut lenght " + (endIndex - startIndex + 1) * windowShiftTime);

		double[][] cutSpectrum = new double[endIndex - startIndex + 1][spectrum[0].length];

		for (int i = startIndex; i <= endIndex; i++) {
			cutSpectrum[i - startIndex] = spectrum[i];
		}

		return cutSpectrum;
	}

	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration conf = new AlgorithmConfiguration();
		conf.setConfigPath("./cfg/");
		conf.initRapidMiner();
		// double[] signal = new double[] {1,2,Double.NaN,4,5};
		double[] signal = new double[] { Double.NaN, 1, 2, 3, 4, 5, Double.NaN };
		// double[] signal = new double[] {Double.NaN,Double.NaN,Double.NaN};
		// double[] signal = new double[] {Double.NaN,Double.NaN,0};
		double[] resignal = fillSignal(signal);
		System.out.println(resignal);
	}
}
