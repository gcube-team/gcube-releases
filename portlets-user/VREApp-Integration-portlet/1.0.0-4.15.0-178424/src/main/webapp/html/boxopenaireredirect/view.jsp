<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@ page import="com.liferay.portal.util.PortalUtil" %>
<portlet:defineObjects />
<%
String screenName = UserLocalServiceUtil.getUser(PortalUtil.getUserId(request)).getScreenName();
%>
<script>
location.href= 'https://box.openaire.eu?user=<%=screenName%>';
</script>