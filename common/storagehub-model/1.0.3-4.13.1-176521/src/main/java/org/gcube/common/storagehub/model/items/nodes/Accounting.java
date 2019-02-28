package org.gcube.common.storagehub.model.items.nodes;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.gcube.common.storagehub.model.annotations.AttributeRootNode;
import org.gcube.common.storagehub.model.annotations.ListNodes;
import org.gcube.common.storagehub.model.items.nodes.accounting.AccountEntry;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@NoArgsConstructor
@Data
@AttributeRootNode("nthl:accountingSet")
public class Accounting {

	@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
	@ListNodes(includeTypeStartWith="nthl:accounting", listClass=AccountEntry.class)
	List<AccountEntry> entries = new ArrayList<AccountEntry>();
}
