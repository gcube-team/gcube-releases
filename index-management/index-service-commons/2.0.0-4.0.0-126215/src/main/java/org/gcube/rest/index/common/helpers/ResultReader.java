package org.gcube.rest.index.common.helpers;

import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.rest.commons.helpers.JSONConverter;

public class ResultReader {

	public static List<Map<String, String>> resultSetToRecords(String grslocator)
			throws Exception {
		List<Map<String, String>> records = new ArrayList<Map<String, String>>();

		ForwardReader<GenericRecord> reader = new ForwardReader<GenericRecord>(
				new URI(grslocator));

		try {
			Iterator<GenericRecord> it = reader.iterator();
			while (it.hasNext()) {
				GenericRecord rec = it.next();
				Map<String, String> record = new HashMap<String, String>();

				for (Field field : rec.getFields()) {
					String fieldName = field.getFieldDefinition().getName();
					String value = (((StringField) field).getPayload());
					record.put(fieldName, value);
				}
				records.add(record);
			}
			reader.close();
		} catch (Exception e) {
			reader.close();
			throw e;
		} 

		return records;
	}

	public static String recordsToJson(List<Map<String, String>> records,
			boolean pretty) {
		return JSONConverter.convertToJSON(records, pretty);
	}

	public static String resultSetToJsonRecords(String grslocator,
			boolean pretty) {

		List<Map<String, String>> records = null;
		try {
			records = resultSetToRecords(grslocator);
		} catch (Exception e) {
			return JSONConverter.convertToJSON("error",
					"error while consuming grs2 : " + e.getMessage());
		}
		return JSONConverter.convertToJSON(records, pretty);
	}
	
	
	public static void streamResultSetToJsonRecords(Writer writer, String grslocator,
			boolean pretty) throws Exception {

		
		ForwardReader<GenericRecord> reader = new ForwardReader<GenericRecord>(
				new URI(grslocator));

		try {
			Iterator<GenericRecord> it = reader.iterator();
			while (it.hasNext()) {
				GenericRecord rec = it.next();
				Map<String, String> record = new HashMap<String, String>();

				for (Field field : rec.getFields()) {
					String fieldName = field.getFieldDefinition().getName();
					String value = (((StringField) field).getPayload());
					record.put(fieldName, value);
				}
				
				String jsonRec = JSONConverter.convertToJSON(record, pretty);
				writer.write(jsonRec);
			}
			reader.close();
		} catch (Exception e) {
			reader.close();
			throw e;
		} 
		
	}

}
