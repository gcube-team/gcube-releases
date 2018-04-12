package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DimensionData implements Serializable {

	private static final long serialVersionUID = 4540272192257245556L;
	private int id;
	private String fullName;
	private int lenght;
	private boolean unlimited;
	private boolean variable;
	private boolean shared;

	public DimensionData() {
		super();
	}

	public DimensionData(int id,String fullName, int lenght, boolean unlimited, boolean variable, boolean shared) {
		super();
		this.id=id;
		this.fullName = fullName;
		this.lenght = lenght;
		this.unlimited = unlimited;
		this.variable = variable;
		this.shared = shared;
	}

	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public int getLenght() {
		return lenght;
	}

	public void setLenght(int lenght) {
		this.lenght = lenght;
	}

	public boolean isUnlimited() {
		return unlimited;
	}

	public void setUnlimited(boolean unlimited) {
		this.unlimited = unlimited;
	}

	public boolean isVariable() {
		return variable;
	}

	public void setVariable(boolean variable) {
		this.variable = variable;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	@Override
	public String toString() {
		return "DimensionData [id=" + id + ", fullName=" + fullName + ", lenght=" + lenght + ", unlimited=" + unlimited
				+ ", variable=" + variable + ", shared=" + shared + "]";
	}

	

}
