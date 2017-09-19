/**
 *
 */
package org.gcube.datatransfer.resolver.catalogue;


/**
 * The Class CatalogueParameter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 2, 2016
 */
public class CatalogueParameter {

	private String key;
	private boolean mandatory;

	/**
	 * Instantiates a new catalogue parameter.
	 *
	 * @param key the key
	 * @param mandatory the mandatory
	 */
	public CatalogueParameter(String key, boolean mandatory) {
		this.key = key;
		this.mandatory = mandatory;
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


	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {

		this.key = key;
	}


	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {

		this.mandatory = mandatory;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("CatalogueParameter [key=");
		builder.append(key);
		builder.append(", mandatory=");
		builder.append(mandatory);
		builder.append("]");
		return builder.toString();
	}

}
