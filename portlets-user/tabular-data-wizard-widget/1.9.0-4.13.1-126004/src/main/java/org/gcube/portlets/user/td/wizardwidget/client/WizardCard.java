/**
 * 
 */
package org.gcube.portlets.user.td.wizardwidget.client;

import org.gcube.portlets.user.td.wizardwidget.client.dataresource.ResourceBundle;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class WizardCard extends BorderLayoutContainer {
	
	private WizardWindow wizardWindow;
	protected ContentPanel titlePanel;
	protected ContentPanel footerPanel;
	protected boolean calculateFooter = false;
	protected HTML titleHtml;
	protected HTML footerHtml;
	
	protected ResourceBundle res=ResourceBundle.INSTANCE;
	
	/**
	 * Creates a new wizard card.
	 * The footer is automatically calculated.
	 * @param title the card title.
	 */
	public WizardCard(String title) {
		this(title,"");
		calculateFooter = true;
	}

	/**
	 * Creates a new wizard card.
	 * @param title the card title.
	 * @param footer the card footer.
	 */
	public WizardCard(String title, String footer) {
		Log.info(title);
		res.wizardCSS().ensureInjected();

		//add the title panel  
		titlePanel = new ContentPanel();  
		titlePanel.setHeight(30);
		titlePanel.setBodyStyle("background-color:#C3D9FF");
		titlePanel.setHeaderVisible(false);
		
		titleHtml = new HTML(title);
		titleHtml.setStylePrimaryName(res.wizardCSS().getWizardTitle());
		titlePanel.add(titleHtml);
		
		setNorthWidget(titlePanel, new BorderLayoutData(30));  

		//add the footer panel  
		footerPanel = new ContentPanel();  
		footerPanel.setHeight(30);  
		footerPanel.setBodyStyle("background-color:#CDEB8B");
		footerPanel.setHeaderVisible(false);
		
		footerHtml = new HTML(footer);
		footerHtml.setStylePrimaryName(res.wizardCSS().getWizardFooter());
		footerPanel.add(footerHtml);
		
		setSouthWidget(footerPanel, new BorderLayoutData(30));  

	}
	

	/**
	 * {@inheritDoc}
	 */
	public void setTitle(String title)
	{
		titleHtml.setHTML("<h1>"+title+"</h1>");
	}
	
	/**
	 * Sets the card footer.
	 * @param footer the footer.
	 */
	public void setFooter(String footer)
	{
		footerHtml.setHTML("<p>"+footer+"</p>");
	}
	
	/**
	 * Sets the card content.
	 * @param content the card content.
	 */
	public void setContent(Component content)
	{
		setCenterWidget(content);  
	}
	
	/**
	 * Sets the card content.
	 * @param content the card content.
	 */
	public void setContent(com.google.gwt.user.client.ui.Panel content)
	{
		setCenterWidget(content);  
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
		if (calculateFooter) {
			StringBuilder footer = new StringBuilder();
			footer.append("Step ");
			footer.append(getCardPosition());
			footer.append(" of ");
			footer.append(getCardSize());
			setFooter(footer.toString());
		}
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
	public void addNextButtonListener(SelectHandler listener)
	{
		if (wizardWindow!=null){
			wizardWindow.addNextButtonListener(listener);
		}
	}
	
	/**
	 * Gets the number of cards in the wizard window.
	 * @return the number of cards.
	 */
	public int getCardSize()
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
	
	public void showErrorAndHide(String title, final String message, String details, final Throwable throwable)
	{
		wizardWindow.showErrorAndHide(title, message, details, throwable);
	}
	
	public void hideWindow()
	{
		wizardWindow.hide();
	}
	
	public EventBus getEventBus(){
		return wizardWindow.eventBus;
	}

}
