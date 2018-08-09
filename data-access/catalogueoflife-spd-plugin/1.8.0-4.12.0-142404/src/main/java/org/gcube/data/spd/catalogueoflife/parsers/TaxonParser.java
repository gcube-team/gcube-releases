package org.gcube.data.spd.catalogueoflife.parsers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.gcube.data.spd.catalogueoflife.Utils;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.products.TaxonomyStatus;
import org.gcube.data.spd.model.products.TaxonomyStatus.Status;
import org.gcube.data.spd.model.util.ElementProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaxonParser implements ResultParser<TaxonomyItem> {

	private Logger logger = LoggerFactory.getLogger(TaxonParser.class);

	private boolean addParent = true;

	public TaxonParser(boolean addParent) {
		super();
		this.addParent = addParent;
	}


	@Override
	public TaxonomyItem parse(XMLEventReader eventReader) throws XMLStreamException{

		TaxonomyItem item = null;
		TaxonomyItem parent = null;
		Boolean flag = true;
		List<String> parents = null;

		while (eventReader.hasNext()){
			//			if (item!=null)
			//			System.out.println("item.getStatus() " + item.getStatus());
			XMLEvent event = eventReader.nextEvent();

			if (Utils.checkStartElement(event, "id")){
				event = eventReader.nextEvent();	
				if (item == null){
					item = new TaxonomyItem(event.asCharacters().getData());
					item.setCredits(Utils.createCredits());
					item.setCitation(Utils.createCitation());
				}
				continue;		
			} 
			else if (Utils.checkStartElement(event, "name")){
				if (item == null)
					break;
				if ((item.getScientificName() == null))
					try {
						item.setScientificName(Utils.readCharacters(eventReader));
					} catch (Exception e) {
						logger.error("error reading character");
						throw new XMLStreamException();
					}
				continue;
			}
			else if (Utils.checkStartElement(event, "rank")){
				event = eventReader.nextEvent();
				if ((item.getRank() == null) && (event.isCharacters()))
					item.setRank(event.asCharacters().getData());
				continue;
			} 
			else if (Utils.checkStartElement(event, "author")){
				event = eventReader.nextEvent();
				if ((item.getScientificNameAuthorship() == null) && (event.isCharacters()))
					item.setScientificNameAuthorship(event.asCharacters().getData());
				continue;			
			}
			else if (Utils.checkStartElement(event, "record_scrutiny_date")){
				event = eventReader.nextEvent();
				Calendar modified = null;
				if (event.isCharacters())
					modified = Utils.getCalendar(event.asCharacters().getData());
				item.setModified(modified);
				continue;			
			}	
			else if (Utils.checkStartElement(event, "source_database")){
				event = eventReader.nextEvent();
				if (event.isCharacters()){
					ElementProperty property = new ElementProperty("Source Database", event.asCharacters().getData());
					item.addProperty(property);
					//					item.setCitation(event.asCharacters().getData());					
				}
				continue;			
			}	
			else if (Utils.checkStartElement(event, "references") && (flag)){
				flag = false;
				//				event = eventReader.nextEvent();
				getReferences(eventReader, item);
				continue;				
			}		
			else if (Utils.checkStartElement(event, "name_status") && (item.getStatus() == null)){
				//				logger.trace("name_status");
				event = eventReader.nextEvent();
//				logger.trace("name status " + event.asCharacters().getData());

				if ((event.asCharacters().getData()).equals("accepted name")){		

					item.setStatus(new TaxonomyStatus("accepted name", Status.ACCEPTED));
				}else if ((event.asCharacters().getData()).equals("synonym")){
					item.setStatus(new TaxonomyStatus(Status.SYNONYM, null, "synonym"));
				}else if ((event.asCharacters().getData()).equals("ambiguous synonym")){
					item.setStatus(new TaxonomyStatus(Status.SYNONYM, null, "synonym"));		
				}else if ((event.asCharacters().getData()).equals("provisionally accepted name")){		
					item.setStatus(new TaxonomyStatus("provisionally accepted name", Status.PROVISIONAL));
				}else if ((event.asCharacters().getData()).equals("misapplied name")){		
					item.setStatus(new TaxonomyStatus("misapplied name", Status.MISAPPLIED));	
				}else {
					item.setStatus(new TaxonomyStatus((event.asCharacters().getData()).toString(), Status.UNKNOWN));
				}

				continue;
			} 
			//			else if (Utils.checkStartElement(event, "sn_id") && (item.getStatus().equals(Status.SYNONYM))){
			//				event = eventReader.nextEvent();
			//
			//				if (!(event.asCharacters().getData()).equals(0)){	
			//					TaxonomyStatus stat = item.getStatus();
			//					stat.setRefId(event.asCharacters().getData());
			////					logger.trace("SYNONYM");
			////					item.setStatus(new TaxonomyStatus(Status.SYNONYM, event.asCharacters().getData(), "synonym"));		
			//				}
			//
			//				continue;
			//			} 
			else if (Utils.checkStartElement(event, "common_names")){	
				event = eventReader.nextTag();
				if (Utils.checkStartElement(event, "common_name")){
					//					System.out.println("common_names *****");
					event = eventReader.nextEvent();
					//					System.out.println(event.asCharacters().getData());
					item.setCommonNames(Utils.getCommonNames(eventReader));					
				}
				continue;
			} 
			else if (Utils.checkStartElement(event, "classification") && addParent){	
				event = eventReader.nextTag();
				if (Utils.checkStartElement(event, "taxon")){
//					System.out.println("Classification *****");
					event = eventReader.nextEvent();

					parents = getParentList(eventReader);
					//									
				}
				continue;
			} 
			else if (Utils.checkStartElement(event, "accepted_name")){	
				event = eventReader.nextTag();
				if (Utils.checkStartElement(event, "id")){
					event = eventReader.nextEvent();
					TaxonomyStatus stat = item.getStatus();
					stat.setRefId(event.asCharacters().getData());	
				}
			}
			else if (Utils.checkEndElement(event, "accepted_name")){	

				if (item.getStatus() == null)
					item.setStatus(new TaxonomyStatus("unknown", Status.UNKNOWN));

				//				item.setCitation("Accessed through: World Register of Marine Species at http://www.marinespecies.org/aphia.php?p=taxdetails&id=10194 on 2012-10-26");
				return item;
			}
			else if (Utils.checkEndElement(event, "result")){	

//							System.out.println("****** close result *****");
				if (item.getStatus() == null)
					item.setStatus(new TaxonomyStatus("unknown", Status.UNKNOWN));			


				//list of parents
//								for (String p: parents)				
//									System.out.println(p);
					
				if (parents!=null){
					if (parents.size()>0){
						item.setParent(Utils.searchTaxonomy(parent, parents));	
					}
				}

				//				logger.trace("put writer");
				return item;
			}
		}
		throw new XMLStreamException("error parsing the result");
	}




	//get a list of parents
	private List<String> getParentList(XMLEventReader eventReader) {

		List<String> parents = new ArrayList<String>();
		//				TaxonomyItem t = null;
		while (eventReader.hasNext()){
			//			System.out.println("WHILE");
			try {
				XMLEvent event = eventReader.nextEvent();
				//System.out.println(event);
				if (Utils.checkStartElement(event, "id")){
					event = eventReader.nextEvent();	
					parents.add(event.asCharacters().getData());
					//					logger.trace("ID " + event.asCharacters().getData());											
					continue;						
				}else if (Utils.checkEndElement(event, "classification")){
					//					logger.trace("EXIT searchTaxonomy *****");
					break;
				}	

			} catch (XMLStreamException e) {
				logger.error("XMLStreamException", e);
			} catch (NoSuchElementException e) {
				logger.error("NoSuchElementException", e);
			}
		}
		return parents;
	}


	//get references
	private void getReferences(XMLEventReader eventReader, TaxonomyItem item) throws XMLStreamException {

		StringBuilder reference = new StringBuilder();

		while (eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();
			//			if (Utils.checkStartElement(event, "reference")){
			//				System.out.println("****** start reference *****");
			//				continue;	
			//			}
			//			else 
			if (Utils.checkStartElement(event, "author")){	
				try {
					reference.append(Utils.readCharacters(eventReader));
				} catch (Exception e) {
					logger.error("Exception",e);
				}
				continue;						
			} 
			else if (Utils.checkStartElement(event, "year")){		
				try {
					reference.append(Utils.readCharacters(eventReader));
				} catch (Exception e) {
					logger.error("Exception",e);
				}
				continue;
			}
			else if (Utils.checkStartElement(event, "title")){		
				try {
					reference.append(Utils.readCharacters(eventReader));
				} catch (Exception e) {
					logger.error("Exception",e);
				}
				continue;			
			}
			else if (Utils.checkStartElement(event, "source")){		
				try {
					reference.append(Utils.readCharacters(eventReader));
				} catch (Exception e) {
					logger.error("Exception",e);
				}
				continue;
			}
			else if (Utils.checkEndElement(event, "reference")){	
				ElementProperty property = new ElementProperty("Reference", reference.toString());
				item.addProperty(property);
				//logger.trace(reference.toString());
				reference.delete(0, reference.length());
				continue;
			}
			else if (Utils.checkEndElement(event, "references")){	
				break;
			}
		}
	}


}
