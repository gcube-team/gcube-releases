package org.gcube.portlets.widgets.exporter.client.event;


import java.io.File;

import com.google.gwt.event.shared.GwtEvent;


public class ReportExporterEvent extends GwtEvent<ReportExporterEventHandler> {

	public enum OperationResult {
		
		SAVED,
		
		SAVED_OPEN,
		
		FAILURE;
	}
	
	private final OperationResult result;
	private final String itemId;
	public static Type<ReportExporterEventHandler> TYPE = new Type<ReportExporterEventHandler>();
	
	
	public ReportExporterEvent(OperationResult result, String itemId) {
		super();
		this.result = result;
		this.itemId = itemId;
	}
	
	@Override
	public Type<ReportExporterEventHandler> getAssociatedType() {	
		return TYPE;
	}

	@Override
	protected void dispatch(ReportExporterEventHandler handler) {
		handler.onCompletedExport(this);
	}
	
	public OperationResult getOperationResult() {
		return result;
	}

	public String getItemId() {
		return itemId;
	}
}
