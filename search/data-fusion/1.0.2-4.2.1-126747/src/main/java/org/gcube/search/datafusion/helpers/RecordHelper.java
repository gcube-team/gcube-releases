package org.gcube.search.datafusion.helpers;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.ObjectField;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.URLField;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.id.jericho.lib.html.Source;

/**
 * 
 * @author Alex Antoniadis
 *
 */
public class RecordHelper implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String objid = "ObjectID";
	public static final String QUERY_FIELD = "query_field";
	public static final String ID_FIELD = "ID_FIELD";
	public static final String SCORE_FIELD = "rank";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RecordHelper.class);
	
	/**
	 * Get the score field from the rec
	 * @param rec
	 * @return rec score
	 */
	public static Float getRank(GenericRecord rec){
		Float val = null;
		try {
			String payload =((StringField)rec.getField(SCORE_FIELD)).getPayload().toString();
			val=  Float.parseFloat(payload);
			LOGGER.info("payload : " + payload + " val : " + val);
		} catch (Exception e) {
			return null;
		}
		
		return val;
	}
	
	/**
	 * Gets the payload of the snippetField from the rec
	 * @param rec
	 * @param snippetField
	 * @return rec payload
	 * @throws GRS2RecordDefinitionException
	 * @throws GRS2BufferException
	 */
	public static String getSnippetPayload(GenericRecord rec, String snippetField) throws GRS2RecordDefinitionException, GRS2BufferException {
		String payload = null;
		
		try {
			payload = ((StringField)rec.getField(snippetField)).getPayload();
		} catch (Exception e) {
			LOGGER.info("Could not get payload of field : " + snippetField);
			// TODO: handle exception
		}
		
		return payload;
	}
	
	/**
	 * Getis the actual content of the rec. It retrieves the page from the URI in objectID field
	 * and extracts the text. null is returned if not found
	 * 
	 * @param rec
	 * @return actual content of rec or null if not found
	 * @throws GRS2RecordDefinitionException
	 * @throws GRS2BufferException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String getRecordContentFromObjID(GenericRecord rec) throws GRS2RecordDefinitionException, GRS2BufferException, MalformedURLException, IOException {
		String text = null;
		try {
			String payload = ((StringField)rec.getField(objid)).getPayload();
			LOGGER.info("Retrieving content from url : " + payload);
			Source source=new Source(new URL(payload));
			text =source.extractText();
		} catch (Exception e) {
			LOGGER.error("Record : " + rec.getID() + " does not have object id");
		}
		return text;
	}
	
	/**
	 * Gets the record id from the record. 0 is returned if not found
	 * 
	 * @param rec
	 * @return record id or 0 if not found
	 * @throws GRS2RecordDefinitionException
	 * @throws GRS2BufferException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String getRecordID(GenericRecord rec) throws GRS2RecordDefinitionException, GRS2BufferException, MalformedURLException, IOException {
		String id = "0";
		try {
			id = ((StringField)rec.getField(objid)).getPayload();
		} catch (Exception e) {
			LOGGER.error("Record : " + rec.getID() + " does not have object id");
		}
		return  id;
	}
	
	/**
	 * Gets the payload from either the snippetFields or from the actual payload.
	 * The fields in snippetFields are examined one by one and then the actual payload.
	 * If snippet is found then the search stops.
	 * If no payload is found after search empty string is returned
	 * 
	 * @param rec
	 * @param snippetFields
	 * @return snippet or empty string if not found
	 * @throws GRS2RecordDefinitionException
	 * @throws GRS2BufferException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String getQuerySnippetFields(GenericRecord rec, List<String> snippetFields) throws GRS2RecordDefinitionException, GRS2BufferException, MalformedURLException, IOException {
		String payload = null;
		
		for (String field : snippetFields) {
			try {
				if (rec.getField(field) != null) {
					payload = getQueryFieldContent(rec, field);
					if (payload != null) {
						LOGGER.info("found snippet for field : " + field);
						break;
					}
				}
			} catch (Exception e) {
				LOGGER.warn("error while getting field : " + field + " from record");
			}
			
		}
		
		if (payload == null) {
			LOGGER.info("Snippet not found for record. trying to retrieve content");
			LOGGER.info("-------------------");
			LOGGER.info("tried the following snippet fields : " + snippetFields);
			LOGGER.info("fields in record : ");
			for (gr.uoa.di.madgik.grs.record.field.Field f : rec.getFields()) {
				LOGGER.info("\t" + f.getFieldDefinition().getName());
			}
			LOGGER.info("-------------------");
			
			payload = getRecordContentFromObjID(rec);
		}
		
		return payload != null ? payload : "";
	}
	
	/**
	 * Gets the payload of the snippetField from the rec.
	 * Currently the same as {@link RecordHelper#getSnippetPayload}
	 * 
	 * @param rec
	 * @param snippetField
	 * @return payload of snippetField or null if not found
	 * @throws GRS2RecordDefinitionException
	 * @throws GRS2BufferException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String getQueryFieldContent(GenericRecord rec, String snippetField) throws GRS2RecordDefinitionException, GRS2BufferException, MalformedURLException, IOException {
		String payload = getSnippetPayload(rec, snippetField);
		
		/*if (payload == null) {
			logger.info("Snippet not found for record. trying to retrieve content");
			payload = getRecordContentFromObjID(rec);
		}*/
		
		return payload;
	}
	
	/**
	 * Converts and stores a {@link GenericRecord} to lucene.
	 * queryField is the field where the query will be performed later.
	 * 
	 * @param w
	 * @param id
	 * @param rsRec
	 * @param queryField
	 * @throws Exception
	 */
	public static void rsRecToLucene(IndexWriter w, String id, GenericRecord rsRec, String queryField) throws Exception {
		Document doc = new Document();
		
		doc.add(new TextField(ID_FIELD,  id, Field.Store.YES));
		doc.add(new TextField(QUERY_FIELD, queryField, Field.Store.YES));
		
		
		for (gr.uoa.di.madgik.grs.record.field.Field f : rsRec.getFields()) {
			String fieldName = f.getFieldDefinition().getName();
			String payload = getFieldString(f);
			
			doc.add(new TextField(fieldName, payload, Field.Store.YES));
		}
		w.addDocument(doc);
	}
	
	/**
	 * Gets a {@link GenericRecord} from a lucene {@link Document}.
	 * fieldsName is a collection with all the fields that we want to project from the record.
	 * If a field is not contained in the record null is returned.
	 * Typically, all the records that are returned will have the same fieldsName which is the union of all
	 * the records that is constructed in feeding see {@link IndexHelper#feedLucene}
	 * 
	 * @param doc
	 * @param fieldsName
	 * @param score
	 * @return {@link GenericRecord} from the lucene {@link Document}
	 * @throws Exception
	 */
	public static GenericRecord luceneToRSRecord(Document doc, Set<String> fieldsName, Float score) throws Exception {
		GenericRecord rec = new GenericRecord();
		ArrayList<gr.uoa.di.madgik.grs.record.field.Field> fields = new ArrayList<gr.uoa.di.madgik.grs.record.field.Field>();
		
		
		for (String fieldName : fieldsName) {
			String payload = null;
			if (fieldName.equals(SCORE_FIELD)){
				payload = String.valueOf(score);
			}
			else{
				payload = doc.get(fieldName);
				//TODO: return something else instead of null?
			}
			fields.add(new StringField(payload));
		}
		
		rec.setFields(fields.toArray(new gr.uoa.di.madgik.grs.record.field.Field[fields.size()]));
		
		return rec;
	}
	
	/**
	 * Gets the payload from a {@link gr.uoa.di.madgik.grs.record.field.Field}
	 * 
	 * @param f
	 * @return payload of the field
	 * @throws Exception
	 */
	private static String getFieldString(gr.uoa.di.madgik.grs.record.field.Field f) throws Exception {
		String payload = null;

		if (f.getClass().equals(FileField.class)) {
			payload = ((FileField) f).getOriginalPayload().getName();
		} else if (f.getClass().equals(ObjectField.class)) {
			payload = ((ObjectField) f).getPayload().toString();
		} else if (f.getClass().equals(StringField.class)) {
			payload = ((StringField) f).getPayload().toString();
		} else if (f.getClass().equals(URLField.class)) {
			payload = ((URLField) f).getPayload().toString();
		} else {
			throw new Exception("Field : " + f.getClass() + " not of known class");
		}

		return payload;
	}
	
	
}
