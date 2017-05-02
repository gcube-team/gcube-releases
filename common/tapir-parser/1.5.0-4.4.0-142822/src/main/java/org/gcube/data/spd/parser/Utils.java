package org.gcube.data.spd.parser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utils {


	static Logger logger = LoggerFactory.getLogger(Utils.class);


	protected static boolean checkStartElement(XMLEvent event, String value){
//		System.out.println("* ");
		return event.getEventType() == XMLStreamConstants.START_ELEMENT && event.asStartElement().getName().getLocalPart().equals(value);
	}

	protected static boolean checkEndElement(XMLEvent event, String value){
		return event.getEventType() == XMLStreamConstants.END_ELEMENT && event.asEndElement().getName().getLocalPart().equals(value);
	}


	protected static String readCharacters(XMLEventReader eventReader) throws Exception{
		String characters="";
		XMLEvent event = eventReader.nextEvent();
		while (eventReader.hasNext() && event.isCharacters() ){
			characters+= event.asCharacters().getData();
			event = eventReader.nextEvent();
		}			
		return characters.trim();
	}
	

}
