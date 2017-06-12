package org.gcube.dataanalysis.ecoengine.spatialdistributions;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;

public class AquamapsNative extends AquamapsSuitable {

	@Override
	public String getDescription() {
		return "Algorithm for Native Distribution by AquaMaps. A distribution algorithm that generates a table containing  species distribution probabilities on half-degree cells according to the AquaMaps approach for Native (Actual) distributions.";
	}
	
	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS [] p = {ALG_PROPS.SPECIES_VS_CSQUARE_FROM_DATABASE, ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON};
//		ALG_PROPS [] p = {ALG_PROPS.SPECIES_VS_CSQUARE_FROM_DATABASE};
		return p;
	}
	
	@Override
	// to overwrite in case of native generation in order to filer on the probabilities types
	public Queue<String> filterProbabilitySet(Queue<String> probabiltyRows) {

		ConcurrentLinkedQueue<String> speciesCriteria1 = new ConcurrentLinkedQueue<String>();
		ConcurrentLinkedQueue<String> speciesCriteria2 = new ConcurrentLinkedQueue<String>();
		ConcurrentLinkedQueue<String> speciesCriteria3 = new ConcurrentLinkedQueue<String>();
		int size = probabiltyRows.size();
		for (int i = 0; i < size; i++) {
			String rowString = probabiltyRows.poll();
			if ((rowString != null) && (rowString.length() > 0)) {
				String[] probabilityRows = rowString.split(",");
				short Inbox = Short.parseShort(probabilityRows[3].replace("'",""));
				short InFAO = Short.parseShort(probabilityRows[4].replace("'",""));
				if ((Inbox == 1) && (InFAO == 1)) {
					speciesCriteria1.offer(rowString);
				} else if ((Inbox == 0) && (InFAO == 1)) {
					speciesCriteria2.offer(rowString);
				} else if ((Inbox == 1) && (InFAO == 0)) {
					speciesCriteria3.offer(rowString);
				}
			}
		}
		
		if (speciesCriteria1.size()>0)
			return speciesCriteria1;
		else if (speciesCriteria2.size()>0)
			return speciesCriteria2;
		else if (speciesCriteria3.size()>0)
			return speciesCriteria3;
		else
			return new ConcurrentLinkedQueue<String>();
	}

	@Override
	public boolean isSynchronousProbabilityWrite() {
		return true;
	}
	
	@Override
	public String getName() {
		return "AQUAMAPS_NATIVE";
	}
}
