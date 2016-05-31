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

<%@ include file="/html/portlet/iframe/init.jsp" %>

<%
String iframeSrc = StringPool.BLANK;

if (relative) {
	iframeSrc = themeDisplay.getPathContext();
}

iframeSrc += (String)request.getAttribute(WebKeys.IFRAME_SRC);

if (Validator.isNotNull(iframeVariables)) {
	if (iframeSrc.indexOf(StringPool.QUESTION) != -1) {
		iframeSrc = iframeSrc.concat(StringPool.AMPERSAND).concat(StringUtil.merge(iframeVariables, StringPool.AMPERSAND));
	}
	else {
		iframeSrc = iframeSrc.concat(StringPool.QUESTION).concat(StringUtil.merge(iframeVariables, StringPool.AMPERSAND));
	}
}

String baseSrc = iframeSrc;

int lastSlashPos = iframeSrc.substring(7).lastIndexOf(StringPool.SLASH);

if (lastSlashPos != -1) {
	baseSrc = iframeSrc.substring(0, lastSlashPos + 8);
}

String iframeHeight = heightNormal;

if (windowState.equals(WindowState.MAXIMIZED)) {
	iframeHeight = heightMaximized;
}
%>

<c:choose>
	<c:when test="<%= auth && Validator.isNull(userName) && !themeDisplay.isSignedIn() %>">
		<div class="portlet-msg-info">
			<a href="<%= themeDisplay.getURLSignIn() %>" target="_top"><liferay-ui:message key="please-sign-in-to-access-this-application" /></a>
		</div>
	</c:when>
	<c:otherwise>
		<div>
			<iframe alt="<%= alt %>" border="<%= border %>" bordercolor="<%= bordercolor %>" frameborder="<%= frameborder %>" height="<%= iframeHeight %>" hspace="<%= hspace %>" id="<portlet:namespace />iframe" longdesc="<%= longdesc%>" name="<portlet:namespace />iframe" onload="<portlet:namespace />monitorIframe(); <%= resizeAutomatically ?  renderResponse.getNamespace() + "resizeIframe();" : StringPool.BLANK %>" scrolling="<%= scrolling %>" src="<%= iframeSrc %>" vspace="<%= vspace %>" width="<%= width %>">
				<%= LanguageUtil.format(pageContext, "your-browser-does-not-support-inline-frames-or-is-currently-configured-not-to-display-inline-frames.-content-can-be-viewed-at-actual-source-page-x", iframeSrc) %>
			</iframe>
		</div>
	</c:otherwise>
</c:choose>

<aui:script>
	function <portlet:namespace />maximizeIframe(iframe) {
		var winHeight = 0;

		if (typeof(window.innerWidth) == 'number') {

			// Non-IE

			winHeight = window.innerHeight;
		}
		else if ((document.documentElement) &&
				 (document.documentElement.clientWidth || document.documentElement.clientHeight)) {

			// IE 6+

			winHeight = document.documentElement.clientHeight;
		}
		else if ((document.body) &&
				 (document.body.clientWidth || document.body.clientHeight)) {

			// IE 4 compatible

			winHeight = document.body.clientHeight;
		}

		// The value 139 here is derived (tab_height * num_tab_levels) +
		// height_of_banner + bottom_spacer. 139 just happend to work in
		// this instance in IE and Firefox at the time.

		iframe.height = (winHeight - 139);
	}

	function <portlet:namespace />monitorIframe() {
		var url = null;

		try {
			var iframe = document.getElementById('<portlet:namespace />iframe');

			url = iframe.contentWindow.document.location.href;
		}
		catch (e) {
			return true;
		}

		var baseSrc = '<%= baseSrc %>';
		var iframeSrc = '<%= iframeSrc %>';

		if ((url == iframeSrc) || (url == iframeSrc + '/')) {
		}
		else if (Liferay.Util.startsWith(url, baseSrc)) {
			url = url.substring(baseSrc.length);

			<portlet:namespace />updateHash(url);
		}
		else {
			<portlet:namespace />updateHash(url);
		}

		return true;
	}

	function <portlet:namespace />resizeIframe() {
		var iframe = document.getElementById('<portlet:namespace />iframe');

		var height = null;

		try {
			height = iframe.contentWindow.document.body.scrollHeight;
		}
		catch (e) {
			if (themeDisplay.isStateMaximized()) {
				<portlet:namespace />maximizeIframe(iframe);
			}
			else {
				iframe.height = <%= heightNormal %>;
			}

			return true;
		}

		iframe.height = height + 50;

		return true;
	}

	Liferay.provide(
		window,
		'<portlet:namespace />init',
		function() {
			var A = AUI();

			var hash = document.location.hash;

			if ((hash != '#') && (hash != '')) {
				var src = '';

				var path = hash.substring(1);

				if (path.indexOf('http://') != 0) {
					src = '<%= baseSrc %>';
				}

				src += path;

				var iframe = A.one('#<portlet:namespace />iframe');

				if (iframe) {
					iframe.attr('src', src);
				}
			}
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />updateHash',
		function(url) {
			var A = AUI();

			document.location.hash = url;

			var maximize = A.one('#p_p_id<portlet:namespace /> .portlet-maximize-icon a');

			if (maximize) {
				var href = maximize.attr('href');

				if (href.indexOf('#') != -1) {
					href = href.substring(0, href.indexOf('#'));
				}

				maximize.attr('href', href + '#' + url);
			}

			var restore = A.one('#p_p_id<portlet:namespace /> a.portlet-icon-back');

			if (restore) {
				var href = restore.attr('href');

				if (href.indexOf('#') != -1) {
					href = href.substring(0, href.indexOf('#'));
				}

				restore.attr('href', href + '#' + url);
			}
		},
		['aui-base']
	);

	<portlet:namespace />init();
</aui:script>