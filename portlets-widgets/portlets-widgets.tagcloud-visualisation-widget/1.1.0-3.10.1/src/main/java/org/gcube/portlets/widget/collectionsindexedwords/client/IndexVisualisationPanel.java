package org.gcube.portlets.widget.collectionsindexedwords.client;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widget.collectionsindexedwords.client.exceptions.DataException;
import org.gcube.portlets.widget.collectionsindexedwords.client.exceptions.OnlyOpensearchException;
import org.gcube.portlets.widget.collectionsindexedwords.client.resource.ImageResources;
import org.gcube.portlets.widget.collectionsindexedwords.client.rpc.IndexClientCaller;
import org.gcube.portlets.widget.collectionsindexedwords.client.rpc.IndexClientCallerAsync;
import org.gcube.portlets.widget.collectionsindexedwords.shared.IndexData;

import gr.uoa.di.madgik.visualisations.WordCloudGroup.client.WordCloudGroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class IndexVisualisationPanel extends GCubeDialog {
	
	private String width,height,domElementID;
	private WordCloudGroup wcg;
	private VerticalPanel hostPanel = new VerticalPanel();
	private Image loadingImage;
	
	protected IndexClientCallerAsync clientCaller = GWT.create(IndexClientCaller.class);

	
	public IndexVisualisationPanel(String domElementID, int width, int height){
		super();
		loadingImage = new Image(ImageResources.INSTANCE.loading());
//		loadingImage = new Image("loading.gif");
		this.width = String.valueOf(width);
		this.height = String.valueOf(height);
		setText("Most common indexed terms for current search");
		setModal(true);
		setAutoHideEnabled(true);
		setAnimationEnabled(true);
		hostPanel.setPixelSize(width, height);
		this.domElementID = domElementID;
		setWidget(hostPanel);
		hostPanel.getElement().setId(domElementID);
		wcg = new WordCloudGroup();
		show();
		center();
	}

	
	public void visualiseCollectionData(Map<String,Integer> collectionKeyValues, Map<String, Map<String,Integer>> allWordcloudsKeyValues){
		wcg.createVisualisations(domElementID, String.valueOf(width), String.valueOf(height));
		wcg.visualiseCollections(collectionKeyValues, allWordcloudsKeyValues);
	}

	public void visualiseWordCloudData(Map<String,Integer> wordcloudKeyValues){
		wcg.createVisualisations(domElementID, String.valueOf(width), String.valueOf(height));
		wcg.visualiseWordCloud(wordcloudKeyValues);
	}

	public void redrawWordCloudData(Map<String,Integer> wordcloudKeyValues){
		wcg.redrawWordCloud(wordcloudKeyValues);
	}
	
	public boolean visualiseQueryStats(int queryId, int maxStats){
		
		//add loading image
		hostPanel.add(loadingImage);
		this.show();
		
		clientCaller.getValues(queryId, maxStats, new AsyncCallback<IndexData>() {
			
			@Override
			public void onFailure(Throwable caught) {
				loadingImage.removeFromParent();
				IndexVisualisationPanel.this.removeFromParent();
				if(caught instanceof OnlyOpensearchException){
					IndexVisualisationPanel.this.removeFromParent();
					Window.alert("You should include at least one non-opensearch collection. Opensearch collections are not indexed!");
				}
				if(caught instanceof DataException){
					Window.alert("Could not acquire data from the index services");
				}
				
			}

			@Override
			public void onSuccess(IndexData result) {
				loadingImage.removeFromParent();
				//copy result to internal structure
				HashMap<String, Integer> values = new HashMap<String,Integer>();
				ArrayList<Integer> vals = result.getValues();
				ArrayList<String> wds = result.getWords();
				for(int i=0;i<wds.size();i++)
					values.put(wds.get(i), vals.get(i));
				//do the visualisation
				if(values.size()==0){
					IndexVisualisationPanel.this.removeFromParent();
					Window.alert("No words to visualise from indexing services!");
				}
				else{
					wcg.createVisualisations(domElementID, String.valueOf(width), String.valueOf(height));
					wcg.visualiseWordCloud(values);
				}
			}

		});
		
		return true;
		
		
		
	}

	

}


