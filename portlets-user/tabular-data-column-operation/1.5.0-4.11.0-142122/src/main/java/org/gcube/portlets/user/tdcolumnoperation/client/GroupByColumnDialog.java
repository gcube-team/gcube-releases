package org.gcube.portlets.user.tdcolumnoperation.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 *
 */
public class GroupByColumnDialog extends Window {
	protected TRId trId;
	protected String columnName = null;
	protected EventBus eventBus;
	
	public GroupByColumnDialog(TRId trId,  EventBus eventBus) {
		create(trId, null, eventBus);
	}

	public GroupByColumnDialog(TRId trId, String columnName,  EventBus eventBus) {
		create(trId, columnName, eventBus);
	}

	protected void create(TRId trId, String columnName,  EventBus eventBus) {
		this.trId = trId;
		this.columnName = columnName;
		this.eventBus=eventBus;
		setBodyBorder(false);
		
		GroupByColumnPanel grouPanel;
		
		try {
			grouPanel = new GroupByColumnPanel(trId, columnName, eventBus);
			add(grouPanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
