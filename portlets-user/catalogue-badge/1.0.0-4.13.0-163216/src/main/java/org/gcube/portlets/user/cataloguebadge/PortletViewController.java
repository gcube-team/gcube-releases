/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.gcube.portlets.user.cataloguebadge;


import java.text.DecimalFormat;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.datacatalogue.ckanutillibrary.server.ApplicationProfileScopePerUrlReader;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueImpl;
import org.gcube.datacatalogue.ckanutillibrary.shared.Statistics;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

@Controller
@RequestMapping("VIEW")
public class PortletViewController {
	private static Log _log = LogFactoryUtil.getLog(PortletViewController.class);
	private DataCatalogueFactory factory = DataCatalogueFactory.getFactory();
	private static final long K = 1000;
	private static final long M = K * K;
	private static final long G = M * K;

	private static GroupManager gm = new LiferayGroupManager();

	@RenderMapping
	public String question(RenderRequest request,RenderResponse response, Model model) {
		model.addAttribute("releaseInfo", ReleaseInfo.getReleaseInfo());

		try {
			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(request);

			long groupId = PortalUtil.getScopeGroupId(request);
			PortalContext pContext = PortalContext.getConfiguration(); 
			String context = pContext.getCurrentScope(""+groupId);
			String catalogueURL = getCatalougeFriendlyURL(GroupLocalServiceUtil.getGroup(groupId));
			DataCatalogueImpl utils = null;
			if (gm.isRootVO(groupId)) {
				String gatewaySiteURL = pContext.getGatewayURL(httpServletRequest);
				if (!gatewaySiteURL.startsWith("https"))
					gatewaySiteURL = gatewaySiteURL.replaceAll("http:", "https:");
				String siteLandingPage = pContext.getSiteLandingPagePath(httpServletRequest);
				String clientURL = gatewaySiteURL+siteLandingPage;
				try {
					String appPerScopeURL = ApplicationProfileScopePerUrlReader.getScopePerUrl(clientURL);
					_log.info("Catalogue for this Gateway is in this scope: " + appPerScopeURL);
					utils = factory.getUtilsPerScope(appPerScopeURL);
					_log.info("Here I instanciated factory.getUtilsPerScope with scope " + appPerScopeURL);
				} catch (Exception e) {
					_log.warn("Returning default catalogue for the context, could not find the catologue for this Gateway: " + clientURL);
					utils = factory.getUtilsPerScope(context);
				}
			}
			else {
				utils = factory.getUtilsPerScope(context);
				_log.info("regular factory.getUtilsPerScope with context: " + context);
			}
			Statistics stats = utils.getStatistics();
			_log.info("Got Statistics ... ");
			model.addAttribute("itemsNo", convertToStringRepresentation(stats.getNumItems()));
			model.addAttribute("groupsNo", stats.getNumGroups());
			model.addAttribute("organisationsNo", stats.getNumOrganizations());
			model.addAttribute("typesNo", stats.getNumTypes());
			model.addAttribute("catalogueURL", catalogueURL);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "Catalogue-badge-portlet/view";
	}

	public static String convertToStringRepresentation(final long value){
		final long[] dividers = new long[] { G, M, K, 1 };
		final String[] units = new String[] {"Giga", "M", "K", ""};
		if(value < 1)
			throw new IllegalArgumentException("Invalid file size: " + value);
		String result = null;
		for(int i = 0; i < dividers.length; i++){
			final long divider = dividers[i];
			if(value >= divider){
				result = format(value, divider, units[i]);
				break;
			}
		}
		return result;
	}

	private static String format(final long value,
			final long divider,
			final String unit){
		final double result =
				divider > 1 ? (double) value / (double) divider : (double) value;
				return new DecimalFormat("#,##0.#").format(result) + " " + unit;
	}

	/**
	 * @param currentGroup
	 * @return Returns the friendly u r l of this group.
	 */
	private static String getCatalougeFriendlyURL(final Group currentGroup) throws Exception {
		String friendlyURL = GCubePortalConstants.PREFIX_GROUP_URL;
		StringBuffer sb = new StringBuffer();
		sb.append(friendlyURL).append(currentGroup.getFriendlyURL())
		.append(GCubePortalConstants.CATALOGUE_FRIENDLY_URL);
		return sb.toString();
	}


}