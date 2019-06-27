package org.gcube.portlets.user.accountingdashboard.client.application.dialog.monitor;

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

public class MonitorPresenter extends PresenterWidget<MonitorPresenter.MonitorPresenterView>
		implements MonitorUiHandlers {
	private static Logger logger = java.util.logging.Logger.getLogger("");
	
	interface MonitorPresenterView extends PopupView, HasUiHandlers<MonitorPresenter> {
		public void monitor(boolean enable);
	}

	
	@Inject
	MonitorPresenter(EventBus eventBus, MonitorPresenterView view) {
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

	public void enable(boolean enable) {
		logger.log(Level.FINE,"Monitor enable: " + enable);
		getView().monitor(enable);
	}

}
