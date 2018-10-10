package org.gcube.data.access.storagehub.handlers.content;

import java.io.InputStream;
import java.util.Calendar;

import org.gcube.common.storagehub.model.items.GenericFileItem;
import org.gcube.common.storagehub.model.items.nodes.Content;
import org.gcube.common.storagehub.model.types.ItemAction;

public class GenericFileHandler implements ContentHandler{

	Content content = new Content();

	@Override
	public void initiliseSpecificContent(InputStream is) throws Exception {}

	@Override
	public Content getContent() {
		return content;
	}

	@Override
	public GenericFileItem buildItem(String name, String description, String login) {
		GenericFileItem item = new GenericFileItem();
		Calendar now = Calendar.getInstance();
		item.setName(name);
		item.setTitle(name);
		item.setDescription(description);
		//item.setCreationTime(now);
		item.setHidden(false);
		item.setLastAction(ItemAction.CREATED);
		item.setLastModificationTime(now);
		
		item.setLastModifiedBy(login);
		item.setOwner(login);
		item.setContent(this.content);
		return item;
	}




	
}
