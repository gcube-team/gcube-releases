<%@ include file="/html/portlet/login/init.jsp" %>

<%@ page import="com.liferay.portal.util.PortalUtil" %>

<link rel="stylesheet" href="/html/portlet/login/css/shibboleth.css">

<%

/* initialize variables for login login request */

boolean isShibbolethEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), "shibboleth.auth.enabled", true);
String shibbolethLoginUrl = PortalUtil.getPathContext() + PropsUtil.get("shibboleth.hook.call.url");
String redirect = ParamUtil.getString(request, "redirect");
session.setAttribute("redirectUrlAfterLogin", redirect);

String[] navSocialsArray = PropsUtil.getArray("login.form.navigation.socials");
String[] navPostsArray = PropsUtil.getArray("login.form.navigation.post");

Set<String> navSocialsSet = new HashSet<String>(Arrays.asList(navSocialsArray));
Set<String> navPostsSet = new HashSet<String>(Arrays.asList(navPostsArray));

boolean inSocials = navSocialsSet.contains("shibboleth");
boolean inPosts = navPostsSet.contains("shibboleth");

%>

<c:if test="<%= isShibbolethEnabled %>">

	<div class="social-hook">
		<c:choose>
		<c:when test="<%= inSocials %>">
			<a href="<%= shibbolethLoginUrl %>" class="shibboleth-hook-link">
				<div class="shibboleth-hook-label-wrapper">
					<div class="shibboleth-hook-icon">
						<h5>
							<i class="icon-external-link"></i>
						</h5>
					</div>
					<div class="shibboleth-hook-text">
						<h5><%= LanguageUtil.get(pageContext, "sign-in-with-shibboleth")%></h5>
					</div>
				</div>
			</a>
		</c:when>
		<c:when test="<%= inPosts %>">
			<liferay-ui:icon
				message="shibboleth"
				src="/html/portlet/login/navigation/geant.png"
				url="<%= shibbolethLoginUrl %>"
			/>
		</c:when>
		</c:choose>
	</div>
</c:if>
