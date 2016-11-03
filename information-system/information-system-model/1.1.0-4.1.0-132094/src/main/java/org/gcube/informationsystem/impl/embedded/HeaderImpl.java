/**
 * 
 */
package org.gcube.informationsystem.impl.embedded;

import java.util.Date;
import java.util.UUID;

import org.gcube.informationsystem.model.embedded.Header;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=Header.NAME)
public class HeaderImpl implements Header, Comparable<Header> {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 5102553511155113169L;
	
	protected UUID uuid;
	protected String creator;
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	protected Date creationTime;
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	protected Date lastUpdateTime;
			
	public HeaderImpl(){}
	
	/**
	 * @return the uuid
	 */
	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public String getCreator() {
		return creator;
	}
	
	@Override
	public Date getCreationTime() {
		return creationTime;
	}

	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public int compareTo(Header header) {
		return uuid.compareTo(header.getUUID());
	}

}
