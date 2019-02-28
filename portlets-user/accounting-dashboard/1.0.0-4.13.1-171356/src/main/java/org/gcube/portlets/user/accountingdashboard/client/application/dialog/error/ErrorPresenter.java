package org.gcube.portlets.user.accountingdashboard.client.application.dialog.error;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */

public class ErrorPresenter extends PresenterWidget<ErrorPresenter.ErrorPresenterView>
		implements ErrorUiHandlers {
	private static Logger logger = java.util.logging.Logger.getLogger("");
	
	interface ErrorPresenterView extends PopupView, HasUiHandlers<ErrorPresenter> {
		public void errorMessage(String error);
	}

	
	@Inject
	ErrorPresenter(EventBus eventBus, ErrorPresenterView view) {
		super(eventBus, view);
	
		getView().setUiHandlers(this);
		bindToEvent();
	}

	@Override
	public void onBind() {
		super.onBind();
	}

	private void bindToEvent() {
		/*
		controller.getEventBus().addHandler(MonitorEvent.TYPE, new MonitorEvent.MonitorEventHandler() {

			@Override
			public void onMonitor(MonitorEvent event) {

			}
		});
			*/
	}

	public void errorMessage(String error) {
		logger.log(Level.FINE,"Error message: " + error);
		getView().errorMessage(error);
	}

}
