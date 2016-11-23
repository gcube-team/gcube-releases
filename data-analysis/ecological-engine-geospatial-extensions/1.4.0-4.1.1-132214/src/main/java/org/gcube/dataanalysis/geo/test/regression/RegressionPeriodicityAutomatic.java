package org.gcube.dataanalysis.geo.test.regression;

import org.gcube.dataanalysis.ecoengine.signals.PeriodicityDetector;

public class RegressionPeriodicityAutomatic {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
		takeSignal();
	}
	public static void takeSignal() throws Exception{
		

		PeriodicityDetector pd = new PeriodicityDetector();
		
		double[] signal = pd.produceNoisySignal(2000, 1, 0.1f, 0f);		
		
		double F = pd.detectFrequency(signal,true);
		
		System.out.println("Detected Frequency:"+F+" indecision ["+pd.lowermeanF+" , "+pd.uppermeanF+"]");
		System.out.println("Detected Period:"+pd.meanPeriod+" indecision ["+pd.lowermeanPeriod+" , "+pd.uppermeanPeriod+"]");
		System.out.println("Detected Periodicity Strength:"+pd.periodicityStrength+":"+pd.getPeriodicityStregthInterpretation());
	}
	
}
