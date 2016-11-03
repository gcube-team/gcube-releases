<%@ include file="/html/portlet/login/init.jsp" %>

<%

String[] socials = PropsUtil.getArray("login.form.navigation.socials");

%>


<div class="socials">
<% for (String social : socials){ %>

	<div class="social">
		<liferay-util:include page='<%= "/html/portlet/login/navigation/" + social + ".jsp" %>' portletId="<%= portletDisplay.getRootPortletId() %>" />
	</div>

<% } %>
</div>