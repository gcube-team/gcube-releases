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
@AttributeRootNode("nthl:accountingEntryEnabledPublicAccess")
public class AccountEntryEnabledPublicAccess extends AccountEntry{
	
	AccountingEntryType type = AccountingEntryType.ENABLED_PUBLIC_ACCESS;

}
