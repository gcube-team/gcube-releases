/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client.tr;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.data.analysis.dataminermanagercl.shared.data.ColumnItem;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TabularResourceData implements Serializable {

	private static final long serialVersionUID = 3680043028889915839L;
	private String tabularResourceId;
	private String name;
	private String description;
	private String type;
	private ArrayList<ColumnItem> columns;

	/**
	 * 
	 */
	public TabularResourceData() {
		super();
	}

	public TabularResourceData(String tabularResourceId, String name,
			String description, String type, ArrayList<ColumnItem> columns) {
		super();
		this.tabularResourceId = tabularResourceId;
		this.name = name;
		this.description = description;
		this.type = type;
		this.columns = columns;
	}

	public String getTabularResourceId() {
		return tabularResourceId;
	}

	public void setTabularResourceId(String tabularResourceId) {
		this.tabularResourceId = tabularResourceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<ColumnItem> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<ColumnItem> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "TabularResourceData [tabularResourceId=" + tabularResourceId
				+ ", name=" + name + ", description=" + description + ", type="
				+ type + ", columns=" + columns + "]";
	}

	

}
