package org.gcube.portlets.user.accountingdashboard.server.accounting;

import java.util.ArrayList;

import org.gcube.accounting.accounting.summary.access.model.Record;
import org.gcube.accounting.accounting.summary.access.model.Report;
import org.gcube.accounting.accounting.summary.access.model.ReportElement;
import org.gcube.accounting.accounting.summary.access.model.Series;
import org.gcube.portlets.user.accountingdashboard.shared.data.RecordData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ReportElementData;
import org.gcube.portlets.user.accountingdashboard.shared.data.SeriesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ReportDataBuilder {
	private static Logger logger = LoggerFactory.getLogger(ReportDataBuilder.class);

	private Report report;

	public ReportDataBuilder(Report report) {
		this.report = report;
	}

	public ReportData build() {
		if (report == null || report.getElements() == null) {
			logger.error("Invalid report: " + report);
			return null;
		}

		ArrayList<ReportElementData> elements = new ArrayList<>();
		for (ReportElement reportElement : report.getElements()) {
			if (reportElement.getSerieses() == null) {
				ReportElementData reportElementData = new ReportElementData();
				reportElementData.setLabel(reportElement.getLabel());
				reportElementData.setCategory(reportElement.getCategory());
				reportElementData.setxAxis(reportElement.getXAxis());
				reportElementData.setyAxis(reportElement.getYAxis());
				reportElementData.setSerieses(null);
				elements.add(reportElementData);
			} else {

				Series[] serieses = reportElement.getSerieses();
				int seriesLen = serieses.length;
				SeriesData[] seriesesData = new SeriesData[seriesLen];
				for (int i = 0; i < seriesLen; i++) {
					Series series = serieses[i];
					SeriesData seriesData = null;
					if (series.getDataRow() == null) {
						seriesData = new SeriesData();
						seriesData.setLabel(series.getLabel());
						seriesData.setDataRow(null);
					} else {
						Record[] dataRow = series.getDataRow();
						int dataRowLen = dataRow.length;
						RecordData[] dataRowData = new RecordData[dataRowLen];
						for (int j = 0; j < dataRowLen; j++) {
							Record record = dataRow[j];
							RecordData recordData = new RecordData();
							recordData.setX(record.getX());
							recordData.setY(record.getY());
							dataRowData[j] = recordData;
						}
						seriesData = new SeriesData();
						seriesData.setLabel(series.getLabel());
						seriesData.setDataRow(dataRowData);
					}
					seriesesData[i] = seriesData;
				}
				ReportElementData reportElementData = new ReportElementData();
				reportElementData.setLabel(reportElement.getLabel());
				reportElementData.setCategory(reportElement.getCategory());
				reportElementData.setxAxis(reportElement.getXAxis());
				reportElementData.setyAxis(reportElement.getYAxis());
				reportElementData.setSerieses(seriesesData);
				elements.add(reportElementData);
			}
		}
		ReportData reportData = new ReportData(elements);
		logger.debug("ReportData: " + reportData);
		return reportData;

	}

}
