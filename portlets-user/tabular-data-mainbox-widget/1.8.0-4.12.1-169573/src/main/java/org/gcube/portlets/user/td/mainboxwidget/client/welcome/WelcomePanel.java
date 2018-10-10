package org.gcube.portlets.user.td.mainboxwidget.client.welcome;

import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.TabularResourceDataView;
import org.gcube.portlets.user.tdwx.client.TabularDataX;

import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class WelcomePanel extends FramedPanel {
	
	protected EventBus eventBus;
	protected TabularResourceDataView tabularResourceDataView;
	protected TabularDataX tabularData;

	public WelcomePanel(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		init();
		addWelcomeMessage();

		forceLayout();
	}
	
	protected void init(){
		setHeaderVisible(false);
		setBodyBorder(false);
		setResize(true);
		forceLayoutOnResize=true;
	}

	protected void addWelcomeMessage() {
		try {
			HTML welcome=new HTML("You Are Welcome!");
		
			
			
			add(welcome,new MarginData());
			
			

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}



}
