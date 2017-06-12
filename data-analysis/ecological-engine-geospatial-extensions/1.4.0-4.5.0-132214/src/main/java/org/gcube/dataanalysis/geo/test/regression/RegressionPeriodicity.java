package org.gcube.dataanalysis.geo.test.regression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.gcube.dataanalysis.ecoengine.signals.PeriodicityDetector;

public class RegressionPeriodicity {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
		takeSignal();
	}
	public static void takeSignal() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(new File("signalPeriodic.txt")));
		String line = br.readLine();
		double[] signal = null;
		
		while (line!=null){
			String [] el = line.split(",");
			signal=new double[el.length];
			int i=0;	
			for (String e:el){
				signal[i]=Double.parseDouble(e);
				i++;	
			}
			line = null;
		}
		br.close();
		
		PeriodicityDetector pd = new PeriodicityDetector();
		double F = pd.detectFrequency(signal,true);
		
		System.out.println("Detected Frequency:"+F+" indecision ["+pd.lowermeanF+" , "+pd.uppermeanF+"]");
		System.out.println("Detected Period:"+pd.meanPeriod+" indecision ["+pd.lowermeanPeriod+" , "+pd.uppermeanPeriod+"]");
		System.out.println("Detected Periodicity Strength:"+pd.periodicityStrength+":"+pd.getPeriodicityStregthInterpretation());
	}
	
}
