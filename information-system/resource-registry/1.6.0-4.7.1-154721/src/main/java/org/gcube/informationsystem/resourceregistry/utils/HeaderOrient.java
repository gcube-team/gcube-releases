/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.utils;

import java.util.Date;
import java.util.UUID;

import org.gcube.informationsystem.model.embedded.Header;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class HeaderOrient extends ODocument implements org.gcube.informationsystem.model.embedded.Header {

	public HeaderOrient(){
		super(Header.NAME);
	}
	
	protected HeaderOrient(String iClassName){
		super(iClassName);
	}
	
	@Override
	public UUID getUUID() {
		return UUID.fromString((String) this.field(Header.UUID_PROPERTY));
	}
	
	@Override
	public void setUUID(UUID uuid){
		this.field(Header.UUID_PROPERTY, uuid.toString());
	}

	@Override
	public String getCreator() {
		return this.field(Header.CREATOR_PROPERTY);
	}
	
	public void setCreator(String creator){
		this.field(Header.CREATOR_PROPERTY, creator);
	}
	
	@Override
	public Date getCreationTime() {
		return this.field(Header.CREATION_TIME_PROPERTY);
	}
	
	public void setCreationTime(Date creationTime){
		this.field(Header.CREATION_TIME_PROPERTY, creationTime);
	}
	
	@Override
	public Date getLastUpdateTime() {
		return this.field(Header.LAST_UPDATE_TIME_PROPERTY);
	}
	
	public void setLastUpdateTime(Date lastUpdateTime){
		this.field(Header.LAST_UPDATE_TIME_PROPERTY, lastUpdateTime);
	}

}
