package org.gcube.portlets.admin.fhn_manager_portlet.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class SessionDescriptor {

	private String userName;
	private String context;
	private String token;
}
