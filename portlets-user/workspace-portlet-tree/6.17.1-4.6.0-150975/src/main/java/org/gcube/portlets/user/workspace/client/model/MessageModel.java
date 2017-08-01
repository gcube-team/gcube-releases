package org.gcube.portlets.user.workspace.client.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class MessageModel extends BaseModelData implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MessageModel() {}

	/**
	 *  USED IN OPEN MESSAGE
	 * @param id
	 * @param subject
	 * @param sender
	 * @param date
	 * @param text
	 * @param attachs
	 * @param listContactsToString
	 * @param messageType
	 * @param isRead
	 */
	public MessageModel(String id, String subject, InfoContactModel sender, Date date, String text, List<FileModel> attachs, List<String> listContactsToString, String messageType, boolean isRead) {
		this(id,subject,sender,date,attachs.size(),isRead, messageType);
		setListContactsToString(listContactsToString);
		setListAttachments(attachs);
		setText(text);
	
	}
	
	
	/**
	 * BASIC CONSTRUCTOR
	 */
	public MessageModel(String id, String subject, InfoContactModel sender, Date date, int numAttachs, boolean isRead, String messageType) {
		setId(id);
		setSubject(subject);
		setFromContact(sender);
		setDate(date);
		setFromLogin(sender.getLogin());
		setNumAttachments(numAttachs);
		setRead(""+isRead);
		setMessageType(messageType);
	}

	
	/**
	 * USED TO VIEW MESSAGE IN GRID
	 * @param id
	 * @param subject
	 * @param sender
	 * @param date
	 * @param attachsNames
	 * @param messageType
	 * @param isRead
	 */
	public MessageModel(String id, String subject, InfoContactModel sender, Date date, List<String> attachsNames, String messageType, boolean isRead) {
		setId(id);
		setSubject(subject);
		setFromContact(sender);
		setDate(date);
		setFromLogin(sender.getLogin());
		setNumAttachments(attachsNames.size());
		setMessageType(messageType);
		setRead(""+isRead);
//		System.out.println("##################IN CONSTRUCTROR Attach size: " + attachsNames.size());
		setAttachmentsNames(attachsNames);		
//		System.out.println("##########START MESSAGE: " + id);
//		
//		for(String mess: attachsNames){
//			System.out.println("##################IN CONSTRUCTROR Attach: " + mess);
//		}
//		
//		System.out.println("##########END MESSAGE: " + id);
		setAttachmentsNamesView(attachsNames);
	}

	/**
	 * Set string with first attach name and the numbers of attachs
	 * @param attachsNames
	 */
	private void setAttachmentsNamesView(List<String> attachsNames) {
		if(attachsNames.size()==0){
			set(ConstantsExplorer.ATTACHS, "");	
		}else if(attachsNames.size() == 1){
			set(ConstantsExplorer.ATTACHS, attachsNames.get(0));	
		}else if(attachsNames.size() > 1)
			set(ConstantsExplorer.ATTACHS, attachsNames.get(0) + " [+"+attachsNames.size() + "]");	
	}
	
	
	private void setMessageType(String messageType) {
		set(ConstantsExplorer.MESSAGETYPE, messageType);	
	}

	private void setAttachmentsNames(List<String> attachsNames) {
		set(ConstantsExplorer.LISTATTACHMENTSNAMES, attachsNames);	
	}
	
	private void setListContactsToString(List<String> listContactsToString) {
		set(ConstantsExplorer.LISTCONTACTSTOSTRING, listContactsToString);		
	}
	
	
	private void setRead(String isRead) {
		set(ConstantsExplorer.ISREAD, isRead);	
		
	}
	
	public String getIsRead() {
		return get(ConstantsExplorer.ISREAD);
	}

	private void setListContactsTo(List<InfoContactModel> listContactsTo) {
		set(ConstantsExplorer.LISTCONTACTSTO, listContactsTo);	
	}
	
	private void setListAttachments(List<FileModel> listAttachs) {
		set(ConstantsExplorer.LISTATTACHS, listAttachs);	
	}
	
	private void setDate(Date date) {
		set(ConstantsExplorer.DATE, date);
	}

	private void setNumAttachments(int num) {
		set(ConstantsExplorer.NUMATTACHS, num);
	}

	public void setId(String id) {
		set(ConstantsExplorer.ID, id);
	}
	
	private void setText(String text) {
		set(ConstantsExplorer.TEXTMESS, text);		
	}


	private void setFromContact(InfoContactModel contact) {
		set(ConstantsExplorer.FROM, contact);	
	}

	private void setSubject(String subject) {
		set(ConstantsExplorer.SUBJECT, subject);
	}
	
	private void setFromLogin(String login){
		set(ConstantsExplorer.FROMLOGIN, login);
	}

	public String getId() {
		return get(ConstantsExplorer.ID);
	}

	public String getSubject() {
		return get(ConstantsExplorer.SUBJECT);
	}
	
	public Date getDate() {
		return (Date) get(ConstantsExplorer.DATE);
	}
	
	public int getNumAttchments() {
		return (Integer) get(ConstantsExplorer.NUMATTACHS);
	}
	
	public InfoContactModel getFromContact() {
		return (InfoContactModel) get(ConstantsExplorer.FROM);
	}
	
	public String getFromLogin() {
		return get(ConstantsExplorer.FROMLOGIN);
	}
	
	public String getTextMessage() {
		return get(ConstantsExplorer.TEXTMESS);
	}
	
	
	/**
	 * used in grid
	 * @return
	 */
	public String getAttachmentsNamesView() {
		return get(ConstantsExplorer.ATTACHS);
	}
	
	public List<InfoContactModel> getListContactsTo() {
		return get(ConstantsExplorer.LISTCONTACTSTO);
	}
	
	public List<String> getListContactsToString() {
		return get(ConstantsExplorer.LISTCONTACTSTOSTRING);
	}
	
	public List<FileModel> getListAttachments() {
		return get(ConstantsExplorer.LISTATTACHS);
	}
	
	public String getMessageType(){
		return get(ConstantsExplorer.MESSAGETYPE);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageModel [getIsRead()=");
		builder.append(getIsRead());
		builder.append(", getId()=");
		builder.append(getId());
		builder.append(", getSubject()=");
		builder.append(getSubject());
		builder.append(", getDate()=");
		builder.append(getDate());
		builder.append(", getNumAttchments()=");
		builder.append(getNumAttchments());
		builder.append(", getFromContact()=");
		builder.append(getFromContact());
		builder.append(", getFromLogin()=");
		builder.append(getFromLogin());
		builder.append(", getTextMessage()=");
		builder.append(getTextMessage());
		builder.append(", getAttachmentsNamesView()=");
		builder.append(getAttachmentsNamesView());
		builder.append(", getListContactsTo()=");
		builder.append(getListContactsTo());
		builder.append(", getListContactsToString()=");
		builder.append(getListContactsToString());
		builder.append(", getListAttachments()=");
		builder.append(getListAttachments());
		builder.append(", getMessageType()=");
		builder.append(getMessageType());
		builder.append("]");
		return builder.toString();
	}
		
}