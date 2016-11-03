package org.gcube.search.sru.consumer.service.helpers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.gcube.datatransformation.DataTransformationClient;
import org.gcube.datatransformation.client.library.beans.Types.ContentType;
import org.gcube.datatransformation.client.library.beans.Types.Input;
import org.gcube.datatransformation.client.library.beans.Types.Output;
import org.gcube.datatransformation.client.library.beans.Types.Parameter;
import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnit;
import org.gcube.datatransformation.client.library.exceptions.EmptySourceException;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ParserHelper {

	static final Logger logger = LoggerFactory.getLogger(ParserHelper.class);

	public static List<Map<String, String>> parseResponse(DataTransformationClient dtsclient, String url, SruConsumerResource resource, List<String> projections, Map<String, String> fieldsMapping, String snippetTranslatedField)
			throws Exception{
	
		List<Map<String, String>> records = transformSRUSource(dtsclient, url);
		List<Map<String, String>> projectedRecords = Lists.newArrayList();
		
		for (Map<String, String> record : records){
			Map<String, String> projectedRecord = applyProjection(record,
					resource.getRecordIDField(), resource.getCollectionID(), projections, fieldsMapping, snippetTranslatedField);
			
			projectedRecords.add(projectedRecord);
			logger.debug("records : " + record + " projected record : " + projectedRecord);
		}
		
		return projectedRecords;	
		
	}
	
	public static List<Map<String, String>> transformSRUSource(DataTransformationClient dtsclient, String url) throws Exception {

		TransformDataWithTransformationUnit request = new TransformDataWithTransformationUnit();
		request.tpID = "$XSLT_Transformer";
		request.transformationUnitID = "0";

		/* INPUT */
		Input input = new Input();
		input.inputType = "SRUDataSource";
		input.inputValue = url; //
		request.inputs = Arrays.asList(input);

		/* OUTPUT */
		request.output = new Output();
		request.output.outputType = "RS2";

		/* TARGET CONTENT TYPE */
		request.targetContentType = new ContentType();
		request.targetContentType.mimeType = "json/application";

		/* PROGRAM PARAMETERS */
		Parameter xsltParameter1 = new Parameter("xslt:1", "$BrokerXSLT_FlattenXML");
		Parameter xsltParameter2 = new Parameter("finalftsxslt", "$BrokerXSLT_XML_to_JSON");

		Parameter xsltParameter3 = new Parameter("delimiter", " & ");

		request.tProgramUnboundParameters = Arrays.asList(xsltParameter1, xsltParameter2, xsltParameter3);

		request.filterSources = false;
		request.createReport = false;
		
		
		List<Map<String, String>> maps = null;
		
		try {
			String resp = dtsclient.transformDataWithTransformationUnit(request, true, true);
			maps = DataTransformationClient.getMapFromResponse(resp);
		} catch (EmptySourceException e){
			logger.debug("no records returned by dts", e);
			maps = Lists.newArrayList();
		}
		
		logger.info("dts returned " + maps.size() + " records");

		List<Map<String, String>> records = Lists.newArrayList();
		for (Map<String, String> map : maps) {
			String rowset = map.get("Rowset");
			if (rowset != null){
				Map<String, String> rec = transformRowsetToMap(rowset); 
				
				records.add(rec);
			}
		}
		
		return records;
	}
	
	static Map<String, String> transformRowsetToMap(String rowset){
		Gson gson = new Gson();
		
		logger.debug("rowset retrieved by dts and will be parsed to Map<String, Map<String, String>>  : " + rowset);
		
		Map<String, Map<String, String>> rec = gson.fromJson(rowset, 
					new TypeToken<Map<String, Map<String, String>>>() {}.getType());
		
		
		Map<String, String> fields = rec.values().iterator().next();
		
		return fields;
	}
	

	static Map<String, String> applyProjection(
			Map<String, String> rec, String idField, String collectionID,
			List<String> projections, Map<String, String> fieldsMapping, String snippetTranslatedField) {
		List<String> projectionFields = Lists.newArrayList(projections);
//		
		if (idField != null && !projectionFields.contains(idField)) {
			projectionFields.add(0, idField);
		}
		

		Map<String, String> projectedRecord = Maps.newHashMap();

		logger.info("will apply projection for the following fields : " + projectionFields);
		
		for (String projection : projectionFields) {
			String value = rec.get(projection);
			if (value == null)
				continue;

			if (snippetTranslatedField != null & projection.equalsIgnoreCase(snippetTranslatedField)){
				projection = "S";
			}
			
			if (fieldsMapping != null && fieldsMapping.containsKey(projection)){
				projection = fieldsMapping.get(projection);
			} else {
				if (projection.equals(idField)){
					projection = "ObjectID";
				}
			}
			
			projectedRecord.put(projection, value);
		}
		
		if (projectedRecord.get("ObjectID") == null){
			projectedRecord.put("ObjectID", "noID");
		}
		
		if (projectedRecord.get("gDocCollectionID") == null){
			projectedRecord.put("gDocCollectionID", collectionID);
		}
		
		return projectedRecord;
	}
	
	public static void main(String[] args) throws Exception {
		DataTransformationClient dtsclient = new DataTransformationClient();
		dtsclient.setScope("/gcube/devNext");
		dtsclient.randomClient();
		
		System.out.println(transformSRUSource(dtsclient, "http://www.nla.gov.au/apps/srw/search/peopleaustralia?operation=searchRetrieve&query=%28%28%28%28%28%28contributor+%3D+cacikumar%29+or+%28title+%3D+cacikumar%29%29%29+or+%28creator+%3D+cacikumar%29%29%29+or+%28%28%28%28%28contributor+%3D+fish%29+or+%28title+%3D+fish%29%29%29+or+%28creator+%3D+fish%29%29%29%29&maximumRecords=10&recordSchema=info:srw/schema/1/dc-v1.1&version=1.1"));
	}
}
