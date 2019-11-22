package org.gcube.spatial.data.sdi.engine.impl.gn.extension;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.faults.gn.MetadataNotFoundException;
import org.gcube.spatial.data.sdi.model.gn.Group;
import org.gcube.spatial.data.sdi.model.gn.User;
import org.gcube.spatial.data.sdi.model.gn.User.Profile;
import org.gcube.spatial.data.sdi.utils.StringUtils;

import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchRequest.Config;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoNetworkUtils {

	/**
	 * Adds a suffix to groupName if needed
	 * 
	 * @param existing
	 * @param groupName
	 * @return
	 */
	public static Group generateGroup(Set<Group> existing, String groupName, String description, String contactMail){
		Set<String> existingNames=new HashSet<>();
		int maxId=0;
		for(Group g:existing){
			existingNames.add(g.getName());
			if(maxId<g.getId())maxId=g.getId();
		}
		
		String toUseName=clashSafeString(groupName,existingNames);
		Integer maxLength=Integer.parseInt(LocalConfiguration.getProperty(LocalConfiguration.GEONETWORK_GROUP_MAX_LENGTH, "30"));
		if(toUseName.length()>maxLength)
			return generateGroup(existing, toUseName.substring(0, maxLength-2), description, contactMail);
		return new Group(toUseName, description, contactMail, maxId+1);		
	}
	
	
	public static User generateUser(Set<User> existing, Integer passwordLength, String username){
		Set<String> existingNames=new HashSet<>();
		for(User g:existing)existingNames.add(g.getUsername());
		
		String toUseUserName=clashSafeString(username,existingNames);
		
		return new User(0, // NB will be updated when creating it..
				toUseUserName,
				StringUtils.generateRandomString(passwordLength),Profile.Reviewer);	
	}
	
		
	public static String clashSafeString(String originalString,Set<String> existingSet) {
		String toReturn=originalString;
		int suffix=1;
		while(existingSet.contains(toReturn)) {
			toReturn=originalString+"_"+suffix;
			suffix++;
		}
		return toReturn;
	}
	
	
		
	public static long getIDByUUID(GeoNetworkClient client, String uuid) throws MetadataNotFoundException, GNLibException, GNServerException {
		log.debug("Looking for uuid : {} ",uuid);
		
		GNSearchRequest req=new GNSearchRequest();
		req.addParam(GNSearchRequest.Param.any,uuid);
		req.addConfig(Config.similarity, "1");
		
		GNSearchResponse resp=client.query(req);
		
		Iterator<GNMetadata> iterator=resp.iterator();
		log.debug("Got {} hits for UUID {}",resp.getCount(),uuid);
		while(iterator.hasNext()){
			GNMetadata meta=iterator.next();
			if(meta.getUUID().equals(uuid)) return meta.getId();
		}
		throw new MetadataNotFoundException("Unable to find metadata from uuid "+uuid); 
	}
}
