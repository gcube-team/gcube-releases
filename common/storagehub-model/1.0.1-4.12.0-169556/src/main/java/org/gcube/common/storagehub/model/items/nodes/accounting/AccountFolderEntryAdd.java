package org.gcube.common.storagehub.model.items.nodes.accounting;

import org.gcube.common.storagehub.model.annotations.AttributeRootNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeRootNode("nthl:accountingFolderEntryAdd")
public class AccountFolderEntryAdd extends AccountFolderEntryRemoval{
	
	AccountingEntryType type = AccountingEntryType.ADD;

}