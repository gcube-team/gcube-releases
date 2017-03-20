package org.gcube.common.homelibary.model.items.accounting;
import java.util.Calendar;
import java.util.Map;



import lombok.Data;

@Data
public class AccountingDelegate {
	
	String id;
	
	String user;
	
	String version;
	
	Calendar date;
	
	AccountingEntryType entryType;

	Map<AccountingProperty,String> accountingProperties;
}
