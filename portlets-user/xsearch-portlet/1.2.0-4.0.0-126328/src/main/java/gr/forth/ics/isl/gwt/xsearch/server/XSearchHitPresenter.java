/*
 * 
 * Copyright 2012 FORTH-ICS-ISL (http://www.ics.forth.gr/isl/) 
 * Foundation for Research and Technology - Hellas (FORTH)
 * Institute of Computer Science (ICS) 
 * Information Systems Laboratory (ISL)
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * 
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 * 
 */
package gr.forth.ics.isl.gwt.xsearch.server;

import gr.forth.ics.isl.gwt.xsearch.server.metadatagroupings.MetadataGroupingsGenerator;
import gr.forth.ics.isl.xsearch.configuration.Conf;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

/**
 * A Class that formulates that formulates the search results in a presentable
 * way.
 *
 * @author Pavlos fafalios
 */
public class XSearchHitPresenter {

    HashSet<String> importantFields;

    /**
     * Initialize the fields that are important.
     */
    public XSearchHitPresenter() {
        importantFields = new HashSet<>();
        importantFields.add("title");
        importantFields.add("s");
        importantFields.add("publisher");
        importantFields.add("created");
        importantFields.add("language");
        importantFields.add("ispartof");
        importantFields.add("type");
    }

    /**
     * Create a new Hit presentable and add the metadata of that hit to
     * metadataGroupings collections.
     *
     * @param metadataGroupings a structure that stores the metatada Groups
     * @param hitn the position of hit in query's answer
     * @param httpReq
     * @param xmlRepresentation the xml representation of a hit/object
     * @return a string with a presentable new hit
     */
    public String createNewHit(MetadataGroupingsGenerator metadataGroupings, int hitn, HttpServletRequest httpReq, String xmlRepresentation) {
        String mainFields = "<table border='0px' class=\"aHitTable\" cellspacing='5px' cellpadding='5px'>";
        String metadataFields = "<table border='0px' class=\"aHitTable\" cellspacing='5px' cellpadding='5px'>";

        // HTMLTag object for the XML data
        HTMLTag xmlReprReader = new HTMLTag(xmlRepresentation);

        // get the index of the first element 'field' in the xml data
        int i = xmlReprReader.getFirstTagIndex("field");

        // Iterate through all 'fields in the XMLRepresentation
        String objTitle = "";
        String objSnippet = "";
        String objDescription = "";
        String objSummary = "";

        String docURI = "";
        ArrayList<String> metedataRows = new ArrayList<>();

        int nn = 0;
        int nForShow = 0;
        while (i != -1) {
            nForShow++;
            //Get the content of the current field
            String fieldData = xmlReprReader.getFirstTagData("field", i);

            // Create a HTMLTag object for the content field
            HTMLTag fieldTagger = new HTMLTag(fieldData);

            // Get the element Name
            String name = fieldTagger.getFirstTagData("fieldName");

            // Get the elemen id
            String id = fieldTagger.getFirstTagData("fieldId");

            // Get the element value
            String value = fieldTagger.getFirstTagData("fieldValue");

            // In case that the field does not have ID and the name is the same with
            // the id then the value of id is "null, thus we overwrite it.
            if (name.equals("null")) {
                name = id;
            }

            if (id.trim().equals("ObjectID")) {
                // Get DocURI
                docURI = value;
                String shownValue = value;
                if (shownValue == null) {
                    shownValue = "";
                }
                if (shownValue.trim().equals("NoMetaId")) {
                    shownValue = "";
                }
                if (shownValue.toLowerCase().startsWith("http")) {
                    shownValue = "<a href='" + shownValue + "' target='_blank'>" + shownValue + "</a>";
                }
                if (shownValue.equals("") || shownValue.toLowerCase().equals("null")) {
                    shownValue = "-";
                }
                String row = "<tr><td width='140px' class='hitTdTitle'><span class='objsnippetTitle'>" + name + ": </span></td><td class='hitTdValue'><span class='objsnippetContent'>" + shownValue + "</span></td></tr>";
                metedataRows.add(row);

            }else {

                if (value == null) {
                    value = "";
                }

                if (value.toLowerCase().equals("null")) {
                    value = "";
                }

                if (name.trim().toLowerCase().equals("title")) {
                    value = value.replace("&amp;gt;", ">").replace("&amp;lt;", "<");
                    value = value.replace("&amp;amp;", "&");
                    objTitle = value;
                } else if (name.trim().toLowerCase().equals("s")) {
                    objSnippet = value;
                } else {
                    if (name.trim().toLowerCase().equals("description")) {
                        objDescription = value;
                    }

                    if (name.trim().toLowerCase().equals("summary")) {
                        objSummary = value;
                    }

                    value = value.replace("&amp;amp;gt;", ">").replace("&amp;amp;lt;", "<");
                    value = value.replace("&amp;gt;", ">").replace("&amp;lt;", "<");
                    value = value.replace("&amp;amp;", "&");

                    String presValue = value;
                    if (!value.equals("")) {
                        if (value.toLowerCase().startsWith("http")) {
                            presValue = "<a href='" + value + "' target='_blank'>" + value + "</a>";
                        }
                        String row = "<tr><td width='140px' class='hitTdTitle'><span class='objsnippetTitle'>" + name + ": </span></td><td class='hitTdValue'><span class='objsnippetContent'>" + presValue + "</span></td></tr>";
                        metedataRows.add(row);
                    }
                }
            }
            nn++;

            // Find the index of the next field
            i = xmlReprReader.getFirstTagIndex("field", i + 1);

            // Update metadata_Groupings
            if (Conf.enableMetadataGroupings && !name.trim().toLowerCase().equals("s") && !name.trim().toLowerCase().equals("description")) {
                if (!value.isEmpty()) {
                    metadataGroupings.addMetadata(name, value, hitn);
                }
            }

        }

        String titleRow = "";
        if (docURI == null) {
            docURI = "";
        }
        if (docURI.trim().toLowerCase().equals("nometaid")) {
            docURI = "";
        }
        if (docURI.equals("")) {
            titleRow = "<tr><td colspan='2' class='hitTdTitleOneRow'><a class='objtitle' "
                    + "href=javascript:viewContent()>"
                    + objTitle + "</a></td></tr>";
        } else {
            titleRow = "<tr><td colspan='2' class='hitTdTitleOneRow'><a class='objtitle' "
                    + "href=javascript:viewContent('" + docURI + "')>"
                    + objTitle + "</a></td></tr>";
        }

        if (objSnippet.equals("")) {
            if (objDescription.equals("")) {
                objSnippet = objSummary;
            } else {
                objSnippet = objDescription;
            }
        }

        int limit = 400;
        objSnippet = objSnippet.replace("&amp;amp;gt;", ">").replace("&amp;amp;lt;", "<").replace("&amp;gt;", ">").replace("&amp;lt;", "<").replace("&lt;", "<").replace("&gt;", ">");

        Random rand = new Random();
        int n1 = rand.nextInt(99999999);
        String objSnippetShort = objSnippet;

        if (objSnippet.length() > limit) {

            objSnippet = objSnippet.replace("^", "").replace("~", "");
            objSnippet = objSnippet.replace("<b>", "^");
            objSnippet = objSnippet.replace("</b>", "~");

            String id1 = "showall" + hitn + "_" + nForShow + "_" + n1;
            String id2 = "remtext" + hitn + "_" + nForShow + "_" + n1;

            int cutPos = limit;
            int i1 = objSnippet.toLowerCase().indexOf("~", limit); // index of </b>
            int i2 = objSnippet.toLowerCase().indexOf("^", limit); // index of <b>
            if (i1 == -1) {
                //ok
            } else {
                if (i2 == -1) {
                    cutPos = i1 + 1;
                } else {
                    if (i1 > i2) {
                        //ok
                    } else {
                        cutPos = i1 + 1;
                    }
                }
            }

            if (cutPos > objSnippet.length()) {
                objSnippetShort = objSnippet.replace("~", "</b>").replace("^", "<b>");
                objSnippetShort += "<a style=\"text-decoration:none;\" href=\"#\" onClick=\"javascript:document.getElementById('" + id1 + "').style.display='none';document.getElementById('" + id2 + "').style.display='inline';\"><span id='" + id1 + "' class='showalltext'>...show all</span></a>";
                objSnippetShort += "<span id=\"" + id2 + "\" style=\"display:none;\">" + objSnippet.replace("~", "</b>").replace("^", "<b>") + "</span>";
            } else {
                objSnippetShort = objSnippet.substring(0, cutPos).replace("~", "</b>").replace("^", "<b>");
                objSnippetShort += "<a style=\"text-decoration:none;\" href=\"#\" onClick=\"javascript:document.getElementById('" + id1 + "').style.display='none';document.getElementById('" + id2 + "').style.display='inline';\"><span id='" + id1 + "' class='showalltext'>...show all</span></a>";
                objSnippetShort += "<span id=\"" + id2 + "\" style=\"display:none;\">" + objSnippet.substring(cutPos).replace("~", "</b>").replace("^", "<b>") + "</span>";
            }
        }


        String snippetRow = "<tr><td colspan='2' class='hitTdTitleOneRow'>" + objSnippetShort + "</td></tr>";
        mainFields += (titleRow + snippetRow);

        for (String r : metedataRows) {
            metadataFields += r;
        }

        mainFields += "</table>";
        metadataFields += "</table>";

//        String hitFinalFormat = "<div class='objtitlespan'><a class='objtitle' onclick=\"window.open('"
//                + getContentURI(httpReq, docURI)
//                + "','windowname',' width=400,height=200')\" >"
//                + objTitle + "</a></div>" + "<br />";

        // create tab
        //TabPanel object = new TabPanel();
        //object.add(new HTML(mainFields), "Object");
        // object.add(new HTML(metadataFields), "Metadata");


        return mainFields + "\t" + metadataFields;
    }

    /**
     * Creates the documents URI and Returns it. Stale is not used any more
     *
     * @return documents URI
     */
    private String getContentURI(HttpServletRequest httpReq, String docURI) {
        String uri = "";
        //<scheme>://<serverName>:<serverPort>/applicationSupportLayerHttp/ContentViewer?documentURI=<docURI>&username=<username>
        String scheme = httpReq.getScheme();
        String serverName = httpReq.getServerName();
        int serverPort = httpReq.getServerPort();
        String usrName = (String) httpReq.getSession()
                .getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

        uri = scheme
                + "://"
                + serverName + ":"
                + serverPort + "/aslHttpContentAccess/ContentViewer?documentURI="
                + docURI
                + "&username="
                + usrName;

        return uri;
    }
}
