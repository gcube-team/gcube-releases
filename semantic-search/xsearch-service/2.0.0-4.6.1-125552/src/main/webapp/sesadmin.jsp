<%-- 
    Author     : Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
--%>

<%@page import="java.util.HashMap"%>
<%@page import="java.util.HashSet"%>
<%@page import="gr.forth.ics.isl.xsearch.admin.SessionCounter"%>
<%@page import="gr.forth.ics.isl.xsearch.util.HTMLTag"%>
<%@page import="gr.forth.ics.isl.xsearch.resources.Resources"%>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html;charset=utf-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="icon" href="files/graphics/favicon.ico" type="image/x-icon" />
        <title>X-Search | Linking Marine Resources - Session-based Configuration Page</title>


        <link rel="stylesheet" type="text/css" href="css/admin.css" />
        <script type="text/javascript" src="js/admin.js"></script>

    </head>
    <body>

        <%@ include file="inc/html/info.html" %>

        <div class="headerContainer">
            <div class="header">

                <div class="logo">
                    <a title="X-Search - Home Page" href="./">
                        <img border="0" src="files/graphics/xsearch_logo.png" />
                    </a>
                </div>
                <div class="menu">
                    <div class="nav">
                        <span class="adminTitle">Session-based Configuration Page</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="contentContainer">
            <div class="content">

                <!-- Reset session parameters -->
                <div class="property">
                    <span class="propertyName">
                        Reset Session: 
                        <font color="red">
                            <a title="Reset Session" href="javascript:resetConfiguration()" style="color:red">reset</a>
                        </font>
                    </span>
                </div>

                <%
                    String descrDoc = (String) session.getAttribute("descrDoc");
                    if (descrDoc == null) {
                        descrDoc = Resources.DESCRIPTIONDOCUMENT;
                    }
                %>

                <!-- OPEN SEARCH DESCRIPTION DOCUMENT -->
                <div class="property">
                    <span class="propertyName">OpenSearch Description Document <font style="font-size:11px;">(or search system)</font>: </span>
                    <input id="descriptionDocument" class="propertyValue" type="text" name="descriptionDocument" value="<%=descrDoc%>" disabled="disabled" />
                    <input type="hidden" id="initialDescrDoc" name="initialDescrDoc" value="<%=descrDoc%>" />
                    <img border="0" id="descriptionDocumentImg" src="files/graphics/indicator.gif" style="display: none" />
                    <span class="editinput" id="editDescriptionDocument"><a href="javascript:allowInputEditing('descriptionDocument', 'editDescriptionDocument', 'changeDescriptionDocument', 'filecontents')">edit</a></span>
                    <span class="editinput" id="filecontents">|&nbsp;<a href="ShowDescriptionDocument" target="_blank">file</a></span>
                    <span class="changeinput" id="changeDescriptionDocument" style="display:none"><a href="javascript:changeDescriptionDocument('y')">change</a></span>
                </div>

                <div class="errormessage" id="errormessage">

                </div>


                <!-- MINE QUERY  -->
                <%
                    boolean mineQuery = Resources.MINE_QUERY;
                    String mineQ = (String) session.getAttribute("mineQuery");
                    if (mineQ != null) {
                        mineQuery = Boolean.parseBoolean(mineQ);
                    }
                %>
                <div class="property">
                    <span class="propertyName">Mine Query: </span>
                    <select class="propertyValueSmall" id="mineQuery" name="mineQuery" disabled="disabled">
                        <option value="yes">yes</option>
                        <option value="no">no</option>
                    </select>
                    <img border="0" id="mineQueryImg" src="files/graphics/indicator.gif" style="display: none" />
                    <span class="editinput" id="editMinequery"><a href="javascript:allowSelectEditing('mineQuery', 'editMinequery', 'changeMinequery')">edit</a></span>
                    <span class="changeinput" id="changeMinequery" style="display:none"><a href="javascript:changeMinequery('y')">change</a></span>
                </div>

                <script type="text/javascript">
                    <% if (mineQuery) {%>
                        document.getElementById("mineQuery").value = "yes";
                    <% } else {%>
                        document.getElementById("mineQuery").value = "no";
                    <% }%>
                </script>


                <!-- CLUSTERING ALGORITHM -->
                <%

                    int clusteringAlgorithm = Resources.CLUSTERING_ALGORITHM;
                    String clustAlg = (String) session.getAttribute("clustAlg");
                    if (clustAlg != null) {
                        clusteringAlgorithm = Integer.parseInt(clustAlg);
                    }
                %>
                <div class="property">
                    <span class="propertyName">Clustering algorithm: </span>
                    <select class="propertyValueSmall" id="clusteringAlgorithm" name="clusteringAlgorithm" disabled="disabled">
                        <option value="1">STC</option>
                        <option value="2">STC+</option>
                        <option value="3">NM-STC</option>
                        <option value="4">STC++</option>
                        <option value="5">NM-STC+</option>
                    </select>
                    <img border="0" id="clusteringAlgorithmImg" src="files/graphics/indicator.gif" style="display: none" />
                    <span class="editinput" id="editClusteringAlgorithm"><a href="javascript:allowSelectEditing('clusteringAlgorithm', 'editClusteringAlgorithm', 'changeClusteringAlgorithm')">edit</a></span>
                    <span class="changeinput" id="changeClusteringAlgorithm" style="display:none"><a href="javascript:changeClusteringAlgorithm('y')">change</a></span>
                </div>


                <script type="text/javascript">
                    document.getElementById("clusteringAlgorithm").value = "<%=clusteringAlgorithm%>";
                </script>


                <!-- ACCEPTED CATEGORIES -->
                <%
                    HashSet<String> acceptedCategories = (HashSet<String>) session.getAttribute("acceptedCategories");
                    if (acceptedCategories == null) {
                        acceptedCategories = new HashSet<String>();
                        acceptedCategories.addAll(Resources.MINING_ACCEPTED_CATEGORIES);
                    }
                %>
                <div class="property">
                    <span class="propertyName">Accepted categories: </span>
                    <br />
                    <ul id="acceptedCategoriesList">
                        <%
                            for (String oneCategory : acceptedCategories) {
                        %>     
                        <li class="categorylist" id="category<%=oneCategory%>"><%=oneCategory%> <span class="editinput"><a href="javascript:removeAcceptedCategory('<%=oneCategory%>','y')">remove</a></span></li>
                        <%
                            }
                        %>
                    </ul>

                    <span class="categoryName">Add accepted category:</span>&nbsp;

                    <select id="addCategory" name="addCategory">
                        <%
                            for (String onePossibleCategory : Resources.MINING_ALL_POSSIBLE_CATEGORIES) {
                                if (!acceptedCategories.contains(onePossibleCategory)) {
                        %>     
                        <option id="pcategory<%=onePossibleCategory%>" value="<%=onePossibleCategory%>"><%=onePossibleCategory%></option> 
                        <%
                                }
                            }
                        %>
                    </select>
                    <input type="button" name="addcategory" value="Add" onclick="addAcceptedCategory('y');" />
                </div>

                <%
                    String sampleQuery = ""
                            + "PREFIX ecosystems_def: <http://www.ecoscope.org/ontologies/ecosystems_def#>\n"
                            + "SELECT DISTINCT ?Harbour\n"
                            + "WHERE {\n"
                            + "  ?URI  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.ecoscope.org/ontologies/ecosystems_def#harbour> .\n"
                            + "  ?URI  <http://www.w3.org/2004/02/skos/core#prefLabel> ?Harbour\n"
                            + "}";

                %>
                <!-- ADD NEW CATEGORY  -->
                <div class="property">
                    <span class="propertyName">Add a new category: </span>
                    <br />

                    <div class="addNewCategory">
                        <br />
                        Category name: 
                        <input id="newCategoryName" class="newCategoryName" type="text" name="newCategoryName" />
                        <input type="button" id="addNewCategoryButton" name="addNewCategoryButton" value="Continue" onclick="contAddNewCategory();" /> 

                        <div id="addNewCategoryInputs" class="sparqlqueryaddcategory">
                            List of words/phrases (a word or phrase per line): <br />
                            <textarea id="newCategoryList" class="textarealist" name="newCategoryList"></textarea>     
                            <br />

                            <a href="javascript:runAquery()">Load a list by running a SPARQL Query</a>
                            <br />
                            <div class="runaqueryinputs" id="runaqueryinputs">
                                SPARQL endpoint: 
                                <input id="newCategoryEndpoint" class="propertyValue" type="text" name="newCategoryEndpoint" value="http://ecoscopebc.mpl.ird.fr/joseki/ecoscope?query=" /> <br />
                                SPARQL query: <br />
                                <textarea id="newCategoryQuery" class="textarea" name="newCategoryQuery"><%=sampleQuery%></textarea>  
                                <br />
                                <input type="button" name="loadList" value="Load" style="width: 100px;" onclick="loadList();" />
                                <img border="0" id="loadCategoryImage" src="files/graphics/indicator.gif" style="display: none" /> 
                                <span class="lineerrormessage" id="loadCategoryQueryErrorMsg"></span>
                            </div>
                            <img border="0" id="newCategoryQueryImage" src="files/graphics/indicator.gif" style="display: none" /> 
                            <span class="lineerrormessage" id="newCategoryQueryErrorMsg"></span>
                            <br />
                            <input type="button" name="addnewcategory" value="Add" style="width: 100px;" onclick="addNewCategory();" />
                        </div>

                    </div>


                </div>


                <!-- ENTITY ENRICHMENT - SPARQL ENDPOINTS AND TEMPLATE QUERIES -->
                <%
                    HashMap<String, String> endpoints = (HashMap<String, String>) session.getAttribute("endpoints");
                    if (endpoints == null) {
                        endpoints = new HashMap<String, String>();
                        endpoints.putAll(Resources.SPARQL_ENDPOINTS);
                    }

                    HashMap<String, String> templateQueries = (HashMap<String, String>) session.getAttribute("templateQueries");
                    if (templateQueries == null) {
                        templateQueries = new HashMap<String, String>();
                        templateQueries.putAll(Resources.SPARQL_TEMPLATES);
                    }
                %>
                <div class="property">
                    <span class="propertyName">Entity enrichment: </span>
                    <br />
                    <ul id="sparqlEndpointList">
                        <%
                            for (String category : endpoints.keySet()) {
                        %>     
                        <li class="categorylistMorePadding" id="endpointof<%=category%>">
                            <div class="categentenr">
                                <span class="categtitle"> <%=category%></span>
                                <span class="editinput"><a onclick="return removeSure();" href="javascript:removeEntityEnrichment('<%=category%>','y')">remove</a></span>
                            </div>

                            <div class="sparqlendpoint">
                                SPARQL endpoint: 
                                <input id="endpointValueOf<%=category%>" class="propertyValue" type="text" name="endpointvalueof<%=category%>" value="<%=endpoints.get(category)%>" disabled="disabled" />
                                <input type="hidden" id="initialEndpointOf<%=category%>" name="initialEndpointOf<%=category%>" value="<%=endpoints.get(category)%>" />
                                <img border="0" id="endpointImgOf<%=category%>" src="files/graphics/indicator.gif" style="display: none" />
                                <span class="editinput" id="enpointEditOf<%=category%>"><a href="javascript:allowInputEditing('endpointValueOf<%=category%>', 'enpointEditOf<%=category%>', 'endpointChangeOf<%=category%>', 'endpointLinkOf<%=category%>')">edit</a></span>
                                <span class="editinput" id="endpointLinkOf<%=category%>">|&nbsp;<a href="<%=endpoints.get(category)%>" target="_blank">open</a></span>
                                <span class="changeinput" id="endpointChangeOf<%=category%>" style="display:none"><a href="javascript:changeEndpoint('<%=category%>')">change</a></span>
                                <% if (Resources.SPARQL_ENPOINTS_USERNAMES.containsKey(endpoints.get(category)) && Resources.SPARQL_ENPOINTS_PASSWORDS.containsKey(endpoints.get(category))) { %>
                                &nbsp;&nbsp;<span style="font-size:13px;font-family: Calibri;font-style: italic">(requires Authentication)</span>
                                <% } %>
                                <span class="lineerrormessage" id="endpointErrorMessageOf<%=category%>"></span>
                            </div>
                            <div class="sparqltemplatequery">
                                SPARQL template query: <br />
                                <textarea id="templateValueOf<%=category%>" class="textarea" name="templatevalueof<%=category%>" disabled="disabled"><%=HTMLTag.readFile(templateQueries.get(category))%></textarea>
                                <input type="hidden" id="initialTemplateOf<%=category%>" name="initialTemplateOf<%=category%>" value="<%=HTMLTag.readFile(templateQueries.get(category))%>" />
                                <img border="0" id="templateImgOf<%=category%>" src="files/graphics/indicator.gif" style="display: none" />
                                <span class="editinput" id="templateEditOf<%=category%>"><a href="javascript:allowTextAreaEditing('templateValueOf<%=category%>', 'templateEditOf<%=category%>', 'templateChangeOf<%=category%>')">edit</a></span>
                                <span class="changeinput" id="templateChangeOf<%=category%>" style="display:none"><a href="javascript:changeTemplate('<%=category%>')">change</a></span>
                                <span class="lineerrormessage" id="templateErrorMessageOf<%=category%>"></span>
                            </div>
                        </li>
                        <%
                            }
                        %>
                    </ul> 

                    <span class="categoryName">Add LOD entity enrichment for the category:</span>&nbsp;

                    <select id="addLODenrichmentCategory" name="addEntityEnrichment">
                        <%
                            for (String cat : acceptedCategories) {
                                if (!templateQueries.containsKey(cat)) {
                        %>     
                        <option value="<%=cat%>"><%=cat%></option> 
                        <%
                                }
                            }
                        %>
                    </select>
                    <input type="button" id="addContinueButton" name="addContinueButton" value="Continue" onclick="contEntityEnrichment();" />

                    <div id="addEEendpoint" class="addEE" style="display: none">
                        SPARQL Endpoint:  <input id="addLODenrichmentEndpoint" class="propertyValue" type="text" name="addLODenrichmentInput" />
                    </div>

                    <div id="addEEtemplate" class="addEE" style="display: none">
                        SPARQL template query:
                        <textarea id="addLODenrichmentTemplate" class="textarea" name="addLODenrichmentTemplate"></textarea>
                    </div>
                    <input type="button" class="addCategoryButton" id="addCategoryButton" name="addCategoryButton" value="Add" style="display: none" onclick="addEntityEnrichment('y');" />
                    <img border="0" id="addEEimg" src="files/graphics/indicator.gif" style="display: none" />
                    <span class="lineerrormessage" id="addEEerrorMessage"></span>
                </div>


                <!-- My X-Search configuration -->
                <div class="property">
                    <span class="propertyName">My X-Search configuration: </span>
                    <ul>
                        <li class="categorylistMorePadding">
                            <a href="javascript:saveConfiguration('y');">Store my configuration</a>
                            <img border="0" id="storeImg" src="files/graphics/indicator.gif" style="display: none" />
                            <span class="storingSpanText" id="storingSpanText"></span>
                        </li>
                        <li class="categorylistMorePadding">
                            Load my configuration: 
                            <input type="text" name="configurationID" id="configurationID" />
                            <input type="button" name="Load" value="Load" onclick="loadConfiguration('y')" />
                            <img border="0" id="loadImg" src="files/graphics/indicator.gif" style="display: none" />
                            <span class="errorLoadingMyConf" id="errorLoadingMyConf"></span>
                        </li>
                    </ul>

                </div>

            </div>
        </div>



        <div class="footer">
            &nbsp;
        </div>




    </body>
</html>
