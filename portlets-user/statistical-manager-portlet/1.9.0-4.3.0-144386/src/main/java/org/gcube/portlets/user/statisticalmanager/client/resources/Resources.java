package org.gcube.portlets.user.statisticalmanager.client.resources;


import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {
	
	@Source("logo.png")
	ImageResource logo();

	@Source("logoLittle.png")
	ImageResource logoLittle();
	
	@Source("goBack.png")
	ImageResource goBack();

	@Source("computation.png")
	ImageResource computationIcon();

	@Source("computations.png")
	ImageResource jobsIcon();

	@Source("inputSpace.png")
	ImageResource inputSpaceIcon();

	@Source("application_side_expand.png")
	ImageResource addOperator();
	
	@Source("connector1.png")
	ImageResource workflowConnector1();

	@Source("connector2.png")
	ImageResource workflowConnector2();

	@Source("triangle.png")
	ImageResource startComputation();

	@Source("delete.png")
	ImageResource removeAll();

	@Source("sort_asc.gif")
	ImageResource sortAscending();

	@Source("tree.gif")
	ImageResource tree();

	@Source("folder_explore.png")
	ImageResource folderExplore();

	@Source("cancel_icon.png")
	ImageResource cancel();

	@Source("add.png")
	ImageResource add();

	@Source("table.png")
	ImageResource table();

	@Source("refresh.png")
	ImageResource refresh();

	@Source("details.png")
	ImageResource details();

	@Source("tableResult.png")
	ImageResource tableResult();

	@Source("menuItemComputations.png")
	ImageResource menuItemComputations();
	
	@Source("menuItemExperiment.png")
	ImageResource menuItemExperiment();
	
	@Source("menuItemDataspace.png")
	ImageResource menuItemInputspace();

	@Source("table.png")
	ImageResource fileDownload(); // TODO change

	@Source("ajax-loader.gif")
	ImageResource loader();
	
	@Source("ajax-loader-big.gif")
	ImageResource loaderBig();
	
	@Source("ajax-complete.gif")
	ImageResource loadingComplete();
	
	@Source("inputSpaceImporter.png")
	ImageResource inputSpaceImporter();
	
	@Source("inputSpaceMonitor.png")
	ImageResource inputSpaceMonitor();

	@Source("save.png")
	ImageResource save();

	@Source("alert.png")
	ImageResource error();
	
	@Source("arrow_out.png")
	ImageResource expand();
	
	@Source("arrow_in.png")
	ImageResource collapse();
	
	@Source("application_view_list.png")
	ImageResource groupBy();

	@Source("map.png")
	ImageResource map();
	
	@Source("user_green.png")
	ImageResource userPerspective();
	
	@Source("monitor.png")
	ImageResource computationPerspective();

	@Source("arrow_redo.png")
	ImageResource resubmit();

}
