package org.gcube.portets.user.message_conversations.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
@SuppressWarnings("serial")
public class WSUser implements IsSerializable{
	private String id;
	private String screenname;
	private String fullName;
	private String email;

	public WSUser() {
		super();
	}
	
	public WSUser(String id, String screenname, String fullName, String email) {
		super();
		this.id = id;
		this.screenname = screenname;
		this.fullName = fullName;
		this.email = email;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getScreenname() {
		return screenname;
	}
	public void setScreenname(String screenname) {
		this.screenname = screenname;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}	
	public String toString() {
		return "\nid="+id+" \nlogin="+screenname+" \nfullname="+fullName+" \nemail="+email;
	}
}
