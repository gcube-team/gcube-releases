package org.gcube.portlets.user.td.widgetcommonevent.client.event;

import org.gcube.portlets.user.td.widgetcommonevent.client.type.UIStateType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.DataViewType;

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
public class UIStateEvent extends GwtEvent<UIStateEvent.UIStateHandler> {

	public static Type<UIStateHandler> TYPE = new Type<UIStateHandler>();
	private UIStateType uiStateType;
	private TRId trId;
	private DataViewType dataViewType;

	public interface UIStateHandler extends EventHandler {
		void onUIState(UIStateEvent event);
	}

	public interface HasUIStateHandler extends HasHandlers {
		public HandlerRegistration addUIStateHandler(UIStateHandler handler);
	}

	public UIStateEvent(UIStateType uiStateType) {
		this.uiStateType = uiStateType;
		this.trId=null;
		this.dataViewType=DataViewType.GRID;
	}
	
	public UIStateEvent(UIStateType uiStateType, TRId trId, DataViewType dataViewType) {
		this.uiStateType = uiStateType;
		this.trId=trId;
		this.dataViewType=dataViewType;
	}

	public UIStateType getUIStateType() {
		return uiStateType;
	}

	@Override
	protected void dispatch(UIStateHandler handler) {
		handler.onUIState(this);
	}

	@Override
	public Type<UIStateHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<UIStateHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, UIStateEvent uiStateEvent) {
		source.fireEvent(uiStateEvent);
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public DataViewType getDataViewType() {
		return dataViewType;
	}

	public void setDataViewType(DataViewType dataViewType) {
		this.dataViewType = dataViewType;
	}

	@Override
	public String toString() {
		return "UIStateEvent [uiStateType=" + uiStateType + ", trId=" + trId
				+ ", dataViewType=" + dataViewType + "]";
	}

}
