package org.gcube.portlets.user.tdtemplateoperation.client;

import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;

import com.google.gwt.user.client.Command;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 *
 */
public class NormalizeColumnDialog extends Window {
	protected ServerObjectId srId;
	protected EventBus eventBus;
	protected NormalizeColumnPanel normalizePanel;
	
	public NormalizeColumnDialog(ServerObjectId srId,  EventBus eventBus) {
		create(srId, eventBus);
	}

	protected void create(ServerObjectId srId, EventBus eventBus) {
		this.srId = srId;
		this.eventBus=eventBus;
		setBodyBorder(false);
		this.setHeadingText("Normalize Columns:");

		try {
			
			Command hideWin = new Command() {
				
				@Override
				public void execute() {
					NormalizeColumnDialog.this.hide();
				}
			};
			
			normalizePanel = new NormalizeColumnPanel(srId, eventBus, hideWin);
			add(normalizePanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Show.
	 *
	 * @param zIndex the z index
	 * @param x the x cordinate page position
	 * @param y the y cordinate page position
	 * @param modal the modal
	 */
	public void show(int zIndex, int x, int y, boolean modal) {
//		this.getElement().getStyle().setZIndex(zIndex+1);
		this.setPagePosition(x, y);
		this.setModal(modal);
		this.show();
	}

	/**
	 * @param listColumns
	 */
	public void loadListColumns(List<TdColumnData> listColumns) {
		normalizePanel.loadListTdColumnData(listColumns);
		
	}
}
