package org.gcube.elasticsearch.helpers;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.gcube.elasticsearch.FTNodeCache;
import org.gcube.indexmanagement.common.FullTextIndexType;
import org.gcube.indexmanagement.common.IndexException;
import org.gcube.indexmanagement.common.IndexType;
import org.gcube.indexmanagement.resourceregistry.RRadaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

public class QueryParser {

	private static final Logger logger = LoggerFactory.getLogger(QueryParser.class);

	

	

	public static List<String> createPresentableForIndexTypes(Map<String, List<String>> presentableFieldsPerIndexType,
			Set<String> indexTypes) {
		Set<String> presentables = new HashSet<String>();
		for (String idxType : indexTypes)
			presentables.addAll(presentableFieldsPerIndexType.get(idxType));

		logger.info("for indexTypes : " + indexTypes + " presentables found : " + presentables);
		return new ArrayList<String>(presentables);
	}
	
	public static List<String> createSearchablesForIndexTypes(Map<String, List<String>> searchablesFieldsPerIndexType,
			Set<String> indexTypes) {
		Set<String> searchables = new HashSet<String>();
		for (String idxType : indexTypes)
			searchables.addAll(searchablesFieldsPerIndexType.get(idxType));

		
		logger.info("for indexTypes : " + indexTypes + " searchables found : " + searchables);
		return new ArrayList<String>(searchables);
	}
	
	public static List<String> createHighlightablesForIndexTypes(Map<String, List<String>> highlightableFieldsPerIndexType,
			Set<String> indexTypes) {
		Set<String> highlightables = new HashSet<String>();
		for (String idxType : indexTypes)
			highlightables.addAll(highlightableFieldsPerIndexType.get(idxType));

		
		logger.info("for indexTypes : " + indexTypes + " highlightables found : " + highlightables);
		return new ArrayList<String>(highlightables);
	}
	

	private static String regexCollID = IndexType.COLLECTION_FIELD + "\\s*==\\s*\"(\\S+)\"";
	private static Pattern patternCollID = Pattern.compile(regexCollID);

	public static List<String> getCollectionsIDFromQuery(String queryString) {
		List<String> matches = new ArrayList<String>();
		Matcher m = patternCollID.matcher(queryString);

		while (m.find())
			matches.add(m.group(1).trim());

		return matches;
	}
	
	public static FieldDefinition[] createFieldDefinition(List<String> returnFields, RRadaptor adaptor)
			throws Exception {
		List<FieldDefinition> fieldDef = new ArrayList<FieldDefinition>();
		// add three more fields for the score, the statistics and the docID
		fieldDef.add(new StringFieldDefinition(IndexType.SCORE_FIELD));
		// fieldDef.add(new StringFieldDefinition(IndexType.STATS_FIELD));
		fieldDef.add(new StringFieldDefinition(IndexType.DOCID_FIELD));

		// these cases correspond to the way the worker fills the RS
		// the plus 3 fields are for score, stats and docID
		logger.info("return fields : " + returnFields);

		if (returnFields != null) {
			for (String fieldName : returnFields) {
	
				String fieldID = null;
				
				if (adaptor == null) { 
					fieldID = fieldName;
				} else {
					fieldID = adaptor.getFieldIDFromName(fieldName);
				}

				fieldDef.add(new StringFieldDefinition(fieldID));
			}
		}
		// fieldDef.add(new StringFieldDefinition(IndexType.PAYLOAD_FIELD));

		return Iterables.toArray(fieldDef, FieldDefinition.class);
	}

	public static String createIndexTypekey(String indexType, String scope) {
		return indexType + "_" + scope;
	}

//	public static FullTextIndexType retrieveIndexType(String indexTypeStr, String scope, FTNodeCache cache) {
//		
//		return retrieveIndexType(indexTypeStr, scope, cache);
//	}

	public static FullTextIndexType retrieveIndexType(String indexTypeStr, String scope, FTNodeCache cache) {

		logger.info("scope : " + scope);
		
		FullTextIndexType indexType = null;

		logger.info("Retrieving index type for : " + indexTypeStr);
		if (cache.cachedIndexTypes.containsKey(createIndexTypekey(indexTypeStr, scope))) {
			logger.info("Index type : " + indexTypeStr + " found in cache");
			indexType = cache.cachedIndexTypes.get(createIndexTypekey(indexTypeStr, scope));
		} else {
			logger.info("Index type : " + indexTypeStr + " NOT found in cache");

			indexType = new FullTextIndexType(indexTypeStr, scope);
			
			logger.info("Retrieved from IS indextype : " + indexType);
			
			addFullTextIndexTypeIntoCache(indexTypeStr, scope, indexType, cache);
		}
		// logger.info("Index type returned : " + indexType);

		return indexType;
	}

	public static void addFullTextIndexTypeIntoCache(String indexTypeStr, String scope,
			FullTextIndexType indexType, FTNodeCache cache) {

		if(cache.cachedIndexTypes.containsKey(createIndexTypekey(indexTypeStr, scope)))
			return;
		logger.info("Index type : " + indexTypeStr + "adding into cache");
		cache.cachedIndexTypes.put(createIndexTypekey(indexTypeStr, scope), indexType);

		logger.info("Cache : " + cache.cachedIndexTypes);
		return;
	}
	
	private static String extractValueFromMap(Map<String, ? extends Object> docMap, String key, boolean isSource){
		if (isSource){
			Object val = docMap.get(key);
			return (val == null) ? null : val.toString();
		}
		else {
			return ((SearchHitField)docMap.get(key)).getValue();
		}
	}
	
	private static String extractValueFromHit(Object obj, boolean isSource){
		if (isSource){
			return (obj == null) ? null : obj.toString();
		}
		else {
			return ((SearchHitField)obj).getValue();
		}
	}
	
	public static boolean writeSearchHitFieldsInResultSet(SearchHit hit, RecordWriter<GenericRecord> rsWriter,
			List<String> returnFields, int maxFragmentCount, long rsTimeout)
			throws GRS2WriterException {
		if (rsWriter.getStatus() != Status.Open)
			return false;
		
		// the fields for this record
		List<gr.uoa.di.madgik.grs.record.field.Field> fields = new ArrayList<gr.uoa.di.madgik.grs.record.field.Field>();

		// TODO: other statistics? wc? terms?

		Map<String, SearchHitField> docMap = null;
		docMap = hit.getFields();
		logger.trace("getting docMap from fields. fields : " + hit.getFields().size());
		
		logger.trace("Hit from index : ");
		logger.trace("-------------------------------------");
		if (logger.isTraceEnabled())
			for (Entry<String, SearchHitField> f : docMap.entrySet())
				logger.trace(f.getKey() + ":" + f.getValue().getValue());
		logger.trace("-------------------------------------");
		// field 0 is the score

		logger.trace("Adding score field with value : " + hit.getScore());
		
		fields.add(new StringField(String.valueOf(hit.getScore())));
		// TODO: can we remove this?
		//String docStatistics = "<docStatistics><rank>" + String.valueOf(hit.getScore()) + "</rank></docStatistics>";
		//fields.add(new StringField(docStatistics));

		String fieldContentDocID = docMap.containsKey(IndexType.DOCID_FIELD) && docMap.get(IndexType.DOCID_FIELD) != null && docMap.get(IndexType.DOCID_FIELD).getValue() != null ? docMap.get(IndexType.DOCID_FIELD).getValue().toString() : "NoMetaId";

		logger.trace("Adding " + IndexType.DOCID_FIELD + " field with value : " + fieldContentDocID);
		fields.add(new StringField(fieldContentDocID));

		// fields.add(new
		// StringField(XMLTokenReplacer.XMLResolve(doc.get(IndexType.PAYLOAD_FIELD))));
		// fields.add(new StringField("dummy payload"));

		if (returnFields != null) {
			logger.trace("returnFields : " + returnFields);

			for (String fieldName : returnFields) {
				String fieldContent = null;

				if (fieldName.equalsIgnoreCase(IndexType.SNIPPET))
					fieldContent = SnippetsHelper.createSnippetString(hit, maxFragmentCount);
				else
					fieldContent = docMap.containsKey(fieldName) && docMap.get(fieldName) != null && docMap.get(fieldName).getValue() != null ? docMap.get(fieldName).getValue().toString() : "";

				logger.trace("adding field : " + fieldName + " with value : " + fieldContent);
				fields.add(new StringField(fieldContent));
			}
		}

		// while the reader hasn't stopped reading
		if (rsWriter.getStatus() != Status.Open)
			return false;

		// the current RS record
		GenericRecord rec = new GenericRecord();
		// set the fields in the record
		rec.setFields(Iterables.toArray(fields, gr.uoa.di.madgik.grs.record.field.Field.class));

		while (!rsWriter.put(rec, rsTimeout, TimeUnit.SECONDS)) {
			// while the reader hasn't stopped reading
			if (rsWriter.getStatus() != Status.Open)
				break;
		}

		return true;
	}


	public static boolean writeSearchHitInResultSet(SearchHit hit, RecordWriter<GenericRecord> rsWriter,
			List<String> returnFields, int maxFragmentCount, long rsTimeout)
			throws GRS2WriterException {
		if (rsWriter.getStatus() != Status.Open)
			return false;
		
		// the fields for this record
		int arrSize = returnFields != null ? returnFields.size() + 2 : 10; 
		ArrayList<gr.uoa.di.madgik.grs.record.field.Field> fields = new ArrayList<gr.uoa.di.madgik.grs.record.field.Field>(arrSize);

		/*
		Map<String, ?> docMap = null;
		boolean isSource = true;
		if (hit.getFields() != null){
			docMap = hit.getFields();
			logger.trace("getting docMap from fields. fields : " + hit.getFields().size());
		} else {
			docMap = hit.getSource();
			isSource = true;
			logger.trace("getting docMap from source. fields : " + hit.getSource().size());
		}*/
		
		Map<String, Object> docMap = hit.getSource();
		boolean isSource = true;
		logger.trace("getting docMap from source. fields : " + hit.getSource().size());
		
		logger.trace("Hit from index : ");
		logger.trace("-------------------------------------");
		if (logger.isTraceEnabled())
			for (Entry<String, ?> f : docMap.entrySet())
				logger.trace(f.getKey() + ":" + extractValueFromHit(f.getValue(), isSource));
		logger.trace("-------------------------------------");
		// field 0 is the score

		logger.trace("Adding score field with value : " + hit.getScore());
		
		fields.add(new StringField(String.valueOf(hit.getScore())));

		String fieldContentDocID = 
				hit.getFields().containsKey(IndexType.DOCID_FIELD) ? 
						extractValueFromHit(hit.getFields().get(IndexType.DOCID_FIELD), false) 
						: "NoMetaId";

		logger.trace("Adding " + IndexType.DOCID_FIELD + " field with value : " + fieldContentDocID);
		fields.add(new StringField(fieldContentDocID));

		if (returnFields != null) {
			logger.trace("returnFields : " + returnFields);
			
			for (String fieldName : returnFields) {
				String fieldContent = null;
	
				if (fieldName.equalsIgnoreCase(IndexType.SNIPPET))
					fieldContent = SnippetsHelper.createSnippetString(hit, maxFragmentCount);
				else
					fieldContent = docMap.containsKey(fieldName) ? extractValueFromMap(docMap, fieldName, isSource) : "";
	
				logger.trace("adding field : " + fieldName + " with value : " + fieldContent);
				fields.add(new StringField(fieldContent));
			}
		}

		// while the reader hasn't stopped reading
		if (rsWriter.getStatus() != Status.Open)
			return false;

		// set the fields in the record
		GenericRecord rec = new GenericRecord();
		rec.setFields(Iterables.toArray(fields, gr.uoa.di.madgik.grs.record.field.Field.class));

		while (!rsWriter.put(rec, rsTimeout, TimeUnit.SECONDS)) {
			// while the reader hasn't stopped reading
			if (rsWriter.getStatus() != Status.Open)
				break;
		}

		return true;
	}

	public static RecordWriter<GenericRecord> initRSWriterForSearchHits(List<String> returnFields, RRadaptor adaptor) throws IndexException, GRS2WriterException {
		logger.info("Initializing gRS2 writer");
		logger.info("(1/3) getting field definitions");
		FieldDefinition[] fieldDef = null;
		try {
			fieldDef = createFieldDefinition(returnFields, adaptor);
		} catch (Exception e) {
			logger.error("Could not create field definition: ", e);
			throw new IndexException(e);
		}

		logger.info("(2/3) creating record definitions");
		RecordDefinition[] definition = new RecordDefinition[] { new GenericRecordDefinition(fieldDef) };

		logger.info("(3/3) creating rsWriter");
		return new RecordWriter<GenericRecord>(new TCPWriterProxy(), definition, 200, 1, 0.5f);

	}
	
}
