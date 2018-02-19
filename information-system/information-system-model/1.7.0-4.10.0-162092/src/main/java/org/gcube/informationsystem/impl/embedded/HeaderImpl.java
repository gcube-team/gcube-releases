/**
 * 
 */
package org.gcube.informationsystem.impl.embedded;

import java.util.Date;
import java.util.UUID;

import org.gcube.informationsystem.model.embedded.Header;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Header.NAME)
public class HeaderImpl extends EmbeddedImpl implements Header {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 5102553511155113169L;

	protected UUID uuid;
	protected String creator;
	protected String modifiedBy;
	protected Date creationTime;
	protected Date lastUpdateTime;

	public HeaderImpl() {
		super();
	}

	public HeaderImpl(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the uuid
	 */
	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getCreator() {
		return creator;
	}

	@Override
	public String getModifiedBy() {
		return modifiedBy;
	}

	@Override
	public Date getCreationTime() {
		return creationTime;
	}

	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

}
