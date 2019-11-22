package org.gcube.portlets.user.tdcolumnoperation.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;

/**
 * Delete Column Dialog
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class MergeColumnDialog extends Window {
	protected TRId trId;
	protected String columnName = null;
	protected EventBus eventBus;
	
	public MergeColumnDialog(TRId trId,  EventBus eventBus) {
		create(trId, null, eventBus);
	}

	public MergeColumnDialog(TRId trId, String columnName,  EventBus eventBus) {
		create(trId, columnName, eventBus);
	}

	protected void create(TRId trId, String columnName,  EventBus eventBus) {
		this.trId = trId;
		this.columnName = columnName;
		this.eventBus=eventBus;
		setBodyBorder(false);
		
		MergeColumnPanel mergePanel;
		
		try {
			mergePanel = new MergeColumnPanel(trId, columnName, eventBus);
			add(mergePanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
