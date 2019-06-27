package org.gcube.portlets.user.accountingdashboard.server.accounting;

import java.time.Instant;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.gcube.accounting.accounting.summary.access.AccountingDao;
import org.gcube.accounting.accounting.summary.access.model.MeasureResolution;
import org.gcube.accounting.accounting.summary.access.model.Report;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.portlets.user.accountingdashboard.shared.Constants;
import org.gcube.portlets.user.accountingdashboard.shared.data.RecordData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ReportElementData;
import org.gcube.portlets.user.accountingdashboard.shared.data.RequestReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ScopeData;
import org.gcube.portlets.user.accountingdashboard.shared.data.SeriesData;
import org.gcube.portlets.user.accountingdashboard.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class AccountingService {

	private static Logger logger = LoggerFactory.getLogger(AccountingService.class);

	private AccountingDao dao;

	public AccountingService(AccountingServiceType accountingServiceType) throws ServiceException {
		try {
			if (Constants.DEBUG_MODE) {
				return;
			}

			if (accountingServiceType == null) {
				logger.error("Invalid AccountingServiceType requested: null");
			}

			switch (accountingServiceType) {
			case CurrentScope:
				logger.debug("AccountingService: CurrentScope");
				dao = AccountingDao.get();
				break;
			case PortalContex:
				logger.debug("AccountingService: PortalContext");
				dao = AccountingDao.get(new PortalContextTreeProvider());
				break;
			default:
				logger.debug("AccountingService: CurrentScope");
				dao = AccountingDao.get();
				break;

			}

		} catch (Throwable e) {
			logger.error("Error retrieving Tree: " + e.getLocalizedMessage(), e);
			throw new ServiceException("Error retrieving Tree: " + e.getLocalizedMessage(), e);
		}

	}

	public ScopeData getTree(HttpServletRequest httpServletRequest) throws ServiceException {
		try {
			if (Constants.DEBUG_MODE) {
				return debugScope();
			}
			logger.debug("AccountingService GetTree()");
			ScopeDescriptor scopeDescriptor = dao.getTree(httpServletRequest);
			logger.debug("ScopeDescriptor: " + scopeDescriptor);
			ScopeData scopeData = getScopeData(scopeDescriptor, "");
			logger.debug("ScopeData: " + scopeData);
			return scopeData;

		} catch (Throwable e) {
			logger.error("Error retrieving Tree: " + e.getLocalizedMessage(), e);
			throw new ServiceException("Error retrieving Tree: " + e.getLocalizedMessage(), e);
		}
	}

	private ScopeData getScopeData(ScopeDescriptor scopeDescriptor, String parentScope) {
		ScopeData scopeData = null;
		if (scopeDescriptor != null) {
			if (scopeDescriptor.hasChildren()) {
				ArrayList<ScopeData> childs = new ArrayList<>();
				for (ScopeDescriptor sd : scopeDescriptor.getChildren()) {
					childs.add(getScopeData(sd, parentScope + "/" + scopeDescriptor.getName()));
				}
				scopeData = new ScopeData(scopeDescriptor.getId(), scopeDescriptor.getName(),
						parentScope + "/" + scopeDescriptor.getName(), childs);
			} else {
				scopeData = new ScopeData(scopeDescriptor.getId(), scopeDescriptor.getName(),
						parentScope + "/" + scopeDescriptor.getName(), null);
			}
		}
		return scopeData;

	}

	public ReportData getReport(HttpServletRequest httpServletRequest, RequestReportData requestReportData)
			throws ServiceException {
		try {
			if (Constants.DEBUG_MODE) {
				return debugReport();
			}

			logger.debug("AccountingService GetReport(): " + requestReportData);
			if (requestReportData != null && requestReportData.getScopeData() != null
					&& requestReportData.getScopeData().getScope() != null
					&& !requestReportData.getScopeData().getScope().isEmpty()) {

				ScopeDescriptor scopeDescriptor = searchScopeDescriptor(httpServletRequest,
						requestReportData.getScopeData());

				String dateStart = requestReportData.getDateFrom();
				String dateEnd = requestReportData.getDateTo();

				if (dateStart == null || dateStart.isEmpty() || dateEnd == null || dateEnd.isEmpty()) {
					logger.error("Invalid date: [dateStart=" + dateStart + ", dateEnd=" + dateEnd + "]");
					throw new ServiceException(
							"Invalid format: [dateStart=" + dateStart + ", dateEnd=" + dateEnd + "]");
				}

				Instant dateFrom;
				Instant dateTo;
				try {
					
					dateFrom=Instant.parse(dateStart+"T00:00:00.00Z");
										
					dateTo=Instant.parse(dateEnd+"T00:00:00.00Z");
					
				} catch (Throwable e) {
					logger.error("Invalid date format: [dateStart=" + dateStart + ", dateEnd=" + dateEnd + "]");
					throw new ServiceException(
							"Invalid date format: [dateStart=" + dateStart + ", dateEnd=" + dateEnd + "]");

				}

				logger.debug("getReportByScope(): [ScopeDescriptor=" + scopeDescriptor + ", dateFrom="
						+ dateFrom + ", dateTo=" + dateTo + ", measureResolution="
						+ MeasureResolution.MONTHLY + "]");
				Report report = dao.getReportByScope(scopeDescriptor, dateFrom, dateTo,
						MeasureResolution.MONTHLY);

				logger.debug("Report: " + report);
				ReportDataBuilder reportDataBuilder = new ReportDataBuilder(report);
				ReportData reportData = reportDataBuilder.build();
				return reportData;
			} else {
				throw new ServiceException("Invalid report request, " + requestReportData);
			}

		} catch (Throwable e) {
			logger.error("Error in create report: " + e.getLocalizedMessage(), e);
			throw new ServiceException("Error in create report: " + e.getLocalizedMessage(), e);
		}
	}

	private ScopeDescriptor searchScopeDescriptor(HttpServletRequest httpServletRequest, ScopeData scopeData)
			throws ServiceException {
		try {
			logger.debug("SearchScopeDescirptor(): ScopeData=" + scopeData);
			ScopeDescriptor scopeDescriptor = dao.getTree(httpServletRequest);
			logger.debug("Service ScopeDescriptor: " + scopeDescriptor);
			String scopeToSearch = scopeData.getScope();
			logger.debug("Scope request: " + scopeToSearch);
			String[] searchPath = scopeToSearch.split("\\/");
			if (searchPath == null || searchPath.length == 0) {
				logger.error("Error searching scope descriptor: scope=" + searchPath);
				throw new ServiceException("Scope=" + searchPath);
			}
			logger.debug("Search path: " + searchPath);
			if (searchPath.length > 1) {
				int i = 1;
				String segment = searchPath[i];
				if (scopeDescriptor.getName().compareTo(segment) == 0) {
					i++;
					if (i < searchPath.length) {
						return searchInChild(scopeDescriptor, scopeToSearch, searchPath, i);
					} else {
						return scopeDescriptor;
					}
				} else {
					throw new ServiceException("Scope descriptor not found: " + scopeToSearch);
				}
			} else {
				throw new ServiceException("Scope descriptor not found: " + scopeToSearch);
			}

		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error("Error searching scope descriptor: " + e.getLocalizedMessage(), e);
			throw new ServiceException("Error searching scope descriptor: " + e.getLocalizedMessage(), e);
		}
	}

	private ScopeDescriptor searchInChild(ScopeDescriptor scopeDescriptor, String scopeToSearch, String[] searchPath,
			int i) throws ServiceException {
		try {
			for (ScopeDescriptor child : scopeDescriptor.getChildren()) {
				if (child.getName() != null && !child.getName().isEmpty()
						&& child.getName().compareTo(searchPath[i]) == 0) {
					i++;
					if (i < searchPath.length) {
						return searchInChild(child, scopeToSearch, searchPath, i);
					} else {
						return child;
					}
				}
			}
			throw new ServiceException("Scope descriptor not found: " + scopeToSearch);

		} catch (ServiceException e) {
			throw e;
		} catch (Throwable e) {
			logger.error("Error searching scope descriptor: " + e.getLocalizedMessage(), e);
			throw new ServiceException("Error searching scope descriptor: " + e.getLocalizedMessage(), e);
		}
	}

	private ScopeData debugScope() {
		ArrayList<ScopeData> children=new ArrayList<>();
		ScopeData child1=new ScopeData("Child1","Child1","/Debug/ChildChild1",null);
		ScopeData child2=new ScopeData("Child2","Child2","/Debug/ChildChild2",null);
		children.add(child1);
		children.add(child2);
		ScopeData scopeData = new ScopeData("Debug", "Debug", "/Debug", children);
		return scopeData;
	}

	private ReportData debugReport() {

		RecordData recordData1 = new RecordData();
		recordData1.setX("January");
		recordData1.setY(3d);

		RecordData recordData2 = new RecordData();
		recordData2.setX("February");
		recordData2.setY(2d);

		RecordData recordData3 = new RecordData();
		recordData3.setX("March");
		recordData3.setY(4d);

		RecordData[] dataRow1 = new RecordData[3];
		dataRow1[0] = recordData1;
		dataRow1[1] = recordData2;
		dataRow1[2] = recordData3;

		RecordData recordData4 = new RecordData();
		recordData4.setX("January");
		recordData4.setY(1d);

		RecordData recordData5 = new RecordData();
		recordData5.setX("February");
		recordData5.setY(5d);

		RecordData recordData6 = new RecordData();
		recordData6.setX("March");
		recordData6.setY(2d);

		RecordData[] dataRow2 = new RecordData[3];
		dataRow2[0] = recordData4;
		dataRow2[1] = recordData5;
		dataRow2[2] = recordData6;

		SeriesData seriesData1 = new SeriesData();
		seriesData1.setLabel("Series1");
		seriesData1.setDataRow(dataRow1);

		SeriesData seriesData2 = new SeriesData();
		seriesData2.setLabel("Series2");
		seriesData2.setDataRow(dataRow2);

		SeriesData[] serieses = new SeriesData[2];
		serieses[0] = seriesData1;
		serieses[1] = seriesData2;

		ReportData reportData1 = new ReportData();
		ArrayList<ReportElementData> elements = new ArrayList<>();

		ReportElementData reportElementData1 = new ReportElementData();
		reportElementData1.setxAxis("XAxis");
		reportElementData1.setyAxis("YAxis");
		reportElementData1.setCategory("Category1");
		reportElementData1.setLabel("Label1");
		reportElementData1.setSerieses(serieses);
		elements.add(reportElementData1);

		ReportElementData reportElementData2 = new ReportElementData();
		reportElementData2.setxAxis("XAxis");
		reportElementData2.setyAxis("YAxis");
		reportElementData2.setCategory("Category2");
		reportElementData2.setLabel("Label2");
		reportElementData2.setSerieses(serieses);
		elements.add(reportElementData2);

		reportData1.setElements(elements);

		return reportData1;

	}

}
