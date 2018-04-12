package org.gcube.dataanalysis.executor.nodes.transducers.bionym;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.utils.Tuple;

public class EVBPreprocessing {
	

//	"preparsecleaning";"Pre-parsing cleaning";"Does not require knowledge of where the individual components of the complete namestring start or end"
	public static String[][] preparsecleaning = {
		{" ", " ","3"},//space
		{""+(char)10," ","3"}, //space
		{" {2,}"," ","3"}, //consecutive spaces
		{"^ ", "","3"},//leading space
		{" $", "","3"},//trailing space
		{"[?]", "", "1"},//uncertain identification
		{" v(ar)?\\.? "," v. ","1" },//standardise variety indication
		{" f(orm(a)?)?.? ", " f. ","1"}//standardise form indication		
		};
	
	public static String[][] postparsecleaning = {
		{" sp[\\.]?( ?[1-9a-zA-Z])?$","","1"}//remove temporary species indication
		};
	
	public static String[] preparsecleaningorigins = {
			" ", //space
			""+(char)10, //space
			" {2,}", //consecutive spaces
			"^ ", //leading space
			" $", //trailing space
			"[?]", //uncertain identification
			" v(ar)?\\.? ", //standardise variety indication
			" f(orm(a)?)?.? " //standardise form indication		
	};
	
	public static String[] preparsecleaningtargets = {
		" ", //space
		" ", //space
		" ", //consecutive spaces
		"", //leading space
		"", //trailing space
		"", //uncertain identification
		" v. ", //standardise variety indication
		" f. " //standardise form indication		
	};
	
	//"postparsecleaning";"Post-parsing cleaning";"Does require knowledge of where the individual components of the complete namestring start or end; assumes namestring is split in name proper and authority"
	public static String[] postparsecleaningorigin = {
		" sp[\\.]?( ?[1-9a-zA-Z])?$" //remove temporary species indication
	};
	
	public static String[] postparsecleaningtargets = {
		"" //remove temporary species indication
	};
	
	//"fuzzymatch";"Fuzzy matching";"Based on original idea from Tony Rees"
	public static String[] fuzzymatchorigins = {
	"h", //remove all characters h
	"y", //all y to i
	"s|k" //all s and k to c
	};

	public static String[] fuzzymatchtargets = {
		"''", //remove all characters h
		"i", //all y to i
		"c" //all s and k to c
	};
	
	public static enum Preprocessors{
		EXPERT_RULES,
		NONE
	}
	
	public static boolean appliesToScientificName(int regexProperty){
		 int t = (1 & regexProperty);
		 return t>0;
	}
	
	public static boolean appliesToAuthorship(int regexProperty){
		 int t = (2 & regexProperty);
		 return t>0;
	}
	
	public static List<Tuple<String>> populateTuples(List<String> rawnames){
		List<Tuple<String>> preprocessednames = new ArrayList<Tuple<String>>();
		for (String rawn:rawnames){
			preprocessednames.add(new Tuple<String>(rawn,""));
		}
		return preprocessednames;
	}
	
	public List<Tuple<String>> preprocess(String parser, String sandboxFolder, List<String> rawnamesFiltered) throws Exception{
		File FParserinputFile = new File(sandboxFolder,"inputEVBParser.csv");
		File FParseroutputFile = new File(sandboxFolder,"outputEVBParser.csv");
		
		try{
			FParserinputFile.delete();
		}catch(Exception e){
			
		}
		
		try{
			FParseroutputFile.delete();
		}catch(Exception e){
			
		}
		
		String parserinputFile = FParserinputFile.getAbsolutePath();
		String parseroutputFile = FParseroutputFile.getAbsolutePath();
		
		List<String> preprocessedrawnames = new ArrayList<String>(rawnamesFiltered);
		//apply evb preprocess
		int namessize = preprocessedrawnames.size();

		System.out.println("Applying preprocessing to strings");
		
		for (int i=0;i<namessize;i++){
			String preprocessedrawname = preprocessedrawnames.get(i);
			for (int j=0;j<preparsecleaningorigins.length;j++){
				preprocessedrawname = preprocessedrawname.replaceAll(preparsecleaningorigins[j], preparsecleaningtargets[j]);
//				System.out.println(preparsecleaningorigins[j]+"->"+preparsecleaningtargets[j]+"="+preprocessedrawname);
			}
		}
		
		System.out.println("Applying parsing");
		
		CometMatcherManager.dumpCometInput(parserinputFile, populateTuples(preprocessedrawnames));
		CometMatcherManager.cometParse(sandboxFolder,parser, parseroutputFile, parserinputFile);
		List<Tuple<String>> parsedNames = CometMatcherManager.parseCometParserOutput(parseroutputFile);
		
		int pnamessize = parsedNames.size();
		System.out.println("Applying postprocessing to "+pnamessize+" strings");
		for (int i=0;i<pnamessize;i++){
			
			String postprocessedrawname = parsedNames.get(i).getElements().get(0);
			String postprocessedrawauthor = parsedNames.get(i).getElements().get(1);
			System.out.println("Parsed name: "+postprocessedrawname+" author: "+postprocessedrawauthor);
			for (int j=0;j<postparsecleaningorigin.length;j++){
				postprocessedrawname = postprocessedrawname.replaceAll(postparsecleaningorigin[j], postparsecleaningtargets[j]);
			}
		}
		
		return parsedNames;
	}
	
	
	public static void main(String[] args) throws Exception{
		
		/*
		List<String> normalized = CometMatcherManager.parseCometParserOutput("./PARALLEL_PROCESSING/outs.csv");
		for (String norm:normalized){
			System.out.println(norm);
		}
		*/
		 System.out.println("Gadus var. morhua".replaceAll(" v(ar)?\\.? ", " v. "));
				 
	} 
	
}
