/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client;

import org.gcube.portlets.user.tdtemplate.client.event.ExpressionDialogOpenedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.ExpressionDialogOpenedEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.FlowCreateEvent;
import org.gcube.portlets.user.tdtemplate.client.event.FlowCreateEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.SetColumnTypeCompletedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.SetColumnTypeCompletedEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.TemplateCompletedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.TemplateComplitedEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.TemplateSelectedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.TemplateSelectedEventHandler;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.flow.WindowFlowCreate;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 27, 2014
 *
 */
public class TdTemplatePrivateEventsBinder {

	private EventBus privateTaskBus = new SimpleEventBus();
	private TdTemplateControllerState templateControllerState;

	public TdTemplatePrivateEventsBinder(){
	}
	
	protected void bindEvents(TdTemplateControllerState controllerState){
		this.templateControllerState = controllerState;

		privateTaskBus.addHandler(TemplateSelectedEvent.TYPE, new TemplateSelectedEventHandler() {
			
			@Override
			public void onTemplateSelectedEvent(TemplateSelectedEvent event) {

				templateControllerState.doInitTemplate(event.getSwitcher());
			}

		});
		
		privateTaskBus.addHandler(TemplateCompletedEvent.TYPE, new TemplateComplitedEventHandler() {
			
			@Override
			public void onTemplateComplitedEvent(TemplateCompletedEvent templateValidEvent) {
				templateControllerState.getSubmitTool().setEnabled(templateValidEvent.isCompleted());
//				templateControllerState.activePostAction(templateValidEvent.isCompleted());
				
			}
			
		});
		
		privateTaskBus.addHandler(ExpressionDialogOpenedEvent.TYPE, new ExpressionDialogOpenedEventHandler() {
			
		

			@Override
			public void onExpressionDialogOpen(ExpressionDialogOpenedEvent expressionDialogEvent) {
				
				if(expressionDialogEvent!=null && expressionDialogEvent.getColumnIndex()!=-1){
//					int columId = templateControllerState.getExpressionDialogIndexUpdate();
//					columId = expressionDialogEvent.getColumnId();
//					GWT.log("Updated expression dialog open as index "+templateControllerState.getExpressionDialogIndexUpdate());
					
					int columIndex  = expressionDialogEvent.getColumnIndex();
					int expressionIndex = expressionDialogEvent.getExpressionIndex();

					TemplateRuleHandler updater = new TemplateRuleHandler(new TemplateIndexes(columIndex, expressionIndex), expressionDialogEvent.getExpressionDialogType());
					templateControllerState.setExpressionDialogIndexesUpdate(updater);
					GWT.log("Updated expression dialog open as column index "+columIndex +", expression index: "+expressionIndex);
				}
				
			}
		});
		
		privateTaskBus.addHandler(SetColumnTypeCompletedEvent.TYPE, new SetColumnTypeCompletedEventHandler() {
			
			@Override
			public void onSetTypeCompleted(SetColumnTypeCompletedEvent setColumnTypeCompletedEvent) {
				templateControllerState.doUpdateTemplate();
				
			}
		});
		
		
		privateTaskBus.addHandler(FlowCreateEvent.TYPE, new FlowCreateEventHandler() {
			
			@Override
			public void onFlowCreateEvent(FlowCreateEvent flowCreateEvent) {
				
				WindowFlowCreate win = WindowFlowCreate.geInstance();
				win.show();
			}
		});
	}
	
	/**
	 * @return the privateTaskBus
	 */
	public EventBus getPrivateTaskBus() {
		return privateTaskBus;
	}

}
