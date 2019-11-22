/**
 *
 */
package org.gcube.portlets.user.workspace.shared.accounting;

/**
 * The Enum GxtAccountingEntryType.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *         Sep 28, 2016
 */
public enum GxtAccountingEntryType {

	REMOVE("Removed", "removed"),

	RENAME("Renamed", "renamed"),

	CREATE("Created", "created"),

	PASTE("Pasted", "Pasted"),

	CUT("Cut", "cut"),

	READ("Read", "read"),

	ADD("Added", "added"),

	UPDATE("Updated", "updated"),

	SHARE("Shared", "shared"),

	UNSHARE("Unshared", "unshared"),

	ALL("all", "all"),

	RESTORE("Restored", "restored"),

	DISABLED_PUBLIC_ACCESS("DisabledPublicAccess", "disabled public access"),

	ENABLED_PUBLIC_ACCESS("EnabledPublicAccess", "enabled public access"),

	ALLWITHOUTREAD("allwithoutread", "allwithoutread");

	private String id;
	private String name;

	/**
	 * Instantiates a new gxt accounting entry type.
	 *
	 * @param id
	 *            the id
	 * @param name
	 *            the name
	 */
	GxtAccountingEntryType(String id, String name) {
		this.id = id;
		this.name = name;

	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

}
