/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import java.util.Date;
import java.util.UUID;

import org.gcube.informationsystem.impl.embedded.HeaderImpl;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class DummyHeader extends HeaderImpl {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 5650565441753828803L;

	public DummyHeader(){}
	
	/**
	 * @param uuid the uuid to set
	 */
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	/**
	 * @param creationTime the creationTime to set
	 */
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * @param lastUpdateTime the lastUpdateTime to set
	 */
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

}
