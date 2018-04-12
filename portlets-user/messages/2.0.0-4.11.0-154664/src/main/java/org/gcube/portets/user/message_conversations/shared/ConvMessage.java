package org.gcube.portets.user.message_conversations.shared;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


public class ConvMessage implements IsSerializable{
	private String id;
	private String subject;
	private MessageUserModel owner;
	private List<MessageUserModel> recipients;
	private Date date;
	private String content;
	private boolean isRead;
	private ArrayList<FileModel> attachments;
	private boolean hasAttachments;
	
	public ConvMessage() {
		super();
	}


	//full constructors
	public ConvMessage(String id, String subject, MessageUserModel owner, List<MessageUserModel> recipients,
			Date date, String content, boolean isRead, ArrayList<FileModel> attachments, boolean hasAttachments) {
		super();
		this.id = id;
		this.subject = subject;
		this.owner = owner;
		this.recipients = recipients;
		this.date = date;
		this.content = content;
		this.isRead = isRead;
		this.attachments = attachments;
		this.hasAttachments = hasAttachments;
	}



	//without recipients
	public ConvMessage(String id, String subject, MessageUserModel owner, Date date,
			String content, boolean isRead, boolean hasAttachments) {
		super();
		this.id = id;
		this.subject = subject;
		this.owner = owner;
		this.recipients = null;
		this.date = date;
		this.content = content;
		this.isRead = isRead;
		this.hasAttachments = hasAttachments;

	}
	
	
	public ConvMessage(String id, String subject, MessageUserModel owner, List<MessageUserModel> recipients, Date date,
			String content, boolean isRead,  boolean hasAttachments) {
		super();
		this.id = id;
		this.subject = subject;
		this.owner = owner;
		this.recipients = recipients;
		this.date = date;
		this.content = content;
		this.isRead = isRead;
		this.hasAttachments = hasAttachments;
		attachments = new ArrayList<>();
	}


	public List<MessageUserModel> getRecipients() {
		return recipients;
	}

	public void setRecipients(ArrayList<MessageUserModel> recipients) {
		this.recipients = recipients;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public MessageUserModel getOwner() {
		return owner;
	}

	public void setOwner(MessageUserModel owner) {
		this.owner = owner;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public ArrayList<FileModel> getAttachments() {
		return attachments;
	}


	public void setAttachments(ArrayList<FileModel> attachments) {
		this.attachments = attachments;
	}


	public boolean hasAttachments() {
		return hasAttachments;
	}


	public void setHasAttachments(boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}


	@Override
	public String toString() {
		return "ConvMessage [id=" + id + ", subject=" + subject + ", owner=" + owner + ", recipients=" + recipients
				+ ", date=" + date + ", content=" + content + ", isRead=" + isRead + ", attachments=" + attachments
				+ ", hasAttachments=" + hasAttachments + "]";
	}




	
	
}
