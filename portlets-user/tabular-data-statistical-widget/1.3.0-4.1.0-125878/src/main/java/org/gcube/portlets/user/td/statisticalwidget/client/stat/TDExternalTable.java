package org.gcube.portlets.user.td.statisticalwidget.client.stat;

import java.util.Map;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.ExternalTable;

import com.allen_sauer.gwt.log.client.Log;
import com.google.web.bindery.event.shared.EventBus;


/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TDExternalTable implements ExternalTable {
	protected EventBus eventBus;
	protected TRId trId;

	private String id;
	private String label;
	private Map<String,String> columns;
	
	/**
	 * 
	 * @param trId
	 * @param eventBus
	 * @param id
	 * @param label
	 * @param columns
	 */
	public TDExternalTable(TRId trId, EventBus eventBus,String id, String label, Map<String,String> columns) {
		Log.debug("TDExternalTable: "+trId);
		this.trId=trId;
		this.eventBus=eventBus;
		this.id=id;
		this.label=label;
		this.columns=columns;
	
	}
	
	@Override
	public String getId() {
		return "TableId [value="+id+"]";
	}
	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Map<String, String> getColumnsNameAndLabels() {
		return columns;
	}

	@Override
	public String toString() {
		return "TDExternalTable [trId=" + trId + ", id=" + id + ", label="
				+ label + ", columns=" + columns + "]";
	}
	
	
	
}


