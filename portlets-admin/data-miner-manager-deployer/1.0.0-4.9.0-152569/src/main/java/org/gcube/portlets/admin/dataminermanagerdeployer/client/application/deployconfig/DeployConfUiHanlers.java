package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.deployconfig;


import org.gcube.portlets.admin.dataminermanagerdeployer.shared.config.DMDeployConfig;

import com.gwtplatform.mvp.client.UiHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
interface DeployConfUiHandlers extends UiHandlers {

	void executeDeploy(DMDeployConfig dmConfig);
}
