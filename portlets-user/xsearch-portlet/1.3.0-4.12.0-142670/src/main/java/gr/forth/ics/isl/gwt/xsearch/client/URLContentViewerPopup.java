/*
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
 */
package gr.forth.ics.isl.gwt.xsearch.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;

/**
 * Class that extend GCubeDialog.
 * Creates a new PopUp that contains the content URLs of the selected hit.
 * @author  kitsos Ioannis (kitsos@ics.forth.gr, kitsos@csd.uoc.gr)
 */
public class URLContentViewerPopup extends GCubeDialog {

    private Tree serversTree = new Tree();
    private VerticalPanel treePanel = new VerticalPanel();
    private ScrollPanel treePanelScroller = new ScrollPanel();
    private VerticalPanel vPanel = new VerticalPanel();
    private TreeMap<String, List<String>> urls;

    /**
     * 
     * @param urls
     * @param autoShow 
     */
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public URLContentViewerPopup(TreeMap<String, List<String>> urls, boolean autoShow) {
        this.urls = urls;
        new URLContentViewerPopup(urls, -1, -1, autoShow);
    }

    /**
     * Constructor. 
     * Creates a new pop up window that contains the URLs of hit's contents.
     * @param urls a TreeMap that contains the identified URLs into groups (if they have)
     * @param width pop-up window's width
     * @param height pop-up window's height
     * @param autoShow if want to show the window
     */
    public URLContentViewerPopup(TreeMap<String, List<String>> urls, int width, int height, boolean autoShow) {
        this.urls = urls;
        serversTree.setAnimationEnabled(true);
        treePanel.setSpacing(5);
        treePanelScroller.add(treePanel);
        treePanel.add(serversTree);
    
        // Create/Initialize contents Tree
        boolean isContentTreeEmpty = createContentTree();
        
        // Set pop-up properties
        if (width != -1 && height != -1) {
            this.setPixelSize(width, height);
        }       
        this.setModal(true);
        this.setGlassEnabled(true);
        
        if(isContentTreeEmpty){
            this.setText("Content Not Available");
        }else{
             this.setText("Available Content");
        }
        
        // Create close button
        Button closeButton = new Button("Close");
        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });

        // Add objects to Vertical Panel
        vPanel.add(treePanelScroller);
        vPanel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));
        vPanel.add(closeButton);

        // Add Vertical Panel into pop-up
        this.add(vPanel);
        if (autoShow) {
            this.show();
            this.center();
        }
    }

    /**
     * Creates content tree with the main and alternative content URLs.
     * @return true if found URLs in content otherwise false
     */
    private boolean createContentTree() {
        boolean bothAreEmpty = true;
        for (Entry<String, List<String>> item : urls.entrySet()) {
            String type = item.getKey();
            List<String> contentURLs = urls.get(type);
            if (contentURLs != null && !contentURLs.isEmpty()) {
                TreeItem typeNode = serversTree.insertItem(0, type);
                for (int i = 0; i < contentURLs.size(); i++) {
                    String htmlText = "<a href=\"" + contentURLs.get(i)
                            + "\" target=\"_blank\">"
                            + contentURLs.get(i)
                            + "</a>";
                    typeNode.addItem(new HTML(htmlText));
                }
                typeNode.setState(true);
                bothAreEmpty = false;
            }
        }
        
        return bothAreEmpty;
    }
}
