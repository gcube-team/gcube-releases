package org.gcube.portlets.user.dataminermanager.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface Resources extends ClientBundle {

	@Source("logoLittle.png")
	ImageResource logoLittle();

	@Source("goBack.png")
	ImageResource goBack();

	@Source("computation.png")
	ImageResource executionIcon();

	@Source("computations.png")
	ImageResource computationsIcon();

	@Source("inputSpace.png")
	ImageResource inputSpaceIcon();

	@Source("connector1.png")
	ImageResource workflowConnector1();

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

	@Source("menuItemComputations.png")
	ImageResource menuItemComputations();

	@Source("menuItemExperiment.png")
	ImageResource menuItemExperiment();

	@Source("menuItemDataspace.png")
	ImageResource menuItemInputspace();

	@Source("ajax-loader-big.gif")
	ImageResource loaderBig();

	@Source("ajax-complete.gif")
	ImageResource loadingComplete();

	//@Source("save.png")
	//ImageResource save();

	@Source("user_green.png")
	ImageResource userPerspective();

	@Source("monitor.png")
	ImageResource computationPerspective();

	@Source("download.png")
	ImageResource download();
	
	@Source("cancel_circle.png")
	ImageResource cancelCircle();
	
	@Source("delete_circle.png")
	ImageResource deleteCircle();
	
	@Source("refresh.png")
	ImageResource refresh();
	
	@Source("resubmit.png")
	ImageResource resubmit();

	@Source("show.png")
	ImageResource show();
	
}
