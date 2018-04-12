/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class DataLoadEvent extends GwtEvent<DataLoadHandler> {

	/**
	 * Handler type.
	 */
	private static final Type<DataLoadHandler> TYPE = new Type<DataLoadHandler>();

	/**
	 * Fires a data load event on all registered handlers in the handler
	 * manager.
	 * @param source the source of the handlers
	 * @param caught the load error.
	 */
	public static void fireLoadDataFailed(HasDataLoadHandlers source, Throwable caught) {
		DataLoadEvent event = new DataLoadEvent(true, caught);
		source.fireEvent(event);
	}

	/**
	 * Fires a data load event on all registered handlers in the handler
	 * manager.
	 * @param source the source of the handlers
	 */
	public static void fireLoadDataSuccess(HasDataLoadHandlers source) {
		DataLoadEvent event = new DataLoadEvent(false, null);
		source.fireEvent(event);
	}

	/**
	 * Gets the type associated with this event.
	 * 
	 * @return returns the handler type
	 */
	public static Type<DataLoadHandler> getType() {
		return TYPE;
	}

	protected boolean failed;
	protected Throwable caught;

	/**
	 * @param failed
	 * @param caught
	 */
	public DataLoadEvent(boolean failed, Throwable caught) {
		this.failed = failed;
		this.caught = caught;
	}

	/**
	 * @return the failed
	 */
	public boolean isFailed() {
		return failed;
	}

	/**
	 * @return the caught
	 */
	public Throwable getCaught() {
		return caught;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispatch(DataLoadHandler handler) {
		handler.onDataLoad(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<DataLoadHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toDebugString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataLoadEvent [failed=");
		builder.append(failed);
		builder.append(", caught=");
		builder.append(caught);
		builder.append("]");
		return builder.toString();
	}
}
