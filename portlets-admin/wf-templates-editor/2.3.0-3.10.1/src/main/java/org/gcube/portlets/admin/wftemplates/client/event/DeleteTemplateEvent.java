package org.gcube.portlets.admin.wftemplates.client.event;

import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;

import com.google.gwt.event.shared.GwtEvent;

public class DeleteTemplateEvent extends GwtEvent<DeleteTemplateEventHandler> {
	public static Type<DeleteTemplateEventHandler> TYPE = new Type<DeleteTemplateEventHandler>();
	private final WfTemplate deleted;
	
	public DeleteTemplateEvent(WfTemplate deleted) {
		super();
		this.deleted = deleted;
	}

	public WfTemplate getDeleted() {
		return deleted;
	}

	@Override
	public Type<DeleteTemplateEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DeleteTemplateEventHandler handler) {
		handler.onDeleteTemplate(this);
	}
}
