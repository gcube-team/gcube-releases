package org.gcube.portlets.user.td.resourceswidget.client;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.resourceswidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ResourcesWidgetEntry implements EntryPoint {

	// private ResourceTDDescriptor resourceTDDescriptor;
	private CommonMessages msgsCommon;
	private ResourcesMessages msgs;
	
	private SimpleEventBus eventBus;
	private TRId trId;
	private TabResource tabResource;

	public void onModuleLoad() {
		Log.info("Hello!");
		initMessages();
		eventBus = new SimpleEventBus();
		trId = new TRId();
		// TabularResource: [ id=202, type=STANDARD, lastTable=[ id=4901,
		// type=Generic]]
		// TabularResource: [ id=68, type=STANDARD, lastTable=[ id=4884,
		// type=Dataset]]

		trId.setId("68");
		trId.setTableType("STANDARD");
		trId.setTableId("4884");
		retrieveUserInfo();
	}
	
	protected void initMessages(){
		msgs = GWT.create(ResourcesMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	private void start() {
		try {
			// ResourcesDialog dialog=new ResourcesDialog(trId,eventBus);
			// dialog.show();

			/*
			 * resourceTDDescriptor=new ResourceTDDescriptor();
			 * resourceTDDescriptor.setName("Test Chart"); ChartViewerDialog
			 * chartDialog=new ChartViewerDialog(resourceTDDescriptor, trId,
			 * eventBus, true); chartDialog.show();
			 */

			ResourcesListViewDialog resourcesListViewDialog = new ResourcesListViewDialog(
					eventBus);
			resourcesListViewDialog.show();
			resourcesListViewDialog.open(trId);
		} catch (Throwable e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	protected void retrieveUserInfo() {
		TDGWTServiceAsync.INSTANCE.hello(new AsyncCallback<UserInfo>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(
							SessionExpiredType.EXPIREDONSERVER));
				} else {

					Log.error("Error Retrieving User Info: "
							+ caught.getLocalizedMessage());
					UtilsGXT3.alert(msgsCommon.error(), msgs.errorRetrievingUserInfo());

				}
			}

			public void onSuccess(UserInfo userInfo) {
				Log.debug("User Info: " + userInfo);
				getTabularResource();
			}

		});
	}

	protected void getTabularResource() {
		TDGWTServiceAsync.INSTANCE.getTabResourceInformation(trId,
				new AsyncCallback<TabResource>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {

							Log.error("Error get TR information: "
									+ caught.getLocalizedMessage());
							UtilsGXT3
									.alert(msgsCommon.error(), msgs.errorGetTRInformation());

						}

					}

					@Override
					public void onSuccess(TabResource result) {
						Log.debug("TabResource:" + result);
						tabResource = result;
						setCurrentTabularResource();

					}
				});
	}

	protected void setCurrentTabularResource() {
		TDGWTServiceAsync.INSTANCE.setTabResource(tabResource,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {

							Log.error("Error setting Active TR: "
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert(msgsCommon.error(), msgs.errorSettingActiveTR());

						}

					}

					@Override
					public void onSuccess(Void result) {
						Log.debug("Tab Resource set");
						start();
					}
				});

	}

}
