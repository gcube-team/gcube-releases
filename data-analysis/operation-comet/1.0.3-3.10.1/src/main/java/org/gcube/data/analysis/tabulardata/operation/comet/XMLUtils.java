package org.gcube.data.analysis.tabulardata.operation.comet;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

public class XMLUtils {

	

	public static void write(XMLStreamReader xmlr, XMLStreamWriter writer) throws XMLStreamException {
		switch (xmlr.getEventType()) {
		case XMLEvent.START_ELEMENT:
			final String localName = xmlr.getLocalName();
			final String namespaceURI = xmlr.getNamespaceURI();
			if (namespaceURI != null && namespaceURI.length() > 0) {
				final String prefix = xmlr.getPrefix();
				if (prefix != null)
					writer.writeStartElement(prefix, localName, namespaceURI);
				else
					writer.writeStartElement(namespaceURI, localName);
			} else {
				writer.writeStartElement(localName);
			}

			for (int i = 0, len = xmlr.getNamespaceCount(); i < len; i++) {
				writer.writeNamespace(xmlr.getNamespacePrefix(i), xmlr.getNamespaceURI(i));
			}

			for (int i = 0, len = xmlr.getAttributeCount(); i < len; i++) {
				String attUri = xmlr.getAttributeNamespace(i);
				if (attUri != null)
					writer.writeAttribute(attUri, xmlr.getAttributeLocalName(i), xmlr.getAttributeValue(i));
				else
					writer.writeAttribute(xmlr.getAttributeLocalName(i), xmlr.getAttributeValue(i));
			}
			break;
		case XMLEvent.END_ELEMENT:
			writer.writeEndElement();
			break;
		case XMLEvent.SPACE:
		case XMLEvent.CHARACTERS:
			writer.writeCharacters(xmlr.getTextCharacters(), xmlr.getTextStart(), xmlr.getTextLength());
			break;
		case XMLEvent.PROCESSING_INSTRUCTION:
			writer.writeProcessingInstruction(xmlr.getPITarget(), xmlr.getPIData());
			break;
		case XMLEvent.CDATA:
			writer.writeCData(xmlr.getText());
			break;

		case XMLEvent.COMMENT:
			writer.writeComment(xmlr.getText());
			break;
		case XMLEvent.ENTITY_REFERENCE:
			writer.writeEntityRef(xmlr.getLocalName());
			break;
		case XMLEvent.START_DOCUMENT:
			String encoding = xmlr.getCharacterEncodingScheme();
			String version = xmlr.getVersion();

			if (encoding != null && version != null)
				writer.writeStartDocument(encoding, version);
			else if (version != null)
				writer.writeStartDocument(xmlr.getVersion());
			break;
		case XMLEvent.END_DOCUMENT:
			writer.writeEndDocument();
			break;
		case XMLEvent.DTD:
			writer.writeDTD(xmlr.getText());
			break;
		}
	}
}

