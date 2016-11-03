/**
 * 
 */
package org.gcube.portlets.widgets.githubconnector.client.wizard;



import org.gcube.portlets.widgets.githubconnector.client.resource.GCResources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class WizardCard extends SimplePanel {
	
	private WizardWindow wizardWindow;
	private DockPanel dockPanel;
	
	
	protected GCResources res=GCResources.INSTANCE;

		
	/**
	 * Creates a new wizard card.
	 * The footer is automatically calculated.
	 * @param title the card title.
	 */
	public WizardCard(String title) {
		this(title,"");
	}

	/**
	 * Creates a new wizard card.
	 * @param title the card title.
	 * @param footer the card footer.
	 */
	public WizardCard(String title, String footer) {
		GWT.log(title);
		res.wizardCSS().ensureInjected();
		
		HTML t=new HTML(title);
		t.setStyleName(res.wizardCSS().getWizardCardTitle());
		
		HTML f=new HTML(footer);
		f.setStyleName(res.wizardCSS().getWizardCardFooter());
	
		dockPanel=new DockPanel();
		dockPanel.setWidth("100%");
		dockPanel.add(t, DockPanel.NORTH);
		dockPanel.add(f, DockPanel.SOUTH);
		setWidget(dockPanel);
	}
	

	
	/**
	 * Sets the card content.
	 * @param content the card content.
	 */
	public void setContent(Widget content)
	{
		GWT.log("Card SetContent()");
		dockPanel.add(content, DockPanel.CENTER);
	}
	
	
	/**
	 * Enables the next button.
	 * @param enable <code>true</code> to enable it, <code>false</code> otherwise.
	 */
	public void setEnableNextButton(boolean enable)
	{
		if (wizardWindow!=null){
			wizardWindow.setEnableNextButton(enable);
		}
	}
	
	/**
	 * Enables the back button.
	 * @param enable <code>true</code> to enable the button, <code>false</code> otherwise.
	 */
	public void setEnableBackButton(boolean enable)
	{
		if (wizardWindow!=null){
			wizardWindow.setEnableBackButton(enable);
		}
	}
	
	/**dispose
	 * Sets the next button label.
	 * @param text the button label.
	 */
	public void setNextButtonText(String text)
	{
		if (wizardWindow!=null){
			wizardWindow.setNextButtonText(text);
		}
	}
	
	/**
	 * Sets the back button label.
	 * @param text the button label.
	 */
	
	public void setBackButtonText(String text)
	{
		if (wizardWindow!=null){
			wizardWindow.setBackButtonText(text);
		}
	}
	
	
	/**
	 * Visible the next button.
	 * @param visible <code>true</code> to show the button, <code>false</code> otherwise.
	 */
	public void setNextButtonVisible(boolean visible)
	{
		if (wizardWindow!=null){
			wizardWindow.setNextButtonVisible(visible);
		}
	}
	
	/**
	 * Visible the back button.
	 * @param visible <code>true</code> to show the button, <code>false</code> otherwise.
	 */
	public void setBackButtonVisible(boolean visible)
	{
		if (wizardWindow!=null){
			wizardWindow.setBackButtonVisible(visible);
		}
	}
	
	
	public void setNextButtonToFinish()
	{
		if (wizardWindow!=null){
			wizardWindow.setNextButtonToFinish();
		}
	}
	
	/**
	 * Sets the WizardWindow for this import card.
	 * @param wizardWindow the WizardWindow.
	 */
	protected void setWizardWindow(WizardWindow wizardWindow)
	{
		this.wizardWindow = wizardWindow;
	}
	
	/**
	 * Returns the current wizard window.
	 * @return the wizard window.
	 */
	protected WizardWindow getWizardWindow()
	{
		if (wizardWindow==null) throw new IllegalStateException("No Wizard Window setup");
		return wizardWindow;
	}
	
	public void addToWindowTitle(String toAdd)
	{
		wizardWindow.setTitle(wizardWindow.getOriginalTitle()+toAdd);
	}
	
	/**
	 * Called before the card is showed.
	 */
	public void setup()
	{
	}
	
	/**
	 * Called when the card is disposed.
	 */
	public void dispose()
	{}
	
	/**
	 * Add a listener to the next button.
	 * @param listener the listener to add.
	 */
	public void addNextButtonListener(ClickHandler listener)
	{
		if (wizardWindow!=null){
			wizardWindow.addNextButtonListener(listener);
		}
	}
	
	/**
	 * Gets the number of cards in the wizard window.
	 * @return the number of cards.
	 */
	public int getNumberOfCards()
	{
		return getWizardWindow().getCardStackSize();
	}
	
	/**
	 * Returns this card position on card list.
	 * @return the card position on the card stack.
	 */
	public int getCardPosition()
	{	
		int indexPosition = getWizardWindow().getCardStack().indexOf(this);
		return (indexPosition>=0)?indexPosition+1:indexPosition;
	}
	
	public void showErrorAndHide(String title, final String message, final Throwable throwable)
	{
		wizardWindow.showErrorAndHide(title, message, throwable);
	}
	
	public void showErrorAndHide(String title, final String message)
	{
		wizardWindow.showErrorAndHide(title, message);
	}
	
	public int getZIndex(){
		return wizardWindow.getZIndex();
	}
	
	public void hideWindow()
	{
		wizardWindow.hide();
	}
	
	public EventBus getEventBus(){
		return wizardWindow.eventBus;
	}

	
}
