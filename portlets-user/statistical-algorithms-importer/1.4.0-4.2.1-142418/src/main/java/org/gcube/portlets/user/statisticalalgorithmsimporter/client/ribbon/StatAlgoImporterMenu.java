package org.gcube.portlets.user.statisticalalgorithmsimporter.client.ribbon;


import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.TabPanel;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class StatAlgoImporterMenu extends TabPanel {
	//private AccountingManagerMenuMessages msgs;
	@SuppressWarnings("unused")
	private EventBus eventBus;
	

	public StatAlgoImporterMenu(EventBus eventBus) {	
		//super(GWT.<TabPanelAppearance>create(Css3BigTabPanelAppearance.class));
		Log.debug("Create StatisticalRunnerMenu");
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
//		TabItemConfig storageItemConf = new TabItemConfig("Storage", false);
//		storageItemConf.setIcon(AccountingManagerResources.INSTANCE.accountingStorage48());
//		EmptyPanel storageCategory=new EmptyPanel(AccountingType.STORAGE.name());
//		add(storageCategory, storageItemConf);
//		
//		
//
//		
//		setActiveWidget(getWidget(0));
//		
//		
//		addSelectionHandler(new SelectionHandler<Widget>() {
//
//			@Override
//			public void onSelection(SelectionEvent<Widget> event) {
//				Widget widget = event.getSelectedItem();
//				if (widget instanceof EmptyPanel) {
//					EmptyPanel p=(EmptyPanel) widget;
//					AccountingMenuEvent accountMenuEvent=new AccountingMenuEvent(AccountingType.valueOf(p.getId()));
//					eventBus.fireEvent(accountMenuEvent);
//				} else {
//
//				}
//
//			}
//		});
	}



}
