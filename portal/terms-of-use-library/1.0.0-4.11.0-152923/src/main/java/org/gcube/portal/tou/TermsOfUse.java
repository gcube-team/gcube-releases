package org.gcube.portal.tou;

import org.gcube.portal.tou.exceptions.ToUNotFoundException;
import org.gcube.portal.tou.model.ToU;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * Terms of Use Interface (ToU).
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface TermsOfUse {
	
	/**
	 * Check whether the user having userid has accepted the ToU in group with id groupId
	 * @param userid
	 * @param groupId
	 * @return
	 * @throws NumberFormatException
	 * @throws UserRetrievalFault
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	boolean hasAcceptedToU(String userid, long groupId) throws NumberFormatException, UserRetrievalFault, UserManagementSystemException, GroupRetrievalFault;
	
	/**
	 * Check whether the user having userid has accepted the ToU in group with id groupId and return the id of the accepted ToU
	 * @param userid
	 * @param groupId
	 * @return
	 * @throws NumberFormatException
	 * @throws UserRetrievalFault
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	Long hasAcceptedToUVersion(String userid, long groupId) throws NumberFormatException, UserRetrievalFault, UserManagementSystemException, GroupRetrievalFault;
	
	/**
	 * Set as accepted the ToU of group with id groupId for user userid (the id of the ToU is the last available)
	 * @param userid
	 * @param groupId
	 * @throws ToUNotFoundException
	 * @throws PortalException
	 * @throws SystemException
	 * @throws UserRetrievalFault
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	void setAcceptedToU(String userid, long groupId) throws ToUNotFoundException, PortalException, SystemException, UserRetrievalFault, UserManagementSystemException, GroupRetrievalFault;
	
	/**
	 * Retrieve the ToU for group with id groupId 
	 * @param groupName
	 * @return
	 * @throws ToUNotFoundException
	 * @throws GroupRetrievalFault
	 * @throws UserManagementSystemException
	 * @throws PortalException
	 * @throws SystemException
	 */
	ToU getToUGroup(long groupId) throws ToUNotFoundException, GroupRetrievalFault, UserManagementSystemException, PortalException, SystemException;

}
