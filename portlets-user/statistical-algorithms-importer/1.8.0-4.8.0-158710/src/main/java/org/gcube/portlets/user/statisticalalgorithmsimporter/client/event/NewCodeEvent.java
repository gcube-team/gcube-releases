package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code.CodeContentType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Main Code Set Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class NewCodeEvent extends GwtEvent<NewCodeEvent.NewCodeEventHandler> {

	public static Type<NewCodeEventHandler> TYPE = new Type<NewCodeEventHandler>();
	private CodeContentType codeContentType;
	private ItemDescription file;
	private String code;

	public interface NewCodeEventHandler extends EventHandler {
		void onSet(NewCodeEvent event);
	}

	public interface HasSaveNewCodeEventHandler extends HasHandlers {
		public HandlerRegistration addSaveNewCodeEventHandler(NewCodeEventHandler handler);
	}

	public NewCodeEvent(CodeContentType codeContentType, ItemDescription file, String code) {
		this.codeContentType = codeContentType;
		this.file = file;
		this.code = code;
	}

	@Override
	protected void dispatch(NewCodeEventHandler handler) {
		handler.onSet(this);
	}

	@Override
	public Type<NewCodeEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<NewCodeEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, NewCodeEvent saveNewMainCodeEvent) {
		source.fireEvent(saveNewMainCodeEvent);
	}

	public CodeContentType getCodeType() {
		return codeContentType;
	}

	public ItemDescription getFile() {
		return file;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "NewCodeEvent [codeContentType=" + codeContentType + ", file=" + file + ", code=" + code + "]";
	}

	

}
