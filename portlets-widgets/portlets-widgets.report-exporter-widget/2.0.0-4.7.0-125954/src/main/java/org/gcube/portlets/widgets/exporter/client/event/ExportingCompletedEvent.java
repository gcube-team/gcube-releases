package org.gcube.portlets.widgets.exporter.client.event;


import org.gcube.portlets.widgets.exporter.shared.TypeExporter;

import com.google.gwt.event.shared.GwtEvent;


public class ExportingCompletedEvent extends GwtEvent<ExportingCompletedEventHandler> {

	private String filePath;
	private String itemName;
	private TypeExporter type;

	public static Type<ExportingCompletedEventHandler> TYPE = new Type<ExportingCompletedEventHandler>();
	
	
	public ExportingCompletedEvent(String filePath, String itemName, TypeExporter type) {
		this.filePath = filePath;
		this.itemName = itemName;
		this.type = type;
	}
	
	@Override
	public Type<ExportingCompletedEventHandler> getAssociatedType() {	
		return TYPE;
	}

	@Override
	protected void dispatch(ExportingCompletedEventHandler handler) {
		handler.onExportFinished(this);
	}

	public String getFilePath() {
		return filePath;
	}

	public String getItemName() {
		return itemName;
	}

	public TypeExporter getType() {
		return type;
	}
}
