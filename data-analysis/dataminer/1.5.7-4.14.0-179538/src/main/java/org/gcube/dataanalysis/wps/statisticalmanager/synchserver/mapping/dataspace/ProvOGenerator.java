package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.dataspace;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

public class ProvOGenerator {

	static String document ="<prov:document  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
			"xmlns:prov=\"http://www.w3.org/ns/prov#\" xmlns:d4s=\"http://d4science.org/#\">" +
			"#DOCUMENT#" +
			"</prov:document>";

	static String activity = "<prov:activity prov:id=\"d4s:#ID#\">"+
			"<prov:startTime>#START_TIME#</prov:startTime>"+
			"<prov:endTime>#END_TIME#</prov:endTime>"+
			"<prov:type xsi:type=\"xsd:QName\">d4s:computation</prov:type>"+
			"<prov:softwareAgent prov:id=\"d4s:dataminer.d4science.org\" />"+
			"<prov:person prov:id=\"d4s:#PERSON#\" />"+
			"#ENTITIES#"+
			"</prov:activity>";

	static String entity="<prov:entity prov:id=\"d4s:#ENTITY_NAME#\">"+
			//"<prov:type xsi:type=\"xsd:QName\"></prov:type>"+
			"<prov:value xsi:type=\"xsd:string\">#ENTITY_VALUE#</prov:value>"+
			"#SUBENTITY#"+
			"</prov:entity>";

	static String entityWithTime="<prov:entity prov:id=\"d4s:#ENTITY_NAME#\">"+
			//"<prov:type xsi:type=\"xsd:QName\"></prov:type>"+
			"<prov:value xsi:type=\"xsd:string\">#ENTITY_VALUE#</prov:value>"+
			"<prov:time>#TIME#</prov:time>"+
			"#SUBENTITY#"+
			"</prov:entity>";

	static String attribute = "<prov:type xsi:type=\"xsd:QName\">d4s:#NAME#</prov:type>";

	static String referenceActivity = "<prov:activity prov:ref=\"d4s:#ID#\"/>";
	static String referenceEntity = "<prov:entity prov:ref=\"d4s:#ID#\"/>";


	public static String getDataIOAttribute(String IO){
		return attribute(IO);
	}
	public static String getDataTypeAttribute(String type){
		return attribute(type);
	}
	public static String getDataDescriptionEntity(String datadescription){
		return entity(DataspaceManager.data_description, datadescription);
	}

	public static String getOperatorRefEntity(String operator_id){
		return refentity(operator_id);
	}

	public static String getComputationRefEntity(String computation_id){
		return refactivity(computation_id);
	}

	public static String dataToEntity(StoredData data){
		String io = getDataIOAttribute(data.provenance.name());
		String type = getDataTypeAttribute(data.type);
		String description = getDataDescriptionEntity(data.description);
		String operator = getOperatorRefEntity(data.operator);
		String computation = getComputationRefEntity(data.computationId);
		String subentity = computation+operator+description+io+type;
		String dataEntity = completeEntityWithTime(data.id, data.payload, data.creationDate, subentity);
		return dataEntity;
	}

	public static String getStatusEntity(String status){
		return entity(DataspaceManager.status, status);
	}

	public static String getExecutionPlatformEntity(String executionPlatform){
		return entity(DataspaceManager.execution_platform, executionPlatform);
	}

	public static String getOperatorDescriptionEntity(String description){
		return entity(DataspaceManager.operator_description, description);
	}

	public static String getOperatorEntity(String operator){
		return entity(DataspaceManager.operator, operator);
	}

	public static String getOperatorID(String operatorId){
		return entity(DataspaceManager.operator_id, operatorId);
	}

	public static String getVREEntity(String vre){
		return entity(DataspaceManager.vre, vre);
	}

	public static String computationToAction(ComputationData computation,String subEntities){
		String status = getStatusEntity(computation.status);
		String description = getOperatorDescriptionEntity(computation.operatorDescription);
		String operator = getOperatorEntity(computation.operator);
		String operatorId = getOperatorID(computation.operatorId);
		String vre = getVREEntity(computation.vre);
		String subents =operator+operatorId+description+vre+status+subEntities;
		String activity = completeActivity(computation.id,computation.startDate,computation.endDate,computation.user,subents);

		return activity;
	}

	public static String toProvO(ComputationData computation, List<StoredData> input, List<StoredData> output){
		StringBuffer sb = new StringBuffer();
		for (StoredData in:input){
			sb.append(dataToEntity(in));
		}
		for (StoredData out:output){
			sb.append(dataToEntity(out));
		}

		String action = computationToAction(computation, sb.toString());
		String documentString = document.replace("#DOCUMENT#", action);
		documentString = formatXML(documentString);

		return documentString;

	}


	public static String entity(String name, String value){
		return entity.replace("#ENTITY_NAME#", name).replace("#ENTITY_VALUE#", value).replace("#SUBENTITY#","");
	}
	public static String refentity(String id){
		return referenceEntity.replace("#ID#", id);
	}
	public static String refactivity(String id){
		return referenceActivity.replace("#ID#", id);
	}
	public static String attribute(String name){
		return attribute.replace("#NAME#", name);
	}

	public static String entityWithTime(String name, String value,String time){
		return entity.replace("#ENTITY_NAME#", name).replace("#ENTITY_VALUE#", value).replace("#TIME#", time).replace("#SUBENTITY#","");
	}

	public static String completeEntityWithTime(String name, String value,String time,String subEntity){
		return entity.replace("#ENTITY_NAME#", name).replace("#ENTITY_VALUE#", value).replace("#TIME#", time).replace("#SUBENTITY#",subEntity);
	}

	public static String completeActivity(String id, String startTime,String endTime,String person, String subEntity){
		return activity.replace("#ID#", id).replace("#PERSON#", person).replace("#START_TIME#", startTime).replace("#END_TIME#", endTime).replace("#ENTITIES#",subEntity);
	}


	public static String formatXML(String input)
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "3");

			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(parseXml(input));
			transformer.transform(source, result);
			return result.getWriter().toString();
		} catch (Exception e)
		{
			e.printStackTrace();
			return input;
		}
	}

	private static org.w3c.dom.Document parseXml(String in)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

}
