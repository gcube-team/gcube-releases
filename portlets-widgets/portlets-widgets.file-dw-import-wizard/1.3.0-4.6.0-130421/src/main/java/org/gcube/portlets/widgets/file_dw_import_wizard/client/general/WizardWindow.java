/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.general;

import java.util.ArrayList;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.util.ErrorMessageBox;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Command;


public class WizardWindow extends Window {

	protected ArrayList<WizardCard> cardStack = new ArrayList<WizardCard>();

	protected Button backButton;

	protected Button nextButton;

	protected String originalTitle;

	protected boolean checkBeforeClose = true;

	protected boolean nextCardFinish = false;

	protected Command nextButtonAction = null;

	protected CardLayout cardLayout;

	protected ArrayList<WizardListener> listeners;

	/**
	 * Create a new Wizard Window with the specified title.
	 * @param title the wizard window title.
	 */
	public WizardWindow(String title)
	{
		super();
		//setBorder(false);  
		setModal(true);
		setResizable(true);

		listeners = new ArrayList<WizardListener>();

		setHeading(title);
		this.originalTitle = title;

		//setBorder(false);
		cardLayout = new CardLayout();
		cardLayout.setDeferredRender(true);
		setLayout(cardLayout);  
		//setPaddings(2);

		ToolBar toolbar = new ToolBar();  

		backButton = new Button("Back");  
		backButton.setEnabled(false);
		backButton.setTabIndex(1001);
		toolbar.add(backButton);  
		toolbar.add(new FillToolItem());  

		nextButton = new Button("Next"); 
		nextButton.setTabIndex(1000);
		toolbar.add(nextButton);  

		setBottomComponent(toolbar);

		SelectionListener<ButtonEvent> listener = new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				String btnID = ce.getButton().getId();  

				if (btnID.equals(backButton.getId())) {  

					previousCard();
				} else {  

					if (nextButtonAction!=null) nextButtonAction.execute();
					else nextCard();
				}

			}  
		};  

		backButton.addSelectionListener(listener);
		nextButton.addSelectionListener(listener);		
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();
		
		//we can't distinguish between hide and hide with button
		closeBtn.removeAllListeners();
		closeBtn.addListener(Events.Select, new Listener<ComponentEvent>() {
	        public void handleEvent(ComponentEvent ce) {
	        	MessageBox.confirm("Confirm", "Are you sure to cancel the operation?", new Listener<MessageBoxEvent>() {
					
					@Override
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							hide();
							fireAborted();
						}					
					}
				});
	        }
	      });
	}

	public void addListener(WizardListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeListener(WizardListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * Shows the next available card.
	 */
	public void nextCard()
	{

		Component activeItem = cardLayout.getActiveItem();

		if (activeItem instanceof WizardCard) ((WizardCard)activeItem).dispose();

		int cardPos = cardStack.indexOf(cardLayout.getActiveItem());

		//NEXT ->

		nextButton.setEnabled(true);
		backButton.setEnabled(true);

		if (cardPos == 0) {  
			//we are moving forward from the first card
			backButton.setEnabled(true);
		}

		WizardCard card = cardStack.get(cardPos+1);
		cardLayout.setActiveItem(card);


		activeItem = cardLayout.getActiveItem();

		if (activeItem instanceof WizardCard) ((WizardCard)activeItem).setup();
	}

	/**
	 * Shows the previous available card.
	 */
	public void previousCard()
	{
		CardLayout cardLayout = (CardLayout) getLayout();

		Component activeItem = cardLayout.getActiveItem();

		if (activeItem instanceof WizardCard) ((WizardCard)activeItem).dispose();

		int cardPos = cardStack.indexOf(cardLayout.getActiveItem());

		//BACK <-

		nextButton.setEnabled(true);
		backButton.setEnabled(true);

		if (cardPos == cardStack.size()-1) {  
			//we are moving backward from the last card
			nextButton.setEnabled(true);
		} 

		if (cardPos == 1) {  
			//we are moving backward to the first card
			backButton.setEnabled(false);
		}  

		WizardCard card = cardStack.get(cardPos-1);
		cardLayout.setActiveItem(card);

		activeItem = cardLayout.getActiveItem();

		if (activeItem instanceof WizardCard) ((WizardCard)activeItem).setup();
	}

	/**
	 * Returns the number of available cards.
	 * @return
	 */
	public int getCardStackSize()
	{
		return cardStack.size();
	}

	/**
	 * Returns the current active card.
	 * @return
	 */
	public int getCurrentCard()
	{
		CardLayout cardLayout = (CardLayout) getLayout();
		return cardStack.indexOf(cardLayout.getActiveItem());
	}

	public boolean checkBeforeClose()
	{
		return true;
	}

	public void close(boolean check) {
		checkBeforeClose = check;
		hide();
	}

	/**
	 * Sets the label of next button to "Finish" value and add a close command to it.
	 */
	public void setNextButtonToFinish()
	{
		nextButton.setText("Finish");
		nextButtonAction = new Command() {

			public void execute() {
				close(false);
				MessageBox.info("INFO", " Import almost complete. Now give a name to the dataset and finalize the import, please." +
						" Remind that importing Darwin Core Archives will " +
						"also produce Authority files for Vernacular names and Taxa Names.", null);
				
			}
		};
	}

	/**
	 * Set the command for the next button.
	 * @param command the command to execute.
	 */
	public void setNextButtonCommand(Command command)
	{
		nextButtonAction = command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void show() {
		super.show();

		CardLayout cardLayout = (CardLayout) getLayout();

		Component activeItem = cardLayout.getActiveItem();

		if (activeItem instanceof WizardCard) ((WizardCard)activeItem).setup();
	}



	/**
	 * Set the card list.
	 * @param cards
	 */
	public void setCards(ArrayList<WizardCard> cards)
	{
		for (WizardCard card:cards) {
			addCard(card);
		}
	}

	/**
	 * Adds a card to this wizard.
	 * @param card the card to add.
	 */
	public void addCard(WizardCard card)
	{
		card.setWizardWindow(this);
		add(card);
		cardStack.add(card);
		card.setWizardWindow(this);
	}


	/**
	 * Enables the next button on the wizard.
	 * @param enable <code>true</code> to enable the next button, <code>false</code> otherwise.
	 */
	protected void setEnableNextButton(boolean enable)
	{
		nextButton.setEnabled(enable);
	}

	/**
	 * Enables the back button on the wizard.
	 * @param enable <code>true</code> to enable the back button, <code>false</code> otherwise.
	 */
	protected void setEnableBackButton(boolean enable)
	{
		backButton.setEnabled(enable);
	}

	/**
	 * Sets the next button label.
	 * @param text the button label.
	 */
	protected void setNextButtonText(String text)
	{
		nextButton.setText(text);
	}

	/**
	 * Add a listener to the next button.
	 * @param listener the listener to add.
	 */
	protected void addNextButtonListener(SelectionListener<ButtonEvent> listener)
	{
		nextButton.addSelectionListener(listener);
	}

	/**
	 * @return the originalTitle
	 */
	public String getOriginalTitle() {
		return originalTitle;
	}

	/**
	 * Returns the card list.
	 * @return teh card list.
	 */
	public ArrayList<WizardCard> getCardStack()
	{
		return cardStack;
	}

	public void showErrorAndHide(String title, final String failureReason, final String failureDetails, final Throwable throwable)
	{
		ErrorMessageBox.showError(title, failureReason, failureDetails, new Listener<MessageBoxEvent>() {

			@Override
			public void handleEvent(MessageBoxEvent be) {
				hide();
				fireFailed(throwable, failureReason, failureDetails);
			}
		});
	}

	public void fireCompleted()
	{
		for (WizardListener listener:listeners) listener.completed();
	}

	public void fireAborted()
	{
		for (WizardListener listener:listeners) listener.aborted();
	}

	public void fireFailed(Throwable throwable, String reason, String details)
	{
		for (WizardListener listener:listeners) listener.failed(throwable, reason, details);
	}
}
