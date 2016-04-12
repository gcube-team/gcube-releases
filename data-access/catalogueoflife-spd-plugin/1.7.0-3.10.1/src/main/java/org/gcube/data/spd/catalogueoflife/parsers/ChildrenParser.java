package org.gcube.data.spd.catalogueoflife.parsers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.gcube.data.spd.catalogueoflife.Utils;
import org.gcube.data.spd.catalogueoflife.capabilities.SpeciesIdentifier;

public class ChildrenParser implements ResultParser<SpeciesIdentifier> {

	@Override
	public SpeciesIdentifier parse(XMLEventReader eventReader) throws XMLStreamException {
		SpeciesIdentifier speciesIdentifier = new SpeciesIdentifier();
		while (eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();

			if (Utils.checkStartElement(event, "name")){
				if (speciesIdentifier.getName()==null){
					String name = "";
					try {
						name =Utils.readCharacters(eventReader);
						speciesIdentifier.setName(name);
					} catch (Exception e) {
						throw new XMLStreamException("error reading string");
					}
				}
			}else if (Utils.checkEndElement(event, "result")){	
				break;
			}else if (Utils.checkStartElement(event, "child_taxa")){	
				speciesIdentifier.setChildren(retrieveChildTaxa(eventReader));
				break;
			}
		}
		if (speciesIdentifier.getName()!=null) return speciesIdentifier;
		else throw new XMLStreamException("error retrieving scientificName");
	}

	private List<String> retrieveChildTaxa(XMLEventReader eventReader) throws XMLStreamException{
		List<String> children = new ArrayList<String>();
		while (eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();
			if(Utils.checkStartElement(event, "id")){
				event = eventReader.nextEvent();
				if (event.isCharacters())
					children.add(event.asCharacters().getData());
			}else if (Utils.checkEndElement(event, "child_taxa")){	
				break;
			}
		}
		return children;
	}
}
