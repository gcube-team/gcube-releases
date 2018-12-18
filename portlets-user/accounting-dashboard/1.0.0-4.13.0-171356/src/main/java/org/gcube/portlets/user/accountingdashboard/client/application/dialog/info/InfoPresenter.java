package org.gcube.portlets.user.accountingdashboard.client.application.dialog.info;

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

public class InfoPresenter extends PresenterWidget<InfoPresenter.InfoPresenterView>
		implements InfoUiHandlers {
	private static Logger logger = java.util.logging.Logger.getLogger("");
	
	interface InfoPresenterView extends PopupView, HasUiHandlers<InfoPresenter> {
		public void infoMessage(String info);
	}

	
	@Inject
	InfoPresenter(EventBus eventBus, InfoPresenterView view) {
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

	public void infoMessage(String error) {
		logger.log(Level.FINE,"Info message: " + error);
		getView().infoMessage(error);
	}

}
