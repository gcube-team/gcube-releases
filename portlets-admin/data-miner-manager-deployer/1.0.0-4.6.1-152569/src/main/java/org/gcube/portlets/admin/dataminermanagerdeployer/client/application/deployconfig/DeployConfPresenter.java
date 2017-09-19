package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.deployconfig;

import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.ApplicationPresenter;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.monitor.MonitorRequest;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.monitor.MonitorRequestEvent;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.monitor.MonitorRequestEvent.MonitorRequestEventHandler;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.place.NameTokens;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.rpc.DataMinerDeployerServiceAsync;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.Constants;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.config.DMDeployConfig;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

import gwt.material.design.client.ui.MaterialLoader;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class DeployConfPresenter extends
		Presenter<DeployConfPresenter.PresenterView, DeployConfPresenter.PresenterProxy> implements DeployConfUiHandlers {
	interface PresenterView extends View, HasUiHandlers<DeployConfUiHandlers> {
		void setResult(String result, boolean success);

	}

	@ProxyStandard
	@NameToken(NameTokens.DEPLOY)
	@NoGatekeeper
	interface PresenterProxy extends ProxyPlace<DeployConfPresenter> {
	}

	private DataMinerDeployerServiceAsync service;

	@Inject
	DeployConfPresenter(EventBus eventBus, PresenterView view, PresenterProxy proxy,
			DataMinerDeployerServiceAsync service) {
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
		this.service = service;
		getView().setUiHandlers(this);

	}

	@Override
	public void executeDeploy(DMDeployConfig dmDeployConfig) {
		deploy(dmDeployConfig);

	}

	private void deploy(DMDeployConfig dmConfig) {
		MaterialLoader.showLoading(true);
		service.startDeploy(getToken(), dmConfig, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error starting deploy: " + caught.getLocalizedMessage(), caught);
				String result = SafeHtmlUtils.htmlEscape(caught.getLocalizedMessage());
				StackTraceElement[] trace = caught.getStackTrace();
				for (int i = 0; i < trace.length; i++) {
					result += SafeHtmlUtils.htmlEscape(trace[i].toString()) + "\n";
				}
				MaterialLoader.showLoading(false);
				getView().setResult(result, false);
			}

			@Override
			public void onSuccess(String operationId) {
				monitorDeploy(operationId);

			}

		});

	}

	private void monitorDeploy(String operationId) {
		final MonitorRequest monitorRequest = new MonitorRequest();
		MonitorRequestEventHandler handler = new MonitorRequestEventHandler() {

			@Override
			public void onMonitor(MonitorRequestEvent event) {
				service.monitorDeploy(getToken(), operationId, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						monitorRequest.stop();
						GWT.log("Error in deploy: " + caught.getLocalizedMessage(), caught);
						String result = SafeHtmlUtils.htmlEscape(caught.getLocalizedMessage());
						StackTraceElement[] trace = caught.getStackTrace();
						for (int i = 0; i < trace.length; i++) {
							result += SafeHtmlUtils.htmlEscape(trace[i].toString()) + "\n";
						}
						MaterialLoader.showLoading(false);
						getView().setResult(result, false);
					}

					
			
					
					@Override
					public void onSuccess(String status) {
						if (status != null && !status.isEmpty()) {
							switch (status) {
							case "COMPLETED":
								MaterialLoader.showLoading(false);
								getView().setResult("Deploy Success!", true);								
								break;
							case "FAILED":
								retrieveError(operationId, monitorRequest);
								
								break;
							case "IN PROGRESS":
							default:
								monitorRequest.repeat();
								break;

							}
						} else {
							monitorRequest.repeat();

						}

					}

				});
			}

		};

		monitorRequest.addHandler(handler);
		monitorRequest.start();

	}

	private void retrieveError(String operationId,final MonitorRequest monitorRequest) {
		service.retrieveError(getToken(),operationId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				monitorRequest.stop();
				GWT.log("Error retrieving deploy error logs: " + caught.getLocalizedMessage(), caught);
				String result = SafeHtmlUtils.htmlEscape(caught.getLocalizedMessage());
				StackTraceElement[] trace = caught.getStackTrace();
				for (int i = 0; i < trace.length; i++) {
					result += SafeHtmlUtils.htmlEscape(trace[i].toString()) + "\n";
				}
				MaterialLoader.showLoading(false);
				getView().setResult(result, false);
			}

			@Override
			public void onSuccess(String result) {
				monitorRequest.stop();
				GWT.log("Error, deploy failed");
				MaterialLoader.showLoading(false);
				getView().setResult("Error in deploy. "+result, false);
			}

		});

	}

	private String getToken() {
		String token = Window.Location.getParameter(Constants.TOKEN);
		GWT.log("Token: " + token);
		return token;
	}

}