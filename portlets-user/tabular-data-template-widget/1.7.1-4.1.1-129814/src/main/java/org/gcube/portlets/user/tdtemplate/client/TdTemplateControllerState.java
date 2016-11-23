/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client;

import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface;

import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 27, 2014
 *
 */
public interface TdTemplateControllerState {

	void doInitTemplate(TemplateSwitcherInteface switcherInterface);
	TemplateRuleHandler getTemplateRuleUpdater();
	void setExpressionDialogIndexesUpdate(TemplateRuleHandler ruleUpdater);
	ToolBar getSubmitTool();
	void doUpdateTemplate();
	void submitTemplateDefinition();
}
