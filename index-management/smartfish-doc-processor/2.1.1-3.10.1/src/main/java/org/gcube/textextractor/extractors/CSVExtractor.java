package org.gcube.textextractor.extractors;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.semantic.annotator.AnnotationBase;
import org.gcube.semantic.annotator.utils.ANNOTATIONS;
import org.gcube.textextractor.entities.ShortenCE4NameResponse;
import org.gcube.textextractor.helpers.ExtractorHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import org.gcube.textextractor.entities.ExtractedEntity;

public class CSVExtractor extends InformationExtractor {

	private static final Logger logger = LoggerFactory.getLogger(CSVExtractor.class);
			
	public CSVExtractor() {
	}
	
	@Override
	public Map<String, String> extractFieldsFromFile(String filename) throws Exception{
		
		long starttime = System.currentTimeMillis();
		
		try (CSVReader reader = new CSVReader(new FileReader(filename))){ 
			
			reader.readNext();
		    String [] nextLine;
		    Map<String, Set<String>> totalInfo = new HashMap<String, Set<String>>();
		    totalInfo.put("provenance", new HashSet<String>());
		    totalInfo.put("country", new HashSet<String>());
		    totalInfo.put("title", new HashSet<String>());
		    totalInfo.put("species_english_name", new HashSet<String>());
		    totalInfo.put("gear_used", new HashSet<String>());
		    totalInfo.put("type_of_vessel", new HashSet<String>());
		    
		    while ((nextLine = reader.readNext()) != null) {
		    	
		    	this.gatherInfo(totalInfo, "provenance", nextLine[0]);
		    	this.gatherInfo(totalInfo, "country", nextLine[1]);
		    	this.gatherInfo(totalInfo, "title", nextLine[2]);
		    	this.gatherInfo(totalInfo, "gear_used", nextLine[4]);
		    	this.gatherInfo(totalInfo, "type_of_vessel", nextLine[5]);
		    	
		    	List<String> species = Arrays.asList(nextLine[3].split("\\s*;\\s*")); 
		    	totalInfo.get("species_english_name").addAll(species);
		    }
		    
		    Map<String, String> info = new HashMap<String, String>();
		    info.put("provenance", ExtractorHelper.covertToString(totalInfo.get("provenance")));
	    	info.put(ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY), ExtractorHelper.covertToString(totalInfo.get("country")));
	    	info.put("title", ExtractorHelper.covertToString(totalInfo.get("title")));
	    	info.put("species_english_name", ExtractorHelper.covertToString(totalInfo.get("species_english_name")));
	    	info.put(ANNOTATIONS.getLocalName(ANNOTATIONS.GEAR), ExtractorHelper.covertToString(totalInfo.get("gear_used")));
	    	info.put(ANNOTATIONS.getLocalName(ANNOTATIONS.VESSEL), ExtractorHelper.covertToString(totalInfo.get("type_of_vessel")));
		    
	    	
	    	return info;
	    	
		} catch (Exception e) {
			logger.error("error while extracting fields from  : " + filename, e);
            throw e;
		} finally {
			long endtime = System.currentTimeMillis();
        	logger.info("time processing file : " + filename + " : " + (endtime - starttime)/1000.0 + " secs");
		}
	}

	@Override
	public List<Map<String, String>> extractInfo(String path) throws FileNotFoundException {
		List<Map<String, String>> extractedInfo = new ArrayList<Map<String, String>>();
 		
		int cnt = 0;
		List<String> filenames = ExtractorHelper.getFilenames(path);
		
		for (String filename : filenames) {
			 logger.info("Processing file : " + (++cnt) + " " + filename);
			try {
				
				Map<String, String> info = this.extractFieldsFromFile(filename);
				
				long part_start_time = System.currentTimeMillis();
                Map<String, String> enriched = enrichRecord(info, filename);
                long part_end_time = System.currentTimeMillis();
                
                logger.info("~> field enrichment time  : " + (part_end_time - part_start_time)/1000.0 + " secs");
                extractedInfo.add(enriched);
				
			} catch (Exception e) {
				logger.error("error while extracting info from : " + filename + " . will skip this file", e);
			}
		}
		
		return extractedInfo;
	}
	
	private void gatherInfo(Map<String, Set<String>> totalInfo, String fieldName, String value){
		if (value == null || value.trim().length() == 0)
			return;
		totalInfo.get(fieldName).add(value.trim());
	}
	
	@Override
	public Map<String, String> enrichRecord(Map<String, String> record, String filename){
		Map<String, String> enrichedRecord = new HashMap<String, String>();
		Map<String, List<String>> uris = new HashMap<String, List<String>>();
		
		String[] docParts = filename.split("/");
		String fname = docParts[docParts.length - 1];
		String[] fnameParts = fname.split("\\.");
		String docID = fnameParts[0].toLowerCase(); 
		
		String docURI = "http://smartfish.collection/statbase/" + docID;
		
		//replace the documentID
		enrichedRecord.put("documentID", docURI);
				
		enrichedRecord.putAll(record);
		
		
		ExtractorHelper.enrichSimpleField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY), new ExtractorHelper.QueryWrapperSimple() {
			@Override
			public String doCall(ExtractedEntity arg) throws Exception {
				return ExtractorHelper.queryCountry(arg);
			}
		});
		
		
		
		try {
			String speciesURIJson = null; 
			
			//logger.info("getting uris for species : " + record.get("species_english_name"));
			
			if (record.get("species_english_name").trim().length() > 0){
				speciesURIJson = ExtractorHelper.querySpecies(ExtractorHelper.covertToStringList(record.get("species_english_name")));
				uris.put("species_uris", ShortenCE4NameResponse.getURIFromJSON(speciesURIJson));
				enrichedRecord.put("species_uris", speciesURIJson);
			}
		} catch (Exception e){
			logger.warn("Error processing species : " + record.get("species_english_name"), e);
		}
        try {
            //logger.info(uris);
            //process those uris
            
            //add info to enrichedRecord
            annotate(docURI, uris);
        } catch (FileNotFoundException ex) {
        	logger.error("file : " + filename + " not found", ex);
        }
		
		return enrichedRecord;
	}
	
	
	@Override
	public String convertInfoToRowset(Map<String, String> info) {
		String documentID = info.get("documentID");
		info.remove("documentID");
		
		return ExtractorHelper.createRowseFromFields(documentID, collectionID, idxType, info.get("language"), info);
	}
	
	private void annotate(String filename, Map<String, List<String>> uris) throws FileNotFoundException {
        AnnotationBase annotator = AnnotationBase.getInstance();
        Set<Map.Entry<String, List<String>>> entrySet = uris.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY)+"_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_country(filename, uri_);
                }
            }
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.SPECIES)+"_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_species(filename, uri_);
                }
            }
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.GEAR)+"_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_gear(filename, uri_);
                }
            }
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.VESSEL)+"_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_vessel(filename, uri_);
                }
            }
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.WATER_AREA)+"_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_water_area(filename, uri_);
                }
            }
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.LAND_AREA)+"_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_land_area(filename, uri_);
                }
            }
        }
    }
}
