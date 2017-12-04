 
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page
	import="org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager"%>
<%@ page
	import="org.gcube.common.authorization.library.provider.UserInfo"%>
<%@ page import="org.gcube.common.portal.PortalContext"%>


<%
	long groupId = com.liferay.portal.util.PortalUtil.getScopeGroupId(request);
	long userId = com.liferay.portal.util.PortalUtil.getUser(request).getUserId();

	String strURL = response.encodeURL(
			request.getContextPath() + "/MessageConversations.html?gid=" + groupId + "&uid=" + userId);
%>

<script type="application/javascript">
function resizeIFrameToFitContent( iFrame ) {
	var dockBarHeight =  (window.innerWidth > 768) ? 90 : 0;
    iFrame.height = window.innerHeight - dockBarHeight;
}

window.addEventListener('DOMContentLoaded', function(e) {
    var iFrame = document.getElementById( 'myIframe' );
    resizeIFrameToFitContent( iFrame );
} );
</script>

<iframe id="myIframe" src='<%=strURL%>' width="100%" marginwidth="0"
	marginheight="0" frameborder="0" scrolling="no" style="	overflow-x: hidden; "> </iframe>

<!-- iFrame resize handler -->
<script type="text/javascript">
	$(document).ready(function() {
		var resizeDelay = 200;
		var doResize = true;
		var resizer = function() {
			if (doResize) {
				var iFrame = document.getElementById('myIframe');
				resizeIFrameToFitContent(iFrame);
				doResize = false;
			}
		};
		var resizerInterval = setInterval(resizer, resizeDelay);
		resizer();

		$(window).resize(function() {
			doResize = true;
		});
	});
</script>

