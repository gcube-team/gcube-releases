<%@ page session="false" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" import="java.util.*,javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.liferay.portal.model.User"%>
<%@ page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@ page import="com.liferay.portal.service.GroupLocalServiceUtil"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="theme"%>
<%@ page import="com.liferay.portal.model.Contact"%>
<%@ page import="com.liferay.portal.model.Group"%>
<%@ page import="com.liferay.portal.theme.PortletDisplay"%>
<%@ page import="com.liferay.portlet.social.model.SocialRelationConstants"%>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@ page import="com.liferay.portal.kernel.util.WebKeys" %>
<%@ page import="com.liferay.util.portlet.PortletProps" %>

<theme:defineObjects />
<portlet:defineObjects />

<%
String maxNumEmails = PortletProps.get("max-num-emails");

String enterNewEmail = LanguageUtil.get(pageContext, "enter-your-new-email");
String emailIsNotVerified = LanguageUtil.get(pageContext, "your-email-is-not-verified");
String reverifyYourEmailAddress = LanguageUtil.get(pageContext, "reverify-your-email-address");
String cancelButtonLabel = LanguageUtil.get(pageContext, "cancel");
String saveButtonLabel = LanguageUtil.get(pageContext, "save");

%>

<script>window.jQuery || document.write('<script src="<c:url value="/scripts/jquery-1.10.2.min.js" />" type="text/javascript"><\/script>')</script>

<script src="<c:url value="/scripts/additionalEmailAddresses.js" />"></script>

<portlet:resourceURL id="sendEmailVerification" var="sendEmailVerification" />
<portlet:resourceURL id="listAdditionalEmailAddresses" var="listAdditionalEmailAddresses" />
<portlet:resourceURL id="removeAdditionalEmail" var="removeAdditionalEmail" />
<portlet:resourceURL id="selectPrimaryEmailAddress" var="selectPrimaryEmailAddress" />
<portlet:resourceURL id="resendVerificationEmail" var="resendVerificationEmail" />
<portlet:resourceURL id="isEmailAddressAlreadyUsed" var="isEmailAddressAlreadyUsed" />

<p class="subtitle"><%= LanguageUtil.get(pageContext, "associate-more-emails-to-your-account") %></p>

<div class="additional-email-addresses">
	<form id="addtional-email-addresses-form">
		<div class="row-fluid add-email-buttons">
			<div class="span1"></div>
			<div class="span9"><button id="add-email" type="button" class="btn"><%= LanguageUtil.get(pageContext, "add-email") %></button></div>
			<div class="span2"></div>
		</div>
	</form>
	<div class="row-fluid max-emails-notification" hidden="true">
		<div class="span1"></div>
		<div class="span9"><p><%= LanguageUtil.get(pageContext, "max-number-of-emails-has-been-reached") %></p></div>
		<div class="span2"></div>
	</div>
</div>

<script defer="defer" type="text/javascript">
	(function() {
		$(document).ready(function() {

			var sendEmailVerification = '<%= sendEmailVerification %>';
			var isEmailAddressAlreadyUsed = '<%= isEmailAddressAlreadyUsed %>';
			var listAdditionalEmailAddresses = '<%= listAdditionalEmailAddresses %>';
			var removeAdditionalEmail = '<%= removeAdditionalEmail %>';
			var selectPrimaryEmailAddress = '<%= selectPrimaryEmailAddress %>';
			var resendVerificationEmail = '<%= resendVerificationEmail %>';
			var maxNumEmails = '<%= maxNumEmails %>';
			
			var enterNewEmail = '<%= enterNewEmail %>';
			var emailIsNotVerified = '<%= emailIsNotVerified %>';
			var reverifyYourEmailAddress = '<%= reverifyYourEmailAddress %>';
			var cancelButtonLabel = '<%= cancelButtonLabel %>';
			var saveButtonLabel = '<%= saveButtonLabel %>';
			
			additionalEmailAddressesNS.init(sendEmailVerification, listAdditionalEmailAddresses, removeAdditionalEmail, selectPrimaryEmailAddress, resendVerificationEmail, maxNumEmails, isEmailAddressAlreadyUsed);
			
			additionalEmailAddressesNS.language(enterNewEmail, emailIsNotVerified, reverifyYourEmailAddress, cancelButtonLabel, saveButtonLabel);

		});
	}());
</script>