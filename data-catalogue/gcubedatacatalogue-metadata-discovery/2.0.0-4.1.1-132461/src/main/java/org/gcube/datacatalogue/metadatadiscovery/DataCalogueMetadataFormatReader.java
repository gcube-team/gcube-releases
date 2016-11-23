package org.gcube.datacatalogue.metadatadiscovery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataType;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.reader.MedataFormatDiscovery;
import org.gcube.datacatalogue.metadatadiscovery.reader.MedataFormatReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class DataCalogueMetadataReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
public class DataCalogueMetadataFormatReader implements DataCatalogueMetadataDiscovery{

	private MedataFormatDiscovery medataFormatDiscovery;
	private final ScopeBean scope;
	private Map<String, MetadataFormat> hash = new HashMap<String, MetadataFormat>();

	private static Logger logger = LoggerFactory.getLogger(DataCalogueMetadataFormatReader.class);

	/**
	 * Instantiates a new data calogue metadata format reader.
	 *
	 * @throws Exception the exception
	 */
	public DataCalogueMetadataFormatReader() throws Exception {

		String scopeString = ScopeProvider.instance.get();
		logger.debug("DataCalogueMetadataFormatReader read scope "+scopeString +" from ScopeProvider");

		if(scopeString==null || scopeString.isEmpty())
			throw new Exception("Please set a valid scope into ScopeProvider");

		scope  = new ScopeBean(scopeString);
		medataFormatDiscovery = new MedataFormatDiscovery(scope);
		logger.info("MedataFormatDiscovery has retrieved: "+medataFormatDiscovery.getMetadataTypes().size() +" metadata type/s");
		logger.debug("filling cache for MedataFormat");
		for (MetadataType mT : medataFormatDiscovery.getMetadataTypes()) {
			MedataFormatReader reader = new MedataFormatReader(scope, mT.getId());
			hash.put(mT.getId(), reader.getMetadataFormat());
			logger.debug("MetadataType id: "+mT.getId() +" cached as: "+reader.getMetadataFormat());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.datacatalogue.metadatadiscovery.DataCatalogueMetadataDiscovery#getListOfMetadataTypes()
	 */
	@Override
	public List<MetadataType> getListOfMetadataTypes() throws Exception {

		return medataFormatDiscovery.getMetadataTypes();
	}

	/* (non-Javadoc)
	 * @see org.gcube.datacatalogue.metadatadiscovery.DataCatalogueMetadataDiscovery#getMetadataFormatForMetadataType(org.gcube.datacatalogue.metadatadiscovery.bean.MetadataType)
	 */
	@Override
	public MetadataFormat getMetadataFormatForMetadataType(MetadataType type) throws Exception {

		if(type==null)
			throw new Exception("MetadataType is null");

		MetadataFormat format = hash.get(type.getId());
		if(format!=null)
			return format;

		MedataFormatReader reader = new MedataFormatReader(scope, type.getId());
		return reader.getMetadataFormat();
	}

}
