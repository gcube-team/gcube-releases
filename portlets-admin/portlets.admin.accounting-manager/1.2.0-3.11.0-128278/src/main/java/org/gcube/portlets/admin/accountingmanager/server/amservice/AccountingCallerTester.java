package org.gcube.portlets.admin.accountingmanager.server.amservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingFilterTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesJob;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesService;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesStorage;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobDefinition;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceDefinition;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageDefinition;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageTop;
import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingCallerTester implements AccountingCallerInterface {
	static Logger logger = LoggerFactory
			.getLogger(AccountingCallerTester.class);

	public AccountingCallerTester() {
		super();
	}

	public ArrayList<FilterKey> getFilterKeys(AccountingType accountingType)
			throws AccountingManagerServiceException {
		try {
			logger.debug("getFilterKeys(): [AccountingType=" + accountingType
					+ "]");
			if (accountingType == null) {
				return new ArrayList<FilterKey>();
			}
			ArrayList<FilterKey> filterKeys = new ArrayList<FilterKey>();
			FilterKey key = new FilterKey("ConsumerId");
			FilterKey key1 = new FilterKey("ClassName");

			filterKeys.add(key);
			filterKeys.add(key1);

			logger.debug("List FilterKeys:" + filterKeys);
			return filterKeys;
		} catch (Throwable e) {
			logger.error("Error in getFilterKeys(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new AccountingManagerServiceException("No keys available!");

		}
	}

	public ArrayList<FilterValue> getFilterValues(
			FilterValuesRequest filterValuesRequest)
			throws AccountingManagerServiceException {
		try {
			logger.debug("getFilterValue(): [FilterValueRequest="
					+ filterValuesRequest + "]");
			if (filterValuesRequest == null
					|| filterValuesRequest.getAccountingType() == null
					|| filterValuesRequest.getFilterKey() == null) {
				return new ArrayList<FilterValue>();
			}

			ArrayList<FilterValue> filteValues = new ArrayList<FilterValue>();
			List<String> values;

			String[] vals = { "giancarlo.panichi", "gianpaolo.coro" };

			switch (filterValuesRequest.getAccountingType()) {
			case JOB:
				values = Arrays.asList(vals);
			case PORTLET:
				values = Arrays.asList(vals);
			case SERVICE:
				values = Arrays.asList(vals);
			case STORAGE:
				values = Arrays.asList(vals);
			case TASK:
				values = Arrays.asList(vals);
			default:
				values = Arrays.asList(vals);
			}
			for (String value : values) {
				if (value != null && !value.isEmpty()) {
					filteValues.add(new FilterValue(value));
				}
			}

			return filteValues;
		} catch (Throwable e) {
			logger.error("Error in getFilterValues(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new AccountingManagerServiceException("No values available!");

		}
	}

	public SeriesResponse getSeries(AccountingType accountingType,
			SeriesRequest seriesRequest)
			throws AccountingManagerServiceException {
		try {
			logger.debug("getSeries(): [AccountingType=" + accountingType
					+ " , seriesRequest=" + seriesRequest + "]");
			SeriesResponse seriesResponse = null;
			switch (accountingType) {
			case JOB:
				SeriesJobDefinition serieJobDefinition = null;
				if (seriesRequest.getAccountingFilterDefinition()
						.getChartType() != null) {
					ArrayList<SeriesJobData> seriesJobData = new ArrayList<>();
					for (int i = 0; i < 30; i++) {
						SeriesJobData data = new SeriesJobData(new Date(),
								new Double(10 * Math.random()).longValue(),
								new Double(10 * Math.random()).longValue(),
								new Double(10 * Math.random()).longValue(),
								new Double(100 * Math.random()).longValue());
						seriesJobData.add(data);

					}
					switch (seriesRequest.getAccountingFilterDefinition()
							.getChartType()) {
					case Basic:
						serieJobDefinition = new SeriesJobBasic(seriesJobData);
						break;
					case Top:
						AccountingFilterTop accountingFilterTop = (AccountingFilterTop) seriesRequest
								.getAccountingFilterDefinition();

						ArrayList<SeriesJobDataTop> seriesJobDataTopList = new ArrayList<>();
						for (int i = 0; i < accountingFilterTop.getTopNumber(); i++) {

							SeriesJobDataTop seriesJobDataTop = new SeriesJobDataTop(
									new FilterValue("User" + i), seriesJobData);
							seriesJobDataTopList.add(seriesJobDataTop);
						}
						serieJobDefinition = new SeriesJobTop(
								seriesJobDataTopList);
						break;
					default:
						break;

					}
				}

				seriesResponse = new SeriesJob(serieJobDefinition);

				break;
			case PORTLET:
				break;
			case SERVICE:
				SeriesServiceDefinition serieServiceDefinition = null;
				if (seriesRequest.getAccountingFilterDefinition()
						.getChartType() != null) {
					ArrayList<SeriesServiceData> seriesServiceData = new ArrayList<>();
					for (int i = 0; i < 30; i++) {
						SeriesServiceData data = new SeriesServiceData(
								new Date(),
								new Double(50 * Math.random()).longValue(),
								new Double(10000 * Math.random()).longValue(),
								new Double(10000 * Math.random()).longValue(),
								new Double(10000 * Math.random()).longValue());
						seriesServiceData.add(data);

					}
					switch (seriesRequest.getAccountingFilterDefinition()
							.getChartType()) {
					case Basic:
						serieServiceDefinition = new SeriesServiceBasic(
								seriesServiceData);
						break;
					case Top:

						AccountingFilterTop accountingFilterTop = (AccountingFilterTop) seriesRequest
								.getAccountingFilterDefinition();
						ArrayList<SeriesServiceDataTop> seriesServiceDataTopList = new ArrayList<>();

						for (int i = 0; i < accountingFilterTop.getTopNumber(); i++) {

							SeriesServiceDataTop seriesServiceDataTop1 = new SeriesServiceDataTop(
									new FilterValue("User" + i),
									seriesServiceData);
							seriesServiceDataTopList.add(seriesServiceDataTop1);
						}

						serieServiceDefinition = new SeriesServiceTop(
								seriesServiceDataTopList);
						break;
					default:
						break;

					}
				}

				seriesResponse = new SeriesService(serieServiceDefinition);

				break;
			case STORAGE:
				SeriesStorageDefinition serieStorageDefinition = null;
				if (seriesRequest.getAccountingFilterDefinition()
						.getChartType() != null) {
					ArrayList<SeriesStorageData> seriesStorageData = new ArrayList<>();
					for (int i = 0; i < 30; i++) {
						SeriesStorageData data = new SeriesStorageData(
								new Date(),
								new Double(1024*1024 * Math.random()).longValue(),
								new Double(100 * Math.random()).longValue());
						seriesStorageData.add(data);

					}
					switch (seriesRequest.getAccountingFilterDefinition()
							.getChartType()) {
					case Basic:
						serieStorageDefinition = new SeriesStorageBasic(
								seriesStorageData);
						break;
					case Top:
						AccountingFilterTop accountingFilterTop = (AccountingFilterTop) seriesRequest
								.getAccountingFilterDefinition();
						ArrayList<SeriesStorageDataTop> seriesStorageDataTopList = new ArrayList<>();
						for (int i = 0; i < accountingFilterTop.getTopNumber(); i++) {
							SeriesStorageDataTop seriesStorageDataTop = new SeriesStorageDataTop(
									new FilterValue("User"+i),
									seriesStorageData);
							seriesStorageDataTopList.add(seriesStorageDataTop);
						}
						serieStorageDefinition = new SeriesStorageTop(
								seriesStorageDataTopList);
						break;
					default:
						break;

					}
				}

				seriesResponse = new SeriesStorage(serieStorageDefinition);

				break;
			case TASK:
				break;
			default:
				break;

			}

			if (seriesResponse == null) {
				throw new AccountingManagerServiceException(
						"Error creating series response!");
			}
			logger.debug("SeriesResponse Created: " + seriesResponse);
			return seriesResponse;
		} catch (Throwable e) {
			logger.error("Error in GetSeries(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new AccountingManagerServiceException("No data available!");

		}
	}

}
