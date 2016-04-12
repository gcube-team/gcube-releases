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

<%@ include file="/html/taglib/ui/social_bookmarks/init.jsp" %>

<c:if test="<%= typesArray.length > 0 %>">

	<%
	String randomNamespace = PortalUtil.generateRandomKey(request, "taglib_ui_social_bookmarks_page") + StringPool.UNDERLINE;
	%>

	<div class="taglib-social-bookmarks" id="<%= randomNamespace %>socialBookmarks">
		<a class="show-bookmarks" href="javascript:;"><liferay-ui:message key="add-this-to" /><img alt="delicious" src="<%= themeDisplay.getPathThemeImages() %>/social_bookmarks/delicious.png" width="10" /> <img alt="digg" src="<%= themeDisplay.getPathThemeImages() %>/social_bookmarks/digg.png" width="10" /> <img alt="furl" src="<%= themeDisplay.getPathThemeImages() %>/social_bookmarks/furl.png" width="10" /></a>

		<ul class="lfr-component">

			<%
			for (int i = 0; i < typesArray.length; i++) {
			%>

				<li>
					<liferay-ui:social-bookmark type="<%= typesArray[i] %>" url="<%= url %>" title="<%= title %>" target="<%= target %>" />
				</li>

			<%
			}
			%>

		</ul>
	</div>

	<aui:script use="aui-base">
		var socialBookmarks = A.one('#<%= randomNamespace %>socialBookmarks');

		if (socialBookmarks) {
			var linkSocialBookmarks = socialBookmarks.all('.show-bookmarks');

			if (linkSocialBookmarks) {
				linkSocialBookmarks.on(
					'click',
					function(event) {
						socialBookmarks.toggleClass('visible');
					}
				);
			}
		}
	</aui:script>
</c:if>