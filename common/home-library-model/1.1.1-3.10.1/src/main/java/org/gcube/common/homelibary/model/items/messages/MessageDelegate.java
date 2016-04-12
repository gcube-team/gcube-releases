package org.gcube.common.homelibary.model.items.messages;
import java.util.List;

import lombok.Data;

@Data
public class MessageDelegate {
	
	String id;
	
	String owner;
	
	String subject;
	
	String body;
	
	boolean read;
	
	boolean open;
	
	List<String> addresses;
	
	

}
