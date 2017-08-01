package org.gcube.portlets.admin.accountingmanager.server.export.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.portlets.admin.accountingmanager.server.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesJob;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobDataContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobDefinition;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobTop;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accounting Model 4 Job
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class AccountingModel4Job extends AccountingModelBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(AccountingModel4Job.class);
	private AccountingStateData accountingStateData;

	public AccountingModel4Job(AccountingStateData accountingStateData) {
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
		if (seriesResponse instanceof SeriesJob) {
			SeriesJob seriesService = (SeriesJob) seriesResponse;
			SeriesJobDefinition definition = seriesService
					.getSeriesJobDefinition();
			if (definition instanceof SeriesJobBasic) {
				SeriesJobBasic seriesJobBasic = (SeriesJobBasic) definition;
				fileName = "Job_" + periodType + "_" + startDate + "_"
						+ endDate;
				header = new ArrayList<>(Arrays.asList(new String[] { "Date",
						"Operation Count", "Duration", "Max Invocation",
						"Min Invocation" }));
				ArrayList<SeriesJobData> listData = seriesJobBasic
						.getSeries();
				for (SeriesJobData seriesData : listData) {
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
				if (definition instanceof SeriesJobTop) {
					SeriesJobTop seriesJobTop = (SeriesJobTop) definition;
					fileName = "JobTop_" + periodType + "_" + startDate
							+ "_" + endDate;
					header = new ArrayList<>(Arrays.asList(new String[] {
							"Value", "Date", "Operation Count", "Duration",
							"Max Invocation", "Min Invocation" }));
					ArrayList<SeriesJobDataTop> listDataTop = seriesJobTop
							.getSeriesJobDataTopList();
					for (SeriesJobDataTop seriesDataTop : listDataTop) {
						FilterValue filterValue = seriesDataTop
								.getFilterValue();
						ArrayList<SeriesJobData> listData = seriesDataTop
								.getSeries();
						for (SeriesJobData seriesData : listData) {
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
					if (definition instanceof SeriesJobContext) {
						SeriesJobContext seriesJobContext = (SeriesJobContext) definition;
						fileName = "JobContext_" + periodType + "_" + startDate
								+ "_" + endDate;
						header = new ArrayList<>(Arrays.asList(new String[] {
								"Value", "Date", "Operation Count", "Duration",
								"Max Invocation", "Min Invocation" }));
						ArrayList<SeriesJobDataContext> listDataContext = seriesJobContext
								.getSeriesJobDataContextList();
						for (SeriesJobDataContext seriesDataContext : listDataContext) {
							ArrayList<SeriesJobData> listData = seriesDataContext
									.getSeries();
							for (SeriesJobData seriesData : listData) {
								ArrayList<String> data = new ArrayList<>();
								data.add(seriesDataContext.getContext());
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
						logger.error("Unsupported Serie Definition for Job: "
								+ definition);
						throw new ServiceException(
								"Unsupported Serie Definition for Job: "
										+ definition);
					}
				}
			}
		} else {
			logger.error("Job not support this serie response: "
					+ seriesResponse);
			throw new ServiceException(
					"Job not support this serie response: "
							+ seriesResponse);
		}

		AccountingDataModel invocation = new AccountingDataModel(fileName, header, rows);
		accountingModelSpec.setOp(invocation);

	}
}
