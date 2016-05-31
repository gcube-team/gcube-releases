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

<%@ include file="/html/taglib/ui/captcha/init.jsp" %>

<%
String url = (String)request.getAttribute("liferay-ui:captcha:url");

boolean captchaEnabled = false;

try {
	if (portletRequest != null) {
		captchaEnabled = CaptchaUtil.isEnabled(portletRequest);
	}
	else {
		captchaEnabled = CaptchaUtil.isEnabled(request);
	}
}
catch (CaptchaMaxChallengesException cmce) {
	captchaEnabled = true;
}
%>

<c:if test="<%= captchaEnabled %>">
	<div class="taglib-captcha">
		<img alt="captcha" class="captcha" src="<%= url %>" />

		<table class="lfr-table">
		<tr>
			<td>
				<liferay-ui:message key="text-verification" />
			</td>
			<td>
				<input name="<%= namespace %>captchaText" size="10" type="text" value="" />
			</td>
		</tr>
		</table>
	</div>
</c:if>