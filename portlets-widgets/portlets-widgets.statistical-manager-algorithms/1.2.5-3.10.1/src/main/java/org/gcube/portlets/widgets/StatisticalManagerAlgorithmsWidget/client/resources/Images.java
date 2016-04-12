package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.resources;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.Services;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;




public class Images {

	
	
	public static Map<String,ImageResource> baseImagesMap=new HashMap<String,ImageResource>();
	public static Map<String,ImageResource> categoryImagesMap=new HashMap<String,ImageResource>();
	public static Map<String,ImageResource> operatorImagesMap=new HashMap<String,ImageResource>();
	public static Map<String,ImageResource> provenanceImagesMap=new HashMap<String,ImageResource>();
	public static Map<String,ImageResource> templateImagesMap=new HashMap<String,ImageResource>();
	
	static{
		baseImagesMap.put("fileIcon",Services.getResources().fileIcon());
		baseImagesMap.put("progress-gray",Services.getResources().progressGray());
		baseImagesMap.put("progress-green",Services.getResources().progressGreen());
		baseImagesMap.put("progress-red",Services.getResources().progressRed());
		baseImagesMap.put("triangle-down",Services.getResources().triangleDown());
		baseImagesMap.put("triangle-right",Services.getResources().triangleRight());
		
		
		categoryImagesMap.put("DEFAULT_IMAGE", Services.getResources().categoryDefaultImage());
		categoryImagesMap.put("DISTRIBUTIONS", Services.getResources().categoryDistributions());
		
		operatorImagesMap.put("AQUAMAPS_NATIVE_2050", Services.getResources().aquamapsNative2050Operator());
		operatorImagesMap.put("AQUAMAPS_NATIVE_NEURALNETWORK", Services.getResources().aquamapsNativeNeuralOperator());
		operatorImagesMap.put("AQUAMAPS_NATIVE", Services.getResources().aquamapsNativeOperator());
		operatorImagesMap.put("AQUAMAPS_NEURAL_NETWORK_NS", Services.getResources().aquamapsNeuralOperator());
		operatorImagesMap.put("AQUAMAPS_SUITABLE_2050", Services.getResources().aquamapsSuitable2050Operator());
		operatorImagesMap.put("AQUAMAPS_SUITABLE_NEURALNETWORK", Services.getResources().aquamapsSuitableNeuralOperator());
		operatorImagesMap.put("AQUAMAPSNN", Services.getResources().aquamapsNNOperator());
		operatorImagesMap.put("AQUAMAPSNNNS", Services.getResources().aquamapsNNNSOperator());
		operatorImagesMap.put("DEFAULT_IMAGE", Services.getResources().operatorsDefaultImage());
		operatorImagesMap.put("DEFAULT_IMAGEold", Services.getResources().operatorsOldDefaultImage());
		operatorImagesMap.put("HSPEN", Services.getResources().operatorsHSPEN());
		operatorImagesMap.put("REMOTE_AQUAMAPS_NATIVE_2050", Services.getResources().remoteAquamapsNative2050Operator());
		operatorImagesMap.put("REMOTE_AQUAMAPS_NATIVE", Services.getResources().remoteAquamapsNativeOperator());
		operatorImagesMap.put("REMOTE_AQUAMAPS_SUITABLE_2050", Services.getResources().remoteAquamapsSuitable2050Operator());
		operatorImagesMap.put("REMOTE_AQUAMAPS_SUITABLE", Services.getResources().remoteAquamapsSuitableOperator());
		
		provenanceImagesMap.put("provenanceCOMPUTED",Services.getResources().computedProvenance());
		provenanceImagesMap.put("provenanceCOMPUTED2",Services.getResources().computedProvenance2());
		provenanceImagesMap.put("provenanceIMPORTED",Services.getResources().importedProvenance());
		provenanceImagesMap.put("provenanceIMPORTED2",Services.getResources().importedProvenance2());
		provenanceImagesMap.put("provenanceSYSTEM",Services.getResources().systemProvenance());
		provenanceImagesMap.put("provenanceSYSTEM2",Services.getResources().systemProvenance2());
		
		templateImagesMap.put("CLUSTER", Services.getResources().clusterTemplate());
		templateImagesMap.put("FILE", Services.getResources().fileTemplate());
		templateImagesMap.put("GENERIC", Services.getResources().genericTemplate());
		templateImagesMap.put("HCAF", Services.getResources().hcafTemplate());
		templateImagesMap.put("HSPEC", Services.getResources().hspecTemplate());
		templateImagesMap.put("HSPEN", Services.getResources().hspenTemplate());
		templateImagesMap.put("MINMAXLAT", Services.getResources().minMaxLatTemplate());
		templateImagesMap.put("OCCURRENCE_AQUAMAPS", Services.getResources().occurrenceAMTemplate());
		templateImagesMap.put("OCCURRENCE_SPECIES", Services.getResources().occurrenceSpeciesTemplate());
		templateImagesMap.put("TESTSET", Services.getResources().testSetTemplate());
		templateImagesMap.put("TIME_SERIES", Services.getResources().time_SeriesTemplate());
		templateImagesMap.put("TIMESERIES", Services.getResources().timeSeriesTemplate());
		templateImagesMap.put("TRAININGSET", Services.getResources().trainingSetTemplate());
	}
	
	
	public static AbstractImagePrototype asPrototype(ImageResource res){
		return AbstractImagePrototype.create(res);
	}

	public static AbstractImagePrototype computationIcon() {
		return AbstractImagePrototype.create(Services.getResources().computationIcon());
	}

	public static AbstractImagePrototype inputSpaceIcon() {
		return AbstractImagePrototype.create(Services.getResources().inputSpaceIcon());
	}

	public static AbstractImagePrototype addOperator() {
		return AbstractImagePrototype.create(Services.getResources().addOperator());
	}

	public static AbstractImagePrototype startComputation() {
		return AbstractImagePrototype.create(Services.getResources().startComputation());
	}
	
	public static AbstractImagePrototype removeAll() {
		return AbstractImagePrototype.create(Services.getResources().removeAll());
	}

	public static AbstractImagePrototype showAllOperators() {
		return AbstractImagePrototype.create(Services.getResources().sortAscending());
	}

	public static AbstractImagePrototype showCategories() {
		return AbstractImagePrototype.create(Services.getResources().tree());
	}

	public static AbstractImagePrototype folderExplore() {
		return AbstractImagePrototype.create(Services.getResources().folderExplore());
	}

	public static AbstractImagePrototype cancel() {
		return AbstractImagePrototype.create(Services.getResources().cancel());
	}

	public static AbstractImagePrototype addl() {
		return AbstractImagePrototype.create(Services.getResources().add());
	}

	public static AbstractImagePrototype table() {
		return AbstractImagePrototype.create(Services.getResources().table());
	}

	public static AbstractImagePrototype refresh() {
		return AbstractImagePrototype.create(Services.getResources().refresh());
	}

	public static AbstractImagePrototype fileDownload() {
		return AbstractImagePrototype.create(Services.getResources().fileDownload());
	}

	public static AbstractImagePrototype loader() {
		return AbstractImagePrototype.create(Services.getResources().loader());
	}

	public static AbstractImagePrototype save() {
		return AbstractImagePrototype.create(Services.getResources().save());
	}

	public static AbstractImagePrototype expand() {
		return AbstractImagePrototype.create(Services.getResources().expand());
	}

	public static AbstractImagePrototype collapse() {
		return AbstractImagePrototype.create(Services.getResources().collapse());
	}

	public static AbstractImagePrototype groupBy() {
		return AbstractImagePrototype.create(Services.getResources().groupBy());
	}
	
	public static AbstractImagePrototype map() {
		return AbstractImagePrototype.create(Services.getResources().map());
	}
	
	public static AbstractImagePrototype userPerspective() {
		return AbstractImagePrototype.create(Services.getResources().userPerspective());
	}
	
	public static AbstractImagePrototype computationPerspective() {
		return AbstractImagePrototype.create(Services.getResources().computationPerspective());
	}
	
}
