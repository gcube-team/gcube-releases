package org.gcube.portlets.admin.createusers.shared;

import java.io.Serializable;

/**
 * Information of an already registered user to the VRE.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class VreUserBean implements Serializable{
	
	private static final long serialVersionUID = -8674087530839002999L;
	
	private String name;
	private String surname;
	private String institution;
	private String email;
	private boolean passwordChanged;
	private long regisrationDate;
	private boolean isMale;
	
	/**
	 *  Build a default user bean object.
	 */
	public VreUserBean(){
		super();
	}
	
	/**
	 * Build a user bean object.
	 * @param name
	 * @param surname
	 * @param institution
	 * @param email
	 * @param passwordChanged
	 * @param registrationDate
	 * @param isMale
	 */
	public VreUserBean(String name, String surname, String institution,
			String email, boolean passwordChanged, long registrationDate, boolean isMale) {
		super();
		this.name = name;
		this.surname = surname;
		this.institution = institution;
		this.email = email;
		this.passwordChanged = passwordChanged;
		this.regisrationDate = registrationDate;
		this.isMale = isMale;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(boolean passwordChanged) {
		this.passwordChanged = passwordChanged;
	}
	

	public long getRegisrationDate() {
		return regisrationDate;
	}

	public void setRegisrationDate(long regisrationDate) {
		this.regisrationDate = regisrationDate;
	}
	

	public boolean isMale() {
		return isMale;
	}

	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}

	@Override
	public String toString() {
		return "VreUserBean [name=" + name + ", surname=" + surname
				+ ", institution=" + institution + ", email=" + email
				+ ", passwordChanged=" + passwordChanged + ", regisrationDate="
				+ regisrationDate + ", isMale=" + isMale + "]";
	}
}
