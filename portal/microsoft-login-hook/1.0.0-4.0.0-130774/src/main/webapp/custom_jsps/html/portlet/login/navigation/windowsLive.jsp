<%@ include file="/html/portlet/login/init.jsp" %>

<%@ page import="com.liferay.portal.util.PortalUtil" %>

<link rel="stylesheet" href="/html/portlet/login/css/windowsLive.css">

<!-- initialize variables for microsoft login request -->
<%

String jspName = this.getClass().getSimpleName().replaceFirst("_jsp","");

boolean isWindowsLiveAuthEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), "windowsLive.auth.enabled", true);
String windowsLiveLoginUrl = PortalUtil.getPathContext() + PropsUtil.get("windowsLive.hook.call.url");
String redirect = ParamUtil.getString(request, "redirect");	
session.setAttribute("redirectUrlAfterLogin", redirect);

String[] navSocialsArray = PropsUtil.getArray("login.form.navigation.socials");
String[] navPostsArray = PropsUtil.getArray("login.form.navigation.post");

Set<String> navSocialsSet = new HashSet<String>(Arrays.asList(navSocialsArray));
Set<String> navPostsSet = new HashSet<String>(Arrays.asList(navPostsArray));

boolean inSocials = navSocialsSet.contains(jspName);
boolean inPosts = navPostsSet.contains(jspName);
%>
<!-- If windows live login is enabled create the initial request Url -->
<c:if test="<%= isWindowsLiveAuthEnabled %>">

	<div class="social-hook">
		<c:choose>
		<c:when test="<%= inSocials %>">
			<a href="<%= windowsLiveLoginUrl %>" class="microsoft-hook-link">
				<div class="windowsLive-hook-label-wrapper">
					<div class="windowsLive-hook-icon">
						<h5>
							<i class="icon-windows"></i>
						</h5>
					</div>
					<div class="windowsLive-hook-text">
						<h5>
							<%= LanguageUtil.get(pageContext, "sign-in-with-windowsLive")%>
						</h5>
					</div>
				</div>
			</a>
		</c:when>
		<c:when test="<%= inPosts %>">
			<liferay-ui:icon
				message="windowsLive"
				src="/html/portlet/login/navigation/windowsLive.png"
				url="<%= windowsLiveLoginUrl %>"
			/>
		</c:when>
		</c:choose>
	</div>
	
</c:if>