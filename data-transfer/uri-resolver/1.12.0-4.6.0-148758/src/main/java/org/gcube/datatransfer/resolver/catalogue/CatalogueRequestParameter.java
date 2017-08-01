/**
 *
 */
package org.gcube.datatransfer.resolver.catalogue;


/**
 * The Interface CatalogueRequestParameter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 2, 2016
 */
public enum CatalogueRequestParameter {

	GCUBE_SCOPE("gcube_scope",true),
	ENTITY_CONTEXT("entity_context",true),
	ENTITY_NAME("entity_name",true),
	CLEAR_URL("clear_url",false),
	QUERY_STRING("query_string",false);

	private String key;
	private boolean mandatory;
	/**
	 *
	 */
	private CatalogueRequestParameter(String key, boolean isMandatory) {
		this.key = key;
		this.mandatory = isMandatory;
	}

	/**
	 * @return the key
	 */
	public String getKey() {

		return key;
	}

	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {

		return mandatory;
	}
}
