package org.gcube.portlets.user.tdtemplateoperation.client;

import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;

/**
 * The Class AggregateByTimeColumnDialog.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 */
public class AggregateByTimeColumnDialog extends Window implements LoaderTimeDimensionColumnData{
	protected ServerObjectId srId;
	protected String columnName = null;
	protected EventBus eventBus;
	protected AggregateByTimeColumnPanel aggregateTimePanel;
	
	/**
	 * Instantiates a new aggregate by time column dialog.
	 *
	 * @param srId the sr id
	 * @param eventBus the event bus
	 */
	public AggregateByTimeColumnDialog(ServerObjectId srId,  EventBus eventBus) {
		create(srId, null, eventBus);
	}

	/**
	 * Instantiates a new aggregate by time column dialog.
	 *
	 * @param srId the sr id
	 * @param columnName the column name
	 * @param eventBus the event bus
	 */
	public AggregateByTimeColumnDialog(ServerObjectId srId, String columnName,  EventBus eventBus) {
		create(srId, columnName, eventBus);
	}

	/**
	 * Creates the.
	 *
	 * @param srId the sr id
	 * @param columnName the column name
	 * @param eventBus the event bus
	 */
	protected void create(ServerObjectId srId, String columnName,  EventBus eventBus) {
		this.srId = srId;
		this.columnName = columnName;
		this.eventBus=eventBus;
		setBodyBorder(false);
		this.setHeadingText("Aggregate By Time on: "+columnName);

		try {
			aggregateTimePanel = new AggregateByTimeColumnPanel(srId, columnName, eventBus);
			add(aggregateTimePanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.client.LoaderTimeDimensionColumnData#loadTimeDimensionColumns(java.util.List)
	 */
	@Override
	public boolean loadTimeDimensionColumns(List<TdColumnData> list) {
		aggregateTimePanel.loadTimeDimensionData(list);
		return true;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.client.LoaderTimeDimensionColumnData#loadOtherColumns(java.util.List)
	 */
	@Override
	public boolean loadOtherColumns(List<TdColumnData> list) {
		aggregateTimePanel.loadOthersTdColumnData(list);
		return true;
	}

	
	/**
	 * Show.
	 *
	 * @param zIndex the z index
	 * @param posX the pos x
	 * @param posY the pos y
	 * @param modal the modal
	 */
	public void show(int zIndex, int posX, int posY, boolean modal) {
		this.setModal(modal);
		this.show();
	}
}
