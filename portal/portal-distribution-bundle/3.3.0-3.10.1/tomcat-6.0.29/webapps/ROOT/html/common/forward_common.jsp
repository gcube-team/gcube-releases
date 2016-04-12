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
String forwardURL = null;

String forwardParam = PortalUtil.escapeRedirect(request.getParameter(WebKeys.FORWARD_URL));
String forwardRequest = (String)request.getAttribute(WebKeys.FORWARD_URL);
String forwardSession = (String)session.getAttribute(WebKeys.FORWARD_URL);

if ((forwardParam != null) && (!forwardParam.equals(StringPool.NULL)) && (!forwardParam.equals(StringPool.BLANK))) {
	forwardURL = forwardParam;
}
else if ((forwardRequest != null) && (!forwardRequest.equals(StringPool.NULL)) && (!forwardRequest.equals(StringPool.BLANK))) {
	forwardURL = forwardRequest;
}
else if ((forwardSession != null) && (!forwardSession.equals(StringPool.NULL)) && (!forwardSession.equals(StringPool.BLANK))) {
	forwardURL = forwardSession;
}
else if (themeDisplay != null) {
	forwardURL = themeDisplay.getPathMain();
}
else {
	forwardURL = PortalUtil.getPathMain();
}
%>