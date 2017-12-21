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

package gr.forth.ics.isl.xsearch.clustering;

import gr.forth.ics.isl.xsearch.SearchResult;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.stellaclustering.CLT_Creator;
import gr.forth.ics.isl.stellaclustering.ContentToCluster;
import gr.forth.ics.isl.stellaclustering.util.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class Clustering {

    private String clustersContent;
    private String query;
    private int numOfClusters;
    private boolean onlySnippets;
    private ArrayList<SearchResult> wseResults;
    private CLT_Creator clusterer;

    public Clustering(ArrayList<SearchResult> wseResults, String query, boolean onlySnippets, int numOfClusters, int clusteringAlgorithm) {
        
        this.query = query;
        this.numOfClusters = numOfClusters;
        this.onlySnippets = onlySnippets;
        this.wseResults = wseResults;

        ArrayList<ContentToCluster> contentsToCluster = new ArrayList<ContentToCluster>();
        for (SearchResult wse_res : wseResults) {
            ContentToCluster content;
            if (onlySnippets) { 
                content = new ContentToCluster(wse_res.getTitle(), wse_res.getDescription());
            } else {
                content = new ContentToCluster(wse_res.getTitle() + " | " + wse_res.getDescription(), wse_res.getContent());
            }
            contentsToCluster.add(content);
        }

        clusterer = new CLT_Creator(query, contentsToCluster, clusteringAlgorithm, numOfClusters);
        TreeNode clt = clusterer.getClusterTree();
        formClustersString(clt, contentsToCluster);

    }

    private void formClustersString(TreeNode clt, ArrayList<ContentToCluster> contents) {
        clustersContent = "";
        String storedQuery = query;
        if (contents != null) {
            if (!contents.isEmpty()) {

                storedQuery = storedQuery.replace("'", "&quot;").replace("\"", "&quot;");
                clustersContent += "<script>eval(\"Node = CreateProjectExplorer('" + storedQuery + "(" + contents.size() + ")');\");</script>";


                StringBuilder tmpVec = new StringBuilder();
                String Title;
                ArrayList<Integer> v;
                int size;
                Enumeration en = clt.children();
                while (en.hasMoreElements()) {
                    TreeNode leftChild = (TreeNode) en.nextElement();
                    Title = leftChild.getTitle();
                    v = (ArrayList<Integer>) leftChild.getUserObject();
                    size = v.size();

                    Enumeration en1 = leftChild.children();

                    tmpVec.setLength(0);

                    String docs = "";
                    for (int j = 0; j < v.size(); j++) {

                        int rank = v.get(j);

                        rank = rank - 1;

                        docs += rank;
                        if (j != v.size() - 1) {
                            docs += ",";
                        }

                    }

                    clustersContent += "<script>eval(\"aNode=createProjectNode(Node,'" + Title + "(" + size + ")'," + !leftChild.isLeaf() + ",'" + docs + "');\");</script>";

                    tmpVec.setLength(0);
                    while (en1.hasMoreElements()) {
                        leftChild = (TreeNode) en1.nextElement();
                        Title = leftChild.getTitle();
                        v = (ArrayList) leftChild.getUserObject();
                        size = v.size();

                        Enumeration en2 = leftChild.children();
                        tmpVec.setLength(0);

                        docs = "";
                        for (int j = 0; j < v.size(); j++) {

                            int rank = v.get(j);

                            rank = rank - 1;

                            docs += rank;
                            if (j != v.size() - 1) {
                                docs += ",";
                            }

                        }
                        clustersContent += "<script>eval(\"bNode = createProjectNode(aNode,'" + Title + "(" + size + ")'," + !leftChild.isLeaf() + ",'" + docs + "');\");</script>";



                        tmpVec.setLength(0);
                        while (en2.hasMoreElements()) {
                            leftChild = (TreeNode) en2.nextElement();
                            Title = leftChild.getTitle();
                            v = (ArrayList) leftChild.getUserObject();
                            size = v.size();

                            Enumeration en3 = leftChild.children();
                            tmpVec.setLength(0);

                            docs = "";
                            for (int j = 0; j < v.size(); j++) {

                                int rank = v.get(j);

                                rank = rank - 1;

                                docs += rank;
                                if (j != v.size() - 1) {
                                    docs += ",";
                                }

                            }
                            clustersContent += "<script>eval(\"cNode = createProjectNode(bNode,'" + Title + "(" + size + ")'," + !leftChild.isLeaf() + ",'" + docs + "');\");</script>";



                            tmpVec.setLength(0);
                            while (en3.hasMoreElements()) {
                                leftChild = (TreeNode) en3.nextElement();
                                Title = leftChild.getTitle();
                                v = (ArrayList) leftChild.getUserObject();
                                Enumeration en4 = leftChild.children();
                                size = v.size();

                                docs = "";
                                for (int j = 0; j < v.size(); j++) {

                                    int rank = v.get(j);

                                    rank = rank - 1;

                                    docs += rank;
                                    if (j != v.size() - 1) {
                                        docs += ",";
                                    }

                                }
                                clustersContent += "<script>eval(\"dNode = createProjectNode(cNode,'" + Title + "(" + size + ")'," + !leftChild.isLeaf() + ",'" + docs + "');\");</script>";


                                tmpVec.setLength(0);
                                while (en4.hasMoreElements()) {
                                    leftChild = (TreeNode) en4.nextElement();
                                    Title = leftChild.getTitle();
                                    v = (ArrayList) leftChild.getUserObject();
                                    size = v.size();

                                    docs = "";
                                    for (int j = 0; j < v.size(); j++) {

                                        int rank = v.get(j);

                                        rank = rank - 1;


                                        docs += rank;
                                        if (j != v.size() - 1) {
                                            docs += ",";
                                        }

                                    }
                                    clustersContent += "<script>eval(\"createProjectNode(dNode,'" + Title + "(" + size + ")'," + !leftChild.isLeaf() + ",'" + docs + "');\");</script>";
                                }
                            }//end of child child Node
                        }//enf of child childNodes
                    }//End of childNodes
                }//End of rootChild Nodes
            }
        }
    }

    public String getClustersContent() {
        return clustersContent;
    }

    public void setClustersContent(String clustersContent) {
        this.clustersContent = clustersContent;
    }

    public int getNumOfClusters() {
        return numOfClusters;
    }

    public void setNumOfClusters(int numOfClusters) {
        this.numOfClusters = numOfClusters;
    }

    public boolean isOnlySnippets() {
        return onlySnippets;
    }

    public void setOnlySnippets(boolean onlySnippets) {
        this.onlySnippets = onlySnippets;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<SearchResult> getWseResults() {
        return wseResults;
    }

    public void setWseResults(ArrayList<SearchResult> wseResults) {
        this.wseResults = wseResults;
    }

    public CLT_Creator getClusterer() {
        return clusterer;
    }

    public void setClusterer(CLT_Creator clusterer) {
        this.clusterer = clusterer;
    }
    
    
    
    
}
