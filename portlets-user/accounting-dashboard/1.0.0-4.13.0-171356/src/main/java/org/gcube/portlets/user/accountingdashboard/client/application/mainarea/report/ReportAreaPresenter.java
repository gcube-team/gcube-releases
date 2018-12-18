package org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.accountingdashboard.client.application.controller.Controller;
import org.gcube.portlets.user.accountingdashboard.client.application.event.ReportEvent;
import org.gcube.portlets.user.accountingdashboard.shared.data.ReportData;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ReportAreaPresenter extends PresenterWidget<ReportAreaPresenter.ReportAreaView>
		implements ReportAreaUiHandlers {

	private static Logger logger = Logger.getLogger("");

	interface ReportAreaView extends View, HasUiHandlers<ReportAreaPresenter> {
		void displayReportData(ReportData reportData);
		
	}

	@SuppressWarnings("unused")
	private EventBus eventBus;
	private Controller controller;

	@Inject
	ReportAreaPresenter(EventBus eventBus, ReportAreaView view, Controller controller) {
		super(eventBus, view);
		this.eventBus = eventBus;
		this.controller = controller;
		getView().setUiHandlers(this);
		addProviders();
		bindToEvent();
	}

	private void addProviders() {

	}

	private void bindToEvent() {

		controller.getEventBus().addHandler(ReportEvent.TYPE, new ReportEvent.ReportEventHandler() {

			@Override
			public void onData(ReportEvent event) {
				logger.log(Level.FINE, "ReportEvent received");
				getView().displayReportData(event.getReportData());

			}
		});

	}

	@Override
	protected void onBind() {
		super.onBind();

	}

}
