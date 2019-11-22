/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.persistence;

import javax.persistence.PostRemove;

import org.gcube.portlets.user.performfishanalytics.shared.Population;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The listener interface for receiving packageEntity events.
 * The class that is interested in processing a packageEntity
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPackageEntityListener<code> method. When
 * the packageEntity event occurs, that object's appropriate
 * method is invoked.
 *
 * @see PackageEntityEvent
 */
public class PopulationEntityListener {

	protected static Logger logger = LoggerFactory.getLogger(PopulationEntityListener.class);

	/**
	 * On post remove.
	 *
	 * @param entity the entity
	 */
	@PostRemove void onPostRemove(Population entity) {
//		System.out.println("onPostRemove "+entity);
		logger.trace("onPostRemove Package: "+entity.getInternalId());
		decrementPackages(entity);
	}

	/**
	 * Decrement packages.
	 *
	 * @param pck the pck
	 */
	private void decrementPackages(Population pck){
	}
}
