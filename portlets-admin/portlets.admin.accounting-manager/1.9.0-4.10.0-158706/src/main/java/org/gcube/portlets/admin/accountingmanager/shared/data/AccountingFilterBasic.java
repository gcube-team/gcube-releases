package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class AccountingFilterBasic extends AccountingFilterDefinition implements
		Serializable {

	private static final long serialVersionUID = -6805006183397381154L;
	private ArrayList<AccountingFilter> filters;
	private Boolean noContext;

	public AccountingFilterBasic() {
		super();
		this.chartType = ChartType.Basic;
		noContext = false;
		filters = null;
	}
	
	public AccountingFilterBasic(boolean noContenxt) {
		super();
		chartType = ChartType.Basic;
		this.filters = null;
		this.noContext = noContenxt;
	}

	

	public AccountingFilterBasic(ArrayList<AccountingFilter> filters,
			boolean noContenxt) {
		super();
		chartType = ChartType.Basic;
		this.filters = filters;
		this.noContext = noContenxt;
	}

	public ArrayList<AccountingFilter> getFilters() {
		return filters;
	}

	public void setFilters(ArrayList<AccountingFilter> filters) {
		this.filters = filters;
	}

	public boolean isNoContext() {
		return noContext;
	}

	public void setNoContext(Boolean noContext) {
		this.noContext = noContext;
	}

	@Override
	public String toString() {
		return "AccountingFilterBasic [filters=" + filters + ", noContext="
				+ noContext + "]";
	}

}
