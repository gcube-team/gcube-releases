package org.gcube.portlets.admin.accountingmanager.client.properties;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingFilter;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface AccountingFilterProperties extends
		PropertyAccess<AccountingFilter> {

	ModelKeyProvider<AccountingFilter> id();

	ValueProvider<AccountingFilter, FilterKey> filterKey();

	ValueProvider<AccountingFilter, String> filterValue();

}