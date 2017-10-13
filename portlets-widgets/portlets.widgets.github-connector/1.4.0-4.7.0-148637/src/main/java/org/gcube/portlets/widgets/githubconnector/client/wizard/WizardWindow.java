/**
 * 
 */
package org.gcube.portlets.widgets.githubconnector.client.wizard;

import java.util.ArrayList;

import org.gcube.portlets.widgets.githubconnector.client.resource.GCResources;
import org.gcube.portlets.widgets.githubconnector.client.util.GWTMessages;
import org.gcube.portlets.widgets.githubconnector.client.wizard.event.WizardEvent;
import org.gcube.portlets.widgets.githubconnector.client.wizard.event.WizardEventType;
import org.gcube.portlets.widgets.githubconnector.client.wizard.event.WizardEvent.WizardEventHandler;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class WizardWindow extends DialogBox implements WizardEvent.HasWizardEventHandler {

	private HandlerRegistration resizeHandlerRegistration;

	protected boolean WIZARD_RESIZABLE = false;
	protected boolean WIZARD_COLLAPSIBLE = true;
	protected EventBus eventBus;
	protected String title;

	protected ArrayList<WizardCard> cardStack = new ArrayList<WizardCard>();

	protected Button backButton;

	protected Button nextButton;

	protected String originalTitle;

	protected boolean checkBeforeClose = true;

	protected boolean nextCardFinish = false;

	protected Command nextButtonAction = null;

	protected Command previousButtonAction = null;

	protected DockPanel dockPanel;

	protected FlowPanel moveToolBar;

	// protected FillToolItem fillSpacingCardMoveToolBar;
	protected WizardMessages msgs;
	protected DeckPanel deckPanel;

	private Node closeEventTarget = null;

	private int zIndex = -1;

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
		GWT.log("WizardWindow");
		this.title = title;
		this.eventBus = eventBus;
		this.msgs = GWT.create(WizardMessages.class);
		initWindow();
		initHandler();
		addToolIcon();

		deckPanel = new DeckPanel();

		backButton = new Button("<div><img class='" + GCResources.INSTANCE.wizardCSS().getWizardPreviousButtonIcon()
				+ "' src='" + GCResources.INSTANCE.wizardPrevious24().getSafeUri().asString() + "'/>" + "<span class='"
				+ GCResources.INSTANCE.wizardCSS().getWizardPreviousButtonText() + "'>" + msgs.buttonBackLabel()
				+ "</span>" + "</div>");

		backButton.setEnabled(false);
		backButton.setTabIndex(1001);
		backButton.getElement().getStyle().setFloat(Float.LEFT);
		backButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (previousButtonAction != null)
					previousButtonAction.execute();
				else
					previousCard();

			}
		});

		nextButton = new Button();
		setNextButtonToDefault();
		nextButton.setEnabled(false);
		nextButton.setTabIndex(1002);
		nextButton.getElement().getStyle().setFloat(Float.RIGHT);
		nextButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (nextButtonAction != null)
					nextButtonAction.execute();
				else
					nextCard();

			}
		});

		moveToolBar = new FlowPanel();
		moveToolBar.setWidth("100%");
		moveToolBar.add(backButton);
		moveToolBar.add(nextButton);

		dockPanel = new DockPanel();
		dockPanel.setSpacing(4);

		dockPanel.add(deckPanel, DockPanel.CENTER);
		dockPanel.add(moveToolBar, DockPanel.SOUTH);

		dockPanel.setWidth("100%");
		setWidget(dockPanel);
		center();
	}

	private void initHandler() {
		resizeHandlerRegistration = Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				center();

			}
		});

	}

	protected void initWindow() {
		GWT.log(title);
		setModal(true);
		setGlassEnabled(true);
		setAnimationEnabled(true);
		setText(title);
		this.originalTitle = title;
	}

	/**
	 * Shows the next available card.
	 */
	public void nextCard() {
		try {
			int index = deckPanel.getVisibleWidget();
			int newPos = 0;
			if (index > -1) {
				Widget activeItem = deckPanel.getWidget(index);

				if (activeItem instanceof WizardCard)
					((WizardCard) activeItem).dispose();

				int cardPos = cardStack.indexOf(activeItem);
				deckPanel.remove(activeItem);

				newPos = cardPos + 1;
			}

			nextButtonAction = null;
			previousButtonAction = null;

			GWT.log("cardStack size:" + cardStack.size());
			if (cardStack.size() > 0) {
				nextButton.setEnabled(true);
				backButton.setEnabled(true);

				WizardCard card = cardStack.get(newPos);
				deckPanel.add(card);
				int indexNew = deckPanel.getWidgetIndex(card);
				deckPanel.showWidget(indexNew);
				card.setup();

			} else {
				nextButton.setEnabled(false);
				backButton.setEnabled(false);
			}

		} catch (Throwable e) {
			GWT.log("Error in nextCard():" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Shows the previous available card.
	 */
	public void previousCard() {
		try {
			int index = deckPanel.getVisibleWidget();
			int newPos = 0;
			if (index > -1) {
				Widget activeItem = deckPanel.getWidget(index);

				if (activeItem instanceof WizardCard)
					((WizardCard) activeItem).dispose();
				deckPanel.remove(activeItem);
				int cardPos = cardStack.indexOf(activeItem);
				cardStack.remove(cardPos);
				newPos = cardPos - 1;
			}

			nextButtonAction = null;
			previousButtonAction = null;

			nextButton.setEnabled(true);
			backButton.setEnabled(true);

			if (newPos == 0) {
				backButton.setEnabled(false);
			}

			GWT.log("cardStack size:" + cardStack.size());
			if (cardStack.size() > 0) {
				WizardCard card = cardStack.get(newPos);
				deckPanel.add(card);
				int indexNew = deckPanel.getWidgetIndex(card);
				deckPanel.showWidget(indexNew);
				card.setup();

			} else {
				nextButton.setEnabled(false);
				backButton.setEnabled(false);
			}

		} catch (Throwable e) {
			GWT.log("Error in previousCard():" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Returns the number of available cards.
	 * 
	 * @return the number of available cards
	 */
	public int getCardStackSize() {
		return cardStack.size();
	}

	/**
	 * Returns the current active card.
	 * 
	 * @return active card position
	 */
	public int getCurrentCard() {
		int index = deckPanel.getVisibleWidget();
		Widget activeItem = deckPanel.getWidget(index);
		return cardStack.indexOf(activeItem);
	}

	public boolean checkBeforeClose() {
		return true;
	}

	public void close(boolean check) {
		checkBeforeClose = check;
		hide();
	}

	/**
	 * Sets the label of next button to "Finish" value
	 */
	public void setNextButtonToFinish() {
		nextButton.setHTML("<div><span class='" + GCResources.INSTANCE.wizardCSS().getWizardNextButtonText() + "'>"
				+ msgs.buttonFinishLabel() + "</span>" + "<img class='"
				+ GCResources.INSTANCE.wizardCSS().getWizardNextButtonIcon() + "'" + " src='"
				+ GCResources.INSTANCE.wizardGo24().getSafeUri().asString() + "'/></div>");
		// nextButton.setIcon(GCResources.INSTANCE.wizardGo());
		// nextButton.setIconAlign(IconAlign.RIGHT);
		/*
		 * nextButtonAction = new Command() {
		 * 
		 * public void execute() { close(false); } };
		 */

	}

	public void setNextButtonToDefault() {
		nextButton.setHTML("<div><span class='" + GCResources.INSTANCE.wizardCSS().getWizardNextButtonText() + "'>"
				+ msgs.buttonNextLabel() + "</span>" + "<img class='"
				+ GCResources.INSTANCE.wizardCSS().getWizardNextButtonIcon() + "'" + " src='"
				+ GCResources.INSTANCE.wizardNext24().getSafeUri().asString() + "'/></div>");
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

		int index = deckPanel.getVisibleWidget();
		GWT.log("Visible widget: " + index);

		if (index == -1) {
			if (cardStack.size() > 0) {
				WizardCard firstCard = cardStack.get(0);
				deckPanel.clear();
				deckPanel.add(firstCard);
				int activeIndex = deckPanel.getWidgetIndex(firstCard);
				deckPanel.showWidget(activeIndex);
				firstCard.setup();
				backButton.setEnabled(false);
				center();
			} else {
				backButton.setEnabled(false);
				nextButton.setEnabled(false);
			}
		} else {
			deckPanel.showWidget(index);
			Widget activeItem = deckPanel.getWidget(index);
			if (activeItem instanceof WizardCard)
				((WizardCard) activeItem).setup();
		}

	}

	@Override
	public void hide() {
		if (resizeHandlerRegistration != null) {
			resizeHandlerRegistration.removeHandler();
			resizeHandlerRegistration = null;
		}
		super.hide();
	}

	/**
	 * Set the card list.
	 * 
	 * @param cards
	 *            set the card list
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
		cardStack.add(card);
	}

	/**
	 * Remove a card to this wizard.
	 * 
	 * @param card
	 *            the card to add.
	 */
	public void removeCard(WizardCard card) {
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
	 *            true if next button is visible
	 */
	protected void setNextButtonVisible(boolean visible) {
		nextButton.setVisible(visible);
	}

	/**
	 * Sets visible back button.
	 * 
	 * @param visible
	 *            true if back button is visible
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
	protected void addNextButtonListener(ClickHandler listener) {
		nextButton.addClickHandler(listener);
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

	public void showErrorAndHide(final String title, final String message) {
		showErrorAndHide(title, message, new Throwable());
	}

	public void showErrorAndHide(final String title, final String message, final Throwable throwable) {
		final WizardEvent event = new WizardEvent(WizardEventType.Failed);
		event.setErrorMessage(message);
		event.setException(new Exception(throwable));

		GWTMessages.alert(title, message, getZIndex(), new Callback<Void, Void>() {

			@Override
			public void onFailure(Void reason) {
				fireEvent(event);
				hide();

			}

			@Override
			public void onSuccess(Void result) {
				fireEvent(event);
				hide();

			}

		});

	}

	private void addToolIcon() {

		// get the "dialogTopRight" class td
		Element dialogTopRight = getCellElement(0, 2);

		// close button image html
		dialogTopRight.setInnerHTML("<div  class='" + GCResources.INSTANCE.wizardCSS().getWizardToolButtonText() + "'>"
				+ "<img src='" + GCResources.INSTANCE.toolButtonClose20().getSafeUri().asString() + "' class='"
				+ GCResources.INSTANCE.wizardCSS().getWizardToolButtonIcon() + "' /></div>");

		// set the event target
		closeEventTarget = dialogTopRight.getChild(0).getChild(0);
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();

		if (!event.isCanceled() && (event.getTypeInt() == Event.ONCLICK) && isCloseEvent(nativeEvent)) {
			final WizardEvent wizardEvent = new WizardEvent(WizardEventType.Aborted);
			fireEvent(wizardEvent);
			this.hide();
		}
		super.onPreviewNativeEvent(event);
	}

	// see if the click target is the close button
	private boolean isCloseEvent(NativeEvent event) {
		return event.getEventTarget().equals(closeEventTarget); // compares
																// equality of
																// the
																// underlying
																// DOM elements
	}

	@Override
	public HandlerRegistration addWizardEventHandler(WizardEventHandler handler) {
		return addHandler(handler, WizardEvent.getType());
	}

	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
		getGlassElement().getStyle().setZIndex(zIndex);
		getElement().getStyle().setZIndex(zIndex + 1);

	}

	public int getZIndex() {
		return zIndex;
	}

}
