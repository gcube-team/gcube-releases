/**
 * 
 */
package org.gcube.common.homelibary.model.items.accounting;



/**
 * @author gioia
 *
 */
public enum AccountingEntryType {

	CREATE("nthl:accountingEntryCreate"),

	REMOVAL("nthl:accountingFolderEntryRemoval"),

	RENAMING("nthl:accountingFolderEntryRenaming"),

	ADD("nthl:accountingFolderEntryAdd"),

	PASTE("nthl:accountingEntryPaste"),

	CUT("nthl:accountingFolderEntryCut"),

	READ("nthl:accountingEntryRead"),

	UPDATE("nthl:accountingEntryUpdate"),

	SHARE("nthl:accountingEntryShare"),

	UNSHARE("nthl:accountingEntryUnshare"),

	DELETE("nthl:accountingEntryDelete"),
	
	RESTORE("nthl:accountingEntryRestore"),

	ADD_ACL("nthl:accountingEntryAddACL"),

	MODIFY_ACL("nthl:accountingEntryModifyACL"),

	DELETE_ACL("nthl:accountingEntryDeleteACL");

	private String nodeTypeDefinition;

	AccountingEntryType(String value) {
		this.nodeTypeDefinition = value;
	}

	public String getNodeTypeDefinition() {
		return nodeTypeDefinition;
	}

	public static AccountingEntryType getEnum(String value) {
		for (AccountingEntryType entry : AccountingEntryType.values()) {
			if (entry.getNodeTypeDefinition().compareTo(value) == 0) {
				return entry;
			}
		}
		return null;
	}
}
