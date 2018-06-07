package org.gcube.portal.trainingmodule.shared;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * The Class TrainingUnitProgressDTO.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 9, 2018
 */
public class TrainingUnitProgressDTO implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6297239177241000788L;

	/** The internal id. */
	private long internalId;
	
	/** The username. */
	private String username;
	
	/** The type. */
	private ItemType type;
	
	
	/** The item id. */
	private String itemId;
	
	
	/** The read. */
	private boolean read = false;

	
	/** The unit id. */
	private long unitId;
	
	
	/**
	 * Instantiates a new training unit progress.
	 */
	public TrainingUnitProgressDTO() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Instantiates a new training unit progress.
	 *
	 * @param unitId the unit id
	 * @param username the username
	 * @param type the type
	 * @param itemId the item id
	 * @param read the read
	 */
	public TrainingUnitProgressDTO(long unitId, String username, ItemType type, String itemId, boolean read) {
		super();
		this.unitId = unitId;
		this.username = username;
		this.type = type;
		this.itemId = itemId;
		this.read = read;
	}
	
	/**
	 * Instantiates a new training unit progress.
	 *
	 * @param internalId the internal id
	 * @param unitId the unit id
	 * @param username the username
	 * @param type the type
	 * @param itemId the item id
	 * @param read the read
	 */
	public TrainingUnitProgressDTO(long internalId, long unitId, String username, ItemType type, String itemId, boolean read) {
		super();
		this.internalId = internalId;
		this.unitId = unitId;
		this.username = username;
		this.type = type;
		this.itemId = itemId;
		this.read = read;
	}


	/**
	 * Gets the unit id.
	 *
	 * @return the unit id
	 */
	public long getUnitId() {
		return unitId;
	}



	/**
	 * Sets the unit id.
	 *
	 * @param unitId the new unit id
	 */
	public void setUnitId(long unitId) {
		this.unitId = unitId;
	}



	/**
	 * Gets the internal id.
	 *
	 * @return the internal id
	 */
	public long getInternalId() {
		return internalId;
	}


	/**
	 * Sets the internal id.
	 *
	 * @param internalId the new internal id
	 */
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}


	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}


	/**
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}


	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public ItemType getType() {
		return type;
	}


	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(ItemType type) {
		this.type = type;
	}


	/**
	 * Gets the item id.
	 *
	 * @return the item id
	 */
	public String getItemId() {
		return itemId;
	}


	/**
	 * Sets the item id.
	 *
	 * @param itemId the new item id
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}


	/**
	 * Checks if is read.
	 *
	 * @return true, if is read
	 */
	public boolean isRead() {
		return read;
	}


	/**
	 * Sets the read.
	 *
	 * @param read the new read
	 */
	public void setRead(boolean read) {
		this.read = read;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainingUnitProgressDTO [internalId=");
		builder.append(internalId);
		builder.append(", username=");
		builder.append(username);
		builder.append(", type=");
		builder.append(type);
		builder.append(", itemId=");
		builder.append(itemId);
		builder.append(", read=");
		builder.append(read);
		builder.append(", unitId=");
		builder.append(unitId);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
