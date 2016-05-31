package org.gcube.portlets.admin.software_upload_wizard.client.view;

import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;
import org.gcube.portlets.admin.software_upload_wizard.client.wizard.IWizard;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public abstract class WizardWindow extends Window {

	public static final String NEXT_BUTTON_DEFAULT_TEXT = "Next";
	public static final String PREVIOUS_BUTTON_DEFAULT_TEXT = "Back";
	public static final String NEXT_BUTTON_DEFAULT_FINAL_TEXT = "Finish";

	private String mainTitle;
	private CardLayout wizardCardLayout;
	private ContentPanel wizardContainer;
	private Button backButton;
	private Button nextButton;
	private ToolBar bottomToolBar;
	private WizardCard currentCard = null;

	private ToolButton helpImg;

	private HelpWindow helpWindow = new HelpWindow();

	public WizardWindow(String title) {
		// TODO Manage styles with css
		this.setHeading(title);
		this.mainTitle = title;
		this.setMaximizable(false);
		this.setResizable(false);
		this.setSize(700, 600);
		this.setLayout(new FitLayout());

		// Setup wizard main panel
		wizardCardLayout = new CardLayout();
		wizardContainer = new ContentPanel(wizardCardLayout);
		wizardContainer.setHeaderVisible(false);
		this.add(wizardContainer);

		// Setup bottom tool bar
		backButton = new Button(PREVIOUS_BUTTON_DEFAULT_TEXT);
		// backButton.disable();
		nextButton = new Button(NEXT_BUTTON_DEFAULT_TEXT);

		bottomToolBar = new ToolBar();
		bottomToolBar.setStyleAttribute("padding", "6px");

		bottomToolBar.add(backButton);
		bottomToolBar.add(new FillToolItem());
		bottomToolBar.add(nextButton);

		addHelpButton();

		setBottomComponent(bottomToolBar);

		bind();
	}

	private void bind() {
		this.addListener(Events.Hide, new Listener<ComponentEvent>() {
			@Override
			public void handleEvent(ComponentEvent be) {
				helpWindow.hide();
			}
		});
	}

	@Override
	protected void onPosition(int x, int y) {
		if (helpWindow.isRendered())
			alignHelpWindow();
		super.onPosition(x, y);
	}

	private void addHelpButton() {
		helpImg = new ToolButton("x-tool-help");
		helpImg.addSelectionListener(new SelectionListener<IconButtonEvent>() {

			@Override
			public void componentSelected(IconButtonEvent ce) {
				if (helpWindow.isVisible()) {
					hideHelpWindow();
				} else {
					showHelpWindow();
					WizardWindow.this.alignHelpWindow();
				}
			}
		});

		helpWindow.addListener(Events.Hide, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				helpImg.show();
			}
		});
		getHeader().addTool(helpImg);
	}

	private void showHelpWindow() {
		helpImg.hide();
		helpWindow.show();
		WizardWindow.this.alignHelpWindow();

	}

	private void hideHelpWindow() {
		helpImg.show();
		helpWindow.hide();
	}

	private void alignHelpWindow() {
		helpWindow.alignTo(WizardWindow.this.getElement(), "tr", new int[] { 3,
				0 });
	}

	protected void setNextButtonListener(SelectionListener<ButtonEvent> listener) {
		nextButton.removeAllListeners();
		nextButton.addSelectionListener(listener);
	}

	protected void setBackButtonListener(SelectionListener<ButtonEvent> listener) {
		backButton.removeAllListeners();
		backButton.addSelectionListener(listener);
	}

	/**
	 * Set card to be displayed on the wizard window
	 * 
	 * @param card
	 *            The WizardCard that needs to be displayed
	 */
	public void setCard(WizardCard card) {
		if (card == null) {
			Log.fatal("Trying to add null card to wizard window");
		}
		resetNavigationButtonStatus();
		wizardContainer.removeAll();
		wizardContainer.add(card);
		wizardCardLayout.setActiveItem(card);
		currentCard = card;
		IWizard wizard = Util.getWizard();
		if (wizard != null && wizard.getCurrentCard()==card){
			this.setHeading(mainTitle + " - " + currentCard.getHeading()
					+ " - Step " + wizard.getCurrentWizardStepNumber() + " of "
					+ wizard.getTotalWizardStepsNumber());
//			Info.display("Wizard Progress", "Step {0} of {1}", String.valueOf(wizard.getCurrentWizardStepNumber()), String.valueOf(wizard.getTotalWizardStepsNumber()));
		}
		else{
			this.setHeading(mainTitle + " - " + currentCard.getHeading());
			}
		setHelpContent(currentCard.getHelpContent());
		card.setup();
	}

	private void resetNavigationButtonStatus() {
		setNextButtonEnabled(true);
		setBackButtonEnabled(true);
	}

	public WizardCard getCurrentCard() {
		return currentCard;
	}

	public void setNextButtonEnabled(boolean enabled) {
		nextButton.setEnabled(enabled);
	}

	public void setBackButtonEnabled(boolean enabled) {
		backButton.setEnabled(enabled);
	}

	public Button getNextButton() {
		return nextButton;
	}

	public Button getBackButton() {
		return backButton;
	}

	private void setHelpContent(String htmlContent) {
		if (htmlContent==null){
			hideHelpWindow();
			helpImg.hide();
		}
		else{
			if (!helpWindow.isVisible()) helpImg.show();
			helpWindow.setContent(htmlContent);
		} 
		
	}
	
	public void setNavigationToolbarVisible(boolean visible){
		if (visible)
			bottomToolBar.enable();
		else 
			bottomToolBar.disable();
	} 

}
