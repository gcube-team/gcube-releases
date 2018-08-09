package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class NetCDFId implements Serializable {

	private static final long serialVersionUID = -702861298258611976L;
	private String id;

	public NetCDFId() {
		super();
	}

	public NetCDFId(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "NetCDFId [id=" + id + "]";
	}

}
