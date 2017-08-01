package org.gcube.portlets.user.simulfishgrowth.portlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.simulfishgrowthdata.model.verify.EntityVerify;
import org.gcube.data.simulfishgrowthdata.model.verify.ScenarioVerify;
import org.gcube.data.simulfishgrowthdata.util.AccessPointer;
import org.gcube.portlets.user.simulfishgrowth.model.util.ModelerFullUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.ModelerUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.ScenarioFullUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.ScenarioUtil;
import org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders;
import org.gcube.portlets.user.simulfishgrowth.util.ConnectionUtils;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.util.PortalUtil;

import gr.i2s.fishgrowth.model.ModelerFull;
import gr.i2s.fishgrowth.model.Scenario;
import gr.i2s.fishgrowth.model.ScenarioFull;

/**
 * Portlet implementation class SimulPortlet
 */
public class SimulPortlet extends BasePortlet {
	private static Log logger = LogFactoryUtil.getLog(SimulPortlet.class);

	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		String path = getPath(request);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("render path [%s]", path));
		}

		HttpSession httpSession = PortalUtil.getHttpServletRequest(request).getSession();

		try {
			Exception prev = (Exception) (request.getAttribute("errorReason"));
			if (prev != null) {
				throw prev;
			}

			prepareSession(httpSession, request);

			String scope = getScope(httpSession);

			try {
				if (!ConnectionUtils.hasEndpoint()) {
					ConnectionUtils.setEndPoint(getDataAccessEndpoint(scope));
				}
			} catch (Exception e) {
				throw new Exception("Could not setup communication. If the problem persists, please contact support.",
						e);
			}

			AddGCubeHeaders addGCubeHeaders = new AddGCubeHeadersCreator(request).create();

			if (path == null) {
				path = "/html/simul/view.jsp";
			}
			if (path.endsWith("view.jsp") || path.endsWith("startScenario.jsp")) {
				if (logger.isTraceEnabled())
					logger.trace("Preparing view");

				String ownerId = null; // invalid
				try {
					ServiceContext context = ServiceContextFactory.getInstance(request);
					ownerId = scopeAsOwnerId(scope);
				} catch (SystemException | PortalException e) {
					logger.error("now ownerid", e);
				}
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("ownerId %s", ownerId));
				}

				request.setAttribute("ownerId", ownerId);
				request.setAttribute("addGCubeHeaders", addGCubeHeaders);

			} else if (path.endsWith("edit.jsp")) {
				if (logger.isTraceEnabled())
					logger.trace("Preparing edit");

				String ownerId = null; // invalid
				try {
					ServiceContext context = ServiceContextFactory.getInstance(request);
					ownerId = scopeAsOwnerId(scope);
				} catch (SystemException | PortalException e) {
					logger.error("now ownerid", e);
				}
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("ownerId %s", ownerId));
				}

				List<ModelerFull> modelerList = null;

				try {
					modelerList = new ArrayList<ModelerFull>(new ModelerFullUtil(addGCubeHeaders)
							.getModelerFulls(ownerId, QueryUtil.ALL_POS, QueryUtil.ALL_POS));
				} catch (Exception e) {
					logger.error(e);
				}

				if (modelerList == null) {
					modelerList = new ArrayList<>();
				}
				request.setAttribute("modelerList", modelerList);
				request.setAttribute("addGCubeHeaders", addGCubeHeaders);
			} else {
				if (logger.isTraceEnabled())
					logger.trace("Preparing nothing!");

			}
		} catch (Exception e) {
			logger.error("Could not render properly", e);
			request.setAttribute("errorReason", e);
			SessionMessages.clear(request);
			path = "/html/error.jsp";
		}

		PortletContext portletContext = getPortletContext();
		PortletRequestDispatcher portletRequestDispatcher = portletContext.getRequestDispatcher(path);
		if (portletRequestDispatcher == null) {
			logger.error(String.format("[%s] is not a valid path", path));
		} else {
			portletRequestDispatcher.include(request, response);
		}
	}

	public void save(ActionRequest request, ActionResponse response) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("save action");

		if (logger.isTraceEnabled()) {
			logger.trace(String.format("request parameters %s", request.getParameterMap()));
		}

		ServiceContext context = ServiceContextFactory.getInstance(request);
		HttpSession httpSession = PortalUtil.getHttpServletRequest(request).getSession();

		try {
			String scope = getScope(httpSession);

			AddGCubeHeaders addGCubeHeaders = new AddGCubeHeadersCreator(request).create();

			Long id = ParamUtil.getLong(request, "id");
			ScenarioFull scenario;
			if (id > 0) {
				scenario = new ScenarioFullUtil(addGCubeHeaders).getScenarioFull(id);
			} else {
				scenario = new ScenarioFull();
			}
			String designation = ParamUtil.getString(request, "designation");
			String comments = ParamUtil.getString(request, "comments");
			Long modelerId = ParamUtil.getLong(request, "modelerId");
			String startDate = ParamUtil.getString(request, "startDateFrm");
			Integer fishNo = ParamUtil.getInteger(request, "fishNo");
			Double weight = ParamUtil.getDouble(request, "weight");
			String targetDate = ParamUtil.getString(request, "targetDateFrm");

			scenario.setDesignation(designation);
			scenario.setComments(comments);
			scenario.setModelerId(modelerId);
			try {
				scenario.setStartDateFrm(startDate);
			} catch (Exception e) {
				throw new EntityVerify.VerifyException(String.format("Invalid start date."));
			}
			scenario.setFishNo(fishNo);
			scenario.setWeight(weight);
			try {
				scenario.setTargetDateFrm(targetDate);
			} catch (Exception e) {
				throw new EntityVerify.VerifyException(String.format("Invalid target date."));
			}
			scenario.setOwnerId(scopeAsOwnerId(scope));
			scenario.setStatusId(org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil.STATUS_PENDING_KPI);

			if (logger.isTraceEnabled()) {
				logger.trace(String.format("retrieved [%s]", scenario));
			}

			new ScenarioVerify(scenario).normalise().verify();

			if (logger.isTraceEnabled()) {
				logger.trace(String.format("persisting %s", scenario));
			}

			// if I use ScenarioFull, jersey will complain for the addition
			// fields as Unrecognized (UnrecognizedPropertyException
			if (id > 0) {
				new ScenarioUtil(addGCubeHeaders).update(new Scenario(scenario));
			} else {
				// TODO avoid the double access, if possible
				Scenario bypasserror = new Scenario(scenario);
				new ScenarioUtil(addGCubeHeaders).add(bypasserror);
				scenario = new ScenarioFullUtil(addGCubeHeaders).getScenarioFull(bypasserror.getId());
				// get the actual id now that you can
				id = scenario.getId();

			}

			if (logger.isDebugEnabled())
				logger.debug(String.format("Saved [%s]", scenario));

			final PortletSession psession = request.getPortletSession();
			psession.setAttribute("id", id, PortletSession.PORTLET_SCOPE);
			// psession.getAttribute(key, PortletSession.APPLICATION_SCOPE);

			Boolean doRun = ParamUtil.getBoolean(request, "doRun");
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("Should I execute the scenario? %s", doRun));
			}

			if (doRun) {
				if (scenario
						.getModelerStatusId() == org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil.STATUS_READY) {
					new ScenarioUtil(addGCubeHeaders).executeScenario(id);
					response.setRenderParameter("jspPage", "/html/simul/startScenario.jsp");
				}
			} else {
				response.setRenderParameter("jspPage", "/html/simul/startScenario.jsp");
			}
		} catch (Exception e) {
			if (logger.isTraceEnabled()) {
				logger.trace("Signaling exception on save", e);
			}
			request.setAttribute("errorReason", e);
		}

	}

	public void delete(ActionRequest request, ActionResponse response) throws Exception {
		long id = ParamUtil.getLong(request, "id");
		if (logger.isDebugEnabled())
			logger.debug(String.format("Deleting scenarioId [%s]", id));
		try {
			AddGCubeHeaders addGCubeHeaders = new AddGCubeHeadersCreator(request).create();
			new ScenarioUtil(addGCubeHeaders).delete(id);
		} catch (Exception e) {
			if (logger.isTraceEnabled()) {
				logger.trace("Signaling exception on delete", e);
			}
			request.setAttribute("errorReason", e);
		}

	}

}
