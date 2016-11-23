package gr.uoa.di.madgik.visualisations.client.injectors;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.TextResource;


public interface JsResources extends ClientBundle {
	
	public static final JsResources INSTANCE = GWT.create(JsResources.class);

	
	//basic libraries
	@Source("libs/d3/d3.js")
	TextResource d3JS();

	@Source("libs/nvd3/nv.d3.min.js")
	TextResource nvd3JS();

	@Source("libs/jquery/jquery-1.11.0.min.js")
	TextResource jqueryJS();

	@Source("libs/jquery/jquery.json-2.4.min.js")
	TextResource jqueryjsonJS();

	 
	 //for PieChart
	@Source("code/piechart/pie.js")
	TextResource pieJS();
	 //for PieChart for Plato
	@Source("code/piechartplato/pie.js")
	TextResource piePlatoJS();
	
	//for collisions
	@Source("code/collision/d3.geom.js")
	TextResource d3geomJS();
	@Source("code/collision/d3.layout.js")
	TextResource d3col_layoutJS();
	@Source("code/collision/collision.js")
	TextResource collisionJS();
	
	//for wordcloudgroup
	@Source("code/wordcloudgroup/WordCloud/d3.layout.cloud.js")
	TextResource d3layoutcloudJS();
	@Source("code/wordcloudgroup/Sunburst/sunburst.js")
	TextResource sunburstJS();
	@Source("code/wordcloudgroup/WordCloud/cloud.js")
	TextResource cloudJS();
	@Source("code/wordcloudgroup/Toolbox.js")
	TextResource wordcloudToolboxJS();
	
	//for barchart
	@Source("code/barchart/barchart.js")
	TextResource d3barchartJS();
	
	//for barchart for Plato
	@Source("code/barchartplato/barchart.js")
	TextResource d3barchartPlatoJS();
	
	//for bubbleplot
	@Source("code/bubbleplot/bubbleplot.js")
	TextResource d3bubbleplotJS();
	
	//for circleclusters
	@Source("code/circleclusters/circleclusters.js")
	TextResource d3circleclustersJS();
	
	//for multilines
	@Source("code/multilines/multilines.js")
	TextResource d3multilinesJS();
	
	//for tree
	@Source("code/tree/tree.js")
	TextResource d3treeJS();
	
	//for ClusterPacks
	@Source("code/clusterpacks/d3.js")  //overriding default library with older 
	TextResource d3olderJS();
	@Source("code/clusterpacks/d3.layout.js")
	TextResource d3clust_layoutJS();
	@Source("code/clusterpacks/clusterpacks.js")
	TextResource clusterpacksJS();
	
	
}