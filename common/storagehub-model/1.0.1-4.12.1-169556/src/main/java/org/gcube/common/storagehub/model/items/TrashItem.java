package org.gcube.common.storagehub.model.items;

import java.util.Calendar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.gcube.common.storagehub.model.annotations.Attribute;
import org.gcube.common.storagehub.model.annotations.RootNode;

@NoArgsConstructor
@Getter
@Setter
@RootNode("nthl:trashItem")
public class TrashItem extends Item {
		
	@Attribute("hl:name")
	String name;

	@Attribute("hl:deletedBy")
	String deletedBy;
	
	@Attribute("hl:originalParentId")
	String originalParentId;
	
	@Attribute("hl:deletedFrom")
	String deletedFrom;
	
	@Attribute("hl:deletedTime")
	Calendar deletedTime;
	
	@Attribute("hl:mimeType")
	String mimeType;
	
	@Attribute("hl:length")
	long lenght;
	
	@Attribute("hl:isFolder")
	Boolean folder;

}
