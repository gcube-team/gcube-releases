/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external;

import org.gcube.portlets.user.td.widgetcommonevent.client.event.ExpressionWrapperEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.C_ExpressionContainer;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 11, 2014
 *
 */
public abstract class FilterColumnEvent {
	
	/**
	 * 
	 */
	public FilterColumnEvent(EventBus bus) {

		bus.addHandler(ExpressionWrapperEvent.TYPE, new ExpressionWrapperEvent.ExpressionWrapperEventHandler() {
			
			@Override
			public void onExpression(ExpressionWrapperEvent event) {
				ExpressionWrapper exWrapper=event.getExpressionWrapper();
				
				System.out.println("ExWrapper :" +exWrapper);
				
				if(exWrapper!=null && exWrapper.getConditionExpressionContainer()!=null){

					C_ExpressionContainer rule = exWrapper.getConditionExpressionContainer();
					
					if(rule!=null && rule.getExp()!=null){
						updateDescription(event);
					}
				}
				else
					GWT.log("Expression dialog closed without a valid expression");
			}
		});
//	
		
	}

	/**
	 * @param event
	 */
	public abstract void updateDescription(ExpressionWrapperEvent event);
}
