package org.gcube.portal.databook.shared;

import java.io.Serializable;
import java.util.Date;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
@SuppressWarnings("serial")
public class Invite implements Serializable {

	private String key;
	private String senderUserId;
	private String vreid;
	private String invitedEmail;
	private String controlCode;
	private InviteStatus status;
	private Date time;
	private String senderFullName;
	
	
	public Invite() {
		super();
	}


	

	public Invite(String key, String senderUserId, String vreid,
			String invitedEmail, String controlCode, InviteStatus status,
			Date time, String senderFullName) {
		super();
		this.key = key;
		this.senderUserId = senderUserId;
		this.vreid = vreid;
		this.invitedEmail = invitedEmail;
		this.controlCode = controlCode;
		this.status = status;
		this.time = time;
		this.senderFullName = senderFullName;
	}




	public String getKey() {
		return key;
	}




	public void setKey(String key) {
		this.key = key;
	}




	public String getSenderUserId() {
		return senderUserId;
	}


	public void setSenderUserId(String senderUserId) {
		this.senderUserId = senderUserId;
	}


	public String getVreid() {
		return vreid;
	}


	public void setVreid(String vreid) {
		this.vreid = vreid;
	}


	public String getInvitedEmail() {
		return invitedEmail;
	}


	public void setInvitedEmail(String invitedEmail) {
		this.invitedEmail = invitedEmail;
	}


	public String getControlCode() {
		return controlCode;
	}


	public void setControlCode(String controlCode) {
		this.controlCode = controlCode;
	}


	public InviteStatus getStatus() {
		return status;
	}


	public void setStatus(InviteStatus status) {
		this.status = status;
	}


	public Date getTime() {
		return time;
	}


	public void setTime(Date time) {
		this.time = time;
	}


	public String getSenderFullName() {
		return senderFullName;
	}


	public void setSenderFullName(String senderFullName) {
		this.senderFullName = senderFullName;
	}




	@Override
	public String toString() {
		return "Invite [key=" + key + ", senderUserId=" + senderUserId
				+ ", vreid=" + vreid + ", invitedEmail=" + invitedEmail
				+ ", controlCode=" + controlCode + ", status=" + status
				+ ", time=" + time + ", senderFullName=" + senderFullName + "]";
	}


	

}
