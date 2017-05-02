<%--
/**
 * Copyright (c) gCube Framework. All rights reserved.
 *
 * @Author Massimiliano Assante, CNR-ISTI Italy
 */
--%>

<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>

<%
	if (redirect.startsWith(siteUrl + GCubePortalConstants.AUTHORIZATION_FRIENDLY_URL)) {
%>
<script>
	$(".portlet-title-text").text("Sign in to approve application");
	$("#navigation").css("display", "none");
</script>
<div style="text-align: center;">
	<%
		
			Map<String, String> queryMap = AuthUtil.getQueryMap(redirect);
			if (!queryMap.isEmpty()) {
				String clientId = queryMap.get(GET_CLIENT_ID_PARAMETER);
				if (clientId == null || clientId.compareTo("")==0) {
					clientNotAuthorised = true;%>
					<p>We're sorry your application is not authorised by
							D4Science</p>
						<p style="color: red;">
							Please check that you are passing all the required parameters for you application
						</p>
				<%}
				String scope = queryMap.get(GET_SCOPE_PARAMETER);
				if (scope != null && scope.compareTo("") != 0) {
					boolean isValid = AuthUtil.isValidContext(scope);
					clientNotAuthorised = !isValid;
					if (!isValid) {%>
					<p>We're sorry the scope (infrastructure context) you passed as parameter is not valid in D4Science, passed scope: <b><%= scope %></b></p>
						<p style="color: red;">
							Please check that you are passing all the required parameters for you application
						</p>
				<%  }
				}
				if (clientId != null && clientId.compareTo("") != 0 && !clientNotAuthorised) {
					RequestingApp app = AuthUtil.getAuthorisedApplicationInfoFromIs(clientId);
					if (app != null) {
						hideCreateAccountAndForgotPassword = true;
						%>
						
	<div>
		<%
		if (app.getLogoURL() != null && !app.getLogoURL().isEmpty()) {
		%>

		<img style="width: auto; height: 75px; float: left;" src="<%=app.getLogoURL()%>" />

		<%
			}
		%>
	
	<p class="lead">
		<span style="font-weight: bolder;"><%=app.getApplicationId()%></span>
		would like to access some of your D4Science info: <br /> Name, photo,
		email and current roles
		<%
			String displayContext = scope;
			if (scope != null && scope.compareTo("") != 0) {				
				displayContext = scope.substring(scope.lastIndexOf('/')+1);
		%>
		on context: <span style="font-weight: bolder;"><%=displayContext%></span>
		<%
			}
		%>
	
	<p>
	</div>
	<p>By signing in with your <span style="font-weight: bolder;"><%=context.getGatewayName(request)%> credentials</span> you are approving
		this application's request to access your data and interact
		with D4Science on your behalf.
	<p>
	
</div>
<%
	} else {
%>
<p class="lead">We're sorry your application is not authorised by
	D4Science</p>
<p style="color: red;">
							The client_id does not exist or something occurred in retrieving it
							from the Information System: (<%=clientId%>)
						</p>
<%
	clientNotAuthorised = true;
	System.out.println(
							"Please check that you are passing all the required parameters: clientId="
									+ clientId);
				}
			}
		}
	}
%>