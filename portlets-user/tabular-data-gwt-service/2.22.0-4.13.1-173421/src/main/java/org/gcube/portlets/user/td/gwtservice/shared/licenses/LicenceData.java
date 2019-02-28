package org.gcube.portlets.user.td.gwtservice.shared.licenses;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class LicenceData implements Serializable {

	private static final long serialVersionUID = -1489720811451521606L;
	private int id; // For insert in table only
	private String licenceId;
	private String licenceName;

	public LicenceData(){
		super();
	}
	
	public LicenceData(int id, String licenceId, String licenceName) {
		super();
		this.id = id;
		this.licenceId = licenceId;
		this.licenceName=licenceName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLicenceId() {
		return licenceId;
	}

	public void setLicenceId(String licenceId) {
		this.licenceId = licenceId;
	}

	public String getLicenceName() {
		return licenceName;
	}

	public void setLicenceName(String licenceName) {
		this.licenceName = licenceName;
	}

	@Override
	public String toString() {
		return "LicenceData [id=" + id + ", licenceId=" + licenceId
				+ ", licenceName=" + licenceName + "]";
	}
	
	

}
