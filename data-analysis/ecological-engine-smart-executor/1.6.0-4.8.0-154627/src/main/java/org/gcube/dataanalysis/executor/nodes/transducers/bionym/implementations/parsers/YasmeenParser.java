package org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.parsers;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.MatcherInput;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces.Parser;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenFileTools;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;


public class YasmeenParser implements Parser{

	public static String parserLib = "YASMEEN-parser-1.2.0.jar";
	
	protected  String sandboxFolder = "./";
	public HashMap<String, String> parameters;
	
	@Override
	public void init(String sandboxfolder, HashMap<String, String> parameters) {
		this.sandboxFolder = sandboxfolder;
		this.parameters = parameters;
	}

	@Override
	public MatcherInput parse (List<String> rawnames) throws Exception{
		String parserName = parameters.get(YasmeenGlobalParameters.parserNameParam);
		String inputFileName = parameters.get(YasmeenGlobalParameters.parserInputFileParam);
		String outputFileName = parameters.get(YasmeenGlobalParameters.parserOutputFileParam);
		Boolean preparsing = Boolean.valueOf(parameters.get(YasmeenGlobalParameters.activatePreParsingProcessing));
		
		YasmeenFileTools.writeYasmeenParserInput(sandboxFolder, inputFileName, rawnames);
		
		if (parserName==null || parserName.equalsIgnoreCase(YasmeenGlobalParameters.BuiltinParsers.NONE.name()) )
				parserName = "IDENTITY";
		
		String execution = "java -Xmx512m -Xmx1024m -jar "+new File(sandboxFolder,parserLib)+" -inFile "+new File(sandboxFolder,inputFileName).getAbsolutePath()+" -outFile "+new File(sandboxFolder,outputFileName).getAbsolutePath()+" -parser "+parserName;
		if (preparsing)
			execution+= " -preParsingRuleset commonPreparsingRules -preParsingRuleset bionymPreparsingRules -postParsingRuleset bionymPostparsingRules";
		
		YasmeenFileTools.callYasmeen(execution);
		MatcherInput input = YasmeenFileTools.getYasmeenParserOutput(sandboxFolder, outputFileName);
		
		return input;
	}
	
}
