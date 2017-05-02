package org.gcube.portlets.widgets.widgettour.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * This interface contains the functions used to communicate with the tour-manager library.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@RemoteServiceRelativePath("tourmanagerservice")
public interface TourManagerServices extends RemoteService {
	
	/**
	 * Set the action to take for this tour.
	 * @param callerIdentifier
	 * @param versionNumber
	 * @param show
	 * @return a boolean value that tells if this tour can be shown or not
	 */
	boolean setShowNextTime(String callerIdentifier, int versionNumber, boolean show);
	
	/**
	 * Check if this tour can be shown or not.
	 * @return
	 */
	boolean isTourShowable(String callerIdentifier, int versionNumber);

}
