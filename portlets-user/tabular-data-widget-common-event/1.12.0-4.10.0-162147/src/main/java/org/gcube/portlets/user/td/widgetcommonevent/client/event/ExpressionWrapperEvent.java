package org.gcube.portlets.user.td.widgetcommonevent.client.event;

import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class ExpressionWrapperEvent extends GwtEvent<ExpressionWrapperEvent.ExpressionWrapperEventHandler> {

	public static Type<ExpressionWrapperEventHandler> TYPE = new Type<ExpressionWrapperEventHandler>();
	private ExpressionWrapper expressionWrapper;
	
	
	public interface ExpressionWrapperEventHandler extends EventHandler {
		void onExpression(ExpressionWrapperEvent event);	
	}

	public interface HasExpressionWrapperEventHandler extends HasHandlers{
		public HandlerRegistration addExpressionWrapperEventHandler(ExpressionWrapperEventHandler handler);
	
		public void removeExpressionWrapperEventHandler(ExpressionWrapperEventHandler handler);

	
	}
	
	public ExpressionWrapperEvent(ExpressionWrapper expressionWrapper) {
		this.expressionWrapper = expressionWrapper;
	}

	
	@Override
	protected void dispatch(ExpressionWrapperEventHandler handler) {
		handler.onExpression(this);
	}

	
	
	@Override
	public Type<ExpressionWrapperEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ExpressionWrapperEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, ExpressionWrapper expressionWrapper) {
		source.fireEvent(new ExpressionWrapperEvent(expressionWrapper));
	}

	
	
	public ExpressionWrapper getExpressionWrapper() {
		return expressionWrapper;
	}


	public void setExpressionWrapper(ExpressionWrapper expressionWrapper) {
		this.expressionWrapper = expressionWrapper;
	}


	@Override
	public String toString() {
		return "ExpressionWrapperEvent [expressionWrapper=" + expressionWrapper
				+ "]";
	}


	


}
