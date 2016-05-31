package org.gcube.portlets.admin.accountingmanager.client.maindata;

import org.gcube.portlets.admin.accountingmanager.client.event.StateChangeEvent;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.AccountingChart4Job;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.AccountingChart4Portlet;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.AccountingChart4Service;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.AccountingChart4Storage;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.AccountingChart4Task;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.AccountingChartBuilder;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.AccountingChartDirector;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.AccountingChartPanel;
import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerChartDrawException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.FramedPanel;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ChartViewerPanel extends FramedPanel {

	private EventBus eventBus;

	public ChartViewerPanel(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		// msgs = GWT.create(ServiceCategoryMessages.class);
		init();
		create();
		bindToEvents();
	}

	protected void init() {
		forceLayoutOnResize = true;
		setBodyBorder(false);
		setBorders(false);
		setHeaderVisible(false);
		setResize(true);

	}

	private void create() {

	}

	// Bind to Events
	private void bindToEvents() {
		eventBus.addHandler(StateChangeEvent.TYPE,
				new StateChangeEvent.StateChangeEventHandler() {

					@Override
					public void onStateChange(StateChangeEvent event) {
						Log.debug("Catch Event State Change");
						doStateChangeCommand(event);

					}
				});
	}

	private void doStateChangeCommand(StateChangeEvent event) {
		if (event.getStateChangeType() == null) {
			return;
		}
		switch (event.getStateChangeType()) {
		case Restore:
		case Update:
			onStateChange(event);

			break;
		default:
			break;

		}

	}

	private void onStateChange(StateChangeEvent event) {
		if (event.getAccountingStateData() == null
				|| event.getAccountingStateData().getAccountingType() == null) {
			return;
		}

		switch (event.getAccountingStateData().getAccountingType()) {
		case JOB:
			createChart(new AccountingChart4Job(event.getAccountingStateData()));
			break;
		case PORTLET:
			createChart(new AccountingChart4Portlet(event.getAccountingStateData()));
			break;
		case SERVICE:
			createChart(new AccountingChart4Service(event.getAccountingStateData()));
			break;
		case STORAGE:
			createChart(new AccountingChart4Storage(event.getAccountingStateData()));
			break;
		case TASK:
			createChart(new AccountingChart4Task(event.getAccountingStateData()));
			break;
		default:
			break;
		}

	}

	private void createChart(AccountingChartBuilder chartBuilder) {
		clear();
		try {
			AccountingChartDirector director = new AccountingChartDirector();
			director.setAccountingChartBuilder(chartBuilder);
			director.constructAccountingChart();

			AccountingChartPanel chart = director.getAccountingChart();

			if (chart != null) {
				add(chart.getChart());
			}

		} catch (AccountingManagerChartDrawException e) {
			Log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		forceLayout();

	}

}
