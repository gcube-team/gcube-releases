package gr.cite.gaap.datatransferobjects;

import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalData;

public class PrincipalMessenger {

	private String systemName = null;
	private String fullName = null;
	private String initials = null;
	private String eMail = null;
	private String credential = null;
	private long expirationDate = 0;
	private short isActive = 0;
	private String rights = null;
	private String notificationId = null;
	private String creator = null;
	private String tenant = null;
	private String originalCustomer = null;

	public PrincipalMessenger(Principal principal) {
		
		systemName = principal.getName();
		fullName = principal.getPrincipalData().getFullName();
		initials = principal.getPrincipalData().getInitials();
		eMail =principal.getPrincipalData().getEmail();
		//credential = principal.getPrincipalData().getCredential();
		expirationDate = principal.getPrincipalData().getExpirationDate().getTime();
		isActive = principal.getIsActive().code();
		creator = principal.getCreator().getName();
		if (principal.getTenant() != null)
			tenant = principal.getTenant().getName();
	}
	
	public short getIsActive() {
		return isActive;
	}
	
	public void setIsActive(short isActive) {
		this.isActive = isActive;
	}

	public PrincipalMessenger() {
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(long expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String customer) {
		this.tenant = customer;
	}

	public String getOriginalCustomer() {
		return originalCustomer;
	}

	public void setOriginalCustomer(String originalCustomer) {
		this.originalCustomer = originalCustomer;
	}
}
