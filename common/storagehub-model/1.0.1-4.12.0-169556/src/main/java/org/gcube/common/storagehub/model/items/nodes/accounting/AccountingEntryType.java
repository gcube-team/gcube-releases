package org.gcube.common.storagehub.model.items.nodes.accounting;


public enum AccountingEntryType {
	
	CREATE("nthl:accountingEntryCreate"),
	
	READ("nthl:accountingEntryRead"),

	ADD("nthl:accountingFolderEntryAdd"),

	PASTE("nthl:accountingEntryPaste"),

	CUT("nthl:accountingFolderEntryCut"),

	UPDATE("nthl:accountingEntryUpdate"),

	SHARE("nthl:accountingEntryShare"),

	UNSHARE("nthl:accountingEntryUnshare"),
	
	REMOVAL("nthl:accountingFolderEntryRemoval"),

	RENAMING("nthl:accountingFolderEntryRenaming"),

	DELETE("nthl:accountingEntryDelete"),
	
	RESTORE("nthl:accountingEntryRestore"),
	
	ENABLED_PUBLIC_ACCESS("nthl:accountingEntryEnabledPublicAccess"),
	
	DISABLED_PUBLIC_ACCESS("nthl:accountingEntryDisabledPublicAccess"),

	SET_ACL("nthl:accountingEntryAddACL"),
	
	UNKNOWN("nthl:accountingEntry");
		

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
