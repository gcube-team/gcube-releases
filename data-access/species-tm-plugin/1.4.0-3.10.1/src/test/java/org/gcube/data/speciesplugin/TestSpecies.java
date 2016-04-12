package org.gcube.data.speciesplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.speciesplugin.store.SpeciesNeoStore;
import org.gcube.data.speciesplugin.store.SpeciesStore;
import org.gcube.data.speciesplugin.utils.SpeciesService;
import org.gcube.data.trees.patterns.AnyPattern;

public class TestSpecies {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//		GCUBEScope scope = GCUBEScope.getScope("/gcube/devsec");

		ScopeProvider.instance.set("/gcube/devsec");

		File storeLocation = new File("store");
		String storeId = "MyStore";

		SpeciesStore store = new SpeciesNeoStore(storeId);

		store.start(storeLocation); 

		
		SpeciesService service = new SpeciesService(store);
		List<String> scientificNames = new ArrayList<String>();

		scientificNames.add("Para");
		List<String> dataSource = new ArrayList<String>();

		//dataSource.add("IRMNG");
		//dataSource.add("ITIS");
		dataSource.add("Obis");

		service.createCollection(scientificNames, dataSource);
		
		
		store.get("ITIS:687575", new AnyPattern());
		
		store.stop();

	}

}
