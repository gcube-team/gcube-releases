<%@page import="org.gcube.portlet.user.ToUPortlet"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@ page import="com.liferay.portal.model.User"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@page import="com.liferay.portal.theme.PortletDisplay"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="theme"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<portlet:actionURL var="portletActionURLAccept" windowState="normal"
	name="acceptToU">
</portlet:actionURL>

<portlet:actionURL var="portletActionURLDeny" windowState="normal"
	name="denyToU">
</portlet:actionURL>

<%
	// get the user and the group
	ThemeDisplay themeDisplay = (ThemeDisplay) request
	.getAttribute(WebKeys.THEME_DISPLAY);
	User user = themeDisplay.getUser();
	long groupId = themeDisplay.getSiteGroupId();
	String screename = user.getScreenName();
	boolean accepted = ToUPortlet.hasAcceptedLatestToU(groupId,
	screename);
	if (accepted) {
		// hide this portlet
		renderRequest.setAttribute(
		WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.FALSE);
	} else {
%>
<script>
    window.onload = function() {
		var url = window.location.href;
		var splitOngroup = url.split("group");
		var composedUrl = splitOngroup[0] + "group/";
		var splitsAfterGroup = splitOngroup[1].split("/");
		composedUrl = composedUrl + splitsAfterGroup[1] + (splitsAfterGroup[1].endsWith("/")? "tou/" : "/tou/");
		document.getElementById("view_tou").href = composedUrl;
    }
</script>
<div>
	<div style="font-size: 18px">
		A change in our Terms of Use has been applied. Please read them
		carefully. They are available <a id="view_tou" target="_blank">here</a>.
	</div>
	<br>

	<div class="alert alert-block" id="alertBlock" style="display: none">
		<p style="font-size: 18px">
			<strong>Warning!</strong> Are you sure you want to deny terms of use
			for this group? By doing so you will be removed from this group, thus
			you will no longer receive updates and lose the workspace folder
			related to the group.
		</p>
		<div style="margin-top: 10px">
			<a href="javascript:showFormHideAlert()"
				class="buttons-alert-accept-or-leave btn-link">Cancel</a> or <a
				class="buttons-alert-accept-or-leave btn-link"
				href="<%=portletActionURLDeny%>">Deny Terms</a>
		</div>
	</div>

	<form id="formDismissOrAccept">
		<fieldset>
			<div class="control-group">
				<div class="controls">
					<div class="btn-group btn-group-horizontal" style="float: right">
						<a href="<%=portletActionURLAccept%>" class="btn btn-primary">Accept
							Terms</a>
						<div class="divider"></div>
						<a class="btn" onclick="return showAlertHideForm()">Deny Terms</a>
					</div>
				</div>
			</div>
		</fieldset>
	</form>
</div>
<%
	}
%>
