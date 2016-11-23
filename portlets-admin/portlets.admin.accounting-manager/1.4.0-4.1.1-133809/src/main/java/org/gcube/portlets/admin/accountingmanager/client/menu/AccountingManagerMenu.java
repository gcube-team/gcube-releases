package org.gcube.portlets.admin.accountingmanager.client.menu;

import org.gcube.portlets.admin.accountingmanager.client.event.AccountingMenuEvent;
import org.gcube.portlets.admin.accountingmanager.client.resource.AccountingManagerResources;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.tabs.EnableTab;
import org.gcube.portlets.admin.accountingmanager.shared.tabs.EnableTabs;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.theme.neptune.client.base.tabs.Css3BigTabPanelAppearance;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingManagerMenu extends TabPanel {
	// private AccountingManagerMenuMessages msgs;
	private EventBus eventBus;
	private EnableTabs enableTabs;

	public AccountingManagerMenu(EnableTabs enableTabs, EventBus eventBus) {
		super(GWT.<TabPanelAppearance> create(Css3BigTabPanelAppearance.class));
		Log.debug("Create AccountingManagerMenu");
		this.eventBus = eventBus;
		this.enableTabs = enableTabs;
		// this.msgs = GWT.create(AccountingManagerMenuMessages.class);
		setBodyBorder(false);
		setBorders(false);
		setAnimScroll(false);
		setTabScroll(false);
		setCloseContextMenu(true);
		setHeight(60);
		addTabs();
	}

	private void addTabs() {
		for (EnableTab enable : enableTabs.getTabs()) {
			if (enable.getAccountingType() != null) {
				switch (enable.getAccountingType()) {
				case JOB:
					TabItemConfig jobItemConf = new TabItemConfig("Job", false);
					jobItemConf.setIcon(AccountingManagerResources.INSTANCE
							.accountingJob48());
					EmptyPanel jobCategory = new EmptyPanel(AccountingType.JOB.name());
					add(jobCategory, jobItemConf);
					break;
				case PORTLET:
					break;
				case SERVICE:
					TabItemConfig serviceItemConf = new TabItemConfig("Service", false);
					serviceItemConf.setIcon(AccountingManagerResources.INSTANCE
							.accountingService48());
					EmptyPanel serviceCategory = new EmptyPanel(
							AccountingType.SERVICE.name());
					add(serviceCategory, serviceItemConf);
					break;
				case STORAGE:
					TabItemConfig storageItemConf = new TabItemConfig("Storage", false);
					storageItemConf.setIcon(AccountingManagerResources.INSTANCE
							.accountingStorage48());
					EmptyPanel storageCategory = new EmptyPanel(
							AccountingType.STORAGE.name());
					add(storageCategory, storageItemConf);
					
					break;
				case TASK:
					break;
				default:
					break;
				
				}
			}
		}

	

		

		

		/*
		 * TabItemConfig portletItemConf = new TabItemConfig("Portlet", false);
		 * portletItemConf
		 * .setIcon(AccountingManagerResources.INSTANCE.accountingPortlet48());
		 * EmptyPanel portletCategory=new
		 * EmptyPanel(AccountingType.PORTLET.name()); add(portletCategory,
		 * portletItemConf);
		 * 
		 * TabItemConfig taskItemConf = new TabItemConfig("Task", false);
		 * taskItemConf
		 * .setIcon(AccountingManagerResources.INSTANCE.accountingTask48());
		 * EmptyPanel taskCategory=new EmptyPanel(AccountingType.TASK.name());
		 * add(taskCategory, taskItemConf);
		 */

		setActiveWidget(getWidget(0));

		addSelectionHandler(new SelectionHandler<Widget>() {

			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				Widget widget = event.getSelectedItem();
				if (widget instanceof EmptyPanel) {
					EmptyPanel p = (EmptyPanel) widget;
					AccountingMenuEvent accountMenuEvent = new AccountingMenuEvent(
							AccountingType.valueOf(p.getId()));
					eventBus.fireEvent(accountMenuEvent);
				} else {

				}

			}
		});
	}

}
