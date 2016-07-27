package org.gcube.textextractor.extractors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.gcube.textextractor.helpers.ExtractorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

public class PDFExtractor extends InformationExtractor {

	private static final Logger logger = LoggerFactory.getLogger(PDFExtractor.class);
	
	public PDFExtractor() {
	}
	
	@Override
	public Map<String, String> extractFieldsFromFile(String filename) throws Exception{
		long starttime = System.currentTimeMillis();

		try {
			InputStream input = new FileInputStream(filename);
			ContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			new PDFParser().parse(input, handler, metadata,
					new ParseContext());

			
			String text = ExtractorHelper.removeEmptyLines(handler.toString());
			Map<String, String> info = new HashMap<String, String>();
			
			info.put("documentID", filename);
			info.put("text", text);
			info.put("title",  metadata.get("title"));
			info.put("language",  new LanguageIdentifier(text).getLanguage());
			
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
	public String convertInfoToRowset(Map<String, String> info) {
		return ExtractorHelper.createRowseFromFields(info.get("documentID"), collectionID, idxType, info.get("language"), info);
	}


	@Override
	public Map<String, String> enrichRecord(Map<String, String> record, String filename) {
		return record;
	}
	
}
