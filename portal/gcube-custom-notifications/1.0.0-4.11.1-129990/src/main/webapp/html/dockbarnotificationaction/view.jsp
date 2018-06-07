<%@ include file="init.jsp"%>
<portlet:actionURL var="sendUserNotification" windowState="normal"
	name="sendUserNotification">
</portlet:actionURL>
<form action="<%=sendUserNotification%>" name="userNotificationForm"
	method="POST">
	<h4>Send User Notification</h4>
	<b>Notification Text</b><br />
	<textarea rows="4" cols="100"
		name="<portlet:namespace/>notificationText" style="width: 300px">
</textarea>
	<br /> <input type="submit" name="sendNotification"
		id="sendNotification" value="Notify" />
</form>