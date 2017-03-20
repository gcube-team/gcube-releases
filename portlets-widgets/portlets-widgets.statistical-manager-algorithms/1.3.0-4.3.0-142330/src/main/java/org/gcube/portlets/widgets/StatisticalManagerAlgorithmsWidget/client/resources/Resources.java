package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;




public interface Resources extends ClientBundle {
	
	public static final Resources INSTANCE = GWT.create(Resources.class);
	
		
	@Source("css/StatisticalManagerAlgorithmsWidget.css")
	public CssResource css();

	@Source("images/general/computation.png")
	ImageResource computationIcon();

	@Source("images/general/computations.png")
	ImageResource jobsIcon();

	@Source("images/general/inputSpace.png")
	ImageResource inputSpaceIcon();

	@Source("images/general/application_side_expand.png")
	ImageResource addOperator();
	
	@Source("images/general/connector1.png")
	ImageResource workflowConnector1();

	@Source("images/general/connector2.png")
	ImageResource workflowConnector2();

	@Source("images/general/triangle.png")
	ImageResource startComputation();

	@Source("images/general/delete.png")
	ImageResource removeAll();

	@Source("images/general/sort_asc.gif")
	ImageResource sortAscending();

	@Source("images/general/tree.gif")
	ImageResource tree();

	@Source("images/general/folder_explore.png")
	ImageResource folderExplore();

	@Source("images/general/cancel_icon.png")
	ImageResource cancel();

	@Source("images/general/add.png")
	ImageResource add();

	@Source("images/general/table.png")
	ImageResource table();

	@Source("images/general/refresh.png")
	ImageResource refresh();

	@Source("images/general/details.png")
	ImageResource details();

	@Source("images/general/tableResult.png")
	ImageResource tableResult();

	@Source("images/general/menuItemComputations.png")
	ImageResource menuItemComputations();
	
	@Source("images/general/menuItemExperiment.png")
	ImageResource menuItemExperiment();
	
	@Source("images/general/menuItemDataspace.png")
	ImageResource menuItemInputspace();

	@Source("images/general/table.png")
	ImageResource fileDownload(); // TODO change

	@Source("images/general/ajax-loader.gif")
	ImageResource loader();
	
	@Source("images/general/ajax-loader-big.gif")
	ImageResource loaderBig();
	
	@Source("images/general/ajax-complete.gif")
	ImageResource loadingComplete();
	
	@Source("images/general/inputSpaceImporter.png")
	ImageResource inputSpaceImporter();
	
	@Source("images/general/inputSpaceMonitor.png")
	ImageResource inputSpaceMonitor();

	@Source("images/general/save.png")
	ImageResource save();

	@Source("images/general/alert.png")
	ImageResource error();
	
	@Source("images/general/arrow_out.png")
	ImageResource expand();
	
	@Source("images/general/arrow_in.png")
	ImageResource collapse();
	
	@Source("images/general/application_view_list.png")
	ImageResource groupBy();

	@Source("images/general/map.png")
	ImageResource map();
	
	@Source("images/general/user_green.png")
	ImageResource userPerspective();
	
	@Source("images/general/monitor.png")
	ImageResource computationPerspective();

	@Source("images/general/arrow_redo.png")
	ImageResource resubmit();
	
//	*********************** CATEGORIES
	
	@Source("images/categories/DEFAULT_IMAGE.png")
	ImageResource categoryDefaultImage();
	
	@Source("images/categories/DISTRIBUTIONS.png")
	ImageResource categoryDistributions();
	
//	************************ OPERATORS
	
	@Source("images/operators/AQUAMAPS_NATIVE_2050.png")
	ImageResource aquamapsNative2050Operator();
	@Source("images/operators/AQUAMAPS_NATIVE_NEURALNETWORK.png")
	ImageResource aquamapsNativeNeuralOperator();
	@Source("images/operators/AQUAMAPS_NATIVE.png")
	ImageResource aquamapsNativeOperator();
	@Source("images/operators/AQUAMAPS_NEURAL_NETWORK_NS.png")
	ImageResource aquamapsNeuralOperator();
	
	@Source("images/operators/AQUAMAPS_SUITABLE_2050.png")
	ImageResource aquamapsSuitable2050Operator();
	@Source("images/operators/AQUAMAPS_SUITABLE_NEURALNETWORK.png")
	ImageResource aquamapsSuitableNeuralOperator();
	@Source("images/operators/AQUAMAPS_SUITABLE.png")
	ImageResource aquamapsSuitableOperator();
	
	@Source("images/operators/AQUAMAPSNN.png")
	ImageResource aquamapsNNOperator();
	
	@Source("images/operators/AQUAMAPSNNNS.png")
	ImageResource aquamapsNNNSOperator();
	
	@Source("images/operators/DEFAULT_IMAGE.png")
	ImageResource operatorsDefaultImage();
	
	@Source("images/operators/DEFAULT_IMAGEold.png")
	ImageResource operatorsOldDefaultImage();
	
	@Source("images/operators/HSPEN.png")
	ImageResource operatorsHSPEN();
	
	@Source("images/operators/REMOTE_AQUAMAPS_NATIVE_2050.png")
	ImageResource remoteAquamapsNative2050Operator();
	@Source("images/operators/REMOTE_AQUAMAPS_NATIVE.png")
	ImageResource remoteAquamapsNativeOperator();
	
	@Source("images/operators/REMOTE_AQUAMAPS_SUITABLE_2050.png")
	ImageResource remoteAquamapsSuitable2050Operator();
	@Source("images/operators/REMOTE_AQUAMAPS_SUITABLE.png")
	ImageResource remoteAquamapsSuitableOperator();
	
//	************************** PROVENANCES
	
	@Source("images/provenances/provenanceCOMPUTED.png")
	ImageResource computedProvenance();
	@Source("images/provenances/provenanceCOMPUTED2.png")
	ImageResource computedProvenance2();
	@Source("images/provenances/provenanceIMPORTED.png")
	ImageResource importedProvenance();
	@Source("images/provenances/provenanceIMPORTED2.png")
	ImageResource importedProvenance2();
	@Source("images/provenances/provenanceSYSTEM.png")
	ImageResource systemProvenance();
	@Source("images/provenances/provenanceSYSTEM2.png")
	ImageResource systemProvenance2();
	
//	***************************	TEMPLATE
	
	
	@Source("images/templates/CLUSTER.png")
	ImageResource clusterTemplate();
	@Source("images/templates/FILE.png")
	ImageResource fileTemplate();
	@Source("images/templates/GENERIC.png")
	ImageResource genericTemplate();
	@Source("images/templates/HCAF.png")
	ImageResource hcafTemplate();
	@Source("images/templates/HSPEC.png")
	ImageResource hspecTemplate();
	@Source("images/templates/HSPEN.png")
	ImageResource hspenTemplate();
	@Source("images/templates/MINMAXLAT.png")
	ImageResource minMaxLatTemplate();
	@Source("images/templates/OCCURRENCE_AQUAMAPS.png")
	ImageResource occurrenceAMTemplate();
	@Source("images/templates/OCCURRENCE_SPECIES.png")
	ImageResource occurrenceSpeciesTemplate();
	@Source("images/templates/TESTSET.png")
	ImageResource testSetTemplate();
	@Source("images/templates/TIME_SERIES.png")
	ImageResource time_SeriesTemplate();
	@Source("images/templates/TIMESERIES.png")
	ImageResource timeSeriesTemplate();
	@Source("images/templates/TRAININGSET.png")
	ImageResource trainingSetTemplate();
	
// 	****************************** NO CATEGORY
	
	@Source("images/fileIcon.png")
	ImageResource fileIcon();
	
	@Source("images/progress-gray.gif")
	ImageResource progressGray();
	
	@Source("images/progress-green.gif")
	ImageResource progressGreen();
	
	@Source("images/progress-red.gif")
	ImageResource progressRed();
	
	@Source("images/triangle-down.png")
	ImageResource triangleDown();
	
	@Source("images/triangle-right.png")
	ImageResource triangleRight();
	
}
