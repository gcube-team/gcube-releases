package org.gcube.search.sru.consumer.service.helpers;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.gcube.datatransformation.DataTransformationClient;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ResultSetHelpers {
	
	static final long RSTIMEOUT = 30;
	
	final static ExecutorService executorService =  Executors.newCachedThreadPool();
	
	static final Logger logger = LoggerFactory.getLogger(ResultSetHelpers.class);

	static List<String> getFieldsIds(List<String> fieldNames, Map<String, String> namesToIds){
		List<String> ids = Lists.newArrayList();
		
		for (String fieldName : fieldNames) {
			String id = namesToIds != null && namesToIds.containsKey(fieldName) ? namesToIds.get(fieldName) : fieldName;
			ids.add(id);
		}
		
		return ids;
	}
	
	public static String writeResponseFromUrlToGRS2(DataTransformationClient dtsClient, String urlString,
			SruConsumerResource resource, List<String> projections, final BiMap<String, String> fieldsMapping, String snippetTranslatedField) throws Exception {
		// TODO: get input stream and stream the response

		final long starttime = System.currentTimeMillis();

//		String xml = URLHelper.urlToString(urlString);
//
//		logger.info("xml retrieved from url : " + xml);

		final List<String> projectedFields = projections != null ? 
				new ArrayList<String>(projections) : 
					new ArrayList<String>(resource.getPresentables());
				
				
//		final List<Map<String, String>> records = ParserHelpers
//				.parseResponse(xml, resource, recordConverter, projections, fieldsMapping, snippetTranslatedField);
				
		final List<Map<String, String>> records = ParserHelper
				.parseResponse(dtsClient, urlString, resource, projections, fieldsMapping, snippetTranslatedField);

		logger.info("records extracted : " + records);

		final String idField = resource.getRecordIDField();
		

		projectedFields.remove(idField);
		
		if (projectedFields.contains(snippetTranslatedField)){
			projectedFields.remove(snippetTranslatedField);
			projectedFields.add("S");
		}
			
		
		
		logger.info("projectedFields    : " + projectedFields);
		final List<String> projectedFieldsIds = getFieldsIds(projectedFields, fieldsMapping);
		logger.info("projectedFieldsIds : " + projectedFieldsIds);

		final RecordWriter<GenericRecord> rsWriter = initRSWriterForSearchHits(
				idField, projectedFieldsIds);// QueryParser.initRSWriterForSearchHits(returnFields,
											// rradaptor);
		rsWriter.emit(new KeyValueEvent("resultsNumberFinal", String
				.valueOf(records.size())));

		final Runnable writerRun = new Runnable() {
			public void run() {

				logger.info("will write " + records.size()
						+ " to grs2");
				try {
					for (Map<String, String> rec : records) {

						if (!writeRecordTogRS2(rec,
								projectedFieldsIds,
								rsWriter, RSTIMEOUT))
							break;

					}
					if (rsWriter.getStatus() != Status.Dispose)
						rsWriter.close();
				} catch (Exception e) {
					logger.error("Error during search.", e);
				} finally {
					try {
						rsWriter.close();
					} catch (Exception ex) {
						logger.error(
								"Error while closing RS writer.", ex);
					}
				}
				logger.info("total query time : "
						+ (System.currentTimeMillis() - starttime) / 1000.0
						+ " secs");

			}
		};
		executorService.execute(writerRun);

		String locator = rsWriter.getLocator().toString();

		logger.info("results locator : " + locator);

		return locator;
	}

	public static RecordWriter<GenericRecord> initRSWriterForSearchHits(
			String idField, List<String> returnFields) throws Exception {
		logger.info("Initializing gRS2 writer");
		logger.info("(1/3) getting field definitions");
		FieldDefinition[] fieldDef = null;
		try {
			fieldDef = createFieldDefinition(idField, returnFields);
		} catch (Exception e) {
			logger.error("Could not create field definition: ", e);
			throw e;
		}

		logger.info("(2/3) creating record definitions");
		RecordDefinition[] definition = new RecordDefinition[] { new GenericRecordDefinition(
				fieldDef) };

		logger.info("(3/3) creating rsWriter");
		return new RecordWriter<GenericRecord>(new TCPWriterProxy(),
				definition, 200, 1, 0.5f);

	}

	static boolean writeRecordTogRS2(Map<String, String> record,
			List<String> projectedFields,
			RecordWriter<GenericRecord> rsWriter, long rsTimeout)
			throws GRS2WriterException {

		if (rsWriter.getStatus() != Status.Open) {
			logger.info("result set was not open before writing record : "
					+ record);
			return false;
		}

		List<Field> fields = Lists.newArrayList();

		String objectIDField = record.get("ObjectID");
		if (objectIDField == null){
			objectIDField = "noID";
		}
		logger.info("    adding field id with value : " + objectIDField);
		fields.add(new StringField(objectIDField));
		
		fields.add(new StringField(record.get("gDocCollectionID")));

		for (String fieldName : projectedFields) {
			
			String value = record.get(fieldName);
			
			logger.info("    adding field : " + fieldName + " with value : "
					+ value);
			fields.add(new StringField(value));
		}

		if (rsWriter.getStatus() != Status.Open) {
			logger.info("result set was not open after constructing fields of record : "
					+ record);
			return false;
		}

		// set the fields in the record
		GenericRecord rec = new GenericRecord();
		rec.setFields(Iterables.toArray(fields, Field.class));

		while (!rsWriter.put(rec, rsTimeout, TimeUnit.SECONDS)) {
			logger.info("record : " + record + " was not written");

			// while the reader hasn't stopped reading
			if (rsWriter.getStatus() != Status.Open)
				break;
		}

		logger.info("record was successfully written");

		return true;

	}

	public static FieldDefinition[] createFieldDefinition(String idField,
			List<String> returnFields) throws Exception {
		List<FieldDefinition> fieldDef = Lists.newArrayList();
		// add three more fields for the score, the statistics and the docID

		fieldDef.add(new StringFieldDefinition("ObjectID"));
		fieldDef.add(new StringFieldDefinition("gDocCollectionID"));

		// these cases correspond to the way the worker fills the RS
		// the plus 3 fields are for score, stats and docID

		logger.info("idField        : " + idField);
		logger.info("return  fields : " + returnFields);

		if (returnFields != null) {
			for (String fieldName : returnFields) {

				String fieldID = fieldName;

				fieldDef.add(new StringFieldDefinition(fieldID));
			}
		}
		// fieldDef.add(new StringFieldDefinition(IndexType.PAYLOAD_FIELD));

		return Iterables.toArray(fieldDef, FieldDefinition.class);
	}
}
