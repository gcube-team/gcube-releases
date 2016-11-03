<%@ include file="/html/portlet/login/init.jsp" %>

<%@ page import="com.liferay.portal.util.PortalUtil" %>

<link rel="stylesheet" href="/html/portlet/login/css/linkedin.css">

<!-- initialize variables for login login request -->
<%

boolean isLinkedAuthInEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), "linkedIn.auth.enabled", true);
String linkedinLoginUrl = PortalUtil.getPathContext() + PropsUtil.get("linkedIn.hook.call.url");
String redirect = ParamUtil.getString(request, "redirect");		
session.setAttribute("redirectUrlAfterLogin", redirect);

String[] navSocialsArray = PropsUtil.getArray("login.form.navigation.socials");
String[] navPostsArray = PropsUtil.getArray("login.form.navigation.post");

Set<String> navSocialsSet = new HashSet<String>(Arrays.asList(navSocialsArray));
Set<String> navPostsSet = new HashSet<String>(Arrays.asList(navPostsArray));

boolean inSocials = navSocialsSet.contains("linkedIn");
boolean inPosts = navPostsSet.contains("linkedIn");

%>

<c:if test="<%= isLinkedAuthInEnabled %>">

	<div class="social-hook">
		<c:choose>
		<c:when test="<%= inSocials %>">
			<a href="<%= linkedinLoginUrl %>" class="linkedin-hook-link">
				<div class="linkedin-hook-label-wrapper">
					<div class="linkedin-hook-icon">
						<h5>
							<i class="icon-linkedin"></i>
						</h5>
					</div>
					<div class="linkedin-hook-text">
						<h5>
							<%= LanguageUtil.get(pageContext, "sign-in-with-linkedin")%>
						</h5>
					</div>
				</div>
			</a>
		</c:when>
		<c:when test="<%= inPosts %>">
			<liferay-ui:icon
				message="linkedin"
				src="/html/portlet/login/navigation/linkedin.png"
				url="<%= linkedinLoginUrl %>"
			/>
		</c:when>
		</c:choose>
	</div>
	
</c:if>