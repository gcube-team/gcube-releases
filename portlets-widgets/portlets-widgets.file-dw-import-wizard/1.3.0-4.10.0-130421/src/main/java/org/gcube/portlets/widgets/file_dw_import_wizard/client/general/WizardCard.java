/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.general;


import org.gcube.portlets.widgets.file_dw_import_wizard.client.util.WizardResources;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.HTML;



public class WizardCard extends ContentPanel {
	
	private WizardWindow wizardWindow;
	protected ContentPanel titlePanel;
	protected ContentPanel footerPanel;
	protected boolean calculateFooter = false;
	
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
		setLayout(new BorderLayout());
		setHeaderVisible(false);
		
		WizardResources.INSTANCE.wizardCss().ensureInjected();

		//add the title panel  
		titlePanel = new ContentPanel();  
		titlePanel.setHeight(30);
		titlePanel.setBodyStyle("background-color:#C3D9FF");
		titlePanel.setHeaderVisible(false);
		
		HTML titleHtml = new HTML(title);
		titleHtml.setStylePrimaryName(WizardResources.INSTANCE.wizardCss().getWizardTitle());
			
		titlePanel.add(titleHtml);
		
		add(titlePanel, new BorderLayoutData(LayoutRegion.NORTH,30));  

		//add the footer panel  
		footerPanel = new ContentPanel();  
		footerPanel.setHeight(30);  
		footerPanel.setBodyStyle("background-color:#CDEB8B");
		footerPanel.setHeaderVisible(false);
		
		HTML footerHtml = new HTML(footer);
		footerHtml.setStylePrimaryName(WizardResources.INSTANCE.wizardCss().getWizardFooter());
		footerPanel.add(footerHtml);
		
		add(footerPanel, new BorderLayoutData(LayoutRegion.SOUTH,30));  

	}
	

	/**
	 * {@inheritDoc}
	 */
	public void setTitle(String title)
	{
		HTML titleHtml = new HTML("<h1>"+title+"</h1>");
		titleHtml.setStylePrimaryName(WizardResources.INSTANCE.wizardCss().getWizardTitle());
		titlePanel.removeAll();
		titlePanel.add(titleHtml);
	}
	
	/**
	 * Sets the card footer.
	 * @param footer the footer.
	 */
	public void setFooter(String footer)
	{
		HTML footerHtml = new HTML("<p>"+footer+"</p>");
		footerHtml.setStylePrimaryName(WizardResources.INSTANCE.wizardCss().getWizardFooter());
		footerPanel.removeAll(); 
		footerPanel.add(footerHtml);
	}
	
	/**
	 * Sets the card content.
	 * @param content the card content.
	 */
	public void setContent(Component content)
	{
		add(content, new BorderLayoutData(LayoutRegion.CENTER));  
	}
	
	/**
	 * Sets the card content.
	 * @param content the card content.
	 */
	public void setContent(com.google.gwt.user.client.ui.Panel content)
	{
		add(content, new BorderLayoutData(LayoutRegion.CENTER));  
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
	 * Enables the back button.
	 * @param enable <code>true</code> to enable the button, <code>false</code> otherwise.
	 */
	public void setEnableBackButton(boolean enable)
	{
		if (wizardWindow!=null){
			wizardWindow.setEnableBackButton(enable);
		}
	}
	
	public void setNextButtonToFinish()
	{
		if (wizardWindow!=null){
			wizardWindow.setNextButtonToFinish();
		}
	}
	
	/**
	 * Sets the WizardWindow for this card.
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
	public void addNextButtonListener(SelectionListener<ButtonEvent> listener)
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
	
	public void showErrorAndHide(String title, final String failureReason, final String failureDetails, final Throwable throwable)
	{
		wizardWindow.showErrorAndHide(title, failureReason, failureDetails, throwable);
	}
	
	public void hideWindow()
	{
		wizardWindow.hide();
	}

}
