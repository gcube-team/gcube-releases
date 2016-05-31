package org.gcube.portlets.admin.wftemplates.client.event;

import com.google.gwt.event.shared.GwtEvent;
/**
 * <code> AddStepEvent </code>  is the event fired in case of new add step button clicked
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class AddStepEvent extends GwtEvent<AddStepEventHandler> {
	public static Type<AddStepEventHandler> TYPE = new Type<AddStepEventHandler>();
	private final String name;
	private final String description;

	public AddStepEvent(String name, String desc) {
		super();
		this.name = name;
		description = desc;
	}
		
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public Type<AddStepEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddStepEventHandler handler) {
		handler.onAddStep(this);
	}
}
