/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.updater;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateControllerUpdater;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.TemplateGenerator;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnDefinitionView;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplatePanel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 25, 2014
 *
 */
public class TemplateUpdaterGenerator extends TemplateGenerator{

	private TemplateUpdaterSwitcherInteface switcherUpdater;
	private TdTemplateControllerUpdater controllerUpdater;
	private TemplatePanelUpdater updater;

	/**
	 * @param switcher
	 * @param controller
	 */
	public TemplateUpdaterGenerator(TemplateUpdaterSwitcherInteface switcher, TdTemplateControllerUpdater controller) {
		super(switcher, controller);
		this.switcherUpdater = switcher;
		this.controllerUpdater = controller;
		
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.TemplateGenerator#initTemplatePanel()
	 */
	@Override
	public void initTemplatePanel() {
	
		this.updater = new TemplatePanelUpdater(switcherUpdater, controllerUpdater);
//		super.templatePanel = updater;
	}

	/**
	 * 
	 * @return
	 */
	public TemplatePanelUpdater getUpdater() {
		return updater;
	}
	
	@Override
	public List<ColumnDefinitionView> getListColumnDefinition(){
		return updater.getColumnsDefined();
	}
	
	@Override
	public TemplatePanel getTemplatePanel() {
		return updater;
	}

	/**
	 * 
	 */
	public void setFlowAsReadOnly(boolean bool) {
		this.updater.setFlowAsReadOnly(bool);
	}
	
	/**
	 * 
	 */
	public void setFlowAsVisible(boolean bool) {
		this.updater.setAddFlowAsVisible(bool);
	}
}
