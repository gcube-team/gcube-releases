package org.gcube.portlets.user.td.rulewidget.client;

import org.gcube.portlets.user.td.expressionwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.rulewidget.client.multicolumn.RuleOnTableNewWizard;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.wizardwidget.client.WizardListener;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RuleWidgetEntry implements EntryPoint {

	public void onModuleLoad() {
		callHello();
	}

	protected void test() {

		EventBus eventBus = new SimpleEventBus();
		// TRId trId=new TRId("86",TabResourceType.STANDARD, "1159");
		try {
			RuleOnTableNewWizard createRuleOnTableWizard = new RuleOnTableNewWizard(
					eventBus);
			createRuleOnTableWizard.addListener(new WizardListener() {
				
				@Override
				public void putInBackground() {
					Log.debug("Putted In Background");
					
				}
				
				@Override
				public void failed(String title, String message, String details,
						Throwable throwable) {
					Log.debug("Failed");
					
				}
				
				@Override
				public void completed(TRId id) {
					Log.debug("Completed");
					
				}
				
				@Override
				public void aborted() {
					Log.debug("Aborted");
				}
			});
			createRuleOnTableWizard.show();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		/*
		 * RemoveColumnDialog dialog=new RemoveColumnDialog(trId);
		 * dialog.show(); ChangeToAnnotationColumnDialog changeToAnnotation=new
		 * ChangeToAnnotationColumnDialog(trId); changeToAnnotation.show();
		 */
		/*
		 * ChangeColumnTypeDialog changeColumnType=new
		 * ChangeColumnTypeDialog(trId,eventBus); changeColumnType.show();
		 */

		/*
		 * ChangeColumnTypeProgressDialog c=new
		 * ChangeColumnTypeProgressDialog(eventBus);
		 * c.addProgressDialogListener(changeColumnType); c.show();
		 */

		/*
		 * DialogCodelistSelection dcs=new DialogCodelistSelection();
		 * dcs.show();
		 */

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
