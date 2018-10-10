/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery;

import java.util.List;

import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataProfile;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategory;




/**
 * The Interface DataCatalogueMetadataDiscovery.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 24, 2017
 */
public interface DataCatalogueMetadataDiscovery {


	/**
	 * Gets the list of metadata types.
	 *
	 * @return the list of metadata types
	 * @throws Exception the exception
	 */
	List<MetadataProfile> getListOfMetadataProfiles() throws Exception;



	/**
	 * Gets the list of namespace categories.
	 *
	 * @return the list of namespace categories
	 * @throws Exception the exception
	 */
	List<NamespaceCategory> getListOfNamespaceCategories() throws Exception;


	/**
	 * Gets the metadata format for metadata type.
	 *
	 * @param type the type
	 * @return the metadata format for metadata type
	 * @throws Exception the exception
	 */
	MetadataFormat getMetadataFormatForMetadataProfile(MetadataProfile type) throws Exception;


	/**
	 * Reset metadata profile.
	 * Forces current list of Metadata Profile at null in order to read it from IS againg
	 */
	void resetMetadataProfile();

	/**
	 * Reset namespace categories.
	 * Forces current list of Namespace Categories Profile at null in order to read it from IS againg
	 */
	void resetNamespaceCategories();

}
