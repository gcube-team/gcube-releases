package org.gcube.data.spd.catalogueoflife;

import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.data.spd.catalogueoflife.capabilities.ClassificationCapabilityImpl;
import org.gcube.data.spd.catalogueoflife.capabilities.ExpansionCapabilityImpl;
import org.gcube.data.spd.catalogueoflife.capabilities.NamesMappingImpl;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.UnfoldCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CatalogueOfLifePlugin extends  AbstractPlugin {
	
	static Logger logger = LoggerFactory.getLogger(CatalogueOfLifePlugin.class);
	final static String citation = "Bisby F., Roskov Y., Culham A., Orrell T., Nicolson D., Paglinawan L., Bailly N., Appeltans W., Kirk P., Bourgoin T., Baillargeon G., Ouvrard D., eds (2012). Species 2000 & ITIS Catalogue of Life, 30th May 2012. Digital resource at www.catalogueoflife.org/col/. Species 2000: Reading, UK. Accessed through: Catalogue of Life at http://www.catalogueoflife.org/ on ";
	final static String credits = "This information object has been generated via the Species Product Discovery service on XDATEX by interfacing with the Catalogue of Life (http://www.catalogueoflife.org/)";
	
	private ClassificationCapabilityImpl classificationCapability = new ClassificationCapabilityImpl();
	
	public static String baseurl;
	//public static String baseurl = "http://www.catalogueoflife.org/col/webservice";
	
	@Override
	public void initialize(ServiceEndpoint res) throws Exception {
		setUseCache(true);
		for (AccessPoint ap:res.profile().accessPoints()) {
			if (ap.name().equals("catalogueoflife")) {
				baseurl = ap.address();		
//				logger.trace(baseurl);
			}
		}
		super.initialize(res);
	}

	@Override
	public void update(ServiceEndpoint res) throws Exception {
		for (org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint ap:res.profile().accessPoints()) {		
			if (ap.name().equals("catalogueoflife")) {
				baseurl = ap.address();		
//				logger.trace(baseurl);
			}
		}
		super.update(res);
	}

	@Override
	public String getDescription() {
		return ("Catalogue of Life Plugin");
	}

	@Override
	public String getRepositoryName() {
		return ("CatalogueOfLife");
	}

	@SuppressWarnings("serial")
	@Override
	public Set<Capabilities> getSupportedCapabilities() {
		return new HashSet<Capabilities>(){{add(Capabilities.NamesMapping); add(Capabilities.Classification);add(Capabilities.Expansion);add(Capabilities.Unfold);}};
	}


	@Override
	public MappingCapability getMappingInterface() {
		return new NamesMappingImpl();
	}


	@Override
	public ClassificationCapability getClassificationInterface() {
		return classificationCapability;
	}

	@Override
	public UnfoldCapability getUnfoldInterface() {
		return classificationCapability;
	}

	@Override
	public ExpansionCapability getExpansionInterface() {
		return new ExpansionCapabilityImpl();
	}
	
	@Override
	public void searchByScientificName(String word,
			ObjectWriter<ResultItem> writer, Condition... properties) {
		logger.trace("Search by Scientific Name "+ word + " in CoL");
		
			Utils.searchRI(word, "accepted name", writer, 0, new HashSet<String>());
		
	}

	protected XMLEventReader checkRICommonName(XMLEventReader eventReader, ObjectWriter<ResultItem> writer) throws XMLStreamException {
		while (eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();

			if (Utils.checkStartElement(event, "name_status")){
				event = eventReader.nextEvent();

				if ((event.asCharacters().getData()).equals("common name")){	
					//						logger.trace("common name");
					continue;
				}
				else
					break;						
			} 
			else if (Utils.checkStartElement(event, "accepted_name")){
				//					logger.trace("accepted_name");
				Utils.getResultItem(eventReader, writer, "accepted name");
				return eventReader;
			}
		}
		return eventReader;
	}

	@Override
	public RepositoryInfo getRepositoryInfo() {
		RepositoryInfo info = new RepositoryInfo(
				"http://www.catalogueoflife.org/prototype/images/head/head_leaves.jpg", 
				"http://www.catalogueoflife.org/",
				"The Catalogue of Life is a quality-assured checklist of more than 1.3 million species of plants, animals, fungi and micro-organisms, about 70% of all those known to science. An uncomplicated structure, and both minimal and standardised dataset provide a sound baseline of species information for all biologists. The Catalogue of Life is unique in its breadth of coverage of organisms, the degree of validation in the knowledge set, and its wide global take-up. The Catalogue of Life is a remarkable global partnership. The content is contributed by an array of some 100 expert taxonomic databases world-wide, involving over 3,000 taxonomic specialists: the Global Species Databases. Expert teams peer review the databases and integrate them into a single coherent catalogue, and have established a single hierarchical classification. The Catalogue of Life is evolving to provide an effective partner to six global biodiversity programmes (through the i4Life European e-Infrastructure project, 2010-2013), creating an ecosystem of services. The Catalogue is able to support the needs of these partner programmes in establishing validated taxonomy, and moreover share a variety of related services amongst all: Global Biodiversity Information Facility (GBIF); 	Barcode of Life Data Systems (BOLD); IUCN Red List, Encyclopedia of Life, EMBL European Nucleotide Archive (ENA), LifeWatch.");
		return info;
	}



	

}
