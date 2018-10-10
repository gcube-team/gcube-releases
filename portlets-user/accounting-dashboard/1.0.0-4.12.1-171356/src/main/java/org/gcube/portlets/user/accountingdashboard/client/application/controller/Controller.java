package org.gcube.portlets.user.accountingdashboard.client.application.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.accountingdashboard.client.application.dialog.error.ErrorPresenter;
import org.gcube.portlets.user.accountingdashboard.client.application.dialog.info.InfoPresenter;
import org.gcube.portlets.user.accountingdashboard.client.application.dialog.monitor.MonitorPresenter;
import org.gcube.portlets.user.accountingdashboard.client.application.event.HelloEvent;
import org.gcube.portlets.user.accountingdashboard.client.application.event.ReportEvent;
import org.gcube.portlets.user.accountingdashboard.client.application.event.ScopeDataEvent;
import org.gcube.portlets.user.accountingdashboard.client.rpc.AccountingDashboardServiceAsync;
import org.gcube.portlets.user.accountingdashboard.shared.data.ReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.RequestReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ScopeData;
import org.gcube.portlets.user.accountingdashboard.shared.session.UserInfo;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class Controller {
	private static Logger logger = java.util.logging.Logger.getLogger("");

	private EventBus eventBus;
	private AccountingDashboardServiceAsync service;
	//private ApplicationCache cache;
	private MonitorPresenter monitorPresenter;
	private ErrorPresenter errorPresenter;
	private InfoPresenter infoPresenter;

	@Inject
	Controller(@Named("ControllerEventBus") EventBus eventBus, AccountingDashboardServiceAsync service,
			MonitorPresenter monitorPresenter, ErrorPresenter errorPresenter, InfoPresenter infoPresenter) {
		this.eventBus = eventBus;
		this.service = service;
		this.monitorPresenter = monitorPresenter;
		this.errorPresenter = errorPresenter;
		this.infoPresenter = infoPresenter;
		//this.cache = new ApplicationCache();
		
	}

	public void fireEvent(GwtEvent<?> event) {
		eventBus.fireEventFromSource(event, this);
	}

	public final <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, H handler) {
		return eventBus.addHandler(type, handler);
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void errorShow(String error){
		logger.log(Level.FINE, "Error show: "+error);
		errorPresenter.errorMessage(error);
	}
	
	public void infoShow(String info){
		logger.log(Level.FINE, "Info show: "+info);
		infoPresenter.infoMessage(info);
	}
	
	
	public void hello() {
		eventBus.fireEvent(new HelloEvent());
	}

	public void callHello() {
		/* String groupId= GCubeClientContext.getCurrentContextId(); */

		// String token = Window.Location.getParameter(Constants.TOKEN);
		// logger.log(Level.FINE,"Token: " + token);

		// MaterialLoader.showLoading(true);
		service.hello(new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.FINE, "Error in Hello(): ", caught);
				errorPresenter.errorMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(UserInfo result) {
				logger.log(Level.FINE, "Hello: " + result.getUsername());
			}

		});

	}

	public void getTree() {
		service.getScopeData(new AsyncCallback<ScopeData>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.FINE, "Error in getTree(): ", caught);
				errorPresenter.errorMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(ScopeData scopeData) {
				logger.log(Level.FINE, "ScopeData: " + scopeData);
				ScopeDataEvent event = new ScopeDataEvent(scopeData);
				eventBus.fireEvent(event);
			}

		});

	}

	public void getReport(RequestReportData requestReportData) {
		monitorPresenter.enable(true);
		service.getReport(requestReportData, new AsyncCallback<ReportData>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.FINE, "Error in getReport(): ", caught);
				monitorPresenter.enable(false);
				errorPresenter.errorMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(ReportData reportData) {
				logger.log(Level.FINE, "ReportData: " + reportData);
				monitorPresenter.enable(false);
				ReportEvent event = new ReportEvent(reportData);
				eventBus.fireEvent(event);
			}

		});

	}

}
