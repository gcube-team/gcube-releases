package org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.MatcherInput;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.MatcherOutput;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.SingleEntry;

public class YasmeenFileTools {

	public static MatcherInput getYasmeenParserOutput(String sandboxFolder, String filename) throws Exception{
		File inf = new File(sandboxFolder,filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inf), "UTF-8"));
		
		String line = br.readLine(); // skip headers
		line = br.readLine();
		MatcherInput input = new MatcherInput();
		int i=0;
		//PARSER;INPUT_DATA_SOURCE_ID;INPUT_DATA_ID;INPUT_DATA;PREPARSED_INPUT_DATA;PARSED_SCIENTIFIC_NAME;PARSED_AUTHORITY;POST_PARSED_SCIENTIFIC_NAME	;POST_PARSED_AUTHORITY
		while (line!=null){
			List<String> tokens = Transformations.parseCVSString(line, ";");
			if (tokens.size()>0){
				input.addEntry(i, tokens.get(3), tokens.get(7), tokens.get(8), null);
			}
			line = br.readLine();
			i++;
		}
		
		br.close();
		return input;
		
	}
	
	public static MatcherOutput getYasmeenMatcherOutput(String sandboxFolder, String filename) throws Exception{
		File inf = new File(sandboxFolder,filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inf), "UTF-8"));
		
		String line = br.readLine(); // skip headers
		line = br.readLine();
		
		MatcherOutput output = new MatcherOutput();
		int i=0;
//		SOURCE_DATASOURCE_ID	SOURCE_ID	SOURCE_DATA	PRE_PARSED_SOURCE_DATA	PARSED_SCIENTIFIC_NAME	PARSED_AUTHORITY	PARSER	POST_PARSED_SCIENTIFIC_NAME	POST_PARSED_AUTHORITY	MATCHING_SCORE	TARGET_DATA_SOURCE	TARGET_DATA_ID	TARGET_DATA_SCIENTIFIC_NAME	TARGET_DATA_AUTHORITY	TARGET_DATA_KINGDOM	TARGET_DATA_PHYLUM	TARGET_DATA_CLASS	TARGET_DATA_ORDER	TARGET_DATA_FAMILY	TARGET_DATA_GENUS	TARGET_DATA_SPECIES	TARGET_DATA_VERNACULAR_NAMES
		while (line!=null){
			AnalysisLogger.getLogger().debug("Yasmeen Output line:"+line);
			List<String> tokens = Transformations.parseCVSString(line, ";");
			if (tokens.size()>0){
				output.addEntry(i,tokens.get(2),tokens.get(7),tokens.get(8),
						Double.parseDouble(tokens.get(9)),tokens.get(10),tokens.get(11),tokens.get(12),tokens.get(13),null);
			}
			line = br.readLine();
			i++;
		}
		
		br.close();
		return output;
		
	}
	
	public static void writeYasmeenParserInput(String sandboxFolder, String filename, List<String> rawNames) throws Exception{
			File inf = new File(sandboxFolder,filename);
			if (inf.exists())
				inf.delete();
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inf), "UTF-8"));
			for (String rawName:rawNames)
				bw.append(rawName+"\n");
					
			bw.close();
	}
	
	@Deprecated
	public static void writeYasmeenMatcherInput(String sandboxFolder, String filename, String parserName, MatcherInput input) throws Exception {
		File inFile = new File(sandboxFolder,filename);
		if (inFile.exists())
			inFile.delete();
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inFile), "UTF-8"));
		bw.write("PARSER;INPUT_DATA;PARSED_SCIENTIFIC_NAME;PARSED_AUTHORITY\n");
		int nInputs = input.getEntriesNumber();
		for (int i=0;i<nInputs;i++){
			SingleEntry se = input.getEntry(i);
			bw.write(parserName+ ";\"" + se.originalName + "\";\"" + se.parsedScientificName + "\";\"" + se.parsedAuthorship + "\"");
		}
		bw.close();
	}
	
	
	public  static void callYasmeen(String command) throws Exception {
		
		System.out.println("Executing: " + command);
		Process process = null;
		
		try{
			process = Runtime.getRuntime().exec(command);
		
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

}
