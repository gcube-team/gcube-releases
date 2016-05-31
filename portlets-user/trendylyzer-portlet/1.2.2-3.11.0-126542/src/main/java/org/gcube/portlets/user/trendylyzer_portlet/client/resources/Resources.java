package org.gcube.portlets.user.trendylyzer_portlet.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface Resources extends ClientBundle {
	@Source("spInfo.png")
	ImageResource infoSp();
	
	@Source("DEFAULT_IMAGE.png")
	ImageResource defaultAlg();
	@Source("logo.png")
	ImageResource logo();
	
	@Source("goBack.png")
	ImageResource goBack();
	
	@Source("graph.png")
	ImageResource experiment();
	
	@Source("menuItemExperiment.png")
	ImageResource menuOcc();

	@Source("application_side_expand.png")
	ImageResource addOperator();
	
	@Source("delete.png")
	ImageResource removeAll();

	@Source("folder_explore.png")
	ImageResource folderExplore();
	
	@Source("tree.gif")
	ImageResource tree();
	
	@Source("sort_asc.gif")
	ImageResource sortAscending();
	
	@Source("triangle.png")
	ImageResource startComputation();
	
	@Source("monitor.png")
	ImageResource computationPerspective();
	@Source("connector1.png")
	ImageResource workflowConnector1();

	@Source("connector2.png")
	ImageResource workflowConnector2();

	@Source("ajax-loader-big.gif")
	ImageResource loaderBig();
	
	@Source("cancel_icon.png")
	ImageResource cancel();

	@Source("add.png")
	ImageResource add();

	@Source("table.png")
	ImageResource table();
	
	@Source("save.png")
	ImageResource save();

	@Source("user_green.png")
	ImageResource userPerspective();

		@Source("table.png")
	ImageResource fileDownload();
		
		@Source("menuItemComputations.png")
		ImageResource menuItemComputations();

		@Source("details.png")
		ImageResource details();

		@Source("tableResult.png")
		ImageResource tableResult();

		@Source("arrow_redo.png")
		ImageResource resubmit();
		@Source("refresh.png")
		ImageResource refresh();
		@Source("computationIcon.png")
		ImageResource computationIcon();

	
}