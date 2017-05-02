package org.gcube.rest.index.service.helpers;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gcube.rest.index.common.entities.fields.config.FacetType;
import org.gcube.rest.index.common.entities.fields.config.FieldConfig;
import org.gcube.rest.index.common.entities.fields.config.FieldType;

public class IndexMappingsEditor {

	
	public static Map<String, Object> insertIntoMappingsField(Map<String, Object> sourceAsMapMappings, String field, Map.Entry<String,Object> propertyPair){
		String [] tokenizedField = field.split("\\.");
		Object obj = sourceAsMapMappings;
		for(String subfield : tokenizedField)
			obj = ((Map)((Map)obj).get("properties")).get(subfield);
		((Map)obj).put(propertyPair.getKey(), propertyPair.getValue());
		return sourceAsMapMappings;
	}
	
	
	public static Map<String, Object> removePropertyFromMappingsField(Map<String, Object> sourceAsMapMappings, String field, String propKeyToRemove){
		String [] tokenizedField = field.split("\\.");
		Object obj = sourceAsMapMappings;
		for(String subfield : tokenizedField)
			obj = ((Map)((Map)obj).get("properties")).get(subfield);
		((Map)obj).remove(propKeyToRemove);
		return sourceAsMapMappings;
	}
	
	
	public static Map<String, Object> subFieldIntoMappings(Map<String, Object> sourceAsMapMappings, String field, FieldConfig fieldConfig) throws IOException{
		String analyser = AnalyzerGenerator.getAnalyserNameFor(fieldConfig.getFacetType());
		// if this field is already re-indexed e.g (dc:language.fields.raw_non_tokenized), skip it (we use the original field e.g (dc:language))
		for(FacetType ft : FacetType.values()) {
			if(field.endsWith("."+ft.getText()))
				return sourceAsMapMappings;
		}
		String [] tokenizedField = field.split("\\.");
		Object obj = sourceAsMapMappings;
		for(String subfield : tokenizedField)
			obj = ((Map)((Map)obj).get("properties")).get(subfield);
		FacetType facetType = fieldConfig.getFacetType();
		Map<String, Object> subflds = new LinkedHashMap<String,Object>(); 
		Map<String,Object> nonTokenizedSubfield = new LinkedHashMap<String,Object>();
		nonTokenizedSubfield.put("type", "string");
		if(analyser!=null && !analyser.isEmpty())
			nonTokenizedSubfield.put("analyzer", analyser);
		subflds.put(facetType.toString().toLowerCase(), nonTokenizedSubfield);
		((Map)obj).remove("fields");
		if(!facetType.equals(FacetType.NONE))
			((Map)obj).put("fields", subflds);
		return sourceAsMapMappings;
	}
	
	
	public static class AnalyzerGenerator{
		
		public static String CUSTOM_ANALYZER_PREFIX = "custom_analyser_";
		
		/**
		 * 
		 * @param fieldConfig 
		 * @return <h1 style="color:red;font-size:20px;"><b>NOTICE</b> that it returns empty if no analyser is to be set.</h1>
		 * @throws IOException
		 */
		public static Map<String,Object> formAnalysersFor(FacetType ... facetTypes) throws IOException{
			Map<String,Object> analyser = new LinkedHashMap<String,Object>();
			
			List<String> stopwords = getAllStopwordsListES();
			
			for(FacetType facetType : facetTypes){
				String customAnalyser = getAnalyserNameFor(facetType);
				Map<String,Object> analyserFields = new LinkedHashMap<String,Object>();
				if(facetType.equals(FacetType.NORMAL)){
					analyserFields.put("type", "standard");
					analyserFields.put("tokenizer", "standard");
					analyserFields.put("stopwords", stopwords);
					analyser.put(customAnalyser, analyserFields);
				}
				if(facetType.equals(FacetType.NON_TOKENIZED)){
					analyserFields.put("type", "custom");
					analyserFields.put("tokenizer", "keyword");
//					analyserFields.put("filter", "lowercase"); //optionally
					analyser.put(customAnalyser, analyserFields);
				}
			}
			return analyser;
		}
		
		
		public static String getAnalyserNameFor(FacetType facetType){
			return CUSTOM_ANALYZER_PREFIX + facetType.toString().toLowerCase();
		}
		
		public static List<String> getAllStopwordsListES(){
			return Stream.of("_arabic_", "_armenian_", "_basque_", "_brazilian_", 
					"_bulgarian_", "_catalan_", "_czech_", "_danish_", "_dutch_", 
					"_english_", "_finnish_", "_french_", "_galician_", "_german_", 
					"_greek_", "_hindi_", "_hungarian_", "_indonesian_", "_irish_", 
					"_italian_", "_latvian_", "_norwegian_", "_persian_", "_portuguese_", 
					"_romanian_", "_russian_", "_sorani_", "_spanish_", 
					"_swedish_", "_thai_", "_turkish_")
					.collect(Collectors.toList());
		}
		
	}
}
