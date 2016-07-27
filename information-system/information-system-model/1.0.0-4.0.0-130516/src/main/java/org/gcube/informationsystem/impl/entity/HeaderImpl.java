/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import java.util.UUID;

import org.gcube.informationsystem.model.embedded.Header;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */

public class HeaderImpl implements Header {

	protected UUID uuid;
	
	protected String creator;
	
	protected Long creationTime;
	
	protected Long lastUpdateTime;
	
	@Override
	public UUID getUUID() {
		return this.uuid;
	}
	
	protected void setUUID(UUID uuid){
		this.uuid = uuid;
	}

	@Override
	public String getCreator() {
		return this.creator;
	}
	
	protected void setCreator(String creator){
		this.creator = creator;
	}
	
	@Override
	public Long getCreationTime() {
		return creationTime;
	}
	
	protected void setCreationTime(Long creationTime){
		this.creationTime = creationTime;
	}
	
	@Override
	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}
	
	protected void setLastUpdateTime(Long lastUpdateTime){
		this.lastUpdateTime = lastUpdateTime;
	}
}
