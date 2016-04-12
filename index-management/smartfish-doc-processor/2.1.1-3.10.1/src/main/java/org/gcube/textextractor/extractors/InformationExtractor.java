package org.gcube.textextractor.extractors;

import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class InformationExtractor {

	public static final String idxType = "SmartfishFT";
	public static final String autocompleteIDXType = "SmartfishAutocompleteFT";
	//public static final String autocompleteIDXType = "SmartfishFT";
	
	public static final String collectionID = "faoCollection";
	public static final String autocompleteCollectionID = "faoAutocompleteCollection";
	//public static final String autocompleteCollectionID = "faoCollection";
	
	
	public InformationExtractor() {
	}

	public abstract String convertInfoToRowset(Map<String, String> info);

	public abstract Map<String, String> extractFieldsFromFile(String filename) throws Exception;
	public abstract Map<String, String> enrichRecord(Map<String, String> record, String filename);
	
	public abstract List<Map<String, String>> extractInfo(String path) throws FileNotFoundException;
	
	
	public void extractInfoAndWriteToFile(String path, String outputFilename) throws IOException {

		try (FileWriter fw = new FileWriter(new File(outputFilename), true)) {
                
			for (Map<String, String> info : this.extractInfo(path)) {
				String rowset = convertInfoToRowset(info);
				fw.write(rowset);
			}
	
			fw.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void extractInfoAndWriteToRS(String path, RecordWriter<GenericRecord> writer) throws GRS2WriterException, FileNotFoundException {
		
		for (Map<String, String> info : this.extractInfo(path)) {
			String rowset = convertInfoToRowset(info);
			
			GenericRecord rec = new GenericRecord();
			rec.setFields(new Field[] { new StringField(rowset
					.toString()) });
			writer.put(rec, 2, TimeUnit.MINUTES);
		}
	}

}
