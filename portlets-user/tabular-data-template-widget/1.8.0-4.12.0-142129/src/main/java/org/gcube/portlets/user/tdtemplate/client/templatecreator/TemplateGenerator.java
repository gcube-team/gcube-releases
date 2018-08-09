/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnDefinitionView;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplatePanel;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface;
import org.gcube.portlets.user.tdtemplate.shared.TdFlowModel;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Dec 20, 2013
 *
 */
public class TemplateGenerator {
	
	
	private TemplateSwitcherInteface switcher;
	protected TemplatePanel templatePanel;
	private TdTemplateController controller;

	/**
	 * 
	 */
	public TemplateGenerator(TemplateSwitcherInteface switcher, TdTemplateController controller) {
		this.switcher = switcher;
		this.controller = controller;
	}
	
	/**
	 * 
	 */
	public void initTemplatePanel() {
		this.templatePanel = new TemplatePanel(switcher, controller);
	}

	public final int getNumberOfColumns() {
		return templatePanel.getNumColumns();
	}

	public TemplateSwitcherInteface getSwitcher() {
		return switcher;
	}
	
	public List<ColumnDefinitionView> getListColumnDefinition(){
		return templatePanel.getColumnsDefined();
	}

	public TemplatePanel getTemplatePanel() {
		return templatePanel;
	}
	
	public TdFlowModel getFlowAttached(){
		return templatePanel.getFlow();
	}
	
}
