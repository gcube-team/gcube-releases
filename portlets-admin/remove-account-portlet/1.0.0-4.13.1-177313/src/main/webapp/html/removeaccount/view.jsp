<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<portlet:actionURL var="deleteAccountURL" name="deleteAccount" />

<h3>Delete your Account</h3>
<p>Deleting your account will disable your profile and remove your
	name and photo from most things you've shared on this <a href="http://www.d4science.org/" target="_blank">D4Science</a>	gateway.</p>
<p>Some information may still be visible to others, such as your
	name in the posts and private messages you sent. Non shared files and folders of your virtual workspace will be removed.</p>

<div class="alert alert-block">
	<h4>Warning</h4>
	Clicking on "Confirm delete account" below is an undoable operation, your account will be removed and you'll be automatically logged out from this Gateway.
</div>

<form action="<%=deleteAccountURL%>" method="post"
	id="removeAccountForm" name="myForm">
	<button class="btn btn-large" type="button"
		onClick="document.getElementById('removeAccountForm').submit()">Confirm delete
		account</button>
</form>