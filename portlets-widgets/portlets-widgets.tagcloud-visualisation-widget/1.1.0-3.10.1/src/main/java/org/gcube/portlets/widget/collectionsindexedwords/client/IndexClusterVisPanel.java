package org.gcube.portlets.widget.collectionsindexedwords.client;

import gr.uoa.di.madgik.visualisations.ClusterPacks.client.ClusterPacks;
import gr.uoa.di.madgik.visualisations.Tree.client.Tree;
import gr.uoa.di.madgik.visualisations.WordCloudGroup.client.WordCloudGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widget.collectionsindexedwords.client.rpc.IndexClientCaller;
import org.gcube.portlets.widget.collectionsindexedwords.client.rpc.IndexClientCallerAsync;



import org.gcube.portlets.widget.collectionsindexedwords.shared.IndexData;
import org.gcube.rest.index.client.exceptions.IndexException;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class IndexClusterVisPanel extends GCubeDialog {

	private String width,height,domElementID;
//	private Tree tree;
	private ClusterPacks clusterPacks;
	private VerticalPanel hostPanel = new VerticalPanel();
	
	protected IndexClientCallerAsync clientCaller = GWT.create(IndexClientCaller.class);
	
	public IndexClusterVisPanel(String domElementID, int width, int height){
		super();
		this.width = String.valueOf(width);
		this.height = String.valueOf(height);
		setModal(true);
		setAutoHideEnabled(true);
		setAnimationEnabled(true);
		hostPanel.setPixelSize(width, height);
		this.domElementID = domElementID;
		setWidget(hostPanel);
		hostPanel.getElement().setId(domElementID);
//		tree = new Tree();
		clusterPacks = new ClusterPacks();
		show();
		center();
	}

	
	public void visualiseIndexClusters(int queryId){

		
		clientCaller.getClusterValues(queryId, new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				consoleLog("Could not acquire the visualisation data...." + caught.getMessage());
				IndexClusterVisPanel.this.setTitle("Error");
				IndexClusterVisPanel.this.setText("Could not acquire the visualisation data");
				IndexClusterVisPanel.this.show();
				IndexClusterVisPanel.this.center();
			}

			@Override
			public void onSuccess(String jsonData) {
				consoleLog("Success, proceeding with the visualisation");
//				tree.visualiseTree(IndexClusterVisPanel.this.domElementID, jsonData, IndexClusterVisPanel.this.width, IndexClusterVisPanel.this.height);
				clusterPacks.visualiseClusters(IndexClusterVisPanel.this.domElementID, jsonData, IndexClusterVisPanel.this.width, IndexClusterVisPanel.this.height);
			}
			
		});

	}
	
	
	native void consoleLog( String message) /*-{
	    console.log(message );
	}-*/;

	
}
