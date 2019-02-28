<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ page import="org.gcube.resources.discovery.icclient.ICFactory.*"%>
<%@ page import="org.gcube.common.resources.gcore.*"%>
<%@ page import="org.gcube.common.scope.api.ScopeProvider"%>
<%@ page import="org.gcube.resources.discovery.client.api.*"%>
<%@ page import="org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager"%>
<%@ page import="org.gcube.common.authorization.library.provider.UserInfo"%>
<%@ page import="org.apache.http.*" %>
<%@ page import="java.io.*" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="org.json.simple.parser.ParseException" %>



<%@ page
	import="static org.gcube.resources.discovery.icclient.ICFactory.clientFor"%>
<%@ page
	import="static org.gcube.resources.discovery.icclient.ICFactory.queryFor"%>
	<%@ page
	import="static org.gcube.common.authorization.client.Constants.authorizationService"%>
	
<liferay-theme:defineObjects />
<portlet:defineObjects />

<%
	String SERVICE_NAME = "EODAPortalAuth";
	String SERVICE_CATEGORY = "Service";
	String ENTRY_NAME = "AuthorizationEndpoint";
	long groupId = com.liferay.portal.util.PortalUtil.getScopeGroupId(request);
	String username = com.liferay.portal.util.PortalUtil.getUser(request).getScreenName();
	String scope =  new LiferayGroupManager().getInfrastructureScope(groupId);;
	//set the context for this resource
	ScopeProvider.instance.set(scope);


	
	//construct the xquery
	org.gcube.resources.discovery.client.queries.api.SimpleQuery query = queryFor(ServiceEndpoint.class);
	query.addCondition("$resource/Profile/Name/text() eq '" + SERVICE_NAME + "'");
	query.addCondition("$resource/Profile/Category/text() eq '" + SERVICE_CATEGORY + "'");

	org.gcube.resources.discovery.client.api.DiscoveryClient<ServiceEndpoint> client = clientFor(
			ServiceEndpoint.class);
	java.util.List<ServiceEndpoint> conf = client.submit(query);
	if (conf == null || conf.isEmpty()) {
		out.print("No ServiceEndpoint resource named <b>" + SERVICE_NAME + "</b> is present in the scope <b>" + scope + "</b>");
	} else {
		java.util.List<String> userRoles = new java.util.ArrayList<String>();
		String DEFAULT_ROLE = "OrganizationMember";
		userRoles.add(DEFAULT_ROLE);
		String token = authorizationService().generateUserToken(new UserInfo(username, userRoles), scope);		
		
		ServiceEndpoint re2s = conf.get(0);
		java.util.Iterator<org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint> it = re2s.profile().accessPoints().iterator();
		String hostedOn = re2s.profile().runtime().hostedOn();
		String context = "";
		String tokenServiceEndpoint = "";
		while (it.hasNext()) {
			org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint ep = it.next();
			if (ep.name().compareTo(ENTRY_NAME)==0) {
				tokenServiceEndpoint = ep.address();
				for (ServiceEndpoint.Property prop : ep.properties()) {
					if (prop.name().compareTo("context") == 0) {
						context = org.gcube.common.encryption.StringEncrypter.getEncrypter().decrypt(prop.value());
					}		
				}				
				System.out.println(" ** Found context for "+ENTRY_NAME+"="+context);
				break;
			}
		}
		String tokenService = tokenServiceEndpoint+context;
		String url = hostedOn+getCLSToken(tokenService);
		%>
		
		<iframe src="<%=url%>" width="100%" height="1200" frameborder="0" marginheight="0" scrolling="no"></iframe>
		<%-- 		<script> window.open("<%=url%>", '_blank'); </script> --%>
<%-- 		<p class="lead">If no new window appears, please click <a href="<%=url%>" target="_blank">here</a> to open the <a href="<%=url%>" target="_blank">EODA Portal</a></p> --%>
		<%
	}
	
	
%>

<%!
protected String getCLSToken(String URL) {
 	String toReturn = "";
	org.apache.http.client.HttpClient client = new org.apache.http.impl.client.DefaultHttpClient();
    org.apache.http.client.methods.HttpGet request = new org.apache.http.client.methods.HttpGet(URL);
    HttpResponse response;
    String result = null;
    try {
        response = client.execute(request);         
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            java.io.InputStream instream = entity.getContent();
            result = convertStreamToString(instream); 
            instream.close();
         

            if (response.getStatusLine().getStatusCode() == 200) {
            	  	JSONParser jsonParser = new JSONParser();
       				JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
       				toReturn = (String) jsonObject.get("token");
       				return toReturn;

            } else
            	return "Some error occurred while trying to contact " + URL + " reason="+ response.getStatusLine().getStatusCode();

        }
        
    } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }
    return result;
}

private static String convertStreamToString(InputStream is) {

    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;
    try {
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    return sb.toString();
}

%>

