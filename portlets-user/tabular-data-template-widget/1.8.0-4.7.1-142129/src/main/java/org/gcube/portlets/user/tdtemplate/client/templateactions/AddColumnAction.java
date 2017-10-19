/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templateactions;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplatePanel;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface;
import org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The Class TemplatePanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 */
public class AddColumnAction {

	/** The base switcher interface. */
	private TemplateSwitcherInteface baseSwitcherInterface;
	private TdTemplateController templateController;
	private TemplatePanel addColumnTemplatePanel;
	

	/**
	 * Instantiates a new adds the column action.
	 *
	 * @param switcherInterface the switcher interface
	 * @param controller the controller
	 * @param actionsBus 
	 */
	public AddColumnAction(TemplateSwitcherInteface switcherInterface, TdTemplateController controller, int columnIndex, EventBus actionsBus) {
		this.baseSwitcherInterface = switcherInterface;
		this.templateController = controller;
		SwitcherAddColumn addColumnSwitch = new SwitcherAddColumn();
		addColumnTemplatePanel = new AddColumnTemplatePanel(addColumnSwitch, controller);
	}

	/**
	 * @return the addColumnTemplatePanel
	 */
	public TemplatePanel getAddColumnTemplatePanel() {
		return addColumnTemplatePanel;
	}

	
	/**
	 * Gets the panel.
	 *
	 * @return the panel
	 */
	public LayoutContainer getPanel(){
		return addColumnTemplatePanel.getPanel();
	}
	
	/**
	 * The Class SwitcherAddColumn.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Apr 8, 2015
	 */
	public class SwitcherAddColumn implements TemplateSwitcherInteface{

		/* (non-Javadoc)
		 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface#getType()
		 */
		@Override
		public String getType() {
			return baseSwitcherInterface.getType();
		}

		/* (non-Javadoc)
		 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface#getName()
		 */
		@Override
		public String getName() {
			return baseSwitcherInterface.getName();
		}

		/* (non-Javadoc)
		 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface#getAgency()
		 */
		@Override
		public String getAgency() {
			return baseSwitcherInterface.getAgency();
		}

		/* (non-Javadoc)
		 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface#getDescription()
		 */
		@Override
		public String getDescription() {
			return baseSwitcherInterface.getDescription();
		}

		/* (non-Javadoc)
		 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface#getNumberOfColumns()
		 */
		@Override
		public int getNumberOfColumns() {
			return 1;
		}

		/* (non-Javadoc)
		 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface#getServerId()
		 */
		@Override
		public Long getServerId() {
			return baseSwitcherInterface.getServerId();
		}

		/* (non-Javadoc)
		 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface#setServerId(java.lang.Long)
		 */
		@Override
		public void setServerId(Long id) {
			baseSwitcherInterface.setServerId(id);
		}

		/* (non-Javadoc)
		 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface#getOnError()
		 */
		@Override
		public String getOnError() {
			return baseSwitcherInterface.getOnError();
		}

		/* (non-Javadoc)
		 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface#getTdTTemplateType()
		 */
		@Override
		public TdTTemplateType getTdTTemplateType() {
			return baseSwitcherInterface.getTdTTemplateType();
		}

		/* (non-Javadoc)
		 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface#setTemplates(java.util.List)
		 */
		@Override
		public void setTemplates(List<TdTTemplateType> result) {
			baseSwitcherInterface.setTemplates(result);
			
		}

		/* (non-Javadoc)
		 * @see org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface#setOnErrors(java.util.List)
		 */
		@Override
		public void setOnErrors(List<String> onErrors) {
			baseSwitcherInterface.setOnErrors(onErrors);
		}
		
	}
	
	public void setColumnHeader(String columnHeader){
		addColumnTemplatePanel.setColumnHeaderValue(0, columnHeader);
	}
	
	/**
	 * Checks if is valid add.
	 *
	 * @return true, if is valid add
	 */
	public boolean isValidAdd(){
		return addColumnTemplatePanel.isValidTemplate();
	}

	/**
	 * @param b
	 */
	public void setVisibleAddRule(boolean b) {
		addColumnTemplatePanel.setVisibleAddRule(0,b);
		
	}
}
