package org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DistanceCalculator;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;

public class RealTimeMatcher {

	public String PARSER;
	public String INPUT_DATA_SOURCE_ID;
	public String INPUT_DATA_ID;
	public String INPUT_DATA;
	public String PREPARSED_INPUT_DATA;
	public String PARSED_SCIENTIFIC_NAME;
	public String PARSED_AUTHORITY;
	public String POST_PARSED_SCIENTIFIC_NAME;
	public String POST_PARSED_AUTHORITY;
	static HashMap<String, HashMap<String,TafInfo>> officialTafsMap;
	static int cachCleaningTime = 2 * 60 * 60 *1000;
	static String outputheaders = "SOURCE_DATASOURCE_ID;SOURCE_ID;SOURCE_DATA;PRE_PARSED_SOURCE_DATA;PARSED_SCIENTIFIC_NAME;PARSED_AUTHORITY;PARSER;POST_PARSED_SCIENTIFIC_NAME;POST_PARSED_AUTHORITY;MATCHING_SCORE;TARGET_DATA_SOURCE;TARGET_DATA_ID;TARGET_DATA_SCIENTIFIC_NAME;TARGET_DATA_AUTHORITY;TARGET_DATA_KINGDOM;TARGET_DATA_PHYLUM;TARGET_DATA_CLASS;TARGET_DATA_ORDER;TARGET_DATA_FAMILY;TARGET_DATA_GENUS;TARGET_DATA_SPECIES;TARGET_DATA_VERNACULAR_NAMES";
	
	// MATCHING_SCORE TARGET_DATA_SOURCE TARGET_DATA_ID TARGET_DATA_SCIENTIFIC_NAME TARGET_DATA_AUTHORITY TARGET_DATA_KINGDOM TARGET_DATA_PHYLUM TARGET_DATA_CLASS TARGET_DATA_ORDER TARGET_DATA_FAMILY TARGET_DATA_GENUS TARGET_DATA_SPECIES TARGET_DATA_VERNACULAR_NAMES
	public RealTimeMatcher(){
		//TODO: scheduler
//		databasecheckScheduler = new Timer(); databasecheckScheduler.schedule(new DatabaseController(), 0, refreshTime);
	}
	
	private class TafsCacheCleaner extends TimerTask {
		@Override
		public void run() {
			AnalysisLogger.getLogger().debug("RealTimeMatcher: Cache cleaned");
			officialTafsMap = null;
			System.gc(); 
		}
		
	}
	
	class TafInfo {
		 double MATCHING_SCORE ;
		 String TARGET_DATA_SOURCE ;
		String TARGET_DATA_ID;
		String TARGET_DATA_SCIENTIFIC_NAME;
		String TARGET_DATA_AUTHORITY;
		String TARGET_DATA_KINGDOM;
		String TARGET_DATA_PHYLUM;
		String TARGET_DATA_CLASS;
		String TARGET_DATA_ORDER;
		String TARGET_DATA_FAMILY;
		String TARGET_DATA_GENUS;
		String TARGET_DATA_SPECIES;
		String TARGET_DATA_VERNACULAR_NAMES;
	}

	
	
	private HashMap<String, TafInfo> getCurrentTaf(String tafFile) throws Exception {
		try {
			String file = FileTools.loadString(tafFile, "UTF-8");
			String[] tafrows = file.split("\n");
			HashMap<String, TafInfo> tafMap;
			if (officialTafsMap==null){
					officialTafsMap = new HashMap<String, HashMap<String,TafInfo>>();
					Timer cachecleaner = new Timer(); 
					cachecleaner.schedule(new TafsCacheCleaner(), cachCleaningTime);		
			}
			if (officialTafsMap.get(tafFile)!=null)
					return  officialTafsMap.get(tafFile);
			
			tafMap = new HashMap<String, TafInfo>();
			
			for (String row : tafrows) {
				List<String> elements = Transformations.parseCVSString(row, "\t");
				TafInfo tafInfo = new TafInfo();
				tafInfo.TARGET_DATA_ID = elements.get(0);
				tafInfo.TARGET_DATA_SCIENTIFIC_NAME = elements.get(37);
				tafInfo.TARGET_DATA_AUTHORITY = elements.get(41);
				tafInfo.TARGET_DATA_KINGDOM = elements.get(1);
				tafInfo.TARGET_DATA_PHYLUM = elements.get(5);
				tafInfo.TARGET_DATA_CLASS = elements.get(9);
				tafInfo.TARGET_DATA_ORDER = elements.get(13);
				tafInfo.TARGET_DATA_FAMILY = elements.get(17);
				tafInfo.TARGET_DATA_GENUS = elements.get(21);
				tafInfo.TARGET_DATA_SPECIES = elements.get(29);
				tafInfo.TARGET_DATA_VERNACULAR_NAMES = "";

				tafMap.put((tafInfo.TARGET_DATA_SCIENTIFIC_NAME + " " + tafInfo.TARGET_DATA_AUTHORITY).toLowerCase(), tafInfo);
			}
			
			officialTafsMap.put(tafFile, tafMap);
			return tafMap;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error: could not read Taf file");
		}
	}

	private void getCurrentInput(String postParserFile) throws Exception {
		try {
			String file = FileTools.loadString(postParserFile, "UTF-8");

			String row = file.split("\n")[1];
			List<String> elements = Transformations.parseCVSString(row, ";");
			PARSER = elements.get(0);
			INPUT_DATA_SOURCE_ID = elements.get(1);
			INPUT_DATA_ID = elements.get(2);
			INPUT_DATA = elements.get(3);
			PREPARSED_INPUT_DATA = elements.get(4);
			PARSED_SCIENTIFIC_NAME = elements.get(5);
			PARSED_AUTHORITY = elements.get(6);
			POST_PARSED_SCIENTIFIC_NAME = elements.get(7);
			POST_PARSED_AUTHORITY = elements.get(8);
		} catch (Exception e) {
			throw new Exception("Error: could not read post-parsing file");
		}
	}

	public void match(String taffile, String tafName, String postParserFile, String outputFile, double threshold, int maxvalues) throws Exception {
		
		AnalysisLogger.getLogger().debug("RealTimeMatcher started with the following parameters: ");
		AnalysisLogger.getLogger().debug("taffile: "+taffile);
		AnalysisLogger.getLogger().debug("tafName: "+tafName);
		AnalysisLogger.getLogger().debug("postParserFile: "+postParserFile);
		AnalysisLogger.getLogger().debug("outputFile: "+outputFile);
		AnalysisLogger.getLogger().debug("threshold: "+threshold);
		AnalysisLogger.getLogger().debug("maxvalues: "+maxvalues);
		long t0 = System.currentTimeMillis();
		getCurrentInput(postParserFile);
		long t1 = System.currentTimeMillis();
		AnalysisLogger.getLogger().debug("Current Input rebuilt in "+(t1-t0));
		
		/*
		Properties p = PropertyLoader.loadProperties("cache/cache.ccf", ClassLoader.getSystemClassLoader());
		CompositeCacheManager ccm = CompositeCacheManager.getUnconfiguredInstance();
		ccm.configure(p);
		CacheAccess cache = JCS.getInstance("zone");
		cache.put("hello", "world");
		System.out.println("Retrieved: "+cache.get("hello"));
		 */
		
		HashMap<String, TafInfo> scientificnames = getCurrentTaf(taffile);
//		TafInfo info = scientificnames.get((POST_PARSED_SCIENTIFIC_NAME + " " + POST_PARSED_AUTHORITY).toLowerCase());
		long t2 = System.currentTimeMillis();
		AnalysisLogger.getLogger().debug("TAF rebuilt in "+(t2-t1));
		/*
		if (info != null) {
			AnalysisLogger.getLogger().debug(info.TARGET_DATA_SCIENTIFIC_NAME+" "+info.TARGET_DATA_AUTHORITY);
			return;
		}
		*/
		
		DistanceCalculator dc = new DistanceCalculator();
		List<TafInfo> bestTafs= new ArrayList<RealTimeMatcher.TafInfo>();
		int bestTafList = 0;
		for (TafInfo testInfo:scientificnames.values()){
			double snameScore = dc.CD(false, POST_PARSED_SCIENTIFIC_NAME, testInfo.TARGET_DATA_SCIENTIFIC_NAME, true, false);
			double authScore = 0.5;
			if (POST_PARSED_AUTHORITY!=null && testInfo.TARGET_DATA_AUTHORITY!=null && POST_PARSED_AUTHORITY.length()>0 && testInfo.TARGET_DATA_AUTHORITY.length()>0)
				authScore=dc.CD(false, POST_PARSED_AUTHORITY, testInfo.TARGET_DATA_AUTHORITY, true, false);
			else if (POST_PARSED_AUTHORITY==null || POST_PARSED_AUTHORITY.length() == 0 )
				authScore=1;
			if (authScore*snameScore>threshold){
				testInfo.MATCHING_SCORE=(authScore*snameScore);
				insertTaf(bestTafs,testInfo,bestTafList);
				bestTafList++;
			}
		}
		
		long t3 = System.currentTimeMillis();
		AnalysisLogger.getLogger().debug("Scientific Names rebuilt in "+(t3-t2));
		
		AnalysisLogger.getLogger().debug("Results");
		int belements = 0;
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFile)));
		bw.write(outputheaders+"\n");
		//	static String outputheaders = "SOURCE_DATASOURCE_ID;SOURCE_ID;SOURCE_DATA;PRE_PARSED_SOURCE_DATA;PARSED_SCIENTIFIC_NAME;PARSED_AUTHORITY;PARSER;POST_PARSED_SCIENTIFIC_NAME;POST_PARSED_AUTHORITY;MATCHING_SCORE;TARGET_DATA_SOURCE;TARGET_DATA_ID;TARGET_DATA_SCIENTIFIC_NAME;TARGET_DATA_AUTHORITY;TARGET_DATA_KINGDOM;TARGET_DATA_PHYLUM;TARGET_DATA_CLASS;TARGET_DATA_ORDER;TARGET_DATA_FAMILY;TARGET_DATA_GENUS;TARGET_DATA_SPECIES;TARGET_DATA_VERNACULAR_NAMES";
		for (TafInfo b:bestTafs){
			AnalysisLogger.getLogger().debug(b.TARGET_DATA_SCIENTIFIC_NAME+" "+b.TARGET_DATA_AUTHORITY+" "+b.MATCHING_SCORE);
			bw.write(q(INPUT_DATA_SOURCE_ID)+";"+q(INPUT_DATA_ID)+";"+
					q(INPUT_DATA)+";"+q(PREPARSED_INPUT_DATA)+";"+q(PARSED_SCIENTIFIC_NAME)+";"+
					q(PARSED_AUTHORITY)+";"+q(PARSER)+";"+q(POST_PARSED_SCIENTIFIC_NAME)+";"+
					q(POST_PARSED_AUTHORITY)+";"+MathFunctions.roundDecimal(b.MATCHING_SCORE,2)+";"+
					q(tafName)+";"+
					q(b.TARGET_DATA_ID)+";"+q(b.TARGET_DATA_SCIENTIFIC_NAME)+";"+q(b.TARGET_DATA_AUTHORITY)+";"+
					q(b.TARGET_DATA_KINGDOM)+";"+q(b.TARGET_DATA_PHYLUM)+";"+q(b.TARGET_DATA_CLASS)+";"+
					q(b.TARGET_DATA_ORDER)+";"+q(b.TARGET_DATA_FAMILY)+";"+q(b.TARGET_DATA_GENUS)+";"+
					q(b.TARGET_DATA_SPECIES)+";"+q(b.TARGET_DATA_VERNACULAR_NAMES)+
					"\n");
			
			if (belements>maxvalues)
				break;
			
			belements++;
		}
		
		bw.close();
	}

	private String q(String in){
		return "\""+in+"\"";
	}
	
	private void insertTaf(List<TafInfo> elements, TafInfo toInsert, int elementsLen){
		
		int counter = 0;
		for (TafInfo e:elements){
			if (e.MATCHING_SCORE<toInsert.MATCHING_SCORE)
				break;
			counter++;
		}
		
		elements.add(counter,toInsert);

		
	}
	
	//Gaddius moriua
	public static void main(String args[]) throws Exception{
		AnalysisLogger.setLogger("./cfg/"+AlgorithmConfiguration.defaultLoggerFile);
		RealTimeMatcher rtm = new RealTimeMatcher();
		rtm.match("./cfg/FISHBASE_taxa.taf", "FISHBASE", "./cfg/outputParserf139084898dc4e4fb2d7a214bb33de25.txt","./cfg/outtest.txt", 0.1f, 10);
		rtm.match("./cfg/FISHBASE_taxa.taf", "FISHBASE","./cfg/outputParserf139084898dc4e4fb2d7a214bb33de25.txt", "./cfg/outtest.txt",0.1f, 10);
	}
	

}
