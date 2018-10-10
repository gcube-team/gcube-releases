package org.gcube.dataanalysis.ecoengine.signals;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JPanel;

import marytts.signalproc.display.SpectrogramCustom;
import marytts.signalproc.window.Window;

import org.gcube.contentmanagement.graphtools.data.BigSamplesTable;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.transducers.TimeSeriesAnalysis;
import org.gcube.dataanalysis.ecoengine.utils.Operations;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.MemoryExampleTable;

/**
 * includes tools for basic signal transformations: delta + double delta center frequency cepstral coefficients calculation spectrum frequency cut transformation to and from Rapid Miner Example Set filterbanks fequency to mel frequency to index in fft sinusoid signal generation inverse mel log10 mel filterbanks sample to time and time to sample signal timeline generation index to time in spectrogram spectrogram calculation and display time to index in spectrogram
 * 
 * @author coro
 * 
 */
public class SignalConverter {

	public static double[][] addDeltaDouble(double[][] features) throws Exception {
		int vectorL = features[0].length;
		double[][] delta = new double[features.length][features[0].length * 3];

		for (int k = 0; k < features.length; k++) {
			for (int g = 0; g < vectorL; g++) {
				delta[k][g] = features[k][g];
			}
		}

		Delta.calcDelta(delta, vectorL);
		Delta.calcDoubleDelta(delta, vectorL);

		return delta;
	}

	public static double centerFreq(int i, double samplingRate, double lowerFilterFreq, int numMelFilters) {
		double mel[] = new double[2];
		mel[0] = freqToMel(lowerFilterFreq);
		mel[1] = freqToMel(samplingRate / 2);

		// take inverse mel of:
		double temp = mel[0] + ((mel[1] - mel[0]) / (numMelFilters + 1)) * i;
		return inverseMel(temp);
	}

	public static double[] cepCoefficients(double f[], int numCepstra, int numFilters) {
		double cepc[] = new double[numCepstra];

		for (int i = 0; i < cepc.length; i++) {
			for (int j = 1; j <= numFilters; j++) {
				cepc[i] += f[j - 1] * Math.cos(Math.PI * i / numFilters * (j - 0.5));
			}
		}

		return cepc;
	}

	public static BufferedImage createImage(JPanel panel, int w, int h) {

		// int w = panel.getWidth();
		// int h = panel.getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		panel.paint(g);
		return bi;
	}

	public static double[][] cutSpectrum(double[][] spectrum, float minFreq, float maxfreq, int fftWindowSize, int samplingRate) {
		int minFrequencyIndex = frequencyIndex(minFreq, fftWindowSize, samplingRate);
		int maxFrequencyIndex = frequencyIndex(maxfreq, fftWindowSize, samplingRate);

		double[][] cutSpectrum = new double[spectrum.length][maxFrequencyIndex - minFrequencyIndex + 1];

		for (int i = 0; i < spectrum.length; i++) {
			cutSpectrum[i] = Arrays.copyOfRange(spectrum[i], minFrequencyIndex, maxFrequencyIndex);
		}

		return cutSpectrum;
	}

	public static void exampleSet2Signal(double[] rebuiltSignal, ExampleSet es, Double fillerValueFormissingEntries) {

		MemoryExampleTable met = (MemoryExampleTable) es.getExampleTable();
		int numCol = met.getAttributeCount();
		int numRows = met.size();

		Attribute labelAtt = met.getAttribute(numCol - 1);

		for (int i = 0; i < numRows; i++) {
			int index = (int) met.getDataRow(i).get(labelAtt);
			String label = labelAtt.getMapping().mapIndex(index);
			int id = Integer.parseInt(label);
			Example e = es.getExample(i);
			// System.out.println(es.getExample(i)+"->"+signal[i]);
			for (Attribute a : e.getAttributes()) {
				Double value = e.getValue(a);
				if (value.equals(Double.NaN) && !fillerValueFormissingEntries.equals(Double.NaN))
					value = fillerValueFormissingEntries;

				rebuiltSignal[id] = value;
			}
		}
	}

	public static void exampleSet2Signal(double[] rebuiltSignal, ExampleSet es) {
		exampleSet2Signal(rebuiltSignal, es, null);
	}

	public static int[] fftBinIndices(double samplingRate, int frameSize, int numMelFilters, int numFequencies, float lowerFilterFreq, float upperFilterFreq) {
		int cbin[] = new int[numFequencies + 2];
		AnalysisLogger.getLogger().debug("New Filter banks: " + numFequencies);
		cbin[0] = (int) Math.round(lowerFilterFreq / samplingRate * frameSize);
		cbin[cbin.length - 1] = frequencyIndex(upperFilterFreq, frameSize, (float) samplingRate);
		AnalysisLogger.getLogger().debug("F0: " + lowerFilterFreq);
		for (int i = 1; i <= numFequencies; i++) {
			double fc = centerFreq(i, samplingRate, lowerFilterFreq, numMelFilters);
			AnalysisLogger.getLogger().debug("F" + (i) + ": " + fc);
			cbin[i] = (int) Math.round(fc / samplingRate * frameSize);
		}

		AnalysisLogger.getLogger().debug("F" + (cbin.length - 1) + ": " + upperFilterFreq);

		return cbin;
	}

	public static int[] fftBinIndices(double samplingRate, int frameSize, int numMelFilters, float lowerFilterFreq) {
		int cbin[] = new int[numMelFilters + 2];

		cbin[0] = (int) Math.round(lowerFilterFreq / samplingRate * frameSize);
		cbin[cbin.length - 1] = (int) (frameSize / 2);

		for (int i = 1; i <= numMelFilters; i++) {
			double fc = centerFreq(i, samplingRate, lowerFilterFreq, numMelFilters);

			cbin[i] = (int) Math.round(fc / samplingRate * frameSize);
		}

		return cbin;
	}

	public static double freqToMel(double freq) {
		return 2595 * log10(1 + freq / 700);
	}

	public static int frequencyIndex(float frequency, int fftSize, float samplingRate) {
		return Math.round(frequency * fftSize / samplingRate);
	}

	public static double[] generateSinSignal(int signalLength, float timeShift, float frequency) {
		// final float frequency = 0.3f;// 1f;

		double samples[] = new double[signalLength];
		float time = 0;
		for (int i = 0; i < samples.length; i++) {
			samples[i] = (float) Math.sin(2f * Math.PI * frequency * time);
			// time += 1f / (float) samplingRate;
			time += timeShift;
		}
		return samples;
	}

	public static double inverseMel(double x) {
		double temp = Math.pow(10, x / 2595) - 1;
		return 700 * (temp);
	}

	public static double log10(double value) {
		return Math.log(value) / Math.log(10);
	}

	public static double[] melFilter(double bin[], int cbin[], int numMelFilters) {
		double temp[] = new double[numMelFilters + 2];

		for (int k = 1; k <= numMelFilters; k++) {
			double num1 = 0, num2 = 0;

			for (int i = cbin[k - 1]; i <= cbin[k]; i++) {
				num1 += ((i - cbin[k - 1] + 1) / (cbin[k] - cbin[k - 1] + 1)) * bin[i];
			}

			for (int i = cbin[k] + 1; i <= cbin[k + 1]; i++) {
				num2 += (1 - ((i - cbin[k]) / (cbin[k + 1] - cbin[k] + 1))) * bin[i];
			}

			temp[k] = num1 + num2;
		}

		double fbank[] = new double[numMelFilters];
		for (int i = 0; i < numMelFilters; i++) {
			fbank[i] = temp[i + 1];
		}

		return fbank;
	}

	public static int recalculateMaxMelFilters(double samplingRate, int numMelFilters, float lowerFilterFreq, float maxFilterFreq) {
		int bestIndex = 1;
		for (int i = 1; i <= numMelFilters; i++) {
			double fc = centerFreq(i, samplingRate, lowerFilterFreq, numMelFilters);
			AnalysisLogger.getLogger().debug("fc " + fc);
			if (fc > maxFilterFreq) {
				bestIndex = i;
				break;
			}
		}

		return bestIndex - 1;
	}

	public static double sample2Time(int sample, int sampleRate) {
		return (double) sample / (double) sampleRate;
	}

	public static double[] signalTimeLine(int signalLength, double samplingRate) {
		double time[] = new double[signalLength];
		Arrays.fill(time, Double.NaN);

		for (int i = 0; i < signalLength; i++) {
			time[i] = (double) i / (double) samplingRate;
		}
		AnalysisLogger.getLogger().debug("time " + time[signalLength - 1] * samplingRate + " vs " + signalLength);
		return time;
	}

	public static float spectrumTime(float linearTime, float windowShiftTime) {
		return linearTime / windowShiftTime;
	}

	public static ExampleSet signal2ExampleSet(double[] signal) {
		BigSamplesTable samples = new BigSamplesTable();
		for (int k = 0; k < signal.length; k++) {
			samples.addSampleRow("" + k, signal[k]);
		}
		AnalysisLogger.getLogger().debug("Example Set Created");
		return samples.generateExampleSet();
	}

	public static double[][] spectrogram(String name, double[] signal, int samplingRate, int windowshift, int frameslength, boolean display) throws Exception {
		SpectrogramCustom spec = new SpectrogramCustom(signal, samplingRate, Window.get(Window.HAMMING, frameslength), windowshift, frameslength, 640, 480);
		double[][] spectrum = spec.spectra.toArray(new double[spec.spectra.size()][]);
		if (display) {
			spec.showInJFrame(name, true, true);
			/*
			 * save spectrograms to files BufferedImage image = createImage(spec); ImageIO.write(ImageTools.toBufferedImage(image), "png", new File(name+".png"));
			 */
			// Thread.sleep(2000);
			// createImage(spec);
		}
		return spectrum;
	}

	public static void displaySpectrogram(double[][] spectrum, double[] signal, String name, int samplingRate, int windowshift, int frameslength) throws Exception {

		SpectrogramCustom spec = new SpectrogramCustom(signal, samplingRate, Window.get(Window.HAMMING, frameslength), windowshift, frameslength, 640, 480);

		spec.spectra = new ArrayList<double[]>();
		for (int i = 0; i < spectrum.length; i++) {
			spec.spectra.add(spectrum[i]);
		}
		spec.showInJFrame(name, true, true);

	}

	public static float spectrogramTimeFromIndex(int index, float windowShiftTime) {
		return index * windowShiftTime;
	}

	public static int spectrogramIndex(float linearTime, float windowShiftTime) {
		return (int) (linearTime / windowShiftTime);
	}

	public static int time2Sample(double time, int sampleRate) {
		return (int) (time * sampleRate);
	}

	public double[] averagepower;

	public double[] takeMaxFrequenciesInSpectrogram(double[][] spectrogram, int samplingRate, int windowSamples, float minfreq) {
		double[] maxs = new double[spectrogram.length];
		averagepower = new double[spectrogram.length];
		int j = 0;
		if (TimeSeriesAnalysis.display){
			for (int g=0;g<spectrogram.length;g++){
				SignalProcessing.displaySignalWithGenericTime(spectrogram[g], 0, 1, "spectrum "+(g+1));
			}
		}
		double tolerance = 0.05;
		for (double[] slice : spectrogram) {
			int bestidx = 0;
			double max = -Double.MAX_VALUE;
			double min = Double.MAX_VALUE;
			//take the best frequency over the first minimum in the spectrum
			boolean overfirstmin = false;
			for (int k = 1; k < slice.length; k++) {
				double ele = slice[k];
				if (!overfirstmin && (slice[k]>slice[k-1])){
					 AnalysisLogger.getLogger().debug("First minimum in spectrum is at idx "+k);
					overfirstmin=true;
				}
				if (overfirstmin) {
					if (ele > (max + (Math.abs(max) * tolerance))) {
//						 AnalysisLogger.getLogger().debug(">max up:"+ele +">" +(max + (Math.abs(max) * tolerance))+" at idx "+k);
						max = ele;
						bestidx = k;
					}
					if (ele < (min - (Math.abs(min) * tolerance))) {
						min = ele;
					}
				}
			}

			// maxs[j] = spectrogram[j][bestidx];
			// maxs[j]=bestidx;

			int minFidx = SignalConverter.frequencyIndex(minfreq, windowSamples, samplingRate);
			// System.out.println("min f idx: "+minFidx);
			maxs[j] = spectrumIdx2Frequency(minFidx + bestidx, samplingRate, windowSamples);
			double mean = org.gcube.contentmanagement.graphtools.utils.MathFunctions.mean(slice);
			AnalysisLogger.getLogger().debug("max freq in spec: " + maxs[j]+" index "+minFidx + bestidx);
			if (min == Double.MAX_VALUE) {
				min = max;
			}
			if (max == -Double.MAX_VALUE) {
				averagepower[j] = 0;
			} else {
				max = max - min;
				mean = mean - min;
				if (max == 0)
					averagepower[j] = 0;
				else
					averagepower[j] = Math.abs((max - mean) / max);
			}

			AnalysisLogger.getLogger().debug("max power : " + max + " min power: " + min + " mean " + mean + " power " + averagepower[j]);
			j++;
		}

		return maxs;
	}

	ArrayList<Double>[] currentSpikesPowerSpectra;
	public ArrayList<Double>[] takePeaksInSpectrogramFrames(double[][] spectrogram, int samplingRate, int windowSamples, float minfreq) {
		
		ArrayList<Double>[] maxs = new ArrayList[spectrogram.length];
		ArrayList<Double>[] powers = new ArrayList[spectrogram.length];
		
		if (TimeSeriesAnalysis.display){
			for (int g=0;g<spectrogram.length;g++){
				SignalProcessing.displaySignalWithGenericTime(spectrogram[g], 0, 1, "spectrum "+(g+1));
			}
		}
		int minFidx = SignalConverter.frequencyIndex(minfreq, windowSamples, samplingRate);
		
		for (int j=0;j<spectrogram.length;j++) {
			
			double[] slice = spectrogram[j];
			double maxAmp = Operations.getMax(slice);
			double minAmp = Operations.getMin(slice);
			//old code: once we used the first element of the FFT as reference, but it is unrealiable
			double refAmplitude = 0;
			if (maxAmp!=slice[0])
				refAmplitude = (slice[0]-minAmp);//(maxAmp-minAmp)/2d;
			else
				refAmplitude = MathFunctions.mean(slice)-minAmp;
			
			ArrayList<Double> maxFreqs = new ArrayList<Double>();
			ArrayList<Double> localpowers = new ArrayList<Double>();
			double [] derivSlice = MathFunctions.derivative(slice);
			boolean [] spikes = MathFunctions.findMaxima(derivSlice,0.001);
			
			for (int i=0;i<spikes.length;i++){
				if (spikes[i]){
//					AnalysisLogger.getLogger().debug("Spike at "+i);
					maxFreqs.add((double)spectrumIdx2Frequency(minFidx + i, samplingRate, windowSamples));
					//make the min correspond to y=0
					//take few samples around the spike and evaluate the amplitude with respect to the samples around
					int round =Math.max(slice.length/10,1);
					//take samples to the left
					double roundmean = 0;
					for (int g=1;g<=round;g++){
						if (i-g>=0){
							roundmean = roundmean+slice[i-g]-minAmp;
						}
					}
					//take samples to the right
					for (int g=1;g<=round;g++){
						if (i+g<slice.length){
							roundmean = roundmean+slice[i+g]-minAmp;
						}
					}
					//take mean value
					roundmean = roundmean/(2d*(double)round);
					//calculate the power as the ration between the spike and the surrounding points
					double power = (slice[i]-minAmp)/(roundmean);
					localpowers.add(power);
				}
			}
					
			powers[j]=localpowers;
			maxs[j]=maxFreqs;
		}
		currentSpikesPowerSpectra=powers;
		return maxs;
		
	}
	
	
	public int startStableTractIdx = -1;
	public int endStableTractIdx = -1;

	public double[] takeLongestStableTract(double[] signal, double valuedifftoleranceperc) {
		ArrayList<int[]> pairs = new ArrayList<int[]>();
		int idx1 = -1;

		int[] pair = null;
		// analyze the signal
		for (int i = 1; i < signal.length; i++) {
			// if there is not current range create it
			if (idx1 == -1) {
				idx1 = 1;
				pair = new int[2];
				pair[0] = i - 1;
				pair[1] = i - 1;
			}
			// if the current sample is similar to the previous, enlarge the range
			if (Math.abs(signal[i] - signal[i - 1]) / Math.max(signal[i], signal[i - 1]) <= valuedifftoleranceperc)
				pair[1] = i;
			// otherwise add the couple and reset
			else {
				idx1 = -1;
				pairs.add(pair);
			}
		}
		// if the last couple was reset, add the last interval
		if (idx1 > -1)
			pairs.add(pair);

		// find the longest pair
		int best = 0;
		int maxsize = 0;
		int k = 0;
		for (int[] setcouple : pairs) {
			int diff = setcouple[1] - setcouple[0];
			if (diff > maxsize) {
				maxsize = diff;
				best = k;
			}
			k++;
		}

		// take the longest range
		if (pairs.size() == 0) {
			pairs.add(new int[] { 0, 1 });
		}

		int[] bestcouple = pairs.get(best);
		// take the related slice of signal
		if (bestcouple[1]==bestcouple[0])
			bestcouple[1]=bestcouple[0]+1;
		double[] subsignal = new double[bestcouple[1] - bestcouple[0]];
		AnalysisLogger.getLogger().debug("Longest range: from " + bestcouple[0] + " to " + bestcouple[1]);
		startStableTractIdx = bestcouple[0];
		endStableTractIdx = bestcouple[1];

		int l = 0;
		for (int i = bestcouple[0]; i < bestcouple[1]; i++) {
			subsignal[l] = signal[i];
			l++;
		}

		return subsignal;
	}

	public static float spectrumIdx2Frequency(int idx, int samplingRate, int windowsSizeSamples) {
		return ((float) idx * samplingRate) / (1f*(float) (windowsSizeSamples - 1));
	}

	public static int spectrumFreq2Idx(float freq, int samplingRate, int windowsSizeSamples) {
		return Math.round((windowsSizeSamples - 1) * 1f *freq / samplingRate);
	}

}
