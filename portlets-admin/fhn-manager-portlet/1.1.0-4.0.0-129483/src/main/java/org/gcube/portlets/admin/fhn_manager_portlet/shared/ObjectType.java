package org.gcube.portlets.admin.fhn_manager_portlet.shared;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.Storable;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;

public enum ObjectType {

		REMOTE_NODE("Remote Node",RemoteNode.class),
		SERVICE_PROFILE("Service Profiles",ServiceProfile.class),
		VM_TEMPLATES("VM Templates",VMTemplate.class),
		VM_PROVIDER("VM Providers",VMProvider.class);
		
		private String label;
		private Class<? extends Storable> clazz;
		
		private ObjectType(String label,Class<? extends Storable> clazz) {
			this.label=label;
			this.clazz=clazz;
		}
		public String getLabel() {
			return label;
		}
		public Class<? extends Storable> getClazz() {
			return clazz;
		}
}
