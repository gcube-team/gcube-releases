/**
 * 
 */
package org.gcube.portlets.user.td.wizardwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.wizardwidget.client.dataresource.ResourceBundle;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class WizardWindow extends Window {

	protected String WIZARDWIDTH = "550px";
	protected String WIZARDHEIGHT = "520px";
	protected boolean WIZARRESIZABLE = false;
	protected boolean WIZARDCOLLAPSIBLE = true;
	protected String title;

	protected ArrayList<WizardCard> cardStack = new ArrayList<WizardCard>();

	protected TextButton backButton;

	protected TextButton nextButton;

	protected String originalTitle;

	protected boolean checkBeforeClose = true;

	protected boolean nextCardFinish = false;

	protected Command nextButtonAction = null;

	protected Command previousButtonAction = null;

	protected CardLayoutContainer cardContainer;

	protected ArrayList<WizardListener> listeners;

	protected EventBus eventBus;

	protected ToolBar cardMoveToolBar;

	protected FillToolItem fillSpacingCardMoveToolBar;
	protected WizardMessages msgs;

	/**
	 * Create a new Wizard Window with the specified title.
	 * 
	 * @param title
	 *            the wizard window title.
	 */
	public WizardWindow(String title) {
		this(title, new SimpleEventBus());

	}

	public WizardWindow(String title, EventBus eventBus) {
		super();
		this.title = title;
		this.eventBus = eventBus;
		this.msgs = GWT.create(WizardMessages.class);
		
		initWindow();

		listeners = new ArrayList<WizardListener>();
		VerticalLayoutContainer container = new VerticalLayoutContainer();

		cardContainer = new CardLayoutContainer();
		container.add(cardContainer, new VerticalLayoutData(1, 1));

		cardMoveToolBar = new ToolBar();
		cardMoveToolBar.setSpacing(2);
		cardMoveToolBar.addStyleName(ThemeStyles.get().style().borderTop());

		backButton = new TextButton(msgs.buttonBackLabel());
		backButton.setIcon(ResourceBundle.INSTANCE.wizardPrevious());
		backButton.setIconAlign(IconAlign.LEFT);

		backButton.setEnabled(false);
		backButton.setTabIndex(1001);
		cardMoveToolBar.add(backButton, new BoxLayoutData(new Margins(1)));

		fillSpacingCardMoveToolBar = new FillToolItem();

		cardMoveToolBar.add(fillSpacingCardMoveToolBar);

		nextButton = new TextButton(msgs.buttonNextLabel());
		nextButton.setIcon(ResourceBundle.INSTANCE.wizardNext());
		nextButton.setIconAlign(IconAlign.RIGHT);
		nextButton.setTabIndex(1000);
		cardMoveToolBar.add(nextButton, new BoxLayoutData(new Margins(1)));

		cardMoveToolBar.setLayoutData(new VerticalLayoutData(1, -1));
		container.add(cardMoveToolBar);

		SelectHandler selectionHandler = new SelectHandler() {

			public void onSelect(SelectEvent event) {
				TextButton button = (TextButton) event.getSource();
				String btnID = button.getId();

				if (btnID.equals(backButton.getId())) {

					if (previousButtonAction != null)
						previousButtonAction.execute();
					else
						previousCard();
				} else {

					if (nextButtonAction != null)
						nextButtonAction.execute();
					else
						nextCard();
				}

			}
		};

		backButton.addSelectHandler(selectionHandler);
		nextButton.addSelectHandler(selectionHandler);

		setWidget(container);
	}

	protected void initWindow() {
		Log.info(title);
		setModal(true);
		setResizable(WIZARRESIZABLE);
		setCollapsible(WIZARDCOLLAPSIBLE);
		setWidth(WIZARDWIDTH);
		setHeight(WIZARDHEIGHT);
		setHeadingText(title);
		this.originalTitle = title;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				fireAborted();
				hide();
			}
		});

	}

	@Override
	protected void onKeyPress(Event we) {
		int keyCode = we.getKeyCode();

		// Use Element: import com.google.gwt.dom.client.Element;
		boolean t = getElement().isOrHasChild(
				we.getEventTarget().<Element> cast());
		boolean key = true;
		if (key && super.isClosable() && super.isOnEsc()
				&& keyCode == KeyCodes.KEY_ESCAPE && t) {
			fireAborted();
			hide();
		}

	}

	public void addListener(WizardListener listener) {
		listeners.add(listener);
	}

	public void removeListener(WizardListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Shows the next available card.
	 */
	public void nextCard() {

		Widget activeItem = cardContainer.getActiveWidget();

		if (activeItem instanceof WizardCard)
			((WizardCard) activeItem).dispose();

		int cardPos = cardStack.indexOf(activeItem);

		// NEXT ->

		nextButton.setEnabled(true);
		backButton.setEnabled(true);

		int newPos = cardPos + 1;

		if (newPos == 0) {
			// we are moving forward from the first card
			backButton.setEnabled(false);
		}

		nextButtonAction = null;
		previousButtonAction = null;
		Log.info("cardStack size:" + cardStack.size());
		WizardCard card = cardStack.get(newPos);
		cardContainer.setActiveWidget(card);
		doLayout();
		if (card instanceof WizardCard)
			((WizardCard) card).setup();
	}

	/**
	 * Shows the previous available card.
	 */
	public void previousCard() {
		Widget activeItem = cardContainer.getActiveWidget();

		if (activeItem instanceof WizardCard)
			((WizardCard) activeItem).dispose();

		int cardPos = cardStack.indexOf(activeItem);

		// BACK <-

		nextButton.setEnabled(true);
		backButton.setEnabled(true);

		int newPos = cardPos - 1;

		if (newPos == 0) {
			backButton.setEnabled(false);
		}

		nextButtonAction = null;
		previousButtonAction = null;

		WizardCard card = cardStack.get(newPos);
		cardContainer.setActiveWidget(card);
		doLayout();
		if (card instanceof WizardCard)
			((WizardCard) card).setup();
	}

	/**
	 * Returns the number of available cards.
	 * 
	 * @return
	 */
	public int getCardStackSize() {
		return cardStack.size();
	}

	/**
	 * Returns the current active card.
	 * 
	 * @return
	 */
	public int getCurrentCard() {
		return cardStack.indexOf(cardContainer.getActiveWidget());
	}

	public boolean checkBeforeClose() {
		return true;
	}

	public void close(boolean check) {
		checkBeforeClose = check;
		hide();
	}

	/**
	 * Sets the label of next button to "Finish" value and add a close command
	 * to it.
	 */
	public void setNextButtonToFinish() {
		nextButton.setText(msgs.buttonFinishLabel());
		nextButton.setIcon(ResourceBundle.INSTANCE.wizardGo());
		nextButton.setIconAlign(IconAlign.RIGHT);
		nextButtonAction = new Command() {

			public void execute() {
				close(false);
			}
		};
		forceLayout();
	}

	/**
	 * Set the command for the next button.
	 * 
	 * @param command
	 *            the command to execute.
	 */
	public void setNextButtonCommand(Command command) {
		nextButtonAction = command;
	}

	/**
	 * Set the command for the previous button.
	 * 
	 * @param command
	 *            the command to execute.
	 */
	public void setPreviousButtonCommand(Command command) {
		previousButtonAction = command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void show() {
		super.show();

		Widget activeItem = cardContainer.getActiveWidget();

		if (activeItem instanceof WizardCard)
			((WizardCard) activeItem).setup();
	}

	/**
	 * Set the card list.
	 * 
	 * @param cards
	 */
	public void setCards(ArrayList<WizardCard> cards) {
		for (WizardCard card : cards) {
			addCard(card);
		}
	}

	/**
	 * Adds a card to this wizard.
	 * 
	 * @param card
	 *            the card to add.
	 */
	public void addCard(WizardCard card) {
		card.setWizardWindow(this);
		cardContainer.add(card);
		cardStack.add(card);
	}

	/**
	 * Remove a card to this wizard.
	 * 
	 * @param card
	 *            the card to add.
	 */
	public void removeCard(WizardCard card) {
		cardContainer.remove(card);
		cardStack.remove(card);
	}

	/**
	 * Enables the next button on the wizard.
	 * 
	 * @param enable
	 *            <code>true</code> to enable the next button,
	 *            <code>false</code> otherwise.
	 */
	public void setEnableNextButton(boolean enable) {
		nextButton.setEnabled(enable);
	}

	/**
	 * Enables the back button on the wizard.
	 * 
	 * @param enable
	 *            <code>true</code> to enable the back button,
	 *            <code>false</code> otherwise.
	 */
	public void setEnableBackButton(boolean enable) {
		backButton.setEnabled(enable);
	}

	/**
	 * Sets the next button label.
	 * 
	 * @param text
	 *            the button label.
	 */
	protected void setNextButtonText(String text) {
		nextButton.setText(text);

	}

	/**
	 * Sets the back button label.
	 * 
	 * @param text
	 *            the button label.
	 */
	protected void setBackButtonText(String text) {
		backButton.setText(text);
	}

	/**
	 * Sets visible next button.
	 * 
	 * @param visible
	 */
	protected void setNextButtonVisible(boolean visible) {
		nextButton.setVisible(visible);
	}

	/**
	 * Sets visible back button.
	 * 
	 * @param visible
	 */
	protected void setBackButtonVisible(boolean visible) {
		backButton.setVisible(visible);
	}

	/**
	 * Add a listener to the next button.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	protected void addNextButtonListener(SelectHandler listener) {
		nextButton.addSelectHandler(listener);
	}

	/**
	 * @return the originalTitle
	 */
	public String getOriginalTitle() {
		return originalTitle;
	}

	/**
	 * Returns the card list.
	 * 
	 * @return teh card list.
	 */
	public ArrayList<WizardCard> getCardStack() {
		return cardStack;
	}

	public void showErrorAndHide(final String title, final String message,
			final String details, final Throwable throwable) {
		UtilsGXT3.alert(title, message + " " + details,
				new Callback<Component, Void>() {

					@Override
					public void onFailure(Void reason) {
						hide();
						fireFailed(title, message, details, throwable);

					}

					@Override
					public void onSuccess(Component result) {
						hide();
						fireAborted();

					}

				});

	}

	public void fireCompleted(TRId id) {
		for (WizardListener listener : listeners)
			listener.completed(id);
	}

	public void firePutInBackground() {
		for (WizardListener listener : listeners)
			listener.putInBackground();
	}

	public void fireAborted() {
		for (WizardListener listener : listeners)
			listener.aborted();
	}

	public void fireFailed(String title, String message, String details,
			Throwable throwable) {
		for (WizardListener listener : listeners)
			listener.failed(title, message, details, throwable);
	}

}
