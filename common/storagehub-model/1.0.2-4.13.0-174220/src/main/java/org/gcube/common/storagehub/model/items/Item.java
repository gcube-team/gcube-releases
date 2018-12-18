package org.gcube.common.storagehub.model.items;

import java.util.Calendar;
import static org.gcube.common.storagehub.model.NodeConstants.*;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.annotations.Attribute;
import org.gcube.common.storagehub.model.annotations.NodeAttribute;
import org.gcube.common.storagehub.model.items.nodes.Accounting;
import org.gcube.common.storagehub.model.items.nodes.Owner;
import org.gcube.common.storagehub.model.types.ItemAction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
	
	String id;

	String name;
	
	String path;
	
	String parentId;

	String parentPath;
	
	String primaryType;
	
	boolean trashed;
	
	boolean shared;

	boolean locked;

	@Attribute("hl:isPublic")
	boolean publicItem;
	
	@Attribute(value ="jcr:title")
	String title;

	@Attribute("jcr:description")
	String description;

	@Attribute("jcr:lastModifiedBy")
	String lastModifiedBy;

	@Attribute("jcr:lastModified")
	Calendar lastModificationTime;

	@Attribute(value = "jcr:created", isReadOnly=true)
	Calendar creationTime;

	@Attribute("hl:portalLogin")
	String owner;

	@Attribute("hl:lastAction")
	ItemAction lastAction;
	
	@Attribute("hl:hidden")
	boolean hidden;
	
	@NodeAttribute(value=OWNER_NAME, isReadOnly=true)
	Owner ownerNode;
	
	@NodeAttribute(value=ACCOUNTING_NAME, isReadOnly=true)
	Accounting accounting;

	@NodeAttribute(value=METADATA_NAME)
	Metadata propertyMap = new Metadata();
		
	public String getOwner() {
		if (owner!=null) return owner;
		else if (ownerNode!=null) return ownerNode.getUserName();
		return null;
	}
	
}
