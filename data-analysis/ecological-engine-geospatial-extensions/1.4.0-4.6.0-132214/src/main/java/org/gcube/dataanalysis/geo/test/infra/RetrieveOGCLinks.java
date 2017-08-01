package org.gcube.dataanalysis.geo.test.infra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.infrastructure.GeoNetworkInspector;
import org.gcube.dataanalysis.geo.matrixmodel.MatrixExtractor;
import org.opengis.metadata.Metadata;

public class RetrieveOGCLinks {

	static String scope = "/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab";
	
	public static void main(String[] args) throws Exception{
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		AnalysisLogger.setLogger("./cfg/ALog.properties");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "gcube");
		config.setParam("DatabasePassword", "d4science2");
		config.setParam("DatabaseURL", "jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		MatrixExtractor extractor = new MatrixExtractor(config);
		extractor.getConnector("http://geoserver.d4science-ii.research-infrastructures.eu/geoserver/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=lluidiamaculata20121218223748535cet&format=json&maxfeatures=1", 0.5);
		
	}
	
	public static void main1(String[] args) throws Exception{
		AnalysisLogger.setLogger("./cfg/ALog.properties");
		GeoNetworkInspector gnInspector = new GeoNetworkInspector();
		gnInspector.setScope(scope);
		BufferedReader br = new BufferedReader(new FileReader(new File("AquamapsSpecies.txt")));
		String line = br.readLine();
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("AquamapsSpeciesLinks.csv")));
		
		System.out.println("speciesname,WMS,WFS,algorithm,abstract");
		bw.write("speciesname,WMS,WFS,algorithm,abstract\n");
		while (line!=null){
			String speciesname = line;
			
			List<Metadata> metadts = gnInspector.getAllGNInfobyTitle(speciesname, "1");
		
		for (Metadata meta:metadts){
			String abstractS = ""+meta.getIdentificationInfo().iterator().next().getAbstract();
			String WFS = gnInspector.getWFSLink(meta);
			String WMS = gnInspector.getWFSLink(meta);
			String algoritm = "OTHER";
			
			if (abstractS.contains("AquaMaps NativeRange2050 algorithm")){
				algoritm = "NATIVE 2050";
			}
			else if (abstractS.contains("AquaMaps SuitableRange algorithm")){
				algoritm = "SUITABLE";
			}
			else if (abstractS.contains("AquaMaps SuitableRange2050 algorithm")){
				algoritm = "SUITABLE 2050";
			}
			else if (abstractS.contains("AquaMaps NativeRange algorithm")){
				algoritm = "NATIVE";
			}
			
			String outstring = speciesname+",\""+WMS+"\",\""+WFS+"\","+algoritm+",\""+abstractS.replace("\"", "")+"\"";
//			System.out.println(abstractS);
			System.out.println(outstring);
			bw.write(outstring+"\n");
			//System.out.println("WFS = "+gnInspector.getWFSLink(meta));
			//System.out.println("ABSTRACT = "+meta.getIdentificationInfo().iterator().next().getAbstract());
			
		}
		
			line = br.readLine();
		}
		
		bw.close();
		br.close();
	}
	
	
}
