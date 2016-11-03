/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery;

import java.util.List;

import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataType;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;



/**
 * The Interface DataCatalogueMetadataDiscovery.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 8, 2016
 */
public interface DataCatalogueMetadataDiscovery {


	/**
	 * Gets the list of metadata types.
	 *
	 * @return the list of metadata types
	 * @throws Exception
	 */
	List<MetadataType> getListOfMetadataTypes() throws Exception;


	/**
	 * Gets the metadata format for metadata type.
	 *
	 * @param type the type
	 * @return the metadata format for metadata type
	 * @throws Exception
	 */
	MetadataFormat getMetadataFormatForMetadataType(MetadataType type) throws Exception;

}
