package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.deployconfig;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class DeployConfModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(DeployConfPresenter.class, DeployConfPresenter.PresenterView.class, 
        		DeployConfView.class, DeployConfPresenter.PresenterProxy.class);
    }
}