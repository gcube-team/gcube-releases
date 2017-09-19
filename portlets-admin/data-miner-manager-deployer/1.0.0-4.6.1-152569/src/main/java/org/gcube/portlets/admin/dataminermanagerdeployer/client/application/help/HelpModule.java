package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.help;


import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class HelpModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenter(HelpPresenter.class, HelpPresenter.PresenterView.class, 
        		HelpView.class, HelpPresenter.PresenterProxy.class);
    }
}