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
@AttributeRootNode("nthl:accountingFolderEntryCut")
public class AccountFolderEntryCut extends AccountFolderEntryRemoval{
	
	AccountingEntryType type = AccountingEntryType.CUT;

}

