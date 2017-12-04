/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.portlets.user.td.gwtservice.server.file.CSVFileUploadSession;
import org.gcube.portlets.user.td.gwtservice.server.file.CodelistMappingFileUploadSession;
import org.gcube.portlets.user.td.gwtservice.server.trservice.TRTasksManager;
import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.chart.ChartTopRatingSession;
import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
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
import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXTemplateExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.source.SDMXRegistrySource;
import org.gcube.portlets.user.td.gwtservice.shared.statistical.DataMinerOperationSession;
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
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class SessionUtil {

	private static Logger logger = LoggerFactory.getLogger(SessionUtil.class);

	public static ServiceCredentials getServiceCredentials(HttpServletRequest httpServletRequest)
			throws TDGWTServiceException {
		return getServiceCredentials(httpServletRequest, null);
	}

	public static ServiceCredentials getServiceCredentials(HttpServletRequest httpServletRequest, String scopeGroupId)
			throws TDGWTServiceException {

		ServiceCredentials sCredentials = null;
		String userName = null;
		String scope = null;
		String token = null;
		String groupId = null;
		String groupName = null;

		if (Constants.DEBUG_MODE) {
			logger.info("No credential found in session, use test user!");	
			userName = Constants.DEFAULT_USER;
			scope = Constants.DEFAULT_SCOPE;
			token = Constants.DEFAULT_TOKEN;

			logger.info("Set SecurityToken: " + token);
			SecurityTokenProvider.instance.set(token);
			logger.info("Set ScopeProvider: " + scope);
			ScopeProvider.instance.set(scope);

			sCredentials = new ServiceCredentials(userName, scope, token);
			
			InfoLocale infoLocale = getInfoLocale(httpServletRequest, sCredentials);
			Locale locale = new Locale(infoLocale.getLanguage());
			
			ResourceBundle.getBundle(TDGWTServiceMessagesConstants.TDGWTServiceMessages,
					locale);

			
		} else {
			logger.info("Retrieving credential in session!");
			PortalContext pContext = PortalContext.getConfiguration();
			boolean hasScopeGroupId = false;

			if (scopeGroupId != null && !scopeGroupId.isEmpty()) {
				hasScopeGroupId = true;

			} else {
				hasScopeGroupId = false;
			}

			if (hasScopeGroupId) {
				scope = pContext.getCurrentScope(scopeGroupId);
			} else {
				scope = pContext.getCurrentScope(httpServletRequest);
			}

			if (scope == null || scope.isEmpty()) {
				String error = "Error retrieving scope: " + scope;
				logger.error(error);
				throw new TDGWTServiceException(error);
			}

			GCubeUser gCubeUser = pContext.getCurrentUser(httpServletRequest);

			if (gCubeUser == null) {
				String error = "Error retrieving gCubeUser in scope " + scope + ": " + gCubeUser;
				logger.error(error);
				throw new TDGWTServiceException(error);
			}

			userName = gCubeUser.getUsername();

			if (userName == null || userName.isEmpty()) {
				String error = "Error retrieving username in scope " + scope + ": " + userName;
				logger.error(error);
				throw new TDGWTServiceException(error);
			}

			token = pContext.getCurrentUserToken(scope, userName);

			if (token == null || token.isEmpty()) {
				String error = "Error retrieving token for " + userName + " in " + scope + ": " + token;
				logger.error(error);
				throw new TDGWTServiceException(error);
			}

			String name = gCubeUser.getFirstName();
			String lastName = gCubeUser.getLastName();
			String fullName = gCubeUser.getFullname();

			String userAvatarURL = gCubeUser.getUserAvatarURL();

			String email = gCubeUser.getEmail();

			if (hasScopeGroupId) {
				logger.info("Set SecurityToken: " + token);
				SecurityTokenProvider.instance.set(token);
				logger.info("Set ScopeProvider: " + scope);
				ScopeProvider.instance.set(scope);

				groupId = scopeGroupId;

				long gId;

				try {
					gId = Long.parseLong(scopeGroupId);
				} catch (Throwable e) {
					String error = "Error retrieving groupId: " + scopeGroupId;
					logger.error(error, e);
					throw new TDGWTServiceException(error);
				}

				GCubeGroup group;
				try {
					group = new LiferayGroupManager().getGroup(gId);
				} catch (Throwable e) {
					String error = "Error retrieving group: " + groupName;
					logger.error(error);
					throw new TDGWTServiceException(error);
				}

				groupName = group.getGroupName();

			} else {

				groupId = String.valueOf(pContext.getCurrentGroupId(httpServletRequest));

				groupName = pContext.getCurrentGroupName(httpServletRequest);

			}

			sCredentials = new ServiceCredentials(userName, fullName, name, lastName, email, scope, groupId, groupName,
					userAvatarURL, token);
		}

		logger.debug("ServiceCredentials: " + sCredentials);

		return sCredentials;
	}

	//

	public static InfoLocale getInfoLocale(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials) {
		InfoLocale infoLocale = null;
		String scope = serviceCredentials.getScope();
		HttpSession session = httpRequest.getSession();

		@SuppressWarnings("unchecked")
		HashMap<String, InfoLocale> infoLocaleMap = (HashMap<String, InfoLocale>) session
				.getAttribute(SessionConstants.INFO_LOCALE_MAP);

		if (infoLocaleMap != null) {
			if (infoLocaleMap.containsKey(scope)) {
				infoLocale = infoLocaleMap.get(scope);
			} else {
				infoLocale = new InfoLocale("en");
				infoLocaleMap.put(scope, infoLocale);
			}
		} else {
			infoLocale = new InfoLocale("en");
			infoLocaleMap = new HashMap<>();
			infoLocaleMap.put(scope, infoLocale);
			session.setAttribute(SessionConstants.INFO_LOCALE_MAP, infoLocaleMap);
		}
		return infoLocale;
	}

	public static void setInfoLocale(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			InfoLocale infoLocale) {
		HttpSession httpSession = httpRequest.getSession();
		String scope = serviceCredentials.getScope();
		@SuppressWarnings("unchecked")
		HashMap<String, InfoLocale> infoLocaleMap = (HashMap<String, InfoLocale>) httpSession
				.getAttribute(SessionConstants.INFO_LOCALE_MAP);
		if (infoLocaleMap != null) {
			infoLocaleMap.put(scope, infoLocale);
		} else {
			infoLocale = new InfoLocale("en");
			infoLocaleMap = new HashMap<>();
			infoLocaleMap.put(scope, infoLocale);
			httpSession.setAttribute(SessionConstants.INFO_LOCALE_MAP, infoLocaleMap);

		}

	}

	/**
	 * Set in session the Tabular Resource List retrieved from service only for
	 * caching
	 * 
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service credentials
	 * @param trs
	 *            list of tabular resource
	 */
	public static void setTabularResources(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			List<TabularResource> trs) {
		SessionOp<List<TabularResource>> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SCOPE_TO_TABULAR_RESOURCE_LIST_MAP, trs);
	}

	public static TRId getTRId(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials) {
		SessionOp<TRId> sessionOp = new SessionOp<>();
		TRId trId = sessionOp.get(httpRequest, serviceCredentials, SessionConstants.SCOPE_TO_CURRENT_TRID_MAP);
		return trId;
	}

	public static void setTRId(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials, TRId trId) {
		SessionOp<TRId> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SCOPE_TO_CURRENT_TRID_MAP, trId);
	}

	public static TabResource getTabResource(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials) {
		SessionOp<TabResource> sessionOp = new SessionOp<>();
		TabResource tabResource = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCE_MAP);
		return tabResource;

	}

	public static void setTabResource(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TabResource tabResource) {
		SessionOp<TabResource> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCE_MAP,
				tabResource);
	}

	public static ArrayList<TabResource> getCurrentTabularResourcesOpen(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<ArrayList<TabResource>> sessionOp = new SessionOp<>();
		ArrayList<TabResource> currentTROpen = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);
		return currentTROpen;
	}

	public static void setCurrentTabularResourcesOpen(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, ArrayList<TabResource> currentTROpen) {
		SessionOp<ArrayList<TabResource>> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP,
				currentTROpen);
	}

	public static void addToCurrentTabularResourcesOpen(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, TabResource tabResource) {
		if (tabResource.getTrId() != null && tabResource.getTrId().getId() != null
				&& !tabResource.getTrId().getId().isEmpty()) {

			HttpSession httpSession = httpRequest.getSession();

			@SuppressWarnings("unchecked")
			HashMap<String, ArrayList<TabResource>> scopeToTabResourceOpenMap = (HashMap<String, ArrayList<TabResource>>) httpSession
					.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);

			if (scopeToTabResourceOpenMap == null) {
				scopeToTabResourceOpenMap = new HashMap<>();
				ArrayList<TabResource> openList = new ArrayList<>();
				openList.add(tabResource);
				scopeToTabResourceOpenMap.put(serviceCredentials.getScope(), openList);
				httpSession.setAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP,
						scopeToTabResourceOpenMap);
			} else {
				ArrayList<TabResource> openList = scopeToTabResourceOpenMap.get(serviceCredentials.getScope());

				if (openList != null) {
					for (TabResource tr : openList) {
						if (tr.getTrId().getId().compareTo(tabResource.getTrId().getId()) == 0) {
							openList.remove(tr);
							break;
						}
					}
					openList.add(tabResource);
				} else {
					openList = new ArrayList<>();
					openList.add(tabResource);
				}
				scopeToTabResourceOpenMap.put(serviceCredentials.getScope(), openList);
			}

		} else {
			logger.error("Tabular Resource has invalid id");
		}

	}

	public static void removeFromCurrentTabularResourcesOpen(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, TRId trId) {

		if (trId != null && trId.getId() != null & !trId.getId().isEmpty()) {
			HttpSession httpSession = httpRequest.getSession();

			@SuppressWarnings("unchecked")
			HashMap<String, ArrayList<TabResource>> scopeToTabResourceOpenMap = (HashMap<String, ArrayList<TabResource>>) httpSession
					.getAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);

			if (scopeToTabResourceOpenMap == null) {
				scopeToTabResourceOpenMap = new HashMap<>();
				ArrayList<TabResource> openList = new ArrayList<>();
				scopeToTabResourceOpenMap.put(serviceCredentials.getScope(), openList);
				httpSession.setAttribute(SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP,
						scopeToTabResourceOpenMap);
				logger.debug("No open tr list in session");
			} else {
				ArrayList<TabResource> openList = scopeToTabResourceOpenMap.get(serviceCredentials.getScope());
				if (openList != null) {
					for (TabResource tr : openList) {
						if (tr.getTrId().getId().compareTo(trId.getId()) == 0) {
							openList.remove(tr);
							break;
						}
					}

				} else {
					openList = new ArrayList<>();
				}
				scopeToTabResourceOpenMap.put(serviceCredentials.getScope(), openList);
			}

		} else {
			logger.error("TRId request has invalid id");
		}
	}

	public static void removeAllFromCurrentTabularResourcesOpen(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<ArrayList<TabResource>> sessionOp = new SessionOp<>();
		sessionOp.remove(httpRequest, serviceCredentials, SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);
	}

	public static TabResource getFirstFromCurrentTabularResourcesOpen(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<ArrayList<TabResource>> sessionOp = new SessionOp<>();
		ArrayList<TabResource> openList = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);

		if (openList == null || openList.isEmpty()) {
			logger.info("No open tr in session");
			return null;
		} else {
			return openList.get(0);
		}

	}

	public static TabResource getFromCurrentTabularResourcesOpen(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, TRId trId) {
		SessionOp<ArrayList<TabResource>> sessionOp = new SessionOp<>();
		ArrayList<TabResource> openList = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_CURRENT_TABULAR_RESOURCES_OPEN_MAP);
		if (openList == null || openList.isEmpty()) {
			logger.info("No open tr in session");
		} else {
			logger.debug("Current Tabular Open In session: " + serviceCredentials.getScope() + ", trList: " + openList);
			for (TabResource tabResource : openList) {
				if (tabResource != null && tabResource.getTrId() != null
						&& tabResource.getTrId().getId().compareTo(trId.getId()) == 0) {
					return tabResource;
				}
			}
			logger.info("No tabular resource open in session with: " + trId);

		}
		return null;
	}

	public static void setTDOpenSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TDOpenSession tdOpenSession) {
		SessionOp<TDOpenSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.TDOPEN_SESSION, tdOpenSession);
	}

	public static TabResource getSDMXImportTabResource(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<TabResource> sessionOp = new SessionOp<>();
		TabResource tabResource = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SDMX_IMPORT_TABULAR_RESOURCE);
		return tabResource;
	}

	public static void setSDMXImportTabResource(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TabResource tabResource) {
		SessionOp<TabResource> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SDMX_IMPORT_TABULAR_RESOURCE, tabResource);

	}

	public static TabResource getCSVImportTabResource(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<TabResource> sessionOp = new SessionOp<>();
		TabResource tabResource = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CSV_IMPORT_TABULAR_RESOURCE);
		return tabResource;
	}

	public static void setCSVImportTabResource(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TabResource tabResource) {
		SessionOp<TabResource> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CSV_IMPORT_TABULAR_RESOURCE, tabResource);
	}

	public static void setSDMXImportSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			SDMXImportSession sdmxImportSession) {
		SessionOp<SDMXImportSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SDMX_IMPORT_SESSION, sdmxImportSession);
	}

	public static SDMXImportSession getSDMXImportSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {

		SessionOp<SDMXImportSession> sessionOp = new SessionOp<>();
		SDMXImportSession sdmxImportSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SDMX_IMPORT_SESSION);
		return sdmxImportSession;

	}

	public static void setCSVImportSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			CSVImportSession csvImportSession) {
		SessionOp<CSVImportSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CSV_IMPORT_SESSION, csvImportSession);
	}

	public static CSVImportSession getCSVImportSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<CSVImportSession> sessionOp = new SessionOp<>();
		CSVImportSession csvImportSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CSV_IMPORT_SESSION);
		return csvImportSession;
	}

	public static void setCSVExportSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			CSVExportSession csvExportSession) {
		SessionOp<CSVExportSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CSV_EXPORT_SESSION, csvExportSession);
	}

	public static CSVExportSession getCSVExportSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<CSVExportSession> sessionOp = new SessionOp<>();
		CSVExportSession csvExportSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CSV_EXPORT_SESSION);
		return csvExportSession;

	}

	public static void setInternalSDMXRegistryURLSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			String defaultSDMXRegistryUrl) {
		SessionOp<String> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SDMX_INTERNAL_REGISTRY_URL, defaultSDMXRegistryUrl);
	}

	public static String getInternalSDMXRegistryURLSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<String> sessionOp = new SessionOp<>();
		String defaultSDMXRegistryURL = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SDMX_INTERNAL_REGISTRY_URL);
		return defaultSDMXRegistryURL;
	}

	
	
	public static void setSDMXExportSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			SDMXExportSession sdmxExportSession) {
		SessionOp<SDMXExportSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SDMX_EXPORT_SESSION, sdmxExportSession);
	}

	public static SDMXExportSession getSDMXExportSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<SDMXExportSession> sessionOp = new SessionOp<>();
		SDMXExportSession sdmxExportSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SDMX_EXPORT_SESSION);
		return sdmxExportSession;
	}

	public static void setSDMXTemplateExportSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, SDMXTemplateExportSession sdmxTemplateExportSession) {
		SessionOp<SDMXTemplateExportSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SDMX_TEMPLATE_EXPORT_SESSION,
				sdmxTemplateExportSession);
	}

	public static SDMXTemplateExportSession getSDMXTemplateExportSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<SDMXTemplateExportSession> sessionOp = new SessionOp<>();
		SDMXTemplateExportSession sdmxTemplateExportSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SDMX_TEMPLATE_EXPORT_SESSION);
		return sdmxTemplateExportSession;
	}

	public static void setJSONExportSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			JSONExportSession jsonExportSession) {
		SessionOp<JSONExportSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.JSON_EXPORT_SESSION, jsonExportSession);

	}

	public static JSONExportSession getJSONExportSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<JSONExportSession> sessionOp = new SessionOp<>();
		JSONExportSession jsonExportSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.JSON_EXPORT_SESSION);
		return jsonExportSession;

	}

	//
	public static void setCSVFileUploadSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			CSVFileUploadSession csvFileUploadSession) {
		SessionOp<CSVFileUploadSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CSV_IMPORT_FILE_UPLOAD_SESSION,
				csvFileUploadSession);

	}

	public static CSVFileUploadSession getCSVFileUploadSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<CSVFileUploadSession> sessionOp = new SessionOp<>();
		CSVFileUploadSession csvFileUploadSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CSV_IMPORT_FILE_UPLOAD_SESSION);
		return csvFileUploadSession;
	}

	//
	public static void setCodelistMappingFileUploadSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, CodelistMappingFileUploadSession codelistMappingFileUploadSession) {
		SessionOp<CodelistMappingFileUploadSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CODELIST_MAPPING_FILE_UPLOAD_SESSION,
				codelistMappingFileUploadSession);
	}

	public static CodelistMappingFileUploadSession getCodelistMappingFileUploadSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<CodelistMappingFileUploadSession> sessionOp = new SessionOp<>();
		CodelistMappingFileUploadSession codelistMappingFileUploadSession = sessionOp.get(httpRequest,
				serviceCredentials, SessionConstants.CODELIST_MAPPING_FILE_UPLOAD_SESSION);
		return codelistMappingFileUploadSession;

	}

	//
	public static ArrayList<Codelist> retrieveCodelists(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws Exception {
		logger.info("SessionUtil retrieveCodelists");
		ScopeProvider.instance.set(serviceCredentials.getScope());
		SDMXClient client = getSdmxClient(httpRequest, serviceCredentials);
		return client.getAllCodelists();
	}

	public static ArrayList<Dataset> retrieveDatasets(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws Exception {
		logger.info("SessionUtil retrieveDatasets");
		ScopeProvider.instance.set(serviceCredentials.getScope());
		SDMXClient client = getSdmxClient(httpRequest, serviceCredentials);
		return client.getAllDatasets();
	}

	public static ArrayList<Agencies> retrieveAgencies(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws Exception {
		logger.info("SessionUtil retrieveAgencies");
		ScopeProvider.instance.set(serviceCredentials.getScope());
		SDMXClient client = getSdmxClient(httpRequest, serviceCredentials);
		return client.getAllAgencies();
	}

	//
	public static void setSDMXRegistrySource(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			SDMXRegistrySource sdmxRegistrySource) {
		ScopeProvider.instance.set(serviceCredentials.getScope());
		SessionOp<SDMXRegistrySource> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SDMX_REGISTRY_SOURCE, sdmxRegistrySource);

	}

	public static SDMXRegistrySource getSDMXRegistrySource(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<SDMXRegistrySource> sessionOp = new SessionOp<>();
		SDMXRegistrySource sdmxRegistrySource = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SDMX_REGISTRY_SOURCE);
		return sdmxRegistrySource;

	}

	//
	public static SDMXClient getSDMXClientInSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {
		SessionOp<SDMXClient> sessionOp = new SessionOp<>();
		SDMXClient sdmxClient = sessionOp.get(httpRequest, serviceCredentials, SessionConstants.SDMX_CLIENT_ATTRIBUTE);
		return sdmxClient;
	}

	public static void setSDMXClientInSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			SDMXClient sdmxClient) {
		SessionOp<SDMXClient> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SDMX_CLIENT_ATTRIBUTE, sdmxClient);
	}

	//
	public static SDMXClient getSdmxClient(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials) {
		// HttpSession httpSession = httpRequest.getSession();
		SDMXClient sdmxClient = getSDMXClientInSession(httpRequest, serviceCredentials);
		SDMXRegistrySource sdmxRegistrySource = getSDMXRegistrySource(httpRequest, serviceCredentials);
		if (sdmxRegistrySource == null) {
			logger.error("SDMXRegistrySource was not acquired");
		} else {
			String url = sdmxRegistrySource.getUrl();
			if (url == null || url.isEmpty()) {
				if (sdmxClient == null) {
					sdmxClient = new SDMXClient();
				} else {
					if (sdmxClient.getType().compareTo(SDMXClient.TYPE.ANOTHER) == 0) {
						sdmxClient = new SDMXClient();
					}
				}
			} else {
				if (sdmxClient == null) {
					sdmxClient = new SDMXClient(url);
				} else {
					if (sdmxClient.getType().compareTo(SDMXClient.TYPE.INTERNAL) == 0) {
						sdmxClient = new SDMXClient(url);
					} else {
						if (sdmxClient.getUrl().compareTo(url) != 0) {
							sdmxClient = new SDMXClient(url);
						}
					}
				}
			}
			setSDMXClientInSession(httpRequest, serviceCredentials, sdmxClient);

		}

		return sdmxClient;
	}

	//
	public static void setCSVExportEnd(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			Boolean end) {
		SessionOp<Boolean> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CSV_EXPORT_END, end);

	}

	public static Boolean getCSVExportEnd(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials) {
		SessionOp<Boolean> sessionOp = new SessionOp<>();
		Boolean end = sessionOp.get(httpRequest, serviceCredentials, SessionConstants.CSV_EXPORT_END);
		if (end == null) {
			end = false;
		}
		return end;
	}

	//
	public static void setJSONExportEnd(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			Boolean end) {
		SessionOp<Boolean> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.JSON_EXPORT_END, end);

	}

	public static Boolean getJSONExportEnd(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials) {
		SessionOp<Boolean> sessionOp = new SessionOp<>();
		Boolean end = sessionOp.get(httpRequest, serviceCredentials, SessionConstants.JSON_EXPORT_END);
		if (end == null) {
			end = false;
		}
		return end;
	}

	//
	public static TRTasksManager getTRTasksManager(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<TRTasksManager> sessionOp = new SessionOp<>();
		TRTasksManager tasksManager = sessionOp.get(httpRequest, serviceCredentials, SessionConstants.TR_TASK_MANAGER,
				TRTasksManager.class);

		return tasksManager;

	}

	public static void setTRTasksManager(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TRTasksManager trTasksManager) {
		SessionOp<TRTasksManager> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.TR_TASK_MANAGER, trTasksManager);
	}

	//
	public static ChangeColumnTypeSession getChangeColumnTypeSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ChangeColumnTypeSession> sessionOp = new SessionOp<>();
		ChangeColumnTypeSession changeColumnTypeSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CHANGE_COLUMN_TYPE_SESSION, ChangeColumnTypeSession.class);

		return changeColumnTypeSession;

	}

	public static void setChangeColumnTypeSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			ChangeColumnTypeSession changeColumnTypeSession) {
		SessionOp<ChangeColumnTypeSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CHANGE_COLUMN_TYPE_SESSION,
				changeColumnTypeSession);
	}

	//
	public static LabelColumnSession getLabelColumnSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<LabelColumnSession> sessionOp = new SessionOp<>();
		LabelColumnSession labelColumnSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CHANGE_THE_COLUMN_LABEL_SESSION, LabelColumnSession.class);

		return labelColumnSession;
	}

	//
	public static void setLabelColumnSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			LabelColumnSession labelColumnSession) {
		SessionOp<LabelColumnSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CHANGE_THE_COLUMN_LABEL_SESSION,
				labelColumnSession);
	}

	//
	public static AddColumnSession getAddColumnSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<AddColumnSession> sessionOp = new SessionOp<>();
		AddColumnSession addColumnSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.ADD_COLUMN_SESSION, AddColumnSession.class);

		return addColumnSession;
	}

	public static void setAddColumnSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			AddColumnSession addColumnSession) {
		SessionOp<AddColumnSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.ADD_COLUMN_SESSION, addColumnSession);
	}

	//
	public static DeleteColumnSession getDeleteColumnSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<DeleteColumnSession> sessionOp = new SessionOp<>();
		DeleteColumnSession deleteColumnSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.DELETE_COLUMN_SESSION, DeleteColumnSession.class);

		return deleteColumnSession;

	}

	public static void setDeleteColumnSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			DeleteColumnSession deleteColumnSession) {
		SessionOp<DeleteColumnSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.DELETE_COLUMN_SESSION, deleteColumnSession);
	}

	//
	public static FilterColumnSession getFilterColumnSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<FilterColumnSession> sessionOp = new SessionOp<>();
		FilterColumnSession filterColumnSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.FILTER_COLUMN_SESSION, FilterColumnSession.class);
		return filterColumnSession;

	}

	public static void setFilterColumnSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			FilterColumnSession filterColumnSession) {
		SessionOp<FilterColumnSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.FILTER_COLUMN_SESSION, filterColumnSession);

	}

	//
	public static ReplaceColumnByExpressionSession getReplaceColumnByExpressionSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ReplaceColumnByExpressionSession> sessionOp = new SessionOp<>();
		ReplaceColumnByExpressionSession replaceColumnByExpressionSession = sessionOp.get(httpRequest,
				serviceCredentials, SessionConstants.REPLACE_COLUMN_BY_EXPRESSION_SESSION,
				ReplaceColumnByExpressionSession.class);
		return replaceColumnByExpressionSession;
	}

	public static void setReplaceColumnByExpressionSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, ReplaceColumnByExpressionSession replaceColumnByExpressionSession) {
		SessionOp<ReplaceColumnByExpressionSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.REPLACE_COLUMN_BY_EXPRESSION_SESSION,
				replaceColumnByExpressionSession);
	}

	//
	public static ApplyAndDetachColumnRulesSession getRulesOnColumnApplyAndDetachSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ApplyAndDetachColumnRulesSession> sessionOp = new SessionOp<>();
		ApplyAndDetachColumnRulesSession applyColumnRulesSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.RULES_ON_COLUMN_APPLY_AND_DETACH_SESSION, ApplyAndDetachColumnRulesSession.class);
		return applyColumnRulesSession;
	}

	public static void setRulesOnColumnApplyAndDetachSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, ApplyAndDetachColumnRulesSession applyColumnRulesSession) {
		SessionOp<ApplyAndDetachColumnRulesSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.RULES_ON_COLUMN_APPLY_AND_DETACH_SESSION,
				applyColumnRulesSession);
	}

	//
	public static ApplyTableRuleSession getApplyTableRuleSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ApplyTableRuleSession> sessionOp = new SessionOp<>();
		ApplyTableRuleSession applyTableRuleSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.RULES_ON_TABLE_APPLY_SESSION, ApplyTableRuleSession.class);
		return applyTableRuleSession;
	}

	public static void setApplyTableRuleSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			ApplyTableRuleSession applyTableRuleSession) {
		SessionOp<ApplyTableRuleSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.RULES_ON_TABLE_APPLY_SESSION,
				applyTableRuleSession);
	}

	//
	public static DetachColumnRulesSession getDetachColumnRulesSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<DetachColumnRulesSession> sessionOp = new SessionOp<>();
		DetachColumnRulesSession detachColumnRulesSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.RULES_ON_COLUMN_DETACH_SESSION, DetachColumnRulesSession.class);
		return detachColumnRulesSession;
	}

	public static void setDetachColumnRulesSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, DetachColumnRulesSession detachColumnRulesSession) {
		SessionOp<DetachColumnRulesSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.RULES_ON_COLUMN_DETACH_SESSION,
				detachColumnRulesSession);
	}

	//
	public static DetachTableRulesSession getDetachTableRulesSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<DetachTableRulesSession> sessionOp = new SessionOp<>();
		DetachTableRulesSession detachTableRulesSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.RULES_ON_TABLE_DETACH_SESSION, DetachTableRulesSession.class);
		return detachTableRulesSession;
	}

	public static void setDetachTableRulesSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			DetachTableRulesSession detachTableRulesSession) {
		SessionOp<DetachTableRulesSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.RULES_ON_TABLE_DETACH_SESSION,
				detachTableRulesSession);
	}

	//
	public static TaskResubmitSession getTaskResubmitSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<TaskResubmitSession> sessionOp = new SessionOp<>();
		TaskResubmitSession taskResubmitSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.TASK_RESUBMIT_SESSION, TaskResubmitSession.class);
		return taskResubmitSession;
	}

	public static void setTaskResubmitSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TaskResubmitSession taskResubmitSession) {
		SessionOp<TaskResubmitSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.TASK_RESUBMIT_SESSION, taskResubmitSession);
	}

	//
	public static TaskResumeSession getTaskResumeSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<TaskResumeSession> sessionOp = new SessionOp<>();
		TaskResumeSession taskResumeSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.TASK_RESUME_SESSION, TaskResumeSession.class);
		return taskResumeSession;

	}

	public static void setTaskResumeSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TaskResumeSession taskResumeSession) {
		SessionOp<TaskResumeSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.TASK_RESUME_SESSION, taskResumeSession);
	}

	//
	public static EditRowSession getEditRowSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<EditRowSession> sessionOp = new SessionOp<>();
		EditRowSession editRowSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.EDIT_ROW_SESSION, EditRowSession.class);
		return editRowSession;
	}

	public static void setEditRowSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			EditRowSession editRowSession) {
		SessionOp<EditRowSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.EDIT_ROW_SESSION, editRowSession);
	}

	//
	public static ReplaceColumnSession getReplaceColumnSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ReplaceColumnSession> sessionOp = new SessionOp<>();
		ReplaceColumnSession replaceColumnSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.REPLACE_COLUMN_SESSION, ReplaceColumnSession.class);
		return replaceColumnSession;
	}

	public static void setReplaceColumnSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			ReplaceColumnSession replaceColumnSession) {
		SessionOp<ReplaceColumnSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.REPLACE_COLUMN_SESSION, replaceColumnSession);
	}

	//
	public static ReplaceBatchColumnSession getReplaceBatchColumnSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ReplaceBatchColumnSession> sessionOp = new SessionOp<>();
		ReplaceBatchColumnSession replaceBatchColumnSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.REPLACE_BATCH_COLUMN_SESSION, ReplaceBatchColumnSession.class);
		return replaceBatchColumnSession;
	}

	public static void setReplaceBatchColumnSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, ReplaceBatchColumnSession replaceBatchColumnSession) {
		SessionOp<ReplaceBatchColumnSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.REPLACE_BATCH_COLUMN_SESSION,
				replaceBatchColumnSession);
	}

	//
	public static ChangeTableTypeSession getChangeTableTypeSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ChangeTableTypeSession> sessionOp = new SessionOp<>();
		ChangeTableTypeSession changeTableTypeSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CHANGE_TABLE_TYPE_SESSION, ChangeTableTypeSession.class);
		return changeTableTypeSession;
	}

	public static void setChangeTableTypeSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			ChangeTableTypeSession changeTableTypeSession) {
		SessionOp<ChangeTableTypeSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CHANGE_TABLE_TYPE_SESSION,
				changeTableTypeSession);
	}

	//
	public static CloneTabularResourceSession getCloneTabularResourceSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<CloneTabularResourceSession> sessionOp = new SessionOp<>();
		CloneTabularResourceSession cloneTabularResourceSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CLONE_TABULAR_RESOURCE_SESSION, CloneTabularResourceSession.class);
		return cloneTabularResourceSession;
	}

	public static void setCloneTabularResourceSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, CloneTabularResourceSession cloneTabularResourceSession) {
		SessionOp<CloneTabularResourceSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CLONE_TABULAR_RESOURCE_SESSION,
				cloneTabularResourceSession);
	}

	//
	public static DeleteRowsSession getDeleteRowsSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<DeleteRowsSession> sessionOp = new SessionOp<>();
		DeleteRowsSession deleteRowsSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.DELETE_ROWS_SESSION, DeleteRowsSession.class);
		return deleteRowsSession;
	}

	public static void setDeleteRowsSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			DeleteRowsSession deleteRowsSession) {
		SessionOp<DeleteRowsSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.DELETE_ROWS_SESSION, deleteRowsSession);
	}

	//
	public static TemplateApplySession getTemplateApplySession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<TemplateApplySession> sessionOp = new SessionOp<>();
		TemplateApplySession templateApplySession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.TEMPLATE_APPLY_SESSION, TemplateApplySession.class);
		return templateApplySession;
	}

	public static void setTemplateApplySession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TemplateApplySession templateApplySession) {
		SessionOp<TemplateApplySession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.TEMPLATE_APPLY_SESSION, templateApplySession);
	}

	//
	public static DuplicatesSession getDuplicatesSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<DuplicatesSession> sessionOp = new SessionOp<>();
		DuplicatesSession duplicatesSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.DUPLICATES_ROWS_SESSION, DuplicatesSession.class);
		return duplicatesSession;

	}

	public static void setDuplicatesSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			DuplicatesSession duplicatesSession) {
		SessionOp<DuplicatesSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.DUPLICATES_ROWS_SESSION, duplicatesSession);
	}

	//
	public static ArrayList<TabResource> getCodelistsPagingLoaded(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ArrayList<TabResource>> sessionOp = new SessionOp<>();
		ArrayList<TabResource> ltr = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CODELISTS_PAGING_LOADED);
		return ltr;
	}

	public static void setCodelistsPagingLoaded(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			ArrayList<TabResource> listCodelists) {
		SessionOp<ArrayList<TabResource>> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CODELISTS_PAGING_LOADED, listCodelists);
	}

	//
	public static ArrayList<TabResource> getCodelistsPagingLoadedFilteredCopy(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ArrayList<TabResource>> sessionOp = new SessionOp<>();
		ArrayList<TabResource> ltrFilteredCopy = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CODELISTS_PAGING_LOADED_FILTERED_COPY);
		return ltrFilteredCopy;
	}

	public static void setCodelistsPagingLoadedFilteredCopy(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, ArrayList<TabResource> listCodelistsFilteredCopy) {
		SessionOp<ArrayList<TabResource>> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CODELISTS_PAGING_LOADED_FILTERED_COPY,
				listCodelistsFilteredCopy);
	}

	//
	public static String getCodelistsPagingLoadedFilter(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<String> sessionOp = new SessionOp<>();
		String filter = sessionOp.get(httpRequest, serviceCredentials, SessionConstants.CODELISTS_PAGING_LOADED_FILTER);
		return filter;

	}

	public static void setCodelistsPagingLoadedFilter(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, String filter) {
		SessionOp<String> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CODELISTS_PAGING_LOADED_FILTER, filter);
	}

	//
	public static RollBackSession getRollBackSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<RollBackSession> sessionOp = new SessionOp<>();
		RollBackSession rollBackSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.ROLLBACK_SESSION, RollBackSession.class);
		return rollBackSession;
	}

	public static void setRollBackSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			RollBackSession rollBackSession) {
		SessionOp<RollBackSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.ROLLBACK_SESSION, rollBackSession);
	}

	//
	public static ExtractCodelistSession getExtractCodelistSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ExtractCodelistSession> sessionOp = new SessionOp<>();
		ExtractCodelistSession extractCodelistSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.EXTRACT_CODELIST_SESSION, ExtractCodelistSession.class);
		return extractCodelistSession;
	}

	public static void setExtractCodelistSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			ExtractCodelistSession extractCodelistSession) {
		SessionOp<ExtractCodelistSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.EXTRACT_CODELIST_SESSION,
				extractCodelistSession);
	}

	//
	public static SplitColumnSession getSplitColumnSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<SplitColumnSession> sessionOp = new SessionOp<>();
		SplitColumnSession splitColumnSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SPLIT_COLUMN_SESSION, SplitColumnSession.class);
		return splitColumnSession;
	}

	public static void setSplitColumnSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			SplitColumnSession splitColumnSession) {
		SessionOp<SplitColumnSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SPLIT_COLUMN_SESSION, splitColumnSession);
	}

	//
	public static MergeColumnSession getMergeColumnSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<MergeColumnSession> sessionOp = new SessionOp<>();
		MergeColumnSession mergeColumnSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.MERGE_COLUMN_SESSION, MergeColumnSession.class);
		return mergeColumnSession;
	}

	public static void setMergeColumnSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			MergeColumnSession mergeColumnSession) {
		SessionOp<MergeColumnSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.MERGE_COLUMN_SESSION, mergeColumnSession);
	}

	//
	public static GroupBySession getGroupBySession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<GroupBySession> sessionOp = new SessionOp<>();
		GroupBySession groupBySession = sessionOp.get(httpRequest, serviceCredentials, SessionConstants.GROUPBY_SESSION,
				GroupBySession.class);
		return groupBySession;

	}

	public static void setGroupBySession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			GroupBySession groupBySession) {
		SessionOp<GroupBySession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.GROUPBY_SESSION, groupBySession);
	}

	//
	public static TimeAggregationSession getTimeAggregationSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<TimeAggregationSession> sessionOp = new SessionOp<>();
		TimeAggregationSession timeAggregationSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.TIME_AGGREGATION_SESSION, TimeAggregationSession.class);
		return timeAggregationSession;
	}

	public static void setTimeAggregationSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TimeAggregationSession timeAggregationSession) {
		SessionOp<TimeAggregationSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.TIME_AGGREGATION_SESSION,
				timeAggregationSession);
	}

	//
	public static CodelistMappingSession getCodelistMappingSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<CodelistMappingSession> sessionOp = new SessionOp<>();
		CodelistMappingSession codelistMappingSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CODELIST_MAPPING_SESSION);
		return codelistMappingSession;
	}

	public static void setCodelistMappingSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			CodelistMappingSession codelistMappingSession) {
		SessionOp<CodelistMappingSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CODELIST_MAPPING_SESSION,
				codelistMappingSession);
	}

	//
	public static NormalizationSession getNormalizationSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<NormalizationSession> sessionOp = new SessionOp<>();
		NormalizationSession normalizationSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.NORMALIZATION_SESSION, NormalizationSession.class);
		return normalizationSession;

	}

	public static void setNormalizationSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			NormalizationSession normalizationSession) {
		SessionOp<NormalizationSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.NORMALIZATION_SESSION, normalizationSession);
	}

	//
	public static DenormalizationSession getDenormalizationSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<DenormalizationSession> sessionOp = new SessionOp<>();
		DenormalizationSession denormalizationSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.DENORMALIZATION_SESSION, DenormalizationSession.class);
		return denormalizationSession;
	}

	public static void setDenormalizationSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			DenormalizationSession denormalizationSession) {
		SessionOp<DenormalizationSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.DENORMALIZATION_SESSION,
				denormalizationSession);
	}

	//
	public static UnionSession getUnionSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials)
			throws TDGWTServiceException {
		SessionOp<UnionSession> sessionOp = new SessionOp<>();
		UnionSession unionSession = sessionOp.get(httpRequest, serviceCredentials, SessionConstants.UNION_SESSION,
				UnionSession.class);
		return unionSession;
	}

	public static void setUnionSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			UnionSession unionSession) {
		SessionOp<UnionSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.UNION_SESSION, unionSession);
	}

	//
	public static ChangeColumnsPositionSession getChangeColumnsPositionSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ChangeColumnsPositionSession> sessionOp = new SessionOp<>();
		ChangeColumnsPositionSession changeColumnsPositionSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CHANGE_COLUMNS_POSITION_SESSION, ChangeColumnsPositionSession.class);
		return changeColumnsPositionSession;
	}

	public static void setChangeColumnsPositionSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, ChangeColumnsPositionSession changeColumnsPositionSession) {
		SessionOp<ChangeColumnsPositionSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CHANGE_COLUMNS_POSITION_SESSION,
				changeColumnsPositionSession);
	}

	//
	public static ReplaceByExternalSession getReplaceByExternalSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ReplaceByExternalSession> sessionOp = new SessionOp<>();
		ReplaceByExternalSession replaceByExternalSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.REPLACE_BY_EXTERNAL_SESSION, ReplaceByExternalSession.class);
		return replaceByExternalSession;
	}

	public static void setReplaceByExternalSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, ReplaceByExternalSession replaceByExternalSession) {
		SessionOp<ReplaceByExternalSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.REPLACE_BY_EXTERNAL_SESSION,
				replaceByExternalSession);
	}

	//
	public static DataMinerOperationSession getDataMinerOperationSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<DataMinerOperationSession> sessionOp = new SessionOp<>();
		DataMinerOperationSession dataMinerOperationSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.DATAMINER_OPERATION_SESSION, DataMinerOperationSession.class);
		return dataMinerOperationSession;
	}

	public static void setDataMinerOperationSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, DataMinerOperationSession dataMinerOperationSession) {
		SessionOp<DataMinerOperationSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.DATAMINER_OPERATION_SESSION,
				dataMinerOperationSession);
	}

	//
	public static MapCreationSession getMapCreationSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<MapCreationSession> sessionOp = new SessionOp<>();
		MapCreationSession mapCreationSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.MAP_CREATION_SESSION, MapCreationSession.class);
		return mapCreationSession;
	}

	public static void setMapCreationSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			MapCreationSession mapCreationSession) {
		SessionOp<MapCreationSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.MAP_CREATION_SESSION, mapCreationSession);
	}

	//
	public static ChartTopRatingSession getChartTopRatingSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<ChartTopRatingSession> sessionOp = new SessionOp<>();
		ChartTopRatingSession chartTopRatingSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.CHART_TOPRATING_SESSION, ChartTopRatingSession.class);
		return chartTopRatingSession;
	}

	public static void setChartTopRatingSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			ChartTopRatingSession chartTopRatingSession) {
		SessionOp<ChartTopRatingSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.CHART_TOPRATING_SESSION, chartTopRatingSession);
	}

	// Geospatial
	public static GeospatialCreateCoordinatesSession getGeospatialCreateCoordinatesSession(
			HttpServletRequest httpRequest, ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<GeospatialCreateCoordinatesSession> sessionOp = new SessionOp<>();
		GeospatialCreateCoordinatesSession geospatialCreateCoordinatesSession = sessionOp.get(httpRequest,
				serviceCredentials, SessionConstants.GEOSPATIAL_CREATE_COORDINATES_SESSION,
				GeospatialCreateCoordinatesSession.class);
		return geospatialCreateCoordinatesSession;
	}

	public static void setGeospatialCreateCoordinatesSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials,
			GeospatialCreateCoordinatesSession geospatialCreateCoordinateSession) {
		SessionOp<GeospatialCreateCoordinatesSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.GEOSPATIAL_CREATE_COORDINATES_SESSION,
				geospatialCreateCoordinateSession);
	}

	//
	public static GeospatialDownscaleCSquareSession getGeospatialDownscaleCSquareSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<GeospatialDownscaleCSquareSession> sessionOp = new SessionOp<>();
		GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession = sessionOp.get(httpRequest,
				serviceCredentials, SessionConstants.GEOSPATIAL_DOWNSCALE_CSQUARE_SESSION,
				GeospatialDownscaleCSquareSession.class);
		return geospatialDownscaleCSquareSession;
	}

	public static void setGeospatialDownscaleCSquareSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials,
			GeospatialDownscaleCSquareSession geospatialDownscaleCSquareSession) {
		SessionOp<GeospatialDownscaleCSquareSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.GEOSPATIAL_DOWNSCALE_CSQUARE_SESSION,
				geospatialDownscaleCSquareSession);
	}

	// Geometry
	public static GeometryCreatePointSession getGeometryCreatePointSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<GeometryCreatePointSession> sessionOp = new SessionOp<>();
		GeometryCreatePointSession geometryCreatePointSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.GEOMETRY_CREATE_POINT_SESSION, GeometryCreatePointSession.class);
		return geometryCreatePointSession;
	}

	public static void setGeometryCreatePointSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, GeometryCreatePointSession geometryCreatePointSession) {
		SessionOp<GeometryCreatePointSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.GEOMETRY_CREATE_POINT_SESSION,
				geometryCreatePointSession);
	}

	//
	/**
	 * Retrieve task started
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service Credentials
	 * @param taskId
	 *            Task id
	 * @return Task Wrapper
	 */
	public static TaskWrapper getStartedTask(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			String taskId) {

		TaskWrapper taskWrapper = null;

		if (taskId == null || taskId.isEmpty()) {
			logger.error("TaskId is not valid: " + taskId);
		} else {
			SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
			HashMap<String, TaskWrapper> tasksStarted = sessionOp.get(httpRequest, serviceCredentials,
					SessionConstants.SCOPE_TO_OPERATIONS_TASKS_STARTED_MAP);
			if (tasksStarted == null) {
				logger.debug("Task with id=" + taskId + " was not acquired");
			} else {
				taskWrapper = tasksStarted.get(taskId);
				if (taskWrapper == null) {
					logger.debug("Task with id=" + taskId + " was not acquired");
				}
			}
		}
		return taskWrapper;

	}

	/**
	 * Remove Task Started
	 * 
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service credendials
	 * @param taskWrapper
	 *            Task wrapper
	 */
	public static void removeStartedTask(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TaskWrapper taskWrapper) {

		if (taskWrapper == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask() == null || taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains invalid Task: " + taskWrapper);
			return;
		}

		SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
		HashMap<String, TaskWrapper> tasksStarted = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_STARTED_MAP);
		if (tasksStarted == null) {
			logger.debug("Started tasks was not acquired: " + taskWrapper);
		} else {
			if (tasksStarted.containsKey(taskWrapper.getTask().getId().getValue())) {
				tasksStarted.remove(taskWrapper.getTask().getId().getValue());
				logger.debug("Remove task: " + taskWrapper);
			} else {
				logger.debug("Started tasks was not acquired: " + taskWrapper);
			}
		}
	}

	/**
	 * Add Task Started
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service Credentials
	 * @param taskWrapper
	 *            Task wrapper
	 */
	public static void setStartedTask(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TaskWrapper taskWrapper) {

		if (taskWrapper == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask() == null || taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains invalid Task: " + taskWrapper);
			return;
		}

		SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
		HashMap<String, TaskWrapper> tasksStarted = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_STARTED_MAP);

		if (tasksStarted == null) {
			tasksStarted = new HashMap<String, TaskWrapper>();
			tasksStarted.put(taskWrapper.getTask().getId().getValue(), taskWrapper);
			sessionOp = new SessionOp<>();
			sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SCOPE_TO_OPERATIONS_TASKS_STARTED_MAP,
					tasksStarted);
		} else {
			tasksStarted.put(taskWrapper.getTask().getId().getValue(), taskWrapper);
		}
	}

	//
	/**
	 * Retrieve Aborted Task Map
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service credentials
	 * @return Map from operation to task wrapper in scope
	 */
	public static HashMap<String, TaskWrapper> getAbortedTaskMap(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {

		SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
		HashMap<String, TaskWrapper> tasksAborted = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_ABORTED_MAP);

		return tasksAborted;
	}

	/**
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service Credentials
	 * @param taskWrapper
	 *            Task wrapper
	 */
	public static void setAbortedTasks(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TaskWrapper taskWrapper) {

		if (taskWrapper == null || taskWrapper.getTask() == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask().getId() == null || taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains invalid Task: " + taskWrapper);
			return;
		}

		SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
		HashMap<String, TaskWrapper> tasksAborted = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_ABORTED_MAP);

		if (tasksAborted == null) {
			tasksAborted = new HashMap<String, TaskWrapper>();
			tasksAborted.put(taskWrapper.getTask().getId().getValue(), taskWrapper);
			sessionOp = new SessionOp<>();
			sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SCOPE_TO_OPERATIONS_TASKS_ABORTED_MAP,
					tasksAborted);
		} else {
			tasksAborted.put(taskWrapper.getTask().getId().getValue(), taskWrapper);
		}

	}

	//
	/**
	 * Retrieve Hidden Task Map
	 * 
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service credentials
	 * @return Map
	 */
	public static HashMap<String, TaskWrapper> getHiddenTaskMap(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {

		SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
		HashMap<String, TaskWrapper> tasksHidden = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_HIDDEN_MAP);
		return tasksHidden;

	}

	/**
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service credentials
	 * @param taskWrapper
	 *            Task wrapper
	 */
	public static void setHiddenTask(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TaskWrapper taskWrapper) {

		if (taskWrapper == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask() == null || taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains invalid Task: " + taskWrapper);
			return;
		}

		SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
		HashMap<String, TaskWrapper> tasksHidden = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_HIDDEN_MAP);

		if (tasksHidden == null) {
			tasksHidden = new HashMap<String, TaskWrapper>();
			tasksHidden.put(taskWrapper.getTask().getId().getValue(), taskWrapper);
			sessionOp = new SessionOp<>();
			sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SCOPE_TO_OPERATIONS_TASKS_HIDDEN_MAP,
					tasksHidden);
		} else {
			tasksHidden.put(taskWrapper.getTask().getId().getValue(), taskWrapper);
		}
	}

	/**
	 * Retrieve Tasks In Background
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service credentials
	 * @return Tasks in background
	 */
	public static HashMap<String, TaskWrapper> getTaskInBackgroundMap(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {

		SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
		HashMap<String, TaskWrapper> tasksInBackground = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);
		return tasksInBackground;
	}

	/**
	 * Add Task In Background
	 * 
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service credentials
	 * @param taskWrapper
	 *            Task wrapper
	 */
	public static void setTaskInBackground(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TaskWrapper taskWrapper) {

		if (taskWrapper == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask() == null || taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains invalid Task: " + taskWrapper);
			return;
		}

		SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
		HashMap<String, TaskWrapper> tasksInBackground = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);

		if (tasksInBackground == null) {
			tasksInBackground = new HashMap<String, TaskWrapper>();
			tasksInBackground.put(taskWrapper.getTask().getId().getValue(), taskWrapper);
			sessionOp = new SessionOp<>();
			sessionOp.set(httpRequest, serviceCredentials, SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP,
					tasksInBackground);
		} else {
			tasksInBackground.put(taskWrapper.getTask().getId().getValue(), taskWrapper);
		}

	}

	/**
	 * Remove Task from Tasks In Background
	 * 
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service credentials
	 * @param taskWrapper
	 *            Task wrapper
	 */
	public static void removeTaskInBackground(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			TaskWrapper taskWrapper) {

		if (taskWrapper == null) {
			logger.error("TaskWrapper is null");
			return;
		}

		if (taskWrapper.getTask() == null || taskWrapper.getTask().getId() == null
				|| taskWrapper.getTask().getId().getValue() == null
				|| taskWrapper.getTask().getId().getValue().isEmpty()) {
			logger.error("TaskWrapper contains invalid Task: " + taskWrapper);
			return;
		}

		SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
		HashMap<String, TaskWrapper> tasksInBackground = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);
		if (tasksInBackground == null) {
			logger.debug("Tasks In Background was not acquired: " + taskWrapper);
		} else {
			if (tasksInBackground.containsKey(taskWrapper.getTask().getId().getValue())) {
				tasksInBackground.remove(taskWrapper.getTask().getId().getValue());
				logger.debug("Remove Task In Background: " + taskWrapper);
			} else {
				logger.debug("Task In Background was not acquired: " + taskWrapper);
			}
		}

	}

	/**
	 * Remove All Background Tasks in the scope
	 * 
	 * @param httpRequest
	 *            Requet
	 * @param serviceCredentials
	 *            Service credentials
	 */
	public static void removeAllTasksInBackground(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) {

		SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
		sessionOp.remove(httpRequest, serviceCredentials, SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);

	}

	/**
	 * Remove Task on specific tabular resource from Tasks In Background
	 * 
	 * 
	 * @param httpRequest
	 *            Request
	 * @param serviceCredentials
	 *            Service credentials
	 * @param trId
	 *            TR id
	 */
	public static void removeTaskInBackgroundOnTRId(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, TRId trId) {

		if (trId == null) {
			logger.error("TRId is null");
			return;
		}

		if (trId.getId() == null || trId.getId().isEmpty()) {
			logger.error("TRId contains Id invalid: " + trId);
			return;
		}

		SessionOp<HashMap<String, TaskWrapper>> sessionOp = new SessionOp<>();
		HashMap<String, TaskWrapper> tasksInBackground = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.SCOPE_TO_OPERATIONS_TASKS_IN_BACKGROUND_MAP);

		if (tasksInBackground == null) {
			logger.debug("No Background task for TRId: " + trId);
		} else {
			ArrayList<String> removableKeys = new ArrayList<String>();
			for (String key : tasksInBackground.keySet()) {
				TaskWrapper taskWrapper = tasksInBackground.get(key);
				if (taskWrapper.getTrId() != null && taskWrapper.getTrId().getId() != null
						&& taskWrapper.getTrId().getId().compareTo(trId.getId()) == 0) {
					removableKeys.add(key);
				}
			}

			for (String key : removableKeys) {
				tasksInBackground.remove(key);
			}

		}

	}

	//
	public static FileUploadMonitor getFileUploadMonitor(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<FileUploadMonitor> sessionOp = new SessionOp<>();
		FileUploadMonitor fileUploadMonitor = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.FILE_UPLOAD_MONITOR, FileUploadMonitor.class);
		return fileUploadMonitor;
	}

	public static void setFileUploadMonitor(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			FileUploadMonitor fileUploadMonitor) {
		SessionOp<FileUploadMonitor> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.FILE_UPLOAD_MONITOR, fileUploadMonitor);
	}

	//
	public static SaveResourceSession getSaveResourceSession(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials) throws TDGWTServiceException {
		SessionOp<SaveResourceSession> sessionOp = new SessionOp<>();
		SaveResourceSession saveResourceSession = sessionOp.get(httpRequest, serviceCredentials,
				SessionConstants.RESOURCE_SAVE_SESSION, SaveResourceSession.class);
		return saveResourceSession;
	}

	public static void setSaveResourceSession(HttpServletRequest httpRequest, ServiceCredentials serviceCredentials,
			SaveResourceSession saveResourceSession) {
		SessionOp<SaveResourceSession> sessionOp = new SessionOp<>();
		sessionOp.set(httpRequest, serviceCredentials, SessionConstants.RESOURCE_SAVE_SESSION, saveResourceSession);

	}

}
