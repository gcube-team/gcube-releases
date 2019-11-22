/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util.database.data;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant.BATCH_LEVEL;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.Species;


/**
 * The Class SpeciesData.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 17, 2019
 */
public class SpeciesData {


	/**
	 * Gets the list species.
	 *
	 * @param batchType the batch type
	 * @return the list species
	 */
	public static ArrayList<Species> getListSpecies(PopulationType batchType){

		List<Species> listSpecies = new ArrayList<Species>();
		if(batchType.getName().startsWith(PerformFishAnalyticsConstant.BATCH_LEVEL.PRE_ONGROWING.name()) || batchType.getName().startsWith(BATCH_LEVEL.PRE_ONGROWING_CLOSED_BATCHES.name())){

			listSpecies.add(new Species(java.util.UUID.randomUUID().toString(), "S. aurata", "", batchType));
			listSpecies.add(new Species(java.util.UUID.randomUUID().toString(), "D. labrax", "", batchType));

		}else{

			listSpecies.add(new Species(java.util.UUID.randomUUID().toString(), "S.aurata", "", batchType));
			listSpecies.add(new Species(java.util.UUID.randomUUID().toString(), "D.labrax", "", batchType));
		}

		return (ArrayList<Species>) listSpecies;
	}
}
