/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class NameChangeEvent extends GwtEvent<NameChangeHandler> {

	/**
	 * Handler type.
	 */
	private static final Type<NameChangeHandler> TYPE = new Type<NameChangeHandler>();

	/**
	 * Fires a data load event on all registered handlers in the handler
	 * manager.
	 * @param source the source of the handlers
	 * @param name the new name.
	 * @param correctName <code>true</code> if is a correct name.
	 */
	public static void fire(HasNameChangeHandlers source, String name, boolean correctName) {
		NameChangeEvent event = new NameChangeEvent(name, correctName);
		source.fireEvent(event);
	}

	/**
	 * Gets the type associated with this event.
	 * 
	 * @return returns the handler type
	 */
	public static Type<NameChangeHandler> getType() {
		return TYPE;
	}

	protected String name;
	protected boolean correctName;

	/**
	 * @param name
	 * @param correctName
	 */
	public NameChangeEvent(String name, boolean correctName) {
		this.name = name;
		this.correctName = correctName;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the correctName
	 */
	public boolean isCorrectName() {
		return correctName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispatch(NameChangeHandler handler) {
		handler.onNameChange(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<NameChangeHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toDebugString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NameChangeEvent [name=");
		builder.append(name);
		builder.append(", correctName=");
		builder.append(correctName);
		builder.append("]");
		return builder.toString();
	}
}
