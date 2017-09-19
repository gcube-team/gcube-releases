package org.gcube.portal.TourManager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * The tour manager library interface.
 * Please note that in order to handle guided tours, liferay's custom fields are used.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface TourManager {

	/**This function is invoked to request a specific action to take (@see the <b>show</b> value) the next time the user will see the tour uniquely identified
	 * by the couple {callerIdentifier, versionNumber}. 
	 * @param callerIdentifier is the unique identifier of the portlet (e.g. its name or the name of the servlet it contains, if any)
	 * @param versionNumber is the unique version number for the tour
	 * @param show a boolean value to show or not the tour
	 * @param currentUser the user name of the user involved.
	 */
	public void setShowNextTime(String callerIdentifier, int versionNumber, boolean show, String currentUser);

	/**
	 * Check if a given tour can be shown or not.
	 * @param callerIdentifier is the unique identifier of the portlet (e.g. the name of the portlet or the name of the servlet it contains, if any)
	 * @param versionNumber is the unique version number for the tour
	 * @param currentUser the user name of the user involved.
	 * @return <b>true</b> if it can be shown, <b>false</b> otherwise
	 */
	public boolean isTourShowable(String callerIdentifier, int versionNumber, String currentUser) throws PortalException, SystemException, Exception;

}
