package org.gcube.portlets.user.simulfishgrowth.portlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.gcube.portlets.user.simulfishgrowth.model.util.CurrentRatingUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.OxygenRatingUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.RegionUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.SimilarSiteUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.SiteFullUtil;
import org.gcube.portlets.user.simulfishgrowth.model.util.SiteUtil;
import org.gcube.portlets.user.simulfishgrowth.model.verify.SiteVerify;
import org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders;
import org.gcube.portlets.user.simulfishgrowth.util.ConnectionUtils;
import org.gcube.portlets.user.simulfishgrowth.util.Utils;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.util.PortalUtil;

import gr.i2s.fishgrowth.model.CurrentRating;
import gr.i2s.fishgrowth.model.OxygenRating;
import gr.i2s.fishgrowth.model.Region;
import gr.i2s.fishgrowth.model.Site;
import gr.i2s.fishgrowth.model.Usage;

/**
 * Portlet implementation class ModelerPortlet
 */
public class SitePortlet extends BasePortlet {
	private static Log logger = LogFactoryUtil.getLog(SitePortlet.class);

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
				path = "/html/site/view.jsp";
			}
			if (path.endsWith("view.jsp")) {
				if (logger.isTraceEnabled()) {
					logger.trace("Preparing view");
				}

				String ownerId = null; // invalid
				try {
					ServiceContext context = ServiceContextFactory.getInstance(request);
					ownerId = scopeAsOwnerId(scope);
				} catch (SystemException | PortalException e) {
					logger.error(e);
				}
				List<Usage> ownerUsage = new SiteUtil(addGCubeHeaders).getUsage(ownerId);
				Map<Long, Integer> usages = new HashMap<>();
				for (Usage usage : ownerUsage) {
					usages.put(usage.getId(), usage.getUsage());
				}
				request.setAttribute("usages", usages);
				request.setAttribute("ownerId", ownerId);
				request.setAttribute("addGCubeHeaders", addGCubeHeaders);

			} else if (path.endsWith("edit.jsp")) {
				if (logger.isTraceEnabled()) {
					logger.trace("Preparing edit");
				}

				List<Region> regionList = null;
				List<OxygenRating> oxygenRatingList = null;
				List<CurrentRating> currentRatingList = null;

				try {
					regionList = new ArrayList<Region>(new RegionUtil(addGCubeHeaders).getRegions());
					oxygenRatingList = new ArrayList<OxygenRating>(
							new OxygenRatingUtil(addGCubeHeaders).getOxygenRatings());
					currentRatingList = new ArrayList<CurrentRating>(
							new CurrentRatingUtil(addGCubeHeaders).getCurrentRatings());
				} catch (Exception e) {
					logger.error(e);
				}

				request.setAttribute("regionList", regionList);
				request.setAttribute("oxygenRatingList", oxygenRatingList);
				request.setAttribute("currentRatingList", currentRatingList);

				request.setAttribute("addGCubeHeaders", addGCubeHeaders);
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("Preparing nothing!");
				}

			}
		} catch (Exception e) {
			logger.error("Could not render properly", e);
			request.setAttribute("errorReason", e);
			SessionMessages.clear(request);
			path = "/html/error.jsp";
			// SessionErrors.add(request, e.getMessage());
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
		ServiceContext context = ServiceContextFactory.getInstance(request);
		Long id = ParamUtil.getLong(request, "id");
		logger.info(String.format("Saving [%s]", id));

		HttpSession httpSession = PortalUtil.getHttpServletRequest(request).getSession();

		try {

			String scope = getScope(httpSession);
			AddGCubeHeaders addGCubeHeaders = new AddGCubeHeadersCreator(request).create();

			Site site;
			if (id > 0) {
				site = new SiteUtil(addGCubeHeaders).getSite(id);
			} else {
				site = new Site();
			}
			String designation = ParamUtil.getString(request, "designation");
			Long regionId = ParamUtil.getLong(request, "regionId");
			Long oxygenRatingId = ParamUtil.getLong(request, "oxygenRatingId");
			Long currentRatingId = ParamUtil.getLong(request, "currentRatingId");
			Integer periodJanA = ParamUtil.getInteger(request, "periodJanA");
			Integer periodJanB = ParamUtil.getInteger(request, "periodJanB");
			Integer periodFebA = ParamUtil.getInteger(request, "periodFebA");
			Integer periodFebB = ParamUtil.getInteger(request, "periodFebB");
			Integer periodMarA = ParamUtil.getInteger(request, "periodMarA");
			Integer periodMarB = ParamUtil.getInteger(request, "periodMarB");
			Integer periodAprA = ParamUtil.getInteger(request, "periodAprA");
			Integer periodAprB = ParamUtil.getInteger(request, "periodAprB");
			Integer periodMayA = ParamUtil.getInteger(request, "periodMayA");
			Integer periodMayB = ParamUtil.getInteger(request, "periodMayB");
			Integer periodJunA = ParamUtil.getInteger(request, "periodJunA");
			Integer periodJunB = ParamUtil.getInteger(request, "periodJunB");
			Integer periodJulA = ParamUtil.getInteger(request, "periodJulA");
			Integer periodJulB = ParamUtil.getInteger(request, "periodJulB");
			Integer periodAugA = ParamUtil.getInteger(request, "periodAugA");
			Integer periodAugB = ParamUtil.getInteger(request, "periodAugB");
			Integer periodSepA = ParamUtil.getInteger(request, "periodSepA");
			Integer periodSepB = ParamUtil.getInteger(request, "periodSepB");
			Integer periodOctA = ParamUtil.getInteger(request, "periodOctA");
			Integer periodOctB = ParamUtil.getInteger(request, "periodOctB");
			Integer periodNovA = ParamUtil.getInteger(request, "periodNovA");
			Integer periodNovB = ParamUtil.getInteger(request, "periodNovB");
			Integer periodDecA = ParamUtil.getInteger(request, "periodDecA");
			Integer periodDecB = ParamUtil.getInteger(request, "periodDecB");
			String latitude = ParamUtil.getString(request, "latitude");
			String longitude = ParamUtil.getString(request, "longitude");

			site.setDesignation(designation);
			site.setRegionId(regionId);
			site.setOxygenRatingId(oxygenRatingId);
			site.setCurrentRatingId(currentRatingId);
			site.setPeriodJanA(periodJanA);
			site.setPeriodJanB(periodJanB);
			site.setPeriodFebA(periodFebA);
			site.setPeriodFebB(periodFebB);
			site.setPeriodMarA(periodMarA);
			site.setPeriodMarB(periodMarB);
			site.setPeriodAprA(periodAprA);
			site.setPeriodAprB(periodAprB);
			site.setPeriodMayA(periodMayA);
			site.setPeriodMayB(periodMayB);
			site.setPeriodJunA(periodJunA);
			site.setPeriodJunB(periodJunB);
			site.setPeriodJulA(periodJulA);
			site.setPeriodJulB(periodJulB);
			site.setPeriodAugA(periodAugA);
			site.setPeriodAugB(periodAugB);
			site.setPeriodSepA(periodSepA);
			site.setPeriodSepB(periodSepB);
			site.setPeriodOctA(periodOctA);
			site.setPeriodOctB(periodOctB);
			site.setPeriodNovA(periodNovA);
			site.setPeriodNovB(periodNovB);
			site.setPeriodDecA(periodDecA);
			site.setPeriodDecB(periodDecB);
			site.setLatitude(latitude);
			site.setLongitude(longitude);
			site.setOwnerId(scopeAsOwnerId(scope));
			site.setPeriodYear(new Utils().median(periodJanA, periodJanB, periodFebA, periodFebB, periodMarA,
					periodMarB, periodAprA, periodAprB, periodMayA, periodMayB, periodJunA, periodJunB, periodJulA,
					periodJulB, periodAugA, periodAugB, periodSepA, periodSepB, periodOctA, periodOctB, periodNovA,
					periodNovB, periodDecA, periodDecB));

			if (logger.isTraceEnabled()) {
				logger.trace(String.format("retrieved [%s]", site));
			}

			new SiteVerify(site).normalise().verify();

			if (logger.isTraceEnabled()) {
				logger.trace(String.format("persisting [%s]", site));
			}

			if (id > 0) {
				new SiteUtil(addGCubeHeaders).update(site);
			} else {
				new SiteUtil(addGCubeHeaders).add(site);
			}

		} catch (Exception e) {
			if (logger.isTraceEnabled()) {
				logger.trace("Signaling exception on save", e);
			}
			request.setAttribute("errorReason", e);
			// SessionErrors.add(request, e.getMessage());
		}
	}

	public void delete(ActionRequest request, ActionResponse response) throws Exception {
		long id = ParamUtil.getLong(request, "id");
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Deleting [%s]", id));
		}

		try {
			AddGCubeHeaders addGCubeHeaders = new AddGCubeHeadersCreator(request).create();
			new SiteFullUtil(addGCubeHeaders).delete(id);

		} catch (Exception e) {
			if (logger.isTraceEnabled()) {
				logger.trace("Signaling exception on delete", e);
			}
			request.setAttribute("errorReason", e);
			// SessionErrors.add(request, e.getMessage());
		}
	}

}
