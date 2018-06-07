package org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils;

public class YasmeenGlobalParameters {

	public static String parserNameParam = "Parser_Name";
	public static String parserInputFileParam = "Input_File";
	public static String parserOutputFileParam = "Output_File";
	public static String taxaAuthorityFileParam = "Taxa_Authority_File";
	public static String activatePreParsingProcessing = "Activate_Preparsing_Processing";
	public static String useStemmedGenusAndSpecies = "Use_Stemmed_Genus_and_Species";
	public static String matcherParam = "Use_Stemmed_Genus_and_Species";
	public static String overallMaxResults = "Max_Results_per_Matcher";
	public static String performanceParam = "Accuracy_vs_Speed";
	public static String staticFilesFolderParam = "staticFilesFolder";
	
	public enum BuiltinDataSources {
		ASFIS, FISHBASE, OBIS,OBIS_ANIMALIA,OBIS_CNIDARIA,OBIS_ECHINODERMATA,OBIS_PLATYHELMINTHES, COL_FULL,COL_CHORDATA,COL_MAMMALIA,IRMNG_ACTINOPTERYGII,WORMS_ANIMALIA,WORMS_PISCES
	}
	
	public enum BuiltinParsers{
		SIMPLE, GNI, NONE
	}
	
	public enum BuiltinMatchers{
		GSAy,FUZZYMATCH, LEVENSHTEIN, SOUNDEX,LEV_SDX_TRIG,TRIGRAM,NONE
	}
	
	public enum Performance {
		MAX_ACCURACY,LOW_SPEED,MEDIUM_SPEED,HIGH_SPEED,MAX_SPEED
	}
	
}
