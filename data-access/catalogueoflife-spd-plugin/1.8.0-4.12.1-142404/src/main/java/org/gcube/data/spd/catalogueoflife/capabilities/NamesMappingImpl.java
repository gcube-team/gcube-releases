package org.gcube.data.spd.catalogueoflife.capabilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.gcube.data.spd.catalogueoflife.CatalogueOfLifePlugin;
import org.gcube.data.spd.catalogueoflife.Utils;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NamesMappingImpl implements MappingCapability{

	Logger logger = LoggerFactory.getLogger(NamesMappingImpl.class);

	public Set<Conditions> getSupportedProperties() {
		return Collections.emptySet();
	}


	public Set<String> commonNameToScientificNamesMapping(String name) {

		Set<String> set = null;
		try {
			set = mapping(name, "common name", 0);
		} catch (Exception e) {
			logger.error("Exception",e);
		}
		return set;
	}

//	public Set<String> scientificNameToCommonNamesMapping(String name) {
//
//		Set<String> set = null;
//		try {
//			set = mapping(name, "accepted name", 0);
//		} catch (Exception e) {
//			logger.error("Exception",e);
//		}
//		return set;
//	}

	/**
	 * Mapping
	 */
	public Set<String> mapping(String name, String type, int start) throws Exception {
		String pathUrl = CatalogueOfLifePlugin.baseurl + "?name=" + name.replaceAll(" ", "+") + "&response=full&start=" + start;
//		logger.trace(pathUrl);
		Set<String> set = new HashSet<String>();

		int total_number_of_results = 0;
		int number_of_results_returned = 0;

		InputStream is = null;
		XMLInputFactory ifactory;
		XMLEventReader eventReader = null;

		try{
			is =URI.create(pathUrl).toURL().openStream();
			ifactory = XMLInputFactory.newInstance();
			eventReader = ifactory.createXMLEventReader(is);
			
			while (eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();

				if (Utils.checkStartElement(event, "results")){				

					StartElement element = (StartElement) event;

					@SuppressWarnings("unchecked")
					Iterator<Attribute> iterator = element.getAttributes();
					while (iterator.hasNext()) {

						Attribute attribute = (Attribute) iterator.next();
						QName att = attribute.getName();

						if (att.toString().equals("total_number_of_results")){
							total_number_of_results = (Integer.parseInt(attribute.getValue()));
						}
						else if (att.toString().equals("number_of_results_returned")){
							number_of_results_returned = (Integer.parseInt(attribute.getValue()));
						}				
					}				
					continue;
				}

				else if (Utils.checkStartElement(event, "result")){		
					//					logger.trace("result");
					if (type.equals("common name"))
						checkCommonName(eventReader, set);
					else 
						checkScientificName(eventReader, set);
					continue;
				}

				else if (Utils.checkEndElement(event, "results")){
					if (total_number_of_results > number_of_results_returned + start)	
						mapping(name, type, start+50);
					else
						break;
				}
			}
		} catch (Exception e) {
			logger.error("ExceptionPrintStackTrace",e);
		}finally{
			try {
				if (eventReader != null)
					eventReader.close();
				if (is != null)
					is.close();
			} catch (XMLStreamException e) {
				logger.error("XMLStreamException",e);
			} catch (IOException e) {
				logger.error("IOException",e);
			}				
		}

		return set;
	}


	private void checkScientificName(XMLEventReader eventReader,Set<String> set) {
		boolean flag = false;
		while (eventReader.hasNext()){
			XMLEvent event;
			try {
				event = eventReader.nextEvent();
				if (flag){
					if (Utils.checkStartElement(event, "name_status")){	

						event = eventReader.nextEvent();
//						logger.trace(event.asCharacters().getData());
						flag = false;
						if (!event.asCharacters().getData().equals("accepted name"))
							break;
						else 
							continue;
					}
				}

				if (Utils.checkStartElement(event, "common_name")){		
					event = eventReader.nextEvent();

					set.add(findScientificName(eventReader));

					continue;
				}
				if (Utils.checkStartElement(event, "result")){		
//					logger.trace(set.size());
					break;
				}
			} catch (XMLStreamException e) {
				logger.error("XMLStreamException",e);
			}
		}

//		return eventReader;
	}


	private String findScientificName(XMLEventReader eventReader) {
		while (eventReader.hasNext()){
			XMLEvent event;
			try {
				event = eventReader.nextEvent();
				if (Utils.checkStartElement(event, "name")){		
					event = eventReader.nextEvent();
//					logger.trace(event.asCharacters().getData());
					return event.asCharacters().getData();
				}

				if (Utils.checkEndElement(event, "common_name")){		
					break;
				}
			} catch (XMLStreamException e) {
				logger.error("XMLStreamException",e);
			}
		}
		return null;
	}


	private void checkCommonName(XMLEventReader eventReader, Set<String> set) {

		while (eventReader.hasNext()){
			XMLEvent event;
			try {
				event = eventReader.nextEvent();
				if (Utils.checkStartElement(event, "name_status")){		
					event = eventReader.nextEvent();
//					logger.trace(event.asCharacters().getData());
					if (!event.asCharacters().getData().equals("common name"))
						break;


					continue;
				}

				if (Utils.checkStartElement(event, "accepted_name")){		
					event = eventReader.nextEvent();
					set.add(findCommonName(eventReader));

					break;
				}
			} catch (XMLStreamException e) {
				logger.error("XMLStreamException",e);
			}
		}

//		return eventReader;
	}


	private String findCommonName(XMLEventReader eventReader) {

		while (eventReader.hasNext()){
			XMLEvent event;
			try {
				event = eventReader.nextEvent();
				if (Utils.checkStartElement(event, "name")){		
					event = eventReader.nextEvent();
					return event.asCharacters().getData();
				}

			} catch (XMLStreamException e) {
				logger.error("XMLStreamException",e);
			}
		}
		return null;
	}


	@Override
	public void getRelatedScientificNames(ObjectWriter<String> writer,
			String commonName) {
		Set<String> set = commonNameToScientificNamesMapping(commonName);
		for (String ScientificName : set) {
			if (!writer.isAlive()) return;
			writer.write(ScientificName);	 
		   }
		
	}

}
