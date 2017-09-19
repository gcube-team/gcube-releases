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
public class AggregateByTimeColumnDialog extends Window {
	protected TRId trId;
	protected String columnName = null;
	protected EventBus eventBus;
	
	public AggregateByTimeColumnDialog(TRId trId,  EventBus eventBus) {
		create(trId, null, eventBus);
	}

	public AggregateByTimeColumnDialog(TRId trId, String columnName,  EventBus eventBus) {
		create(trId, columnName, eventBus);
	}

	protected void create(TRId trId, String columnName,  EventBus eventBus) {
		this.trId = trId;
		this.columnName = columnName;
		this.eventBus=eventBus;
		setBodyBorder(false);
		
		AggregateByTimeColumnPanel grouPanel;
		
		try {
			grouPanel = new AggregateByTimeColumnPanel(trId, columnName, eventBus);
			add(grouPanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
