package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class FilterValuesResponse implements Serializable {

	private static final long serialVersionUID = 5446974602912986860L;

	private ArrayList<FilterValue> filterValues;

	public FilterValuesResponse() {
		super();
	}

	public FilterValuesResponse(ArrayList<FilterValue> filterValues) {
		super();
		this.filterValues = filterValues;
	}

	public ArrayList<FilterValue> getFilterValues() {
		return filterValues;
	}

	public void setFilterValues(ArrayList<FilterValue> filterValues) {
		this.filterValues = filterValues;
	}

	@Override
	public String toString() {
		return "FilterValuesResponse [filterValues=" + filterValues + "]";
	}

}
