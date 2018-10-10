package org.gcube.portlets.user.accountingdashboard.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface AppResources extends ClientBundle {

	interface uiDataCss extends CssResource {

		String uiDataMainPanel();

		String uiDataParagraphCentered();

		String uiDataFiltersPanel();

		String uiDataFiltersPeriodPanel();

		String uiDataFiltersExplorePanel();

		String uiDataFiltersTitle();

		String uiDataFiltersFormPanel();

		String uiDataFiltersControls();

		String uiDataExploreTree();

		String uiDataReportPanel();
		
		String uiDataReportTabPanel();

		String uiDataChartWrapper();

		String uiDataChartCanvas();

		String uiDataMonitorPopup();

		String uiDataMonitorPopupGlass();

		String uiDataMonitorPopupProgress();

		String uiDataPopup();

		String uiDataPopupGlass();

		String uiDataPopupCaption();

		String uiDataPopupHr();

		String uiDataIconSettings();

		String uiDataChartMenuPosition();

	}

	@Source("uiData.css")
	uiDataCss uiDataCss();

	@Source("Chart.bundle.js")
	TextResource chartJS();

	@Source("jspdf.min.js")
	TextResource jsPDF();

	@Source("hammer.min.js")
	TextResource hammerJS();

	@Source("chartjs-plugin-zoom.min.js")
	TextResource chartJSPluginZoom();

	@Source("settings.png")
	ImageResource settingsImage();

}
