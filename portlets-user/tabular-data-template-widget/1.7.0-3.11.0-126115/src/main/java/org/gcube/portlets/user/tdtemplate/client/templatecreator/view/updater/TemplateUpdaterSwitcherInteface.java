/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.updater;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface;
import org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 24, 2014
 *
 */
public interface TemplateUpdaterSwitcherInteface extends TemplateSwitcherInteface {
	
	public void setName(String templateName);
	
	public void setAgency(String agency);
	
	public void setDescription(String description);
	
	public void setNumberOfColumns(int numColumns);
	
	/**
	 * @param result
	 */
//	public void selectTemplate(TdTTemplateType select);

	/**
	 * @param onErrors
	 */
	public void setOnErrors(List<String> onErrors, String select);

	/**
	 * @param result
	 * @param select
	 */
	void setTemplates(List<TdTTemplateType> result, TdTTemplateType select);
}
