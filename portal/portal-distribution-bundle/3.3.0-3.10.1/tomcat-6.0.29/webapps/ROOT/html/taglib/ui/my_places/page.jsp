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

<%@ include file="/html/taglib/init.jsp" %>

<%@ page import="com.liferay.portal.service.permission.OrganizationPermissionUtil" %>

<%
int max = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:my_places:max"));

if (max <= 0) {
	max = PropsValues.MY_PLACES_MAX_ELEMENTS;
}

List<Group> myPlaces = user.getMyPlaces(max);
%>

<c:if test="<%= !myPlaces.isEmpty() %>">
	<ul class="taglib-my-places">

		<%
		PortletURL portletURL = new PortletURLImpl(request, PortletKeys.MY_PLACES, plid, PortletRequest.ACTION_PHASE);

		portletURL.setWindowState(WindowState.NORMAL);
		portletURL.setPortletMode(PortletMode.VIEW);

		portletURL.setParameter("struts_action", "/my_places/view");

		for (Group myPlace : myPlaces) {
			myPlace = myPlace.toEscapedModel();

			boolean organizationCommunity = myPlace.isOrganization();
			boolean regularCommunity = myPlace.isCommunity();
			boolean userCommunity = myPlace.isUser();
			int publicLayoutsPageCount = myPlace.getPublicLayoutsPageCount();
			int privateLayoutsPageCount = myPlace.getPrivateLayoutsPageCount();

			Organization organization = null;

			String publicAddPageHREF = null;
			String privateAddPageHREF = null;

			if (organizationCommunity) {
				organization = OrganizationLocalServiceUtil.getOrganization(myPlace.getOrganizationId());

				if (OrganizationPermissionUtil.contains(permissionChecker, organization.getOrganizationId(), ActionKeys.MANAGE_LAYOUTS)) {
					PortletURL addPageURL = new PortletURLImpl(request, PortletKeys.MY_PLACES, plid, PortletRequest.ACTION_PHASE);

					addPageURL.setWindowState(WindowState.NORMAL);
					addPageURL.setPortletMode(PortletMode.VIEW);

					addPageURL.setParameter("struts_action", "/my_places/edit_pages");
					addPageURL.setParameter("redirect", currentURL);
					addPageURL.setParameter("groupId", String.valueOf(myPlace.getGroupId()));
					addPageURL.setParameter("privateLayout", Boolean.FALSE.toString());

					publicAddPageHREF = addPageURL.toString();

					addPageURL.setParameter("privateLayout", Boolean.TRUE.toString());

					privateAddPageHREF = addPageURL.toString();
				}
			}
			else if (regularCommunity) {
				if (GroupPermissionUtil.contains(permissionChecker, myPlace.getGroupId(), ActionKeys.MANAGE_LAYOUTS)) {
					PortletURL addPageURL = new PortletURLImpl(request, PortletKeys.MY_PLACES, plid, PortletRequest.ACTION_PHASE);

					addPageURL.setWindowState(WindowState.NORMAL);
					addPageURL.setPortletMode(PortletMode.VIEW);

					addPageURL.setParameter("struts_action", "/my_places/edit_pages");
					addPageURL.setParameter("redirect", currentURL);
					addPageURL.setParameter("groupId", String.valueOf(myPlace.getGroupId()));
					addPageURL.setParameter("privateLayout", Boolean.FALSE.toString());

					publicAddPageHREF = addPageURL.toString();

					addPageURL.setParameter("privateLayout", Boolean.TRUE.toString());

					privateAddPageHREF = addPageURL.toString();
				}
			}
			else if (userCommunity) {
				PortletURL publicAddPageURL = new PortletURLImpl(request, PortletKeys.MY_ACCOUNT, plid, PortletRequest.RENDER_PHASE);

				publicAddPageURL.setWindowState(WindowState.MAXIMIZED);
				publicAddPageURL.setPortletMode(PortletMode.VIEW);

				publicAddPageURL.setParameter("struts_action", "/my_account/edit_pages");
				publicAddPageURL.setParameter("tabs1", "public-pages");
				publicAddPageURL.setParameter("redirect", currentURL);
				publicAddPageURL.setParameter("groupId", String.valueOf(myPlace.getGroupId()));

				publicAddPageHREF = publicAddPageURL.toString();

				long privateAddPagePlid = myPlace.getDefaultPrivatePlid();

				PortletURL privateAddPageURL = new PortletURLImpl(request, PortletKeys.MY_ACCOUNT, plid, PortletRequest.RENDER_PHASE);

				privateAddPageURL.setWindowState(WindowState.MAXIMIZED);
				privateAddPageURL.setPortletMode(PortletMode.VIEW);

				privateAddPageURL.setParameter("struts_action", "/my_account/edit_pages");
				privateAddPageURL.setParameter("tabs1", "private-pages");
				privateAddPageURL.setParameter("redirect", currentURL);
				privateAddPageURL.setParameter("groupId", String.valueOf(myPlace.getGroupId()));

				privateAddPageHREF = privateAddPageURL.toString();

				if (!PropsValues.LAYOUT_USER_PUBLIC_LAYOUTS_MODIFIABLE) {
					publicAddPageHREF = null;
				}

				if (!PropsValues.LAYOUT_USER_PRIVATE_LAYOUTS_MODIFIABLE) {
					privateAddPageHREF = null;
				}
			}

			boolean showPublicPlace = true;

			boolean hasPowerUserRole = RoleLocalServiceUtil.hasUserRole(user.getUserId(), user.getCompanyId(), RoleConstants.POWER_USER, true);

			if (publicLayoutsPageCount == 0) {
				if (organizationCommunity) {
					showPublicPlace = PropsValues.MY_PLACES_SHOW_ORGANIZATION_PUBLIC_SITES_WITH_NO_LAYOUTS;
				}
				else if (regularCommunity) {
					showPublicPlace = PropsValues.MY_PLACES_SHOW_COMMUNITY_PUBLIC_SITES_WITH_NO_LAYOUTS;
				}
				else if (userCommunity) {
					showPublicPlace = PropsValues.MY_PLACES_SHOW_USER_PUBLIC_SITES_WITH_NO_LAYOUTS;

					if (!PropsValues.LAYOUT_USER_PUBLIC_LAYOUTS_MODIFIABLE || (PropsValues.LAYOUT_USER_PUBLIC_LAYOUTS_POWER_USER_REQUIRED && !hasPowerUserRole)) {
						showPublicPlace = false;
					}
				}
			}

			boolean showPrivatePlace = true;

			if (privateLayoutsPageCount == 0) {
				if (organizationCommunity) {
					showPrivatePlace = PropsValues.MY_PLACES_SHOW_ORGANIZATION_PRIVATE_SITES_WITH_NO_LAYOUTS;
				}
				else if (regularCommunity) {
					showPrivatePlace = PropsValues.MY_PLACES_SHOW_COMMUNITY_PRIVATE_SITES_WITH_NO_LAYOUTS;
				}
				else if (userCommunity) {
					showPrivatePlace = PropsValues.MY_PLACES_SHOW_USER_PRIVATE_SITES_WITH_NO_LAYOUTS;

					if (!PropsValues.LAYOUT_USER_PRIVATE_LAYOUTS_MODIFIABLE || (PropsValues.LAYOUT_USER_PRIVATE_LAYOUTS_POWER_USER_REQUIRED && !hasPowerUserRole)) {
						showPrivatePlace = false;
					}
				}
			}
		%>

			<c:if test="<%= showPublicPlace || showPrivatePlace %>">
				<c:choose>
					<c:when test='<%= PropsValues.MY_PLACES_DISPLAY_STYLE.equals("simple") %>'>

						<%
						portletURL.setParameter("groupId", String.valueOf(myPlace.getGroupId()));
						portletURL.setParameter("privateLayout", Boolean.FALSE.toString());

						boolean firstCommunity = false;

						if (myPlaces.indexOf(myPlace) == 0) {
							firstCommunity = true;
						}

						boolean lastCommunity = false;

						if (myPlaces.size()	== (myPlaces.indexOf(myPlace) + 1)) {
							lastCommunity = true;
						}

						boolean selectedCommunity = false;

						if (layout != null) {
							if (layout.getGroupId() == myPlace.getGroupId()) {
								selectedCommunity = true;
							}
						}

						boolean selectedPlace = false;

						if (layout != null) {
							if (!layout.isPrivateLayout() && (layout.getGroupId() == myPlace.getGroupId())) {
								selectedPlace = true;
							}
						}

						String cssClass = "public-community";

						if (firstCommunity) {
							cssClass += " first";
						}

						if (lastCommunity) {
							cssClass += " last";
						}

						if (selectedCommunity) {
							cssClass += " current-community";
						}

						if (selectedPlace) {
							cssClass += " current-site";
						}
						%>

						<c:if test="<%= showPublicPlace && publicLayoutsPageCount > 0 %>">
							<li class="<%= cssClass %>">
								<a href="<%= HtmlUtil.escape(portletURL.toString()) %>" onclick="Liferay.Util.forcePost(this); return false;">
									<span class="site-name">
										<c:choose>
											<c:when test="<%= organizationCommunity %>">
												<%= HtmlUtil.escape(organization.getName()) %>
											</c:when>
											<c:when test="<%= userCommunity %>">
												<liferay-ui:message key="my-public-pages" />
											</c:when>
											<c:when test="<%= myPlace.getName().equals(GroupConstants.GUEST) %>">
												<%= HtmlUtil.escape(themeDisplay.getAccount().getName()) %>
											</c:when>
											<c:otherwise>
												<%= myPlace.getName() %>
											</c:otherwise>
										</c:choose>
									</span>

									<c:if test="<%= privateLayoutsPageCount > 0 %>">
										<span class="site-type"><liferay-ui:message key="public" /></span>
									</c:if>
								</a>
							</li>
						</c:if>

						<%
						portletURL.setParameter("privateLayout", Boolean.TRUE.toString());

						selectedPlace = false;

						if (layout != null) {
							selectedPlace = layout.isPrivateLayout() && (layout.getGroupId() == myPlace.getGroupId());
						}

						cssClass = "private-community";

						if (selectedCommunity) {
							cssClass += " current-community";
						}

						if (selectedPlace) {
							cssClass += " current-site";
						}
						%>

						<c:if test="<%= showPrivatePlace && privateLayoutsPageCount > 0 %>">
							<li class="<%= cssClass %>">
								<a href="<%= HtmlUtil.escape(portletURL.toString()) %>" onclick="Liferay.Util.forcePost(this); return false;">
									<span class="site-name">
										<c:choose>
											<c:when test="<%= organizationCommunity %>">
												<%= HtmlUtil.escape(organization.getName()) %>
											</c:when>
											<c:when test="<%= userCommunity %>">
												<liferay-ui:message key="my-private-pages" />
											</c:when>
											<c:when test="<%= myPlace.getName().equals(GroupConstants.GUEST) %>">
												<%= HtmlUtil.escape(themeDisplay.getAccount().getName()) %>
											</c:when>
											<c:otherwise>
												<%= myPlace.getName() %>
											</c:otherwise>
										</c:choose>
									</span>

									<c:if test="<%= publicLayoutsPageCount > 0 %>">
										<span class="site-type"><liferay-ui:message key="private" /></span>
									</c:if>
								</a>
							</li>
						</c:if>
					</c:when>
					<c:when test='<%= PropsValues.MY_PLACES_DISPLAY_STYLE.equals("classic") %>'>

						<%
						boolean selectedCommunity = false;

						if (layout != null) {
							if (layout.getGroupId() == myPlace.getGroupId()) {
								selectedCommunity = true;
							}
						}
						%>

						<li class="<%= selectedCommunity ? "current-community" : "" %>">
							<h3>
								<a href="javascript:;">
									<c:choose>
										<c:when test="<%= organizationCommunity %>">
											<%= HtmlUtil.escape(organization.getName()) %>
										</c:when>
										<c:when test="<%= userCommunity %>">
											<liferay-ui:message key="my-community" />
										</c:when>
										<c:otherwise>
											<%= myPlace.getName() %>
										</c:otherwise>
									</c:choose>
								</a>
							</h3>

							<ul>

								<%
								portletURL.setParameter("groupId", String.valueOf(myPlace.getGroupId()));
								portletURL.setParameter("privateLayout", Boolean.FALSE.toString());

								boolean selectedPlace = false;

								if (layout != null) {
									selectedPlace = !layout.isPrivateLayout() && (layout.getGroupId() == myPlace.getGroupId());
								}
								%>

								<c:if test="<%= showPublicPlace %>">
									<li class="public <%= selectedPlace ? "current" : "" %>">
										<a href="<%= publicLayoutsPageCount > 0 ? HtmlUtil.escape(portletURL.toString()) : "javascript:;" %>"

										<c:if test="<%= userCommunity %>">
											id="my-community-public-pages"
										</c:if>

										<c:if test="<%= publicLayoutsPageCount > 0 %>">
											onclick="Liferay.Util.forcePost(this); return false;"
										</c:if>

										><liferay-ui:message key="public-pages" /> <span class="page-count">(<%= publicLayoutsPageCount %>)</span></a>

										<c:if test="<%= publicAddPageHREF != null %>">
											<a class="add-page" href="<%= HtmlUtil.escape(publicAddPageHREF) %>" onclick="Liferay.Util.forcePost(this); return false;"><liferay-ui:message key="manage-pages" /></a>
										</c:if>
									</li>
								</c:if>

								<%
								portletURL.setParameter("groupId", String.valueOf(myPlace.getGroupId()));
								portletURL.setParameter("privateLayout", Boolean.TRUE.toString());

								selectedPlace = false;

								if (layout != null) {
									selectedPlace = layout.isPrivateLayout() && (layout.getGroupId() == myPlace.getGroupId());
								}
								%>

								<c:if test="<%= showPrivatePlace %>">
									<li class="private <%= selectedPlace ? "current" : "" %>">
										<a href="<%= privateLayoutsPageCount > 0 ? HtmlUtil.escape(portletURL.toString()) : "javascript:;" %>"

										<c:if test="<%= userCommunity %>">
											id="my-community-private-pages"
										</c:if>

										<c:if test="<%= privateLayoutsPageCount > 0 %>">
											onclick="Liferay.Util.forcePost(this); return false;"
										</c:if>

										><liferay-ui:message key="private-pages" /> <span class="page-count">(<%= privateLayoutsPageCount %>)</span></a>

										<c:if test="<%= privateAddPageHREF != null %>">
											<a class="add-page" href="<%= HtmlUtil.escape(privateAddPageHREF) %>" onclick="Liferay.Util.forcePost(this); return false;"><liferay-ui:message key="manage-pages" /></a>
										</c:if>
									</li>
								</c:if>
							</ul>
						</li>
					</c:when>
				</c:choose>
			</c:if>

		<%
		}
		%>

	</ul>
</c:if>