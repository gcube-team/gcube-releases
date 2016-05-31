package org.gcube.portlets.user.td.columnwidget.client;



import org.gcube.portlets.user.td.columnwidget.client.create.AddColumnPanel;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TabResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ColumnWidgetEntry  implements EntryPoint  {

	
	public void onModuleLoad() {
		callHello();
	}
	
	protected void test(){

		EventBus eventBus= new SimpleEventBus();
		TRId trId=new TRId("86",TabResourceType.STANDARD, "1159");
		
		
		/*
		RemoveColumnDialog dialog=new RemoveColumnDialog(trId);
		dialog.show();
		ChangeToAnnotationColumnDialog changeToAnnotation=new ChangeToAnnotationColumnDialog(trId);
		changeToAnnotation.show();
		*/
		/*ChangeColumnTypeDialog changeColumnType=new ChangeColumnTypeDialog(trId,eventBus);
		changeColumnType.show();
		*/
		
		/*ChangeColumnTypeProgressDialog c=new ChangeColumnTypeProgressDialog(eventBus);
		c.addProgressDialogListener(changeColumnType);
		c.show();
		*/
		
		/*DialogCodelistSelection dcs=new  DialogCodelistSelection();
		dcs.show();
		*/
		
		//AddColumnPanel
		AddColumnPanel addColumnPanel= new AddColumnPanel(trId, eventBus);
		RootPanel.get().add(addColumnPanel);
		
		Log.info("Hello!");

	}
	
	
	protected void callHello() {
		TDGWTServiceAsync.INSTANCE.hello(new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.info("No valid user found: " + caught.getMessage());
				caught.printStackTrace();
				if (caught instanceof TDGWTSessionExpiredException) {
					UtilsGXT3.alert("Error", "Expired Session");
					
				} else {
					UtilsGXT3.alert("Error", "No user found");
				}
			}

			@Override
			public void onSuccess(UserInfo result) {
				Log.info("Hello: " + result.getUsername());
				test();
			}

		});

	}
	
}
