package org.gcube.portlets.admin.vredefinition.client.presenter;

import org.gcube.portlets.admin.vredefinition.client.event.TreeNodeWizardMenuEvent;
import org.gcube.portlets.admin.vredefinition.client.model.WizardStepModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Widget;

public class WizardMenuViewPresenter {

	public interface Display {
		TreePanel<ModelData> getTreeMenu();
		Widget asWidget();
	}
	
	private final Display display;
	private final HandlerManager eventBus;
	
	public WizardMenuViewPresenter(HandlerManager eventBus, Display display){
		this.display = display;
		this.eventBus = eventBus;
		bind();
	}
	
	public void bind() {
		display.getTreeMenu().addListener(Events.OnClick, new Listener<TreePanelEvent<ModelData>>() {

			public void handleEvent(TreePanelEvent<ModelData> be) {
				if (be.getType() == Events.OnClick) {
					WizardStepModel selectedModel = (WizardStepModel)be.getItem();	
					eventBus.fireEvent(new TreeNodeWizardMenuEvent(selectedModel));

				}
			}
		});
	}
	
	public void go(LayoutContainer container) {
		container.removeAll();
		container.add(display.asWidget());
		container.layout();
	}
	
}
