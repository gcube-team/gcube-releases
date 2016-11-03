package org.gcube.portlets.user.templates.client.components;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.client.uicomponents.ReportUIComponent;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

public class DefaultArea extends ReportUIComponent {

	private Presenter presenter;
	
	public DefaultArea(ComponentType type, Presenter presenter, int left, int top, int width, int height) {
		super(type, left, top, width, height);
		this.presenter = presenter;
	}

	@Override
	public void lockComponent(ReportUIComponent toLock, boolean locked) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTemplateComponent(ReportUIComponent toRemove) {
		presenter.removeTemplateComponent(this);	
	}
}