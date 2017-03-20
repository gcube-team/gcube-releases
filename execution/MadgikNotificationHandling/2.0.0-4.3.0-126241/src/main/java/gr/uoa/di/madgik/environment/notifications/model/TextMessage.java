package gr.uoa.di.madgik.environment.notifications.model;

import gr.uoa.di.madgik.environment.notifications.Message;

public class TextMessage extends Message {

	String messageContent;
	
	public void setText(String message) {
		messageContent = message;
	}
	
	public String getText() {
		return messageContent;
	}
}
