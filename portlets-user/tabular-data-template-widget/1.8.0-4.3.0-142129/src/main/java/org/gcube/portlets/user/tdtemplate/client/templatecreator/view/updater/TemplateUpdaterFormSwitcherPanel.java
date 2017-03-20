/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.updater;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateControllerUpdater;
import org.gcube.portlets.user.tdtemplate.client.event.TemplateSelectedEvent;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateFormSwitcherPanel;
import org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.gwt.core.shared.GWT;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 24, 2014
 *
 */
public class TemplateUpdaterFormSwitcherPanel extends TemplateFormSwitcherPanel implements TemplateUpdaterSwitcherInteface{
	
	private TdTemplateControllerUpdater controller;
	private TemplateUpdaterFormSwitcherPanel INSTANCE = this;

	/**
	 * @param tdTemplateControllerUpdater 
	 * 
	 */
	public TemplateUpdaterFormSwitcherPanel(TdTemplateControllerUpdater tdTemplateControllerUpdater) {
		super(tdTemplateControllerUpdater);
		this.controller = tdTemplateControllerUpdater;
		super.buttonCreateTemplate.setText("Update Template");
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.updater.TemplateUpdaterSwitcherInteface#setName(java.lang.String)
	 */
	@Override
	public void setName(String templateName) {
		super.name.setValue(templateName);
		super.name.setEnabled(false);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.updater.TemplateUpdaterSwitcherInteface#setAgency(java.lang.String)
	 */
	@Override
	public void setAgency(String agency) {
		super.agency.setValue(agency);
		super.agency.setEnabled(false);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.updater.TemplateUpdaterSwitcherInteface#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		super.description.setValue(description);
		super.description.setEnabled(false);
	}


	@Override
	public void setTemplates(List<TdTTemplateType> result, TdTTemplateType select) {
		super.setTemplates(result);
		
		int answerIndex = -1;
		for (int i=0; i<result.size(); i++) {
			TdTTemplateType tdTTemplateType = result.get(i);
			if(select.getId().compareTo(tdTTemplateType.getId())==0){
				answerIndex = i;
				break;
			}
		}
		if(answerIndex>=0){
			super.comboTemplateType.select(answerIndex);
			super.comboTemplateType.setSimpleValue(result.get(answerIndex).getId());
			super.comboTemplateType.setEnabled(false);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.updater.TemplateUpdaterSwitcherInteface#setOnErrors(java.util.List, java.lang.String)
	 */
	@Override
	public void setOnErrors(List<String> onErrors, String select) {
		super.setOnErrors(onErrors);
		
		int answerIndex = onErrors.indexOf(select);

		if(answerIndex>=0){
			super.comboOnErrors.select(answerIndex);
			super.comboOnErrors.setSimpleValue(onErrors.get(answerIndex));
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.template.view.updater.TemplateUpdaterSwitcherInteface#setNumberOfColumns(int)
	 */
	@Override
	public void setNumberOfColumns(int numColumns) {
		super.numberOfColumns.setValue(""+numColumns);
		super.numberOfColumns.setEnabled(false);
		/*BaloonPanel baloon = new BaloonPanel("You can change the number of columns to next step", false);
		int zIndex = controller.getWindowZIndex();
		int zi = zIndex+1;
		baloon.getElement().getStyle().setZIndex(zi);*/
//		baloon.showRelativeTo(super.numberOfColumns);
	}

	@Override
	protected void initListeners() {
		
		buttonCreateTemplate.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				try{
					if(isValidForm()){
						GWT.log("Fire event TemplateSelectedEvent in TemplateUpdaterFormSwitcherPanel");
						controller.getInternalBus().fireEvent(new TemplateSelectedEvent(INSTANCE));
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
					
			}
		});
		
	}
}
