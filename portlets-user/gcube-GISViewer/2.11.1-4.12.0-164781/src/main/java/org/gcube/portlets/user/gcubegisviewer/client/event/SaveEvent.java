/**
 * 
 */
package org.gcube.portlets.user.gcubegisviewer.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class SaveEvent extends GwtEvent<SaveHandler> {

	/**
	 * Handler type.
	 */
	private static final Type<SaveHandler> TYPE = new Type<SaveHandler>();

	/**
	 * Fires a save event on all registered handlers in the handler
	 * manager.
	 * @param source the source of the handlers
	 */
	public static void fireSave(HasSaveHandlers source, String name, String contentType) {
		SaveEvent event = new SaveEvent(EventType.SAVE, name, contentType, null);
		source.fireEvent(event);
	}

	public static void fireSaveSuccess(HasSaveHandlers source, String name, String contentType) {
		SaveEvent event = new SaveEvent(EventType.SAVE_SUCCESS, name, contentType, null);
		source.fireEvent(event);
	}
	
	public static void fireSaveFailure(HasSaveHandlers source, String name, String contentType, Throwable failureCause) {
		SaveEvent event = new SaveEvent(EventType.SAVE_FAILURE, name, contentType, failureCause);
		source.fireEvent(event);
	}

	/**
	 * Gets the type associated with this event.
	 * 
	 * @return returns the handler type
	 */
	public static Type<SaveHandler> getType() {
		return TYPE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<SaveHandler> getAssociatedType() {
		return TYPE;
	}

	protected enum EventType {SAVE, SAVE_SUCCESS, SAVE_FAILURE};
	protected EventType type;
	protected String name; 
	protected String contentType; 
	protected Throwable failureCause;


	/**
	 * @param type
	 * @param name
	 * @param contentType
	 * @param url
	 * @param failureCause
	 */
	public SaveEvent(EventType type, String name, String contentType, Throwable failureCause) {
		this.type = type;
		this.name = name;
		this.contentType = contentType;
		this.failureCause = failureCause;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispatch(SaveHandler handler) {
		switch (type) {
			case SAVE: handler.onSave(this); break;
			case SAVE_SUCCESS: handler.onSaveSuccess(this); break;
			case SAVE_FAILURE: handler.onSaveFailure(this); break;
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @return the failureCause
	 */
	public Throwable getFailureCause() {
		return failureCause;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SaveEvent [type=");
		builder.append(type);
		builder.append(", name=");
		builder.append(name);
		builder.append(", contentType=");
		builder.append(contentType);
		builder.append(", failureCause=");
		builder.append(failureCause);
		builder.append("]");
		return builder.toString();
	}
}
