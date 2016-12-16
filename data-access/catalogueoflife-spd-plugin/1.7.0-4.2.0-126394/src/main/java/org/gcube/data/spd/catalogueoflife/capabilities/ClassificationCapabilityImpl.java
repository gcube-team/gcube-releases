package org.gcube.data.spd.catalogueoflife.capabilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.gcube.data.spd.catalogueoflife.CatalogueOfLifePlugin;
import org.gcube.data.spd.catalogueoflife.Utils;
import org.gcube.data.spd.catalogueoflife.parsers.ChildrenParser;
import org.gcube.data.spd.catalogueoflife.parsers.ResultParser;
import org.gcube.data.spd.catalogueoflife.parsers.TaxonParser;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.MethodNotSupportedException;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.exceptions.StreamNonBlockingException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.UnfoldCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassificationCapabilityImpl extends ClassificationCapability implements UnfoldCapability  {


	Logger logger = LoggerFactory.getLogger(ClassificationCapabilityImpl.class);

	public Set<Conditions> getSupportedProperties() {
		return Collections.emptySet();
	}

	/* 
	 *
	 */
	@Override
	public void getSynonymnsById(ObjectWriter<TaxonomyItem> writer, String id)
			throws IdNotValidException, MethodNotSupportedException, ExternalRepositoryException {
		String pathUrl = CatalogueOfLifePlugin.baseurl + "?id=" + id + "&response=full";
		InputStream is = null;
		XMLInputFactory ifactory;
		XMLEventReader eventReader = null;
		try{
			is =URI.create(pathUrl).toURL().openStream();
			ifactory = XMLInputFactory.newInstance();
			eventReader = ifactory.createXMLEventReader(is);

			while (eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();

				if (Utils.checkStartElement(event, "synonym")){		
					event = eventReader.nextEvent();
					//					logger.trace("result");		
					if (event.isCharacters())
						getSynonym(eventReader, writer, new TaxonParser(true));
					continue;
				}

				else if (Utils.checkEndElement(event, "result")){

					break;
				}
			}
		}catch (XMLStreamException e) {
			logger.error("error retrieving synonyms");
			throw new ExternalRepositoryException();
		} catch (Exception e) {
			logger.error("error",e);
		}finally{
			try {
				if (eventReader != null)
					eventReader.close();
				if (is != null)
					is.close();
			} catch (Exception e) {
				logger.error("error closing streams",e);
			} 				
		}
	}



	@Override
	public TaxonomyItem retrieveTaxonById(String id) {
		try {
			return retrieveById(id, new TaxonParser(true));
		} catch (Exception e) {
			logger.error("error retreiving taxon item with id "+id);
			return null;
		}
	}


	private List<String> getChildren(XMLEventReader eventReader) {

		Set<String> setIds = new HashSet<String>();
		while (eventReader.hasNext()){
			XMLEvent event;
			try {
				event = eventReader.nextEvent();
				if (Utils.checkStartElement(event, "id")){		
					event = eventReader.nextEvent();
					String tempId = event.asCharacters().getData();
					logger.trace("found id "+ tempId);	
					setIds.add(tempId);
				}else if (Utils.checkEndElement(event, "child_taxa")){
					logger.trace("found child taxa closed");	
					break;
				}
			} catch (XMLStreamException e) {
				logger.error("XMLStreamException",e);
			}
		}
		return Collections.list(Collections.enumeration(setIds));
	}

	protected void checkCommonName(XMLEventReader eventReader,
			ObjectWriter<TaxonomyItem> writer, ResultParser<TaxonomyItem> parser) throws XMLStreamException {
		try{
			while (eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();

				if (Utils.checkStartElement(event, "name_status")){
					event = eventReader.nextEvent();

					if ((event.asCharacters().getData()).equals("common name")){	
						continue;
					}
					else
						break;				
				} 
				else if (Utils.checkStartElement(event, "accepted_name")){
					if (writer.isAlive())
						writer.write(parser.parse(eventReader));
					return;
				}
			}
		}catch (Exception e) {
			logger.error("Exception",e);
		}
	}


	public <T> void retrieveByScientificName(ObjectWriter<T> writer, ResultParser<T> parser, String name, int start, Set<String> hash) {

		String pathUrl = CatalogueOfLifePlugin.baseurl + "?name=" + name.replaceAll(" ", "+") + "&response=full&start=" + start;
		logger.trace("PATH " + pathUrl);
		int total_number_of_results = 0;
		int number_of_results_returned = 0;


		InputStream is = null;
		XMLInputFactory ifactory;
		XMLEventReader eventReader = null;

		try{
			is =URI.create(pathUrl).toURL().openStream();

			ifactory = XMLInputFactory.newInstance();
			eventReader = ifactory.createXMLEventReader(is);

			while(eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();
				if (Utils.checkStartElement(event, "results")){

					StartElement element = event.asStartElement();


					@SuppressWarnings("unchecked")
					Iterator<Attribute> attributes = element.getAttributes();
					while(attributes.hasNext()){

						Attribute attribute = attributes.next();
						if(attribute.getName().toString().equals("total_number_of_results")){
							//							logger.trace(attribute.getValue());
							total_number_of_results = (Integer.parseInt(attribute.getValue()));
						}
						else if (attribute.getName().toString().equals("number_of_results_returned")){
							//							logger.trace(attribute.getValue());
							number_of_results_returned = (Integer.parseInt(attribute.getValue()));
						}			
					}				
					continue;
				}else if (Utils.checkStartElement(event, "result")){								
					if (writer.isAlive()){
						writer.write(parser.parse(eventReader));
					}
					else return;
					continue;
				}
				else if (Utils.checkEndElement(event, "results")){
					if (total_number_of_results > number_of_results_returned + start)		
						retrieveByScientificName(writer, parser , name, start+50, hash);
					else
						break;
				}
			}
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} catch (XMLStreamException e) {
			logger.error("XMLStreamException", e);
		} catch (NoSuchElementException e) {
			logger.error("NoSuchElementException", e);
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




	@Override
	public void searchByScientificName(String word,
			ObjectWriter<TaxonomyItem> writer, Condition... properties) {
		try{
			logger.trace("Retrive taxa by scientific name " + word);
			retrieveByScientificName(writer, new TaxonParser(true), "*"+word+"*", 0, new HashSet<String>());
		} catch (Exception e) {
			logger.error("Error searching for scientificName",e);
		} 

	}


	@Override
	public void retrieveTaxonByIds(Iterator<String> ids,
			ClosableWriter<TaxonomyItem> writer) {

		InputStream is = null;
		XMLInputFactory ifactory;
		XMLEventReader eventReader = null;
		TaxonParser parser = new TaxonParser(true);
		while(ids.hasNext()) {
			String id = ids.next(); 
			try{
				logger.trace("Retrive taxa by id " + id);
				String pathUrl = CatalogueOfLifePlugin.baseurl + "?id=" + id + "&response=full";
				//System.out.println(pathUrl);
				is =URI.create(pathUrl).toURL().openStream();
				ifactory = XMLInputFactory.newInstance();
				eventReader = ifactory.createXMLEventReader(is);

				while (eventReader.hasNext()){
					XMLEvent event = eventReader.nextEvent();

					if (Utils.checkStartElement(event, "result")){								
						if (writer.isAlive())
							writer.write(parser.parse(eventReader));
						continue;
					}

					else if (Utils.checkEndElement(event, "results")){
						break;
					}
				}
			} catch (Exception e) {
				logger.error("error reading id "+id,e);
				writer.write(new StreamNonBlockingException("CatalogueOfLife",id));
			}
		}

		writer.close();
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


	private <T> T retrieveById(String id, ResultParser<T> parser) throws XMLStreamException, MalformedURLException, IOException{
		String pathUrl = CatalogueOfLifePlugin.baseurl + "?id=" + id + "&response=full";

		InputStream is = null;
		XMLInputFactory ifactory;
		XMLEventReader eventReader = null;

		try{
			is =URI.create(pathUrl).toURL().openStream();
			ifactory = XMLInputFactory.newInstance();
			eventReader = ifactory.createXMLEventReader(is);
			while (eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();

				if (Utils.checkStartElement(event, "result"))
					return parser.parse(eventReader);
			}
			throw new XMLStreamException("result tag not found");
		}finally{
			try{
				if(eventReader!=null)
					eventReader.close();
				if (is!=null)
					is.close();
			}catch (Exception e) {
				logger.error("error closing reader",e);
			}
		}
	}

	private void getSynonym(XMLEventReader eventReader, ObjectWriter<TaxonomyItem> writer, ResultParser<TaxonomyItem> parser) throws XMLStreamException {

		while (eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();
			if (Utils.checkStartElement(event, "id")){	
				event = eventReader.nextEvent();
				if (event.isCharacters()){
					//						logger.trace(event.asCharacters().getData());
					if (writer.isAlive()){
						String id = event.asCharacters().getData();
						try {
							writer.write(retrieveById(id, parser));
						} catch (Exception e) {
							writer.write(new StreamNonBlockingException("CatalogueOfLife",id));
						}
					}else
						break;
				}
			}
			if (Utils.checkEndElement(event, "synonyms")){
				break;
			}

		}
	}

	@Override
	public List<TaxonomyItem> retrieveTaxonChildrenByTaxonId(String id)
			throws IdNotValidException, ExternalRepositoryException {
		logger.trace("retrieveTaxonChildrenByTaxonId " + id);
		String pathUrl = CatalogueOfLifePlugin.baseurl + "?id=" + id + "&response=full";
		logger.trace(pathUrl);
		List<TaxonomyItem> list = new ArrayList<TaxonomyItem>(); 
		InputStream is = null;
		XMLInputFactory ifactory;
		XMLEventReader eventReader = null;

		TaxonParser parser = new TaxonParser(false);

		try{
			is =URI.create(pathUrl).toURL().openStream();
			ifactory = XMLInputFactory.newInstance();
			eventReader = ifactory.createXMLEventReader(is);
			while (eventReader.hasNext()){
				XMLEvent event = eventReader.nextEvent();

				if (Utils.checkStartElement(event, "child_taxa")){	
					List<String> listIds= getChildren(eventReader);
					//					logger.trace("ids found "+listIds);
					for (String childrenId: listIds )
						list.add(retrieveById(childrenId, parser));
					break;
				}
				else if (Utils.checkEndElement(event, "results")){
					break;
				}
			}

		} catch (NoSuchElementException e) {
			logger.error("element not found",e);
			throw new IdNotValidException();
		}catch (Exception e) {
			logger.error("repository exception",e);
			throw new ExternalRepositoryException(e);
		} finally{
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
		return list;
	}

	@Override
	public void unfold(final ObjectWriter<String> writer, String scientificName) {
		ObjectWriter<SpeciesIdentifier> internalWriter = new ObjectWriter<SpeciesIdentifier>() {

			@Override
			public boolean write(SpeciesIdentifier t) {
				ChildrenParser parser = new ChildrenParser();
				for (String childId: t.getChildren())
					writeAllChildrenName(writer, childId, parser);
				return true;
			}

			@Override
			public boolean write(StreamException error) {
				return writer.write(error);
			}

			@Override
			public boolean isAlive() {
				return writer.isAlive();
			}
		};

		retrieveByScientificName(internalWriter, new ChildrenParser(), scientificName, 0, new HashSet<String>());

	}

	private void writeAllChildrenName(ObjectWriter<String> writer, String id, ChildrenParser parser){
		final int MAX_RETRIES =10;
		final long WAIT_TIME = 10000;
		try{
			SpeciesIdentifier species;
			int retries = 0;
			boolean exit = false;
			do{
				try {
					species = retrieveById(id, parser);
					writer.write(species.getName());
					for (String childId: species.getChildren())
						writeAllChildrenName(writer, childId, parser);
					exit = true;
				} catch (IOException e) {
					retries++;
					try {
						Thread.sleep(WAIT_TIME);
					} catch (InterruptedException e1) {}
				}
			}while (retries<MAX_RETRIES && !exit);
			if (retries==MAX_RETRIES) throw new Exception("too many retries");
		}catch (Exception e) {
			writer.write(new StreamNonBlockingException("catalogueOfLife",id));
		}
	}


}

