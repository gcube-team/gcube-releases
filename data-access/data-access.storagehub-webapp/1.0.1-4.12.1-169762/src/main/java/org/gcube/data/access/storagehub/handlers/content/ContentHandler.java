package org.gcube.data.access.storagehub.handlers.content;

import java.io.InputStream;

import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.nodes.Content;

public interface ContentHandler {
	
	void initiliseSpecificContent(InputStream is) throws Exception;
	
	Content getContent();
	
	AbstractFileItem buildItem(String name, String description, String login);
	
	
}
