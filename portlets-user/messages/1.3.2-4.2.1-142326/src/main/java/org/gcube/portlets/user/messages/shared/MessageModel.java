package org.gcube.portlets.user.messages.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.messages.client.ConstantsPortletMessages;

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
	protected InfoContactModel contact; //TODO remove?
	protected FileModel fileModel; //TODO remove?
	private Map<String, String> mapContacts; //A Map<String, String> username - fullname
	
	public MessageModel() {}

	/**
	 * 
	 * @param id
	 * @param subject
	 * @param sender
	 * @param date
	 * @param text
	 * @param attachs
	 * @param mapContacts
	 * @param messageType
	 * @param isRead
	 */
	public MessageModel(String id, String subject, InfoContactModel sender, Date date, String text, List<FileModel> attachs, Map<String, String> mapContacts, String messageType, boolean isRead) {
		this(id,subject,sender,date,attachs.size(),isRead, messageType);
		setMapContacts(mapContacts);
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
		setFullName(sender.getFullName());
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
		setFullName(sender.getFullName());
		setNumAttachments(attachsNames.size());
		setMessageType(messageType);
		setRead(""+isRead);
		setAttachmentsNames(attachsNames);
		setAttachmentsNamesView(attachsNames);
	}
	
	/**
	 * @return
	 */
	public List<String> getListFullNameToContacts() {
		if(mapContacts!=null){
			List<String> fullNames = new ArrayList<String>(mapContacts.keySet().size());
			for (String login : mapContacts.keySet()) {
				fullNames.add(mapContacts.get(login));
			}
			return fullNames;
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public List<String> getListLoginToContacts() {
		if(mapContacts!=null){
			List<String> logins = new ArrayList<String>(mapContacts.keySet().size());
			for (String login : mapContacts.keySet()) {
				logins.add(login);
			}
			return logins;
		}
		return null;
	}

	private void setFullName(String fullName) {
		set(ConstantsPortletMessages.FULLNAME, fullName);		
		
	}
	
	public String getFullName(){
		return get(ConstantsPortletMessages.FULLNAME);
	}

	/**
	 * Set string with first attach name and the numbers of attachs
	 * @param attachsNames
	 */
	private void setAttachmentsNamesView(List<String> attachsNames) {
		if(attachsNames.size()==0){
			set(ConstantsPortletMessages.ATTACHS, "");	
		}else if(attachsNames.size() == 1){
			set(ConstantsPortletMessages.ATTACHS, attachsNames.get(0));	
		}else if(attachsNames.size() > 1){
			int attachSize = attachsNames.size() - 1;
			set(ConstantsPortletMessages.ATTACHS, attachsNames.get(0) + " [+"+attachSize + "]");
		}
	}
	
	
	/**
	 * @param mapContacts
	 */
	private void setMapContacts(Map<String, String> mapContacts) {
		this.mapContacts = mapContacts;
	}
	
	
	private void setMessageType(String messageType) {
		set(ConstantsPortletMessages.MESSAGETYPE, messageType);	
	}

	private void setAttachmentsNames(List<String> attachsNames) {
		set(ConstantsPortletMessages.LISTATTACHMENTSNAMES, attachsNames);	
	}
	

	private void setRead(String isRead) {
		set(ConstantsPortletMessages.ISREAD, isRead);	
		
	}
	
	public String getIsRead() {
		return get(ConstantsPortletMessages.ISREAD);
	}

	private void setListAttachments(List<FileModel> listAttachs) {
		set(ConstantsPortletMessages.LISTATTACHS, listAttachs);	
	}
	
	private void setDate(Date date) {
		set(ConstantsPortletMessages.DATE, date);
	}

	private void setNumAttachments(int num) {
		set(ConstantsPortletMessages.NUMATTACHS, num);
	}

	public void setId(String id) {
		set(ConstantsPortletMessages.ID, id);
	}
	
	private void setText(String text) {
		set(ConstantsPortletMessages.TEXTMESS, text);		
	}


	private void setFromContact(InfoContactModel contact) {
		set(ConstantsPortletMessages.FROM, contact);	
	}

	private void setSubject(String subject) {
		set(ConstantsPortletMessages.SUBJECT, subject);
	}
	
	private void setFromLogin(String login){
		set(ConstantsPortletMessages.FROMLOGIN, login);
	}

	public String getId() {
		return get(ConstantsPortletMessages.ID);
	}

	public String getSubject() {
		return get(ConstantsPortletMessages.SUBJECT);
	}
	
	public Date getDate() {
		return (Date) get(ConstantsPortletMessages.DATE);
	}
	
	public int getNumAttchments() {
		return (Integer) get(ConstantsPortletMessages.NUMATTACHS);
	}
	
	public InfoContactModel getFromContact() {
		return (InfoContactModel) get(ConstantsPortletMessages.FROM);
	}
	
	public String getFromLogin() {
		return get(ConstantsPortletMessages.FROMLOGIN);
	}
	
	public String getTextMessage() {
		return get(ConstantsPortletMessages.TEXTMESS);
	}
	
	/**
	 * used in grid
	 * @return
	 */
	public String getAttachmentsNamesView() {
		return get(ConstantsPortletMessages.ATTACHS);
	}
	
	public List<FileModel> getListAttachments() {
		return get(ConstantsPortletMessages.LISTATTACHS);
	}
	
	public String getMessageType(){
		return get(ConstantsPortletMessages.MESSAGETYPE);
	}

	/**
	 * 
	 * @return A Map<String, String> username - fullname
	 */
	public Map<String, String> getMapContacts() {
		return mapContacts;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageModel [contact=");
		builder.append(contact);
		builder.append(", fileModel=");
		builder.append(fileModel);
		builder.append(", mapContacts=");
		builder.append(mapContacts);
		builder.append(", getFullName()=");
		builder.append(getFullName());
		builder.append(", getIsRead()=");
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
		builder.append(", getListAttachments()=");
		builder.append(getListAttachments());
		builder.append(", getMessageType()=");
		builder.append(getMessageType());
		builder.append(", getMapContacts()=");
		builder.append(getMapContacts());
		builder.append("]");
		return builder.toString();
	}

	
	
}