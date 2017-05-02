package org.gcube.portlets.admin.accountingmanager.server.is;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.tabs.EnableTab;
import org.gcube.portlets.admin.accountingmanager.shared.tabs.EnableTabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class BuildEnableTabs {

	private static Logger logger = LoggerFactory
			.getLogger(BuildEnableTabs.class);

	public static EnableTabs build(String scope) throws ServiceException {
		ArrayList<EnableTab> enableTabList = new ArrayList<>();
 
		if (Constants.DEBUG_MODE) {
			EnableTab enableTabData = new EnableTab(
					AccountingType.SERVICE, null);
			enableTabList.add(enableTabData);
		} else {
			EnableTabsJAXB enableTabsJAXB=null;
			try {
				enableTabsJAXB= InformationSystemUtils
					.retrieveEnableTab(scope);
			} catch(ServiceException e){
				logger.info(e.getLocalizedMessage());
			}
		
			logger.debug("Enable Tabs: " + enableTabsJAXB);
			if (enableTabsJAXB != null && enableTabsJAXB.getEnableTabs() != null
					&& !enableTabsJAXB.getEnableTabs().isEmpty()) {
				AccountingType type;
				for (EnableTabJAXB enableTab : enableTabsJAXB.getEnableTabs()) {
					type = AccountingType
							.getTypeFromString(enableTab.getName());
					if (type != null) {
						if (enableTab.getRoles() != null
								&& !enableTab.getRoles().isEmpty()) {
							ArrayList<String> enableRoles = new ArrayList<>();
							enableRoles.addAll(enableTab.getRoles());
							enableTabList.add(new EnableTab(type,
									enableRoles));
						} else {
							enableTabList
									.add(new EnableTab(type, null));
						}
					}
				}
			} else {
				logger.info("AccountingManager use default configuration for scope: "+scope);
				EnableTab enableTabDataService = new EnableTab(
						AccountingType.SERVICE, null);
				enableTabList.add(enableTabDataService);
				EnableTab enableTabDataStorage = new EnableTab(
						AccountingType.STORAGE, null);
				enableTabList.add(enableTabDataStorage);
				EnableTab enableTabDataJob = new EnableTab(
						AccountingType.JOB, null);
				enableTabList.add(enableTabDataJob);
				
			}
		}

		EnableTabs enableTabs = new EnableTabs(enableTabList);
		logger.debug("EnableTabsData: " + enableTabs);
		return enableTabs;
	}

}
