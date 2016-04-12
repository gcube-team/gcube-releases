package org.gcube.elasticsearch.helpers;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.base.Joiner;
import com.google.gson.Gson;

/**
 * Simple parsing of the Rowsets that are produced from the gDTS and are fed to
 * the index.
 * 
 * After the parsing collections of the keys and fields are kept as
 * ForwardIndexDocument that can be easily transformed to JSON so they can be
 * stored in elasticsearch.
 * 
 * 
 * @author Alex Antoniadis
 * 
 */
public class FullTextIndexDocument implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(FullTextIndexDocument.class);
	private static Gson gson = new Gson();

	private static final String ELEMENT_KEY = "FIELD";
	private static final String ROWSET_KEY = "ROWSET";
	private static final String ATTRIBUTE_FIELDNAME = "name";

	private static final String ID_KEY_FIELD = "ObjectID";
	private static final String COLLECTION_FIELD = "gDocCollectionID";
	
	private static final String COLLECTION_ATTR_NAME = "colID";
	private static final String LANG_ATTR_NAME = "lang";
	
	public static final String LANGUAGE_FIELD = "gDocCollectionLang";
	public static final String LANG_UNKNOWN = "unknown";
	public static final String DOCID_FIELD = "ObjectID";
	
	public static final int MAX_FIELD_LENGTH = 32000;

	private Map<String, List<String>> fields = null;

	public FullTextIndexDocument() {
		this.fields = new HashMap<String, List<String>>();
	}

	public FullTextIndexDocument(String rowset) throws Exception {
		this();
		this.parseXML(rowset);
	}

	public void parseXML(String rowset) throws Exception {
		parseXML(this.getFields(), rowset);
	}

	public static void parseXML(Map<String, List<String>> fields,
			String rowset) throws Exception {
		Document doc = loadXMLFromString(rowset);
		doc.getDocumentElement().normalize();

		long before = System.currentTimeMillis();

		NodeList rowsetList = doc.getElementsByTagName(ROWSET_KEY);
		Element rowsetEl = (Element) rowsetList.item(0);
		String collID = rowsetEl.getAttribute(COLLECTION_ATTR_NAME).trim();
		String lang = rowsetEl.getAttribute(LANG_ATTR_NAME).trim();
		fields.put(COLLECTION_FIELD, Arrays.asList(collID));
		if (lang == null || lang.length() == 0)
			lang = LANG_UNKNOWN;
		
		fields.put(LANGUAGE_FIELD, Arrays.asList(lang));
		
		NodeList fieldList = doc.getElementsByTagName(ELEMENT_KEY);
		
		logger.info("xml field nodelist length : " + fieldList.getLength());
		
		for (int i = 0; i < fieldList.getLength(); i++) {
			Element field = (Element) fieldList.item(i);

			String fieldName = field.getAttribute(ATTRIBUTE_FIELDNAME).trim();
			String fieldValue = field.getTextContent().trim();

			//empty language case
			if (fieldName.equalsIgnoreCase(LANGUAGE_FIELD) && (fieldValue == null ||  fieldValue.trim().length() == 0)){
				fieldValue = LANG_UNKNOWN;
			}
			
			if (fieldValue.length() == 0)
				continue;
			
			if (fieldValue.length() > MAX_FIELD_LENGTH && fieldName.equalsIgnoreCase("file") == false) {
				logger.trace("encountered field : " +  fieldName + "  with length : " + fieldValue.length() + " will trim it to : " + MAX_FIELD_LENGTH);
				fieldValue = fieldValue.substring(0, MAX_FIELD_LENGTH);
			}
			
			if (fields.containsKey(fieldName)) {
				fields.get(fieldName).add(fieldValue);
			} else {
				ArrayList<String> l = new ArrayList<String>();
				l.add(fieldValue);
				fields.put(fieldName, l);
			}
		}
		
		long after = System.currentTimeMillis();
		logger.info("parse xml after : " + (after - before) / 1000.0 + " secs");
	}

	public String getID() {
		// return calculateID(this.keys);
		return getDocId();
	}

	public String getDocLang() {
		List<String> langs = this.getFields().get(LANGUAGE_FIELD);
		if (langs == null) {
			logger.info("Lang is null for doc with id : " + this.getID());
			return LANG_UNKNOWN;
		} else if (langs.size() == 0) {
			logger.info("No languages found for doc with id : " + this.getID());
			return LANG_UNKNOWN;
		} else if (langs.size() > 1) {
			logger.info("Multiple languages found for doc with id : " + this.getID());
			logger.info("languages are : " + langs + " picking first...");
			return langs.get(0);
		} else {
			logger.info("language found for doc with id : " + this.getID());
			logger.info("language is : " + langs + " picking first...");
			return langs.get(0);
		}
	}

	public String getDocId() {
		List<String> docIDs = this.getFields().get(DOCID_FIELD);
		if (docIDs == null) {
			logger.info("DocID is null for doc");
			return null;
		} else if (docIDs.size() != 1) {
			logger.info("0 or more than 1 docIDs found for doc");
			logger.info("docIDs are : " + docIDs);
			return null;
		} else {
			logger.info("DocID found for doc");
			logger.info("docID is : " + docIDs + " picking first...");
			return docIDs.get(0);
		}
	}

	public String getColId() {
		List<String> colIDs = this.getFields().get(COLLECTION_FIELD);
		if (colIDs == null) {
			logger.info("ColId is null for doc with id : " + this.getID());
			return null;
		} else if (colIDs.size() != 1) {
			logger.info("0 or more than 1 colIDs found for doc with id : " + this.getID());
			logger.info("colIDs are : " + colIDs);
			return null;
		} else {
			logger.info("ColId found for doc with id : " + this.getID());
			logger.info("colIDs is : " + colIDs + " picking first...");
			return colIDs.get(0);
		}
	}

	@SuppressWarnings("unused")
	private static String calculateID(Map<String, ArrayList<String>> keys) {
		return Joiner.on("_").join(keys.get(ID_KEY_FIELD));
	}

	@Override
	public String toString() {
		return "ID : " + this.getID() + ", values : " + this.toJSON();
	}

	// value is the whole document keys + fields

	public String toJSON() {
		return gson.toJson(this);
	}

	// for each key in keys a view should be created with name
	// KEYNAME_DATATYPE_view
	// in the initialization the views can be determined

	public static Document loadXMLFromString(String xml) throws Exception {
		long before = System.currentTimeMillis();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		Document doc = builder.parse(is);

		long after = System.currentTimeMillis();
		logger.info("load xml after : " + (after - before) / 1000.0 + " secs");
		
		return doc;
	}

	public Map<String, List<String>> getFields() {
		return fields;
	}
	
	
//	public static void main(String[] args) throws Exception {
//		String fname = "/home/alex/workspace_3.0.0_release/elasticsearch-gcube_trunk/src/test/resources/FTRowset.xml";
//		String rowset = Files.toString(new File(fname), Charsets.UTF_8);
//		
//		System.out.println("rowset : " + rowset);
//		FullTextIndexDocument doc = new FullTextIndexDocument(rowset);
//		System.out.println(doc.getColId());
//		System.out.println(doc.getID());
//		System.out.println(doc.getDocLang());
//		System.out.println(doc.toJSON());
//	}
}
