package org.gcube.data.spd.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.w3c.dom.NodeList;

public class RecordsIterator implements Iterator<DarwinSimpleRecord> {

	GCUBELog logger = new GCUBELog(RecordsIterator.class);

	public NodeList sum = null;
	public int start = 0;
	public int count = 0;

	public String pathUrl;
	public String filter;
	public String baseurl;
	public String model;
	public int limit;

	public XMLEventReader eventReader = null;


	public XMLInputFactory ifactory = null;
	public InputStream inputStream = null;

	private boolean complete;

	public RecordsIterator(String baseurl, String filter, String model, int limit, boolean complete) {

		this.baseurl = baseurl;
		this.filter = filter;
		this.model = model;
		this.limit = limit;
		this.complete = complete;
		this.pathUrl = baseurl + "?op=search&start=" + start + "&limit=" + limit + "&filter=" + filter + "&model=" + model;

//		System.out.println(this.pathUrl);
	}


	public DarwinSimpleRecord next() {
		//				System.out.println("next()");
		DarwinSimpleRecord record = null;
		try{
			record = new DarwinSimpleRecord(eventReader, complete);
		}catch (Exception e) {
			logger.error("Error creating DarwinCore Record from: " + this.pathUrl , e);
		}
		return record;
	}


	//Returns true if the iteration has more elements
	public boolean hasNext() 
	{		
		try {
			if (getRecord())
				return true;
			else{

				if (checkSummary()){
					//								System.out.println("check next page");
					count++;
					this.pathUrl = baseurl + "?op=search&start=" + ( limit * count) + "&limit=" +  limit + "&filter=" + filter + "&model=" + model;
					//								System.out.println(pathUrl);
					iterator();
					return hasNext();
				}
			}
		} catch (XMLStreamException e) {
			logger.error("Error getting more records from: " + this.pathUrl , e);

		}
		return false;


	}	


	//check attribute next in tag symmary - if it exists, we'll read the next page of results
	private boolean checkSummary() throws XMLStreamException {
		//		System.out.println("checkSummary()");
		//		element = null;
		//		event = null;
		while (eventReader.hasNext()){
			//			System.out.println("while");	
			//			event = null;

			XMLEvent event = eventReader.nextEvent();

			if (Utils.checkStartElement(event, "summary")){		

				//				System.out.println("summary tag ");
				StartElement element = (StartElement) event;



				Iterator<?> iterator = (element.getAttributes());

				while (iterator.hasNext()) {

					Attribute attribute = (Attribute) iterator.next();
					QName att = attribute.getName();

					if (att.toString().equals("next")){
						int next = Integer.parseInt(attribute.getValue());
						if (next>0){
							//							System.out.println("tag next " + next + " results");
							return true;
						}
					}				
				}	
				break;
			}
		}
		//		System.out.println("no more occurrences -- STOP");	
		return false;
	}



	//The remove operation is not supported by this Iterator
	public void remove() {
		throw new UnsupportedOperationException();
	}


	//Get XML document
	public RecordsIterator iterator(){
		final int numberOfRetries = 5 ;
		final long timeToWait = 1000 ;

		for (int i=0; i<numberOfRetries; i++) {
			try{		
				inputStream = URI.create(pathUrl).toURL().openStream();
				ifactory = XMLInputFactory.newInstance();
				eventReader = ifactory.createXMLEventReader(inputStream);
				break;
			} catch (XMLStreamException e) {
				logger.error("XMLStreamException in " + pathUrl, e);
			} catch (FileNotFoundException e) {
				logger.error("FileNotFoundException: " + pathUrl, e);
			} catch (MalformedURLException e) {
				logger.error("MalformedURLException: " + pathUrl, e);
			} catch (IOException e) {
				logger.error("IOException, url: " + pathUrl, e);
				try {
					Thread.sleep(timeToWait);
//					System.out.println("************* RETRY ***************");
				}
				catch (InterruptedException e1) {
				}
			}
		}
		return this;
	}


	public boolean getRecord () throws XMLStreamException{
		//System.out.println("getRecord()");
		while (eventReader.hasNext()){			
			XMLEvent event = eventReader.nextEvent();
			//											System.out.println(event.toString());
			if (Utils.checkStartElement(event, "DarwinRecord")){			
				//							System.out.println("DarwinRecord found");	
				break;
			}else if (Utils.checkEndElement(event, "DarwinRecordSet")){	
				return false;
			}else if (Utils.checkEndElement(event, "response")){	
				logger.info("No results at url: " + pathUrl);	
				return false;
			}
		}
		return true;
	}


}
