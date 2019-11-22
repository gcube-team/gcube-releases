package org.gcube.portlets.user.tdtemplateoperation.client;

import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;

import com.google.gwt.user.client.Command;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;

/**
 * The Class TimeDimensionGroupColumnDialog.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 */
public class CreateTimeDimensionColumnDialog extends Window {
	protected String WIDTH = "500px";
	protected String HEIGHT = "250px";
	protected ServerObjectId srId;
	protected EventBus eventBus;
	private List<TdColumnData> listColumns;
	private CreateTimeDimensionColumnPanel timeDimensionGroupColumnPanel;
	
	/**
	 * Instantiates a new time dimension group column dialog.
	 *
	 * @param srId the sr id
	 * @param listColumns the list columns
	 * @param eventBus the event bus
	 */
	public CreateTimeDimensionColumnDialog(ServerObjectId srId, List<TdColumnData> listColumns,  EventBus eventBus) {
		create(srId, listColumns, eventBus);
	}
	

	/**
	 * Instantiates a new time dimension group column dialog.
	 *
	 * @param srId the sr id
	 * @param eventBus the event bus
	 */
	public CreateTimeDimensionColumnDialog(ServerObjectId srId,  EventBus eventBus) {
		create(srId, null, eventBus);
	}
	

	/**
	 * Creates the.
	 *
	 * @param srId the sr id
	 * @param listColumns the list columns
	 * @param eventBus the event bus
	 */
	protected void create(ServerObjectId srId, List<TdColumnData> listColumns,  EventBus eventBus) {
		this.srId = srId;
		this.listColumns = listColumns;
		this.eventBus=eventBus;
		setBodyBorder(false);
		this.setHeadingText("Create Time Dimension");
		try {
			
			Command hideWin = new Command() {
				
				@Override
				public void execute() {
					CreateTimeDimensionColumnDialog.this.hide();
				}
			};
			timeDimensionGroupColumnPanel = new CreateTimeDimensionColumnPanel(srId, listColumns, eventBus, hideWin);
			add(timeDimensionGroupColumnPanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the time dimension group column panel.
	 *
	 * @return the timeDimensionGroupColumnPanel
	 */
	public CreateTimeDimensionColumnPanel getTimeDimensionGroupColumnPanel() {
		return timeDimensionGroupColumnPanel;
	}
	

	/**
	 * Load list columns.
	 *
	 * @param listColumns the list columns
	 */
	public void loadListColumns(List<TdColumnData> listColumns){
		setListColumns(listColumns);
		timeDimensionGroupColumnPanel.addListColumns(listColumns);
	}

	/**
	 * Gets the list columns.
	 *
	 * @return the listColumns
	 */
	public List<TdColumnData> getListColumns() {
		return listColumns;
	}

	/**
	 * Sets the list columns.
	 *
	 * @param listColumns the listColumns to set
	 */
	public void setListColumns(List<TdColumnData> listColumns) {
		this.listColumns = listColumns;
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
//		timeDimensionGroupColumnPanel.getElement().getStyle().setZIndex(zIndex+1);
//		this.getElement().getStyle().setZIndex(zIndex+1);
//		this.show();
//		this.setModal(true);
		this.setPagePosition(x, y);
		this.setSize(WIDTH, HEIGHT);
		this.setModal(modal);
		this.show();
	}
}
