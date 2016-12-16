package org.gcube.dataanalysis.executor.nodes.transducers.bionym;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;

public class CometMatcherManager {

	public enum Parsers {
		SIMPLE, GNI
	}

	public enum Reference {
		ASFIS, FISHBASE, OBIS
	}

	public enum Weights {
		SOUNDEX, EDIT_DISTANCE, MIXED
	}

	public static void cometParse(String pathToComet, String parser, String outFile, String inFile) throws Exception {
		if (!pathToComet.endsWith("/"))
			pathToComet += "/";
		String execution = "java -Xmx512m -Xmx1024m -jar " + pathToComet + "SpeciMEn1.0.71.jar -pt 6 -parser " + parser + " -parseOnly -parserOutFile " + outFile + " -inFile " + inFile;
		System.out.println("Executing: " + execution);
		Process process = null;
		
		try{
			process = Runtime.getRuntime().exec(execution);
		

		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = br.readLine();
		System.out.println(line);
		while (line != null) {
			line = br.readLine();
			System.out.println(line);
		}
		}catch(Exception e){
			System.out.println("Unable to execute the program");
			throw e;
		}
		finally{
			if (process!=null)
				process.destroy();
		}
	}

	public static void cometMatch(String pathToComet, String parser, String reference, String outFile, String inFile, float sxnw,int maxresults) throws Exception {
		if (!pathToComet.endsWith("/"))
			pathToComet += "/";
		// java -Xmx512m -Xmx1024m -jar SpeciMEn1.0.71.jar -pt 6 -parser SIMPLE -inFile ins.csv -outFile outm.csv -man -may -mc 10 -mSn -mt -ps -pt 6 -sxw 1 -targets ASFIS -xml -xslTemplate csv
		String execution = "java -Xmx512m -Xmx1024m -jar " + pathToComet + "SpeciMEn1.0.71.jar -parser " + parser + " -inFile " + inFile + " " + "-outFile " + outFile + " " + "-man " + "-may " + "-mc "+maxresults+" -mSn " + "-mt " + "-ps " + "-pt 6" + " -sxw " + sxnw + " " + "-targets " + reference + " " + "-xml -xslTemplate csv";
		System.out.println("Executing: " + execution);
		Process process = null;
		try{
		process = Runtime.getRuntime().exec(execution);

		BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String line = br.readLine();
		System.out.println(line);
		while (line != null) {
			line = br.readLine();
			System.out.println(line);
		}
		}catch(Exception e){
			System.out.println("Unable to execute the program");
			throw e;
		}
		finally{
			if (process!=null)
				process.destroy();
		}
	}

	public static List<Tuple<String>> parseCometParserOutput(String parserOutput) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(parserOutput)), "UTF-8"));
		String line = br.readLine(); // skip headers
		line = br.readLine();
		List<Tuple<String>> parsednames = new ArrayList<Tuple<String>>();
		while (line != null) {
			System.out.println("reading from parser output: "+line);
			if (line.trim().length() > 0) {
				List<String> tokens = Transformations.parseCVSString(line, ";");
				int tokenslength = tokens.size();
				// take the 3rd and 4th elements: PARSED_SCIENTIFIC_NAME;PARSED_AUTHORITY

				String scientificname = "";
				if (tokenslength > 2)
					scientificname = tokens.get(2).replace(",", "").trim();
				
				String author = "";
				if (tokenslength > 3)
					author = tokens.get(3).replace(",", "").trim();

				if (scientificname.length()==0 && author.length()==0)
					scientificname = line.replace(",", "");
					
				Tuple<String> t = new Tuple<String>(scientificname, author);
				parsednames.add(t);
			}
			
			line = br.readLine();
		}
		br.close();
		
		return parsednames;
	}
	
	// puts also in normal format, e.g. Species Abra alba (W. Wood, 1802)
	
	List<String> scores = new ArrayList<String>();
	List<String> matchednames= new ArrayList<String>();
	public List<String> getScores(){
		return scores;
	}
	
	public List<String> getMatches(){
		return matchednames;
	}
	
	public List<Tuple<String>> parseCometOutput(String parserOutput) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(parserOutput)), "UTF-8"));
		String line = br.readLine(); // skip headers
		line = br.readLine();
		List<Tuple<String>> parsednames = new ArrayList<Tuple<String>>();
		while (line != null) {

			if (line.trim().length() > 0) {
				System.out.println("Processing Comet output. Line: "+line);
				List<String> tokens = Transformations.parseCVSString(line, ",");
				int tokenslength = tokens.size();

				String score = "";
				if (tokenslength > 4)
					score = tokens.get(4).trim();

				String scientificname = "";
				if (tokenslength > 7)
					scientificname = tokens.get(7).trim();

				String author = "";
				if (tokenslength > 8)
					author = tokens.get(8).replace("(","").replace(")", "").trim();
				
				String matched =  scientificname.replace("(","").replace(")", "").replace(",", "").replace(";", "");
				parsednames.add(new Tuple<String>(matched,author,score));
			}
			line = br.readLine();
		}
		br.close();
		return parsednames;
	}

	public static void dumpCometInput(String inputFile, List<Tuple<String>> rawNames) throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(inputFile)));
		int size = rawNames.size();
		for (int i = 0; i < size; i++) {
			System.out.println("Writing the following to file: "+rawNames.get(i));
			Tuple<String> t = rawNames.get(i);
			String author = "";
			if (t.getElements().get(1).length()>0)
				author = " ("+t.getElements().get(1)+")";
			
			bw.append(t.getElements().get(0)+ author) ;
			if (i < (size - 1))
				bw.append("\n");
		}
		
		bw.close();
	}

	public List<Tuple<String>> match(String parser, String reference, String sandboxFolder, List<Tuple<String>> inputNamesList, float soundexweightF, int maxResults) throws Exception{
		File FmatcherinputFile = new File(sandboxFolder,"inputCometMatcher.csv");
		File FmatcheroutputFile = new File(sandboxFolder,"outputCometMatcher.csv");
		try{
			FmatcherinputFile.delete();
		}catch(Exception e){}
		
		try{
			FmatcheroutputFile.delete();
		}catch(Exception e){}
		
		String matcherinputFile = FmatcherinputFile.getAbsolutePath();
		String matcheroutputFile = FmatcheroutputFile.getAbsolutePath();
		CometMatcherManager.dumpCometInput(matcherinputFile, inputNamesList);
		CometMatcherManager.cometMatch(sandboxFolder,parser, reference, matcheroutputFile, matcherinputFile, soundexweightF, maxResults);
		List<Tuple<String>> outputNames = parseCometOutput(matcheroutputFile);
		return outputNames;
	}
	
}
