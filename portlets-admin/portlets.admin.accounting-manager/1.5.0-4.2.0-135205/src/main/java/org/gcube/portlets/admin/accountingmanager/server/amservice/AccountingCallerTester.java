package org.gcube.portlets.admin.accountingmanager.server.amservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingFilterTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesJob;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesService;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesStorage;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobDataContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobDefinition;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceDataContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceDefinition;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceTop;
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
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingCallerTester implements AccountingCallerInterface {
	private static Logger logger = LoggerFactory
			.getLogger(AccountingCallerTester.class);

	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMMMM dd");

	public AccountingCallerTester() {
		super();
	}

	public ArrayList<FilterKey> getFilterKeys(AccountingType accountingType)
			throws ServiceException {
		try {
			logger.debug("getFilterKeys(): [AccountingType=" + accountingType
					+ "]");
			if (accountingType == null) {
				return new ArrayList<FilterKey>();
			}
			ArrayList<FilterKey> filterKeys = new ArrayList<FilterKey>();
			
			FilterKey key = new FilterKey("ConsumerId");
			filterKeys.add(key);
			
			key = new FilterKey("ClassName");
			filterKeys.add(key);
			
			for (int i = 0; i < 20; i++) {
				key = new FilterKey("ServiceName" + i);
				filterKeys.add(key);
			}

			logger.debug("List FilterKeys:" + filterKeys);
			return filterKeys;
		} catch (Throwable e) {
			logger.error("Error in getFilterKeys(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException("No keys available!");

		}
	}

	public ArrayList<FilterValue> getFilterValues(
			FilterValuesRequest filterValuesRequest) throws ServiceException {
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
			throw new ServiceException("No values available!");

		}
	}

	public SeriesResponse getSeries(AccountingType accountingType,
			SeriesRequest seriesRequest) throws ServiceException {
		try {
			logger.debug("getSeries(): [AccountingType=" + accountingType
					+ " , seriesRequest=" + seriesRequest + "]");
			SeriesResponse seriesResponse = null;

			Calendar startCalendar = GregorianCalendar
					.getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);

			try {
				startCalendar.setTime(sdf.parse(seriesRequest
						.getAccountingPeriod().getStartDate()));
			} catch (ParseException e) {
				e.printStackTrace();
				throw new ServiceException("Start Date not valid!");
			}

			Calendar endCalendar = GregorianCalendar
					.getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);

			// GregorianCalendar endCalendar = new GregorianCalendar();
			// .getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);

			// GregorianCalendar.getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);
			try {
				endCalendar.setTime(sdf.parse(seriesRequest
						.getAccountingPeriod().getEndDate()));
			} catch (ParseException e) {
				e.printStackTrace();
				throw new ServiceException("End Date not valid!");
			}

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
						if (accountingFilterTop.getShowOthers()) {
							for (int i = 0; i < 40; i++) {

								SeriesJobDataTop seriesJobDataTop = new SeriesJobDataTop(
										new FilterValue("User" + i),
										seriesJobData);
								seriesJobDataTopList.add(seriesJobDataTop);
							}
						} else {
							for (int i = 0; i < accountingFilterTop
									.getTopNumber(); i++) {

								SeriesJobDataTop seriesJobDataTop = new SeriesJobDataTop(
										new FilterValue("User" + i),
										seriesJobData);
								seriesJobDataTopList.add(seriesJobDataTop);
							}
						}

						serieJobDefinition = new SeriesJobTop(
								accountingFilterTop.getShowOthers(),
								accountingFilterTop.getTopNumber(),
								seriesJobDataTopList);
						break;
					case Context:
						// AccountingFilterContext accountingFilterContext =
						// (AccountingFilterContext) seriesRequest
						// .getAccountingFilterDefinition();
						ArrayList<SeriesJobDataContext> seriesJobDataContextList = new ArrayList<>();
						ArrayList<String> contexts = new ArrayList<>();

						for (int i = 0; i < 20; i++) {
							String scope = new String("VRE " + i);
							contexts.add(scope);
							SeriesJobDataContext seriesJobDataContext = new SeriesJobDataContext(
									scope, seriesJobData);
							seriesJobDataContextList.add(seriesJobDataContext);
						}

						Context context = new Context(contexts);
						serieJobDefinition = new SeriesJobContext(context,
								seriesJobDataContextList);

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
					switch (seriesRequest.getAccountingPeriod().getPeriod()) {
					case DAILY:
					case HOURLY:
					case MINUTELY:
						for (int i = 0; i < 30; i++) {
							SeriesServiceData data = new SeriesServiceData(
									new Date(),
									new Double(50 * Math.random()).longValue(),
									new Double(10000 * Math.random())
											.longValue(), new Double(
											10000 * Math.random()).longValue(),
									new Double(10000 * Math.random())
											.longValue());
							seriesServiceData.add(data);
						}
						break;
					case MONTHLY:
						while (startCalendar.compareTo(endCalendar) <= 0) {
							SeriesServiceData data = new SeriesServiceData(
									startCalendar.getTime(), new Double(
											50 * Math.random()).longValue(),
									new Double(10000 * Math.random())
											.longValue(), new Double(
											10000 * Math.random()).longValue(),
									new Double(10000 * Math.random())
											.longValue());
							seriesServiceData.add(data);
							startCalendar.add(Calendar.MONTH, 1);
						}
						break;
					case YEARLY:
						while (startCalendar.compareTo(endCalendar) <= 0) {
							SeriesServiceData data = new SeriesServiceData(
									startCalendar.getTime(), new Double(
											50 * Math.random()).longValue(),
									new Double(10000 * Math.random())
											.longValue(), new Double(
											10000 * Math.random()).longValue(),
									new Double(10000 * Math.random())
											.longValue());
							seriesServiceData.add(data);
							startCalendar.add(Calendar.YEAR, 1);
						}
						break;
					default:
						break;

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

						if (accountingFilterTop.getShowOthers()) {

							try {
								startCalendar.setTime(sdf.parse(seriesRequest
										.getAccountingPeriod().getStartDate()));
							} catch (ParseException e) {
								e.printStackTrace();
								throw new ServiceException(
										"Start Date not valid!");
							}

							try {
								endCalendar.setTime(sdf.parse(seriesRequest
										.getAccountingPeriod().getEndDate()));
							} catch (ParseException e) {
								e.printStackTrace();
								throw new ServiceException(
										"End Date not valid!");
							}

							int k = 0;

							switch (seriesRequest.getAccountingPeriod()
									.getPeriod()) {
							case DAILY:
							case HOURLY:
							case MINUTELY:
								for (int i = 0; i < 40; i++) {

									SeriesServiceDataTop seriesServiceDataTop1 = new SeriesServiceDataTop(
											new FilterValue("User" + i),
											seriesServiceData);
									seriesServiceDataTopList
											.add(seriesServiceDataTop1);
								}
								break;
							case MONTHLY:
								k = 0;
								while (startCalendar.compareTo(endCalendar) <= 0) {
									SeriesServiceDataTop seriesServiceDataTop1 = new SeriesServiceDataTop(
											new FilterValue("User" + k),
											seriesServiceData);
									seriesServiceDataTopList
											.add(seriesServiceDataTop1);
									startCalendar.add(Calendar.MONTH, 1);
									k++;
								}
								break;
							case YEARLY:
								k = 0;
								while (startCalendar.compareTo(endCalendar) <= 0) {
									SeriesServiceDataTop seriesServiceDataTop1 = new SeriesServiceDataTop(
											new FilterValue("User" + k),
											seriesServiceData);
									seriesServiceDataTopList
											.add(seriesServiceDataTop1);
									startCalendar.add(Calendar.YEAR, 1);
									k++;
								}
								break;
							default:
								break;
							}
						} else {

							for (int i = 0; i < accountingFilterTop
									.getTopNumber(); i++) {

								SeriesServiceDataTop seriesServiceDataTop1 = new SeriesServiceDataTop(
										new FilterValue("User" + i),
										seriesServiceData);
								seriesServiceDataTopList
										.add(seriesServiceDataTop1);
							}
						}

						serieServiceDefinition = new SeriesServiceTop(
								accountingFilterTop.getShowOthers(),
								accountingFilterTop.getTopNumber(),
								seriesServiceDataTopList);
						break;
					case Context:
						// AccountingFilterContext accountingFilterContext =
						// (AccountingFilterContext) seriesRequest
						// .getAccountingFilterDefinition();
						ArrayList<SeriesServiceDataContext> seriesServiceDataContextList = new ArrayList<>();
						ArrayList<String> contexts = new ArrayList<>();

						for (int i = 0; i < 20; i++) {
							String scope = new String("VRE " + i);
							contexts.add(scope);
							SeriesServiceDataContext seriesServiceDataContext = new SeriesServiceDataContext(
									scope, seriesServiceData);
							seriesServiceDataContextList
									.add(seriesServiceDataContext);
						}

						Context context = new Context(contexts);
						serieServiceDefinition = new SeriesServiceContext(
								context, seriesServiceDataContextList);

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

						// Valori variabili
						SeriesStorageData data = new SeriesStorageData(
								new Date(),
								new Double(1024 * 1024 * Math.random())
										.longValue(), new Double(
										100 * Math.random()).longValue());

						// Valori Fissi
						/*
						 * SeriesStorageData data = new SeriesStorageData( new
						 * Date(), new Double(1024 * 1024) .longValue(), new
						 * Double( 100).longValue());
						 */

						seriesStorageData.add(data);

					}
					switch (seriesRequest.getAccountingFilterDefinition()
							.getChartType()) {
					case Basic:
						for (SeriesStorageData serieStorageData : seriesStorageData) {
							serieStorageData.setDataVolume(serieStorageData
									.getDataVolume() * 1000);
							serieStorageData.setOperationCount(serieStorageData
									.getOperationCount() * 1000);
						}

						serieStorageDefinition = new SeriesStorageBasic(
								seriesStorageData);
						break;
					case Top:
						AccountingFilterTop accountingFilterTop = (AccountingFilterTop) seriesRequest
								.getAccountingFilterDefinition();
						ArrayList<SeriesStorageDataTop> seriesStorageDataTopList = new ArrayList<>();
						if (accountingFilterTop.getShowOthers()) {
							for (int i = 0; i < 1000; i++) {
								SeriesStorageDataTop seriesStorageDataTop = new SeriesStorageDataTop(
										new FilterValue("User" + i),
										seriesStorageData);
								seriesStorageDataTopList
										.add(seriesStorageDataTop);
							}
						} else {

							for (int i = 0; i < accountingFilterTop
									.getTopNumber(); i++) {
								SeriesStorageDataTop seriesStorageDataTop = new SeriesStorageDataTop(
										new FilterValue("User" + i),
										seriesStorageData);
								seriesStorageDataTopList
										.add(seriesStorageDataTop);
							}
						}
						serieStorageDefinition = new SeriesStorageTop(
								accountingFilterTop.getShowOthers(),
								accountingFilterTop.getTopNumber(),
								seriesStorageDataTopList);
						break;
					case Context:
						// AccountingFilterContext accountingFilterContext =
						// (AccountingFilterContext) seriesRequest
						// .getAccountingFilterDefinition();
						ArrayList<SeriesStorageDataContext> seriesStorageDataContextList = new ArrayList<>();
						ArrayList<String> contexts = new ArrayList<>();

						for (int i = 0; i < 20; i++) {
							String scope = new String("VRE " + i);
							contexts.add(scope);
							SeriesStorageDataContext seriesStorageDataContext = new SeriesStorageDataContext(
									scope, seriesStorageData);
							seriesStorageDataContextList
									.add(seriesStorageDataContext);
						}

						Context context = new Context(contexts);
						serieStorageDefinition = new SeriesStorageContext(
								context, seriesStorageDataContextList);

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
				throw new ServiceException("Error creating series response!");
			}
			logger.debug("SeriesResponse Created: " + seriesResponse);
			return seriesResponse;
		} catch (Throwable e) {
			logger.error("Error in GetSeries(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException("No data available!");

		}
	}
}
