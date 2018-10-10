<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="javax.portlet.*"%>
<%@ page import="org.gcube.common.portal.*"%>
<%@ page import=" org.gcube.portal.invites.InvitesManager"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>

<portlet:defineObjects />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<portlet:resourceURL id="createAccount" var="createAccount"></portlet:resourceURL>

<script type="text/javascript">
	$(document).ready(function() {
		doCallback = function() {
			$('#formCreateAccountWrapper').css("display", "none");// to clear the previous option
			$('#loadingAjaxCallback').css("display", "block");
			$.ajax({
				url : "${createAccount}",
				type : 'POST',
				datatype : 'json',
				data : {
					email : $("#email").val(),
					firstname : $("#firstname").val(),
					lastname : $("#lastname").val(),
					password : $("#password").val(),
					repassword : $("#repassword").val()
				},
				success : function(data) {
					$('#loadingAjaxCallback').css("display", "none");
					var result = data.toString().trim();
					if (result == "OK") {
						$('#successDiv').css("display", "block");
					} else {
						$('#errorDiv').css("display", "block");
						$('#errorLabel').text(data);
					}
				}
			});
		}
	});
</script>
<div id="errorDiv" style="display: none;" class="alert alert-error">
	<button type="button" class="close" data-dismiss="alert">&times;</button>
	<h1>Oh snap! You got an error</h1>
	<label id="errorLabel"></label> <br> <br /> If you believe this
	requires support please go to <a
		href="http://www.d4science.org/contact-us" target="_blank">http://www.d4science.org/contact-us</a>
	to ask for D4Science Help Desk support. <br> <br />
</div>

<div id="successDiv"  style="display: none;" class="alert alert-success">
	<h1>Well done!</h1>
	Your account has been successfully created! You will now be asked to sign in using your yet created account. Please, click on the button below to continue.
	<%
		String acceptInviteURL = request.getAttribute("landingPage") + "/explore?" + InvitesManager.SITEID_ATTR
				+ "=" + request.getAttribute("groupId");
	%>
	<p style="margin-top: 20px;">
		<button class="btn btn-large"
			onclick="window.location.href='<%=acceptInviteURL%>'" type="button">
			Continue accept invite on
			<c:out escapeXml="true" value="${vreName}" />
		</button>
	</p>
</div>
<div id="formCreateAccountWrapper">
	<h1>
		Hello
		<c:out escapeXml="true" value="${invitedUser.firstName}" />
		!<br>
	</h1>
	<p class="lead">
		You have recently received an invitation from
		<c:out escapeXml="true" value="${inviteInstance.senderFullName}" />
		to join the <a
			href="/web<c:out escapeXml="true" value="${vreFriendlyURL}" />"
			target="_blank"><c:out escapeXml="true" value="${vreName}" /></a>
		Virtual Research Environment. <img id="loadingAjaxCallback"
			style="display: none;"
			src="<%=renderRequest.getContextPath()%>/images/loader.gif" />
		<c:choose>
			<c:when test="${empty invitedUser}">
				<br />
				<span style="font-style: italic;">Please note</span>: the invite is valid for your email <a
					href="mailto:<c:out escapeXml="true" value="${inviteInstance.invitedEmail}" />">
					<c:out escapeXml="true" value="${inviteInstance.invitedEmail}" />
				</a> only. To accept the invite, please fill in the information below:
        <br />

				<div class="container-fluid">
					<div class="row-fluid">
						<div class="span4">
							<fieldset>
								<label>Email (Not editable)</label> <input type="text"
									name="email" value="${inviteInstance.invitedEmail}" readonly
									id="email" style="color: #999;" /> <label id="labelFirstName">First
									Name (Required)</label> <input type="text" id="firstname" /> <label
									id="labelLastName">Last Name (Required)</label> <input
									type="text" id="lastname" />
							</fieldset>
						</div>
						<div class="span4">
							<fieldset>
								<label id="labelPwd1">Password</label> <input type="password"
									id="password" /> <label id="labelPwd2">Confirm
									Password</label> <input type="password" id="repassword" /> <label
									style="display: none" id="labelPasswordDontMatch">Passwords
									don't match</label> <label style="display: none"
									id="labelPasswordTooShort">Password must be at least 8
									chars length</label>
								<div style="margin-top: 20px;">
									<button class="btn-primary btn-large" type="button"
										id="createAccountButton">Continue</button>
								</div>
							</fieldset>
						</div>
					</div>
				</div>

			</c:when>
			<c:otherwise>
    This invite is valid for your email <a
					href="mailto:<c:out escapeXml="true" value="${inviteInstance.invitedEmail}" />">
					<c:out escapeXml="true" value="${inviteInstance.invitedEmail}" />
				</a> only, you will be asked to enter your password associated to it on this portal. 
 
 <%
					String exploreURL = request.getAttribute("landingPage") + "/explore?" + InvitesManager.SITEID_ATTR
									+ "=" + request.getAttribute("groupId");
				%>
				<p class="lead">
					<button class="btn btn-large btn-primary"
						onclick="window.location.href='<%=exploreURL%>'" type="button">
						Accept invite on
						<c:out escapeXml="true" value="${vreName}" />
					</button>
				</p>
			</c:otherwise>
		</c:choose>
	</p>
</div>