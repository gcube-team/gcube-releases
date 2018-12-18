/**
 *
 */
package org.gcube.common.storagehubwrapper.server.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class ObjectMapper.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 26, 2018
 */
public class ObjectMapper {

	private static Logger logger = LoggerFactory.getLogger(ObjectMapper.class);


	/**
	 * To list logins.
	 *
	 * @param sharedfolder the sharedfolder
	 * @return the list
	 */
	public static List<String> toListLogins(SharedFolder sharedfolder){

		Metadata users = sharedfolder.getUsers();
		Map<String, Object> mapMember = users.getValues();
		List<String> listUsers = new ArrayList<String>(mapMember.size());
		listUsers.addAll(mapMember.keySet());
		logger.debug("Returning "+listUsers.size()+" member/s for sharedFolder with id: "+sharedfolder.getId());
		return listUsers;
	}

}
