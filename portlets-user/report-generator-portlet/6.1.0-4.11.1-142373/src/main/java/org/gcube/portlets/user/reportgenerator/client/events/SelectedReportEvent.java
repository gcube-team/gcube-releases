package org.gcube.portlets.user.reportgenerator.client.events;

import org.gcube.portlets.user.reportgenerator.client.dialog.SelectVMEReportDialog.Action;
import org.gcube.portlets.user.reportgenerator.shared.VMETypeIdentifier;

import com.google.gwt.event.shared.GwtEvent;

public class SelectedReportEvent extends GwtEvent<SelectedReportEventHandler>{
	public static Type<SelectedReportEventHandler> TYPE = new Type<SelectedReportEventHandler>();
	private final String id;
	private final String name;
	private final VMETypeIdentifier type;
	private final Action theAction;

	public SelectedReportEvent(String id, String name, VMETypeIdentifier type, Action theAction) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.theAction = theAction;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public VMETypeIdentifier getType() {
		return type;
	}

	public Action getAction() {
		return theAction;
	}

	@Override
	public Type<SelectedReportEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectedReportEventHandler handler) {
		handler.onReportSelected(this);
	}
}
