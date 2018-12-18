package org.gcube.portlets.user.workspace.client.model;

import java.io.Serializable;
import java.util.Comparator;

import org.gcube.portlets.user.workspace.shared.ContactLogin;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * The Class InfoContactModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 4, 2015
 */
public class InfoContactModel extends BaseModelData implements ContactLogin, Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5439804025263802843L;
	
	protected static final String ID = "id";
	public static final String FULLNAME = "fullname";
	public static final String LOGIN = "login";
	public static final String ISGROUP = "isgroup";


	/**
	 * Instantiates a new info contact model.
	 */
	public InfoContactModel() {}

	/**
	 * Instantiates a new info contact model.
	 *
	 * @param id the id
	 * @param login the login
	 * @param fullName the full name
	 * @param isGroup the is group
	 */
	public InfoContactModel(String id, String login, String fullName, boolean isGroup) {
		setId(id);
		setLogin(login);
		setName(fullName);
		setIsGroup(isGroup);
	}
	
	/**
	 * Sets the checks if is group.
	 *
	 * @param isGroup the new checks if is group
	 */
	public void setIsGroup(boolean isGroup) {
		set(ISGROUP,isGroup);
	}
	
	/**
	 * Checks if is group.
	 *
	 * @return true, if is group
	 */
	public boolean isGroup() {
		return (Boolean) get(ISGROUP);
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		set(FULLNAME,name);
		
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName(){
		String name = (String) (get(FULLNAME)!=null?get(FULLNAME):"");
		return name;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return get(ID);
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		set(ID, id);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.shared.ContactLogin#getLogin()
	 */
	public String getLogin() {
		return get(LOGIN);
	}

	/**
	 * Sets the login.
	 *
	 * @param login the new login
	 */
	public void setLogin(String login) {
		set(LOGIN, login);
	}

	
	public static Comparator<InfoContactModel> COMPARATORFULLNAME = new Comparator<InfoContactModel>() {
		// This is where the sorting happens.
		public int compare(InfoContactModel o1, InfoContactModel o2) {
			
			if(o1==null)
				return -1;
			
			if(o2==null)
				return 1;
			
			return o1.getName().compareTo(o2.getName());
		}
	};
	
	
	public static Comparator<InfoContactModel> COMPARATORLOGINS = new Comparator<InfoContactModel>() {
		// This is where the sorting happens.
		public int compare(InfoContactModel o1, InfoContactModel o2) {
		
			if(o1==null)
				return -1;
			
			if(o2==null)
				return 1;
			
			return o1.getLogin().compareTo(o2.getLogin());
		}
	};

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InfoContactModel [isGroup()=");
		builder.append(isGroup());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getId()=");
		builder.append(getId());
		builder.append(", getLogin()=");
		builder.append(getLogin());
		builder.append("]");
		return builder.toString();
	}
}