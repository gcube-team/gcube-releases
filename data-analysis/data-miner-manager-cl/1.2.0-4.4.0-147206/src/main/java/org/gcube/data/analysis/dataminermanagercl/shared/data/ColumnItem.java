package org.gcube.data.analysis.dataminermanagercl.shared.data;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ColumnItem implements Serializable {

	private static final long serialVersionUID = -3451466410777498956L;
	private String id;
	private String name;

	public ColumnItem() {
		super();
	}

	public ColumnItem(String id, String name) {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ColumnItem [id=" + id + ", name=" + name + "]";
	}

}
