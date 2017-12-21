<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ page import="org.gcube.resources.discovery.icclient.ICFactory.*"%>
<%@ page import="org.gcube.common.resources.gcore.*"%>
<%@ page import="org.gcube.common.scope.api.ScopeProvider"%>
<%@ page import="org.gcube.resources.discovery.client.api.*"%>
<%@ page import="org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager"%>
<%@ page import="org.gcube.common.authorization.library.provider.UserInfo"%>



<%@ page
	import="static org.gcube.resources.discovery.icclient.ICFactory.clientFor"%>
<%@ page
	import="static org.gcube.resources.discovery.icclient.ICFactory.queryFor"%>
	<%@ page
	import="static org.gcube.common.authorization.client.Constants.authorizationService"%>
	
<liferay-theme:defineObjects />
<portlet:defineObjects />

<%
	String SERVICE_NAME = "ShareLatex";
	String SERVICE_CLASS = "DataAccess";
	String ENTRY_NAME = "org.gcube.data.access.sharelatex.connector.Connector";
	long groupId = com.liferay.portal.util.PortalUtil.getScopeGroupId(request);
	String username = com.liferay.portal.util.PortalUtil.getUser(request).getScreenName();
	String scope =  new LiferayGroupManager().getInfrastructureScope(groupId);;
	//set the context for this resource
	ScopeProvider.instance.set(scope);


	
	//construct the xquery
	org.gcube.resources.discovery.client.queries.api.SimpleQuery query = queryFor(GCoreEndpoint.class);
	query.addCondition("$resource/Profile/ServiceName/text() eq '" + SERVICE_NAME + "'");
	query.addCondition("$resource/Profile/ServiceClass/text() eq '" + SERVICE_CLASS + "'");

	org.gcube.resources.discovery.client.api.DiscoveryClient<GCoreEndpoint> client = clientFor(
			GCoreEndpoint.class);
	java.util.List<GCoreEndpoint> conf = client.submit(query);
	if (conf == null || conf.isEmpty()) {
		out.print("No gCore resource named <b>" + SERVICE_NAME + "</b> is present in the scope <b>" + scope + "</b>");
	} else {
		java.util.List<String> userRoles = new java.util.ArrayList<String>();
		String DEFAULT_ROLE = "OrganizationMember";
		userRoles.add(DEFAULT_ROLE);
		String token = authorizationService().generateUserToken(new UserInfo(username, userRoles), scope);		
		
		GCoreEndpoint re2s = conf.get(0);
		java.util.Iterator<org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint> it = re2s.profile().endpoints().iterator();
		String uriService = "";
		while (it.hasNext()) {
			org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint ep = it.next();
			if (ep.name().compareTo(ENTRY_NAME)==0) {
				uriService = ep.uri().toString();
				System.out.println(" ** Found uriService for "+ENTRY_NAME+"="+uriService);
				break;
			}
		}
		String url = uriService+"/connect?gcube-token="+token;
		%>
		<script> window.open("<%=url%>", '_blank'); </script>
		<p class="lead">If no new window appears, please click <a href="<%=url%>" target="_blank">here</a> to open the <a href="<%=url%>" target="_blank">ShareLaTeX Editor</a></p>
		<%
	}
	
	
%>

