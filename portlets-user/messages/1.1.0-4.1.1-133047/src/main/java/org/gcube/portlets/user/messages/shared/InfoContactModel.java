package org.gcube.portlets.user.messages.shared;

import org.gcube.portlets.user.messages.client.ConstantsPortletMessages;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class InfoContactModel extends BaseModelData implements IsSerializable {


	protected static final String ID = "id";
	protected static final String CHECK = "check";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InfoContactModel() {}

	public InfoContactModel(String id, String login, String fullName) {
		setId(id);
		setLogin(login);
		setFullName(fullName);
		setCheck(false);
	}
	
	public void setFullName(String fullName) {
		set(ConstantsPortletMessages.FULLNAME, fullName);
		
	}
	
	public String getFullName() {
		return get(ConstantsPortletMessages.FULLNAME);
	}

	public String getId() {
		return get(ID);
	}

	public void setId(String id) {
		set(ID, id);
	}

	public String getLogin() {
		return get(ConstantsPortletMessages.LOGIN);
	}

	public void setLogin(String login) {
		set(ConstantsPortletMessages.LOGIN, login);
	}
	
	public void setCheck(Boolean bool){
		set(CHECK, bool);
	}
	
	public Boolean isCheck(){
		return (Boolean) get(CHECK);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InfoContactModel [getFullName()=");
		builder.append(getFullName());
		builder.append(", getId()=");
		builder.append(getId());
		builder.append(", getLogin()=");
		builder.append(getLogin());
		builder.append(", isCheck()=");
		builder.append(isCheck());
		builder.append("]");
		return builder.toString();
	}
	
}