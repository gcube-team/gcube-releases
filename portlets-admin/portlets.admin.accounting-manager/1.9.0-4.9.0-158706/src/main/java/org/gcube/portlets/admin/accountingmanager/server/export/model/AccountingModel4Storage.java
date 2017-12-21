package org.gcube.portlets.admin.accountingmanager.server.export.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.portlets.admin.accountingmanager.server.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesStorage;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageDataContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageDefinition;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageTop;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accounting Model 4 Storage
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class AccountingModel4Storage extends AccountingModelBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(AccountingModel4Storage.class);
	private AccountingStateData accountingStateData;

	public AccountingModel4Storage(AccountingStateData accountingStateData) {
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
		if (seriesResponse instanceof SeriesStorage) {
			SeriesStorage seriesStorage = (SeriesStorage) seriesResponse;
			SeriesStorageDefinition definition = seriesStorage
					.getSeriesStorageDefinition();
			if (definition instanceof SeriesStorageBasic) {
				SeriesStorageBasic seriesStorageBasic = (SeriesStorageBasic) definition;
				fileName = "Storage_" + periodType + "_" + startDate + "_"
						+ endDate;
				header = new ArrayList<>(Arrays.asList(new String[] { "Date",
						"Operation Count", "Data Volume" }));
				ArrayList<SeriesStorageData> listData = seriesStorageBasic
						.getSeries();
				for (SeriesStorageData seriesData : listData) {
					ArrayList<String> data = new ArrayList<>();
					data.add(sdfCSVDate.format(seriesData.getDate()));
					data.add(String.valueOf(seriesData.getOperationCount()));
					data.add(String.valueOf(seriesData.getDataVolume()));
					AccountingDataRow csvDataRow = new AccountingDataRow(data);
					rows.add(csvDataRow);
				}
			} else {
				if (definition instanceof SeriesStorageTop) {
					SeriesStorageTop seriesStorageTop = (SeriesStorageTop) definition;
					fileName = "StorageTop_" + periodType + "_" + startDate
							+ "_" + endDate;
					header = new ArrayList<>(
							Arrays.asList(new String[] { "Value", "Date",
									"Operation Count", "Data Volume" }));
					ArrayList<SeriesStorageDataTop> listDataTop = seriesStorageTop
							.getSeriesStorageDataTopList();
					for (SeriesStorageDataTop seriesDataTop : listDataTop) {
						FilterValue filterValue = seriesDataTop
								.getFilterValue();
						ArrayList<SeriesStorageData> listData = seriesDataTop
								.getSeries();
						for (SeriesStorageData seriesData : listData) {
							ArrayList<String> data = new ArrayList<>();
							data.add(filterValue.getValue());
							data.add(sdfCSVDate.format(seriesData.getDate()));
							data.add(String.valueOf(seriesData.getOperationCount()));
							data.add(String.valueOf(seriesData.getDataVolume()));
							AccountingDataRow csvDataRow = new AccountingDataRow(data);
							rows.add(csvDataRow);
						}
					}
				} else {
					if (definition instanceof SeriesStorageContext) {
						SeriesStorageContext seriesStorageContext = (SeriesStorageContext) definition;
						fileName = "StorageContext_" + periodType + "_" + startDate
								+ "_" + endDate;
						header = new ArrayList<>(
								Arrays.asList(new String[] { "Value", "Date",
										"Operation Count", "Data Volume" }));
						ArrayList<SeriesStorageDataContext> listDataContext = seriesStorageContext
								.getSeriesStorageDataContextList();
						for (SeriesStorageDataContext seriesDataContext : listDataContext) {
							ArrayList<SeriesStorageData> listData = seriesDataContext
									.getSeries();
							for (SeriesStorageData seriesData : listData) {
								ArrayList<String> data = new ArrayList<>();
								data.add(seriesDataContext.getContext());
								data.add(sdfCSVDate.format(seriesData.getDate()));
								data.add(String.valueOf(seriesData.getOperationCount()));
								data.add(String.valueOf(seriesData.getDataVolume()));
								AccountingDataRow csvDataRow = new AccountingDataRow(data);
								rows.add(csvDataRow);
							}
						}
					} else {
						logger.error("Unsupported Serie Definition for Storage: "
								+ definition);
						throw new ServiceException(
								"Unsupported Serie Definition for Storage: "
										+ definition);
					}
				}
			}
		} else {
			logger.error("Storage not support this serie response: "
					+ seriesResponse);
			throw new ServiceException(
					"Storage not support this serie response: "
							+ seriesResponse);
		}

		AccountingDataModel invocation = new AccountingDataModel(fileName,header, rows);
		accountingModelSpec.setOp(invocation);

	}
}
