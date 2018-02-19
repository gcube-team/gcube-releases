package org.gcube.portlets.user.dataminermanager.shared.data;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ColumnItem implements Serializable {

	private static final long serialVersionUID = -3451466410777498956L;
	private Integer id;
	private String name;

	public ColumnItem() {
		super();
	}

	public ColumnItem(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getLabel() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ColumnItem [id=" + id + ", name=" + name + "]";
	}

}
