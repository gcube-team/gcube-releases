package org.gcube.portlets.admin.accountingmanager.client.menu;


import org.gcube.portlets.admin.accountingmanager.client.event.AccountingMenuEvent;
import org.gcube.portlets.admin.accountingmanager.client.resource.AccountingManagerResources;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;

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
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class AccountingManagerMenu extends TabPanel {
	//private AccountingManagerMenuMessages msgs;
	private EventBus eventBus;
	

	public AccountingManagerMenu(EventBus eventBus) {	
		super(GWT.<TabPanelAppearance>create(Css3BigTabPanelAppearance.class));
		Log.debug("Create AccountingManagerMenu");
		this.eventBus = eventBus;
		//this.msgs = GWT.create(AccountingManagerMenuMessages.class);
		setBodyBorder(false);
		setBorders(false);
		setAnimScroll(false);
		setTabScroll(false);
		setCloseContextMenu(true);
		addTabs();
		setHeight(60);
		
	}
	
	protected void addTabs() {
		TabItemConfig storageItemConf = new TabItemConfig("Storage", false);
		storageItemConf.setIcon(AccountingManagerResources.INSTANCE.accountingStorage48());
		EmptyPanel storageCategory=new EmptyPanel(AccountingType.STORAGE.name());
		add(storageCategory, storageItemConf);
		
		TabItemConfig serviceItemConf = new TabItemConfig("Service", false);
		serviceItemConf.setIcon(AccountingManagerResources.INSTANCE.accountingService48());
		EmptyPanel serviceCategory=new EmptyPanel(AccountingType.SERVICE.name());
		add(serviceCategory, serviceItemConf);
		
		
		/*
		TabItemConfig portletItemConf = new TabItemConfig("Portlet", false);
		portletItemConf.setIcon(AccountingManagerResources.INSTANCE.accountingPortlet48());
		EmptyPanel portletCategory=new EmptyPanel(AccountingType.PORTLET.name());
		add(portletCategory, portletItemConf);
		
		TabItemConfig taskItemConf = new TabItemConfig("Task", false);
		taskItemConf.setIcon(AccountingManagerResources.INSTANCE.accountingTask48());
		EmptyPanel taskCategory=new EmptyPanel(AccountingType.TASK.name());
		add(taskCategory, taskItemConf);
		*/
		
		/*TabItemConfig jobItemConf = new TabItemConfig("Job", false);
		jobItemConf.setIcon(AccountingManagerResources.INSTANCE.accountingJob48());
		EmptyPanel jobCategory=new EmptyPanel(AccountingType.JOB.name());
		add(jobCategory, jobItemConf);*/
		
		
		setActiveWidget(getWidget(0));
		
		
		addSelectionHandler(new SelectionHandler<Widget>() {

			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				Widget widget = event.getSelectedItem();
				if (widget instanceof EmptyPanel) {
					EmptyPanel p=(EmptyPanel) widget;
					AccountingMenuEvent accountMenuEvent=new AccountingMenuEvent(AccountingType.valueOf(p.getId()));
					eventBus.fireEvent(accountMenuEvent);
				} else {

				}

			}
		});
	}



}
