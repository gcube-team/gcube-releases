package org.gcube.textextractor.extractors;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.textextractor.helpers.ExtractorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class CSVAutocompleteExtractor extends InformationExtractor {

	private static final Logger logger = LoggerFactory.getLogger(CSVAutocompleteExtractor.class);
	
	public CSVAutocompleteExtractor() {
	}
	
	@Override
	public Map<String, String> extractFieldsFromFile(String filename) throws Exception{
		long starttime = System.currentTimeMillis();
		try (CSVReader reader = new CSVReader(new FileReader(filename))){ 
			reader.readNext();
		    String [] nextLine;
		    
		    Map<String, Set<String>> totalInfo = new HashMap<String, Set<String>>();
		    
		    while ((nextLine = reader.readNext()) != null) {
		    	this.gatherInfo(totalInfo, "uri", nextLine[0]);
		    	this.gatherInfo(totalInfo, "label", nextLine[1]);
		    	this.gatherInfo(totalInfo, "language", nextLine[2]);
		    	this.gatherInfo(totalInfo, "type", nextLine[3]);
		    	
		    }
		    
		    
		    Map<String, String> info = new HashMap<String, String>();
		    info.put("uri", ExtractorHelper.covertToString(totalInfo.get("uri")));
		    info.put("label", ExtractorHelper.covertToString(totalInfo.get("label")));
		    info.put("language", ExtractorHelper.covertToString(totalInfo.get("language")));
		    info.put("type", ExtractorHelper.covertToString(totalInfo.get("type")));
		    
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
	
	@Override
	public Map<String, String> enrichRecord(Map<String, String> record, String filename) {
		return record;
	}
	
	@Override
	public String convertInfoToRowset(Map<String, String> info) {
		String documentID = info.get("uri");
		info.remove("uri");
		
		return ExtractorHelper.createRowseFromFields(documentID, autocompleteCollectionID, autocompleteIDXType, info.get("language"), info);
	}
	
	
	private void gatherInfo(Map<String, Set<String>> totalInfo, String fieldName, String value){
		if (value == null || value.trim().length() == 0)
			return;
		totalInfo.get(fieldName).add(value.trim());
	}
	
}
