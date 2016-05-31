package org.gcube.portlets.admin.accountingmanager.client.maindata.charts.service;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.TimeUnitMeasure;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.ChartTimeMeasure;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.DownloadConstants;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.ServiceChartMeasure;
import org.gcube.portlets.admin.accountingmanager.client.resource.AccountingManagerResources;
import org.gcube.portlets.admin.accountingmanager.client.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesService;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceTop;

import com.allen_sauer.gwt.log.client.Log;
import com.github.highcharts4gwt.client.view.widget.HighchartsLayoutPanel;
import com.github.highcharts4gwt.model.array.api.ArrayNumber;
import com.github.highcharts4gwt.model.array.api.ArrayString;
import com.github.highcharts4gwt.model.factory.api.HighchartsOptionFactory;
import com.github.highcharts4gwt.model.factory.jso.JsoHighchartsOptionFactory;
import com.github.highcharts4gwt.model.highcharts.option.api.ChartOptions;
import com.github.highcharts4gwt.model.highcharts.option.api.SeriesColumn;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonArrowAlign;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ServiceChartTopPanel extends SimpleContainer {

	private static final String TIME_UNIT = "Time Unit";
	// private static final String MEASURE_TYPE = "Measure";

	private DateTimeFormat dtf = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH_DAY);

	private AccountingStateData accountingStateData;
	private HighchartsLayoutPanel highchartsLayoutPanel;

	// Download Menu
	private MenuItem downloadPNGItem;
	private MenuItem downloadJPGItem;
	private MenuItem downloadPDFItem;
	private MenuItem downloadSVGItem;

	// Measure Menu
	private MenuItem operationCountItem;
	private MenuItem durationItem;
	private MenuItem maxInvocationTimeItem;
	private MenuItem minInvocationTimeItem;

	// Time Unit Menu
	private MenuItem msItem;
	private MenuItem sItem;
	private MenuItem mItem;
	private MenuItem hItem;

	private long unitMeasure = TimeUnitMeasure.getMilliseconds();
	private String unitMeasureLabel = TimeUnitMeasure.MS;
	private TextButton unitButton;

	private ChartOptions options;

	private ServiceChartMeasure measure = ServiceChartMeasure.OperationCount;
	private TextButton measureButton;

	private VerticalLayoutContainer vert;

	public ServiceChartTopPanel(AccountingStateData accountingStateData) {
		this.accountingStateData = accountingStateData;
		forceLayoutOnResize = true;
		create();

	}

	private void create() {
		ToolBar toolBar = new ToolBar();
		toolBar.setSpacing(2);
		// Download
		final TextButton downloadButton = new TextButton(
				DownloadConstants.DOWNLOAD,
				AccountingManagerResources.INSTANCE.accountingDownload24());
		// downloadButton.setScale(ButtonScale.MEDIUM);
		downloadButton.setIconAlign(IconAlign.RIGHT);
		downloadButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		downloadButton.setMenu(createDownloadMenu());

		// Measure
		measureButton = new TextButton(ServiceChartMeasure.OperationCount.getLabel(),
				AccountingManagerResources.INSTANCE.accountingByte24());
		measureButton.setIconAlign(IconAlign.RIGHT);
		measureButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		measureButton.setMenu(createMeasureMenu());

		// Unit
		unitButton = new TextButton(TIME_UNIT,
				AccountingManagerResources.INSTANCE.accountingUnitms24());
		unitButton.setIconAlign(IconAlign.RIGHT);
		unitButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		unitButton.setMenu(createUnitMenu());
		unitButton.setVisible(false);
		
		toolBar.add(downloadButton, new BoxLayoutData(new Margins(0)));
		toolBar.add(measureButton, new BoxLayoutData(new Margins(0)));
		toolBar.add(unitButton, new BoxLayoutData(new Margins(0)));
		
		
		//
		createChart();

		highchartsLayoutPanel = new HighchartsLayoutPanel();
		highchartsLayoutPanel.renderChart(options);

		//
		vert = new VerticalLayoutContainer();
		vert.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		vert.add(highchartsLayoutPanel, new VerticalLayoutData(1, 1,
				new Margins(0)));

		add(vert, new MarginData(0));

	}

	private void updateChart() {
		createChart();
		highchartsLayoutPanel.renderChart(options);
		forceLayout();
	}

	private Menu createDownloadMenu() {
		Menu menuDownload = new Menu();
		downloadPNGItem = new MenuItem(DownloadConstants.DOWNLOAD_PNG,
				AccountingManagerResources.INSTANCE.accountingFilePNG24());
		downloadPNGItem.setHeight(30);
		downloadJPGItem = new MenuItem(DownloadConstants.DOWNLOAD_JPG,
				AccountingManagerResources.INSTANCE.accountingFileJPG24());
		downloadJPGItem.setHeight(30);
		downloadPDFItem = new MenuItem(DownloadConstants.DOWNLOAD_PDF,
				AccountingManagerResources.INSTANCE.accountingFilePDF24());
		downloadPDFItem.setHeight(30);
		downloadSVGItem = new MenuItem(DownloadConstants.DOWNLOAD_SVG,
				AccountingManagerResources.INSTANCE.accountingFileSVG24());
		downloadSVGItem.setHeight(30);

		downloadPNGItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				String id = highchartsLayoutPanel.getElement().getId();
				onDownloadPNG(id);

			}
		});

		downloadJPGItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				String id = highchartsLayoutPanel.getElement().getId();
				onDownloadJPG(id);
			}
		});

		downloadPDFItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				String id = highchartsLayoutPanel.getElement().getId();
				onDownloadPDF(id);
			}
		});

		downloadSVGItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				String id = highchartsLayoutPanel.getElement().getId();
				onDownloadSVG(id);
			}
		});

		menuDownload.add(downloadPNGItem);
		menuDownload.add(downloadJPGItem);
		menuDownload.add(downloadPDFItem);
		menuDownload.add(downloadSVGItem);
		return menuDownload;

	}

	private Menu createMeasureMenu() {
		Menu menuMeasure = new Menu();
		// TODO
		durationItem = new MenuItem(ServiceChartMeasure.Duration.getLabel());
		durationItem.setHeight(30);
		operationCountItem = new MenuItem(
				ServiceChartMeasure.OperationCount.getLabel());
		operationCountItem.setHeight(30);
		maxInvocationTimeItem = new MenuItem(
				ServiceChartMeasure.MaxInvocationTime.getLabel());
		maxInvocationTimeItem.setHeight(30);
		minInvocationTimeItem = new MenuItem(
				ServiceChartMeasure.MinInvocationTime.getLabel());
		minInvocationTimeItem.setHeight(30);

		durationItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				measureButton.setText(ServiceChartMeasure.Duration.getLabel());
				measure = ServiceChartMeasure.Duration;
				unitButton.setVisible(true);
				updateChart();
			}
		});

		operationCountItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				measureButton.setText(ServiceChartMeasure.OperationCount
						.getLabel());
				measure = ServiceChartMeasure.OperationCount;
				unitButton.setVisible(false);
				updateChart();
			}
		});

		maxInvocationTimeItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				measureButton.setText(ServiceChartMeasure.MaxInvocationTime
						.getLabel());
				measure = ServiceChartMeasure.MaxInvocationTime;
				unitButton.setVisible(true);
				updateChart();
			}
		});

		minInvocationTimeItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				measureButton.setText(ServiceChartMeasure.MinInvocationTime
						.getLabel());
				measure = ServiceChartMeasure.MinInvocationTime;
				unitButton.setVisible(true);
				updateChart();
			}
		});

		menuMeasure.add(durationItem);
		menuMeasure.add(maxInvocationTimeItem);
		menuMeasure.add(minInvocationTimeItem);
		menuMeasure.add(operationCountItem);

		return menuMeasure;

	}

	private Menu createUnitMenu() {
		Menu menuUnit = new Menu();
		msItem = new MenuItem(TimeUnitMeasure.MILLISECONDS,
				AccountingManagerResources.INSTANCE.accountingUnitms24());
		msItem.setHeight(30);
		sItem = new MenuItem(TimeUnitMeasure.SECONDS,
				AccountingManagerResources.INSTANCE.accountingUnits24());
		sItem.setHeight(30);
		mItem = new MenuItem(TimeUnitMeasure.MINUTES,
				AccountingManagerResources.INSTANCE.accountingUnitm24());
		mItem.setHeight(30);
		hItem = new MenuItem(TimeUnitMeasure.HOURS,
				AccountingManagerResources.INSTANCE.accountingUnith24());
		hItem.setHeight(30);

		msItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				unitMeasure = TimeUnitMeasure.getMilliseconds();
				unitMeasureLabel = TimeUnitMeasure.MS;
				unitButton.setIcon(AccountingManagerResources.INSTANCE
						.accountingUnitms24());
				updateChart();
			}
		});

		sItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				unitMeasure = TimeUnitMeasure.getSeconds();
				unitMeasureLabel = TimeUnitMeasure.S;
				unitButton.setIcon(AccountingManagerResources.INSTANCE
						.accountingUnits24());
				updateChart();
			}
		});

		mItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				unitMeasure = TimeUnitMeasure.getMinutes();
				unitMeasureLabel = TimeUnitMeasure.M;
				unitButton.setIcon(AccountingManagerResources.INSTANCE
						.accountingUnitm24());
				updateChart();
			}
		});

		hItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				unitMeasure = TimeUnitMeasure.getHours();
				unitMeasureLabel = TimeUnitMeasure.H;
				unitButton.setIcon(AccountingManagerResources.INSTANCE
						.accountingUnith24());
				updateChart();
			}
		});

		menuUnit.add(msItem);
		menuUnit.add(sItem);
		menuUnit.add(mItem);
		menuUnit.add(hItem);
		return menuUnit;

	}

	// chart.options.exporting.buttons.contextButton.menuItems[0].onclick();

	public static native void onDownloadPNG(String id) /*-{
		console.log(id);
		var chart = $wnd
				.jQuery('#' + id)
				.highcharts(
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.service.ServiceChartTopPanel::options);
		console.log(chart);
		chart.exportChart();

	}-*/;

	public static native void onDownloadJPG(String id) /*-{
		console.log(id);
		var chart = $wnd
				.jQuery('#' + id)
				.highcharts(
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.service.ServiceChartTopPanel::options);
		console.log(chart);
		chart.exportChart({
			type : 'image/jpeg'
		});

	}-*/;

	public static native void onDownloadPDF(String id) /*-{
		console.log(id);
		var chart = $wnd
				.jQuery('#' + id)
				.highcharts(
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.service.ServiceChartTopPanel::options);
		console.log(chart);
		chart.exportChart({
			type : 'application/pdf'
		});

	}-*/;

	public static native void onDownloadSVG(String id) /*-{
		console.log(id);
		var chart = $wnd
				.jQuery('#' + id)
				.highcharts(
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.service.ServiceChartTopPanel::options);
		console.log(chart);
		chart.exportChart({
			type : 'image/svg+xml'
		});

	}-*/;

	private void createChart() {
		SeriesService seriesService = (SeriesService) accountingStateData
				.getSeriesResponse();

		if (!(seriesService.getSerieServiceDefinition() instanceof SeriesServiceTop)) {
			Log.debug("Invalid SeriesServiceTop!");
			return;
		}
		SeriesServiceTop seriesServiceTop = (SeriesServiceTop) seriesService
				.getSerieServiceDefinition();

		double minRange = ChartTimeMeasure
				.calculateMinRange(accountingStateData.getSeriesRequest()
						.getAccountingPeriod());

		double interval = ChartTimeMeasure
				.calculateInterval(accountingStateData.getSeriesRequest()
						.getAccountingPeriod());

		Date dateStart = dtf.parse(accountingStateData.getSeriesRequest()
				.getAccountingPeriod().getStartDate());

		dateStart.setTime(dateStart.getTime()
				+ ChartTimeMeasure.timeZoneOffset() * ChartTimeMeasure.MINUTE);

		Log.debug("BuildChart DateStart: "
				+ DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_FULL)
						.format(dateStart));

		HighchartsOptionFactory highchartsFactory = new JsoHighchartsOptionFactory();

		options = highchartsFactory.createChartOptions();
		options.chart().zoomType("xy");
		options.exporting().buttons().contextButton().enabled(false);
		options.exporting().filename("AccountingServiceTop");
		options.title().text("Accounting Service Top");

		/*
		 * options.subtitle().text("Click and drag in the plot area to zoom in");
		 */

		ArrayString colors = options.colors();
		// colors.setValue(0, "#cc0038");
		// colors.setValue(1, "#32cd32");

		// xAxis
		options.xAxis().type("datetime").minRange(minRange);

		// yAxis
		options.setFieldAsJsonObject("yAxis", retrieveYAxis(colors));

		options.plotOptions().setFieldAsJsonObject(
				"column",
				"{ " + "\"stacking\": \"normal\"," + "\"dataLabels\": { "
						+ "    \"enabled\": \"true\","
						+ "    \"color\": \"white\", " + "    \"style\": {"
						+ "        \"textShadow\": \"0 0 3px black\"" + "    }"
						+ " }" + " }");

		ArrayList<SeriesColumn> seriesColumn = new ArrayList<>();
		for (int i = 0; i < seriesServiceTop.getSeriesServiceDataTopList()
				.size(); i++) {
			SeriesColumn seriesColumnData = highchartsFactory
					.createSeriesColumn();

			seriesColumnData.name(seriesServiceTop
					.getSeriesServiceDataTopList().get(i).getFilterValue()
					.getValue());
			seriesColumnData.color(colors.get(i));
			seriesColumnData.type("column");

			ArrayNumber data = seriesColumnData.dataAsArrayNumber();

			for (SeriesServiceData seriesServiceData : seriesServiceTop
					.getSeriesServiceDataTopList().get(i).getSeries()) {
				switch (measure) {
				case Duration:
					data.push(seriesServiceData.getDuration() / unitMeasure);
					break;
				case MaxInvocationTime:
					data.push(seriesServiceData.getMaxInvocationTime()
							/ unitMeasure);
					break;
				case MinInvocationTime:
					data.push(seriesServiceData.getMinInvocationTime()
							/ unitMeasure);
					break;
				case OperationCount:
					data.push(seriesServiceData.getOperationCount());
					break;
				default:
					data.push(seriesServiceData.getDuration() / unitMeasure);
					break;

				}

			}

			seriesColumnData.pointInterval(interval).pointStart(
					dateStart.getTime());
			seriesColumn.add(seriesColumnData);

		}

		for (SeriesColumn serie : seriesColumn) {
			options.series().addToEnd(serie);
		}

		options.chart().showAxes(true);

		options.legend().enabled(true);
		return;
	}

	private String retrieveYAxis(ArrayString colors) {
		switch (measure) {
		case Duration:
			return "[{" + " \"id\": \"" + ServiceChartMeasure.Duration.name()
					+ "\"," + " \"labels\": {" + "    \"format\": \"{value} "
					+ unitMeasureLabel + "\"," + "    \"style\": {"
					+ "       \"color\": \"" + colors.get(1) + "\"" + "     }"
					+ " }," + " \"stackLabels\": {" + " \"enabled\": \"true\","
					+ " \"style\": {" + "     \"fontWeight\": \"bold\","
					+ "    \"color\": \"gray\"" + "   } " + "   },"
					+ " \"title\": { " + "    \"text\": \""
					+ ServiceChartMeasure.Duration.getLabel() + "\","
					+ "    \"style\": {" + "       \"color\": \""
					+ colors.get(1) + "\"" + "    }" + " }" + "}]";
		case MaxInvocationTime:
			return "[{" + " \"id\": \""
					+ ServiceChartMeasure.MaxInvocationTime.name() + "\","
					+ " \"labels\": {" + "    \"format\": \"{value} "
					+ unitMeasureLabel + "\"," + "    \"style\": {"
					+ "       \"color\": \"" + colors.get(1) + "\"" + "     }"
					+ " }," + " \"stackLabels\": {" + " \"enabled\": \"true\","
					+ " \"style\": {" + "     \"fontWeight\": \"bold\","
					+ "    \"color\": \"gray\"" + "   } " + "   },"
					+ " \"title\": { " + "    \"text\": \""
					+ ServiceChartMeasure.MaxInvocationTime.getLabel() + "\","
					+ "    \"style\": {" + "       \"color\": \""
					+ colors.get(1) + "\"" + "    }" + " }" + "}]";
		case MinInvocationTime:
			return "[{" + " \"id\": \""
					+ ServiceChartMeasure.MinInvocationTime.name() + "\","
					+ " \"labels\": {" + "    \"format\": \"{value} "
					+ unitMeasureLabel + "\"," + "    \"style\": {"
					+ "       \"color\": \"" + colors.get(1) + "\"" + "     }"
					+ " }," + " \"stackLabels\": {" + " \"enabled\": \"true\","
					+ " \"style\": {" + "     \"fontWeight\": \"bold\","
					+ "    \"color\": \"gray\"" + "   } " + "   },"
					+ " \"title\": { " + "    \"text\": \""
					+ ServiceChartMeasure.MinInvocationTime.getLabel() + "\","
					+ "    \"style\": {" + "       \"color\": \""
					+ colors.get(1) + "\"" + "    }" + " }" + "}]";
		case OperationCount:
			return "[{" + " \"id\": \""
					+ ServiceChartMeasure.OperationCount.name() + "\","
					+ " \"labels\": { " + "    \"format\": \"{value}\","
					+ "    \"style\": { " + "      \"color\": \""
					+ colors.get(1) + "\"" + "    }" + " },"
					+ " \"stackLabels\": {" + " \"enabled\": \"true\","
					+ " \"style\": {" + "     \"fontWeight\": \"bold\","
					+ "    \"color\": \"gray\"" + "   } " + "   },"
					+ " \"title\": { " + "    \"text\": \""
					+ ServiceChartMeasure.OperationCount.getLabel() + "\","
					+ "    \"style\": {" + "       \"color\": \""
					+ colors.get(1) + "\"" + "    }" + " }" + "}]";

		default:
			return "[{" + " \"id\": \"" + ServiceChartMeasure.Duration.name()
					+ "\"," + " \"labels\": {" + "    \"format\": \"{value} "
					+ unitMeasureLabel + "\"," + "    \"style\": {"
					+ "       \"color\": \"" + colors.get(1) + "\"" + "     }"
					+ " }," + " \"stackLabels\": {" + " \"enabled\": \"true\","
					+ " \"style\": {" + "     \"fontWeight\": \"bold\","
					+ "    \"color\": \"gray\"" + "   } " + "   },"
					+ " \"title\": { " + "    \"text\": \""
					+ ServiceChartMeasure.Duration.getLabel() + "\","
					+ "    \"style\": {" + "       \"color\": \""
					+ colors.get(1) + "\"" + "    }" + " }" + "}]";

		}

	}

}
