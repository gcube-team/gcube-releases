package org.gcube.dataanalysis.ecoengine.spatialdistributions;

import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.hibernate.SessionFactory;


public class AquamapsSuitable2050 extends AquamapsSuitable {
	
	public void init(AlgorithmConfiguration config,SessionFactory dbHibConnection) {
		super.init(config, dbHibConnection);
		type = "2050";
	}
	
	@Override
	public ALG_PROPS[] getProperties() {
//		ALG_PROPS [] p = {ALG_PROPS.SPECIES_VS_CSQUARE_FROM_DATABASE};
		ALG_PROPS [] p = {ALG_PROPS.SPECIES_VS_CSQUARE_FROM_DATABASE, ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON};
		return p;
	}
	
	@Override
	public String getName() {
		return "AQUAMAPS_SUITABLE_2050";
	}
	
	@Override
	public String getDescription() {
		return "Algorithm for Suitable 2050 Distribution by AquaMaps. A distribution algorithm that generates a table containing  species distribution probabilities on half-degree cells according to the AquaMaps approach for suitable (potential) distributions for the 2050 scenario.";
	}
}
