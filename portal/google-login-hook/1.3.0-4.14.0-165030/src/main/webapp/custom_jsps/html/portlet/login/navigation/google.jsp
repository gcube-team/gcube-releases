<%@ include file="/html/portlet/login/init.jsp" %>

<%@ page import="com.liferay.portal.util.PortalUtil" %>

<link rel="stylesheet" href="/html/portlet/login/css/google.css">

<!-- initialize variables for google login request -->
<%

boolean isGoogleAuthEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), "google.auth.enabled", true);
String googleLoginUrl = PortalUtil.getPathContext().toString() + PropsUtil.get("google.hook.call.url");
String redirect = ParamUtil.getString(request, "redirect");
session.setAttribute("redirectUrlAfterLogin", redirect);

String[] navSocialsArray = PropsUtil.getArray("login.form.navigation.socials");
String[] navPostsArray = PropsUtil.getArray("login.form.navigation.post");

Set<String> navSocialsSet = new HashSet<String>(Arrays.asList(navSocialsArray));
Set<String> navPostsSet = new HashSet<String>(Arrays.asList(navPostsArray));

Boolean inSocials = navSocialsSet.contains("google");
Boolean inPosts = navPostsSet.contains("google");

%>

<c:if test="<%= isGoogleAuthEnabled %>">

	<div class="social-hook">
		<c:choose>
		<c:when test="<%= inSocials %>">
			<a href="<%= googleLoginUrl.toString() %>" class="google-hook-link">
				<div class="google-hook-label-wrapper">
					<div class="google-hook-icon">
						<h5>
							<i class="icon-google-plus"></i>
						</h5>
					</div>
					<div class="google-hook-text">
						<h5>
							<%= LanguageUtil.get(pageContext, "sign-in-with-google")%>
						</h5>
					</div>
				</div>
			</a>
		</c:when>
		<c:when test="<%= inPosts %>">
			<liferay-ui:icon
				message="google"
				src="/html/portlet/login/navigation/google.png"
				url="<%= googleLoginUrl.toString() %>"
			/>
		</c:when>
		</c:choose>
	</div>
	
</c:if>