package org.gcube.data.spd.catalogueoflife.capabilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.gcube.data.spd.catalogueoflife.CatalogueOfLifePlugin;
import org.gcube.data.spd.catalogueoflife.Utils;
import org.gcube.data.spd.model.exceptions.StreamNonBlockingException;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpansionCapabilityImpl implements ExpansionCapability {


	static Logger logger = LoggerFactory.getLogger(ExpansionCapabilityImpl.class);

	@Override
	public void getSynonyms(ObjectWriter<String> writer, String scientifcName) {
		getIds(scientifcName, 0, writer);
	}


	//recorvise method to get a list of ids by scientific name
	private List<String> getIds(String scientifcName, int start, ObjectWriter<String> writer) {
		String pathUrl = CatalogueOfLifePlugin.baseurl + "?name=" + scientifcName.replaceAll(" ", "+") + "*&response=full&start=" + start;
//		logger.trace("PATH " + pathUrl);
		int total_number_of_results = 0;
		int number_of_results_returned = 0;
		List<String> ids = new ArrayList<String>();
		InputStream is = null;
		XMLInputFactory ifactory;
		XMLEventReader eventReader = null;
		try{
			is = URI.create(pathUrl).toURL().openStream();
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
				else if (Utils.checkStartElement(event, "synonyms")){	
					readSynonyms(eventReader, writer);					
					continue;
				}
				else if (Utils.checkEndElement(event, "results")){
					if (total_number_of_results > number_of_results_returned + start)		
						getIds(scientifcName, start+50, writer);
					else
						break;
				}
			}
		} catch (Exception e) {
			writer.write(new StreamNonBlockingException("CatalogueOfLife",""));
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
		return ids;
	}


	
	//read from <synonyms> to </synonyms>
	private void readSynonyms(XMLEventReader eventReader, ObjectWriter<String> writer) throws XMLStreamException {

		while (eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();

			if (Utils.checkStartElement(event, "synonym")){	
				while (eventReader.hasNext()){
					event = eventReader.nextEvent();

					if (Utils.checkStartElement(event, "name")){	
						
						event = eventReader.nextEvent();
						if (event.isCharacters()){
							String id = event.asCharacters().getData();
//							System.out.println(id);
							if (!writer.isAlive()) return;
							writer.write(id);
						}									
					}
					else if (Utils.checkEndElement(event, "synonym"))
						break;
				}
			}
			else if (Utils.checkEndElement(event, "synonyms"))
				break;
			
		}
	}
}
