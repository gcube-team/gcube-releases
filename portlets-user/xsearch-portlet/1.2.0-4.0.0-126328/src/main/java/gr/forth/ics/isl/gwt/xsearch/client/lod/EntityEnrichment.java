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
package gr.forth.ics.isl.gwt.xsearch.client.lod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gr.forth.ics.isl.gwt.xsearch.client.XSearchService;
import gr.forth.ics.isl.gwt.xsearch.client.XSearchServiceAsync;
import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;

/**
 * A Class that is used to get the information about the entity 
 * enrichment and present them within a pop-up.
 * @author Pavlos fafalios
 */
public class EntityEnrichment {

    /**
     * A function that is used to request the properties from the 
     * server and present them within a pop-up.
     * @param category the category name
     * @param uri the content URI
     * @param id unused 
     */
    public static void showProperties(String category, String uri, String id) {

        System.out.println("# Retrieving properties of '" + uri + "' (" + category + ")");

        // Create the popup dialog box 
        final GCubeDialog dialogBox = new GCubeDialog();
        dialogBox.setGlassEnabled(false);
        dialogBox.setModal(false);
        dialogBox.setAnimationEnabled(true);
        dialogBox.getCaption().setText("Semantic Entity Exploration");

        final Button closeButton = new Button("Close");
        closeButton.getElement().setId("closeButton");

        final HTML serverResponseLabel = new HTML();

        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.addStyleName("dialogVPanel");
        dialogVPanel.add(serverResponseLabel);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });

        //final String head = "<center><div class=\"ee_boxtitle\">Properties of:<br /><span class=\"uriInProperties\">'" + uri + "'</span></div><br /></center>";
        serverResponseLabel.setHTML("<center><img src=\"/xsearch-portlet/images/loading.gif\" border=\"0\" style=\"text-decoration:none\"/>");
        dialogBox.center();

        XSearchServiceAsync xsearchSvc = GWT.create(XSearchService.class);
        xsearchSvc.getURIProperties(category, uri, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                dialogBox.setHTML("<font size=\"-1\" color=\"red\"><center>Problem retrieving properties..please try again later!</center></font>");
                closeButton.setFocus(true);
            }

            public void onSuccess(String result) {
                serverResponseLabel.setHTML(result);
            }
        });
    }
    
    /**
     * A function that takes the information for the entity enrichment.
     * @param entityName the name of the entity for which we want to retrieve
     * more information
     * @param categoryName the category of the entity
     */
    private static void getEntityEnrichment(String entityName, String categoryName){
        
        // Create the popup dialog box 
        final GCubeDialog dialogBox = new GCubeDialog();
        dialogBox.setGlassEnabled(false);
        dialogBox.setModal(false);
        dialogBox.setAnimationEnabled(true);

        final Button closeButton = new Button("Close");
        closeButton.getElement().setId("closeButton");

        //final Label entityNameLabel = new Label();
        //final Label categoryNameLabel = new Label();
        final HTML serverResponseLabel = new HTML();

        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.addStyleName("dialogVPanel");
        dialogVPanel.add(serverResponseLabel);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        });
        
        //final String head = "<center><div class=\"ee_boxtitle\">Entity Enrichment</div><br /></center>";
        serverResponseLabel.setHTML("<center><img src=\"/xsearch-portlet/images/loading.gif\" border=\"0\" style=\"text-decoration:none\"/>");
        dialogBox.center();
        dialogBox.getCaption().setText("Semantic Entity Exploration");
        
        XSearchServiceAsync xsearchSvc = GWT.create(XSearchService.class);
        xsearchSvc.getEntityEnrichment(entityName, categoryName, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                dialogBox.setHTML("<font size=\"-1\" color=\"red\"><center>Problem retrieving entities..please try again later!</center></font>");
                closeButton.setFocus(true);
            }

            public void onSuccess(String result) {
                serverResponseLabel.setHTML(result);
                //closeButton.setFocus(true);
            }
        });
    }
}
