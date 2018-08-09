<%@page import="org.gcube.data.simulfishgrowthdata.model.verify.EntityVerify"%>
<%@page import="org.gcube.data.simulfishgrowthdata.util.UserFriendlyException"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%
	Log logger = LogFactoryUtil
			.getLog("org.gcube.portlets.user.simulfishgrowth.portlet.jsp." + this.getClass().getSimpleName());
%>
<%
	Exception reason = (Exception)request.getAttribute("errorReason");
	logger.error("error page triggered", reason);
	if (reason==null || !(reason instanceof UserFriendlyException)) {
		reason = new UserFriendlyException("Something unexpected happened.", reason);
	}
	java.util.List<String> cause = null;
	cause = UserFriendlyException.getFriendlyTraceFrom(reason.getCause());
%>

<div class="i2s-error-page-container">
		<p><button class="btn i2s-btn i2s-info-btn" onclick="history.back()" style="border:0px;"> 
			<i class="icon-arrow-left" style="font-size:22px;"></i>
			&nbsp;
			Back
		</button></p>
	
	<p class="i2s-m-error-msg"> <i class="icon-warning-sign"></i> <%=reason.getMessage() %> </p>  
	<br/>
	<p>
<p>
		
</p>
<br/>
<a  style="border:0px;color:#0271be !important;cursor:pointer;" onclick="toggle()">More info &nbsp;
<i id="expandIcon" class="icon-expand-alt"></i>
<i id="collapseIcon" style="display: none;"class="icon-collapse-alt"></i></a>
</div>
<div id="hiddenLogMsg" class="i2s-error-details-msg i2s-error-page-container " style="margin-top:20px;">
<% if( com.google.common.base.Joiner.on("<br/>").skipNulls().join(cause).length()  == 0) { 
%>	 
	<p> No extra information for the error is available</p>
<% }  else { %>
	<p><%=com.google.common.base.Joiner.on("<br/>").skipNulls().join(cause) %></p>
<% } %>
</div>
<script>
function toggle(){
var hiddenLog = $("#hiddenLogMsg");
var expIcon = $("#expandIcon");
var collapseIcon = $("#collapseIcon");
if(hiddenLog.hasClass("i2s-error-details-msg-show"))
	{
	collapseIcon.hide();
	expIcon.show();
	hiddenLog.removeClass("i2s-error-details-msg-show");
	}
else{
	expIcon.hide();
	collapseIcon.show();
	hiddenLog.addClass("i2s-error-details-msg-show");
	
}
}
</script>