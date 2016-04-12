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

<%@ include file="/html/portlet/communities/init.jsp" %>

<%
String tabs2 = "staging";

String pagesRedirect = ParamUtil.getString(request, "pagesRedirect");

long proposalId = ParamUtil.getLong(request, "proposalId");

TasksProposal proposal = TasksProposalLocalServiceUtil.getProposal(proposalId);

String classPK = proposal.getClassPK();

int pos = classPK.indexOf(PortletConstants.LAYOUT_SEPARATOR);

String selPortletId = classPK.substring(pos + PortletConstants.LAYOUT_SEPARATOR.length());

Portlet selPortlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), selPortletId);

String selPlid = classPK.substring(0, pos);

long groupId = ParamUtil.getLong(request, "groupId");

Group selGroup = GroupLocalServiceUtil.getGroup(groupId);

Group liveGroup = selGroup.getLiveGroup();
Group stagingGroup = selGroup;

long liveGroupId = liveGroup.getGroupId();
long stagingGroupId = stagingGroup.getGroupId();

PortletURL portletURL = renderResponse.createActionURL();

portletURL.setParameter("struts_action", "/communities/edit_proposal");
portletURL.setParameter(Constants.CMD, Constants.PUBLISH);
portletURL.setParameter("proposalId", String.valueOf(proposalId));
portletURL.setParameter("groupId", String.valueOf(liveGroupId));
portletURL.setParameter("plid", selPlid);

boolean supportsLAR = Validator.isNotNull(selPortlet.getPortletDataHandlerClass());
boolean supportsSetup = Validator.isNotNull(selPortlet.getConfigurationActionClass());

response.setHeader("Ajax-ID", request.getHeader("Ajax-ID"));
%>

<aui:form action="<%= portletURL.toString() %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "publish();" %>'>
	<aui:input name="pagesRedirect" type="hidden" value="<%= pagesRedirect %>" />
	<aui:input name="stagingGroupId" type="hidden" value="<%= stagingGroupId %>" />

	<%@ include file="/html/portlet/portlet_configuration/export_import_options.jspf" %>

	<aui:button-row>
		<aui:button name="publishBtn" type="submit" value="publish" />

		<aui:button onClick="AUI().DialogManager.closeByChild(this);" type="cancel" />
	</aui:button-row>
</aui:form>

<aui:script>
	function <portlet:namespace />publish() {
		if (confirm('<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-publish-this-portlet") %>')) {
			submitForm(document.<portlet:namespace />fm);
		}
	}
</aui:script>