package org.gcube.common.storagehub.model.items.nodes;

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
@AttributeRootNode(value="nthl:user")
public class Owner {

	@Attribute("hl:portalLogin")
	String userName;	
	
	@Attribute("hl:uuid")
	String userId;
}
