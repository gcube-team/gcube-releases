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

<%@ include file="/html/common/init.jsp" %>

<%
String referer = null;

String refererParam = PortalUtil.escapeRedirect(request.getParameter(WebKeys.REFERER));
String refererRequest = (String)request.getAttribute(WebKeys.REFERER);
String refererSession = (String)session.getAttribute(WebKeys.REFERER);

if ((refererParam != null) && (!refererParam.equals(StringPool.NULL)) && (!refererParam.equals(StringPool.BLANK))) {
	referer = refererParam;
}
else if ((refererRequest != null) && (!refererRequest.equals(StringPool.NULL)) && (!refererRequest.equals(StringPool.BLANK))) {
	referer = refererRequest;
}
else if ((refererSession != null) && (!refererSession.equals(StringPool.NULL)) && (!refererSession.equals(StringPool.BLANK))) {
	referer = refererSession;
}
else if (themeDisplay != null) {
	referer = themeDisplay.getPathMain();
}
else {
	referer = PortalUtil.getPathMain();
}
%>