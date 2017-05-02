package org.gcube.vomanagement.usermanagement.model;

public enum GatewayRolesNames {
		VRE_MANAGER("VRE-Manager"),
		VRE_DESIGNER("VRE-Designer"),
		VO_ADMIN("VO-Admin"),
		INFRASTRUCTURE_MANAGER("Infrastructure-Manager"),
		DATA_MANAGER("Data-Manager"),
		CATALOGUE_ADMIN("Catalogue-Admin"),
		CATALOGUE_EDITOR("Catalogue-Editor");
			
		private String name;
		
		private GatewayRolesNames(String name) {
			this.name = name;
		}
		
		public String getRoleName() {
			return this.name;
		}
}
