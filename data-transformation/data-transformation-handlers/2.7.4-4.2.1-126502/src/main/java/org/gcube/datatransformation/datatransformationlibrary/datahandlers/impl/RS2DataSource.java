package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GRS2ExceptionWrapper;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.StrDataElement;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.ContentTypeDataSource;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandlerDefinitions;
import org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataSource;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.reports.Record.Type;
import org.gcube.datatransformation.datatransformationlibrary.reports.ReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.xml.XmlEscapers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * This {@link DataSource} fetches data from a result set.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class RS2DataSource implements DataSource, ContentTypeDataSource {

	private static Logger log = LoggerFactory.getLogger(RS2DataSource.class);

	private Boolean hideRecs = false;
	private ForwardReader<GenericRecord> reader = null;
	private boolean isClosed = false;
	private String collectionID;
	
	private BiMap<String, String> mappings;
	
	private Gson gson = new Gson();
	
	/**
	 * This constructor for {@link RS2DataSource}
	 * 
	 * @param input
	 *            The input value of the {@link DataSource}.
	 * @param inputParameters
	 *            The output parameters of the <tt>DataSink</tt>.
	 * @throws Exception
	 *             If the result set could not be created.
	 */
	public RS2DataSource(String input, Parameter[] inputParameters) throws Exception {
		reader = new ForwardReader<GenericRecord>(URI.create(input));
		collectionID = input;
		
		if(inputParameters!=null){
			for(Parameter param: inputParameters){
				if(param!=null && param.getName()!=null && param.getValue()!=null){
					if(param.getName().equalsIgnoreCase("hideRecs")){
						try {
							hideRecs = Boolean.parseBoolean(param.getValue());
							log.debug("RS2DataSource will be set with hideRecs set to " + hideRecs);
					} catch (Exception e) { }
					}

				}
				
				if (param != null && param.getName() != null && param.getValue() != null) {
					if (param.getName().equalsIgnoreCase("mappings")) {
						mappings = HashBiMap.create();
						Map<String, String> map = gson.fromJson(param.getValue(), new TypeToken<Map<String, String>>(){}.getType());
						mappings.putAll(map);
						mappings = mappings.inverse();
					}
				}
			}
		}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#close()
	 */
	public void close() {
		if (!isClosed)
			try {
				isClosed = true;
				log.debug("Total records read: " + reader.totalRecords());
				reader.close();
				ReportManager.closeReport();
			} catch (Exception e) {
				log.error("Could not close ForwardReader ", e);
			}
	}

	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.datahandlers.DataHandler#isClosed()
	 * @return true if the <tt>DataHandler</tt> has been closed.
	 */
	public boolean isClosed() {
		return isClosed;
	}

	@Override
	public boolean hasNext() {
		
		try {
			return (!(reader.getStatus() == Status.Dispose || (reader.getStatus() == Status.Close && reader.availableRecords() == 0)));
		} catch (GRS2ReaderException e) {
			log.warn("Got Exception", e);
			return false;
		}
	}

	@Override
	public DataElement next() throws Exception {
		GenericRecord rec = null;
		try {
			rec = reader.get(60, TimeUnit.SECONDS);
		} catch (GRS2ReaderException e) {
			log.error("Did not manage to read result set element", e);
		}

		if (rec == null) {
			if (hasNext())
				log.warn("Result set returned null object");
			return null;
		}
		
		if (rec instanceof GRS2ExceptionWrapper) {
			Throwable t = ((GRS2ExceptionWrapper)rec).getEx();
			throw new Exception(t);
		}
		
		if(hideRecs) {
			rec.hide();
		}
		
		StringBuilder payload = new StringBuilder("<record>");

		for (Field field : rec.getFields()) {
			if (field instanceof StringField) {
				String fieldName = field.getFieldDefinition().getName();
				payload.append("<");
				payload.append(mappings != null && mappings.get(fieldName) != null ? mappings.get(fieldName) : fieldName);
				payload.append(">");

				payload.append(XmlEscapers.xmlContentEscaper().escape(((StringField) field).getPayload()));
				payload.append("</");
				payload.append(mappings != null && mappings.get(fieldName) != null ? mappings.get(fieldName) : fieldName);
				payload.append(">");
			} else
				log.warn("File field not supported: " + field.getFieldDefinition().getName());
		}
		payload.append("</record>");
		
		return manageObject(String.valueOf(rec.getID()), payload.toString());
	}

	@Override
	public ContentType nextContentType() {
		return new ContentType("application/xml", new ArrayList<Parameter>());
	}
	
	private DataElement manageObject(String id, String payload) {
		StrDataElement de = null;
		try {
			de = StrDataElement.getSourceDataElement();
			de.setId(id);
			de.setContent(payload);

			de.setAttribute(DataHandlerDefinitions.ATTR_COLLECTION_ID, collectionID);
			de.setAttribute(DataHandlerDefinitions.ATTR_CONTENT_OID, id);

			de.setContentType(new ContentType("application/xml", new ArrayList<Parameter>()));

			log.trace("Object with id " + id + " was added for processing");
			ReportManager.manageRecord(id, "Object with id " + id + " was added for processing", org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status.SUCCESSFUL, Type.SOURCE);
		} catch (Exception e) {
			log.error("Could not manage to fetch the object " + id, e);
			ReportManager.manageRecord(id, "Object with id " + id + " could not be fetched TM", org.gcube.datatransformation.datatransformationlibrary.reports.Record.Status.FAILED, Type.SOURCE);
		}
		return de;
	}
	
	public static void main(String[] args) throws Exception {
		String input = "http://dl07.di.uoa.gr:8080/searchsystemservice-3.3.0-3.3.0/search?query=5d86adb5-c137-44bd-bbb2-95bd39bda6cd+%3D+%22tuna%22+project+*&all=false&names=true";
		URL url = new URL(input);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.addRequestProperty("gcube-scope", "/gcube/devNext/NextNext");

		String locator = new Gson().fromJson(inputStreamToString(urlConnection.getInputStream()), JsonObject.class).get("grslocator").getAsString();
		
		String mappings = "{\"gDocCollectionID\":\"48a99f5b-8a64-44df-a98a-2c481c2efa87\",\"subject\":\"66e74338-9fd9-4eda-94ea-e50858a71d8d\",\"rights\":\"2e8de39a-61b9-4e99-ae17-5d5e68199499\",\"relation\":\"0a46a3a2-d61d-4b5e-8c4d-9707d0387a63\",\"allIndexes\":\"5d86adb5-c137-44bd-bbb2-95bd39bda6cd\",\"format\":\"43e3e1fc-ea86-43b3-ac8f-340c069c371f\",\"gDocCollectionLang\":\"ddf17c09-8959-4787-82ca-1d05af19ee82\",\"date\":\"72daf4d7-08c4-4a87-8ee8-e96d60b3340f\",\"type\":\"1b71d39e-48b8-4b0d-a63d-864754124ab3\",\"creator\":\"fe789b9b-44c2-4e27-8f38-1a41e0805711\",\"publisher\":\"7cdd2692-55e7-40bf-bd7d-8d85700af224\",\"title\":\"d0838339-59c4-4606-9578-bd7632710061\",\"source\":\"0b7e589b-d53f-40cc-9cba-623f53a60896\",\"coverage\":\"2d5e5f70-ff8a-4375-9008-a4852dedcb48\",\"description\":\"e6b21194-8b73-41a2-bcd6-8d82b96dc9a1\",\"contributor\":\"afcf248f-2e8e-4be2-9774-4b061f16b00b\",\"S\":\"bedaac02-62df-46fc-ae46-97efa78a73a5\",\"ObjectID\":\"d34be4d1-03a3-4008-8929-592fec5c0056\",\"language\":\"c5aacec7-f97c-4dc3-97eb-f5df3f30653a\",\"identifier\":\"423f88f6-4652-4ec2-94c0-f6abbce49476\"}";
		Parameter[] param = new Parameter[]{new Parameter("mappings", mappings)};
		RS2DataSource source = new RS2DataSource(locator, param);
		
		while(source.hasNext()) {
			DataElement de = source.next();
			System.out.println(de.getId());
			System.out.println(((StrDataElement)de).getStringContent());
		}
	}
	
	public static String inputStreamToString(InputStream inputStream) throws IOException {
		InputStream in = inputStream;
		InputStreamReader is = new InputStreamReader(in);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while (read != null) {
			sb.append(read);
			read = br.readLine();
		}
		
		br.close();
		is.close();
		in.close();
		in = null;

		return sb.toString();
	}
}
