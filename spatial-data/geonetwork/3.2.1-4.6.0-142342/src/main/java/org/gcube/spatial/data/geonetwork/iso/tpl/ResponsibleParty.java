package org.gcube.spatial.data.geonetwork.iso.tpl;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponsibleParty {

	public static class Roles{
		public static final String RESOURCE_PROVIDER="resourceProvider";
		public static final String CUSTODIAN="custodian";
		public static final String OWNER="owner";
		public static final String USER="user";
		public static final String DISTRIBUTOR="distributor";
		public static final String ORIGINATOR="originator";
		public static final String POINT_OF_CONTACT="pointOfContact";
		public static final String PRINCIPAL_INVESTIGATOR="principalInvestigator";
		public static final String PROCESSOR="processor";
		public static final String PUBLISHER="publisher";
		public static final String AUTHOR="author";
	}
	
	@Data
	@AllArgsConstructor
	public static class Contact{
		private String email;
		private String site;
	}
	
	private String individualName;
	private String organization;
	private String role;
	private Contact contact;
	
	public ResponsibleParty(String individualName, String organization, String role) {
		super();
		this.individualName = individualName;
		this.organization = organization;
		this.role = role;
		this.contact=null;
	}
	
	
	
}
