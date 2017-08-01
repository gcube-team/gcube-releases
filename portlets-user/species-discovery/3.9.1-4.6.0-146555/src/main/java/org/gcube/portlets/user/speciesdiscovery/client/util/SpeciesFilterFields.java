/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public enum SpeciesFilterFields implements GridField {
	
	NAME("name","Name"),
	VALUE("value","Value"),
	TYPE("type","Type"),
	OPERATOR("operator","Operator"),
	LABEL("label","Label")
	;

	private String id;
	private String name;
	private boolean sortable;
	
	/**
	 * @param id the field id.
	 * @param name the field name.
	 */
	private SpeciesFilterFields(String id, String name) {
		this(id, name, false);
	}
	
	private SpeciesFilterFields(String id, String name, boolean sortable) {
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
