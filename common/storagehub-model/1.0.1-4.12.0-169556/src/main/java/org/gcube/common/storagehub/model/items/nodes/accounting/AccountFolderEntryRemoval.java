package org.gcube.common.storagehub.model.items.nodes.accounting;

import org.gcube.common.storagehub.model.annotations.Attribute;
import org.gcube.common.storagehub.model.annotations.AttributeRootNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeRootNode("nthl:accountingFolderEntryRemoval")
public class AccountFolderEntryRemoval extends AccountEntry{

	@Attribute("hl:itemName")
	String itemName;
	
	@Attribute("hl:folderItemType")
	String folderItemType;
	
	@Attribute("hl:itemType")
	String itemType;
	
	@Attribute(value = "hl:mimeType")
	String mimeType;
	
	AccountingEntryType type = AccountingEntryType.REMOVAL;
	
}

