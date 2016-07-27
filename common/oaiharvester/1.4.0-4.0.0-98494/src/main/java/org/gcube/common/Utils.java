package org.gcube.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gcube.common.data.Header;
import org.gcube.common.data.Metadata;
import org.gcube.common.data.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utils {
	private static Logger log = LoggerFactory.getLogger(Utils.class);
	private static boolean debugging = false;


	public static OMElement getReaderFromHttpGet(String baseUrl,String verb) throws ClientProtocolException, IOException, XMLStreamException, InterruptedException  {
		//		HttpClient httpclient = new DefaultHttpClient();	
		
//		System.out.println("getReaderFromHttpGet");
	String url = baseUrl+"?verb="+verb;
//		String url = "http://www.nature.com/oai/request?verb=ListRecords&resumptionToken=166460ba-c717-4642-94fc-3611ab23b3c5";
//		String url = "http://darchive.mblwhoilibrary.org:8080/oai/request?verb=ListRecords&resumptionToken=MTo2NDAwfDI6fDM6fDQ6fDU6b2FpX2Rj";
		log.trace("getting records from: " + url);

//		System.out.println("***************url repository: " + url);

		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpGet httpget = new HttpGet(url);

	
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
			StatusLine status = response.getStatusLine();

			if(debugging) {
				//				System.out.println("debugging");
				log.debug("Debuggin " + response.getStatusLine());
				org.apache.http.Header[] header =  response.getAllHeaders();
				for(int i=0;i<header.length;i++) {
					log.debug(header[i].getName()+" value: "+header[i].getValue());
				}
			}

			if(status.getStatusCode() == 503){
				//				System.out.println("getStatusCode() == 503");
				org.apache.http.Header[] headers = response.getAllHeaders();
				for(int i=0;i<headers.length;i++) {
					if(headers[i].getName().equals("Retry-After")) {
						String retry_time = headers[i].getValue();
						log.info("Retry-After: " + retry_time);
						Thread.sleep(Integer.parseInt(retry_time)*1000);
						httpclient.getConnectionManager().shutdown();
						httpclient = HttpClientBuilder.create().build();
						response = httpclient.execute(httpget);					
					}
				}
			}

		}
		catch(ClientProtocolException clientProtocolException) {
			throw new ClientProtocolException(clientProtocolException);
		}
		catch(IOException ioException) {
			throw new IOException(ioException);
		}

		HttpEntity entity = response.getEntity();
		InputStream instream = null;
		BufferedInputStream bis = null;
		XMLStreamReader parser = null;

		try {
			instream = entity.getContent();
			bis = new BufferedInputStream(instream);
		}
		catch(IOException ioException) {
			throw new IOException(ioException);
		}

		try {
			parser = XMLInputFactory.newInstance().createXMLStreamReader(bis);
		} 
		catch(XMLStreamException xmlStreamException ){
			throw new XMLStreamException(xmlStreamException);
		}
//		finally{			
//			System.out.println("shoutdown");
//			httpclient.getConnectionManager().shutdown();
//		}
		
		StAXOMBuilder builder = new StAXOMBuilder(parser);
		
		OMElement documentElement =  builder.getDocumentElement();

		return documentElement;
	}


	/**
	 * Exceptions wrapper for common OAI errors.
	 * @param element
	 * @throws Exception
	 */
	public static void sendException(OMElement element) throws Exception {
		String msg = null;
		if(!element.getText().isEmpty())
			msg = ": "+element.getText();
		OMAttribute attr = element.getAttribute(new QName("code"));

		if(attr.getAttributeValue().equals("badArgument")) {
			throw new Exception("badArgument "+msg);
		}
		else if(attr.getAttributeValue().equals("cannotDisseminateFormat")) {
			throw new Exception("cannotDisseminateFormat "+msg);
		}
		else if(attr.getAttributeValue().equals("idDoesNotExist")) {
			throw new Exception("idDoesNotExist "+msg);
		}
	}

	public static void extractHeader(OMElement headerElement,Header header) {
		if(headerElement.getLocalName().equals("identifier")) {
			header.setIdentifier(headerElement.getText().trim());
			//			System.out.println("identifier "+ headerElement.getText());
		}
		else if(headerElement.getLocalName().equals("datestamp")) {
			//			System.out.println("datestamp "+ headerElement.getText());
			header.setDatestamp(headerElement.getText().trim());

		}
		else if(headerElement.getLocalName().equals("setSpec")) {
			//			System.out.println("setSpec "+ headerElement.getText());
			header.addSpec(headerElement.getText().trim());
		}
	}

	public static void extractMetadata(OMElement metaElement,Metadata metadata) {

		if(metaElement.getLocalName().equals("title")) {
			try{
				metadata.addTitle(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting title");
			}
		}
		else if(metaElement.getLocalName().equals("creator")) {
			try{

				metadata.addCreator(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting creator");		
			}
		}
		else if(metaElement.getLocalName().equals("type")) {
			try{

				metadata.addType(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting type");
			}
		}
		else if(metaElement.getLocalName().equals("source")) {
			try{
				metadata.addSource(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting description");
			}
		}
		else if(metaElement.getLocalName().equals("language")) {
			try{
				metadata.addLanguage(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting language");
			}
		}
		else if(metaElement.getLocalName().equals("identifier")) {
			try{

				metadata.addIdentifier(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting identifier");
			}
		}
		else if(metaElement.getLocalName().equals("contributor")) {
			try{
				metadata.addContributor(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting contributor");
			}
		}
		else if(metaElement.getLocalName().equals("subject")) {
			try{
				metadata.addSubject(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting subject");
			}
		}
		else if(metaElement.getLocalName().equals("publisher")) {
			try{
				metadata.addPublisher(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting publisher");
			}
		}
		else if(metaElement.getLocalName().equals("date")) {
			try{
				metadata.addDate(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting date");
			}
		}
		else if(metaElement.getLocalName().equals("format")) {
			try{
				metadata.addFormat(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting format");
			}
		}
		else if(metaElement.getLocalName().equals("description")) {
			try{
				metadata.addDescription(metaElement.getText().trim());
			}catch (Exception e) {
				log.error("Error getting description");
			}
		}
	}


	public static Record getRecord(OMElement elementType) {
		Record record = null;

		try{
			record = new Record();
			@SuppressWarnings("unchecked")
			Iterator<OMElement> getRecordContent = elementType.getChildElements();

			Header header = new Header();
			Metadata metadata = new Metadata();

			while(getRecordContent.hasNext()) {
				OMElement recordElement = getRecordContent.next();
				if(recordElement.getLocalName().equals("header")) {

					//check if the record is deleted
					@SuppressWarnings("unchecked")
					Iterator<OMAttribute> attribute = recordElement.getAllAttributes();
					while(attribute.hasNext()) {
						OMAttribute prefixElement = attribute.next();
						if(prefixElement.getLocalName().equals("status")) {
							if (prefixElement.getAttributeValue().equals("deleted")){		
								record.setDeleted(true);
							}
						}
					}

					@SuppressWarnings("unchecked")
					Iterator<OMElement> headerContents = recordElement.getChildElements();
					while(headerContents.hasNext()) {
						OMElement headerContent = headerContents.next();
						Utils.extractHeader(headerContent,header);
					}

				}
				else if(recordElement.getLocalName().equals("metadata")) {

					try{
						record.setMetadataElement(recordElement);
					}catch (Exception e) {
						log.error("error setting metadata element");
					}
					@SuppressWarnings("unchecked")
					Iterator<OMElement> metadataContents = recordElement.getChildElements();

					if (metadataContents.hasNext()){

						OMElement metadataElement = metadataContents.next(); 					
						//						System.out.println(metadataElement.toString());

						//get xsi:schemaLocation
						@SuppressWarnings("unchecked")
						Iterator<OMAttribute> attributes = metadataElement.getAllAttributes();
						while(attributes.hasNext()) {
							OMAttribute attribute = attributes.next();
							if(attribute.getLocalName().equals("schemaLocation")) 
								metadata.setSchemaLocation(attribute.getAttributeValue());		
							//							System.out.println(metadata.getSchemaLocation());
						}

						@SuppressWarnings("unchecked")
						Iterator<OMElement> dcContents = metadataElement.getChildElements();

						while(dcContents.hasNext()) {						
							OMElement dcElement = dcContents.next();								
							Utils.extractMetadata(dcElement,metadata);															
						}
						//					record.setMetadata(metadata);
					}				

				}

				record.setMetadata(metadata);
				record.setHeader(header);
			}

		}catch (Exception e) {
			log.error("Error getting record");
		}
		return record;
	}
	
	
}
