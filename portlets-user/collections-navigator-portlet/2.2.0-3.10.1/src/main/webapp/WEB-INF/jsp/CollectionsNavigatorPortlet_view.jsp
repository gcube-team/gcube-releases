<%@ page contentType="text/html" %>
<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="org.gcube.application.framework.core.session.*"%>
<%@page import="org.gcube.application.framework.search.library.model.Query"%>
<%@page import="org.gcube.application.framework.search.library.model.*"%>
<%@page import="org.gcube.application.framework.search.library.impl.SearchHelper"%>
<%@page import="org.gcube.application.framework.search.library.model.CollectionInfo"%>
<%@page import="org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper"%>

<!--                                           -->
<!-- The module reference below is the link    -->
<!-- between html and your Web Toolkit module  -->		
<!--                                           -->

<script src="<%=request.getContextPath()%>/collectionsnavigatorportletg/pagebus.js" language="JavaScript1.2"></script>

<script type="text/javascript">
	if(window.parent.PageBus) {
		window.PageBus = window.parent.PageBus;
	}
</script>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/collectionsnavigatorportletg/collectionsnavigatorportletg.nocache.js"></script>

<div id="collectionsnavigatorExternal"></div>

<div style="display:none;" id="sessionID"><%= session.getId() %></div>
<div style="display:none;" id="collectionActionURL"><portlet:actionURL></portlet:actionURL></div>
<div id=""></div>