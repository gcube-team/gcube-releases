package org.gcube.portlets.admin.accountingmanager.client.maindata.charts.job;

import java.util.Date;

import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.ChartTimeMeasure;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.DownloadConstants;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.JobChartMeasure;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.TimeUnitMeasure;
import org.gcube.portlets.admin.accountingmanager.client.resource.AccountingManagerResources;
import org.gcube.portlets.admin.accountingmanager.client.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesJob;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobData;

import com.allen_sauer.gwt.log.client.Log;
import com.github.highcharts4gwt.client.view.widget.HighchartsLayoutPanel;
import com.github.highcharts4gwt.model.array.api.ArrayNumber;
import com.github.highcharts4gwt.model.array.api.ArrayString;
import com.github.highcharts4gwt.model.factory.api.HighchartsOptionFactory;
import com.github.highcharts4gwt.model.factory.jso.JsoHighchartsOptionFactory;
import com.github.highcharts4gwt.model.highcharts.option.api.ChartOptions;
import com.github.highcharts4gwt.model.highcharts.option.api.SeriesArea;
import com.github.highcharts4gwt.model.highcharts.option.api.SeriesColumn;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonArrowAlign;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
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
public class JobChartBasicPanel extends SimpleContainer {
	private DateTimeFormat dtf = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH_DAY);
	private static final String TIME_UNIT = "Time Unit";
	private static final String SINGLE_AXIS = "Single Axis";

	private AccountingStateData accountingStateData;
	private HighchartsLayoutPanel highchartsLayoutPanel;

	// Download Menu
	private MenuItem downloadPNGItem;
	private MenuItem downloadJPGItem;
	private MenuItem downloadPDFItem;
	private MenuItem downloadSVGItem;

	// Time Unit Menu
	private MenuItem msItem;
	private MenuItem sItem;
	private MenuItem mItem;
	private MenuItem hItem;

	private ChartOptions options;
	private VerticalLayoutContainer vert;

	private long unitMeasure = TimeUnitMeasure.getMilliseconds();
	private String unitMeasureLabel = TimeUnitMeasure.MS;
	private TextButton unitButton;
	private ToggleButton toggleButton;

	public JobChartBasicPanel(AccountingStateData accountingStateData) {
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

		unitButton = new TextButton(TIME_UNIT,
				AccountingManagerResources.INSTANCE.accountingUnitms24());
		unitButton.setIconAlign(IconAlign.RIGHT);
		unitButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		unitButton.setMenu(createUnitMenu());

		// Single Axis
		toggleButton = new ToggleButton(SINGLE_AXIS);
		toggleButton.setIcon(AccountingManagerResources.INSTANCE
				.accountingChartVariableAxis24());
		toggleButton.setIconAlign(IconAlign.RIGHT);
		toggleButton.setValue(false);

		toggleButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				updateChart();
			}
		});

		toolBar.add(downloadButton, new BoxLayoutData(new Margins(0)));
		toolBar.add(unitButton, new BoxLayoutData(new Margins(0)));
		toolBar.add(toggleButton, new BoxLayoutData(new Margins(0)));

		//
		createMultiAxisChart();

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
		if (toggleButton.getValue()) {
			createSingleAxisChart();
			highchartsLayoutPanel.renderChart(options);
		} else {
			createMultiAxisChart();
			highchartsLayoutPanel.renderChart(options);
		}
		forceLayout();
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

	// chart.options.exporting.buttons.contextButton.menuItems[0].onclick();

	public static native void onDownloadPNG(String id) /*-{
		console.log(id);
		var chart = $wnd
				.jQuery('#' + id)
				.highcharts(
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.job.JobChartBasicPanel::options);
		console.log(chart);
		chart.exportChart();

	}-*/;

	public static native void onDownloadJPG(String id) /*-{
		console.log(id);
		var chart = $wnd
				.jQuery('#' + id)
				.highcharts(
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.job.JobChartBasicPanel::options);
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
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.job.JobChartBasicPanel::options);
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
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.job.JobChartBasicPanel::options);
		console.log(chart);
		chart.exportChart({
			type : 'image/svg+xml'
		});

	}-*/;

	private void createMultiAxisChart() {
		SeriesJob seriesJob = (SeriesJob) accountingStateData
				.getSeriesResponse();

		if (!(seriesJob.getSeriesJobDefinition() instanceof SeriesJobBasic)) {
			Log.error("Invalid SeriesJobBasic!");
			return;
		}
		SeriesJobBasic seriesJobBasic = (SeriesJobBasic) seriesJob
				.getSeriesJobDefinition();

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
		options.navigation().buttonOptions().enabled(false);
		options.exporting().filename("AccountingJobBasic");
		options.title().text("Accounting Job");

		/*
		 * options.subtitle().text("Click and drag in the plot area to zoom in");
		 */

		ArrayString colors = options.colors();
		// colors.setValue(0, "#cc0038");
		// colors.setValue(1, "#32cd32");

		// xAxis
		options.xAxis().type("datetime").minRange(minRange);

		// yAxis
		String multiAxis = "[{" + " \"id\": \""
				+ JobChartMeasure.OperationCount.name() + "\","
				+ " \"labels\": { " + "    \"format\": \"{value}\","
				+ "    \"style\": { " + "      \"color\": \"" + colors.get(1)
				+ "\"" + "    }" + " }," + " \"title\": { "
				+ "    \"text\": \""
				+ JobChartMeasure.OperationCount.getLabel() + "\","
				+ "    \"style\": {" + "       \"color\": \"" + colors.get(1)
				+ "\"" + "    }" + " }" + "} , {" + " \"id\": \""
				+ JobChartMeasure.Duration.name() + "\", "
				+ " \"title\": {" + "    \"text\": \""
				+ JobChartMeasure.Duration.getLabel() + "\","
				+ "    \"style\": {" + "       \"color\": \"" + colors.get(0)
				+ "\"" + "    }" + " }," + " \"labels\": {"
				+ "    \"format\": \"{value} " + unitMeasureLabel + "\","
				+ "    \"style\": {" + "       \"color\": \"" + colors.get(0)
				+ "\"" + "     }" + " }," + " \"opposite\": \"true\""
				+ ", \"showFirstLabel\": \"false\"" + "} , {" + " \"id\": \""
				+ JobChartMeasure.MaxInvocationTime.name() + "\", "
				+ " \"title\": {" + "    \"text\": \""
				+ JobChartMeasure.MaxInvocationTime.getLabel() + "\","
				+ "    \"style\": {" + "       \"color\": \"" + colors.get(2)
				+ "\"" + "    }" + " }," + " \"labels\": {"
				+ "    \"format\": \"{value} " + unitMeasureLabel + "\","
				+ "    \"style\": {" + "       \"color\": \"" + colors.get(2)
				+ "\"" + "     }" + " }," + " \"opposite\": \"true\""
				+ ", \"showFirstLabel\": \"false\"" + "} , {" + " \"id\": \""
				+ JobChartMeasure.MinInvocationTime.name() + "\", "
				+ " \"title\": {" + "    \"text\": \""
				+ JobChartMeasure.MinInvocationTime.getLabel() + "\","
				+ "    \"style\": {" + "       \"color\": \"" + colors.get(3)
				+ "\"" + "    }" + " }," + " \"labels\": {"
				+ "    \"format\": \"{value} " + unitMeasureLabel + "\","
				+ "    \"style\": {" + "       \"color\": \"" + colors.get(3)
				+ "\"" + "     }" + " }," + " \"opposite\": \"true\""
				+ ", \"showFirstLabel\": \"false\"" + "}]"

		;

		options.setFieldAsJsonObject("yAxis", multiAxis);

		// does not seem to be working
		String fillcolor = "{" + "\"linearGradient\": {" + "\"x1\": 0,"
				+ "\"y1\": 0," + "\"x2\": 0," + "\"y2\": 1" + "},"
				+ "\"stops\": [" + "[" + "0, \"#058DC7\"" + "]," + "["
				+ "1, \"#FFFFFF\"" + "]" + "]" + "}";

		options.plotOptions().area()
				.setFieldAsJsonObject("fillColor", fillcolor).marker()
				.radius(2).lineWidth(1).states().hover().lineWidth(1);

		SeriesColumn seriesOperationCount = highchartsFactory
				.createSeriesColumn();
		seriesOperationCount
				.name(JobChartMeasure.OperationCount.getLabel());
		seriesOperationCount.color(colors.get(1));
		seriesOperationCount.type("column");

		ArrayNumber dataOperationCount = seriesOperationCount
				.dataAsArrayNumber();

		seriesOperationCount.pointInterval(interval).pointStart(
				dateStart.getTime());

		SeriesArea seriesDuration = highchartsFactory.createSeriesArea();
		seriesDuration.name(JobChartMeasure.Duration.getLabel());
		seriesDuration.color(colors.get(0));
		seriesDuration.yAxisAsString(JobChartMeasure.Duration.name());

		ArrayNumber dataDuration = seriesDuration.dataAsArrayNumber();

		seriesDuration.pointInterval(interval).pointStart(dateStart.getTime());

		SeriesArea seriesMaxInvocationTime = highchartsFactory
				.createSeriesArea();
		seriesMaxInvocationTime.name(JobChartMeasure.MaxInvocationTime
				.getLabel());
		seriesMaxInvocationTime.color(colors.get(2));
		seriesMaxInvocationTime
				.yAxisAsString(JobChartMeasure.MaxInvocationTime.name());

		ArrayNumber dataMaxInvocationTime = seriesMaxInvocationTime
				.dataAsArrayNumber();

		seriesMaxInvocationTime.pointInterval(interval).pointStart(
				dateStart.getTime());

		SeriesArea seriesMinInvocationTime = highchartsFactory
				.createSeriesArea();
		seriesMinInvocationTime.name(JobChartMeasure.MinInvocationTime
				.getLabel());
		seriesMinInvocationTime.color(colors.get(3));
		seriesMinInvocationTime
				.yAxisAsString(JobChartMeasure.MinInvocationTime.name());

		ArrayNumber dataMinInvocationTime = seriesMinInvocationTime
				.dataAsArrayNumber();

		seriesMinInvocationTime.pointInterval(interval).pointStart(
				dateStart.getTime());

		for (SeriesJobData seriesJobData : seriesJobBasic
				.getSeries()) {
			dataOperationCount.push(seriesJobData.getOperationCount());
			dataDuration.push(seriesJobData.getDuration() / unitMeasure);
			dataMaxInvocationTime.push(seriesJobData.getMaxInvocationTime()
					/ unitMeasure);
			dataMinInvocationTime.push(seriesJobData.getMinInvocationTime()
					/ unitMeasure);
		}

		options.series().addToEnd(seriesOperationCount);
		options.series().addToEnd(seriesDuration);
		options.series().addToEnd(seriesMaxInvocationTime);
		options.series().addToEnd(seriesMinInvocationTime);

		options.chart().showAxes(true);

		options.legend().enabled(true);
		return;
	}

	private void createSingleAxisChart() {
		SeriesJob seriesJob = (SeriesJob) accountingStateData
				.getSeriesResponse();

		if (!(seriesJob.getSeriesJobDefinition() instanceof SeriesJobBasic)) {
			Log.debug("Invalid SeriesJobBasic!");
			return;
		}
		SeriesJobBasic seriesJobBasic = (SeriesJobBasic) seriesJob
				.getSeriesJobDefinition();

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

		options.navigation().buttonOptions().enabled(false);
		options.exporting().filename("AccountingJob");
		options.chart().zoomType("xy");

		options.title().text("Accounting Job");

		/*
		 * options.subtitle().text("Click and drag in the plot area to zoom in");
		 */

		ArrayString colors = options.colors();
		// colors.setValue(0, "#cc0038");
		// colors.setValue(1, "#32cd32");

		// xAxis
		options.xAxis().type("datetime").minRange(minRange);

		// yAxis
		// options.yAxis().title().text("Exchange rate");
		// Highcharts.getOptions().colors[0]

		String multiAxis = "[{" + " \"id\": \""
				+ JobChartMeasure.OperationCount.name() + "\","
				+ " \"labels\": { " + "    \"format\": \"{value}\","
				+ "    \"style\": { " + "      \"color\": \"" + colors.get(1)
				+ "\"" + "    }" + " }," + " \"title\": { "
				+ "    \"text\": \""
				+ JobChartMeasure.OperationCount.getLabel() + "\","
				+ "    \"style\": {" + "       \"color\": \"" + colors.get(1)
				+ "\"" + "    }" + " }" + "} , {"
				+ " \"id\": \"JobData\", " + " \"linkedTo\": \"0\","
				+ "  \"gridLineWidth\": \"0\"," + " \"title\": {"
				+ "    \"text\": \"\"," + "    \"style\": {"
				+ "       \"color\": \"" + colors.get(1) + "\"" + "    }"
				+ " }," + " \"labels\": {" + "    \"format\": \"{value} "
				+ unitMeasureLabel + "\"," + "    \"style\": {"
				+ "       \"color\": \"" + colors.get(1) + "\"" + "     }"
				+ " }," + " \"opposite\": \"true\"" + // +
														// ", \"showFirstLabel\": \"false\""
														// +
				"}]";

		options.setFieldAsJsonObject("yAxis", multiAxis);

		// does not seem to be working
		String fillcolor = "{" + "\"linearGradient\": {" + "\"x1\": 0,"
				+ "\"y1\": 0," + "\"x2\": 0," + "\"y2\": 1" + "},"
				+ "\"stops\": [" + "[" + "0, \"#058DC7\"" + "]," + "["
				+ "1, \"#FFFFFF\"" + "]" + "]" + "}";

		options.plotOptions().area()
				.setFieldAsJsonObject("fillColor", fillcolor).marker()
				.radius(2).lineWidth(1).states().hover().lineWidth(1);

		SeriesColumn seriesOperationCount = highchartsFactory
				.createSeriesColumn();
		seriesOperationCount
				.name(JobChartMeasure.OperationCount.getLabel());
		seriesOperationCount.color(colors.get(1));
		seriesOperationCount.type("column");

		ArrayNumber dataOperationCount = seriesOperationCount
				.dataAsArrayNumber();

		seriesOperationCount.pointInterval(interval).pointStart(
				dateStart.getTime());

		SeriesArea seriesDuration = highchartsFactory.createSeriesArea();
		seriesDuration.name(JobChartMeasure.Duration.getLabel());
		seriesDuration.color(colors.get(0));
		//seriesDuration.yAxisAsString("JobData");

		ArrayNumber dataDuration = seriesDuration.dataAsArrayNumber();

		seriesDuration.pointInterval(interval).pointStart(dateStart.getTime());

		SeriesArea seriesMaxInvocationTime = highchartsFactory
				.createSeriesArea();
		seriesMaxInvocationTime.name(JobChartMeasure.MaxInvocationTime
				.getLabel());
		seriesMaxInvocationTime.color(colors.get(2));
		//seriesMaxInvocationTime.yAxisAsString("JobData");

		ArrayNumber dataMaxInvocationTime = seriesMaxInvocationTime
				.dataAsArrayNumber();

		seriesMaxInvocationTime.pointInterval(interval).pointStart(
				dateStart.getTime());

		SeriesArea seriesMinInvocationTime = highchartsFactory
				.createSeriesArea();
		seriesMinInvocationTime.name(JobChartMeasure.MinInvocationTime
				.getLabel());
		seriesMinInvocationTime.color(colors.get(3));
		//seriesMinInvocationTime.yAxisAsString("JobData");

		ArrayNumber dataMinInvocationTime = seriesMinInvocationTime
				.dataAsArrayNumber();

		seriesMinInvocationTime.pointInterval(interval).pointStart(
				dateStart.getTime());

		for (SeriesJobData seriesJobData : seriesJobBasic
				.getSeries()) {
			dataOperationCount.push(seriesJobData.getOperationCount());
			dataDuration.push(seriesJobData.getDuration() / unitMeasure);
			dataMaxInvocationTime.push(seriesJobData.getMaxInvocationTime()
					/ unitMeasure);
			dataMinInvocationTime.push(seriesJobData.getMinInvocationTime()
					/ unitMeasure);
		}

		options.series().addToEnd(seriesOperationCount);
		options.series().addToEnd(seriesDuration);
		options.series().addToEnd(seriesMaxInvocationTime);
		options.series().addToEnd(seriesMinInvocationTime);

		options.chart().showAxes(true);

		options.legend().enabled(true);
		return;
	}

}
