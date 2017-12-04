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

public class TestSignal {

	static String cfg = "./cfg/";
	public static void main(String[] args) throws Exception{
//		String  layertitle = "Statistical Mean in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Temperature - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/temperature_annual_1deg_ENVIRONMENT_OCEANS_.nc}";
//		String  layertitle = "Mass Concentration of Chlorophyll in Sea Water in [03-30-13 01:00] (3D) {Mercator Ocean BIOMER1V1R1: Data extracted from dataset http://atoll-mercator.vlandata.cls.fr:44080/thredds/dodsC/global-analysis-bio-001-008-a}";
//		String  layertitle = "Objectively Analyzed Climatology in [07-01-01 01:00] (3D) {World Ocean Atlas 09: Sea Water Temperature - annual: dods://thredds.research-infrastructures.eu/thredds/dodsC/public/netcdf/temperature_annual_1deg_ENVIRONMENT_OCEANS_.nc}";
		//temperature anomaly: long signal
		//String  layertitle = "be24800d-7583-4efa-b925-e0d8760e0fd3";
		//temperature short periodic signal
//		String  layertitle = "dffa504b-dbc8-4553-896e-002549f8f5d3";
//		String  layertitle = "afd54b39-30f7-403a-815c-4f91c6c74c26";
//		String  layertitle = "6411b110-7572-457a-a662-a16e4ff09e4e";
		//wind stress
//		String  layertitle = "255b5a95-ad28-4fec-99e0-5d48112dd6ab";
		//wind speed
//		layertitle = "a116c9bc-9380-4d40-8374-aa0e376a6820";
		//nitrates
//		layertitle = "b1cd9549-d9d0-4c77-9532-b161a69fbd44";
		
		//ASC
//		String  layertitle = "2c2304d1-681a-4f3a-8409-e8cdb5ed447f";
		//WFS
//		String  layertitle = "0aac424b-5f5b-4fa6-97d6-4b4deee62b97";
		//Chlorophyll
		String  layertitle = "c565e32c-c5b3-4964-b44f-06dc620563e9";
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
