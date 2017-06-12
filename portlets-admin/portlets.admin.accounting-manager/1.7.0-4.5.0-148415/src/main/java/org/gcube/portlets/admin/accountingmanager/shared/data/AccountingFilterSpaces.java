package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class AccountingFilterSpaces extends AccountingFilterDefinition
		implements Serializable {

	private static final long serialVersionUID = -6805006183397381154L;
	private Spaces spaces;
	private ArrayList<AccountingFilter> filters;

	public AccountingFilterSpaces() {
		super();
		this.chartType = ChartType.Spaces;
		spaces = null;
		filters = null;

	}

	/**
	 * 
	 * @param spaces spaces
	 */
	public AccountingFilterSpaces(Spaces spaces) {
		super();
		chartType = ChartType.Spaces;
		this.spaces = spaces;
		this.filters = null;
	}

	/**
	 * 
	 * @param spaces spaces
	 * @param filters filters
	 */
	public AccountingFilterSpaces(Spaces spaces,
			ArrayList<AccountingFilter> filters) {
		super();
		chartType = ChartType.Spaces;
		this.spaces = spaces;
		this.filters = filters;

	}

	public Spaces getSpaces() {
		return spaces;
	}

	public void setSpaces(Spaces spaces) {
		this.spaces = spaces;
	}

	public ArrayList<AccountingFilter> getFilters() {
		return filters;
	}

	public void setFilters(ArrayList<AccountingFilter> filters) {
		this.filters = filters;
	}

	@Override
	public String toString() {
		return "AccountingFilterSpaces [spaces=" + spaces + ", filters="
				+ filters + "]";
	}

}
