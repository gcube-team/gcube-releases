package org.gcube.data.spd.catalogueoflife;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.products.TaxonomyStatus;
import org.gcube.data.spd.model.products.TaxonomyStatus.Status;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Utils {


	static Logger logger = LoggerFactory.getLogger(Utils.class);


	public static boolean checkStartElement(XMLEvent event, String value){
		return event.getEventType() == XMLStreamConstants.START_ELEMENT && event.asStartElement().getName().getLocalPart().equals(value);
	}

	public static boolean checkEndElement(XMLEvent event, String value){
		return event.getEventType() == XMLStreamConstants.END_ELEMENT && event.asEndElement().getName().getLocalPart().equals(value);
	}


	public static String readCharacters(XMLEventReader eventReader) throws XMLStreamException{
		String characters="";
		XMLEvent event = eventReader.nextEvent();
		while (eventReader.hasNext() && event.isCharacters() ){
			characters+= event.asCharacters().getData();
			event = eventReader.nextEvent();
		}			
		return characters.trim();
	}

	/**
	 * Search an element using name or an id
	 */
	public static void searchRI(String name, String name_status, ObjectWriter<ResultItem> writer, int start, Set<String> hash) {

		String pathUrl = CatalogueOfLifePlugin.baseurl + "?name=*" + name.replaceAll(" ", "+") + "*&response=full&start=" + start;
		//			logger.trace("PATH " + pathUrl);
		int total_number_of_results = 0;
		int number_of_results_returned = 0;

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
							//							logger.trace(total_number_of_results);
						}
						else if (att.toString().equals("number_of_results_returned")){
							number_of_results_returned = (Integer.parseInt(attribute.getValue()));
							//							logger.trace(number_of_results_returned);
						}				
					}				
					continue;
				}
				else if (Utils.checkStartElement(event, "result")){			
					eventReader = getResultItem(eventReader, writer, name_status);
					continue;
				}
				else if (Utils.checkEndElement(event, "results")){
					if (total_number_of_results > number_of_results_returned + start)		
						searchRI( name, name_status, writer, start+50, hash);
					else
						break;
				}
			}
		} catch (Exception e) {
			writer.write(new StreamBlockingException("CatalogueOfLife", ""));
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
	}


	public static TaxonomyItem retrieveTIById(String id, Boolean skip) throws XMLStreamException{

		TaxonomyItem item = null;
		InputStream is = null;
		XMLInputFactory ifactory;
		XMLEventReader eventReader = null;
		try{
			String pathUrl = CatalogueOfLifePlugin.baseurl + "?id=" + id + "&response=full";
//			logger.trace(pathUrl);

			is = URI.create(pathUrl).toURL().openStream();
			ifactory = XMLInputFactory.newInstance();
			eventReader = ifactory.createXMLEventReader(is);

			while (eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();

				if (Utils.checkStartElement(event, "id")){
					if (item == null){
						item = new TaxonomyItem(readCharacters(eventReader));
						item.setCredits(Utils.createCredits());
						item.setCitation(Utils.createCitation());
					}
					continue;		
				} else if (Utils.checkStartElement(event, "name")){
					if (item == null) break;
					if (item.getScientificName() == null)
						item.setScientificName(readCharacters(eventReader));
					continue;
				} else if (Utils.checkStartElement(event, "rank")){
					if (item.getRank() == null)
						item.setRank(readCharacters(eventReader));
					continue;
				} else if (Utils.checkStartElement(event, "author")){					
					if (item.getScientificNameAuthorship() == null) 
						item.setScientificNameAuthorship(readCharacters(eventReader));
					continue;			
				} else if (Utils.checkStartElement(event, "record_scrutiny_date")){					
					Calendar modified = null;
					modified = Utils.getCalendar(readCharacters(eventReader));
					item.setModified(modified);
					continue;			
				} else if (Utils.checkStartElement(event, "name_status")){

					event = eventReader.nextEvent();
					if (item.getStatus() == null){
//						System.out.println("name_status " + event.asCharacters().getData());
						if ((event.asCharacters().getData()).equals("accepted name")){		
							item.setStatus(new TaxonomyStatus(event.asCharacters().getData(), Status.ACCEPTED));
						} else if ((event.asCharacters().getData()).equals("synonym")){		
							//			item.setStatus( new TaxonomyStatus(Status.SYNONYM, sn_id));
						} else if ((event.asCharacters().getData()).equals("ambiguous synonym")){		
							//			item.setStatus( new TaxonomyStatus(Status.SYNONYM, sn_id));
						} else if ((event.asCharacters().getData()).equals("misapplied name")){		
							item.setStatus( new TaxonomyStatus("misapplied name", Status.MISAPPLIED));
						} else if ((event.asCharacters().getData()).equals("provisional accepted name")){		
							item.setStatus( new TaxonomyStatus("provisional accepted name", Status.PROVISIONAL));
						} else
							item.setStatus(new TaxonomyStatus(event.asCharacters().getData(), Status.UNKNOWN));
					}
					continue;

				}else if (Utils.checkStartElement(event, "sn_id") && (item.getStatus() == null)){
					event = eventReader.nextEvent();

					if (!(event.asCharacters().getData()).equals(0)){	
						//											logger.trace("SYNONYM");
						item.setStatus(new TaxonomyStatus(Status.SYNONYM, event.asCharacters().getData(), "synonym"));		
					}
					continue;
				} else if (Utils.checkStartElement(event, "common_names")){	
					event = eventReader.nextTag();
					if (Utils.checkStartElement(event, "common_name")){
						//						System.out.println("common_names *****");
						event = eventReader.nextEvent();
						//					System.out.println(event.asCharacters().getData());

						item.setCommonNames(Utils.getCommonNames(eventReader));					
					}
					continue;
				}

				else if (Utils.checkStartElement(event, "classification") & !skip){	
					event = eventReader.nextTag();
					if (Utils.checkStartElement(event, "taxon")){

						event = eventReader.nextTag();
						if (Utils.checkStartElement(event, "id")){
							event = eventReader.nextEvent();
//														logger.trace(event.asCharacters().getData());
//							System.out.println("item.setParent(retrieveTIById(event.asCharacters().getData(), true))");
							item.setParent(retrieveTIById(event.asCharacters().getData(), true));			
						}
					}
					continue;
				}
				else if (Utils.checkEndElement(event, "result")){	
					break;
				}
			}	
		}catch(Exception e){
			throw new XMLStreamException(e);
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
		return item; 
	}


	public static XMLEventReader getResultItem(XMLEventReader eventReader, ObjectWriter<ResultItem> writer, String name_status) throws XMLStreamException {

		Set<DataSet> setDataSet = null;
		ResultItem item = null;		
		ResultItem parent = null;
		Boolean flag = true;
		Boolean flag_source = true;
		Boolean flag_ref = true;
		DataProvider dp = null;
		DataSet dataSet = null;
		String source_database = null;


		while (eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();
			if (flag){	
				item = createItem(eventReader, writer, name_status);
				flag = false;	
			}

			if (item != null){

				if (Utils.checkStartElement(event, "source_database") && flag_source){
					//					System.out.println("source db");
					event = eventReader.nextEvent();
					if (event.isCharacters()){
						source_database = event.asCharacters().getData();							
						flag_source = false;
					}
					continue;			
				}

				if (Utils.checkStartElement(event, "references") && flag_ref){	
					event = eventReader.nextTag();
					if (Utils.checkStartElement(event, "reference")){
						setDataSet = getReferences(eventReader);
						flag_ref = false;
					}		
					continue;				
				}

				if (Utils.checkStartElement(event, "classification")){		
					//					System.out.println("classification");
					event = eventReader.nextTag();
					if (Utils.checkStartElement(event, "taxon")){
						item.setParent(searchTaxonomyRI(eventReader, parent));					
					}
					continue;
				}

				if (Utils.checkStartElement(event, "common_names")){					
					event = eventReader.nextTag();
					if (Utils.checkStartElement(event, "common_name")){
						//						System.out.println("common names");
						event = eventReader.nextEvent();
						item.setCommonNames(getCommonNames(eventReader));		
						//						System.out.println("common_names *****");
					}
					continue;
				}

				if (Utils.checkEndElement(event, "result")){	
					//										System.out.println("****** result*****");

					if (setDataSet != null){
						//						System.out.println("****** dataset size***** " + setDataSet.size());
						for(DataSet d: setDataSet){

							ResultItem item1 = new ResultItem(item.getId(), item.getScientificName());
							item1.setScientificNameAuthorship(item.getScientificNameAuthorship());
							item1.setCommonNames(item.getCommonNames());
							item1.setParent(item.getParent());
							item1.setRank(item.getRank());

							item.setCredits(Utils.createCredits());
							item1.setCitation(Utils.createCitation());


							dataSet = new DataSet(d.getId());
							dataSet.setName(d.getName());
							dataSet.setCitation(d.getCitation());

							if (dp == null){
								dp = new DataProvider("CoL");
								dp.setName("Catalogue of Life");
							} else{
								dp = new DataProvider(source_database);
								dp.setName(source_database);		
							}

							dataSet.setDataProvider(dp);							
							item1.setDataSet(dataSet);

							if (writer.isAlive())
								writer.write(item1);
							else
								break;	
						}
					}	else{

						item.setCitation(Utils.createCitation());

						//						logger.trace("no reference");
						dataSet = new DataSet("CoL");
						dataSet.setName("Catalogue of Life");
						dataSet.setCitation("");

						if (dp == null){
							dp = new DataProvider("CoL");
							dp.setName("Catalogue of Life");
						}

						dataSet.setDataProvider(dp);						
						item.setDataSet(dataSet);

//						Calendar now = Calendar.getInstance();
//						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//						String credits = "This information object has been generated via the Species Product Discovery service on " +format.format(now.getTime()) + " by interfacing with of the Catalogue of Life (http://www.catalogueoflife.org/)";
						item.setCredits(Utils.createCredits());

						if (writer.isAlive())
							writer.write(item);
						else
							break;	

					}

					break;
				}
			}
			else
				break;
		}
		return eventReader;
	}


	public static Set<DataSet> getReferences(XMLEventReader eventReader) throws XMLStreamException {

		String author = null;
		String source = null;

		Set<DataSet> setDataSet = new HashSet<DataSet>();

		while (eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();

			if (Utils.checkStartElement(event, "author")){
				try {
					author = readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception",e);
				}
				continue;						
			} 
			else if (Utils.checkStartElement(event, "source")){				
				try {
					source = readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception",e);
				}
				continue;
			}
			else if (Utils.checkEndElement(event, "reference")){	
				DataSet dataSet = new DataSet(source);
				dataSet.setName(source);
				dataSet.setCitation(author);
				setDataSet.add(dataSet);
				continue;
			}
			else if (Utils.checkEndElement(event, "references")){	
				return setDataSet;
			}
		}


		return setDataSet;

	}

	public static ResultItem createItem(XMLEventReader eventReader, ObjectWriter<ResultItem> writer, String name_status) throws XMLStreamException {

		ResultItem item = null;		
		String id = null;
		String name = null;
		String rank = null;
		String author = null;
		String status = null;

		while (eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();

			if (Utils.checkStartElement(event, "id")){
				event = eventReader.nextEvent();	
				if (event.isCharacters() && (id == null)){
					id = event.asCharacters().getData();
					//					logger.trace("id " + event.asCharacters().getData());
				}		
				continue;	
			}

			if (Utils.checkStartElement(event, "name") && (name == null)){
				try {
					name = readCharacters(eventReader);

				} catch (Exception e) {
					logger.error("Exception",e);
				}
				continue;
			}

			if (Utils.checkStartElement(event, "rank") && (rank == null)){
				try {
					rank = readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception",e);
				}

				continue;
			}

			if (Utils.checkStartElement(event, "name_status") && (status == null)){	
				event = eventReader.nextEvent();
				if (event.isCharacters()){
					status = event.asCharacters().getData();
					//					logger.trace(event.asCharacters().getData());
					//					logger.trace((name_status));

					if (!(event.asCharacters().getData()).equals(name_status)){ 
						//						logger.trace("no accepted name");
						break;
					}
				}
				continue;
			}

			if (Utils.checkStartElement(event, "author") && (author == null)){
				try {
					author  = readCharacters(eventReader);
				} catch (Exception e) {
					logger.error("Exception",e);
				}
				continue;			
			}

			if (Utils.checkStartElement(event, "author") || Utils.checkStartElement(event, "url")){	

				if ((id != null) && (name != null)){
					//					System.out.println("** return item first part ***** ");
					item = new ResultItem(id, name);
					item.setCitation(author);
					item.setRank(rank);

					return item;
				}
			}
		}
		return item;
	}


	public static ResultItem searchTaxonomyRI(XMLEventReader eventReader, ResultItem t){
		String id = null;
		ResultItem p = null;
		//		TaxonomyItem t = null;
		while (eventReader.hasNext()){
			try {
				XMLEvent event = eventReader.nextEvent();

				if (Utils.checkStartElement(event, "id")){
					event = eventReader.nextEvent();	
					id = event.asCharacters().getData(); 

					//					logger.trace(event.asCharacters().getData());
					continue;						
				}else if (Utils.checkStartElement(event, "name")){
					event = eventReader.nextEvent();
					t = new ResultItem(id, event.asCharacters().getData()); 
					//					logger.trace(event.asCharacters().getData());
					continue;
				}else if (Utils.checkStartElement(event, "rank")){
					event = eventReader.nextEvent();
					t.setRank(event.asCharacters().getData()); 
					//					logger.trace(event.asCharacters().getData());
					continue;
				}else if (Utils.checkEndElement(event, "taxon")){
					event = eventReader.nextEvent();				
					t.setParent(p);	
					p = t;
					continue;
				}else if (Utils.checkEndElement(event, "classification")){
					//					logger.trace("exit searchTaxonomy *****");
					break;
				}	

			} catch (XMLStreamException e) {
				logger.error("printStackTrace", e);
			}
		}

		return p;

	}

	
	//create chain of taxon
	public static TaxonomyItem searchTaxonomy(TaxonomyItem t, List<String> parents) throws XMLStreamException{
		TaxonomyItem p = null;
		for (String parent: parents){
//			logger.trace("ID " + parent);
//				System.out.println("retrieveTIById skip");
				//				t = new TaxonomyItem(null);
				t = retrieveTIById(parent, true);
//				t.setStatus(new TaxonomyStatus("accepted name",Status.ACCEPTED));
				//						logger.trace("accepted name");
				t.setParent(p);	
				p = t;
		}
		return p;
	}




	public static List<CommonName> getCommonNames(XMLEventReader eventReader) throws XMLStreamException {

		List<CommonName> list = new ArrayList<CommonName> ();
		CommonName comm = null;
		String language = null;
		String name = null;
		String country = null;

		while (eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();

			if (Utils.checkStartElement(event, "name")){
				event = eventReader.nextEvent();	
				if (event.isCharacters())
					name = event.asCharacters().getData();
				continue;						
			} 
			else if (Utils.checkStartElement(event, "language")){
				event = eventReader.nextEvent();
				if (event.isCharacters())
					language = event.asCharacters().getData();
				continue;
			}
			else if (Utils.checkStartElement(event, "country")){
				event = eventReader.nextEvent();
				if (event.isCharacters())
					country = event.asCharacters().getData();
				continue;
			} 
			else if (Utils.checkEndElement(event, "common_name")){	
				comm = new CommonName(language, name);
				comm.setLocality(country);
				list.add(comm);
				continue;
			}
			else if (Utils.checkEndElement(event, "common_names")){	
				//				System.out.println("****** close common_names *****");
				break;
			}
		}

		return list;
	}





	/**
	 * Get tag value
	 */

	public static String getTagValue(String sTag, Element eElement) {

		String a = "";			
		NodeList nlList = eElement.getElementsByTagName(sTag);
		if (nlList!= null){	
			Element xmlNode = (Element)nlList.item(0);
			if (xmlNode != null){
				NodeList textFNList = xmlNode.getChildNodes();
				if (textFNList != null){
					Node nValue = (Node) textFNList.item(0);  
					if (nValue != null)
					{
						a = nValue.getNodeValue(); 
					}
				}
			}
		}
		return a;
	}




	public static Calendar getDate(NodeList date2) {

		//		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

		Calendar cal = null;

		if (date2.getLength()!= 0) {
			Node nNode = date2.item(0);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) { 

				Element eElement = (Element) nNode;	      
				String scrutiny  = getTagValue("scrutiny", eElement);
				if (!(scrutiny.equals(""))){
					String modified = scrutiny;
					Date date = DateUtil.getInstance().parse(modified);

					//						d = DateUtil.getInstance().parse(modified);
					if (date != null){
						cal=Calendar.getInstance();
						cal.setTime(new Date(date.getTime()));
					}	
					//					else
					//						logger.warn("Unknown data format");
				}

			}
		}
		return cal;
	}


	//format date
	public static String createDate() {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(now.getTime());
		return date;
	}

	public static String createCitation() {
		StringBuilder cit = new StringBuilder();
		cit.append(CatalogueOfLifePlugin.citation);
		cit.append(createDate());
		return cit.toString();
	}

	public static String createCredits() {
		String cred = CatalogueOfLifePlugin.credits;
		cred.replace("XDATEX",createDate());	
		return cred;
	}

	public static Calendar getCalendar(String date1) {

		//		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		Calendar cal = null;
		Date date = DateUtil.getInstance().parse(date1);
		//						d = DateUtil.getInstance().parse(modified);
		if (date != null){
			cal=Calendar.getInstance();
			cal.setTime(new Date(date.getTime()));
		}	
		//		else
		//			logger.warn("Unknown data format");

		return cal;
	}
}
