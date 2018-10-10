package org.gcube.common.storagehub.model.items.nodes.accounting;

import java.util.Calendar;

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
@AttributeRootNode("nthl:accountingEntry")
public class AccountEntry {

	AccountingEntryType type = AccountingEntryType.UNKNOWN;
	
	@Attribute("hl:user")
	String user;
		
	@Attribute("hl:date")
	Calendar date;
	
	@Attribute(value = "hl:version")
	String version;
	
	@Attribute("jcr:primaryType")
	private String primaryType;
	
}
