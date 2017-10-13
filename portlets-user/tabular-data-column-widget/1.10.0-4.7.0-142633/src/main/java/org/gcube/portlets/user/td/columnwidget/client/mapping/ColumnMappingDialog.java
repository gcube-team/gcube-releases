package org.gcube.portlets.user.td.columnwidget.client.mapping;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingList;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ColumnMappingDialog extends Window {
	private static final String WIDTH = "530px";
	private static final String HEIGHT = "450px";
	
	private ArrayList<ColumnMappingListener> listeners;
	private ColumnMappingMessages msgs;
	
	/**
	 * 
	 * @param trId
	 * @param selectedColumn
	 * @param dimensionTR
	 * @param referenceColumn
	 * @param eventBus
	 */
	public ColumnMappingDialog(TRId trId, ColumnData selectedColumn,
			TabResource dimensionTR, ColumnData referenceColumn,
			EventBus eventBus) {
		Log.debug("ColumnMappingDialog: [trId:" + trId
				+ ", selectedColumn:" + selectedColumn + ", dimensionTR:"
				+ dimensionTR + ", columnReference:" + referenceColumn
				+ ", eventBus:" + eventBus
				+ "]");
		listeners=new ArrayList<ColumnMappingListener>();
		initMessages();
		initWindow();
		ColumnMappingPanel columnMappingPanel = new ColumnMappingPanel(this,
				trId, selectedColumn,
				dimensionTR, referenceColumn,
				eventBus);
		add(columnMappingPanel);
	}
	
	
	protected void initMessages(){
		msgs = GWT.create(ColumnMappingMessages.class);
	}
	
	/**
	 * 
	 */
	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogHead());
		setClosable(true);
		setModal(true);
		forceLayoutOnResize = true;
		//getHeader().setIcon(ResourceBundle.INSTANCE.replaceBatch());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	/**
	 * 
	 */
	public void close() {
		fireAbortedColumnMapping();
		hide();

	}
	
	/**
	 * 
	 * @param columnMappingList
	 */
	public void saveMapping(ColumnMappingList columnMappingList) {
		fireSelectedColumnMapping(columnMappingList);
		hide();

	}
	
	
	/**
	 * 
	 * @param listener
	 */
	public void addColumnMappingListener(ColumnMappingListener listener) {
		listeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeColumnMappingListener(ColumnMappingListener listener) {
		listeners.remove(listener);
	}

	/**
	 * 
	 * @param columnMappingList
	 */
	public void fireSelectedColumnMapping(ColumnMappingList columnMappingList) {
		for (ColumnMappingListener listener : listeners)
			listener.selectedColumnMapping(columnMappingList);
		hide();
	}

	/**
	 * 
	 */
	public void fireAbortedColumnMapping() {
		for (ColumnMappingListener listener : listeners)
			listener.abortedColumnMapping();
		hide();
	}

	/**
	 * 
	 * @param reason
	 * @param detail
	 */
	public void fireFailedColumnMapping(String reason, String detail) {
		for (ColumnMappingListener listener : listeners)
			listener.failedColumnMapping(reason, detail);
		hide();
	}

	

}
