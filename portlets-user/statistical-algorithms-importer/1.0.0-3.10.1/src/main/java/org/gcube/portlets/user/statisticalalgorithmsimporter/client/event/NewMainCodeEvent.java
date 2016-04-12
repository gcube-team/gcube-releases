package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Main Code Set Event
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class NewMainCodeEvent extends
		GwtEvent<NewMainCodeEvent.NewMainCodeEventHandler> {

	public static Type<NewMainCodeEventHandler> TYPE = new Type<NewMainCodeEventHandler>();
	private ItemDescription file;
	private String code;

	public interface NewMainCodeEventHandler extends EventHandler {
		void onSet(NewMainCodeEvent event);
	}

	public interface HasSaveNewMainCodeEventHandler extends HasHandlers {
		public HandlerRegistration addSaveNewMainCodeEventHandler(
				NewMainCodeEventHandler handler);
	}

	public NewMainCodeEvent(ItemDescription file, String code) {
		this.file = file;
		this.code = code;
	}

	@Override
	protected void dispatch(NewMainCodeEventHandler handler) {
		handler.onSet(this);
	}

	@Override
	public Type<NewMainCodeEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<NewMainCodeEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			NewMainCodeEvent saveNewMainCodeEvent) {
		source.fireEvent(saveNewMainCodeEvent);
	}

	public ItemDescription getFile() {
		return file;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "NewMainCodeEvent [file=" + file + ", code=" + code + "]";
	}

	

}
