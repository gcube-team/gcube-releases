package org.gcube.portlets.admin.accountingmanager.client.event;

import org.gcube.portlets.admin.accountingmanager.client.type.FiltersChangeType;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class FiltersChangeEvent extends
		GwtEvent<FiltersChangeEvent.FiltersChangeEventHandler> {

	public static Type<FiltersChangeEventHandler> TYPE = new Type<FiltersChangeEventHandler>();
	private FiltersChangeType filtersChangeType;
	private SeriesRequest seriesRequest;
	
	public interface FiltersChangeEventHandler extends EventHandler {
		void onFiltersChange(FiltersChangeEvent event);
	}

	public interface HasFiltersChangeEventHandler extends HasHandlers {
		public HandlerRegistration addFiltersChangeEventHandler(
				FiltersChangeEventHandler handler);
	}

	public FiltersChangeEvent(FiltersChangeType filtersChangeType, SeriesRequest seriesRequest) {
		this.filtersChangeType = filtersChangeType;
		this.seriesRequest=seriesRequest;
		
	}

	@Override
	protected void dispatch(FiltersChangeEventHandler handler) {
		handler.onFiltersChange(this);
	}

	@Override
	public Type<FiltersChangeEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<FiltersChangeEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			FiltersChangeEvent filtersChangeEvent) {
		source.fireEvent(filtersChangeEvent);
	}

	public FiltersChangeType getFiltersChangeType() {
		return filtersChangeType;
	}

	public SeriesRequest getSeriesRequest() {
		return seriesRequest;
	}

	@Override
	public String toString() {
		return "FiltersChangeEvent [filtersChangeType=" + filtersChangeType
				+ ", seriesRequest=" + seriesRequest + "]";
	}

	
	
}
