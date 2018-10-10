package org.gcube.portlets.widgets.dataminermanagerwidget.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface Resources extends ClientBundle {
	@Source("DataMiner.css")
	CssResource dataMinerCSS();

	@Source("WikiLink.txt")
	ExternalTextResource wikiLink();

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

	@Source("draw-geometry.png")
	ImageResource drawGeometry();

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

	@Source("menuItemHelp.png")
	ImageResource menuItemHelp();

	@Source("ajax-loader-big.gif")
	ImageResource loaderBig();

	@Source("ajax-complete.gif")
	ImageResource loadingComplete();

	// @Source("save.png")
	// ImageResource save();

	@Source("user_green.png")
	ImageResource userPerspective();

	@Source("monitor.png")
	ImageResource computationPerspective();

	@Source("download.png")
	ImageResource download();
	
	@Source("show.png")
	ImageResource netcdf();

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

	@Source("table.png")
	ImageResource tabularResource();

	@Source("triangle-down.png")
	ImageResource triangleDown();

	@Source("triangle-right.png")
	ImageResource triangleRight();

	// *********************** CATEGORIES

	@Source("categories/DEFAULT_IMAGE.png")
	ImageResource categoryDefaultImage();

	@Source("categories/DISTRIBUTIONS.png")
	ImageResource categoryDistributions();

	// ************************ OPERATORS

	@Source("operators/AQUAMAPS_NATIVE_2050.png")
	ImageResource operatorsAquamapsNative2050Operator();

	@Source("operators/AQUAMAPS_NATIVE_NEURALNETWORK.png")
	ImageResource operatorsAquamapsNativeNeuralOperator();

	@Source("operators/AQUAMAPS_NATIVE.png")
	ImageResource operatorsAquamapsNativeOperator();

	@Source("operators/AQUAMAPS_NEURAL_NETWORK_NS.png")
	ImageResource operatorsAquamapsNeuralOperator();

	@Source("operators/AQUAMAPS_SUITABLE_2050.png")
	ImageResource operatorsAquamapsSuitable2050Operator();

	@Source("operators/AQUAMAPS_SUITABLE_NEURALNETWORK.png")
	ImageResource operatorsAquamapsSuitableNeuralOperator();

	@Source("operators/AQUAMAPS_SUITABLE.png")
	ImageResource operatorsAquamapsSuitableOperator();

	@Source("operators/AQUAMAPSNN.png")
	ImageResource operatorsAquamapsNNOperator();

	@Source("operators/AQUAMAPSNNNS.png")
	ImageResource operatorsAquamapsNNNSOperator();

	@Source("operators/DEFAULT_IMAGE.png")
	ImageResource operatorsDefaultImage();

	@Source("operators/DEFAULT_IMAGEold.png")
	ImageResource operatorsOldDefaultImage();

	@Source("operators/HSPEN.png")
	ImageResource operatorsHSPEN();

	@Source("operators/REMOTE_AQUAMAPS_NATIVE_2050.png")
	ImageResource operatorsRemoteAquamapsNative2050Operator();

	@Source("operators/REMOTE_AQUAMAPS_NATIVE.png")
	ImageResource operatorsRemoteAquamapsNativeOperator();

	@Source("operators/REMOTE_AQUAMAPS_SUITABLE_2050.png")
	ImageResource operatorsRemoteAquamapsSuitable2050Operator();

	@Source("operators/REMOTE_AQUAMAPS_SUITABLE.png")
	ImageResource operatorsRemoteAquamapsSuitableOperator();

	// *************************** TEMPLATE

	@Source("templateIcons/CLUSTER.png")
	ImageResource clusterTemplate();

	@Source("templateIcons/FILE.png")
	ImageResource fileTemplate();

	@Source("templateIcons/GENERIC.png")
	ImageResource genericTemplate();

	@Source("templateIcons/HCAF.png")
	ImageResource hcafTemplate();

	@Source("templateIcons/HSPEC.png")
	ImageResource hspecTemplate();

	@Source("templateIcons/HSPEN.png")
	ImageResource hspenTemplate();

	@Source("templateIcons/MINMAXLAT.png")
	ImageResource minMaxLatTemplate();

	@Source("templateIcons/OCCURRENCE_AQUAMAPS.png")
	ImageResource occurrenceAMTemplate();

	@Source("templateIcons/OCCURRENCE_SPECIES.png")
	ImageResource occurrenceSpeciesTemplate();

	@Source("templateIcons/TESTSET.png")
	ImageResource testSetTemplate();

	@Source("templateIcons/TIME_SERIES.png")
	ImageResource time_SeriesTemplate();

	@Source("templateIcons/TIMESERIES.png")
	ImageResource timeSeriesTemplate();

	@Source("templateIcons/TRAININGSET.png")
	ImageResource trainingSetTemplate();

}
