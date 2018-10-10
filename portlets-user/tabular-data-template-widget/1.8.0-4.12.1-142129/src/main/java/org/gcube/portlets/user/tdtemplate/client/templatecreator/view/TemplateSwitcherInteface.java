/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType;




/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 16, 2014
 *
 */
public interface TemplateSwitcherInteface {
	
	public String getType(); //is template type id
	
	public String getName();
	
	public String getAgency();
	
	public String getDescription();
	
	public int getNumberOfColumns();
	
	public Long getServerId();
	
	public void setServerId(Long id);

	/**
	 * @return
	 */
	public String getOnError();

	/**
	 * @return
	 */
	public TdTTemplateType getTdTTemplateType();

	/**
	 * @param result
	 */
	void setTemplates(List<TdTTemplateType> result);

	/**
	 * @param onErrors
	 */
	void setOnErrors(List<String> onErrors);
	
}
