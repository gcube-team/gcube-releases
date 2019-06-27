/**
 *
 */
package org.gcube.data.analysis.dminvocation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.gcube.data.analysis.dminvocation.model.DataMinerInvocation;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;



/**
 * The Class DataMinerInvocationManager.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 10, 2018
 */
public class DataMinerInvocationManager {

	private static DataMinerInvocationManager singleInstance = null;
    private JAXBContext jaxbContext;
	private Schema schema;

	/*
	 *
	 * JAXBContext is thread safe and should only be created once and reused to avoid the cost of initializing the metadata multiple times.
	 * Marshaller and Unmarshaller are not thread safe, but are lightweight to create and could be created per operation.
	*/

	/**
	 * Instantiates a new data miner invocation manager.
	 *
	 * @throws JAXBException the JAXB exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private DataMinerInvocationManager() throws JAXBException, IOException, SAXException{
		jaxbContext= JAXBContext.newInstance(DataMinerInvocation.class);
		schema = generateSchema();
	}


	/**
	 * Gets the single instance of DataMinerInvocationManager.
	 *
	 * @return single instance of DataMinerInvocationManager
	 * @throws JAXBException the JAXB exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	public static DataMinerInvocationManager getInstance() throws JAXBException, IOException, SAXException {

		  if (singleInstance == null)
			  singleInstance = new DataMinerInvocationManager();

	        return singleInstance;
	}


	/**
	 * Marshaling xml.
	 *
	 * @param dmInvocation the dm invocation
	 * @param validateModel the validate model
	 * @param prettyPrint set Pretty Printing
	 * @return the string
	 * @throws JAXBException the JAXB exception
	 */
	public String marshalingXML(DataMinerInvocation dmInvocation, boolean validateModel, boolean prettyPrint) throws JAXBException
	{
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		if(prettyPrint)
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	    if(validateModel)
			jaxbMarshaller.setSchema(schema);

		jaxbMarshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, MediaType.ApplicationXML.getMimeType());
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    jaxbMarshaller.marshal(dmInvocation, baos);
	    return new String(baos.toByteArray());
	}


	/**
	 * Marshaling json.
	 *
	 * @param dmInvocation the dm invocation
	 * @param validateModel the validate model. If true checks the input DataMinerInvocation against the dataminer-invocation-model
	 * @param prettyPrint set Pretty Printing
	 * @return the string
	 * @throws JAXBException the JAXB exception
	 */
	public String marshalingJSON(DataMinerInvocation dmInvocation, boolean validateModel, boolean prettyPrint) throws JAXBException{


		GsonBuilder gsonBuilder = new GsonBuilder();
	    DataMinerInvocationJSONAdaptor jsonAdaptor = new DataMinerInvocationJSONAdaptor();
	    jsonAdaptor.registerRequiredField("operator-id");
	    gsonBuilder.registerTypeAdapter(DataMinerInvocation.class, jsonAdaptor);

	    Gson gson = prettyPrint?gsonBuilder.setPrettyPrinting().create():gsonBuilder.create();
		DataMinerInvocationJSONWrapper wrapper = new DataMinerInvocationJSONWrapper(dmInvocation);
		return gson.toJson(wrapper);
	}


	/**
	 * Unmarshaling xml.
	 *
	 * @param dmInvocationIS the dm invocation is
	 * @param validateModel the validate model. If true checks the InputStream against the dataminer-invocation-model
	 * @return the data miner invocation
	 * @throws JAXBException the JAXB exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public DataMinerInvocation unmarshalingXML(InputStream dmInvocationIS, boolean validateModel) throws JAXBException, IOException {
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

	    if(validateModel)
	    	jaxbUnmarshaller.setSchema(schema);

	    jaxbUnmarshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, MediaType.ApplicationXML.getMimeType());
	    return (DataMinerInvocation) jaxbUnmarshaller.unmarshal(dmInvocationIS);
	}


	/**
	 * Unmarshaling json.
	 *
	 * @param dmInvocationIS the dm invocation is
	 * @param validate the validate
	 * @return the data miner invocation
	 * @throws JsonSyntaxException the json syntax exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public DataMinerInvocation unmarshalingJSON(InputStream dmInvocationIS, boolean validate) throws JsonSyntaxException, IOException {

		GsonBuilder gsonBuilder = new GsonBuilder();

		if(validate){
		    DataMinerInvocationJSONAdaptor jsonAdaptor = new DataMinerInvocationJSONAdaptor();
		    gsonBuilder.registerTypeAdapter(DataMinerInvocation.class, jsonAdaptor);
		}

	    Gson gson = gsonBuilder.create();
		String json = convertToString(dmInvocationIS);
	    DataMinerInvocationJSONWrapper wrapper = gson.fromJson(json, DataMinerInvocationJSONWrapper.class);
	    return wrapper.getDataminerInvocation();
	}


	/**
	 * Generate schema.
	 *
	 * @return the schema
	 * @throws JAXBException the JAXB exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SAXException the SAX exception
	 */
	private Schema generateSchema() throws JAXBException, IOException, SAXException {

	    // generate schema
	    ByteArrayStreamOutputResolver schemaOutput = new ByteArrayStreamOutputResolver();
	    jaxbContext.generateSchema(schemaOutput);

	    // load schema
	    ByteArrayInputStream schemaInputStream = new ByteArrayInputStream(schemaOutput.getSchemaContent());
	    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	   return sf.newSchema(new StreamSource(schemaInputStream));
	}



	/**
	 * The Class ByteArrayStreamOutputResolver.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * Dec 7, 2018
	 */
	private static class ByteArrayStreamOutputResolver extends SchemaOutputResolver {

	    private ByteArrayOutputStream schemaOutputStream;

	    /* (non-Javadoc)
    	 * @see javax.xml.bind.SchemaOutputResolver#createOutput(java.lang.String, java.lang.String)
    	 */
    	public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {

	        schemaOutputStream = new ByteArrayOutputStream();
	        StreamResult result = new StreamResult(schemaOutputStream);

	        // We generate single XSD, so generator will not use systemId property
	        // Nevertheless, it validates if it's not null.
	        result.setSystemId("");

	        return result;
	    }

	    /**
    	 * Gets the schema content.
    	 *
    	 * @return the schema content
    	 */
    	public byte[] getSchemaContent() {
	        return schemaOutputStream.toByteArray();
	    }
	}


	/**
	 * Convert to string.
	 *
	 * @param inputStream the input stream
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String convertToString(InputStream inputStream)throws IOException {

		StringBuilder textBuilder = new StringBuilder();
		try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
			int c = 0;
			while ((c = reader.read()) != -1) {
				textBuilder.append((char) c);
			}
		}
		return textBuilder.toString();
	}
}
