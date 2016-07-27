/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.utils;

import java.util.UUID;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.informationsystem.model.orientdb.impl.embedded.Header;
import org.gcube.informationsystem.model.orientdb.impl.entity.Entity;
import org.gcube.informationsystem.model.orientdb.impl.relation.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class HeaderUtility {

	private static final Logger logger = LoggerFactory
			.getLogger(HeaderUtility.class);

	public static Header createHeader(UUID uuid) {
		Header header = new Header();

		if (uuid == null) {
			uuid = UUID.randomUUID();
		}

		header.setUUID(uuid);

		String creator = org.gcube.informationsystem.model.embedded.Header.UNKNOWN_USER;
		try {
			UserInfo userInfo = AuthorizationProvider.instance.get();
			String username = userInfo.getUserName();
			if (username != null && username.compareTo("") != 0) {
				creator = username;
			} else {
				throw new Exception("Username null or empty");
			}
		} catch (Exception e) {
			logger.error("Unable to retrieve user");
		}
		header.setCreator(creator);

		long timestamp = System.currentTimeMillis();
		header.setCreationTime(timestamp);
		header.setLastUpdateTime(timestamp);

		return header;
	}

	
	public static Header addHeader(Vertex vertex, UUID uuid) {
		Header header = createHeader(uuid);
		vertex.setProperty(Entity.HEADER_PROPERTY, header);
		return header;
	}

	public static Header addHeader(Entity entity, UUID uuid) {
		Header header = createHeader(uuid);
		entity.setHeader(header);
		return header;
	}
	
	
	public static Header addHeader(Edge edge, UUID uuid) {
		Header header = createHeader(uuid);
		edge.setProperty(Entity.HEADER_PROPERTY, header);
		return header;
	}

	public static Header addHeader(@SuppressWarnings("rawtypes") Relation relation, UUID uuid) {
		Header header = createHeader(uuid);
		relation.setHeader(header);
		return header;
	}
	
}
