package org.gcube.data.spd.catalogueoflife.parsers;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public interface ResultParser<T> {

	T parse(XMLEventReader eventReader) throws XMLStreamException;	
}
