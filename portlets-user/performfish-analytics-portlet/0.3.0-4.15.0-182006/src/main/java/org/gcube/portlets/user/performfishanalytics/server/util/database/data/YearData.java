package org.gcube.portlets.user.performfishanalytics.server.util.database.data;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.Year;

/**
 * The Class YearData.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * May 6, 2019
 */
public class YearData {
	

	/**
	 * Gets the list years.
	 *
	 * @param batchType the batch type
	 * @return the list years
	 */
	public static ArrayList<Year> getListYears(PopulationType batchType){

		List<Year> listSpecies = new ArrayList<Year>();
		listSpecies.add(new Year(java.util.UUID.randomUUID().toString(), "2016", batchType));
		listSpecies.add(new Year(java.util.UUID.randomUUID().toString(), "2017", batchType));
		listSpecies.add(new Year(java.util.UUID.randomUUID().toString(), "2018", batchType));

		return (ArrayList<Year>) listSpecies;
	}

}
