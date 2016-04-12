package org.gcube.dataanalysis.geo.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.signals.PeriodicityDetector;
import org.gcube.dataanalysis.ecoengine.signals.SignalProcessing;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.gcube.dataanalysis.geo.matrixmodel.TimeSeriesExtractor;

public class TestGeoTiff {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
		String  layertitle = "WorldClimBioWCS2";

		long t0 = System.currentTimeMillis();
		AnalysisLogger.setLogger(cfg+AlgorithmConfiguration.defaultLoggerFile);
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		config.setConfigPath(cfg);
		TimeSeriesExtractor intersector = new TimeSeriesExtractor(config);

//		intersector.takeTimeSlice(layertitle, 0, -180, 180, -10, 10, 0, 1, 1);
//		intersector.takeTimeSlice(layertitle, 0, -10, 10, -10, 10, 0,1, 1);
//		intersector.takeTimeInstantMatrix(layertitle, 0, -180, 180, -90, 90, 0, 0.5, 0.5);
		double signal[] = intersector.extractT(layertitle, 0d, 0d,0d,0.5);
		
//		SignalProcessing.displaySignalWithGenericTime(signal, 0, 1, "signal");
		
		System.out.println("ELAPSED TIME: "+(System.currentTimeMillis()-t0));
		
		System.out.println("Signal: "+signal.length);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("signal.txt")));
		for (double si: signal){
			bw.write(si+",");
		}
		bw.close();
		
		PeriodicityDetector pd = new PeriodicityDetector();
		double F = pd.detectFrequency(signal,true);
		
		System.out.println("Detected Frequency:"+F+" indecision ["+pd.lowermeanF+" , "+pd.uppermeanF+"]");
		System.out.println("Detected Period:"+pd.meanPeriod+" indecision ["+pd.lowermeanPeriod+" , "+pd.uppermeanPeriod+"]");
		System.out.println("Detected Periodicity Strength:"+pd.periodicityStrength+" "+pd.getPeriodicityStregthInterpretation());
	}
	
	public static void main1(String[] args) throws Exception{
		takeSignal();
	}
	public static void takeSignal() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(new File("signal.txt")));
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
		
		
//		SignalProcessing.displaySignalWithGenericTime(signal, 0, 1, "signal");
		PeriodicityDetector pd = new PeriodicityDetector();
		
//		signal = pd.produceNoisySignal(2000, 1, 0.1f, 0f);
		
		//float freq=1;//signal.length;
		
//		double F = pd.detectFrequency(signal, (int)freq, 0, freq, 1f,true);
		double F = pd.detectFrequency(signal,true);
		
		System.out.println("Detected Frequency:"+F+" indecision ["+pd.lowermeanF+" , "+pd.uppermeanF+"]");
		System.out.println("Detected Period:"+pd.meanPeriod+" indecision ["+pd.lowermeanPeriod+" , "+pd.uppermeanPeriod+"]");
		System.out.println("Detected Periodicity Strength:"+pd.periodicityStrength);
		
	}
	
}
