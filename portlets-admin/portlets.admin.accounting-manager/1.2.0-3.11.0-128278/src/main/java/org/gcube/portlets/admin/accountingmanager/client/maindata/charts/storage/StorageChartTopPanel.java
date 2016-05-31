package org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.ByteUnitMeasure;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.ChartTimeMeasure;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.DownloadConstants;
import org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils.StorageChartMeasure;
import org.gcube.portlets.admin.accountingmanager.client.resource.AccountingManagerResources;
import org.gcube.portlets.admin.accountingmanager.client.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesStorage;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageTop;

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
public class StorageChartTopPanel extends SimpleContainer {

	private static final String DATA_VOLUME_UNIT = "Data Volume Unit";
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
	private MenuItem dataVolumeItem;
	private MenuItem operationCountItem;

	// Unit Menu
	private MenuItem kBItem;
	private MenuItem MBItem;
	private MenuItem GBItem;
	private MenuItem TBItem;

	private long unitMeasure = ByteUnitMeasure.getMegaByteDimForStorage();
	private String unitMeasureLabel = ByteUnitMeasure.MB;
	private TextButton unitButton;

	private ChartOptions options;

	private StorageChartMeasure measure = StorageChartMeasure.DataVolume;
	private TextButton measureButton;

	private VerticalLayoutContainer vert;

	public StorageChartTopPanel(AccountingStateData accountingStateData) {
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
		measureButton = new TextButton(
				StorageChartMeasure.DataVolume.getLabel(),
				AccountingManagerResources.INSTANCE.accountingByte24());
		measureButton.setIconAlign(IconAlign.RIGHT);
		measureButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		measureButton.setMenu(createMeasureMenu());

		// Unit
		unitButton = new TextButton(DATA_VOLUME_UNIT,
				AccountingManagerResources.INSTANCE.accountingUnitMB24());
		unitButton.setIconAlign(IconAlign.RIGHT);
		unitButton.setArrowAlign(ButtonArrowAlign.RIGHT);
		unitButton.setMenu(createUnitMenu());

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
		/*
		vert.remove(highchartsLayoutPanel);
		//
		createChart();

		highchartsLayoutPanel = new HighchartsLayoutPanel();
		highchartsLayoutPanel.renderChart(options);

		vert.add(highchartsLayoutPanel, new VerticalLayoutData(1, 1,
				new Margins(0)));

		forceLayout();
		*/
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
		dataVolumeItem = new MenuItem(StorageChartMeasure.DataVolume.getLabel());
		dataVolumeItem.setHeight(30);
		operationCountItem = new MenuItem(StorageChartMeasure.OperationCount.getLabel());
		operationCountItem.setHeight(30);

		dataVolumeItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				measureButton.setText(StorageChartMeasure.DataVolume.getLabel());
				measure=StorageChartMeasure.DataVolume;
				unitButton.setVisible(true);
				updateChart();
			}
		});

		operationCountItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				measureButton.setText(StorageChartMeasure.OperationCount
						.getLabel());
				measure=StorageChartMeasure.OperationCount;
				unitButton.setVisible(false);
				updateChart();
			}
		});

		menuMeasure.add(dataVolumeItem);
		menuMeasure.add(operationCountItem);

		return menuMeasure;

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
				unitButton.setIcon(AccountingManagerResources.INSTANCE.accountingUnitkB24());
				updateChart();
			}
		});

		MBItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				unitMeasure = ByteUnitMeasure.getMegaByteDimForStorage();
				unitMeasureLabel = ByteUnitMeasure.MB;
				unitButton.setIcon(AccountingManagerResources.INSTANCE.accountingUnitMB24());
				updateChart();
			}
		});

		GBItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				unitMeasure = ByteUnitMeasure.getGigaByteDimForStorage();
				unitMeasureLabel = ByteUnitMeasure.GB;
				unitButton.setIcon(AccountingManagerResources.INSTANCE.accountingUnitGB24());
				updateChart();
			}
		});

		TBItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				unitMeasure = ByteUnitMeasure.getTeraByteDimForStorage();
				unitMeasureLabel = ByteUnitMeasure.TB;
				unitButton.setIcon(AccountingManagerResources.INSTANCE.accountingUnitTB24());
				updateChart();
			}
		});

		menuUnit.add(kBItem);
		menuUnit.add(MBItem);
		menuUnit.add(GBItem);
		menuUnit.add(TBItem);
		return menuUnit;

	}

	// chart.options.exporting.buttons.contextButton.menuItems[0].onclick();

	public static native void onDownloadPNG(String id) /*-{
		console.log(id);
		var chart = $wnd
				.jQuery('#' + id)
				.highcharts(
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage.StorageChartTopPanel::options);
		console.log(chart);
		chart.exportChart();

	}-*/;

	public static native void onDownloadJPG(String id) /*-{
		console.log(id);
		var chart = $wnd
				.jQuery('#' + id)
				.highcharts(
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage.StorageChartTopPanel::options);
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
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage.StorageChartTopPanel::options);
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
						this.@org.gcube.portlets.admin.accountingmanager.client.maindata.charts.storage.StorageChartTopPanel::options);
		console.log(chart);
		chart.exportChart({
			type : 'image/svg+xml'
		});

	}-*/;

	private void createChart() {
		SeriesStorage seriesStorage = (SeriesStorage) accountingStateData
				.getSeriesResponse();

		if (!(seriesStorage.getSeriesStorageDefinition() instanceof SeriesStorageTop)) {
			Log.debug("Invalid SeriesStorageTop!");
			return;
		}
		SeriesStorageTop seriesStorageTop = (SeriesStorageTop) seriesStorage
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
		options.exporting().filename("AccountingStorageTop");
		options.title().text("Accounting Storage Top");

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
		for (int i = 0; i < seriesStorageTop.getSeriesStorageDataTopList()
				.size(); i++) {
			SeriesColumn seriesColumnData = highchartsFactory
					.createSeriesColumn();

			seriesColumnData.name(seriesStorageTop
					.getSeriesStorageDataTopList().get(i).getFilterValue()
					.getValue());
			seriesColumnData.color(colors.get(i));
			seriesColumnData.type("column");

			ArrayNumber data = seriesColumnData.dataAsArrayNumber();

			for (SeriesStorageData seriesStorageData : seriesStorageTop
					.getSeriesStorageDataTopList().get(i).getSeries()) {
				switch (measure) {
				case DataVolume:
					data.push(seriesStorageData.getDataVolume()/unitMeasure);
					break;
				case OperationCount:
					data.push(seriesStorageData.getOperationCount());
					break;
				default:
					data.push(seriesStorageData.getDataVolume());
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
		case DataVolume:
			return "[{" + " \"id\": \"" + StorageChartMeasure.DataVolume.name()
					+ "\"," + " \"labels\": {" + "    \"format\": \"{value} "
					+ unitMeasureLabel + "\"," + "    \"style\": {"
					+ "       \"color\": \"" + colors.get(1) + "\"" + "     }"
					+ " }," + " \"stackLabels\": {" + " \"enabled\": \"true\","
					+ " \"style\": {" + "     \"fontWeight\": \"bold\","
					+ "    \"color\": \"gray\"" + "   } " + "   },"
					+ " \"title\": { " + "    \"text\": \""
					+ StorageChartMeasure.DataVolume.getLabel() + "\","
					+ "    \"style\": {" + "       \"color\": \""
					+ colors.get(1) + "\"" + "    }" + " }" + "}]";

		case OperationCount:
			return "[{" + " \"id\": \""
					+ StorageChartMeasure.OperationCount.name() + "\","
					+ " \"labels\": { " + "    \"format\": \"{value}\","
					+ "    \"style\": { " + "      \"color\": \""
					+ colors.get(1) + "\"" + "    }" + " },"
					+ " \"stackLabels\": {" + " \"enabled\": \"true\","
					+ " \"style\": {" + "     \"fontWeight\": \"bold\","
					+ "    \"color\": \"gray\"" + "   } " + "   },"
					+ " \"title\": { " + "    \"text\": \""
					+ StorageChartMeasure.OperationCount.getLabel() + "\","
					+ "    \"style\": {" + "       \"color\": \""
					+ colors.get(1) + "\"" + "    }" + " }" + "}]";

		default:
			return "[{" + " \"id\": \"" + StorageChartMeasure.DataVolume.name()
					+ "\"," + " \"labels\": {" + "    \"format\": \"{value} "
					+ unitMeasureLabel + "\"," + "    \"style\": {"
					+ "       \"color\": \"" + colors.get(1) + "\"" + "     }"
					+ " }," + " \"stackLabels\": {" + " \"enabled\": \"true\","
					+ " \"style\": {" + "     \"fontWeight\": \"bold\","
					+ "    \"color\": \"gray\"" + "   } " + "   },"
					+ " \"title\": { " + "    \"text\": \""
					+ StorageChartMeasure.DataVolume.getLabel() + "\","
					+ "    \"style\": {" + "       \"color\": \""
					+ colors.get(1) + "\"" + "    }" + " }" + "}]";

		}

	}

}
