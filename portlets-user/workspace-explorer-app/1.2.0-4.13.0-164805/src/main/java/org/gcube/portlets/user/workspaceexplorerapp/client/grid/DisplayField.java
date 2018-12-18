/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client.grid;


/**
 * The Enum DisplayField.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 18, 2016
 */
public enum DisplayField {

	ICON("Icon", true, false),
	NAME("Name", true, true),
	OWNER("Owner", true, true),
	CREATION_DATE("Creation Date", false, true);


	private String label;
	private boolean isSortable;
	private boolean showIntable;
	/**
	 * Instantiates a new display field.
	 *
	 * @param id the id
	 * @param isSortable the is sortable
	 */
	private DisplayField(String label, boolean showInTable, boolean isSortable) {
		this.label = label;
		this.isSortable = isSortable;
		this.showIntable = showInTable;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {

		return label;
	}

	/**
	 * @return the isSortable
	 */
	public boolean isSortable() {

		return isSortable;
	}

	/**
	 * @return the showIntable
	 */
	public boolean isShowIntable() {

		return showIntable;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {

		this.label = label;
	}

	/**
	 * @param isSortable the isSortable to set
	 */
	public void setSortable(boolean isSortable) {

		this.isSortable = isSortable;
	}

	/**
	 * @param showIntable the showIntable to set
	 */
	public void setShowIntable(boolean showIntable) {

		this.showIntable = showIntable;
	}

}
