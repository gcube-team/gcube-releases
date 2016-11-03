package gr.uoa.di.madgik.visualisations.client.injectors;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;

public interface CssResources extends ClientBundle {

	public static final CssResources INSTANCE = GWT.create(CssResources.class);
	
	 @Source("libs/nvd3/nv.d3.min.css")
	 TextResource nvd3CSS();
	
	 
	 //for collisions
	 @Source("code/collision/style.css")
	 TextResource collisionCSS();
	
	 //for wordcloudgroup
	 @Source("code/wordcloudgroup/WordCloud/cloud.css")
	 TextResource wordcloudCSS();
	 @Source("code/wordcloudgroup/docVis.css")
	 TextResource docVisCSS();

	 //for circleclusters
	 @Source("code/circleclusters/circleclusters.css")
	 TextResource circleclustersCSS();
	 
	 //for tree
	 @Source("code/tree/tree.css")
	 TextResource treeCSS();
	 
	 //for clusterpacks
	 @Source("code/clusterpacks/style.css")
	 TextResource clusterpacksCSS();
	 
	
}
