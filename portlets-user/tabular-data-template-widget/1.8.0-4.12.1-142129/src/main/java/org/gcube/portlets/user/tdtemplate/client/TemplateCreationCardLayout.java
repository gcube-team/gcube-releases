package org.gcube.portlets.user.tdtemplate.client;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateController.CREATION_STATE;
import org.gcube.portlets.user.tdtemplate.client.event.BackToTemplateCreatedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.SaveAsTemplateCreatedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.SaveTemplateCreatedEvent;
import org.gcube.portlets.user.tdtemplate.client.event.TemplateCreatedEvent;
import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.client.templateactions.TemplatePanelActionEdit;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ChangeLabelDialog;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplatePanel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The Class TemplateCreationCardLayout.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class TemplateCreationCardLayout extends LayoutContainer {

	private CardLayout cardLayout = new CardLayout();
//	private static TemplateCreationCardLayout instance;
	private LayoutContainer activePanel = null;
	private TemplatePanel templateCreatorLayout;
	private TemplatePanelActionEdit templateActionLayout;
	private CREATION_STATE currentState;
	private SplitButton saveButton;
	private Button nextButton;
	private Button backButton;
	private Window window;
	private ToolBar submitTool;
	private EventBus controllerBus;
	private boolean isUpdate = false;
	
	/**
	 * Instantiates a new template creation card layout.
	 *
	 * @param templateCreatorLayout the template creator layout
	 * @param templateActionLayout the template action layout
	 */
	public TemplateCreationCardLayout(TemplatePanel templateCreatorLayout, TemplatePanelActionEdit templateActionLayout, Window win, ToolBar submitTool, EventBus controllerBus) {
		this.templateCreatorLayout = templateCreatorLayout;
		this.templateActionLayout = templateActionLayout;
		this.window = win;
		this.submitTool = submitTool;
		this.controllerBus = controllerBus;
//		this.isUpdate = isUpdateTemplate;
		initToolbarButtons();
		nextButton.setEnabled(false); //DISABLED DEFAULT
		
		setLayout(new FitLayout());

		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		cp.setBorders(false);
		cp.setLayout(cardLayout);
		
		cp.add(templateCreatorLayout.getPanel());
		cp.add(templateActionLayout.getPanel());
		
		add(cp);
	}
	
	/**
	 * Update current grid view.
	 *
	 * @param state the state
	 */
	public void changeWizardView(CREATION_STATE state) {
		this.currentState = state;
		cardLayout.setActiveItem(templateCreatorLayout.getPanel());
		switchPanelForNewState();
	}
	
	
	/**
	 * @return the currentState
	 */
	public CREATION_STATE getCurrentState() {
		return currentState;
	}


	/**
	 * Switch grid panel.
	 *
	 * @param submitTool the submit tool
	 */
	private void switchPanelForNewState(){
		
		switch (currentState) {
		
		case TEMPLATE_CREATION:
		case TEMPLATE_UPDATE:

//			resultRowPanel.resetStore();
			cardLayout.setActiveItem(templateCreatorLayout.getPanel());
			submitTool.removeAll();
			submitTool.add(new FillToolItem());
			submitTool.add(nextButton);
			submitTool.layout();
			window.layout(true);
			
			break;
			
		case POST_OPERATION_CREATE:
		case POST_OPERATION_UPDATE:

//			taxonomyRowPanel.resetStore();
			cardLayout.setActiveItem(templateActionLayout.getPanel());
			submitTool.removeAll();
			submitTool.add(backButton);
			submitTool.add(new FillToolItem());
			submitTool.add(saveButton);
			submitTool.layout();
			window.layout(true);
			break;
			
		}
		
	}


	/**
	 * Active tool bar buttons.
	 *
	 * @param b the b
	 */
	public void activeToolBarButtons(boolean b) {
		
		if(activePanel.equals(templateCreatorLayout))
			templateCreatorLayout.setVisibleToolbar(b);
		else if(activePanel.equals(templateActionLayout))
			templateActionLayout.setVisibleToolbar(b);
	}
	
	
	protected void initToolbarButtons(){
		
		saveButton = new SplitButton("Save");
		saveButton.setIcon(TdTemplateAbstractResources.submit());
		
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				controllerBus.fireEvent(new SaveTemplateCreatedEvent(isUpdate));
			}
		});
		
		Menu menu = new Menu();
		MenuItem save = new MenuItem("Save");
		save.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				controllerBus.fireEvent(new SaveTemplateCreatedEvent(isUpdate));
			}
		});
		
		MenuItem saveAs = new MenuItem("Save As...");
		saveAs.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				
				final ChangeLabelDialog cld = new ChangeLabelDialog("Save Template as...", templateCreatorLayout.getTemplateSwitcherInteface().getName());
				cld.getElement().getStyle().setZIndex(templateCreatorLayout.getController().getWindowZIndex()+100);
				cld.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(cld.isValidHide())
							controllerBus.fireEvent(new SaveAsTemplateCreatedEvent(cld.getNewLabel()));
					}
				});

				cld.showRelativeTo(saveButton);
			}
		});
		
		menu.add(save);
		menu.add(saveAs);
		saveButton.setMenu(menu);
		
		nextButton = new Button("Next");
		nextButton.setIcon(TdTemplateAbstractResources.submit());
		
		nextButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				controllerBus.fireEvent(new TemplateCreatedEvent(true, false));
			}
		});
		
		backButton = new Button("Back");
		backButton.setIcon(TdTemplateAbstractResources.back());
		
		backButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				controllerBus.fireEvent(new BackToTemplateCreatedEvent());
			}
		});
	}
	

	public void submitToolEnable(boolean bool){
		submitTool.setEnabled(bool);
	}
	

	/**
	 * @return the submitTool
	 */
	public ToolBar getSubmitTool() {
		return submitTool;
	}

	/**
	 * @param b
	 */
	public void setIsUpdate(boolean bool) {
		this.isUpdate = bool;
	}
}
