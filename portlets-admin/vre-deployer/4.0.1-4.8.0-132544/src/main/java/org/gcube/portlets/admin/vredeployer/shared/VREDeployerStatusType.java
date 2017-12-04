package org.gcube.portlets.admin.vredeployer.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author massi
 *
 */
public enum VREDeployerStatusType implements IsSerializable {
	/**
	 * APPROVE
	 */
	APPROVE,
	/**
	 * NON_APPROVE
	 */
	NON_APPROVE,
	/**
	 * DEPLOYING
	 */
	DEPLOYING;
}
