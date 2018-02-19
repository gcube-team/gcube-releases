
<%@page import="gr.forth.ics.isl.xsearch.resources.Resources"%>
<%@page import="gr.forth.ics.isl.xsearch.IOSLog"%>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html;charset=utf-8" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="icon" href="files/graphics/favicon.ico" type="image/x-icon" />
        <title>X-Search | About</title>


        <link rel="stylesheet" type="text/css" href="css/about.css" />

    </head>
    <body>

        <%

            String ip = request.getRemoteAddr();
            String date = IOSLog.getCurrentDate();

            String line = "\n" + Resources.SYSTEMNAME + "\t" + date + "\t" + ip + "\tOPENING THE ABOUT PAGE!";
            IOSLog.writeToLog(line);
        %>

        <div class="headerContainer">
            <div class="header">

                <div class="logo">
                    <a title="Instant Overview Search - Home Page" href="./">
                        <img border="0" src="files/graphics/xsearch_logo.png" />
                    </a>
                </div>
                <div class="menu">
                    <div class="nav">
                        <ul class="menuitems">
                            <li class="menuitem">
                                <a href="#whatis">What is X-Search?</a>
                            </li>
                            <li class="menuitem">
                                <a href="#prototypes">Prototypes/Applications</a>
                            </li>
                            <li class="menuitem">
                                <a href="#publications">Publications</a>
                            </li>
                            <li class="menuitem">
                                <a href="#contact">Contact</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div class="contentContainer">


            <div class="content">


                <div class="infocontent" id="whatis">
                    <font class="info_title">
                        &raquo;&nbsp;What is <span class="ios">X-Search</span>?
                    </font>
                    <hr width="100%" size="0.01em" align="left" style="color:#ccc" noshade="noshade" />
                    <font class="info_text">
                        X-Search is a meta-search engine that 
                        reads the description of an underlying search source (<a target="_blank" href="http://www.opensearch.org/" title="OpenSearch">OpenSearch</a> compatible), 
                        queries that source, analyzes the returned results in various ways 
                        and also exploits the availability of semantic repositories. 
                        Specifically, it offers:
                        <ul>
                            <li>
                                <b>Textual clustering of the results.</b> 
                                Clustering is performed on the textual snippets of the returned results. 
                                Clustering of the textual contents is also supported. 
                                Furthermore, a ranking on the identified clusters is performed. 
                            </li>
                            <li>
                                <b>Textual entity mining of the results.</b> 
                                Text entity mining can be performed either over the textual snippets or 
                                over the entire contents. It also supports ranking of the identified entities.
                            </li>
                            <li>
                                <b>Metadata-based grouping of the results.</b> 
                                The results can be grouped according to their static metadata values
                                (in case the search system provides metadata for each result).
                                It also supports ranking of the metadata values. 
                            </li>
                            <li>
                                <b>Faceted search-like exploration of the results.</b> 
                                The results of clustering, entity mining and metadata-based grouping are visualized and exploited according to 
                                the faceted exploration interaction paradigm: 
                                when the user clicks on a cluster or entity, 
                                the results are restricted to those that contain that cluster or entity. 
                            </li>
                            <li>
                                <b>On-click semantic exploration of a Knowledge Base.</b>
                                X-Search provides the necessary linkage between the mined entities and semantic information. 
                                In particular, by exploiting appropriate Knowledge Bases 
                                (i.e. <a href="http://dbpedia.org/" target="_blank">DBPedia</a>, <a href="http://www.fao.org/figis/flod/" target="_blank">FAO FLOD</a>, <a href="http://www.ecoscopebc.ird.fr/" target="_blank">Ecoscope Knowledge Base</a>, <a href="http://www.ics.forth.gr/isl/MarineTLO/#applications" target="_blank">MarineTLO-based Warehouse</a>, etc.) 
                                the user can retrieve more information about an entity by querying and browsing over these 
                                Knowledge Bases. 
                            </li>
                            <li>
                                <b>On-demand semantic analysis of individual hits.</b>
                                User is able to perform entity mining at the contents of a hit on-demand, by
                                clicking the "find its entities" hyperlink. In that case, the contents
                                of the particular document are downloaded and entity mining is performed.
                                The discovered entities are grouped according to the category they belong to. 
                                Then the user is able to investigate a particular entity by inspecting where
                                in the document the entity was found or semantically explore its properties.
                            </li>
                            <li>
                                <b>Entity discovery and exploration during plain Web browsing.</b>
                                X-Search also offers entity discovery and exploration while the user is browsing
                                on the Web.
                                Specifically, the user is able to inspect the entities of a particular
                                Web page by clicking a <em><a href="http://en.wikipedia.org/wiki/Bookmarklet" target="_blank">bookmarklet</a></em> (a special bookmark)
                                and then to further retrieve  more information about an entity by 
                                querying a Knowledge Base. 
                                Namely, the user can  exploit the proposed functionality (at real-time) while
                                browsing.
                            </li>
                        </ul>
                    </font>
                </div>

                <div class="gotop">
                    <span class="gotoptext"><a href="javascript:scroll(0,0);">top</a></span>
                </div>

                <div class="infocontent" id="prototypes">
                    <font class="info_title">
                        &raquo;&nbsp;Prototypes/applications
                    </font>
                    <hr width="100%" size="0.01em" align="left" style="color:#ccc" noshade="noshade" />
                    <font class="info_text">
                        
                        <ul>
                            <li>
                                <b>X-Search for the marine domain (over <u>Bing</u> and the <u>MarineTLO-based Warehouse</u>):</b> <a href="Servlet_OpenURI?uri=http://139.91.183.72/x-search/" target="_blank" title="X-Search over Bing">http://139.91.183.72/x-search/</a><br />
                                This prototype runs on top of <a href="http://www.bing.com/" target="_blank">Bing</a> Web search engine and analyzes the snippets of the top-K results (the default value of K is 50). 
                                In order to provide the linkage with semantic resources it exploits the <a href="http://www.ics.forth.gr/isl/MarineTLO/#applications" target="_blank">MarineTLO-based Warehouse</a> (through its <a href="http://62.217.127.213:8890/sparql" target="_blank">SPARQL endpoint</a>). 
                                It also supports the analysis of more results (i.e. top 100, 200), as well as the analysis over the whole contents of the results (rather than just the snippets) upon user request. 
                                It is fully configurable in terms of the underlying Web search engine (OpenSearch compatible) or the Knowledge Bases that are used, the categories of the mined entities, the clustering algorithm, etc. 
                            </li>
                            <li>
                                <b>X-Search for the marine domain (over <u>Ecoscope</u> and the <u>MarineTLO-based Warehouse</u>):</b> <a href="Servlet_OpenURI?uri=http://139.91.183.72/x-search-fao/" target="_blank" title="X-Search over Bing">http://139.91.183.72/x-search-fao/</a><br />
                                This prototype uses <a href="http://www.ecoscopebc.ird.fr/" target="_blank">Ecoscope</a> as the underlying search system (a Knowledge Base about marine ecosystems).
                                For supporting entity enrichment and exploration, the  <a href="http://www.ics.forth.gr/isl/MarineTLO/#applications" target="_blank">MarineTLO-based Warehouse</a>  is queried.
                            </li>
                             <li>
                                <b>X-Search for the marine domain (within <u>gCube e-Infrastructure</u>):</b> <a href="https://i-marine.d4science.org" target="_blank" title="gCube - iMarine">https://i-marine.d4science.org</a> (restricted access)<br />
                                In the context of the <a href="http://www.i-marine.eu/" target="_blank">iMarine Project</a>, 
                                an X-Search portlet has been developed for offering advanced search services within gCube e-Infrastructure.
                                The X-Search portlet communicates with a corresponding X-Search service, 
                                searches several marine resources (registered in the infrastructure),
                                and offers entity mining and textual clustering over the top search results.
                                Again, the  <a href="http://www.ics.forth.gr/isl/MarineTLO/#applications" target="_blank">MarineTLO-based Warehouse</a> 
                                is exploited for enriching and exploring the identified entities.
                           </li>
                            <li>
                                <b>X-Search for Patent Search (over <u>Clef-IP 2011</u> and <u>DBpedia</u>):</b> <a href="http://139.91.183.72/x-search-metadata-groupings/" target="_blank" title="X-Search for Patent Search">http://139.91.183.72/x-search-metadata-groupings/</a><br />
                                This prototype searches over the <a href="http://www.ifs.tuwien.ac.at/~clef-ip/download-central.shtml" target="_blank">Clef-IP 2011</a> dataset of patents and
                                exploits <a href="http://dbpedia.org/" target="_blank">DBPedia</a> for enriching and exploring the identified entities.
                           </li>
                            <li>
                                <b>Bookmarklet:</b>  
                                The functionality of X-Search can be applied in any Web page (PDF files are also supported). 
                                In particular, the user can trigger the bookmarklet as he is viewing a Web page. 
                                The bookmarklet sends the URL of the Web page to X-Search, 
                                X-Search reads the contents of the Web page, performs entity mining and 
                                returns to the user the same Web page with the mined entities annotated. 
                                Furthermore, the user is able to get more information about the identified entities by exploiting the corresponding 
                                Knowledge Bases. 
                                The bookmarklet can be added to the user’s Web browser (the bookmarklet link can be found in the upper right corner of the home page of each prototype). 
                            </li>
                        </ul>
                    </font>
                </div>

                <div class="gotop">
                    <span class="gotoptext"><a href="javascript:scroll(0,0);">top</a></span>
                </div>

                <div class="infocontent" id="publications">
                    <font class="info_title">
                        &raquo;&nbsp;Related Publications
                    </font>
                    <hr width="100%" size="0.01em" align="left" style="color:#ccc" noshade="noshade" />
                    <font class="info_text">

                        1) P. Fafalios, I. Kitsos and Y. Tzitzikas
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>"Scalable, Flexible and Generic Instant Overview Search"</i>
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Demo Paper, Proceedings of the 21st International Conference on World Wide Web, WWW 2012, Lyon, France, April 2012. 
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<a href="Servlet_OpenURI?uri=http://www.ics.forth.gr/~tzitzik/publications/Tzitzikas_2012_WWW.pdf" title="Scalable, Flexible and Generic Instant Overview Search" target="_blank">pdf</a>&nbsp;&bull;&nbsp;<a href="Servlet_OpenURI?uri=http://www.ics.forth.gr/~fafalios/ios-presentation.pdf" target="_blank" title="Scalable, Flexible and Generic Instant Overview Search - Presentation">ppt</a>&nbsp;&bull;&nbsp;<a href="Servlet_OpenURI?uri=http://www.ics.forth.gr/~fafalios/fafalios2012scalable.bib" title="Scalable, Flexible and Generic Instant Overview Search - BIB Entry" target="_blank">bib</a>)
                        <br />&nbsp;<br />

                        2) P. Fafalios, I. Kitsos, Y. Marketakis, C. Baldassarre, M. Salampasis and Y. Tzitzikas
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>"Web Searching with Entity Mining at Query Time"</i>
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Proceedings of the 5th Information Retrieval Facility Conference, IRF 2012, Vienna, July 2012. 
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<a href="Servlet_OpenURI?uri=http://www.ics.forth.gr/~tzitzik/publications/Tzitzikas_2012_IRF.pdf" target="_blank" title="Web Searching with Entity Mining at Query Time">pdf</a>&nbsp;&bull;&nbsp;<a href="http://users.ics.forth.gr/~fafalios/files/ppts/fafalios_2012_irfc_presentation.pdf" title="Web Searching with Entity Mining at Query Time - Presentation" target="_blank">ppt</a>&nbsp;&bull;&nbsp;<a href="Servlet_OpenURI?uri=http://www.ics.forth.gr/~fafalios/fafalios2012websearching.bib" title="Web Searching with Entity Mining at Query Time - BIB Entry" target="_blank">bib</a>)
                        <br />&nbsp;<br />
                        
                        3) P. Fafalios, M. Salampasis and Y. Tzitzikas
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>"Exploratory Patent Search with Faceted Search and Configurable Entity Mining"</i>
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Proceedings of the 1st International Workshop on Integrating IR technologies for Professional Search,
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;in conjunction with the 35th European Conference on Information Retrieval (ECIR'13), Moscow, Russia, March 2013.
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<a href="http://users.ics.forth.gr/~fafalios/files/pubs/fafalios_2013_explPatSearch.pdf" target="_blank" title="Exploratory Patent Search with Faceted Search and Configurable Entity Mining">pdf</a>&nbsp;&bull;&nbsp;<a href="http://users.ics.forth.gr/~fafalios/files/bibs/fafalios2013explPatSearch.bib" title="Exploratory Patent Search with Faceted Search and Configurable Entity Mining - BIB Entry" target="_blank">bib</a>)
                        <br />&nbsp;<br />
                        
                        4) P. Fafalios and Y. Tzitzikas
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>"X-ENS: Semantic Enrichment of Web Search Results at Real-Time"</i>
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Demo Paper, Proceedings of the 36th International ACM SIGIR Conference on Research and Development in Information Retrieval, 
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SIGIR 2013, Dublin, Ireland, 28 July - 1 August 2013.
                        <br />
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(<a href="http://users.ics.forth.gr/~fafalios/files/pubs/fafalios_2013_sigir.pdf" target="_blank" title="X-ENS: Semantic Enrichment of Web Search Results at Real-Time">pdf</a>&nbsp;&bull;&nbsp;<a href="http://users.ics.forth.gr/~fafalios/files/bibs/fafalios2013xens.bib" title="X-ENS: Semantic Enrichment of Web Search Results at Real-Time - BIB Entry" target="_blank">bib</a>)
                        <br />&nbsp;<br />
                        
                    </font>
                </div>

                <div class="gotop">
                    <span class="gotoptext"><a href="javascript:scroll(0,0);">top</a></span>
                </div>

                <div class="infocontent" id="contact">
                    <font class="info_title">
                        &raquo;&nbsp;Contact
                    </font>
                    <hr width="100%" size="0.01em" align="left" style="color:#ccc" noshade="noshade" />
                    <font class="info_text">

                        This system was developed in the scope of
                        <a href="http://users.ics.forth.gr/~fafalios/">Pavlos Fafalios</a> master's thesis 
                        under the supervision of <a href="http://users.ics.forth.gr/~tzitzik/">Yannis Tzitzikas</a>,
                        assistant professor at the University of Crete, 
                        and was partially supported by the EU project <a href="Servlet_OpenURI?uri=http://www.i-marine.eu/" title="iMarine EU Project" target="_blank">iMarine</a> (FP7-283644)
                        and the <a href="http://www.mumia-network.eu/" title="MUMIA COST Action">MUMIA COST Action</a> (IC1002, 2010-2014).

                        <br />&nbsp;<br />
                        <div class="contact">
                            <font size="+1">Pavlos Fafalios</font>
                            <br />
                            <a href="Servlet_OpenURI?uri=http://www.csd.uoc.gr/" target="_blank" title="Computer Science Department, University of Crete">Computer Science Department, University of Crete, Greece</a>
                            <br />
                            <a href="Servlet_OpenURI?uri=http://www.ics.forth.gr/isl/" target="_blank" title="">Information Systems Laboratory, Institute of  Computer Science, Foundation for Research and Technology - Hellas (FORTH), Greece</a>
                            <br />
                            Email: <a href="mailto:fafalios@csd.uoc.gr">fafalios@csd.uoc.gr</a>
                            <br />
                            Web page: <a target="_blank" href="Servlet_OpenURI?uri=http://www.ics.forth.gr/~fafalios/">http://www.ics.forth.gr/~fafalios/</a>
                        </div>

                        <br />

                        <div class="contact">
                            <font size="+1">Yannis Tzitzikas</font>
                            <br />
                            <a href="Servlet_OpenURI?uri=http://www.csd.uoc.gr/" target="_blank" title="Computer Science Department, University of Crete">Computer Science Department, University of Crete, Greece</a>
                            <br />
                            <a href="Servlet_OpenURI?uri=http://www.ics.forth.gr/isl/" target="_blank" title="">Information Systems Laboratory, Institute of  Computer Science, Foundation for Research and Technology - Hellas (FORTH), Greece</a>
                            <br />
                            Email: <a href="mailto:tzitzik@ics.forth.gr">tzitzik@ics.forth.gr</a>
                            <br />
                            Web page: <a target="_blank" href="Servlet_OpenURI?uri=http://www.ics.forth.gr/~tzitzik/">http://www.ics.forth.gr/~tzitzik/</a>
                        </div>

                    </font>
                </div>

                <div class="gotop">
                    <span class="gotoptext"><a href="javascript:scroll(0,0);">top</a></span>
                </div>

            </div>
        </div>

        <div class="footer">
            &nbsp;
        </div>




    </body>
</html>
