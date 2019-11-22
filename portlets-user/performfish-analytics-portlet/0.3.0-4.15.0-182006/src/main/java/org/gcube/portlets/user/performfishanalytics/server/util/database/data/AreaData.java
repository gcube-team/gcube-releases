/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util.database.data;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.shared.Area;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;


/**
 * The Class AreaData.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 15, 2019
 */
public class AreaData {

	/**
	 * Gets the list area.
	 *
	 * @param batchType the batch type
	 * @return the list area
	 */
	public static ArrayList<Area> getListArea(PopulationType batchType){

		List<Area> listArea = new ArrayList<Area>();
		listArea.add(new Area(java.util.UUID.randomUUID().toString(), "Zone 0", "", batchType));
		listArea.add(new Area(java.util.UUID.randomUUID().toString(), "Zone 1", "", batchType));
		listArea.add(new Area(java.util.UUID.randomUUID().toString(), "Zone 2", "", batchType));
		listArea.add(new Area(java.util.UUID.randomUUID().toString(), "Zone 3", "", batchType));
		return (ArrayList<Area>) listArea;
	}
}
