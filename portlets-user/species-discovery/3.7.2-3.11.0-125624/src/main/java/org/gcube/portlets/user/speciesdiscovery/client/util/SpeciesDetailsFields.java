/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public enum SpeciesDetailsFields implements GridField {
	NAME("PARAMETER_NAME", "Parameter"),
	VALUE("PARAMETER_VALUE", "Value"),
	GROUP("PARAMETER_GROUP", "Group")
	;

	private String id;
	private String name;
	private boolean sortable;
	
	/**
	 * @param id the field id.
	 * @param name the field name.
	 */
	private SpeciesDetailsFields(String id, String name) {
		this(id, name, false);
	}
	
	private SpeciesDetailsFields(String id, String name, boolean sortable) {
		this.id = id;
		this.name = name;
		this.sortable = sortable;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	@Override
	public boolean isSortable() {
		return sortable;
	}
}
