package org.gcube.portlets.user.tdcolumnoperation.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.form.ComboBox;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 3, 2014
 *
 */
public class SplitColumnDialog extends Window {
	protected TRId trId;
	protected ComboBox<ColumnData> combo = null;
	protected String columnName = null;
	protected EventBus eventBus;
	
	public SplitColumnDialog(TRId trId,  EventBus eventBus) {
		create(trId, null, eventBus);
	}

	public SplitColumnDialog(TRId trId, String columnName,  EventBus eventBus) {
		create(trId, columnName, eventBus);
	}

	protected void create(TRId trId, String columnName,  EventBus eventBus) {
		this.trId = trId;
		this.columnName = columnName;
		this.eventBus=eventBus;
		setBodyBorder(false);
		
		SplitColumnPanel spliPanel;
		
		try {
			spliPanel = new SplitColumnPanel(trId, columnName, eventBus);
			add(spliPanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
