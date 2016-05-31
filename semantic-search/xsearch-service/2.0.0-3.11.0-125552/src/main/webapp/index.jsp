<%-- 
    Author     : Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
--%>

<%@page import="java.net.URL"%>
<%@ page contentType="text/html;charset=utf-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="icon" href="files/graphics/favicon.ico" type="image/x-icon" />
        <title>X-Search | Linking Marine Resources</title>

        <link rel="stylesheet" type="text/css" href="css/main.css" />
        <script type="text/javascript" src="js/mining.js"></script>

    </head>
    <body>

        <div style="position: fixed;font-size: 11px;color:gray;padding-left:4px;">
            Please use the latest version 
            <br /> 
            of 
            <a target="_blank" href="http://www.mozilla.org/en-US/firefox/new/">Firefox</a>, 
            <a target="_blank" href="https://www.google.com/chrome">Chrome</a> or 
            <a target="_blank" href="http://windows.microsoft.com/en-US/internet-explorer/products/ie/home?WT.mc_id=MSCOM_EN_US_DLC_FAMILIES_121LMUS007473">IE</a>.
        </div>

        <div style="position: fixed; right: 5px; font-size: 11px;color:gray;padding-left:4px; z-index: 1">
            <%
                String path = this.getServletContext().getContextPath();
                URL reconstructedURL = new URL(request.getScheme(),
                        request.getServerName(),
                        request.getServerPort(),
                        path);
                path = reconstructedURL.toString() + "/PageMining";
            %>
            Use our bookmarklet to <br />find the entities in a  page <br />while browsing. 
            Drag <br />and drop this 
            <a title="Entity Mining Bookmarklet" onclick="alert('Use this bookmarklet to identify and explore entities in a web page while browsing. Drag and drop this bookmarklet in your bookmarks, then visit a web page and click the bookmark (PDF web pages are also supported).');return false;" href="javascript:(function()%20{window.location='<%=path%>?how=2&url='+window.location.toString()})()" target="_blank">link</a> 
            in <br />your bookmarks!
        </div>

        <%@ include file="inc/html/info.html" %>

        <table style="z-index: 99;" align="center" border="0" cellpadding="0" cellspacing="0">
            <tr valign="top">
                <td colspan="2" align="center" valign="top" style="padding-left:50px;">
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

                                                <input id="suggestion" AUTOCOMPLETE="off" class="gtext_" name="query" type="text" size="40" maxlength="256" style="height:25px; font-size:18px; font-family: Calibri; padding:3px 3px 3px 3px;" />

                                            </td>
                                            <td align="left">
                                                <input class="searchbox" id="search_box" value="Search" type="submit" style="height:35px; width: 70px; font-size: 18px; font-family: Calibri;" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="left" valign="top" style="padding-top:5px;color: red">
                                                <%@ include file="inc/html/about.html" %>
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
                                                            <option value="50" selected="selected">50</option>
                                                            <option value="100">100</option>
                                                            <option value="200">200</option>
                                                            <option value="300">300</option>
                                                            <option value="400">400</option>
                                                            <option value="500">500</option>
                                                        </select>
                                                        <font class="text_advance">results to mine/cluster</font>
                                                        <br />
                                                        <input type="radio" name="type" id="onlySnippets" value="onlySnippets" checked /><font class="text_advance">only snippets</font> 
                                                        <br />
                                                        <input type="radio" name="type" id="fullContent" value="fullContent" /><font class="text_advance">full content</font>
                                                        <hr size="1px" style="color: #ccc" noshade="noshade" />
                                                        <input type="checkbox" id="mining_checkbox" name="mining" onclick="change_mining();" value="true" checked="checked" /><font class="text_advance">mining</font>
                                                        <br />
                                                        <input type="checkbox" id="clustering_checkbox" name="clustering" onclick="change_clustering();" value="true" checked="checked" /><font class="text_advance">clustering</font>
                                                        <br />
                                                        <select id="clustering_num_option2" class="num_option" name="clnum">
                                                            <option value="5">5</option>
                                                            <option value="10">10</option>
                                                            <option value="15" selected="selected">15</option>
                                                            <option value="20">20</option>
                                                            <option value="30">30</option>
                                                            <option value="40">40</option>
                                                            <option value="50">50</option>
                                                        </select>
                                                        <font class="text_advance">clusters to show</font>&nbsp;&nbsp;&nbsp;&nbsp;<img src="files/graphics/minus.png" border="0" id="image_advance" class="advance_image" onclick="hide_options();" />
                                                    </div>
                                                </div>
                                                <br />
                                                <div align="left" class="clusterTree2" id="clusterLabelTree2"></div>
                                            </td> 
                                        </tr>
                                        <tr>
                                            <td colspan="2" align="left" style="padding-top:20px;">

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
            <tr align="center" id="inforow" valign="top">
                <td colspan="2" align="center" valign="top">  
                    <div class="logos">
                        <a target="_blank" href="http://www.ics.forth.gr/isl/" title="FORTH-ICS-ISL">
                            <img border="0" width="120px" height="130px" src="files/graphics/forth_logo.jpg" />
                        </a>
                        <br />
                        <a target="_blank" href="http://www.csd.uoc.gr/en/" title="UOC-CSD">
                            <img border="0" width="140px" height="135px" src="files/graphics/uoc_logo.png" />
                        </a>
                    </div>
                    <div id="resultsFirstPage" style="display: none;"></div>
                    <div id="clusterLabelTree" style="display: none;"></div>
                </td>
            </tr>
            <tr valign="top">
                <td valign="top" width="230px">

                </td>
            </tr>
        </table>

        <br />&nbsp;<br />&nbsp;<br />

    </body>
</html>
