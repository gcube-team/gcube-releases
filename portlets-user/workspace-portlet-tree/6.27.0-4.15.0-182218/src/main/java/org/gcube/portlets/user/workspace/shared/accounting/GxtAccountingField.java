package org.gcube.portlets.user.workspace.shared.accounting;

import java.io.Serializable;
import java.util.Date;

import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;



/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * May 23, 2013
 * 
 */
public class GxtAccountingField implements Serializable {

	private static final long serialVersionUID = -2114527164447302004L;

	private InfoContactModel user;
	private Date date;
	private GxtAccountingEntryType operation;
	private String description;

	public GxtAccountingField() {

	}

	public GxtAccountingField(String description, InfoContactModel user,
			Date date, GxtAccountingEntryType operation) {
		setUser(user);
		setDate(date);
		setOperation(operation);
		setDescription(description);
	}

	public InfoContactModel getUser() {
		return user;
	}

	public void setUser(InfoContactModel user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the operation
	 */
	public GxtAccountingEntryType getOperation() {
		return operation;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(GxtAccountingEntryType operation) {
		this.operation = operation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
