package org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage;

import java.util.Date;

import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.ByteUnitMeasure;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.ChartTimeMeasure;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.DownloadConstants;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.StorageChartMeasure;
import org.gcube.portlets.admin.accountingmanager.client.resource.AccountingManagerResources;
import org.gcube.portlets.admin.accountingmanager.client.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesStorage;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageData;

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
public class StorageChartBasicPanel extends SimpleContainer {

	private static final String DATA_VOLUME_UNIT = "Data Volume Unit";

	private DateTimeFormat dtf = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH_DAY);

	private AccountingStateData accountingStateData;
	private HighchartsLayoutPanel highchartsLayoutPanel;

	// Download Menu
	private MenuItem downloadPNGItem;
	private MenuItem downloadJPGItem;
	private MenuItem downloadPDFItem;
	private MenuItem downloadSVGItem;

	// Unit Menu
	private MenuItem kBItem;
	private MenuItem MBItem;
	private MenuItem GBItem;
	private MenuItem TBItem;

	private long unitMeasure = ByteUnitMeasure.getMegaByteDimForStorage();
	private String unitMeasureLabel = ByteUnitMeasure.MB;
	private TextButton unitButton;

	private ChartOptions options;
	private VerticalLayoutContainer vert;

	public StorageChartBasicPanel(AccountingStateData accountingStateData) {
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

		unitButton = new TextButton(DATA_VOLUME_UNIT,
				AccountingManagerResources.INSTANCE.accountingUnitMB24());
		unitButton.setIconAlign(IconAlign.RIGHT);
		unitButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		unitButton.setMenu(createUnitMenu());

		toolBar.add(downloadButton, new BoxLayoutData(new Margins(0)));
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
		
		/*
		 * vert.remove(highchartsLayoutPanel); // createChart();
		 * 
		 * highchartsLayoutPanel = new HighchartsLayoutPanel();
		 * highchartsLayoutPanel.renderChart(options);
		 * 
		 * vert.add(highchartsLayoutPanel, new VerticalLayoutData(1, 1, new
		 * Margins(0)));
		 * 
		 * forceLayout();
		 */
	}

	private Menu createUnitMenu() {
		Menu menuUnit = new Menu();
		kBItem = new MenuItem(ByteUnitMeasure.KILOBYTE,
				AccountingManagerResources.INSTANCE.accountingUnitkB24());
		kBItem.setHeight(30);
		MBItem = new MenuItem(ByteUnitMeasure.MEGABYTE,
				AccountingManagerResources.INSTANCE.accountingUnitMB24());
		MBItem.setHeight(30);
		GBItem = new MenuItem(ByteUnitMeasure.GIGABYTE,
				AccountingManagerResources.INSTANCE.accountingUnitGB24());
		GBItem.setHeight(30);
		TBItem = new MenuItem(ByteUnitMeasure.TERABYTE,
				AccountingManagerResources.INSTANCE.accountingUnitTB24());
		TBItem.setHeight(30);

		kBItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				unitMeasure = ByteUnitMeasure.getKiloByteDimForStorage();
				unitMeasureLabel = ByteUnitMeasure.kB;
				unitButton.setIcon(AccountingManagerResources.INSTANCE
						.accountingUnitkB24());
				updateChart();
			}
		});

		MBItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				unitMeasure = ByteUnitMeasure.getMegaByteDimForStorage();
				unitMeasureLabel = ByteUnitMeasure.MB;
				unitButton.setIcon(AccountingManagerResources.INSTANCE
						.accountingUnitMB24());
				updateChart();
			}
		});

		GBItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				unitMeasure = ByteUnitMeasure.getGigaByteDimForStorage();
				unitMeasureLabel = ByteUnitMeasure.GB;
				unitButton.setIcon(AccountingManagerResources.INSTANCE
						.accountingUnitGB24());
				updateChart();
			}
		});

		TBItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				unitMeasure = ByteUnitMeasure.getTeraByteDimForStorage();
				unitMeasureLabel = ByteUnitMeasure.TB;
				unitButton.setIcon(AccountingManagerResources.INSTANCE
						.accountingUnitTB24());
				updateChart();
			}
		});

		menuUnit.add(kBItem);
		menuUnit.add(MBItem);
		menuUnit.add(GBItem);
		menuUnit.add(TBItem);
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
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage.StorageChartBasicPanel::options);
		console.log(chart);
		chart.exportChart();

	}-*/;

	public static native void onDownloadJPG(String id) /*-{
		console.log(id);
		var chart = $wnd
				.jQuery('#' + id)
				.highcharts(
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage.StorageChartBasicPanel::options);
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
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage.StorageChartBasicPanel::options);
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
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage.StorageChartBasicPanel::options);
		console.log(chart);
		chart.exportChart({
			type : 'image/svg+xml'
		});

	}-*/;

	private void createChart() {
		SeriesStorage seriesStorage = (SeriesStorage) accountingStateData
				.getSeriesResponse();

		if (!(seriesStorage.getSeriesStorageDefinition() instanceof SeriesStorageBasic)) {
			Log.error("Invalid SeriesStorageBasic!");
			return;
		}
		SeriesStorageBasic seriesStorageBasic = (SeriesStorageBasic) seriesStorage
				.getSeriesStorageDefinition();

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
		options.exporting().filename("AccountingStorageBasic");
		options.title().text("Accounting Storage");

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
				+ StorageChartMeasure.OperationCount.name() + "\","
				+ " \"labels\": { " + "    \"format\": \"{value}\","
				+ "    \"style\": { " + "      \"color\": \"" + colors.get(1)
				+ "\"" + "    }" + " }," + " \"title\": { "
				+ "    \"text\": \""
				+ StorageChartMeasure.OperationCount.getLabel() + "\","
				+ "    \"style\": {" + "       \"color\": \"" + colors.get(1)
				+ "\"" + "    }" + " }" + "} , {" + " \"id\": \""
				+ StorageChartMeasure.DataVolume.name() + "\", "
				+ " \"title\": {" + "    \"text\": \""
				+ StorageChartMeasure.DataVolume.getLabel() + "\","
				+ "    \"style\": {" + "       \"color\": \"" + colors.get(0)
				+ "\"" + "    }" + " }," + " \"labels\": {"
				+ "    \"format\": \"{value} " + unitMeasureLabel + "\","
				+ "    \"style\": {" + "       \"color\": \"" + colors.get(0)
				+ "\"" + "     }" + " }," + " \"opposite\": \"true\"" + "}]";

		options.setFieldAsJsonObject("yAxis", multiAxis);

		// TODO does not seem to be working
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
				.name(StorageChartMeasure.OperationCount.getLabel());
		seriesOperationCount.color(colors.get(1));
		seriesOperationCount.type("column");

		ArrayNumber dataOperationCount = seriesOperationCount
				.dataAsArrayNumber();

		seriesOperationCount.pointInterval(interval).pointStart(
				dateStart.getTime());

		SeriesArea seriesDataVolume = highchartsFactory.createSeriesArea();
		seriesDataVolume.name(StorageChartMeasure.DataVolume.getLabel());
		seriesDataVolume.color(colors.get(0));
		seriesDataVolume.yAxisAsString(StorageChartMeasure.DataVolume.name());

		ArrayNumber dataDataVolume = seriesDataVolume.dataAsArrayNumber();

		seriesDataVolume.pointInterval(interval)
				.pointStart(dateStart.getTime());

		for (SeriesStorageData seriesStorageData : seriesStorageBasic
				.getSeries()) {
			dataOperationCount.push(seriesStorageData.getOperationCount());
			dataDataVolume
					.push(seriesStorageData.getDataVolume() / unitMeasure);
		}

		options.series().addToEnd(seriesOperationCount);
		options.series().addToEnd(seriesDataVolume);

		options.chart().showAxes(true);

		options.legend().enabled(true);
		return;
	}

}
