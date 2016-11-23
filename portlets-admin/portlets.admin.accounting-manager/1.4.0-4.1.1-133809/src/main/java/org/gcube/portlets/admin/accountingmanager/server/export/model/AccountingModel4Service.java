package org.gcube.portlets.admin.accountingmanager.server.export.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.portlets.admin.accountingmanager.server.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesService;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceDefinition;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceTop;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accounting Model 4 Service
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class AccountingModel4Service extends AccountingModelBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(AccountingModel4Service.class);
	private AccountingStateData accountingStateData;

	public AccountingModel4Service(AccountingStateData accountingStateData) {
		this.accountingStateData = accountingStateData;
	}

	@Override
	public void buildOpEx() throws ServiceException {
		SeriesRequest seriesRequest = accountingStateData.getSeriesRequest();

		if (seriesRequest == null) {
			logger.error("Error series request is null");
			throw new ServiceException(
					"Error series request is null");

		}

		String startDate="";
		try {
			startDate = sdfFile.format(sdf.parse(seriesRequest.getAccountingPeriod()
					.getStartDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String endDate="";
		try {
			endDate = sdfFile.format(sdf.parse(seriesRequest.getAccountingPeriod()
					.getEndDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String periodType = seriesRequest.getAccountingPeriod().getPeriod()
				.getLabel();

		SeriesResponse seriesResponse = accountingStateData.getSeriesResponse();

		if (seriesResponse == null) {
			logger.error("Error series response is null");
			throw new ServiceException(
					"Error series response is null");
		}

		String fileName = "Untitled";
		ArrayList<String> header;
		ArrayList<AccountingDataRow> rows = new ArrayList<>();
		if (seriesResponse instanceof SeriesService) {
			SeriesService seriesService = (SeriesService) seriesResponse;
			SeriesServiceDefinition definition = seriesService
					.getSerieServiceDefinition();
			if (definition instanceof SeriesServiceBasic) {
				SeriesServiceBasic seriesServiceBasic = (SeriesServiceBasic) definition;
				fileName = "Service_" + periodType + "_" + startDate + "_"
						+ endDate;
				header = new ArrayList<>(Arrays.asList(new String[] { "Date",
						"Operation Count", "Duration", "Max Invocation",
						"Min Invocation" }));
				ArrayList<SeriesServiceData> listData = seriesServiceBasic
						.getSeries();
				for (SeriesServiceData seriesData : listData) {
					ArrayList<String> data = new ArrayList<>();
					data.add(sdfCSVDate.format(seriesData.getDate()));
					data.add(String.valueOf(seriesData.getOperationCount()));
					data.add(String.valueOf(seriesData.getDuration()));
					data.add(String.valueOf(seriesData.getMaxInvocationTime()));
					data.add(String.valueOf(seriesData.getMinInvocationTime()));
					AccountingDataRow csvDataRow = new AccountingDataRow(data);
					rows.add(csvDataRow);
				}
			} else {
				if (definition instanceof SeriesServiceTop) {
					SeriesServiceTop seriesServiceTop = (SeriesServiceTop) definition;
					fileName = "ServiceTop_" + periodType + "_" + startDate
							+ "_" + endDate;
					header = new ArrayList<>(Arrays.asList(new String[] {
							"Value", "Date", "Operation Count", "Duration",
							"Max Invocation", "Min Invocation" }));
					ArrayList<SeriesServiceDataTop> listDataTop = seriesServiceTop
							.getSeriesServiceDataTopList();
					for (SeriesServiceDataTop seriesDataTop : listDataTop) {
						FilterValue filterValue = seriesDataTop
								.getFilterValue();
						ArrayList<SeriesServiceData> listData = seriesDataTop
								.getSeries();
						for (SeriesServiceData seriesData : listData) {
							ArrayList<String> data = new ArrayList<>();
							data.add(filterValue.getValue());
							data.add(sdfCSVDate.format(seriesData.getDate()));
							data.add(String.valueOf(seriesData
									.getOperationCount()));
							data.add(String.valueOf(seriesData.getDuration()));
							data.add(String.valueOf(seriesData
									.getMaxInvocationTime()));
							data.add(String.valueOf(seriesData
									.getMinInvocationTime()));
							AccountingDataRow csvDataRow = new AccountingDataRow(data);
							rows.add(csvDataRow);
						}
					}
				} else {
					logger.error("Unsupported Serie Definition for Service: "
							+ definition);
					throw new ServiceException(
							"Unsupported Serie Definition for Service: "
									+ definition);
				}
			}
		} else {
			logger.error("Service not support this serie response: "
					+ seriesResponse);
			throw new ServiceException(
					"Service not support this serie response: "
							+ seriesResponse);
		}

		AccountingDataModel invocation = new AccountingDataModel(fileName, header, rows);
		accountingModelSpec.setOp(invocation);

	}
}
