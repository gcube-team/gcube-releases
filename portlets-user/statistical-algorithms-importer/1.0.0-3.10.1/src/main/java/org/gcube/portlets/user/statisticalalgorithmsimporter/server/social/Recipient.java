package org.gcube.portlets.user.statisticalalgorithmsimporter.server.social;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class Recipient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951811134381609022L;
	private String user;
	private String surname;
	private String name;

	public Recipient(String user, String surname, String name) {
		super();
		this.user = user;
		this.surname = surname;
		this.name = name;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Recipient [user=" + user + ", surname=" + surname + ", name="
				+ name + "]";
	}

}
