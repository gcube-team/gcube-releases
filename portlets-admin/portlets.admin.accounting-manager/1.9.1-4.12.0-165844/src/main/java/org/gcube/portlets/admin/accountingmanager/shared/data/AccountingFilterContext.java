package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class AccountingFilterContext extends AccountingFilterDefinition
		implements Serializable {

	private static final long serialVersionUID = -6805006183397381154L;
	private Context context;
	private ArrayList<AccountingFilter> filters;

	public AccountingFilterContext() {
		super();
		this.chartType = ChartType.Context;
		context = null;
		filters = null;

	}

	public AccountingFilterContext(Context context,
			ArrayList<AccountingFilter> filters) {
		super();
		chartType = ChartType.Context;
		this.context = context;
		this.filters = filters;

	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ArrayList<AccountingFilter> getFilters() {
		return filters;
	}

	public void setFilters(ArrayList<AccountingFilter> filters) {
		this.filters = filters;
	}

	@Override
	public String toString() {
		return "AccountingFilterContext [context=" + context + ", filters="
				+ filters + "]";
	}

}
