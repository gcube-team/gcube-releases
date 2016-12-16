package gr.uoa.di.madgik.visualisations.WordCloudGroup.client;

import java.util.Map;

import gr.uoa.di.madgik.visualisations.client.injectors.CssResources;
import gr.uoa.di.madgik.visualisations.client.injectors.JSInjector;
import gr.uoa.di.madgik.visualisations.client.injectors.JsResources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;





public class WordCloudGroup implements EntryPoint{

	private String width;
	private String height;
	private String divID;
	private boolean alreadyVisualised;
	
	
//	private String col = "{\"name\":\"flare\",\"children\":[{\"name\":\"CategoryA\",\"size\":100},{\"name\":\"CategoryB\",\"size\":100},{\"name\":\"CategoryC\",\"size\":100},{\"name\":\"CategoryD\",\"size\":100},{\"name\":\"CategoryE\",\"size\":100},{\"name\":\"CategoryF\",\"size\":100}]}";
//	private String wor = "[{\"key\":\"cat\",\"value\":100},{\"key\":\"mouse\",\"value\":170},{\"key\":\"elephant\",\"value\":67},{\"key\":\"girraffe\",\"value\":92},{\"key\":\"dog\",\"value\":123},{\"key\":\"crocodile\",\"value\":80},{\"key\":\"fish\",\"value\":176}]";

	
	public void onModuleLoad() {

		//inject js/css resources
		JSInjector.inject(JsResources.INSTANCE.d3layoutcloudJS().getText());
		JSInjector.inject(JsResources.INSTANCE.sunburstJS().getText());
		JSInjector.inject(JsResources.INSTANCE.cloudJS().getText());
		JSInjector.inject(JsResources.INSTANCE.wordcloudToolboxJS().getText());
		
		StyleInjector.inject(CssResources.INSTANCE.wordcloudCSS().getText());
		StyleInjector.inject(CssResources.INSTANCE.docVisCSS().getText());
		
		alreadyVisualised = false;
		
	}
	
	
//	 // Run this to initiate all the DIVS for the visualisation.	
//	public void createWordcloud(String divID, String width, String height) {
//		this.divID = divID;
//		this.width = width;
//		this.height = height;
//		createWordcloudJS(divID, width, height);
//	}
//	
//	private static native void createWordcloudJS(String divID, String width, String height) /*-{
//		var divElem = $wnd.$("#"+divID);
//		divElem.empty(); // clear all the previous content within the div (clear all html)
//		divElem.removeAttr("style");  //clear also the styling
//		divElem.width(width);
//		divElem.height(height);
//		
//		$wnd.d3.select("#"+divID).append("div").attr("id", "wordcloud");
//	}-*/;
	
	
	/**
	 * Use this for a single cloud visualisation (just a wordcloud)
	 * @param wordcloudDataJSON
	 * @param width
	 * @param height
	 */
	public void visualiseWordCloud(String wordcloudDataJSON, String width, String height) {
		if(!alreadyVisualised)
			visualiseWordCloudJS(divID, wordcloudDataJSON, width, height, true, true);
		else
			visualiseWordCloudJS(divID, wordcloudDataJSON, width, height, true, false);
		alreadyVisualised = true;
	}
	
	private static native void visualiseWordCloudJS(String divID, String wordsJSON, String width, String height, boolean useAllSpace, boolean createnew) /*-{
//		$wnd.alert("Trying the visualise visualiseWords");
		$wnd.visualiseWords($wnd.d3.select("#"+divID), width, height, wordsJSON, useAllSpace, createnew);
		//$wnd.align();
	}-*/;
	
	
	/**
	 * 
	 * @param originalJSON the json as output by the gCube index
	 * @return the json as 
	 */
	private static native String transformFilterStopwordsJS(String originalJSON) /*-{
		return $wnd.transformFilterStopwords(originalJSON);
	}-*/;
	
	
	 // Run this to initiate all the DIVS for the visualisations.	
	public void createVisualisations(String divID, String width, String height) {
		this.divID = divID;
		this.width = width;
		this.height = height;
		createVisualisationsJS(divID, width, height);
	}
	
	/**
	 * Use this for a group of visualisations (wordcloud with surrounding ring)
	 * @param collectionsJSON
	 * @param wordcloudDataJSON
	 * @param width
	 * @param height
	 */
	private void visualiseCollections(String collectionsJSON, String wordcloudDataJSON, String width, String height) {
		if(!alreadyVisualised)
			visualiseAllCollectionsJS(divID, collectionsJSON, wordcloudDataJSON , width, height);
		else
			visualiseCollectionsJS(divID, collectionsJSON, wordcloudDataJSON, width, height, false);
		alreadyVisualised = true;
	}
	
	
	
	private static native void createVisualisationsJS(String divID, String width, String height) /*-{
		var divElem = $wnd.$("#"+divID);
		divElem.empty(); // clear all the previous content within the div (clear all html)
		divElem.removeAttr("style");  //clear also the styling
		divElem.width(width);
		divElem.height(height);
		
		$wnd.d3.select("#"+divID).append("div").attr("id", "wordcloud");
		$wnd.d3.select("#"+divID).append("div").attr("id", "sunburst");
	}-*/;
	
	
	private static native void visualiseAllCollectionsJS(String divID, String collectionsJSON, String allWordCloudsJSON, String width, String height) /*-{
		$wnd.visualiseAllCollections($wnd.d3.select("#"+divID), width, height, collectionsJSON, allWordCloudsJSON);
	}-*/;
	
	
	private static native void visualiseCollectionsJS(String divID, String collectionsJSON, String wordsJSON, String width, String height, boolean createnew) /*-{
//		$wnd.alert("Trying the visualiseCollections");
		$wnd.visualiseCollections($wnd.d3.select("#"+divID), width, height, collectionsJSON, createnew);
//		$wnd.alert("Trying the visualise visualiseWords");
		$wnd.visualiseWords($wnd.d3.select("#"+divID), width, height, wordsJSON, createnew);
		$wnd.align($wnd.d3.select("#"+divID));
	}-*/;

	/**
	 * Visualise a single bunch of words as a wordcloud
	 * 
	 * @param wordcloudKeyValues should contain (word, number_of_occurrences) pairs
	 */
	public void visualiseWordCloud(Map<String,Integer> wordcloudKeyValues){
		StringBuilder jsonWordCloudSB = new StringBuilder("[");
		for(String key: wordcloudKeyValues.keySet())
			jsonWordCloudSB.append("{\"key\":\""+key+"\",\"value\":"+wordcloudKeyValues.get(key)+"},");
		String jsonWordCloud = jsonWordCloudSB.toString().substring(0, jsonWordCloudSB.length()-1)+"]";
		createVisualisations(divID,  width, height);
		visualiseWordCloud(jsonWordCloud, width, height);
	}

	
	/*
	 * Redraw an already visualised wordcloud
	 */
	public void redrawWordCloud(Map<String,Integer> wordcloudKeyValues){
		StringBuilder jsonWordCloudSB = new StringBuilder("[");
		for(String key: wordcloudKeyValues.keySet())
			jsonWordCloudSB.append("{\"key\":\""+key+"\",\"value\":"+wordcloudKeyValues.get(key)+"},");
		String jsonWordCloud = jsonWordCloudSB.toString().substring(0, jsonWordCloudSB.length()-1)+"]";
		visualiseWordCloud(jsonWordCloud, width, height);
	}
	
	
	/**
	 * Visualise Collections and Words of collections
	 * If collectionKeyValues is null, it visualises only the words in a wordcloud
	 * 
	 * @param collectionKeyValues should contain (collection_name, a_number) pairs
	 * @param wordcloudsKeyValues should have a form:  (collection_name (word, number_of_occurrences))
	 */
	public void visualiseCollections(Map<String,Integer> collectionKeyValues, Map<String, Map<String,Integer>> wordcloudsKeyValues){
		
		
		String jsonWordClouds="", jsonColls="";
		
//		final String col = "{\"name\":\"flare\",\"children\":[{\"name\":\"CategoryA\",\"size\":100},{\"name\":\"CategoryB\",\"size\":100},{\"name\":\"CategoryC\",\"size\":100},{\"name\":\"CategoryD\",\"size\":100},{\"name\":\"CategoryE\",\"size\":100},{\"name\":\"CategoryF\",\"size\":100}]}";
		
		StringBuilder jsonCollsSB = new StringBuilder("{\"name\":\"flare\",\"children\":[");
		for(String key: collectionKeyValues.keySet())
			jsonCollsSB.append("{\"name\":\""+key+"\",\"size\":"+collectionKeyValues.get(key)+"},");
		jsonColls = jsonCollsSB.toString().substring(0, jsonCollsSB.length()-1)+"]}";
			
//		final String wor = "[{\"key\":\"cat\",\"value\":100},{\"key\":\"mouse\",\"value\":170},{\"key\":\"elephant\",\"value\":67},{\"key\":\"girraffe\",\"value\":92},{\"key\":\"dog\",\"value\":123},{\"key\":\"crocodile\",\"value\":80},{\"key\":\"fish\",\"value\":176}]";
		
		StringBuilder jsonWordCloud = new StringBuilder("{");
		for(String colname : wordcloudsKeyValues.keySet()){
			StringBuilder wordCloudSB = new StringBuilder();
			Map<String,Integer> wordcloud = wordcloudsKeyValues.get(colname);
			wordCloudSB.append("\""+colname+"\":[");
			for(String word : wordcloud.keySet())
				wordCloudSB.append("{\"key\":\""+word+"\",\"value\":"+wordcloud.get(word)+"},");
			jsonWordCloud.append(wordCloudSB.toString().substring(0, wordCloudSB.length()-1)+"],");
		}
		jsonWordClouds = jsonWordCloud.toString().substring(0, jsonWordCloud.length()-1)+"}";
		
//		Window.alert("jsonColls= "+jsonColls);
//		Window.alert("jsonWordClouds= "+jsonWordCloud);
		
		createVisualisations(divID,  width, height);
		visualiseCollections(jsonColls, jsonWordClouds, width, height);
		
	}

	
	
}