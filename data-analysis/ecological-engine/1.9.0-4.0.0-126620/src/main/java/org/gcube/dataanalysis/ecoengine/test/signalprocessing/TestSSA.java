package org.gcube.dataanalysis.ecoengine.test.signalprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.signals.PeriodicityDetector;
import org.gcube.dataanalysis.ecoengine.signals.ssa.SSAWorkflow;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;

public class TestSSA {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		int windowLength = 20;
		float eigenvaluespercthr = 0.7f;
		int pointsToReconstruct = 100;
		
		SSAWorkflow.applyCompleteWorkflow(sawTimeSeries(), windowLength,eigenvaluespercthr,pointsToReconstruct,false);
		
		SSAWorkflow.applyCompleteWorkflow(sinTimeSeries(), windowLength,eigenvaluespercthr,pointsToReconstruct,false);
		
		SSAWorkflow.applyCompleteWorkflow(noisyTimeSeries(), windowLength,eigenvaluespercthr,pointsToReconstruct,false);
		
		SSAWorkflow.applyCompleteWorkflow(largeTimeSeries(), windowLength,eigenvaluespercthr,10,false);
	}

	
	public static List<Double> sawTimeSeries() throws Exception{
		String file = "timeseries";
		
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = "";
		List<Double> timeseries = new ArrayList<Double>();
		while ((line = br.readLine()) != null) {
			timeseries.add(Double.parseDouble(line));
		}
		br.close();
		return timeseries;
	}
	
	public static List<Double> sinTimeSeries() throws Exception{
		double sin [] = new PeriodicityDetector().produceNoisySignal(120, 1, 0.1f, 0);
		
		List<Double> timeseries = new ArrayList<Double>();
		for (int i=0;i<sin.length;i++){
			timeseries.add(sin[i]);
		}
		
		return timeseries;
	}
	
	public static List<Double> noisyTimeSeries() throws Exception{
		double sin [] = new PeriodicityDetector().produceNoisySignal(120, 1, 0.1f, 1.2f);
		
		List<Double> timeseries = new ArrayList<Double>();
		for (int i=0;i<sin.length;i++){
			timeseries.add(sin[i]);
		}
		
		return timeseries;
	}
	
	public static List<Double> largeTimeSeries() throws Exception{
		String file = "LargeTS.csv";
		
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line = "";
		List<Double> timeseries = new ArrayList<Double>();
		LinkedHashMap<String,String> values= new LinkedHashMap<String,String>();
		line = br.readLine();
		while ((line = br.readLine()) != null) {
		
			List<String> row= Transformations.parseCVSString(line, ",");
			if (values.get(row.get(3))==null)
				values.put(row.get(3), row.get(5));
			else{
				double val = Double.parseDouble(values.get(row.get(3)));
				val = val+Double.parseDouble(row.get(5));
				values.put(row.get(3), ""+val);
			}
				
		}
		br.close();
		
		for (String val:values.values()){
			timeseries.add(Double.parseDouble(val));
		}
		
		return timeseries;
	}
}
