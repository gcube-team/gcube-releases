/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.td.gwtservice.server.file.CSVFileUploadSession;
import org.gcube.portlets.user.td.gwtservice.server.file.CodelistMappingFileUploadSession;
import org.gcube.portlets.user.td.gwtservice.server.trservice.TRTasksManager;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartTopRatingSession;
import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistSession;
import org.gcube.portlets.user.td.gwtservice.shared.file.FileUploadMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.geometry.GeometryCreatePointSession;
import org.gcube.portlets.user.td.gwtservice.shared.geospatial.GeospatialCreateCoordinatesSession;
import org.gcube.portlets.user.td.gwtservice.shared.geospatial.GeospatialDownscaleCSquareSession;
import org.gcube.portlets.user.td.gwtservice.shared.history.RollBackSession;
import org.gcube.portlets.user.td.gwtservice.shared.i18n.InfoLocale;
import org.gcube.portlets.user.td.gwtservice.shared.json.JSONExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.map.MapCreationSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyAndDetachColumnRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyTableRuleSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.DetachColumnRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.DetachTableRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.source.SDMXRegistrySource;
import org.gcube.portlets.user.td.gwtservice.shared.statistical.StatisticalOperationSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskResubmitSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskResumeSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskWrapper;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateApplySession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ReplaceBatchColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.clone.CloneTabularResourceSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.AddColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ChangeColumnsPositionSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.DeleteColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.FilterColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.LabelColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.MergeColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnByExpressionSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.ReplaceColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.SplitColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.type.ChangeColumnTypeSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.groupby.GroupBySession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.groupby.TimeAggregationSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.normalization.DenormalizationSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.normalization.NormalizationSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.open.TDOpenSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.SaveResourceSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.DeleteRowsSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.DuplicatesSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.EditRowSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.ChangeTableTypeSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Agencies;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Codelist;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Dataset;
import org.gcube.portlets.user.td.gwtservice.shared.tr.union.UnionSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SessionUtil {

	private static Logger logger = LoggerFactory.getLogger(SessionUtil.class);

	public static ASLSession getAslSession(HttpSession httpSession)
			throws TDGWTSessionExpiredException {
		String username = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession aslSession;
		if (username == null) {
			InfoLocale infoLocale = getInfoLocale(httpSession);
			Locale locale = new Locale(infoLocale.getLanguage());
			ResourceBundle messages = ResourceBundle.getBundle(
					TDGWTServiceMessagesConstants.TDGWTServiceMessages, locale);

			if (Constants.DEBUG_MODE) {
				logger.info("no user found in session, use test user");
				// Remove comment for Test
				username = Constants.DEFAULT_USER;
				String scope = Constants.DEFAULT_SCOPE;

				httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE,
						username);
				aslSession = SessionManager.getInstance().getASLSession(
						httpSession.getId(), username);
				aslSession.setScope(scope);
			} else {
				logger.info("no user found in session, use test user");
				throw new TDGWTSessionExpiredException(
						messages.getString(TDGWTServiceMessagesConstants.sessionExpired));

			}
		} else {
			aslSession = SessionManager.getInstance().getASLSession(
					httpSession.getId(), username);

		}

		logger.info("SessionUtil: aslSession " + aslSession.getUsername() + " "
				+ aslSession.getScope());

		return aslSession;
	}

	public static String getToken(ASLSession aslSession) {
		String token = null;
		if (Constants.DEBUG_MODE) {
			List<String> userRoles = new ArrayList<>();
			userRoles.add(Constants.DEFAULT_ROLE);

			token = null;// authorizationService().build().generate(
			// aslSession.getUsername(), userRoles);

		} else {
			token = aslSession.getSecurityToken();
		}
		logger.info("received token: " + token);
		return token;

	}

	//
	public static InfoLocale getInfoLocale(HttpSession httpSession) {
		InfoLocale infoLocale = (InfoLocale) httpSession
				.getAttribute(SessionConstants.INFO_LOCALE);
		if (infoLocale != null) {
			return infoLocale;
		} else {
			infoLocale = new InfoLocale("en");
			httpSession.setAttribute(SessionConstants.INFO_LOCALE, infoLocale);
			return infoLocale;
		}
	}

	public static void setInfoLocale(HttpSession httpSession,
			InfoLocale infoLocale) {
		InfoLocale infoL = (InfoLocale) httpSession
				.getAttribute(SessionConstants.INFO_LOCALE);
		if (infoL != null) {
			httpSession.removeAttribute(SessionConstants.INFO_LOCALE);
		}
		httpSession.setAttribute(SessionConstants.INFO_LOCALE, infoLocale);

	}

	/**
	 * Set in session the Tabular Resource List retrieved from service only for
	 * caching
	 * 
	 * @param httpSession
	 * @param trs
	 * @throws TDGWTSessionExpiredException
	 */
	public static void setTabularResources(HttpSession httpSession,
			List<TabularResource> trs) throws TDGWTSessionExpiredException {

		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, List<TabularResource>> scopeToTabularResourcesMap = (HashMap<String, List<TabularResource>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_TABULAR_RESOURCE_LIST_MAP);

		if (scopeToTabularResourcesMap == null) {
			scopeToTabularResourcesMap = new HashMap<String, List<TabularResource>>();
			scopeToTabularResourcesMap.put(aslSession.getScope(), trs);
		} else {
			scopeToTabularResourcesMap.put(aslSession.getScope(), trs);
			httpSession
					.removeAttribute(SessionConstants.SCOPE_TO_TABULAR_RESOURCE_LIST_MAP);

		}
		httpSession.setAttribute(
				SessionConstants.SCOPE_TO_TABULAR_RESOURCE_LIST_MAP,
				scopeToTabularResourcesMap);

	}

	/**
	 * Set current TRId in current scope
	 * 
	 * @param httpSession
	 * @return
	 * @throws TDGWTSessionExpiredException
	 */
	public static TRId getTRId(HttpSession httpSession)
			throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, TRId> scopeToTRIdMap = (HashMap<String, TRId>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TRID_MAP);

		TRId id = null;
		if (scopeToTRIdMap == null) {
			logger.error("TR_ID was not acquired");
		} else {
			id = scopeToTRIdMap.get(aslSession.getScope());
			if (id == null) {
				logger.error("TR_ID was not acquired");
			}
		}
		return id;
	}

	/**
	 * 
	 * @param httpSession
	 * @param trId
	 * @throws TDGWTSessionExpiredException
	 */
	public static void setTRId(HttpSession httpSession, TRId trId)
			throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, TRId> scopeToTRIdMap = (HashMap<String, TRId>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TRID_MAP);

		if (scopeToTRIdMap == null) {
			scopeToTRIdMap = new HashMap<String, TRId>();
			scopeToTRIdMap.put(aslSession.getScope(), trId);
		} else {
			scopeToTRIdMap.put(aslSession.getScope(), trId);
			httpSession
					.removeAttribute(SessionConstants.SCOPE_TO_CURRENT_TRID_MAP);
		}
		httpSession.setAttribute(SessionConstants.SCOPE_TO_CURRENT_TRID_MAP,
				scopeToTRIdMap);
		return;

	}

	/**
	 * 
	 * @param httpSession
	 * @return
	 * @throws TDGWTSessionExpiredException
	 */
	public static TabResource getTabResource(HttpSession httpSession)
			throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, TabResource> scopeToTabResourceMap = (HashMap<String, TabResource>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCE_MAP);

		TabResource tabResource = null;
		if (scopeToTabResourceMap == null) {
			logger.error("CURRENT_TABULAR_RESOURCE was not acquired");
		} else {
			tabResource = scopeToTabResourceMap.get(aslSession.getScope());
			if (tabResource == null) {
				logger.error("CURRENT_TABULAR_RESOURCE was not acquired");
			}
		}
		return tabResource;

	}

	/**
	 * 
	 * @param httpSession
	 * @param tabResource
	 * @throws TDGWTSessionExpiredException
	 */
	public static void setTabResource(HttpSession httpSession,
			TabResource tabResource) throws TDGWTSessionExpiredException {

		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, TabResource> scopeToTabResourceMap = (HashMap<String, TabResource>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCE_MAP);

		if (scopeToTabResourceMap == null) {
			scopeToTabResourceMap = new HashMap<String, TabResource>();
			scopeToTabResourceMap.put(aslSession.getScope(), tabResource);
		} else {
			scopeToTabResourceMap.put(aslSession.getScope(), tabResource);
			httpSession
					.removeAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCE_MAP);
		}
		httpSession.setAttribute(
				SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCE_MAP,
				scopeToTabResourceMap);
		return;

	}

	//
	/**
	 * 
	 * @param httpSession
	 * @return
	 * @throws TDGWTSessionExpiredException
	 */
	public static ArrayList<TabResource> getCurrentTabularResourcesOpen(
			HttpSession httpSession) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, ArrayList<TabResource>> scopeToTabResourceOpenMap = (HashMap<String, ArrayList<TabResource>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);

		ArrayList<TabResource> currentTROpen = null;
		if (scopeToTabResourceOpenMap == null) {
			logger.error("CURRENT_TABULAR_RESOURCES_OPEN was not acquired");
		} else {
			currentTROpen = scopeToTabResourceOpenMap
					.get(aslSession.getScope());
			if (currentTROpen == null) {
				logger.error("CURRENT_TABULAR_RESOURCES_OPEN was not acquired");
			}
		}
		return currentTROpen;
	}

	/**
	 * 
	 * @param httpSession
	 * @param currentTROpen
	 * @throws TDGWTSessionExpiredException
	 */
	public static void setCurrentTabularResourcesOpen(HttpSession httpSession,
			ArrayList<TabResource> currentTROpen)
			throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, ArrayList<TabResource>> scopeToTabResourceOpenMap = (HashMap<String, ArrayList<TabResource>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);

		if (scopeToTabResourceOpenMap == null) {
			scopeToTabResourceOpenMap = new HashMap<String, ArrayList<TabResource>>();
			scopeToTabResourceOpenMap.put(aslSession.getScope(), currentTROpen);
		} else {
			scopeToTabResourceOpenMap.put(aslSession.getScope(), currentTROpen);
			httpSession
					.removeAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);
		}
		httpSession.setAttribute(
				SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP,
				scopeToTabResourceOpenMap);
		return;
	}

	/**
	 * 
	 * @param httpSession
	 * @param tabResource
	 * @throws TDGWTSessionExpiredException
	 */
	public static void addToCurrentTabularResourcesOpen(
			HttpSession httpSession, TabResource tabResource)
			throws TDGWTSessionExpiredException {

		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		if (tabResource.getTrId() != null
				&& tabResource.getTrId().getId() != null
				& !tabResource.getTrId().getId().isEmpty()) {

			@SuppressWarnings("unchecked")
			HashMap<String, ArrayList<TabResource>> scopeToTabResourceOpenMap = (HashMap<String, ArrayList<TabResource>>) httpSession
					.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);

			if (scopeToTabResourceOpenMap == null) {
				scopeToTabResourceOpenMap = new HashMap<String, ArrayList<TabResource>>();
				ArrayList<TabResource> openList = new ArrayList<TabResource>();
				openList.add(tabResource);
				scopeToTabResourceOpenMap.put(aslSession.getScope(), openList);
			} else {
				ArrayList<TabResource> openList = scopeToTabResourceOpenMap
						.get(aslSession.getScope());

				if (openList != null) {
					for (TabResource tr : openList) {
						if (tr.getTrId().getId()
								.compareTo(tabResource.getTrId().getId()) == 0) {
							openList.remove(tr);
							break;
						}
					}
					openList.add(tabResource);
				} else {
					openList = new ArrayList<TabResource>();
					openList.add(tabResource);
				}
				scopeToTabResourceOpenMap.put(aslSession.getScope(), openList);
				httpSession
						.removeAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);
			}
			httpSession
					.setAttribute(
							SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP,
							scopeToTabResourceOpenMap);
		} else {
			logger.error("Tabular Resource has invalid id");
		}

	}

	/**
	 * 
	 * @param httpSession
	 * @param trId
	 * @throws TDGWTSessionExpiredException
	 */
	public static void removeFromCurrentTabularResourcesOpen(
			HttpSession httpSession, TRId trId)
			throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		if (trId != null && trId.getId() != null & !trId.getId().isEmpty()) {

			@SuppressWarnings("unchecked")
			HashMap<String, ArrayList<TabResource>> scopeToTabResourceOpenMap = (HashMap<String, ArrayList<TabResource>>) httpSession
					.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);

			if (scopeToTabResourceOpenMap == null) {
				scopeToTabResourceOpenMap = new HashMap<String, ArrayList<TabResource>>();
				ArrayList<TabResource> openList = new ArrayList<TabResource>();
				scopeToTabResourceOpenMap.put(aslSession.getScope(), openList);
				logger.debug("No open tr list in session");
			} else {
				ArrayList<TabResource> openList = scopeToTabResourceOpenMap
						.get(aslSession.getScope());
				if (openList != null) {
					for (TabResource tr : openList) {
						if (tr.getTrId().getId().compareTo(trId.getId()) == 0) {
							openList.remove(tr);
							break;
						}
					}

				} else {
					openList = new ArrayList<TabResource>();
				}
				scopeToTabResourceOpenMap.put(aslSession.getScope(), openList);
				httpSession
						.removeAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);
			}
			httpSession
					.setAttribute(
							SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP,
							scopeToTabResourceOpenMap);
		} else {
			logger.error("TRId request has invalid id");
		}
	}

	/**
	 * 
	 * @param httpSession
	 * @throws TDGWTSessionExpiredException
	 */
	public static void removeAllFromCurrentTabularResourcesOpen(
			HttpSession httpSession) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, ArrayList<TabResource>> scopeToTabResourceOpenMap = (HashMap<String, ArrayList<TabResource>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);

		if (scopeToTabResourceOpenMap == null) {
			logger.debug("No open tr list in session");
		} else {
			scopeToTabResourceOpenMap.remove(aslSession.getScope());
			httpSession
					.removeAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);
			httpSession
					.setAttribute(
							SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP,
							scopeToTabResourceOpenMap);
		}

	}

	/**
	 * 
	 * @param httpSession
	 * @return
	 * @throws TDGWTSessionExpiredException
	 */
	public static TabResource getFirstFromCurrentTabularResourcesOpen(
			HttpSession httpSession) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, ArrayList<TabResource>> scopeToTabResourceOpenMap = (HashMap<String, ArrayList<TabResource>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);

		if (scopeToTabResourceOpenMap == null) {
			logger.info("No open tr in session");
			return null;
		} else {
			ArrayList<TabResource> openList = scopeToTabResourceOpenMap
					.get(aslSession.getScope());
			if (openList == null || openList.isEmpty()) {
				logger.info("No open tr in session");
				return null;
			} else {
				return openList.get(0);
			}
		}

	}

	/**
	 * 
	 * @param httpSession
	 * @param trId
	 * @return
	 * @throws TDGWTSessionExpiredException
	 */
	public static TabResource getFromCurrentTabularResourcesOpen(
			HttpSession httpSession, TRId trId)
			throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, ArrayList<TabResource>> scopeToTabResourceOpenMap = (HashMap<String, ArrayList<TabResource>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);

		if (scopeToTabResourceOpenMap == null) {
			logger.info("No open tr in session");
			return null;
		} else {
			ArrayList<TabResource> openList = scopeToTabResourceOpenMap
					.get(aslSession.getScope());
			if (openList == null || openList.isEmpty()) {
				logger.info("No open tr in session");
			} else {
				logger.debug("Current Tabular Open In session: "
						+ aslSession.getScope() + ", trList: " + openList);
				for (TabResource tabResource : openList) {
					if (tabResource != null
							&& tabResource.getTrId() != null
							&& tabResource.getTrId().getId()
									.compareTo(trId.getId()) == 0) {
						return tabResource;
					}
				}
			}
		}
		return null;

	}

	//
	/**
	 * 
	 * @param httpSession
	 * @param s
	 * @throws TDGWTSessionExpiredException
	 */
	public static void setTDOpenSession(HttpSession httpSession, TDOpenSession s)
			throws TDGWTSessionExpiredException {

		TDOpenSession session = (TDOpenSession) httpSession
				.getAttribute(SessionConstants.TDOPEN_SESSION);
		if (session != null)
			httpSession.removeAttribute(SessionConstants.TDOPEN_SESSION);
		httpSession.setAttribute(SessionConstants.TDOPEN_SESSION, s);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());

	}

	public static void retrieveResources(HttpSession httpSession)
			throws Exception {

		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
		return;
	}

	//
	public static TabResource getSDMXImportTabResource(HttpSession httpSession) {
		TabResource tabResource = (TabResource) httpSession
				.getAttribute(SessionConstants.SDMX_IMPORT_TABULAR_RESOURCE);
		if (tabResource == null) {
			logger.error("SDMX_IMPORT_TABULAR_RESOURCE was not acquired");
		}
		return tabResource;
	}

	public static void setSDMXImportTabResource(HttpSession httpSession,
			TabResource tabResource) throws TDGWTSessionExpiredException {
		TabResource t = (TabResource) httpSession
				.getAttribute(SessionConstants.SDMX_IMPORT_TABULAR_RESOURCE);
		if (t != null)
			httpSession
					.removeAttribute(SessionConstants.SDMX_IMPORT_TABULAR_RESOURCE);
		httpSession.setAttribute(SessionConstants.SDMX_IMPORT_TABULAR_RESOURCE,
				tabResource);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
	}

	// //
	public static TabResource getCSVImportTabResource(HttpSession httpSession) {
		TabResource tabResource = (TabResource) httpSession
				.getAttribute(SessionConstants.CSV_IMPORT_TABULAR_RESOURCE);
		if (tabResource == null) {
			logger.error("CSV_IMPORT_TABULAR_RESOURCE was not acquired");
		}
		return tabResource;
	}

	public static void setCSVImportTabResource(HttpSession httpSession,
			TabResource tabResource) throws TDGWTSessionExpiredException {
		TabResource t = (TabResource) httpSession
				.getAttribute(SessionConstants.CSV_IMPORT_TABULAR_RESOURCE);
		if (t != null)
			httpSession
					.removeAttribute(SessionConstants.CSV_IMPORT_TABULAR_RESOURCE);
		httpSession.setAttribute(SessionConstants.CSV_IMPORT_TABULAR_RESOURCE,
				tabResource);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
	}

	// //

	public static void setSDMXImportSession(HttpSession httpSession,
			SDMXImportSession s) throws TDGWTSessionExpiredException {

		SDMXImportSession session = (SDMXImportSession) httpSession
				.getAttribute(SessionConstants.SDMX_IMPORT_SESSION);
		if (session != null)
			httpSession.removeAttribute(SessionConstants.SDMX_IMPORT_SESSION);
		httpSession.setAttribute(SessionConstants.SDMX_IMPORT_SESSION, s);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
	}

	public static SDMXImportSession getSDMXImportSession(HttpSession httpSession) {
		SDMXImportSession importSession = (SDMXImportSession) httpSession
				.getAttribute(SessionConstants.SDMX_IMPORT_SESSION);
		if (importSession == null) {
			logger.error("SDMXImportSession was not acquired");
		}
		return importSession;
	}

	public static void setCSVImportSession(HttpSession httpSession,
			CSVImportSession s) throws TDGWTSessionExpiredException {

		CSVImportSession session = (CSVImportSession) httpSession
				.getAttribute(SessionConstants.CSV_IMPORT_SESSION);
		if (session != null)
			httpSession.removeAttribute(SessionConstants.CSV_IMPORT_SESSION);
		httpSession.setAttribute(SessionConstants.CSV_IMPORT_SESSION, s);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
	}

	public static CSVImportSession getCSVImportSession(HttpSession httpSession) {
		CSVImportSession importSession = (CSVImportSession) httpSession
				.getAttribute(SessionConstants.CSV_IMPORT_SESSION);
		if (importSession == null) {
			logger.error("CSVImportSession was not acquired");
		}
		return importSession;
	}

	public static void setCSVExportSession(HttpSession httpSession,
			CSVExportSession s) throws TDGWTSessionExpiredException {

		CSVExportSession session = (CSVExportSession) httpSession
				.getAttribute(SessionConstants.CSV_EXPORT_SESSION);
		if (session != null)
			httpSession.removeAttribute(SessionConstants.CSV_EXPORT_SESSION);
		httpSession.setAttribute(SessionConstants.CSV_EXPORT_SESSION, s);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
	}

	public static CSVExportSession getCSVExportSession(HttpSession httpSession) {
		CSVExportSession exportSession = (CSVExportSession) httpSession
				.getAttribute(SessionConstants.CSV_EXPORT_SESSION);
		if (exportSession == null) {
			logger.error("CSVExportSession was not acquired");
		}
		return exportSession;
	}

	public static void setSDMXExportSession(HttpSession httpSession,
			SDMXExportSession s) throws TDGWTSessionExpiredException {

		SDMXExportSession session = (SDMXExportSession) httpSession
				.getAttribute(SessionConstants.SDMX_EXPORT_SESSION);
		if (session != null)
			httpSession.removeAttribute(SessionConstants.SDMX_EXPORT_SESSION);
		httpSession.setAttribute(SessionConstants.SDMX_EXPORT_SESSION, s);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
	}

	public static SDMXExportSession getSDMXExportSession(HttpSession httpSession) {
		SDMXExportSession exportSession = (SDMXExportSession) httpSession
				.getAttribute(SessionConstants.SDMX_EXPORT_SESSION);
		if (exportSession == null) {
			logger.error("SDMXExportSession was not acquired");
		}
		return exportSession;
	}

	//
	public static void setJSONExportSession(HttpSession httpSession,
			JSONExportSession s) throws TDGWTSessionExpiredException {

		JSONExportSession session = (JSONExportSession) httpSession
				.getAttribute(SessionConstants.JSON_EXPORT_SESSION);
		if (session != null)
			httpSession.removeAttribute(SessionConstants.JSON_EXPORT_SESSION);
		httpSession.setAttribute(SessionConstants.JSON_EXPORT_SESSION, s);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
	}

	public static JSONExportSession getJSONExportSession(HttpSession httpSession) {
		JSONExportSession exportSession = (JSONExportSession) httpSession
				.getAttribute(SessionConstants.JSON_EXPORT_SESSION);
		if (exportSession == null) {
			logger.error("JSONExportSession was not acquired");
		}
		return exportSession;
	}

	//
	public static void setCSVFileUploadSession(HttpSession httpSession,
			CSVFileUploadSession s) throws TDGWTSessionExpiredException {

		CSVFileUploadSession session = (CSVFileUploadSession) httpSession
				.getAttribute(SessionConstants.CSV_IMPORT_FILE_UPLOAD_SESSION);
		if (session != null)
			httpSession
					.removeAttribute(SessionConstants.CSV_IMPORT_FILE_UPLOAD_SESSION);
		httpSession.setAttribute(
				SessionConstants.CSV_IMPORT_FILE_UPLOAD_SESSION, s);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
	}

	public static CSVFileUploadSession getCSVFileUploadSession(
			HttpSession httpSession) {
		CSVFileUploadSession fileUploadSession = (CSVFileUploadSession) httpSession
				.getAttribute(SessionConstants.CSV_IMPORT_FILE_UPLOAD_SESSION);
		if (fileUploadSession == null) {
			logger.error("CSVFileUploadSession was not acquired");
		}
		return fileUploadSession;
	}

	public static void setCodelistMappingFileUploadSession(
			HttpSession httpSession, CodelistMappingFileUploadSession s)
			throws TDGWTSessionExpiredException {

		CodelistMappingFileUploadSession session = (CodelistMappingFileUploadSession) httpSession
				.getAttribute(SessionConstants.CODELIST_MAPPING_FILE_UPLOAD_SESSION);
		if (session != null)
			httpSession
					.removeAttribute(SessionConstants.CODELIST_MAPPING_FILE_UPLOAD_SESSION);
		httpSession.setAttribute(
				SessionConstants.CODELIST_MAPPING_FILE_UPLOAD_SESSION, s);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
	}

	public static CodelistMappingFileUploadSession getCodelistMappingFileUploadSession(
			HttpSession httpSession) {
		CodelistMappingFileUploadSession fileUploadSession = (CodelistMappingFileUploadSession) httpSession
				.getAttribute(SessionConstants.CODELIST_MAPPING_FILE_UPLOAD_SESSION);
		if (fileUploadSession == null) {
			logger.error("CodelistMappingFileUploadSession was not acquired");
		}
		return fileUploadSession;
	}

	//
	public static ArrayList<Codelist> retrieveCodelists(HttpSession httpSession)
			throws Exception {
		logger.info("SessionUtil retriveCodelists");
		SDMXClient client = getSdmxClient(httpSession);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
		return client.getAllCodelists();
	}

	public static ArrayList<Dataset> retrieveDatasets(HttpSession httpSession)
			throws Exception {
		logger.info("SessionUtil retriveDatasets");
		SDMXClient client = getSdmxClient(httpSession);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
		return client.getAllDatasets();
	}

	public static ArrayList<Agencies> retrieveAgencies(HttpSession httpSession)
			throws Exception {
		logger.info("SessionUtil retriveAgencies");
		SDMXClient client = getSdmxClient(httpSession);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
		return client.getAllAgencies();
	}

	public static void setSDMXRegistrySource(HttpSession httpSession,
			SDMXRegistrySource sdmxRegistrySource)
			throws TDGWTSessionExpiredException {

		SDMXRegistrySource source = (SDMXRegistrySource) httpSession
				.getAttribute(SessionConstants.SDMX_REGISTRY_SOURCE);
		if (source != null)
			httpSession.removeAttribute(SessionConstants.SDMX_REGISTRY_SOURCE);
		httpSession.setAttribute(SessionConstants.SDMX_REGISTRY_SOURCE,
				sdmxRegistrySource);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
	}

	public static SDMXRegistrySource getSDMXRegistrySource(
			HttpSession httpSession) {
		SDMXRegistrySource sdmxRegistrySource = (SDMXRegistrySource) httpSession
				.getAttribute(SessionConstants.SDMX_REGISTRY_SOURCE);
		if (sdmxRegistrySource == null) {
			logger.error("SDMXRegistrySource was not acquired");
		}
		return sdmxRegistrySource;
	}

	public static SDMXClient getSdmxClient(HttpSession httpSession) {
		SDMXClient sdmxClient = (SDMXClient) httpSession
				.getAttribute(SessionConstants.SDMX_CLIENT_ATTRIBUTE);
		SDMXRegistrySource sdmxRegistrySource = (SDMXRegistrySource) httpSession
				.getAttribute(SessionConstants.SDMX_REGISTRY_SOURCE);
		if (sdmxRegistrySource == null) {
			logger.error("SDMXRegistrySource was not acquired");
		} else {
			String url = sdmxRegistrySource.getUrl();
			if (url == null || url.isEmpty()) {
				if (sdmxClient == null) {
					sdmxClient = new SDMXClient();
				} else {
					if (sdmxClient.type.compareTo(SDMXClient.TYPE.ANOTHER) == 0) {
						sdmxClient = new SDMXClient();
					}
				}
			} else {
				if (sdmxClient == null) {
					sdmxClient = new SDMXClient(url);
				} else {
					if (sdmxClient.type.compareTo(SDMXClient.TYPE.INTERNAL) == 0) {
						sdmxClient = new SDMXClient(url);
					} else {
						if (sdmxClient.url.compareTo(url) != 0) {
							sdmxClient = new SDMXClient(url);
						}
					}
				}
			}
			httpSession.setAttribute(SessionConstants.SDMX_CLIENT_ATTRIBUTE,
					sdmxClient);

		}

		return sdmxClient;
	}

	//
	public static void setCSVExportEnd(HttpSession httpSession, Boolean end) {
		Boolean fin = (Boolean) httpSession
				.getAttribute(SessionConstants.CSV_EXPORT_END);
		if (fin != null)
			httpSession.removeAttribute(SessionConstants.CSV_EXPORT_END);
		httpSession.setAttribute(SessionConstants.CSV_EXPORT_END, end);
	}

	public static Boolean getCSVExportEnd(HttpSession httpSession) {
		Boolean end = (Boolean) httpSession
				.getAttribute(SessionConstants.CSV_EXPORT_END);
		logger.debug("getCSVExportEnd(): " + end);
		if (end == null) {
			logger.error("CSV_EXPORT_END was not acquired");
			end = false;
		}
		return end;
	}

	//
	public static void setJSONExportEnd(HttpSession httpSession, Boolean end) {
		Boolean fin = (Boolean) httpSession
				.getAttribute(SessionConstants.JSON_EXPORT_END);
		if (fin != null)
			httpSession.removeAttribute(SessionConstants.JSON_EXPORT_END);
		httpSession.setAttribute(SessionConstants.JSON_EXPORT_END, end);
	}

	public static Boolean getJSONExportEnd(HttpSession httpSession) {
		Boolean end = (Boolean) httpSession
				.getAttribute(SessionConstants.JSON_EXPORT_END);
		logger.debug("getJSONExportEnd(): " + end);
		if (end == null) {
			logger.error("JSON_EXPORT_END was not acquired");
			end = false;
		}
		return end;
	}

	//
	public static TRTasksManager getTRTasksManager(HttpSession httpSession) {
		TRTasksManager tasksManager = (TRTasksManager) httpSession
				.getAttribute(SessionConstants.TR_TASK_MANAGER);
		if (tasksManager != null) {
			return tasksManager;
		} else {
			tasksManager = new TRTasksManager();
			httpSession.setAttribute(SessionConstants.TR_TASK_MANAGER,
					tasksManager);
			return tasksManager;
		}
	}

	public static void setTRTasksManager(HttpSession httpSession,
			TRTasksManager trTasksManager) {
		TRTasksManager tm = (TRTasksManager) httpSession
				.getAttribute(SessionConstants.TR_TASK_MANAGER);
		if (tm != null) {
			httpSession.removeAttribute(SessionConstants.TR_TASK_MANAGER);
		}
		httpSession.setAttribute(SessionConstants.TR_TASK_MANAGER,
				trTasksManager);

	}

	//
	public static ChangeColumnTypeSession getChangeColumnTypeSession(
			HttpSession httpSession) {
		ChangeColumnTypeSession changeColumnTypeSession = (ChangeColumnTypeSession) httpSession
				.getAttribute(SessionConstants.CHANGE_COLUMN_TYPE_SESSION);
		if (changeColumnTypeSession != null) {
			return changeColumnTypeSession;
		} else {
			changeColumnTypeSession = new ChangeColumnTypeSession();
			httpSession.setAttribute(
					SessionConstants.CHANGE_COLUMN_TYPE_SESSION,
					changeColumnTypeSession);
			return changeColumnTypeSession;
		}
	}

	public static void setChangeColumnTypeSession(HttpSession httpSession,
			ChangeColumnTypeSession changeColumnTypeSession) {
		ChangeColumnTypeSession ct = (ChangeColumnTypeSession) httpSession
				.getAttribute(SessionConstants.CHANGE_COLUMN_TYPE_SESSION);
		if (ct != null) {
			httpSession
					.removeAttribute(SessionConstants.CHANGE_COLUMN_TYPE_SESSION);
		}
		httpSession.setAttribute(SessionConstants.CHANGE_COLUMN_TYPE_SESSION,
				changeColumnTypeSession);

	}

	// /

	public static LabelColumnSession getLabelColumnSession(
			HttpSession httpSession) {
		LabelColumnSession labelColumnSession = (LabelColumnSession) httpSession
				.getAttribute(SessionConstants.CHANGE_THE_COLUMN_LABEL_SESSION);
		if (labelColumnSession != null) {
			return labelColumnSession;
		} else {
			labelColumnSession = new LabelColumnSession();
			httpSession.setAttribute(
					SessionConstants.CHANGE_THE_COLUMN_LABEL_SESSION,
					labelColumnSession);
			return labelColumnSession;
		}
	}

	public static void setLabelColumnSession(HttpSession httpSession,
			LabelColumnSession labelColumnSession) {
		LabelColumnSession lc = (LabelColumnSession) httpSession
				.getAttribute(SessionConstants.CHANGE_THE_COLUMN_LABEL_SESSION);
		if (lc != null) {
			httpSession
					.removeAttribute(SessionConstants.CHANGE_THE_COLUMN_LABEL_SESSION);
		}
		httpSession.setAttribute(
				SessionConstants.CHANGE_THE_COLUMN_LABEL_SESSION,
				labelColumnSession);

	}

	//

	public static AddColumnSession getAddColumnSession(HttpSession httpSession) {
		AddColumnSession addColumnSession = (AddColumnSession) httpSession
				.getAttribute(SessionConstants.ADD_COLUMN_SESSION);
		if (addColumnSession != null) {
			return addColumnSession;
		} else {
			addColumnSession = new AddColumnSession();
			httpSession.setAttribute(SessionConstants.ADD_COLUMN_SESSION,
					addColumnSession);
			return addColumnSession;
		}
	}

	public static void setAddColumnSession(HttpSession httpSession,
			AddColumnSession addColumnSession) {
		AddColumnSession ac = (AddColumnSession) httpSession
				.getAttribute(SessionConstants.ADD_COLUMN_SESSION);
		if (ac != null) {
			httpSession.removeAttribute(SessionConstants.ADD_COLUMN_SESSION);
		}
		httpSession.setAttribute(SessionConstants.ADD_COLUMN_SESSION,
				addColumnSession);

	}

	//

	public static DeleteColumnSession getDeleteColumnSession(
			HttpSession httpSession) {
		DeleteColumnSession deleteColumnSession = (DeleteColumnSession) httpSession
				.getAttribute(SessionConstants.DELETE_COLUMN_SESSION);
		if (deleteColumnSession != null) {
			return deleteColumnSession;
		} else {
			deleteColumnSession = new DeleteColumnSession();
			httpSession.setAttribute(SessionConstants.DELETE_COLUMN_SESSION,
					deleteColumnSession);
			return deleteColumnSession;
		}
	}

	public static void setDeleteColumnSession(HttpSession httpSession,
			DeleteColumnSession deleteColumnSession) {
		DeleteColumnSession dc = (DeleteColumnSession) httpSession
				.getAttribute(SessionConstants.DELETE_COLUMN_SESSION);
		if (dc != null) {
			httpSession.removeAttribute(SessionConstants.DELETE_COLUMN_SESSION);
		}
		httpSession.setAttribute(SessionConstants.DELETE_COLUMN_SESSION,
				deleteColumnSession);

	}

	//

	public static FilterColumnSession getFilterColumnSession(
			HttpSession httpSession) {
		FilterColumnSession filterColumnSession = (FilterColumnSession) httpSession
				.getAttribute(SessionConstants.FILTER_COLUMN_SESSION);
		if (filterColumnSession != null) {
			return filterColumnSession;
		} else {
			filterColumnSession = new FilterColumnSession();
			httpSession.setAttribute(SessionConstants.FILTER_COLUMN_SESSION,
					filterColumnSession);
			return filterColumnSession;
		}
	}

	public static void setFilterColumnSession(HttpSession httpSession,
			FilterColumnSession filterColumnSession) {
		FilterColumnSession fc = (FilterColumnSession) httpSession
				.getAttribute(SessionConstants.FILTER_COLUMN_SESSION);
		if (fc != null) {
			httpSession.removeAttribute(SessionConstants.FILTER_COLUMN_SESSION);
		}
		httpSession.setAttribute(SessionConstants.FILTER_COLUMN_SESSION,
				filterColumnSession);

	}

	//
	public static ReplaceColumnByExpressionSession getReplaceColumnByExpressionSession(
			HttpSession httpSession) {
		ReplaceColumnByExpressionSession replaceColumnByExpressionSession = (ReplaceColumnByExpressionSession) httpSession
				.getAttribute(SessionConstants.REPLACE_COLUMN_BY_EXPRESSION_SESSION);
		if (replaceColumnByExpressionSession != null) {
			return replaceColumnByExpressionSession;
		} else {
			replaceColumnByExpressionSession = new ReplaceColumnByExpressionSession();
			httpSession.setAttribute(
					SessionConstants.REPLACE_COLUMN_BY_EXPRESSION_SESSION,
					replaceColumnByExpressionSession);
			return replaceColumnByExpressionSession;
		}
	}

	public static void setReplaceColumnByExpressionSession(
			HttpSession httpSession,
			ReplaceColumnByExpressionSession replaceColumnByExpressionSession) {
		ReplaceColumnByExpressionSession rce = (ReplaceColumnByExpressionSession) httpSession
				.getAttribute(SessionConstants.REPLACE_COLUMN_BY_EXPRESSION_SESSION);
		if (rce != null) {
			httpSession
					.removeAttribute(SessionConstants.REPLACE_COLUMN_BY_EXPRESSION_SESSION);
		}
		httpSession.setAttribute(
				SessionConstants.REPLACE_COLUMN_BY_EXPRESSION_SESSION,
				replaceColumnByExpressionSession);

	}

	//
	public static ApplyAndDetachColumnRulesSession getRulesOnColumnApplyAndDetachSession(
			HttpSession httpSession) {
		ApplyAndDetachColumnRulesSession applyColumnRulesSession = (ApplyAndDetachColumnRulesSession) httpSession
				.getAttribute(SessionConstants.RULES_ON_COLUMN_APPLY_AND_DETACH_SESSION);
		if (applyColumnRulesSession != null) {
			return applyColumnRulesSession;
		} else {
			applyColumnRulesSession = new ApplyAndDetachColumnRulesSession();
			httpSession.setAttribute(
					SessionConstants.RULES_ON_COLUMN_APPLY_AND_DETACH_SESSION,
					applyColumnRulesSession);
			return applyColumnRulesSession;
		}
	}

	public static void setRulesOnColumnApplyAndDetachSession(
			HttpSession httpSession,
			ApplyAndDetachColumnRulesSession applyColumnRulesSession) {
		ApplyAndDetachColumnRulesSession rules = (ApplyAndDetachColumnRulesSession) httpSession
				.getAttribute(SessionConstants.RULES_ON_COLUMN_APPLY_AND_DETACH_SESSION);
		if (rules != null) {
			httpSession
					.removeAttribute(SessionConstants.RULES_ON_COLUMN_APPLY_AND_DETACH_SESSION);
		}
		httpSession.setAttribute(
				SessionConstants.RULES_ON_COLUMN_APPLY_AND_DETACH_SESSION,
				applyColumnRulesSession);

	}

	//
	public static ApplyTableRuleSession getApplyTableRuleSession(
			HttpSession httpSession) {
		ApplyTableRuleSession applyTableRuleSession = (ApplyTableRuleSession) httpSession
				.getAttribute(SessionConstants.RULES_ON_TABLE_APPLY_SESSION);
		if (applyTableRuleSession != null) {
			return applyTableRuleSession;
		} else {
			applyTableRuleSession = new ApplyTableRuleSession();
			httpSession.setAttribute(
					SessionConstants.RULES_ON_TABLE_APPLY_SESSION,
					applyTableRuleSession);
			return applyTableRuleSession;
		}
	}

	public static void setApplyTableRuleSession(HttpSession httpSession,
			ApplyTableRuleSession applyTableRuleSession) {
		ApplyTableRuleSession atrs = (ApplyTableRuleSession) httpSession
				.getAttribute(SessionConstants.RULES_ON_TABLE_APPLY_SESSION);
		if (atrs != null) {
			httpSession
					.removeAttribute(SessionConstants.RULES_ON_TABLE_APPLY_SESSION);
		}
		httpSession.setAttribute(SessionConstants.RULES_ON_TABLE_APPLY_SESSION,
				applyTableRuleSession);

	}

	//
	public static DetachColumnRulesSession getDetachColumnRulesSession(
			HttpSession httpSession) {
		DetachColumnRulesSession detachColumnRulesSession = (DetachColumnRulesSession) httpSession
				.getAttribute(SessionConstants.RULES_ON_COLUMN_DETACH_SESSION);
		if (detachColumnRulesSession != null) {
			return detachColumnRulesSession;
		} else {
			detachColumnRulesSession = new DetachColumnRulesSession();
			httpSession.setAttribute(
					SessionConstants.RULES_ON_COLUMN_DETACH_SESSION,
					detachColumnRulesSession);
			return detachColumnRulesSession;
		}
	}

	public static void setDetachColumnRulesSession(HttpSession httpSession,
			DetachColumnRulesSession detachColumnRulesSession) {
		DetachColumnRulesSession rules = (DetachColumnRulesSession) httpSession
				.getAttribute(SessionConstants.RULES_ON_COLUMN_DETACH_SESSION);
		if (rules != null) {
			httpSession
					.removeAttribute(SessionConstants.RULES_ON_COLUMN_DETACH_SESSION);
		}
		httpSession.setAttribute(
				SessionConstants.RULES_ON_COLUMN_DETACH_SESSION,
				detachColumnRulesSession);

	}

	//
	public static DetachTableRulesSession getDetachTableRulesSession(
			HttpSession httpSession) {
		DetachTableRulesSession detachTableRulesSession = (DetachTableRulesSession) httpSession
				.getAttribute(SessionConstants.RULES_ON_TABLE_DETACH_SESSION);
		if (detachTableRulesSession != null) {
			return detachTableRulesSession;
		} else {
			detachTableRulesSession = new DetachTableRulesSession();
			httpSession.setAttribute(
					SessionConstants.RULES_ON_TABLE_DETACH_SESSION,
					detachTableRulesSession);
			return detachTableRulesSession;
		}
	}

	public static void setDetachTableRulesSession(HttpSession httpSession,
			DetachTableRulesSession detachTableRulesSession) {
		DetachTableRulesSession rules = (DetachTableRulesSession) httpSession
				.getAttribute(SessionConstants.RULES_ON_TABLE_DETACH_SESSION);
		if (rules != null) {
			httpSession
					.removeAttribute(SessionConstants.RULES_ON_TABLE_DETACH_SESSION);
		}
		httpSession.setAttribute(
				SessionConstants.RULES_ON_TABLE_DETACH_SESSION,
				detachTableRulesSession);

	}

	//
	public static TaskResubmitSession getTaskResubmitSession(
			HttpSession httpSession) {
		TaskResubmitSession taskResubmitSession = (TaskResubmitSession) httpSession
				.getAttribute(SessionConstants.TASK_RESUBMIT_SESSION);
		if (taskResubmitSession != null) {
			return taskResubmitSession;
		} else {
			taskResubmitSession = new TaskResubmitSession();
			httpSession.setAttribute(SessionConstants.TASK_RESUBMIT_SESSION,
					taskResubmitSession);
			return taskResubmitSession;
		}
	}

	public static void setTaskResubmitSession(HttpSession httpSession,
			TaskResubmitSession taskResubmitSession) {
		TaskResubmitSession tr = (TaskResubmitSession) httpSession
				.getAttribute(SessionConstants.TASK_RESUBMIT_SESSION);
		if (tr != null) {
			httpSession.removeAttribute(SessionConstants.TASK_RESUBMIT_SESSION);
		}
		httpSession.setAttribute(SessionConstants.TASK_RESUBMIT_SESSION,
				taskResubmitSession);

	}

	//
	public static TaskResumeSession getTaskResumeSession(HttpSession httpSession) {
		TaskResumeSession taskResumeSession = (TaskResumeSession) httpSession
				.getAttribute(SessionConstants.TASK_RESUME_SESSION);
		if (taskResumeSession != null) {
			return taskResumeSession;
		} else {
			taskResumeSession = new TaskResumeSession();
			httpSession.setAttribute(SessionConstants.TASK_RESUME_SESSION,
					taskResumeSession);
			return taskResumeSession;
		}
	}

	public static void setTaskResumeSession(HttpSession httpSession,
			TaskResumeSession taskResumeSession) {
		TaskResumeSession tr = (TaskResumeSession) httpSession
				.getAttribute(SessionConstants.TASK_RESUME_SESSION);
		if (tr != null) {
			httpSession.removeAttribute(SessionConstants.TASK_RESUME_SESSION);
		}
		httpSession.setAttribute(SessionConstants.TASK_RESUME_SESSION,
				taskResumeSession);

	}

	//
	public static EditRowSession getEditRowSession(HttpSession httpSession) {
		EditRowSession editRowSession = (EditRowSession) httpSession
				.getAttribute(SessionConstants.EDIT_ROW_SESSION);
		if (editRowSession != null) {
			return editRowSession;
		} else {
			editRowSession = new EditRowSession();
			httpSession.setAttribute(SessionConstants.EDIT_ROW_SESSION,
					editRowSession);
			return editRowSession;
		}
	}

	public static void setEditRowSession(HttpSession httpSession,
			EditRowSession editRowSession) {
		EditRowSession er = (EditRowSession) httpSession
				.getAttribute(SessionConstants.EDIT_ROW_SESSION);
		if (er != null) {
			httpSession.removeAttribute(SessionConstants.EDIT_ROW_SESSION);
		}
		httpSession.setAttribute(SessionConstants.EDIT_ROW_SESSION,
				editRowSession);

	}

	//

	public static ReplaceColumnSession getReplaceColumnSession(
			HttpSession httpSession) {
		ReplaceColumnSession replaceColumnSession = (ReplaceColumnSession) httpSession
				.getAttribute(SessionConstants.REPLACE_COLUMN_SESSION);
		if (replaceColumnSession != null) {
			return replaceColumnSession;
		} else {
			replaceColumnSession = new ReplaceColumnSession();
			httpSession.setAttribute(SessionConstants.REPLACE_COLUMN_SESSION,
					replaceColumnSession);
			return replaceColumnSession;
		}
	}

	public static void setReplaceColumnSession(HttpSession httpSession,
			ReplaceColumnSession replaceColumnSession) {
		ReplaceColumnSession rc = (ReplaceColumnSession) httpSession
				.getAttribute(SessionConstants.REPLACE_COLUMN_SESSION);
		if (rc != null) {
			httpSession
					.removeAttribute(SessionConstants.REPLACE_COLUMN_SESSION);
		}
		httpSession.setAttribute(SessionConstants.REPLACE_COLUMN_SESSION,
				replaceColumnSession);

	}

	//
	public static ReplaceBatchColumnSession getReplaceBatchColumnSession(
			HttpSession httpSession) {
		ReplaceBatchColumnSession replaceBatchColumnSession = (ReplaceBatchColumnSession) httpSession
				.getAttribute(SessionConstants.REPLACE_BATCH_COLUMN_SESSION);
		if (replaceBatchColumnSession != null) {
			return replaceBatchColumnSession;
		} else {
			replaceBatchColumnSession = new ReplaceBatchColumnSession();
			httpSession.setAttribute(
					SessionConstants.REPLACE_BATCH_COLUMN_SESSION,
					replaceBatchColumnSession);
			return replaceBatchColumnSession;
		}
	}

	public static void setReplaceBatchColumnSession(HttpSession httpSession,
			ReplaceBatchColumnSession replaceBatchColumnSession) {
		ReplaceBatchColumnSession rbc = (ReplaceBatchColumnSession) httpSession
				.getAttribute(SessionConstants.REPLACE_BATCH_COLUMN_SESSION);
		if (rbc != null) {
			httpSession
					.removeAttribute(SessionConstants.REPLACE_BATCH_COLUMN_SESSION);
		}
		httpSession.setAttribute(SessionConstants.REPLACE_BATCH_COLUMN_SESSION,
				replaceBatchColumnSession);

	}

	//

	public static ChangeTableTypeSession getChangeTableTypeSession(
			HttpSession httpSession) {
		ChangeTableTypeSession changeTableTypeSession = (ChangeTableTypeSession) httpSession
				.getAttribute(SessionConstants.CHANGE_TABLE_TYPE_SESSION);
		if (changeTableTypeSession != null) {
			return changeTableTypeSession;
		} else {
			changeTableTypeSession = new ChangeTableTypeSession();
			httpSession.setAttribute(
					SessionConstants.CHANGE_TABLE_TYPE_SESSION,
					changeTableTypeSession);
			return changeTableTypeSession;
		}
	}

	public static void setChangeTableTypeSession(HttpSession httpSession,
			ChangeTableTypeSession changeTableTypeSession) {
		ChangeTableTypeSession ctts = (ChangeTableTypeSession) httpSession
				.getAttribute(SessionConstants.CHANGE_TABLE_TYPE_SESSION);
		if (ctts != null) {
			httpSession
					.removeAttribute(SessionConstants.CHANGE_TABLE_TYPE_SESSION);
		}
		httpSession.setAttribute(SessionConstants.CHANGE_TABLE_TYPE_SESSION,
				changeTableTypeSession);

	}

	// /

	public static CloneTabularResourceSession getCloneTabularResourceSession(
			HttpSession httpSession) {
		CloneTabularResourceSession cloneTabularResourceSession = (CloneTabularResourceSession) httpSession
				.getAttribute(SessionConstants.CLONE_TABULAR_RESOURCE_SESSION);
		if (cloneTabularResourceSession != null) {
			return cloneTabularResourceSession;
		} else {
			cloneTabularResourceSession = new CloneTabularResourceSession();
			httpSession.setAttribute(
					SessionConstants.CLONE_TABULAR_RESOURCE_SESSION,
					cloneTabularResourceSession);
			return cloneTabularResourceSession;
		}
	}

	public static void setCloneTabularResourceSession(HttpSession httpSession,
			CloneTabularResourceSession cloneTabularResourceSession) {
		CloneTabularResourceSession c = (CloneTabularResourceSession) httpSession
				.getAttribute(SessionConstants.CLONE_TABULAR_RESOURCE_SESSION);
		if (c != null) {
			httpSession
					.removeAttribute(SessionConstants.CLONE_TABULAR_RESOURCE_SESSION);
		}
		httpSession.setAttribute(
				SessionConstants.CLONE_TABULAR_RESOURCE_SESSION,
				cloneTabularResourceSession);

	}

	// /

	public static DeleteRowsSession getDeleteRowsSession(HttpSession httpSession) {
		DeleteRowsSession deleteRowsSession = (DeleteRowsSession) httpSession
				.getAttribute(SessionConstants.DELETE_ROWS_SESSION);
		if (deleteRowsSession != null) {
			return deleteRowsSession;
		} else {
			deleteRowsSession = new DeleteRowsSession();
			httpSession.setAttribute(SessionConstants.DELETE_ROWS_SESSION,
					deleteRowsSession);
			return deleteRowsSession;
		}
	}

	public static void setDeleteRowsSession(HttpSession httpSession,
			DeleteRowsSession deleteRowsSession) {
		DeleteRowsSession dr = (DeleteRowsSession) httpSession
				.getAttribute(SessionConstants.DELETE_ROWS_SESSION);
		if (dr != null) {
			httpSession.removeAttribute(SessionConstants.DELETE_ROWS_SESSION);
		}
		httpSession.setAttribute(SessionConstants.DELETE_ROWS_SESSION,
				deleteRowsSession);

	}

	//

	public static TemplateApplySession getTemplateApplySession(
			HttpSession httpSession) {
		TemplateApplySession templateApplySession = (TemplateApplySession) httpSession
				.getAttribute(SessionConstants.TEMPLATE_APPLY_SESSION);
		if (templateApplySession != null) {
			return templateApplySession;
		} else {
			templateApplySession = new TemplateApplySession();
			httpSession.setAttribute(SessionConstants.TEMPLATE_APPLY_SESSION,
					templateApplySession);
			return templateApplySession;
		}
	}

	public static void setTemplateApplySession(HttpSession httpSession,
			TemplateApplySession templateApplySession) {
		TemplateApplySession dr = (TemplateApplySession) httpSession
				.getAttribute(SessionConstants.TEMPLATE_APPLY_SESSION);
		if (dr != null) {
			httpSession
					.removeAttribute(SessionConstants.TEMPLATE_APPLY_SESSION);
		}
		httpSession.setAttribute(SessionConstants.TEMPLATE_APPLY_SESSION,
				templateApplySession);

	}

	//

	public static DuplicatesSession getDuplicatesSession(HttpSession httpSession) {
		DuplicatesSession duplicatesSession = (DuplicatesSession) httpSession
				.getAttribute(SessionConstants.DUPLICATES_ROWS_SESSION);
		if (duplicatesSession != null) {
			return duplicatesSession;
		} else {
			duplicatesSession = new DuplicatesSession();
			httpSession.setAttribute(SessionConstants.DUPLICATES_ROWS_SESSION,
					duplicatesSession);
			return duplicatesSession;
		}
	}

	public static void setDuplicatesSession(HttpSession httpSession,
			DuplicatesSession duplicatesSession) {
		DuplicatesSession dr = (DuplicatesSession) httpSession
				.getAttribute(SessionConstants.DUPLICATES_ROWS_SESSION);
		if (dr != null) {
			httpSession
					.removeAttribute(SessionConstants.DUPLICATES_ROWS_SESSION);
		}
		httpSession.setAttribute(SessionConstants.DUPLICATES_ROWS_SESSION,
				duplicatesSession);

	}

	//

	public static ArrayList<TabResource> getCodelistsPagingLoaded(
			HttpSession httpSession) {
		@SuppressWarnings("unchecked")
		ArrayList<TabResource> ltr = ((ArrayList<TabResource>) httpSession
				.getAttribute(SessionConstants.CODELISTS_PAGING_LOADED));
		if (ltr == null) {
			logger.error("CODELISTS_PAGING_LOADED was not acquired");
		}
		return ltr;
	}

	public static void setCodelistsPagingLoaded(HttpSession httpSession,
			ArrayList<TabResource> listCodelists) {
		@SuppressWarnings("unchecked")
		ArrayList<TabResource> ltr = ((ArrayList<TabResource>) httpSession
				.getAttribute(SessionConstants.CODELISTS_PAGING_LOADED));
		if (ltr != null)
			httpSession
					.removeAttribute(SessionConstants.CODELISTS_PAGING_LOADED);
		httpSession.setAttribute(SessionConstants.CODELISTS_PAGING_LOADED,
				listCodelists);
	}

	public static ArrayList<TabResource> getCodelistsPagingLoadedFilteredCopy(
			HttpSession httpSession) {
		@SuppressWarnings("unchecked")
		ArrayList<TabResource> ltrFilteredCopy = ((ArrayList<TabResource>) httpSession
				.getAttribute(SessionConstants.CODELISTS_PAGING_LOADED_FILTERED_COPY));
		if (ltrFilteredCopy == null) {
			logger.error("CODELISTS_PAGING_LOADED_FILTERED_COPY was not acquired");
		}
		return ltrFilteredCopy;
	}

	public static void setCodelistsPagingLoadedFilteredCopy(
			HttpSession httpSession,
			ArrayList<TabResource> listCodelistsFilteredCopy) {
		@SuppressWarnings("unchecked")
		ArrayList<TabResource> ltrFilteredCopy = ((ArrayList<TabResource>) httpSession
				.getAttribute(SessionConstants.CODELISTS_PAGING_LOADED_FILTERED_COPY));
		if (ltrFilteredCopy != null)
			httpSession
					.removeAttribute(SessionConstants.CODELISTS_PAGING_LOADED_FILTERED_COPY);
		httpSession.setAttribute(
				SessionConstants.CODELISTS_PAGING_LOADED_FILTERED_COPY,
				listCodelistsFilteredCopy);
	}

	public static String getCodelistsPagingLoadedFilter(HttpSession httpSession) {
		String filter = (String) httpSession
				.getAttribute(SessionConstants.CODELISTS_PAGING_LOADED_FILTER);
		if (filter == null) {
			logger.error("CODELISTS_PAGING_LOADED_FILTER was not acquired");
		}
		return filter;
	}

	public static void setCodelistsPagingLoadedFilter(HttpSession httpSession,
			String filter) {

		String fil = ((String) httpSession
				.getAttribute(SessionConstants.CODELISTS_PAGING_LOADED_FILTER));
		if (fil != null)
			httpSession
					.removeAttribute(SessionConstants.CODELISTS_PAGING_LOADED_FILTER);
		httpSession.setAttribute(
				SessionConstants.CODELISTS_PAGING_LOADED_FILTER, filter);
	}

	//

	public static RollBackSession getRollBackSession(HttpSession httpSession) {
		RollBackSession rollBackSession = (RollBackSession) httpSession
				.getAttribute(SessionConstants.ROLLBACK_SESSION);
		if (rollBackSession != null) {
			return rollBackSession;
		} else {
			rollBackSession = new RollBackSession();
			httpSession.setAttribute(SessionConstants.ROLLBACK_SESSION,
					rollBackSession);
			return rollBackSession;
		}
	}

	public static void setRollBackSession(HttpSession httpSession,
			RollBackSession rollBackSession) {
		RollBackSession rb = (RollBackSession) httpSession
				.getAttribute(SessionConstants.ROLLBACK_SESSION);
		if (rb != null) {
			httpSession.removeAttribute(SessionConstants.ROLLBACK_SESSION);
		}
		httpSession.setAttribute(SessionConstants.ROLLBACK_SESSION,
				rollBackSession);

	}

	//
	public static ExtractCodelistSession getExtractCodelistSession(
			HttpSession httpSession) {
		ExtractCodelistSession extractCodelistSession = (ExtractCodelistSession) httpSession
				.getAttribute(SessionConstants.EXTRACT_CODELIST_SESSION);
		if (extractCodelistSession != null) {
			return extractCodelistSession;
		} else {
			extractCodelistSession = new ExtractCodelistSession();
			httpSession.setAttribute(SessionConstants.EXTRACT_CODELIST_SESSION,
					extractCodelistSession);
			return extractCodelistSession;
		}
	}

	public static void setExtractCodelistSession(HttpSession httpSession,
			ExtractCodelistSession extractCodelistSession) {
		ExtractCodelistSession ec = (ExtractCodelistSession) httpSession
				.getAttribute(SessionConstants.EXTRACT_CODELIST_SESSION);
		if (ec != null) {
			httpSession
					.removeAttribute(SessionConstants.EXTRACT_CODELIST_SESSION);
		}
		httpSession.setAttribute(SessionConstants.EXTRACT_CODELIST_SESSION,
				extractCodelistSession);

	}

	//
	public static SplitColumnSession getSplitColumnSession(
			HttpSession httpSession) {
		SplitColumnSession splitColumnSession = (SplitColumnSession) httpSession
				.getAttribute(SessionConstants.SPLIT_COLUMN_SESSION);
		if (splitColumnSession != null) {
			return splitColumnSession;
		} else {
			splitColumnSession = new SplitColumnSession();
			httpSession.setAttribute(SessionConstants.SPLIT_COLUMN_SESSION,
					splitColumnSession);
			return splitColumnSession;
		}
	}

	public static void setSplitColumnSession(HttpSession httpSession,
			SplitColumnSession splitColumnSession) {
		SplitColumnSession sc = (SplitColumnSession) httpSession
				.getAttribute(SessionConstants.SPLIT_COLUMN_SESSION);
		if (sc != null) {
			httpSession.removeAttribute(SessionConstants.SPLIT_COLUMN_SESSION);
		}
		httpSession.setAttribute(SessionConstants.SPLIT_COLUMN_SESSION,
				splitColumnSession);

	}

	//
	public static MergeColumnSession getMergeColumnSession(
			HttpSession httpSession) {
		MergeColumnSession mergeColumnSession = (MergeColumnSession) httpSession
				.getAttribute(SessionConstants.MERGE_COLUMN_SESSION);
		if (mergeColumnSession != null) {
			return mergeColumnSession;
		} else {
			mergeColumnSession = new MergeColumnSession();
			httpSession.setAttribute(SessionConstants.MERGE_COLUMN_SESSION,
					mergeColumnSession);
			return mergeColumnSession;
		}
	}

	public static void setMergeColumnSession(HttpSession httpSession,
			MergeColumnSession splitColumnSession) {
		MergeColumnSession mc = (MergeColumnSession) httpSession
				.getAttribute(SessionConstants.MERGE_COLUMN_SESSION);
		if (mc != null) {
			httpSession.removeAttribute(SessionConstants.MERGE_COLUMN_SESSION);
		}
		httpSession.setAttribute(SessionConstants.MERGE_COLUMN_SESSION,
				splitColumnSession);

	}

	//
	public static GroupBySession getGroupBySession(HttpSession httpSession) {
		GroupBySession groupBySession = (GroupBySession) httpSession
				.getAttribute(SessionConstants.GROUPBY_SESSION);
		if (groupBySession != null) {
			return groupBySession;
		} else {
			groupBySession = new GroupBySession();
			httpSession.setAttribute(SessionConstants.GROUPBY_SESSION,
					groupBySession);
			return groupBySession;
		}
	}

	public static void setGroupBySession(HttpSession httpSession,
			GroupBySession groupBySession) {
		GroupBySession gb = (GroupBySession) httpSession
				.getAttribute(SessionConstants.GROUPBY_SESSION);
		if (gb != null) {
			httpSession.removeAttribute(SessionConstants.GROUPBY_SESSION);
		}
		httpSession.setAttribute(SessionConstants.GROUPBY_SESSION,
				groupBySession);

	}

	//
	public static TimeAggregationSession getTimeAggregationSession(
			HttpSession httpSession) {
		TimeAggregationSession timeAggregationSession = (TimeAggregationSession) httpSession
				.getAttribute(SessionConstants.TIME_AGGREGATION_SESSION);
		if (timeAggregationSession != null) {
			return timeAggregationSession;
		} else {
			timeAggregationSession = new TimeAggregationSession();
			httpSession.setAttribute(SessionConstants.TIME_AGGREGATION_SESSION,
					timeAggregationSession);
			return timeAggregationSession;
		}
	}

	public static void setTimeAggregationSession(HttpSession httpSession,
			TimeAggregationSession timeAggregationSession) {
		TimeAggregationSession timeAggr = (TimeAggregationSession) httpSession
				.getAttribute(SessionConstants.TIME_AGGREGATION_SESSION);
		if (timeAggr != null) {
			httpSession
					.removeAttribute(SessionConstants.TIME_AGGREGATION_SESSION);
		}
		httpSession.setAttribute(SessionConstants.TIME_AGGREGATION_SESSION,
				timeAggregationSession);

	}

	//
	public static CodelistMappingSession getCodelistMappingSession(
			HttpSession httpSession) {
		CodelistMappingSession importSession = (CodelistMappingSession) httpSession
				.getAttribute(SessionConstants.CODELIST_MAPPING_SESSION);
		if (importSession == null) {
			logger.error("CSVImportSession was not acquired");
		}
		return importSession;
	}

	public static void setCodelistMappingSession(HttpSession httpSession,
			CodelistMappingSession codelistMappingSession)
			throws TDGWTSessionExpiredException {

		CodelistMappingSession session = (CodelistMappingSession) httpSession
				.getAttribute(SessionConstants.CODELIST_MAPPING_SESSION);
		if (session != null)
			httpSession
					.removeAttribute(SessionConstants.CODELIST_MAPPING_SESSION);
		httpSession.setAttribute(SessionConstants.CODELIST_MAPPING_SESSION,
				codelistMappingSession);
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope().toString());
	}

	//
	public static NormalizationSession getNormalizationSession(
			HttpSession httpSession) {
		NormalizationSession normalizationSession = (NormalizationSession) httpSession
				.getAttribute(SessionConstants.NORMALIZATION_SESSION);
		if (normalizationSession != null) {
			return normalizationSession;
		} else {
			normalizationSession = new NormalizationSession();
			httpSession.setAttribute(SessionConstants.NORMALIZATION_SESSION,
					normalizationSession);
			return normalizationSession;
		}
	}

	public static void setNormalizationSession(HttpSession httpSession,
			NormalizationSession normalizationSession) {
		NormalizationSession gb = (NormalizationSession) httpSession
				.getAttribute(SessionConstants.NORMALIZATION_SESSION);
		if (gb != null) {
			httpSession.removeAttribute(SessionConstants.NORMALIZATION_SESSION);
		}
		httpSession.setAttribute(SessionConstants.NORMALIZATION_SESSION,
				normalizationSession);

	}

	//
	public static DenormalizationSession getDenormalizationSession(
			HttpSession httpSession) {
		DenormalizationSession denormalizationSession = (DenormalizationSession) httpSession
				.getAttribute(SessionConstants.DENORMALIZATION_SESSION);
		if (denormalizationSession != null) {
			return denormalizationSession;
		} else {
			denormalizationSession = new DenormalizationSession();
			httpSession.setAttribute(SessionConstants.DENORMALIZATION_SESSION,
					denormalizationSession);
			return denormalizationSession;
		}
	}

	public static void setDenormalizationSession(HttpSession httpSession,
			DenormalizationSession denormalizationSession) {
		DenormalizationSession dn = (DenormalizationSession) httpSession
				.getAttribute(SessionConstants.DENORMALIZATION_SESSION);
		if (dn != null) {
			httpSession
					.removeAttribute(SessionConstants.DENORMALIZATION_SESSION);
		}
		httpSession.setAttribute(SessionConstants.DENORMALIZATION_SESSION,
				denormalizationSession);

	}

	//
	public static UnionSession getUnionSession(HttpSession httpSession) {
		UnionSession unionSession = (UnionSession) httpSession
				.getAttribute(SessionConstants.UNION_SESSION);
		if (unionSession != null) {
			return unionSession;
		} else {
			unionSession = new UnionSession();
			httpSession.setAttribute(SessionConstants.UNION_SESSION,
					unionSession);
			return unionSession;
		}
	}

	public static void setUnionSession(HttpSession httpSession,
			UnionSession unionSession) {
		UnionSession us = (UnionSession) httpSession
				.getAttribute(SessionConstants.UNION_SESSION);
		if (us != null) {
			httpSession.removeAttribute(SessionConstants.UNION_SESSION);
		}
		httpSession.setAttribute(SessionConstants.UNION_SESSION, unionSession);

	}

	//
	public static ChangeColumnsPositionSession getChangeColumnsPositionSession(
			HttpSession httpSession) {
		ChangeColumnsPositionSession changeColumnsPositionSession = (ChangeColumnsPositionSession) httpSession
				.getAttribute(SessionConstants.CHANGE_COLUMNS_POSITION_SESSION);
		if (changeColumnsPositionSession != null) {
			return changeColumnsPositionSession;
		} else {
			changeColumnsPositionSession = new ChangeColumnsPositionSession();
			httpSession.setAttribute(
					SessionConstants.CHANGE_COLUMNS_POSITION_SESSION,
					changeColumnsPositionSession);
			return changeColumnsPositionSession;
		}
	}

	public static void setChangeColumnsPositionSession(HttpSession httpSession,
			ChangeColumnsPositionSession changeColumnsPositionSession) {
		ChangeColumnsPositionSession ccps = (ChangeColumnsPositionSession) httpSession
				.getAttribute(SessionConstants.CHANGE_COLUMNS_POSITION_SESSION);
		if (ccps != null) {
			httpSession
					.removeAttribute(SessionConstants.CHANGE_COLUMNS_POSITION_SESSION);
		}
		httpSession.setAttribute(
				SessionConstants.CHANGE_COLUMNS_POSITION_SESSION,
				changeColumnsPositionSession);

	}

	//
	public static ReplaceByExternalSession getReplaceByExternalSession(
			HttpSession httpSession) {
		ReplaceByExternalSession replaceByExternalSession = (ReplaceByExternalSession) httpSession
				.getAttribute(SessionConstants.REPLACE_BY_EXTERNAL_SESSION);
		if (replaceByExternalSession != null) {
			return replaceByExternalSession;
		} else {
			replaceByExternalSession = new ReplaceByExternalSession();
			httpSession.setAttribute(
					SessionConstants.REPLACE_BY_EXTERNAL_SESSION,
					replaceByExternalSession);
			return replaceByExternalSession;
		}
	}

	public static void setReplaceByExternalSession(HttpSession httpSession,
			ReplaceByExternalSession replaceByExternalSession) {
		ReplaceByExternalSession re = (ReplaceByExternalSession) httpSession
				.getAttribute(SessionConstants.REPLACE_BY_EXTERNAL_SESSION);
		if (re != null) {
			httpSession
					.removeAttribute(SessionConstants.REPLACE_BY_EXTERNAL_SESSION);
		}
		httpSession.setAttribute(SessionConstants.REPLACE_BY_EXTERNAL_SESSION,
				replaceByExternalSession);

	}

	//
	public static StatisticalOperationSession getStatisticalOperationSession(
			HttpSession httpSession) {
		StatisticalOperationSession statisticalOperationSession = (StatisticalOperationSession) httpSession
				.getAttribute(SessionConstants.STATISTICAL_OPERATION_SESSION);
		if (statisticalOperationSession != null) {
			return statisticalOperationSession;
		} else {
			statisticalOperationSession = new StatisticalOperationSession();
			httpSession.setAttribute(
					SessionConstants.STATISTICAL_OPERATION_SESSION,
					statisticalOperationSession);
			return statisticalOperationSession;
		}
	}

	public static void setStatisticalOperationSession(HttpSession httpSession,
			StatisticalOperationSession statisticalOperationSession) {
		StatisticalOperationSession so = (StatisticalOperationSession) httpSession
				.getAttribute(SessionConstants.STATISTICAL_OPERATION_SESSION);
		if (so != null) {
			httpSession
					.removeAttribute(SessionConstants.STATISTICAL_OPERATION_SESSION);
		}
		httpSession.setAttribute(
				SessionConstants.STATISTICAL_OPERATION_SESSION,
				statisticalOperationSession);

	}

	// /

	public static MapCreationSession getMapCreationSession(
			HttpSession httpSession) {
		MapCreationSession mapCreationSession = (MapCreationSession) httpSession
				.getAttribute(SessionConstants.MAP_CREATION_SESSION);
		if (mapCreationSession != null) {
			return mapCreationSession;
		} else {
			mapCreationSession = new MapCreationSession();
			httpSession.setAttribute(SessionConstants.MAP_CREATION_SESSION,
					mapCreationSession);
			return mapCreationSession;
		}
	}

	public static void setMapCreationSession(HttpSession httpSession,
			MapCreationSession mapCreationSession) {
		MapCreationSession mapC = (MapCreationSession) httpSession
				.getAttribute(SessionConstants.MAP_CREATION_SESSION);
		if (mapC != null) {
			httpSession.removeAttribute(SessionConstants.MAP_CREATION_SESSION);
		}
		httpSession.setAttribute(SessionConstants.MAP_CREATION_SESSION,
				mapCreationSession);

	}

	// /

	public static ChartTopRatingSession getChartTopRatingSession(
			HttpSession httpSession) {
		ChartTopRatingSession chartTopRatingSession = (ChartTopRatingSession) httpSession
				.getAttribute(SessionConstants.CHART_TOPRATING_SESSION);
		if (chartTopRatingSession != null) {
			return chartTopRatingSession;
		} else {
			chartTopRatingSession = new ChartTopRatingSession();
			httpSession.setAttribute(SessionConstants.CHART_TOPRATING_SESSION,
					chartTopRatingSession);
			return chartTopRatingSession;
		}
	}

	public static void setChartTopRatingSession(HttpSession httpSession,
			ChartTopRatingSession chartTopRatingSession) {
		ChartTopRatingSession chart = (ChartTopRatingSession) httpSession
				.getAttribute(SessionConstants.CHART_TOPRATING_SESSION);
		if (chart != null) {
			httpSession
					.removeAttribute(SessionConstants.CHART_TOPRATING_SESSION);
		}
		httpSession.setAttribute(SessionConstants.CHART_TOPRATING_SESSION,
				chartTopRatingSession);

	}

	// Geospatial
	public static GeospatialCreateCoordinatesSession getGeospatialCreateCoordinatesSession(
			HttpSession httpSession) {
		GeospatialCreateCoordinatesSession geospatialCreateCoordinatesSession = (GeospatialCreateCoordinatesSession) httpSession
				.getAttribute(SessionConstants.GEOSPATIAL_CREATE_COORDINATES_SESSION);
		if (geospatialCreateCoordinatesSession != null) {
			return geospatialCreateCoordinatesSession;
		} else {
			geospatialCreateCoordinatesSession = new GeospatialCreateCoordinatesSession();
			httpSession.setAttribute(
					SessionConstants.GEOSPATIAL_CREATE_COORDINATES_SESSION,
					geospatialCreateCoordinatesSession);
			return geospatialCreateCoordinatesSession;
		}
	}

	public static void setGeospatialCreateCoordinatesSession(
			HttpSession httpSession,
			GeospatialCreateCoordinatesSession geospatialCreateCoordinateSession) {
		GeospatialCreateCoordinatesSession sc = (GeospatialCreateCoordinatesSession) httpSession
				.getAttribute(SessionConstants.GEOSPATIAL_CREATE_COORDINATES_SESSION);
		if (sc != null) {
			httpSession
					.removeAttribute(SessionConstants.GEOSPATIAL_CREATE_COORDINATES_SESSION);
		}
		httpSession.setAttribute(
				SessionConstants.GEOSPATIAL_CREATE_COORDINATES_SESSION,
				geospatialCreateCoordinateSession);

	}

	public static GeospatialDownscaleCSquareSession getGeospatialDownscaleCSquareSession(
			HttpSession httpSession) {
		GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession = (GeospatialDownscaleCSquareSession) httpSession
				.getAttribute(SessionConstants.GEOSPATIAL_DOWNSCALE_CSQUARE_SESSION);
		if (geospatialDownscaleCSquareSession != null) {
			return geospatialDownscaleCSquareSession;
		} else {
			geospatialDownscaleCSquareSession = new GeospatialDownscaleCSquareSession();
			httpSession.setAttribute(
					SessionConstants.GEOSPATIAL_DOWNSCALE_CSQUARE_SESSION,
					geospatialDownscaleCSquareSession);
			return geospatialDownscaleCSquareSession;
		}
	}

	public static void setGeospatialDownscaleCSquareSession(
			HttpSession httpSession,
			GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession) {
		GeospatialDownscaleCSquareSession sc = (GeospatialDownscaleCSquareSession) httpSession
				.getAttribute(SessionConstants.GEOSPATIAL_DOWNSCALE_CSQUARE_SESSION);
		if (sc != null) {
			httpSession
					.removeAttribute(SessionConstants.GEOSPATIAL_DOWNSCALE_CSQUARE_SESSION);
		}
		httpSession.setAttribute(
				SessionConstants.GEOSPATIAL_DOWNSCALE_CSQUARE_SESSION,
				geospatialDownscaleCSquareSession);

	}

	// Geometry
	public static GeometryCreatePointSession getGeometryCreatePointSession(
			HttpSession httpSession) {
		GeometryCreatePointSession geometryCreatePointSession = (GeometryCreatePointSession) httpSession
				.getAttribute(SessionConstants.GEOMETRY_CREATE_POINT_SESSION);
		if (geometryCreatePointSession != null) {
			return geometryCreatePointSession;
		} else {
			geometryCreatePointSession = new GeometryCreatePointSession();
			httpSession.setAttribute(
					SessionConstants.GEOMETRY_CREATE_POINT_SESSION,
					geometryCreatePointSession);
			return geometryCreatePointSession;
		}
	}

	public static void setGeometryCreatePointSession(HttpSession httpSession,
			GeometryCreatePointSession geometryCreatePointSession) {
		GeometryCreatePointSession geom = (GeometryCreatePointSession) httpSession
				.getAttribute(SessionConstants.GEOMETRY_CREATE_POINT_SESSION);
		if (geom != null) {
			httpSession
					.removeAttribute(SessionConstants.GEOMETRY_CREATE_POINT_SESSION);
		}
		httpSession.setAttribute(
				SessionConstants.GEOMETRY_CREATE_POINT_SESSION,
				geometryCreatePointSession);

	}

	/**
	 * Retrieve task started
	 * 
	 * @param httpSession
	 * @param taskId
	 * @return
	 * @throws TDGWTSessionExpiredException
	 */
	public static TaskWrapper getStartedTask(HttpSession httpSession,
			String taskId) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		TaskWrapper taskWrapper = null;

		if (taskId == null || taskId.isEmpty()) {
			logger.error("TaskId is not valid: " + taskId);
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, HashMap<String, TaskWrapper>> scopeToTasksStartedMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
					.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_STARTED_MAP);

			if (scopeToTasksStartedMap == null) {
				logger.debug("Task with id=" + taskId + " was not acquired");
			} else {
				HashMap<String, TaskWrapper> tasksStarted = scopeToTasksStartedMap
						.get(aslSession.getScope());
				if (tasksStarted == null) {
					logger.debug("Task with id=" + taskId + " was not acquired");
				} else {
					taskWrapper = tasksStarted.get(taskId);
					if (taskWrapper == null) {
						logger.debug("Task with id=" + taskId
								+ " was not acquired");
					}
				}
			}

		}
		return taskWrapper;

	}

	/**
	 * Remove Task Started
	 * 
	 * @param httpSession
	 * @param taskWrapper
	 * @throws TDGWTSessionExpiredException
	 */
	public static void removeStartedTask(HttpSession httpSession,
			TaskWrapper taskWrapper) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		if (taskWrapper == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains Task with invalid task id");
			return;
		}

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TaskWrapper>> scopeToTasksStartedMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_STARTED_MAP);

		if (scopeToTasksStartedMap == null) {
			logger.debug("TaskWrapper was not acquired: " + taskWrapper);
		} else {
			HashMap<String, TaskWrapper> tasksStarted = scopeToTasksStartedMap
					.get(aslSession.getScope());
			if (tasksStarted == null) {
				logger.debug("TaskWrapper was not acquired: " + taskWrapper);
			} else {
				tasksStarted.remove(taskWrapper.getTask().getId().getValue());
				scopeToTasksStartedMap.put(aslSession.getScope(), tasksStarted);
				httpSession
						.removeAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_STARTED_MAP);
				httpSession.setAttribute(
						SessionConstants.SCOPE_TO_OPERATIONS_TASKS_STARTED_MAP,
						scopeToTasksStartedMap);
			}
		}

		return;

	}

	/**
	 * Add Task Started
	 * 
	 * @param httpSession
	 * @param taskWrapper
	 * @throws TDGWTSessionExpiredException
	 */
	public static void setStartedTask(HttpSession httpSession,
			TaskWrapper taskWrapper) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		if (taskWrapper == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains Task with invalid task id");
			return;
		}

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TaskWrapper>> scopeToTasksStartedMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_STARTED_MAP);

		if (scopeToTasksStartedMap == null) {
			scopeToTasksStartedMap = new HashMap<String, HashMap<String, TaskWrapper>>();
			HashMap<String, TaskWrapper> tasksStarted = new HashMap<String, TaskWrapper>();
			tasksStarted.put(taskWrapper.getTask().getId().getValue(),
					taskWrapper);
			scopeToTasksStartedMap.put(aslSession.getScope(), tasksStarted);
		} else {
			HashMap<String, TaskWrapper> tasksStarted = scopeToTasksStartedMap
					.get(aslSession.getScope());
			if (tasksStarted == null) {
				tasksStarted = new HashMap<String, TaskWrapper>();
				tasksStarted.put(taskWrapper.getTask().getId().getValue(),
						taskWrapper);
				scopeToTasksStartedMap.put(aslSession.getScope(), tasksStarted);
			} else {
				tasksStarted.put(taskWrapper.getTask().getId().getValue(),
						taskWrapper);
				scopeToTasksStartedMap.put(aslSession.getScope(), tasksStarted);

			}
			httpSession
					.removeAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_STARTED_MAP);
		}
		httpSession.setAttribute(
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_STARTED_MAP,
				scopeToTasksStartedMap);

		return;

	}

	/**
	 * Retrieve Aborted Task Map
	 * 
	 * @param httpSession
	 * @return
	 * @throws TDGWTSessionExpiredException
	 */
	public static HashMap<String, TaskWrapper> getAbortedTaskMap(
			HttpSession httpSession) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TaskWrapper>> scopeToTasksAbortedMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_ABORTED_MAP);
		if (scopeToTasksAbortedMap == null) {
			return null;
		} else {
			HashMap<String, TaskWrapper> tasksAborted = scopeToTasksAbortedMap
					.get(aslSession.getScope());
			return tasksAborted;

		}

	}

	/**
	 * 
	 * @param httpSession
	 * @param taskWrapper
	 * @throws TDGWTSessionExpiredException
	 */
	public static void setAbortedTasks(HttpSession httpSession,
			TaskWrapper taskWrapper) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		if (taskWrapper == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask() == null
				|| taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains Task with invalid task id: "
					+ taskWrapper);
			return;
		}

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TaskWrapper>> scopeToTasksAbortedMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_ABORTED_MAP);

		if (scopeToTasksAbortedMap == null) {
			scopeToTasksAbortedMap = new HashMap<String, HashMap<String, TaskWrapper>>();
			HashMap<String, TaskWrapper> tasksAborted = new HashMap<String, TaskWrapper>();
			tasksAborted.put(taskWrapper.getTask().getId().getValue(),
					taskWrapper);
			scopeToTasksAbortedMap.put(aslSession.getScope(), tasksAborted);
		} else {
			HashMap<String, TaskWrapper> tasksAborted = scopeToTasksAbortedMap
					.get(aslSession.getScope());

			if (tasksAborted == null) {
				tasksAborted = new HashMap<String, TaskWrapper>();
				tasksAborted.put(taskWrapper.getTask().getId().getValue(),
						taskWrapper);
				scopeToTasksAbortedMap.put(aslSession.getScope(), tasksAborted);
			} else {
				tasksAborted.put(taskWrapper.getTask().getId().getValue(),
						taskWrapper);
				scopeToTasksAbortedMap.put(aslSession.getScope(), tasksAborted);

			}
			httpSession
					.removeAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_ABORTED_MAP);
		}
		httpSession.setAttribute(
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_ABORTED_MAP,
				scopeToTasksAbortedMap);

		return;

	}

	/**
	 * Retrive Hidden Task Map
	 * 
	 * @param httpSession
	 * @return
	 * @throws TDGWTSessionExpiredException
	 */
	public static HashMap<String, TaskWrapper> getHiddenTaskMap(
			HttpSession httpSession) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TaskWrapper>> scopeToHiddenTasksMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_HIDDEN_MAP);
		if (scopeToHiddenTasksMap == null) {
			return null;
		} else {
			HashMap<String, TaskWrapper> hiddenTasks = scopeToHiddenTasksMap
					.get(aslSession.getScope());
			return hiddenTasks;

		}

	}

	/**
	 * 
	 * @param httpSession
	 * @param taskWrapper
	 * @throws TDGWTSessionExpiredException
	 */
	public static void setHiddenTask(HttpSession httpSession,
			TaskWrapper taskWrapper) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		if (taskWrapper == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask() == null
				|| taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains Task with invalid task id: "
					+ taskWrapper);
			return;
		}

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TaskWrapper>> scopeToHiddenTasksMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_HIDDEN_MAP);

		if (scopeToHiddenTasksMap == null) {
			scopeToHiddenTasksMap = new HashMap<String, HashMap<String, TaskWrapper>>();
			HashMap<String, TaskWrapper> hiddenTasks = new HashMap<String, TaskWrapper>();
			hiddenTasks.put(taskWrapper.getTask().getId().getValue(),
					taskWrapper);
			scopeToHiddenTasksMap.put(aslSession.getScope(), hiddenTasks);
		} else {
			HashMap<String, TaskWrapper> hiddenTasks = scopeToHiddenTasksMap
					.get(aslSession.getScope());

			if (hiddenTasks == null) {
				hiddenTasks = new HashMap<String, TaskWrapper>();
				hiddenTasks.put(taskWrapper.getTask().getId().getValue(),
						taskWrapper);
				scopeToHiddenTasksMap.put(aslSession.getScope(), hiddenTasks);
			} else {
				hiddenTasks.put(taskWrapper.getTask().getId().getValue(),
						taskWrapper);
				scopeToHiddenTasksMap.put(aslSession.getScope(), hiddenTasks);

			}
			httpSession
					.removeAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_HIDDEN_MAP);
		}
		httpSession.setAttribute(
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_HIDDEN_MAP,
				scopeToHiddenTasksMap);

		return;

	}

	/**
	 * Retrieve Tasks In Background
	 * 
	 * @param httpSession
	 * @return
	 * @throws TDGWTSessionExpiredException
	 */
	public static HashMap<String, TaskWrapper> getTaskInBackgroundMap(
			HttpSession httpSession) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TaskWrapper>> scopeToTasksInBackgroundMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);
		if (scopeToTasksInBackgroundMap == null) {
			return null;
		} else {
			HashMap<String, TaskWrapper> tasksInBackground = scopeToTasksInBackgroundMap
					.get(aslSession.getScope());
			return tasksInBackground;

		}

	}

	/**
	 * Add Task In Background
	 * 
	 * @param httpSession
	 * @param taskWrapper
	 * @throws TDGWTSessionExpiredException
	 */
	public static void setTaskInBackground(HttpSession httpSession,
			TaskWrapper taskWrapper) throws TDGWTSessionExpiredException {
		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		if (taskWrapper == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask() == null
				|| taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains Task with invalid task id: "
					+ taskWrapper);
			return;
		}

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TaskWrapper>> scopeToTasksInBackgroundMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);

		if (scopeToTasksInBackgroundMap == null) {
			scopeToTasksInBackgroundMap = new HashMap<String, HashMap<String, TaskWrapper>>();
			HashMap<String, TaskWrapper> tasksInBackground = new HashMap<String, TaskWrapper>();
			tasksInBackground.put(taskWrapper.getTask().getId().getValue(),
					taskWrapper);
			scopeToTasksInBackgroundMap.put(aslSession.getScope(),
					tasksInBackground);
		} else {
			HashMap<String, TaskWrapper> tasksInBackground = scopeToTasksInBackgroundMap
					.get(aslSession.getScope());

			if (tasksInBackground == null) {
				tasksInBackground = new HashMap<String, TaskWrapper>();
				tasksInBackground.put(taskWrapper.getTask().getId().getValue(),
						taskWrapper);
				scopeToTasksInBackgroundMap.put(aslSession.getScope(),
						tasksInBackground);
			} else {
				tasksInBackground.put(taskWrapper.getTask().getId().getValue(),
						taskWrapper);
				scopeToTasksInBackgroundMap.put(aslSession.getScope(),
						tasksInBackground);

			}
			httpSession
					.removeAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);
		}
		httpSession.setAttribute(
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP,
				scopeToTasksInBackgroundMap);

		return;
	}

	/**
	 * Reomove Task from Tasks In Background
	 * 
	 * @param httpSession
	 * @param taskWrapper
	 * @throws TDGWTSessionExpiredException
	 */
	public static void removeTaskInBackground(HttpSession httpSession,
			TaskWrapper taskWrapper) throws TDGWTSessionExpiredException {

		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		if (taskWrapper == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask() == null
				|| taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains Task with invalid task id: "
					+ taskWrapper);
			return;
		}

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TaskWrapper>> scopeToTasksInBackgroundMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);

		if (scopeToTasksInBackgroundMap == null) {
			logger.debug("TaskWrapper was not acquired: " + taskWrapper);
		} else {
			HashMap<String, TaskWrapper> tasksInBackground = scopeToTasksInBackgroundMap
					.get(aslSession.getScope());
			if (tasksInBackground == null) {
				logger.debug("TaskWrapper was not acquired: " + taskWrapper);
			} else {
				tasksInBackground.remove(taskWrapper.getTask().getId()
						.getValue());
				scopeToTasksInBackgroundMap.put(aslSession.getScope(),
						tasksInBackground);
				httpSession
						.removeAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);
				httpSession
						.setAttribute(
								SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP,
								scopeToTasksInBackgroundMap);
			}
		}
		return;

	}

	/**
	 * Reomove All Background Tasks in the scope
	 * 
	 * @param httpSession
	 * @param taskWrapper
	 * @throws TDGWTSessionExpiredException
	 */
	public static void removeAllTasksInBackground(HttpSession httpSession)
			throws TDGWTSessionExpiredException {

		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TaskWrapper>> scopeToTasksInBackgroundMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);

		if (scopeToTasksInBackgroundMap == null) {
			logger.debug("No tasks in background in session");
		} else {
			HashMap<String, TaskWrapper> tasksInBackground = scopeToTasksInBackgroundMap
					.get(aslSession.getScope());
			if (tasksInBackground == null) {
				logger.debug("No tasks in background in session for scope: "
						+ aslSession.getScope());
			} else {
				scopeToTasksInBackgroundMap.remove(aslSession.getScope());
				httpSession
						.removeAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);
				httpSession
						.setAttribute(
								SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP,
								scopeToTasksInBackgroundMap);
			}
		}
		return;

	}

	/**
	 * Reomove Task on specific tabular resource from Tasks In Background
	 * 
	 * @param httpSession
	 * @param taskWrapper
	 * @throws TDGWTSessionExpiredException
	 */
	public static void removeTaskInBackgroundOnTRId(HttpSession httpSession,
			TRId trId) throws TDGWTSessionExpiredException {

		ASLSession aslSession = getAslSession(httpSession);
		ScopeProvider.instance.set(aslSession.getScope());

		if (trId == null) {
			logger.error("TRId is null");
			return;
		}

		if (trId.getId() == null || trId.getId().isEmpty()) {
			logger.error("TRId contains Id invalid: " + trId);
			return;
		}

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, TaskWrapper>> scopeToTasksInBackgroundMap = (HashMap<String, HashMap<String, TaskWrapper>>) httpSession
				.getAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);

		if (scopeToTasksInBackgroundMap == null) {
			logger.debug("No Background task in Session for TRId: " + trId);
		} else {
			HashMap<String, TaskWrapper> tasksInBackground = scopeToTasksInBackgroundMap
					.get(aslSession.getScope());
			if (tasksInBackground == null) {
				logger.debug("No Background task for TRId: " + trId);
			} else {
				ArrayList<String> removableKeys = new ArrayList<String>();
				for (String key : tasksInBackground.keySet()) {
					TaskWrapper taskWrapper = tasksInBackground.get(key);
					if (taskWrapper.getTrId() != null
							&& taskWrapper.getTrId().getId() != null
							&& taskWrapper.getTrId().getId()
									.compareTo(trId.getId()) == 0) {
						removableKeys.add(key);
					}
				}

				for (String key : removableKeys) {
					tasksInBackground.remove(key);
				}

				scopeToTasksInBackgroundMap.put(aslSession.getScope(),
						tasksInBackground);
				httpSession
						.removeAttribute(SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);
				httpSession
						.setAttribute(
								SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP,
								scopeToTasksInBackgroundMap);
			}
		}
		return;
	}

	//
	public static FileUploadMonitor getFileUploadMonitor(HttpSession httpSession) {
		FileUploadMonitor fileUploadMonitor = (FileUploadMonitor) httpSession
				.getAttribute(SessionConstants.FILE_UPLOAD_MONITOR);
		if (fileUploadMonitor != null) {
			return fileUploadMonitor;
		} else {
			fileUploadMonitor = new FileUploadMonitor();
			httpSession.setAttribute(SessionConstants.FILE_UPLOAD_MONITOR,
					fileUploadMonitor);
			return fileUploadMonitor;
		}
	}

	public static void setFileUploadMonitor(HttpSession httpSession,
			FileUploadMonitor fileUploadMonitor) {
		FileUploadMonitor fum = (FileUploadMonitor) httpSession
				.getAttribute(SessionConstants.FILE_UPLOAD_MONITOR);
		if (fum != null) {
			httpSession.removeAttribute(SessionConstants.FILE_UPLOAD_MONITOR);
		}
		httpSession.setAttribute(SessionConstants.FILE_UPLOAD_MONITOR,
				fileUploadMonitor);

	}

	//
	public static SaveResourceSession getSaveResourceSession(
			HttpSession httpSession) {
		SaveResourceSession saveResourceSession = (SaveResourceSession) httpSession
				.getAttribute(SessionConstants.RESOURCE_SAVE_SESSION);
		if (saveResourceSession != null) {
			return saveResourceSession;
		} else {
			saveResourceSession = new SaveResourceSession();
			httpSession.setAttribute(SessionConstants.RESOURCE_SAVE_SESSION,
					saveResourceSession);
			return saveResourceSession;
		}
	}

	public static void setSaveResourceSession(HttpSession httpSession,
			SaveResourceSession saveResourceSession) {
		SaveResourceSession srs = (SaveResourceSession) httpSession
				.getAttribute(SessionConstants.RESOURCE_SAVE_SESSION);
		if (srs != null) {
			httpSession.removeAttribute(SessionConstants.RESOURCE_SAVE_SESSION);
		}
		httpSession.setAttribute(SessionConstants.RESOURCE_SAVE_SESSION,
				saveResourceSession);

	}

}
