/**
 * 
 */
package org.gcube.informationsystem.model.orientdb.impl.embedded;

import java.util.Date;
import java.util.UUID;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@SuppressWarnings("unchecked")
public class Header extends ODocument implements org.gcube.informationsystem.model.embedded.Header {

	public Header(){
		super(org.gcube.informationsystem.model.embedded.Header.NAME);
	}
	
	protected Header(String iClassName){
		super(iClassName);
	}
	
	@Override
	public UUID getUUID() {
		return UUID.fromString((String) this.field(org.gcube.informationsystem.model.embedded.Header.UUID_PROPERTY));
	}
	
	public void setUUID(UUID uuid){
		this.field(org.gcube.informationsystem.model.embedded.Header.UUID_PROPERTY, uuid.toString());
	}

	@Override
	public String getCreator() {
		return this.field(org.gcube.informationsystem.model.embedded.Header.CREATOR_PROPERTY);
	}
	
	public void setCreator(String creator){
		this.field(org.gcube.informationsystem.model.embedded.Header.CREATOR_PROPERTY, creator);
	}
	
	@Override
	public Date getCreationTime() {
		return this.field(org.gcube.informationsystem.model.embedded.Header.CREATION_TIME_PROPERTY);
	}
	
	public void setCreationTime(Date creationTime){
		this.field(org.gcube.informationsystem.model.embedded.Header.CREATION_TIME_PROPERTY, creationTime);
	}
	
	@Override
	public Date getLastUpdateTime() {
		return this.field(org.gcube.informationsystem.model.embedded.Header.LAST_UPDATE_TIME_PROPERTY);
	}
	
	public void setLastUpdateTime(Date lastUpdateTime){
		this.field(org.gcube.informationsystem.model.embedded.Header.LAST_UPDATE_TIME_PROPERTY, lastUpdateTime);
	}

}
