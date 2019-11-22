package org.gcube.portlets.widgets.workspacesharingwidget.shared;

import java.io.Serializable;
import java.util.Comparator;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Francesco Mangiacrapa
 *
 */
public class InfoContactModel extends BaseModelData implements Serializable, Comparable<InfoContactModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6158514541724213534L;

	protected static final String ID = "id";
	public static final String FULLNAME = "fullname";
	public static final String LOGIN = "login";
	public static final String EMAIL_DOMAIN = "emailDomain";
	public static final String ISGROUP = "isgroup";

	private CredentialModel referenceCredential;

	public InfoContactModel() {
	}

	public InfoContactModel(String id, String login, String fullName, String emailDomain, boolean isGroup) {
		setId(id);
		setLogin(login);
		setName(fullName);
		setEmailDomain(emailDomain);
		setIsGroup(isGroup);
	}

	public String getId() {
		return get(ID);
	}

	public void setId(String id) {
		set(ID, id);
	}

	public String getLogin() {
		return get(LOGIN);
	}

	public void setLogin(String login) {
		set(LOGIN, login);
	}

	public String getName() {
		return get(FULLNAME);
	}

	public void setName(String name) {
		set(FULLNAME, name);

	}

	public String getEmailDomain() {
		return (String) get(EMAIL_DOMAIN);
	}

	public void setEmailDomain(String emailDomain) {
		set(EMAIL_DOMAIN, emailDomain);
	}

	public Boolean isGroup() {
		return (Boolean) get(ISGROUP);
	}

	public void setIsGroup(boolean isGroup) {
		set(ISGROUP, isGroup);
	}

	public static Comparator<InfoContactModel> COMPARATORLOGINS = new Comparator<InfoContactModel>() {
		// This is where the sorting happens.
		public int compare(InfoContactModel o1, InfoContactModel o2) {
			return o1.getLogin().compareToIgnoreCase(o2.getLogin());
		}
	};

	/**
	 * @return the referenceCredential
	 */
	public CredentialModel getReferenceCredential() {
		return referenceCredential;
	}

	/**
	 * @param referenceCredential
	 *            the referenceCredential to set
	 */
	public void setReferenceCredential(CredentialModel referenceCredential) {
		this.referenceCredential = referenceCredential;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(InfoContactModel o) {
		return InfoContactModel.COMPARATORLOGINS.compare(this, o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		int compare = compareTo((InfoContactModel) obj);
		return compare == 0 ? true : false;
	}

	@Override
	public String toString() {
		return "InfoContactModel [getId()=" + getId() + ", getLogin()=" + getLogin() + ", getName()=" + getName()
				+ ", getEmailDomain()=" + getEmailDomain() + ", isGroup()=" + isGroup() + ", getReferenceCredential()="
				+ getReferenceCredential() + "]";
	}

}