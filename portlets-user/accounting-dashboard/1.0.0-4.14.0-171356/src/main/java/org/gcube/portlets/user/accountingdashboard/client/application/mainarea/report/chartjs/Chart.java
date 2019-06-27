package org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs;

import org.gcube.portlets.user.accountingdashboard.client.resources.AppResources;
import org.gcube.portlets.user.accountingdashboard.shared.data.ReportElementData;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class Chart extends HTMLPanel {

	private String name;

	private ReportElementData reportElementData;

	public Chart(AppResources resources, String name, ReportElementData reportElementData) {
		super("<div class='dropdown'>" + "<button class='btn dropdown-toggle' "
				+ "type='button' style='float:right;max-width:80px;' data-toggle='dropdown'><span class='"
				+ resources.uiDataCss().uiDataIconSettings() + "'></span>" + "<span class='caret'></span>" + "</button>"
				+ "<ul class='" + resources.uiDataCss().uiDataChartMenuPosition() + " dropdown-menu'>" + "<li><a id='"
				+ name + "_ExportJPEG' href='#' download='" + reportElementData.getLabel() + ".jpeg'>Export JPEG</a>"
				+ "</li>" + "<li><a id='" + name + "_ExportPNG' href='#' download='" + reportElementData.getLabel()
				+ ".png'>Export PNG</a>" + "</li>" + "<li><a id='" + name + "_ExportPDF' href='#' download='"
				+ reportElementData.getLabel() + ".pdf'>Export PDF</a>" + "</li>" + "<li><a id='" + name
				+ "_ExportCSV' href='#' download='" + reportElementData.getLabel() + ".csv'>Export CSV</a>" + "</li>"
				+ "</ul>" + "</div>" + "<canvas id=" + name + " class='" + resources.uiDataCss().uiDataChartCanvas()
				+ "'></canvas>");
		this.name = name;
		this.reportElementData = reportElementData;
		this.addStyleName(resources.uiDataCss().uiDataChartWrapper());

		addAttachHandler(new AttachEvent.Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					drawChart();
				}

			}
		});

	}

	public void forceLayout() {
		redrawChart();
	}

	private static native void getCanvas(Chart chart)/*-{
		var name = chart.@org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::name;
		console.log('Element name: ' + name);
		var element = this;
		var canvas = $doc.getElementById(name);
		console.log('Canvas: ' + canvas);
		canvas.style.display = 'block';
		canvas.style.visibility = 'visible';
		canvas.width = 800;
		canvas.height = 600;

		var w = canvas.width;
		var h = canvas.height;
		console.log('Canvas dimensions: ' + w + ' x ' + h);

		return canvas;

	}-*/;

	private native void createMenu()/*-{
		console.log('CreateMenu()');
		console.log('This: ' + this);
		var name = this.@org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::name;

		//Export JPEG
		var elementNameJPEG = name + '_ExportJPEG';
		var exportJPEGElement = $doc.getElementById(elementNameJPEG);
		console.log('ExportJPEGElement: ' + exportJPEGElement);
		exportJPEGElement.onclick = @org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::saveImageJPEG(Lorg/gcube/portlets/user/accountingdashboard/client/application/mainarea/report/chartjs/Chart;)(this);

		//Export PNG
		var elementNamePNG = name + '_ExportPNG';
		var exportPNGElement = $doc.getElementById(elementNamePNG);
		console.log('ExportPNGElement: ' + exportPNGElement);
		exportPNGElement.onclick = @org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::saveImagePNG(Lorg/gcube/portlets/user/accountingdashboard/client/application/mainarea/report/chartjs/Chart;)(this);

		//Export PDF
		var elementNamePDF = name + '_ExportPDF';
		var exportPDFElement = $doc.getElementById(elementNamePDF);
		console.log('ExportPDFElement: ' + exportPDFElement);
		exportPDFElement.onclick = @org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::saveFilePDF(Lorg/gcube/portlets/user/accountingdashboard/client/application/mainarea/report/chartjs/Chart;Ljava/lang/String;)(this,exportPDFElement.download);

		//Export CSV
		var elementNameCSV = name + '_ExportCSV';
		var exportCSVElement = $doc.getElementById(elementNameCSV);
		console.log('ExportCSVElement: ' + exportCSVElement);
		exportCSVElement.onclick = @org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::saveFileCSV(Lorg/gcube/portlets/user/accountingdashboard/client/application/mainarea/report/chartjs/Chart;)(this);

	}-*/;

	private static native void saveImageJPEG(Chart chart)/*-{
		console.log('saveImageJPEG()');
		var name = chart.@org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::name;
		console.log('Element name: ' + name);
		var canvas = @org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::getCanvas(Lorg/gcube/portlets/user/accountingdashboard/client/application/mainarea/report/chartjs/Chart;)(chart);
		return function() {
			console.log('Save file jpeg');
			//Set Background White
			var w = canvas.width;
			var h = canvas.height;
			var ctx = canvas.getContext('2d');
			var data = ctx.getImageData(0, 0, w, h);
			var compositeOperation = ctx.globalCompositeOperation;
			ctx.globalCompositeOperation = "destination-over";
			ctx.fillStyle = '#ffffff';
			ctx.fillRect(0, 0, w, h);
			var image = canvas.toDataURL("image/jpeg").replace("image/jpeg",
					"image/octet-stream");
			ctx.clearRect(0, 0, w, h);
			ctx.putImageData(data, 0, 0);
			ctx.globalCompositeOperation = compositeOperation;
			this.href = image;
		};
	}-*/;

	private static native void saveImagePNG(Chart chart)/*-{
		console.log('saveImagePNG()');
		var name = chart.@org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::name;
		console.log('Element name: ' + name);
		var canvas = @org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::getCanvas(Lorg/gcube/portlets/user/accountingdashboard/client/application/mainarea/report/chartjs/Chart;)(chart);
		return function() {
			console.log('Save file png');
			var image = canvas.toDataURL("image/png").replace("image/png",
					"image/octet-stream");
			this.href = image;
		};
	}-*/;

	private static native void saveFilePDF(Chart chart, String filename)/*-{
		console.log('saveFilePDF()');
		var name = chart.@org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::name;
		console.log('Element name: ' + name);
		var canvas = @org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::getCanvas(Lorg/gcube/portlets/user/accountingdashboard/client/application/mainarea/report/chartjs/Chart;)(chart);
		return function() {
			console.log('Save file pdf');
			var image = canvas.toDataURL("image/png", 1.0).replace("image/png",
					"image/octet-stream");
			var doc = new jsPDF('landscape');
			doc.setFontSize(20);
			doc.addImage(image, 'JPEG', 10, 10, 280, 150);
			doc.save(filename);
			return false;
		};
	}-*/;

	private static native void saveFileCSV(Chart chart)/*-{
		console.log('saveFileCSV()');
		var reportElementData = chart.@org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::reportElementData;
		console.log('ReportElementData: ' + reportElementData);
		return function() {
			console.log('Save file csv');

			var serieses = reportElementData.getSerieses();
			var seriesesLen = serieses.length;
			console.log('Serieses lenght: ' + seriesesLen);

			var csvContent = "data:text/csv;charset=utf-8,";
			for (var i = 0; i < seriesesLen; i++) {
				var seriesData = serieses[i];
				var dataRow = seriesData.getDataRow();
				var dataRowLen = dataRow.length;

				var dataArray = [];
				if (i == 0) {
					var heading = "Date";
					var datasetRow = seriesData.getLabel();
					for (var j = 0; j < dataRowLen; j++) {
						var recordData = dataRow[j];
						heading += "," + recordData.getX();
						datasetRow += "," + recordData.getY();
					}
					csvContent += heading + "\r\n";
					csvContent += datasetRow + "\r\n";

				} else {
					var datasetRow = seriesData.getLabel();
					for (var j = 0; j < dataRowLen; j++) {
						var recordData = dataRow[j];
						datasetRow += "," + recordData.getY();
					}
					csvContent += datasetRow + "\r\n";
				}
			}
			this.href = csvContent;

		};

	}-*/;

	private static native void getScales(Chart chart)/*-{
		var reportElementData = chart.@org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::reportElementData;

		var xAxisLabel = reportElementData.getxAxis();
		var yAxisLabel = reportElementData.getyAxis();
		var scalesType = {
			xAxes : [ {
				scaleLabel : {
					display : true,
					labelString : xAxisLabel
				}
			} ],

			yAxes : [ {
				scaleLabel : {
					display : true,
					labelString : yAxisLabel
				},
				ticks : {
					beginAtZero : true
				}
			} ]
		};

		return scalesType;

	}-*/;

	private native void redrawChart() /*-{
		console.log('RedrawChart()');

		var canvas = @org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::getCanvas(Lorg/gcube/portlets/user/accountingdashboard/client/application/mainarea/report/chartjs/Chart;)(this);
		this.@org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::drawChart()();

	}-*/;

	private native void drawChart() /*-{

		var seedPalette = 0;
		function getRandomColor() {
			var palette = [ '#87CEEB', '#DAA520', '#3CB371', '#FF4500',
					'#8FBC8F', '#FF00FF', '#BDB76B', '#5F9EA0', '#A0522D',
					'#6B8E23', '#FFA07A', '#696969', '#DDA0DD', '#C71585',
					'#1E90FF', '#D2B48C', '#90EE90', '#B22222', '#00CED1',
					'#9400D3', '#FFDAB9', '#663399', '#FFE4C4', '#ADFF2F',
					'#FF0000', '#00FFFF', '#483D8B', '#FFFF00', '#00008B',
					'#FFDEAD', '#7CFC00', '#FF6347', '#AFEEEE', '#6A5ACD',
					'#FFFACD', '#0000CD', '#F5DEB3', '#00FF00', '#FF7F50',
					'#7FFFD4', '#7B68EE', '#FFEFD5', '#0000FF', '#DEB887',
					'#32CD32', '#FFA500', '#40E0D0', '#9370DB', '#FFE4B5',
					'#4169E1', '#BC8F8F', '#98FB98', '#8B0000', '#48D1CC',
					'#800080', '#EEE8AA', '#00BFFF', '#F4A460', '#00FA9A',
					'#800000', '#556B2F', '#8A2BE2', '#F0E68C', '#6495ED',
					'#B8860B', '#2E8B57', '#FFD700', '#66CDAA', '#9932CC',
					'#FFC0CB', '#87CEFA', '#CD853F', '#228B22', '#DC143C',
					'#20B2AA', '#BA55D3', '#FFB6C1', '#B0E0E6', '#D2691E',
					'#008000', '#CD5C5C', '#008B8B', '#FF00FF', '#FF69B4',
					'#B0C4DE', '#808000', '#006400', '#F08080', '#008080',
					'#EE82EE', '#FF1493', '#4682B4', '#8B4513', '#9ACD32',
					'#FA8072', '#778899', '#DA70D6', '#DB7093' ];
			seedPalette = (seedPalette) % palette.length;
			var color = palette[seedPalette];
			seedPalette += 1;
			return color;
		}

		this.@org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::createMenu()();

		var canvas = @org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::getCanvas(Lorg/gcube/portlets/user/accountingdashboard/client/application/mainarea/report/chartjs/Chart;)(this);
		console.log('Canvas: ' + canvas);

		var reportElementData = this.@org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::reportElementData;

		var serieses = reportElementData.getSerieses();
		var seriesesLen = serieses.length;
		console.log('Serieses lenght: ' + seriesesLen);

		var datasetsArray = [];
		var labelsArray = []
		for (var i = 0; i < seriesesLen; i++) {

			var seriesData = serieses[i];
			console.log('SeriesData: ' + seriesData);
			var dataRow = seriesData.getDataRow();
			var dataRowLen = dataRow.length;

			var dataArray = [];
			if (i == 0) {
				for (var j = 0; j < dataRowLen; j++) {
					var recordData = dataRow[j];
					dataArray.push(recordData.getY());
					labelsArray.push(recordData.getX());
				}

			} else {
				for (var j = 0; j < dataRowLen; j++) {
					var recordData = dataRow[j];
					dataArray.push(recordData.getY());

				}
			}

			var colorChart = getRandomColor();

			datasetsArray.push({
				label : seriesData.getLabel(),
				backgroundColor : colorChart,
				borderColor : colorChart,
				borderWidth : 1,
				data : dataArray
			});
		}

		var barChartData = {
			labels : labelsArray,
			datasets : datasetsArray
		};

		var label = [ reportElementData.getLabel(), ' [',
				reportElementData.getCategory(), ']' ].filter(Boolean).join("");

		var scalesType = @org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.chartjs.Chart::getScales(Lorg/gcube/portlets/user/accountingdashboard/client/application/mainarea/report/chartjs/Chart;)(this);

		var ctx = canvas.getContext('2d');

		console.log('Create chart');

		var timeOut = setTimeout(function() {
			var chart = new Chart(ctx, {
				// The type of chart we want to create
				type : 'bar',
				data : barChartData,
				backgroundColor : "#FFFFFF",
				options : {
					animation : false,
					responsive : false,
					maintainAspectRatio : false,
					aspectRatio : 1, // width == height
					legend : {
						position : 'top',
					},
					title : {
						display : true,
						text : label
					},
					scales : scalesType,
					tooltips : {
						enabled : true,
						mode : 'point',
						intersect : true

					},
					zoom : {
						enabled : true,
						mode : 'xy',
						limits : {
							max : 20,
							min : 0.1
						}
					}
				}
			});

			chart.update();
			console.log('Canvas offset: left=' + canvas.offsetLeft + ', top='
					+ canvas.offsetTop);
			clearTimeout(timeOut);

		}, 300);

	}-*/;

}
