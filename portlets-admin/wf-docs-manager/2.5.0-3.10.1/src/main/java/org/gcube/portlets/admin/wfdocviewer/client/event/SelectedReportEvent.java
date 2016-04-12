package org.gcube.portlets.admin.wfdocviewer.client.event;



import com.google.gwt.event.shared.GwtEvent;

public class SelectedReportEvent extends GwtEvent<SelectedReportEventHandler> {
	public static Type<SelectedReportEventHandler> TYPE = new Type<SelectedReportEventHandler>();
	
	private final String selectedReportId;
	private final String selectedReportName;
	
	public SelectedReportEvent(String id, String name) {
		selectedReportId = id;
		selectedReportName = name;
	}

	public String getSelectedReportId() {	return selectedReportId; }
	public String getSelectedReportName() {	return selectedReportName; }

	@Override
	protected void dispatch(SelectedReportEventHandler handler) {
		handler.onSelectedReport(this);		
	}

	@Override
	public Type<SelectedReportEventHandler> getAssociatedType() {
		return TYPE;
	}

}
