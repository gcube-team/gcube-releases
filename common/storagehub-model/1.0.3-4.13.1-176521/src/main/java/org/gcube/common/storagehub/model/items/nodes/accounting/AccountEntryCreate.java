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
@AttributeRootNode("nthl:accountingEntryCreate")
public class AccountEntryCreate extends AccountEntry{

	@Attribute("hl:itemName")
	String itemName;
	
	final AccountingEntryType type = AccountingEntryType.CREATE;
}
