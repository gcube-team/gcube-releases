<%
/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
%>

<%@ include file="/html/portlet/dockbar/init.jsp" %>

<%
Group group = null;

if (layout != null) {
	group = layout.getGroup();
}

List<Portlet> portlets = new ArrayList<Portlet>();

for (String portletId : PropsValues.DOCKBAR_ADD_PORTLETS) {
	Portlet portlet = PortletLocalServiceUtil.getPortletById(portletId);

	if (portlet.isInclude() && portlet.isActive() && portlet.hasAddPortletPermission(user.getUserId())) {
		portlets.add(portlet);
	}
}
%>

<div class="dockbar" data-namespace="<portlet:namespace />" id="dockbar">
	<ul class="aui-toolbar">
		<li class="pin-dockbar">
			<a href="javascript:;"><img alt='<liferay-ui:message key="pin-the-dockbar" />' src="<%= HtmlUtil.escape(themeDisplay.getPathThemeImages()) %>/spacer.png" /></a>
		</li>

		<c:if test="<%= (group != null) && !group.isControlPanel() && (!group.hasStagingGroup() || group.isStagingGroup()) && LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.UPDATE) %>">
			<li class="add-content has-submenu" id="<portlet:namespace />addContent">
				<a class="menu-button" href="javascript:;">
					<span>
						<liferay-ui:message key="add" />
					</span>
				</a>

				<div class="aui-menu add-content-menu aui-overlaycontext-hidden" id="<portlet:namespace />addContentContainer">
					<div class="aui-menu-content">
						<ul>
							<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, scopeGroupId, ActionKeys.MANAGE_LAYOUTS) && !group.isLayoutPrototype() %>">
								<li class="first add-page">
									<a href="javascript:;" id="addPage">
										<liferay-ui:message key="page" />
									</a>
								</li>
							</c:if>

							<c:if test="<%= !themeDisplay.isStateMaximized() %>">
								<li class="last common-items">
									<div class="aui-menugroup">
										<div class="aui-menugroup-content">
											<span class="aui-menu-label"><liferay-ui:message key="applications" /></span>

											<ul>

												<%
												for (int i = 0; i < portlets.size(); i++) {
													Portlet portlet = portlets.get(i);
												%>

													<li class="<%= (i == 0) ? "first" : "" %>">
														<a class="app-shortcut" data-portlet-id="<%= portlet.getPortletId() %>" href="javascript:;">
															<liferay-portlet:icon-portlet portlet="<%= portlet %>" />

															<%= PortalUtil.getPortletTitle(portlet.getPortletId(), locale) %>
														</a>
													</li>

												<%
												}
												%>

												<li class="add-application last more-applications">
													<a href="javascript:;" id="<portlet:namespace />addApplication">
														<liferay-ui:message key="more" />&hellip;
													</a>
												</li>
											</ul>
										</div>
									</div>
								</li>
							</c:if>
						</ul>
					</div>
				</div>
			</li>
		</c:if>

		<c:if test="<%= !group.isControlPanel() && themeDisplay.isShowControlPanelIcon() || themeDisplay.isShowPageSettingsIcon() || themeDisplay.isShowLayoutTemplatesIcon() %>">
			<li class="manage-content has-submenu" id="<portlet:namespace />manageContent">
				<a class="menu-button" href="javascript:;">
					<span>
						<liferay-ui:message key="manage" />
					</span>
				</a>

				<div class="aui-menu manage-content-menu aui-overlaycontext-hidden" id="<portlet:namespace />manageContentContainer">
					<div class="aui-menu-content">
						<ul>
							<c:if test="<%= themeDisplay.isShowPageSettingsIcon() %>">
								<li class="first manage-page">
									<aui:a href="<%= themeDisplay.getURLPageSettings().toString() %>" label="page" />
								</li>
							</c:if>

							<c:if test="<%= themeDisplay.isShowLayoutTemplatesIcon() && !themeDisplay.isStateMaximized() %>">
								<li class="page-layout">
									<a href="javascript:;" id="pageTemplate">
										<liferay-ui:message key="page-layout" />
									</a>
								</li>
							</c:if>

							<c:if test="<%= themeDisplay.isShowPageSettingsIcon() && !group.isLayoutPrototype() %>">
								<li class="sitemap">
									<aui:a href='<%= HttpUtil.setParameter(themeDisplay.getURLPageSettings().toString(), "selPlid", "-1") %>' label="sitemap" />
								</li>
							</c:if>

							<c:if test="<%= themeDisplay.isShowPageSettingsIcon() && !group.isLayoutPrototype() %>">

								<%
								String pageSettingsURL = themeDisplay.getURLPageSettings().toString();

								pageSettingsURL = HttpUtil.removeParameter(pageSettingsURL, "tabs1");
								pageSettingsURL = HttpUtil.setParameter(pageSettingsURL, PortalUtil.getPortletNamespace(PortletKeys.LAYOUT_MANAGEMENT) + "tabs1", "settings");
								%>

								<li class="settings">
									<aui:a href="<%= pageSettingsURL %>" label="settings" />
								</li>
							</c:if>

							<c:if test="<%= themeDisplay.isShowControlPanelIcon() %>">
								<li class="control-panel last" id="<portlet:namespace />controlPanel">
									<aui:a href="<%= themeDisplay.getURLControlPanel() %>" label="control-panel" />
								</li>
							</c:if>
						</ul>
					</div>
				</div>
			</li>
		</c:if>

		<c:if test="<%= themeDisplay.isShowStagingIcon() %>">
			<li class="staging-options has-submenu" id="<portlet:namespace />staging">
				<a class="menu-button" href="javascript:;">
					<span>
						<liferay-ui:message key="staging" />
					</span>
				</a>

				<div class="aui-menu staging-menu aui-overlaycontext-hidden" id="<portlet:namespace />stagingContainer">
					<div class="aui-menu-content">
						<liferay-ui:staging />
					</div>
				</div>
			</li>
		</c:if>

		<li class="aui-toolbar-separator">
			<span></span>
		</li>

		<c:if test="<%= !group.isControlPanel() && themeDisplay.isSignedIn() %>">
			<li class="toggle-controls" id="<portlet:namespace />toggleControls">
				<a href="javascript:;">
					<liferay-ui:message key="toggle-edit-controls" />
				</a>
			</li>
		</c:if>

		<c:if test="<%= group.isControlPanel() %>">

			<%
			String refererGroupDescriptiveName = null;
			String backURL = null;

			if (themeDisplay.getRefererPlid() > 0) {
				Layout refererLayout = LayoutLocalServiceUtil.getLayout(themeDisplay.getRefererPlid());

				Group refererGroup = refererLayout.getGroup();

				refererGroupDescriptiveName = refererGroup.getDescriptiveName();

				if (refererGroup.isUser() && (refererGroup.getClassPK() == user.getUserId())) {
					if (refererLayout.isPublicLayout()) {
						refererGroupDescriptiveName = LanguageUtil.get(pageContext, "my-public-pages");
					}
					else {
						refererGroupDescriptiveName = LanguageUtil.get(pageContext, "my-private-pages");
					}
				}

				backURL = PortalUtil.getLayoutURL(refererLayout, themeDisplay);
			}
			else {
				refererGroupDescriptiveName = themeDisplay.getAccount().getName();
				backURL = themeDisplay.getURLHome();
			}

			if (Validator.isNotNull(themeDisplay.getDoAsUserId())) {
				backURL = HttpUtil.addParameter(backURL, "doAsUserId", themeDisplay.getDoAsUserId());
			}

			if (Validator.isNotNull(themeDisplay.getDoAsUserLanguageId())) {
				backURL = HttpUtil.addParameter(backURL, "doAsUserLanguageId", themeDisplay.getDoAsUserLanguageId());
			}
			%>

			<li class="back-link" id="<portlet:namespace />backLink">
				<a class="portlet-icon-back nobr" href="<%= PortalUtil.escapeRedirect(backURL) %>">
					<%= LanguageUtil.format(pageContext, "back-to-x", HtmlUtil.escape(refererGroupDescriptiveName)) %>
				</a>
			</li>
		</c:if>
	</ul>

	<ul class="aui-toolbar user-toolbar">
		<c:if test="<%= user.hasMyPlaces() %>">
			<li class="my-places has-submenu" id="<portlet:namespace />myPlaces">
				<a class="menu-button" href="javascript:;">
					<span>
						<liferay-ui:message key="go-to" />
					</span>
				</a>

				<div class="aui-menu my-places-menu aui-overlaycontext-hidden" id="<portlet:namespace />myPlacesContainer">
					<div class="aui-menu-content">
						<liferay-ui:my-places />
					</div>
				</div>
			</li>
		</c:if>

		<li class="aui-toolbar-separator">
			<span></span>
		</li>

		<li class="user-avatar <%= themeDisplay.isImpersonated() ? "impersonating-user has-submenu" : "" %>" id="<portlet:namespace />userAvatar">
			<span class="user-links <%= themeDisplay.isImpersonated() ? "menu-button": "" %>">
				<aui:a cssClass="user-portrait" href="<%= themeDisplay.getURLMyAccount().toString() %>">
					<img alt="<%= HtmlUtil.escape(user.getFullName()) %>" src="<%= HtmlUtil.escape(themeDisplay.getPathImage() + "/user_" + (user.isFemale() ? "female" : "male") + "_portrait?img_id=" + user.getPortraitId() + "&t=" + ImageServletTokenUtil.getToken(user.getPortraitId())) %>" />
				</aui:a>

				<aui:a cssClass="user-fullname" href="<%= themeDisplay.getURLMyAccount().toString() %>"><%= HtmlUtil.escape(user.getFullName()) %></aui:a>

				<c:if test="<%= themeDisplay.isShowSignOutIcon() %>">
					<span class="sign-out">(<aui:a href="<%= themeDisplay.getURLSignOut() %>" label="sign-out" />)</span>
				</c:if>
			</span>

			<c:if test="<%= themeDisplay.isImpersonated() %>">
				<div class="aui-menu impersonation-menu aui-overlaycontext-hidden" id="<portlet:namespace />userOptionsContainer">
					<div class="aui-menu-content">
						<div class="notice-message portlet-msg-info">
							<c:choose>
								<c:when test="<%= themeDisplay.isSignedIn() %>">
									<%= LanguageUtil.format(pageContext, "you-are-impersonating-x", new Object[] {HtmlUtil.escape(user.getFullName())}) %>
								</c:when>
								<c:otherwise>
									<liferay-ui:message key="you-are-impersonating-the-guest-user" />
								</c:otherwise>
							</c:choose>
						</div>

						<ul>
							<li>
								<aui:a href="<%= PortalUtil.getLayoutURL(layout, themeDisplay, false) %>"><liferay-ui:message key="be-yourself-again" /> (<%= HtmlUtil.escape(realUser.getFullName()) %>)</aui:a>
							</li>

							<%
							Locale realUserLocale = realUser.getLocale();
							Locale userLocale = user.getLocale();
							%>

							<c:if test="<%= !realUserLocale.equals(userLocale) %>">

								<%
								String doAsUserLanguageId = null;
								String changeLanguageMessage = null;

								if (locale.getLanguage().equals(realUserLocale.getLanguage()) && locale.getCountry().equals(realUserLocale.getCountry())) {
									doAsUserLanguageId = userLocale.getLanguage() + "_" + userLocale.getCountry();
									changeLanguageMessage = LanguageUtil.format(realUserLocale, "use-x's-preferred-language-(x)", new String[] {HtmlUtil.escape(user.getFullName()), userLocale.getDisplayLanguage(realUserLocale)});
								}
								else {
									doAsUserLanguageId = realUserLocale.getLanguage() + "_" + realUserLocale.getCountry();
									changeLanguageMessage = LanguageUtil.format(realUserLocale, "use-your-preferred-language-(x)", realUserLocale.getDisplayLanguage(realUserLocale));
								}
								%>

								<li class="current-user-language">
									<aui:a href='<%= HttpUtil.setParameter(PortalUtil.getCurrentURL(request), "doAsUserLanguageId", doAsUserLanguageId) %>'><%= changeLanguageMessage %></aui:a>
								</li>
							</c:if>
						</ul>
					</div>
				</div>
			</c:if>
		</li>
	</ul>

	<div class="dockbar-messages" id="<portlet:namespace />dockbarMessages">
		<div class="aui-header"></div>

		<div class="aui-body"></div>

		<div class="aui-footer"></div>
	</div>

	<%
	List<LayoutPrototype> layoutPrototypes = LayoutPrototypeServiceUtil.search(company.getCompanyId(), Boolean.TRUE, null);
	%>

	<c:if test="<%= !layoutPrototypes.isEmpty() %>">
		<div id="layoutPrototypeTemplate" class="aui-html-template">
			<ul>

				<%
				for (LayoutPrototype layoutPrototype : layoutPrototypes) {
				%>
					<li>
						<label>
							<a href="javascript:;">
								<input name="template" type="radio" value="<%= layoutPrototype.getLayoutPrototypeId() %>" /> <%= HtmlUtil.escape(layoutPrototype.getName(user.getLanguageId())) %>
							</a>
						</label>
					</li>
				<%
				}
				%>

			</ul>
		</div>
	</c:if>
</div>

<aui:script position="inline" use="liferay-dockbar">
	Liferay.Dockbar.init();
</aui:script>