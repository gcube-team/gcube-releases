package org.gcube.portlets.admin.accountingmanager.client.filters;

import org.gcube.portlets.admin.accountingmanager.client.event.FiltersChangeEvent;
import org.gcube.portlets.admin.accountingmanager.client.resource.AccountingManagerResources;
import org.gcube.portlets.admin.accountingmanager.client.type.FiltersChangeType;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingFilterDefinition;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingPeriod;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class FiltersPanel extends FramedPanel {
	private EventBus eventBus;
	private AccountingPeriodPanel accountPeriodPanel;
	private TextButton updateCharts;
	private ActiveFiltersPanel activeFiltersPanel;

	public FiltersPanel(EventBus eventBus) {
		super();
		Log.debug("FiltersPanel");
		this.eventBus = eventBus;
		// msgs = GWT.create(ServiceCategoryMessages.class);
		init();
		create();

	}

	protected void init() {
		forceLayoutOnResize = true;
		setBodyBorder(false);
		setBorders(false);
		setHeaderVisible(false);

	}

	protected void create() {
		accountPeriodPanel = new AccountingPeriodPanel(eventBus);
		activeFiltersPanel = new ActiveFiltersPanel(eventBus);

		updateCharts = new TextButton("Update Chart");
		updateCharts.setIcon(AccountingManagerResources.INSTANCE
				.accountingReload24());
		updateCharts.setIconAlign(IconAlign.RIGHT);
		updateCharts.setToolTip("Update Chart");

		updateCharts.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				updateChart();

			}
		});

		HBoxLayoutContainer hBox = new HBoxLayoutContainer();
		hBox.setPack(BoxLayoutPack.START);
		hBox.add(updateCharts, new BoxLayoutData(new Margins(2, 5, 2, 5)));

		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.setScrollMode(ScrollMode.AUTO);
		vlc.add(accountPeriodPanel, new VerticalLayoutData(1, -1, new Margins(
				4, 0, 2, 4)));
		vlc.add(activeFiltersPanel, new VerticalLayoutData(1, 1, new Margins(4,
				0, 2, 4)));

		vlc.add(hBox, new VerticalLayoutData(1, -1, new Margins(0)));

		add(vlc);
	}

	protected void updateChart() {
		AccountingPeriod accountingPeriod = accountPeriodPanel
				.getAccountingPeriod();
		AccountingFilterDefinition accountingFilterDefinition = activeFiltersPanel
				.getActiveFilters();

		if (accountingPeriod != null) {
			SeriesRequest seriesRequest = new SeriesRequest(accountingPeriod,
					accountingFilterDefinition);
			Log.debug("UpdateChart: " + seriesRequest);

			FiltersChangeEvent filtersChangeEvent = new FiltersChangeEvent(
					FiltersChangeType.Update, seriesRequest);
			eventBus.fireEvent(filtersChangeEvent);
		}

	}

}
