/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateDefinition;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 29, 2014
 * 
 */
public abstract class SetColumnTypeDefinition {

	private TemplateSwitcherInteface templateSwitcherInteface;
	private List<TdTColumnCategory> listCategory;
	private TdTemplateDefinition templateDefinition;
	
	/**
	 * @return the templateDefinition
	 */
	public TdTemplateDefinition getTemplateDefinition() {
		return templateDefinition;
	}

	public abstract void updateListCategory();
	
	/**
	 * 
	 * @param templateType
	 *            is template category
	 */
	public SetColumnTypeDefinition(TemplateSwitcherInteface templateSwitcherInteface, boolean isValidTemplate) {
		this.templateSwitcherInteface = templateSwitcherInteface;
		
		templateDefinition = new TdTemplateDefinition(templateSwitcherInteface.getName(), templateSwitcherInteface.getDescription(), templateSwitcherInteface.getType(), templateSwitcherInteface.getAgency(), templateSwitcherInteface.getOnError());
		templateDefinition.setServerId(templateSwitcherInteface.getServerId());
		
		GWT.log("Template definition is : "+templateDefinition);
		
		TdTemplateController.tdTemplateServiceAsync.getColumnCategoryByTdTemplateDefinition(templateDefinition,isValidTemplate, new AsyncCallback<List<TdTColumnCategory>>() {

			@Override
			public void onSuccess(List<TdTColumnCategory> result) {

				GWT.log("ColumnCategoryByTemplateId are: "+result);
				if (result != null){
					listCategory = result;
					updateListCategory();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				GWT.log("Error getColumnCategoryByTdTemplateDefinition: "+ caught.getLocalizedMessage());

			}
		});
	}


	public String getTemplateType() {
		return templateSwitcherInteface.getType();
	}


	public List<TdTColumnCategory> getListCategory() {
		return listCategory;
	}

}
