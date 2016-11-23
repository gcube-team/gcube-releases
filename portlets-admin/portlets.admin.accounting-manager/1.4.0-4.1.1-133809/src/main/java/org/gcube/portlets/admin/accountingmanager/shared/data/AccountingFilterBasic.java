package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingFilterBasic extends AccountingFilterDefinition implements Serializable {

	private static final long serialVersionUID = -6805006183397381154L;
	ArrayList<AccountingFilter> filters;

	
	public AccountingFilterBasic() {
		super();	
		this.chartType=ChartType.Basic;
		filters=null;
	}

	public AccountingFilterBasic(ArrayList<AccountingFilter> filters) {
		super();
		chartType = ChartType.Basic;
		this.filters = filters;
	}

	public ArrayList<AccountingFilter> getFilters() {
		return filters;
	}

	public void setFilters(ArrayList<AccountingFilter> filters) {
		this.filters = filters;
	}

	@Override
	public String toString() {
		return "AccountingFilterBasic [filters=" + filters + "]";
	}

	

}
