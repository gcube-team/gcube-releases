package org.gcube.spatial.data.geonetwork;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.gcube.spatial.data.geonetwork.model.Group;
import org.gcube.spatial.data.geonetwork.model.User;
import org.gcube.spatial.data.geonetwork.model.User.Profile;
import org.gcube.spatial.data.geonetwork.model.faults.AuthorizationException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingServiceEndpointException;

public interface GeoNetworkAdministration extends GeoNetworkPublisher {

	public void createGroup(String name, String description, String mail) throws GNLibException, GNServerException,MissingServiceEndpointException;
	public Set<Group> getGroups() throws GNLibException, GNServerException,MissingServiceEndpointException;
	public Set<User>  getUsers() throws GNLibException, GNServerException,MissingServiceEndpointException;
	public void createUsers(String username, String password, Profile profile,
			Collection<Integer> groups) throws GNLibException,
			GNServerException, MissingServiceEndpointException;
	public void assignOwnership(List<Long> toTransferIds,Integer targetUserId, Integer targetGroupId) throws AuthorizationException, GNServerException, MissingServiceEndpointException, GNLibException;
	
	public String getAvailableOwnershipTransfer(Integer userId)throws GNServerException, MissingServiceEndpointException, GNLibException;
	public String getMetadataOwners()throws GNServerException, MissingServiceEndpointException, GNLibException;
	public void transferOwnership(Integer sourceUserId,Integer sourceGroupId,Integer targetUserId,Integer targetGroupId) throws GNServerException, MissingServiceEndpointException, GNLibException;
}
