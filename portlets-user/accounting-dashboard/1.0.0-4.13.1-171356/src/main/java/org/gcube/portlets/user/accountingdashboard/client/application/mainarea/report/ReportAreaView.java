package org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart;
import org.gcube.portlets.user.accountingdashboard.client.resources.AppResources;
import org.gcube.portlets.user.accountingdashboard.shared.data.ReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ReportElementData;

import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ReportAreaView extends ViewWithUiHandlers<ReportAreaPresenter>
		implements ReportAreaPresenter.ReportAreaView {

	private static Logger logger = java.util.logging.Logger.getLogger("");

	interface Binder extends UiBinder<Widget, ReportAreaView> {
	}

	@UiField
	HTMLPanel reportPanel;

	private HashMap<String, ArrayList<Chart>> categories;

	private AppResources resources;

	@Inject
	ReportAreaView(Binder uiBinder, AppResources resources) {
		this.resources = resources;
		init();
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void init() {

	}

	@Override
	public void displayReportData(ReportData reportData) {
		if (reportData == null) {
			reportPanel.clear();
		} else {
			reportPanel.clear();
			
			/*HTMLPanel tabContent = new HTMLPanel("");
			for (int i = 0; i < reportData.getElements().size(); i++) {
				ReportElementData reportElementData = reportData.getElements().get(i);
				Chart chart = new Chart(resources, "report_" + i, reportElementData);
				tabContent.add(chart);
			}
			reportPanel.add(tabContent);
			*/
			
			categories = new HashMap<>();
			for (int i = 0; i < reportData.getElements().size(); i++) {
				ReportElementData reportElementData = reportData.getElements().get(i);
				String key = reportElementData.getCategory();
				ArrayList<Chart> category;
				if (categories.containsKey(key)) {
					category = categories.get(reportElementData.getCategory());
				} else {
					category = new ArrayList<>();
				}
				Chart chart = new Chart(resources, "report_" + i, reportElementData);
				category.add(chart);
				categories.put(key, category);
			}

			TabPanel tabPanel = new TabPanel();
			tabPanel.addStyleName(resources.uiDataCss().uiDataReportTabPanel());
			boolean first = true;
			for (String category : categories.keySet()) {
				Tab tab = new Tab();
				tab.setHeading(category);
				HTMLPanel tabContent = new HTMLPanel("");
				for (Chart chart : categories.get(category)) {
					tabContent.add(chart);
				}
				if (first) {
					tab.setActive(true);
					first = false;
				}
				tab.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						logger.fine("ClickEvent: " + event.getSource().getClass());
						IconAnchor iconAnchor = (IconAnchor) event.getSource();
						String category = iconAnchor.getText();
						if (category != null) {
							category = category.trim();
						}
						logger.fine("Category found: " + category);
						ArrayList<Chart> chartsInCategory = categories.get(category);
						// logger.fine("Charts List:"+chartsInCategory);
						if (chartsInCategory != null) {
							for (Chart chart : chartsInCategory) {
								chart.forceLayout();
							}
						}
					}
				});

				tab.add(tabContent);
				tabPanel.add(tab);
			}

			reportPanel.add(tabPanel);
			
		}

	}

}
