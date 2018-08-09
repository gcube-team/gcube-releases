package org.gcube.dataanalysis.ecoengine.signals;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class PeriodicityDetector {

	/*
	 * static int defaultSamplingRate = 1000;// Hz static float defaultSignalLengthTimeinSec = 5;// s static float defaultHiddenFrequency = 100f;// Hz static float defaultMinPossibleFreq = 0; // Hz static float defaultMaxPossibleFreq = 200; // Hz static float defaultSNratio = 2; static float defaultFreqError = 1f;
	 */
	static int defaultSamplingRate = 8000;// Hz
	static float defaultSignalLengthTimeinSec = 5;// s
	static float defaultHiddenFrequency = 2f;// Hz
	static float defaultMinPossibleFreq = 0; // Hz
	static float defaultMaxPossibleFreq = 1000; // Hz
	static float defaultSNratio = 0;
	static float defaultFreqError = 1f;

	public int currentSamplingRate;
	public int currentWindowShiftSamples;
	public int currentWindowAnalysisSamples;
	public double[][] currentspectrum;

	public double meanF = 0;
	public double lowermeanF = 0;
	public double uppermeanF = 0;

	public double meanPeriod = 0;
	public double lowermeanPeriod = 0;
	public double uppermeanPeriod = 0;

	public double startPeriodTime = 0;
	public double endPeriodTime = 0;
	public double startPeriodSampleIndex = 0;
	public double endPeriodSampleIndex = 0;

	public double periodicityStrength = 0;
	public double minFrequency;
	public double maxFrequency;

	public String getPeriodicityStregthInterpretation() {
		if (periodicityStrength > 0.6)
			return "High";
		if (periodicityStrength <= 0.6 && periodicityStrength > 0.5)
			return "Moderate";
		if (periodicityStrength <= 0.5 && periodicityStrength > 0.3)
			return "Weak";
		if (periodicityStrength >= 0.3)
			return "Very Low";
		else
			return "None";
	}

	public String getPowerSpectrumStregthInterpretation(double powerStrength) {

		if (powerStrength > 3)
			return "High";
		if (powerStrength <= 3 && powerStrength > 2.5)
			return "Moderate";
		if (powerStrength <= 2.5 && powerStrength > 2)
			return "Weak";
		if (powerStrength >= 1.4)
			return "Very Low";
		else
			return "None";
	}
	
	public void demo() throws Exception {

		double[] signal = produceNoisySignal(defaultSignalLengthTimeinSec, defaultSamplingRate, defaultHiddenFrequency, defaultSNratio);
		AnalysisLogger.getLogger().debug("Signal samples: " + signal.length);
		double F = detectFrequency(signal, defaultSamplingRate, defaultMinPossibleFreq, defaultMaxPossibleFreq, defaultFreqError, -1, true);
		AnalysisLogger.getLogger().debug("Detected F:" + F + " indecision [" + lowermeanF + " , " + uppermeanF + "]");
	}

	public static void main(String[] args) throws Exception {

		PeriodicityDetector processor = new PeriodicityDetector();
		processor.demo();
	}

	public double[] produceNoisySignal(float signalLengthTimeinSec, int samplingRate, float frequency, float SNratio) {

		// generate a signal with the above period
		double[] sin = SignalConverter.generateSinSignal((int) signalLengthTimeinSec * samplingRate, 1f / samplingRate, frequency);

		// add noise
		for (int i = 0; i < sin.length; i++) {
			sin[i] = sin[i] + SNratio * Math.random();
		}
		return sin;
	}

	public double detectFrequency(double[] signal, boolean display) throws Exception {

		return detectFrequency(signal, 1, 0, 1, 1f, -1, display);
	}

	public double detectFrequency(double[] signal) throws Exception {

		return detectFrequency(signal, false);
	}

	public double detectFrequency(double[] signal, int samplingRate, float minPossibleFreq, float maxPossibleFreq, float wantedFreqError, int FFTnsamples, boolean display) throws Exception {

		// estimate the best samples based on the error we want
		int wLength = 0;
		long pow = 0;
		if (wantedFreqError > -1) {
			pow = Math.round(Math.log((float) samplingRate / wantedFreqError) / Math.log(2));

			if (pow <= 1)
				pow = Math.round(Math.log((float) signal.length / (float) ("" + signal.length).length()) / Math.log(2));

			AnalysisLogger.getLogger().debug("Suggested pow for window length=" + pow);

		}
		// adjust FFT Samples to be even
		else {
			if (FFTnsamples < 2)
				FFTnsamples = 2;
			else if (FFTnsamples > signal.length)
				FFTnsamples = signal.length;

			pow = Math.round(Math.log((float) FFTnsamples) / Math.log(2));
		}

		wLength = (int) Math.pow(2, pow);

		AnalysisLogger.getLogger().debug("Suggested windows length (samples)=" + wLength);
		AnalysisLogger.getLogger().debug("Suggested windows length (s)=" + ((float) wLength / (float) samplingRate) + " s");
		int windowAnalysisSamples = (int) Math.pow(2, 14);// (int)
		windowAnalysisSamples = wLength;
		int windowShiftSamples = (int) Math.round((float) windowAnalysisSamples / 2f);
		float windowShiftTime = (float) SignalConverter.sample2Time(windowShiftSamples, samplingRate);

		float error = ((float) samplingRate / (float) windowAnalysisSamples);

		AnalysisLogger.getLogger().debug("Error in the Measure will be: " + error + " Hz");
		AnalysisLogger.getLogger().debug("A priori Min Freq: " + minPossibleFreq + " s");
		AnalysisLogger.getLogger().debug("A priori Max Freq: " + maxPossibleFreq + " s");
		if (maxPossibleFreq >= samplingRate)
			maxPossibleFreq = (float) (samplingRate / 2f) - (0.1f * samplingRate / 2f);

		if (minPossibleFreq == 0)
			minPossibleFreq = 0.1f;

		minFrequency = minPossibleFreq;
		maxFrequency = maxPossibleFreq;
		// display the signal
		// if (display)
		// SignalProcessing.displaySignalWithGenericTime(signal, 0, 1, "signal");

		this.currentSamplingRate = samplingRate;
		this.currentWindowShiftSamples = windowShiftSamples;
		this.currentWindowAnalysisSamples = windowAnalysisSamples;

		// trace spectrum
		double[][] spectrum = SignalConverter.spectrogram("spectrogram", signal, samplingRate, windowShiftSamples, windowAnalysisSamples, false);
		if (display)
			SignalConverter.displaySpectrogram(spectrum, signal, "complete spectrogram", samplingRate, windowShiftSamples, windowAnalysisSamples);
		// apply the bandpass filter
		spectrum = SignalConverter.cutSpectrum(spectrum, minPossibleFreq, maxPossibleFreq, windowAnalysisSamples, samplingRate);
		if (display)
			// display cut spectrum
			SignalConverter.displaySpectrogram(spectrum, signal, "clean spectrogram", samplingRate, windowShiftSamples, windowAnalysisSamples);
		// extract the maximum frequencies in each frame
		SignalConverter signalMaximumAnalyzer = new SignalConverter();
		double[] maxfrequencies = signalMaximumAnalyzer.takeMaxFrequenciesInSpectrogram(spectrum, samplingRate, windowAnalysisSamples, minPossibleFreq);
		double[] powers = signalMaximumAnalyzer.averagepower;
		currentspectrum = spectrum;
		// display the maximum freqs
		AnalysisLogger.getLogger().debug("Number of frequency peaks " + maxfrequencies.length);
		// take the longest stable sequence of frequencies
		SignalConverter signalconverter = new SignalConverter();
		maxfrequencies = signalconverter.takeLongestStableTract(maxfrequencies, 0.01);

		if (maxfrequencies == null)
			return 0;

		this.startPeriodTime = SignalConverter.spectrogramTimeFromIndex(signalconverter.startStableTractIdx, windowShiftTime);
		this.endPeriodTime = SignalConverter.spectrogramTimeFromIndex(signalconverter.endStableTractIdx, windowShiftTime);
		this.startPeriodSampleIndex = SignalConverter.time2Sample(startPeriodTime, samplingRate);
		this.endPeriodSampleIndex = Math.min(SignalConverter.time2Sample(endPeriodTime, samplingRate), signal.length - 1);

		float power = 0;
		int counter = 0;
		// calculate the average spectrum relative amplitude in the most stable periodic tract
		for (int i = signalconverter.startStableTractIdx; i < signalconverter.endStableTractIdx; i++) {
			power = MathFunctions.incrementPerc(power, (float) powers[i], counter);
			counter++;
		}

		this.periodicityStrength = power;
		if (this.periodicityStrength == -0.0)
			this.periodicityStrength = 0;

		// reconstruct the F
		double meanF = MathFunctions.mean(maxfrequencies);
		// we consider a complete cycle
		double possibleperiod = 2d / meanF;
		AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Frequency " + meanF);
		AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Periodicity " + possibleperiod);

		double maxperiod = Math.min(signal.length, currentWindowAnalysisSamples);

		if ((meanF <= minPossibleFreq) || (meanF >= maxPossibleFreq) || (possibleperiod == 0) || (possibleperiod > (maxperiod))) {
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->Invalid periodicity " + (meanF <= minPossibleFreq) + " , " + (meanF >= maxPossibleFreq) + " , " + (possibleperiod == 0) + " , " + (possibleperiod > (maxperiod)));

			meanF = 0;
			this.meanF = 0;
			this.lowermeanF = 0;
			this.uppermeanF = 0;

			this.meanPeriod = 0;
			this.lowermeanPeriod = 0;
			this.uppermeanPeriod = 0;
			this.periodicityStrength = 0;
			this.startPeriodTime = 0;
			this.endPeriodTime = 0;
			this.startPeriodSampleIndex = 0;
			this.endPeriodSampleIndex = 0;

		} else {
			AnalysisLogger.getLogger().debug("TimeSeriesAnalysis->periodicity is valid " + possibleperiod);
			this.meanF = meanF;
			this.lowermeanF = Math.max(meanF - error, minPossibleFreq);
			this.uppermeanF = Math.min(meanF + error, maxFrequency);

			this.meanPeriod = possibleperiod;
			this.lowermeanPeriod = 2d / lowermeanF;
			this.uppermeanPeriod = 2d / uppermeanF;
		}
		return meanF;
	}

	public void adjustParameters(double[] signal, int samplingRate, float minPossibleFreq, float maxPossibleFreq, float wantedFreqError, int FFTnsamples) {
		// estimate the best samples based on the error we want
		int wLength = 0;
		long pow = 0;
		if (wantedFreqError > -1) {
			pow = Math.round(Math.log((float) samplingRate / wantedFreqError) / Math.log(2));

			if (pow <= 1)
				pow = Math.round(Math.log((float) signal.length / (float) ("" + signal.length).length()) / Math.log(2));

			AnalysisLogger.getLogger().debug("Suggested pow for window length=" + pow);

		}
		// adjust FFT Samples to be even
		else {
			if (FFTnsamples < 2)
				FFTnsamples = 2;
			else if (FFTnsamples > signal.length)
				FFTnsamples = signal.length;

			pow = Math.round(Math.log((float) FFTnsamples) / Math.log(2));
		}

		wLength = (int) Math.pow(2, pow);

		AnalysisLogger.getLogger().debug("Suggested windows length (samples)=" + wLength);
		AnalysisLogger.getLogger().debug("Suggested windows length (s)=" + ((float) wLength / (float) samplingRate) + " s");
		int windowAnalysisSamples = (int) Math.pow(2, 14);// (int)
		windowAnalysisSamples = wLength;
		int windowShiftSamples = (int) Math.round((float) windowAnalysisSamples / 2f);
		float windowShiftTime = (float) SignalConverter.sample2Time(windowShiftSamples, samplingRate);

		float error = ((float) samplingRate / (float) windowAnalysisSamples);

		AnalysisLogger.getLogger().debug("Error in the Measure will be: " + error + " Hz");
		AnalysisLogger.getLogger().debug("A priori Min Freq: " + minPossibleFreq + " s");
		AnalysisLogger.getLogger().debug("A priori Max Freq: " + maxPossibleFreq + " s");
		if (maxPossibleFreq >= samplingRate)
			maxPossibleFreq = (float) (samplingRate / 2f) - (0.1f * samplingRate / 2f);

		if (minPossibleFreq == 0)
			minPossibleFreq = 0.1f;

		minFrequency = minPossibleFreq;
		maxFrequency = maxPossibleFreq;
		// display the signal
		// if (display)
		// SignalProcessing.displaySignalWithGenericTime(signal, 0, 1, "signal");

		this.currentSamplingRate = samplingRate;
		this.currentWindowShiftSamples = windowShiftSamples;
		this.currentWindowAnalysisSamples = windowAnalysisSamples;
	}

	public LinkedHashMap<String, String> detectAllFrequencies(double[] signal, int samplingRate, float minPossibleFreq, float maxPossibleFreq, float wantedFreqError, int FFTnsamples, float sensitivity, boolean display) throws Exception {

		adjustParameters(signal, samplingRate, minPossibleFreq, maxPossibleFreq, wantedFreqError, FFTnsamples);
		//evaluate the minimum frequency resolution
		double frequencyRes = ((double)samplingRate/2d)/ (double)currentWindowAnalysisSamples;
		AnalysisLogger.getLogger().debug("Frequency Resolution: "+frequencyRes);
		// trace spectrum
		double[][] spectrum = SignalConverter.spectrogram("spectrogram", signal, samplingRate, currentWindowShiftSamples, currentWindowAnalysisSamples, false);
		if (display)
			SignalConverter.displaySpectrogram(spectrum, signal, "complete spectrogram", samplingRate, currentWindowShiftSamples, currentWindowAnalysisSamples);

		// apply the bandpass filter
		spectrum = SignalConverter.cutSpectrum(spectrum, minPossibleFreq, maxPossibleFreq, currentWindowAnalysisSamples, samplingRate);
		if (display)
			// display cut spectrum
			SignalConverter.displaySpectrogram(spectrum, signal, "clean spectrogram", samplingRate, currentWindowShiftSamples, currentWindowAnalysisSamples);

		float windowShiftTime = (float) SignalConverter.sample2Time(this.currentWindowShiftSamples, samplingRate);
		float windowLengthTime = (float) SignalConverter.sample2Time(this.currentWindowAnalysisSamples, samplingRate);
		float signalTime = (float) SignalConverter.sample2Time(signal.length, samplingRate);
		currentspectrum = spectrum;
		// extract the maximum frequencies in each frame
		SignalConverter signalMaximumAnalyzer = new SignalConverter();

		ArrayList<Double>[] maxfrequencies = signalMaximumAnalyzer.takePeaksInSpectrogramFrames(spectrum, samplingRate, currentWindowAnalysisSamples, minPossibleFreq);

		LinkedHashMap<String, String> peaks = new LinkedHashMap<String, String>();

		double maxperiod = (double) Math.min(signal.length, currentWindowAnalysisSamples) * (double) samplingRate;
		double error= 1.96*frequencyRes;// ((float) samplingRate / (float) currentWindowAnalysisSamples);
		
		for (int i = 0; i < maxfrequencies.length; i++) {
			double startTime = SignalConverter.spectrogramTimeFromIndex(i, windowShiftTime);
			double endTime = Math.min(startTime+windowLengthTime,signalTime);
			int counter = 0;
			int freqCounter = 0;
			Double previousFreq=0d; 
			Double previousPeriod=-100d; 
			String prefix = "";
			if (maxfrequencies.length>1)
				prefix = " (Section " + (i + 1)+")";
			
			for (Double peakFreq : maxfrequencies[i]) {

				double period = 1d / peakFreq;
				double power = signalMaximumAnalyzer.currentSpikesPowerSpectra[i].get(freqCounter);
				
				
				double periodResolution = sensitivity/samplingRate;
				//the period distance has to be at least of 9 sample rates, the frequencies should not go under the resolution and over the borders
				//the period should be included two times in the window
				//the power of spectrum should be high enough
				if ((Math.abs(previousPeriod-period)>(periodResolution)) 
						&& (peakFreq-previousFreq>error) 
						&& (peakFreq >= minPossibleFreq) 
						&& (peakFreq <= maxPossibleFreq) 
						&& (period > 0) 
						&& (period < maxperiod*0.55f) 
						&& (!getPowerSpectrumStregthInterpretation(power).equalsIgnoreCase("None"))) 
				{
					AnalysisLogger.getLogger().debug("DISCREPANCY WITH RESPECT TO THE PREVIOUS FREQ:"+(peakFreq-previousFreq));
					AnalysisLogger.getLogger().debug("RATIO WITH RESPECT TO THE PREVIOUS FREQ:"+((peakFreq-previousFreq)/error));	
					if (counter == 0) {
						AnalysisLogger.getLogger().debug("Section "+(i+1));
						peaks.put("*StartTime_In_Spectrogram"+prefix, "" + startTime);
						peaks.put("*EndTime_In_Spectrogram" + prefix, "" + endTime);
					}
					double lowermeanF = Math.max(peakFreq - error, minPossibleFreq);
					double uppermeanF = Math.min(peakFreq + error, maxPossibleFreq);
					double upperUncertPeriod = 0;
					double lowerUncertPeriod = 0;
					if (peakFreq-previousFreq>error){
						upperUncertPeriod=MathFunctions.roundDecimal(1d / lowermeanF,2);
						lowerUncertPeriod=MathFunctions.roundDecimal(1d / uppermeanF,2);
					}
					else
					{
						upperUncertPeriod=MathFunctions.roundDecimal(period+periodResolution/2,2);
						lowerUncertPeriod=Math.max(1/samplingRate,MathFunctions.roundDecimal(period-periodResolution/2,2));
					}
					peaks.put("Period_"+(counter+1)+prefix, MathFunctions.roundDecimal(period,2)+" ~ "+"["+lowerUncertPeriod+";"+upperUncertPeriod+"]");
					peaks.put("Frequency_"+(counter+1)+prefix, MathFunctions.roundDecimal(peakFreq,2)+" ~ "+"["+MathFunctions.roundDecimal(lowermeanF,2)+";"+MathFunctions.roundDecimal(uppermeanF,2)+"]");
					peaks.put("Strength_of_Periodicity_"+(counter+1)+prefix, MathFunctions.roundDecimal(signalMaximumAnalyzer.currentSpikesPowerSpectra[i].get(freqCounter),2)+" ("+getPowerSpectrumStregthInterpretation(signalMaximumAnalyzer.currentSpikesPowerSpectra[i].get(freqCounter))+")");
					
					int minFidx = SignalConverter.frequencyIndex(minPossibleFreq, currentWindowAnalysisSamples, samplingRate);
					double spectrogramidx = SignalConverter.spectrumFreq2Idx(peakFreq.floatValue(), samplingRate, currentWindowAnalysisSamples)-minFidx;
					AnalysisLogger.getLogger().debug("SpectorgramIdx_"+(counter+1)+":" + spectrogramidx);
					AnalysisLogger.getLogger().debug("Strength_of_Periodicity_"+(counter+1)+":" + signalMaximumAnalyzer.currentSpikesPowerSpectra[i].get(freqCounter));
					AnalysisLogger.getLogger().debug("Strength_of_Periodicity_Interpretation"+(counter+1)+":" + getPowerSpectrumStregthInterpretation(signalMaximumAnalyzer.currentSpikesPowerSpectra[i].get(freqCounter)));
					AnalysisLogger.getLogger().debug("Frequency_"+(counter+1)+":" + peakFreq);
					AnalysisLogger.getLogger().debug("UpperFrequencyConfidence_"+(counter+1)+":" + uppermeanF);
					AnalysisLogger.getLogger().debug("LowerFrequencyConfidence_"+(counter+1)+":" + lowermeanF);
					AnalysisLogger.getLogger().debug("Period"+":" + period);
					AnalysisLogger.getLogger().debug("UpperFrequencyPeriod_"+(counter+1)+":" + (1d / lowermeanF));
					AnalysisLogger.getLogger().debug("LowerFrequencyPeriod_"+(counter+1)+":"+ (1d / uppermeanF));
					AnalysisLogger.getLogger().debug("");
					counter++;
					previousFreq=peakFreq;
					previousPeriod=period;
				}
				freqCounter++;
			}
			
			if (counter==0)
				peaks.put("Periodicity_"+(counter+1)+prefix, "No periodicities found");
		
		}
		
		return peaks;
	}
	
}
