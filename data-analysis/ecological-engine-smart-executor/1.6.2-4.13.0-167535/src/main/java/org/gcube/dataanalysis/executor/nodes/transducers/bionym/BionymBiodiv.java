package org.gcube.dataanalysis.executor.nodes.transducers.bionym;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces.Matcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;

public class BionymBiodiv extends BionymFlexibleWorkflowTransducer {
	
	@Override
	public String getName() {
		return "BIONYM_BIODIV";
	}
	
	@Override
	public String getDescription() {
		return "An algorithm implementing BiOnym oriented to Biodiversity Taxa Names Matching with a predefined and optimized workflow. This version applies in sequence the following Matchers: GSay (thr:0.6, maxRes:10), FuzzyMatcher (thr:0.6, maxRes:10), Levenshtein (thr:0.4, maxRes:10), Trigram (thr:0.4, maxRes:10). BiOnym is a flexible workflow approach to taxon name matching. The workflow allows to activate several taxa names matching algorithms and to get the list of possible transcriptions for a list of input raw species names with possible authorship indication.";
	}
	
	@Override
	public List<Matcher> buildMatcherList(AlgorithmConfiguration config, String sandboxFolder, HashMap<String,String> globalparameters){
		//use the default matchers
		return null;
	}
	
	@Override
	public List<StatisticalType> getInputParameters() {
		List<TableTemplates> templateLWRInput = new ArrayList<TableTemplates>();
		templateLWRInput.add(TableTemplates.GENERIC);
		
		InputTable p1 = new InputTable(templateLWRInput, originTableParam, "Input table containing raw taxa names that you want to match", "bionym");
		ColumnType p2 = new ColumnType(originTableParam, rawnamesColumnParam, "The column containing the raw taxa names with or without authoship information", "rawnames", false);
		ServiceType p3 = new ServiceType(ServiceParameters.RANDOMSTRING, destinationTableParam, "name of the table that will contain the matches", "bion_");
		PrimitiveType p4 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, destinationTableLableParam, "Name of the table which will contain the matches", "bionout");
		PrimitiveType p5 = new PrimitiveType(Enum.class.getName(), YasmeenGlobalParameters.BuiltinDataSources.values(), PrimitiveTypes.ENUMERATED, YasmeenGlobalParameters.taxaAuthorityFileParam, "The reference dataset to use", "" +YasmeenGlobalParameters.BuiltinDataSources.FISHBASE);
		PrimitiveType p6 = new PrimitiveType(Enum.class.getName(), YasmeenGlobalParameters.BuiltinParsers.values(), PrimitiveTypes.ENUMERATED, YasmeenGlobalParameters.parserNameParam, "The Species - Authority parser", "" + YasmeenGlobalParameters.BuiltinParsers.SIMPLE);
		PrimitiveType p7 = new PrimitiveType(Boolean.class.getName(), null, PrimitiveTypes.BOOLEAN, YasmeenGlobalParameters.activatePreParsingProcessing,"Use preparsing rules to correct common errors","true");
		PrimitiveType p8 = new PrimitiveType(Boolean.class.getName(), null, PrimitiveTypes.BOOLEAN, YasmeenGlobalParameters.useStemmedGenusAndSpecies,"Process using Genus and Species names without declension","false");
		PrimitiveType p9 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, YasmeenGlobalParameters.overallMaxResults,"The maximum number of matching candidates per each raw input species","10");
		
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		parameters.add(p1);
		parameters.add(p3);
		parameters.add(p2);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p6);
		parameters.add(p7);
		parameters.add(p8);
		parameters.add(p9);
		
		DatabaseType.addDefaultDBPars(parameters);

		return parameters;

	}
	
	
}
