package org.gcube.portlets.user.accountingdashboard.client.application.mainarea.filter;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.accountingdashboard.client.application.controller.Controller;
import org.gcube.portlets.user.accountingdashboard.client.application.event.ScopeDataEvent;
import org.gcube.portlets.user.accountingdashboard.shared.data.RequestReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ScopeData;

import com.google.gwt.i18n.client.DateTimeFormat;
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
public class FilterAreaPresenter extends PresenterWidget<FilterAreaPresenter.FilterAreaView>
		implements FilterAreaUiHandlers {

	private static Logger logger = Logger.getLogger("");

	interface FilterAreaView extends View, HasUiHandlers<FilterAreaPresenter> {

		void displayScopeData(ScopeData scopeData);

	}

	@SuppressWarnings("unused")
	private EventBus eventBus;
	private Controller controller;

	@Inject
	FilterAreaPresenter(EventBus eventBus, FilterAreaView view, Controller controller) {
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

		controller.getEventBus().addHandler(ScopeDataEvent.TYPE, new ScopeDataEvent.ScopeDataEventHandler() {

			@Override
			public void onData(ScopeDataEvent event) {
				logger.log(Level.FINE, "ScopeDataEvent received");
				getView().displayScopeData(event.getScopeData());

			}
		});

	}

	@Override
	protected void onBind() {
		super.onBind();
		controller.getTree();
	}

	@Override
	public void getReport(RequestReportData requestReportData) {
		if(checkDate(requestReportData.getDateFrom(),requestReportData.getDateTo())){
			controller.getReport(requestReportData);
		}
		
	}
	
	private boolean checkDate(String dateStart, String dateEnd) {
		DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
		
		Date dateStartD = null;
		try {
			logger.fine("DateTemp1: " + dateStart);
			dateStartD = dateTimeFormat.parse(dateStart);
			logger.fine("DateStart: " + dateStartD);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in start date: " + e.getLocalizedMessage(), e);
			return false;
		}

		
		Date dateEndD = null;
		try {
			logger.fine("DateTemp2: " + dateEnd);
			dateEndD = dateTimeFormat.parse(dateEnd);
			logger.fine("DateEnd: " + dateEndD);

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in end date: " + e.getLocalizedMessage(), e);
			return false;
		}

		if (dateStartD.compareTo(dateEndD) > 0) {
			controller.errorShow("Attention the start date must be less than the end date!");
			return false;
		} else {
			return true;
		}
	}

}
