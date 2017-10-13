<%-- 
    Author     : Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
--%>

<%@page import="gr.forth.ics.isl.textentitymining.Category"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>

<%@ page contentType="text/html;charset=utf-8" %>

<%

    session.setAttribute("query_submitted", "yes");

    String mining_selected = request.getParameter("mining");
    boolean mining_sel;
    if (mining_selected == null) {
        mining_sel = false;
    } else {
        mining_sel = true;
    }

    String clustering_selected = request.getParameter("clustering");
    boolean clustering_sel;
    if (clustering_selected == null) {
        clustering_sel = false;
    } else {
        clustering_sel = true;
    }

%>


<jsp:useBean id="entities" type="gr.forth.ics.isl.xsearch.Bean_Search" scope="session" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="icon" href="files/graphics/favicon.ico" type="image/x-icon" />
        <title><% out.print(entities.getQuery());%> | X-Search - Linking Marine Resources</title>

        <link rel="stylesheet" type="text/css" href="css/main.css" />

        <script type="text/javascript" src="js/mining.js"></script>
        <script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
        <script type="text/javascript" src="js/createCLT.js"></script>

    </head>
    <body>

        <!--
        <div id="bubbleFirst" class="bubble">
            <div class="bubbleHeader">
                <font class="popup_title">Entity Exploration</font>&nbsp;<a class="closePopup" href="javascript:closePopup()">(close)</a>
            </div>
            <div class="bubbleData" id="bubbleFirstData">
                Pop up Data here
            </div>
            <div class="bubbleFooter">
                <a class="closePopup" href="javascript:closePopup()">(close)</a>
            </div>
        </div>
        -->
        <%@ include file="inc/html/info.html" %>

        <table align="center" border="0" cellpadding="0" cellspacing="0">
            <tr valign="top">
                <td colspan="2" valign="top" align="center" style="padding-left:50px;">
                    <form class="basicForm" method="get" name="search" id="search" autocomplete="off" onsubmit="return sure();" action="Servlet_Search">
                        <code autocomplete="off"></code>
                        <table class="mainform">
                            <tr>
                                <td valign="top" style="padding-top: 5px">
                                    <a href="index.jsp">
                                        <img align="center" alt="X-Search - Mining and Clustering" src="files/graphics/xsearch_logo.png" border="0" />
                                        <br />
                                    </a>
                                </td>
                                <td valign="top" style="padding-top: 10px; padding-left:15px;">
                                    <table border="0" cellpadding="0" cellspacing="0">
                                        <tr>
                                            <td>

                                                <input id="suggestion" AUTOCOMPLETE="off" class="gtext_" name="query" value="<% out.print(entities.getQuery());%>" type="text" size="40" maxlength="256" style="height:25px; font-size:18px; font-family: Calibri; padding:3px 3px 3px 3px;" />
                                                <input type="hidden" name="start" value="0"/>
                                            </td>
                                            <td align="left">
                                                <input class="searchbox" id="search_box" value="Search" type="submit" style="height:35px; width: 70px; font-size: 18px; font-family: Calibri;" />
                                                <span id="indicator" style="display:none;"><img border="0" src="files/graphics/indicator.gif" /></span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="left" valign="top" style="padding-top:5px;color: red">
                                                <%@ include file="inc/html/about.html" %>
                                                <!--<span id="message" class="message"><font color="green" class="message_style"><b>Navigate to suggestions with arrow keys &dArr; and &uArr;</b></font></span>-->
                                            </td>
                                            <td rowspan="2" align="left" valign="top" >
                                                <div class="advance_options_main">
                                                    <img border="0" src="files/graphics/plus.gif" id="image_advance" class="advance_image" onclick="display_options();" />
                                                    <div id="advance_options" class="advance_options">   
                                                        <select class="num_option" id="mining_num_option" name="n">
                                                            <option value="10">10</option>
                                                            <option value="20">20</option>
                                                            <option value="30">30</option>
                                                            <option value="40">40</option>
                                                            <option value="50">50</option> 
                                                            <option value="100">100</option>
                                                            <option value="200">200</option>                                                
                                                            <option value="300">300</option>   
                                                            <option value="500">500</option>
                                                        </select>
                                                        <script type="text/javascript">
                                                            document.getElementById("mining_num_option").value = "<%=entities.getMax_num_of_results_from_wse()%>";
                                                        </script>
                                                        <font class="text_advance">results to mine/cluster</font>
                                                        <br />
                                                        <%
                                                            if (entities.isOnly_snippets()) {
                                                        %>
                                                        <input type="radio" name="type" id="onlySnippets" value="onlySnippets" checked /><font class="text_advance">only snippets</font> 
                                                        <br />
                                                        <input type="radio" name="type" id="fullContent" value="fullContent" /><font class="text_advance">full content</font>
                                                        <%                                                        } else {
                                                        %>
                                                        <input type="radio" name="type" id="onlySnippets" value="onlySnippets" /><font class="text_advance">only snippets</font> 
                                                        <br />
                                                        <input type="radio" name="type" id="fullContent" value="fullContent" checked /><font class="text_advance">full content</font>
                                                        <%                                                            }
                                                        %>
                                                        <hr size="1px" style="color: #ccc" noshade="noshade" />
                                                        <%
                                                            if (mining_sel) {
                                                        %>
                                                        <input type="checkbox" id="mining_checkbox" name="mining" onclick="change_mining();" value="true" checked="checked" /><font class="text_advance">mining</font>
                                                        <%                                                        } else {
                                                        %>
                                                        <input type="checkbox" id="mining_checkbox" name="mining" onclick="change_mining();" value="true" /><font class="text_advance">mining</font>
                                                        <%                                                            }
                                                        %>
                                                        <br />
                                                        <%
                                                            if (clustering_sel) {
                                                        %>
                                                        <input type="checkbox" id="clustering_checkbox" name="clustering" onclick="change_clustering();" value="true" checked="checked" /><font class="text_advance">clustering</font>
                                                        <%                                                        } else {
                                                        %>
                                                        <input type="checkbox" id="clustering_checkbox" name="clustering" onclick="change_clustering();" value="true" /><font class="text_advance">clustering</font>
                                                        <%                                                            }
                                                        %>

                                                        <br />        
                                                        <select id="clustering_num_option2" class="num_option" name="clnum">
                                                            <option value="5">5</option>
                                                            <option value="10">10</option>
                                                            <option value="15">15</option>
                                                            <option value="20">20</option>
                                                            <option value="30">30</option>
                                                            <option value="40">40</option>
                                                            <option value="50">50</option>
                                                        </select>
                                                        <script type="text/javascript">
                                                            document.getElementById("clustering_num_option2").value = "<%=entities.getNumOfClusters()%>";
                                                        </script>
                                                        <font class="text_advance">clusters to show</font>&nbsp;&nbsp;&nbsp;&nbsp;<img border="0" src="files/graphics/minus.png" id="image_advance" class="advance_image" onclick="hide_options();" />
                                                    </div>
                                                </div>

                                                <br />
                                                <div align="left" class="clusterTree2" id="clusterLabelTree2">

                                                    <%
                                                        if (entities.isClustering()) {
                                                    %>

                                                    <% out.print(entities.getClustersContent());%>

                                                    <%
                                                        }
                                                    %>



                                                </div>
                                            </td> 
                                        </tr>
                                    </table>
                                    <br />
                                </td>
                            </tr>
                        </table>
                    </form>
                </td>
            </tr>
            <tr valign="top">
                <td valign="top" width="230px">
                    <div align="left" class="clusterTree" id="clusterLabelTree">
                        <%

                            if (entities.isMining()) {
                                if (!entities.getEntities().isEmpty()) {

                        %>
                        <br />
                        <font style="font-size: 17px;">Entities: </font>
                        <br />&nbsp;<br />
                        <%

                                    for (Category cat : entities.getEntities()) {
                                        out.print(cat.getCategory_representation() + "<br />");
                                    }
                                }
                            }
                        %>
                        <br />&nbsp;<br />&nbsp;<br />&nbsp;
                    </div>
                </td>
                <td valign="top"> 
                    <div align="left" id="resultsFirstPage" class="resultsFirstPage">

                        <%
                            if (!entities.getResults_first_page().equals("")) {

                        %>
                        <jsp:getProperty name="entities" property="results_first_page" />

                        <%                        } else {
                                out.print("No results!");
                            }

                        %>    

                        <br />&nbsp;<br />&nbsp;<br />&nbsp;
                    </div> 
                </td>
            </tr>
        </table>

        <script type="text/javascript">            
            if (document.getElementById("clustering_checkbox").checked != true) {
                document.getElementById('clustering_num_option2').disabled = true;
            }
        </script>
        <div id="ios_approaches" style="visibility: hidden"></div>

        <br />&nbsp;<br />&nbsp;<br />
        <script type="text/javascript">
            document.getElementById("suggestion").focus();
            document.getElementById("suggestion").select();
        </script>

    </body>
</html>
