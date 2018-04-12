package org.gcube.portlets.user.gcubewidgets.client;

import org.gcube.portlets.user.gcubewidgets.client.exceptions.GCubeInvalidCommandException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasCaption;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;



/**
 * <code> GCubePanel </code> is the wrapper panel gCube Portlet that lays all of its widgets out in a single vertical column.
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class GCubePanel extends VerticalPanel implements HasCaption {

	/**
	 * help image
	 */
	public static final String IMAGE_HELP = GWT.getModuleBaseURL() + "gcube_images/help.png";
	/**
	 * help tooltip
	 */
	public static final String IMAGE_TOOLTIP_EN = "Click here to open this portlet User's Guide";
	/**
	 * static header height
	 */
	public static final int HEADER_HEIGHT = 25;
	/**
	 * the main header panel
	 */
	private CellPanel header  = new HorizontalPanel();
	/**
	 * 
	 */
	private CellPanel captionheader  = new HorizontalPanel();
	/**
	 * 
	 */
	private CellPanel mainheader  = new HorizontalPanel();
	/**
	 * 
	 */
	private SimplePanel helpPanel  = new SimplePanel();
	/**
	 * 
	 */
	private HTML captionHTML;
	/**
	 * 
	 */
	private boolean isHeaderEnabled;
	/**
	 * the help url to open
	 */
	private String helpUrl;

	/**
	 * Header constructor with Caption enables
	 * @param caption the caption to show on the header
	 * @param helpURL the URL of the User's Guide for this Portlet
	 */
	public GCubePanel(String caption, String helpURL) {
		designPanel(helpURL, true);
		HTML captionHTML = new HTML("<nobr>"+caption+"</nobr>");		
		captionHTML.setStyleName("gcube_header_caption");		
		captionheader.add(captionHTML);	
	}

	/**
	 * Header constructor with Caption disanabled
	 * @param helpURL the URL of the User's Guide for this Portlet
	 *
	 */
	public GCubePanel(String helpURL) {
		designPanel(helpURL, true);
		mainheader.add(new HTML("&nbsp;"));

	}

	/**
	 * private
	 * @param helpURL
	 * @param enable
	 */
	private void designPanel(String helpURL, boolean enable) {
		header.setStyleName("gcube_header_background");
		header.add(captionheader);
		header.add(mainheader);
		header.add(helpPanel);	
		//chech if has to use the help
		if (helpURL.compareTo("") != 0) {
			Image help = new Image(IMAGE_HELP);
			help.setStyleName("button_help");
			help.setTitle(IMAGE_TOOLTIP_EN);
			this.helpUrl = helpURL;
			help.addClickHandler(helpClickListener);
			helpPanel.add(help);
		}
		header.setCellWidth(mainheader, "100%");
		header.setSize("100%", ""+HEADER_HEIGHT);
		header.setCellVerticalAlignment(captionheader, HasAlignment.ALIGN_MIDDLE);
		header.setCellVerticalAlignment(mainheader, HasAlignment.ALIGN_MIDDLE);
		header.setCellVerticalAlignment(helpPanel, HasAlignment.ALIGN_MIDDLE);
		insert(header, 0);
		setStyleName("gcube_panel_thick_border");
		enableHeader(enable);
		isHeaderEnabled = enable;
	}

	/**
	 * Use it to add your custom widgets to the header, no css styles need to be defined for your widgets
	 * @param toAdd the widget you want to add
	 */
	public void addHeaderWidget(Widget toAdd) {
		if (isHeaderEnabled) {
			toAdd.addStyleName("margin");
			mainheader.add(toAdd);
		}
		else
			throw new GCubeInvalidCommandException("Cannot add this Widget since Panel Header is hidden");
	}

	/**
	 * Sets whether the gCube Header is visible.
	 * 
	 * @param enable <code>true</code> to show the header, <code>false</code>
	 *          to hide it
	 */
	public void enableHeader(boolean enable) {
		header.setVisible(enable);
		isHeaderEnabled = enable;
		if (enable) 
			setStyleName("gcube_panel_thick_border");
		else
			setStyleName("");
	}
	/**
	 * Gets this widget's caption.
	 * @return the caption.
	 */
	public String getCaption() {
		return captionHTML.getText();
	}
	/**
	 * Sets this widget's caption.
	 * @param caption the new caption.
	 */
	public void setCaption(String caption) {
		captionHTML = new HTML(caption);		
		captionHTML.setStyleName("gcube_header_caption");
		captionheader.add(captionHTML);
	}

	public void clear() {
		super.clear();
		if (isHeaderEnabled)
			insert(header, 0);
	}
	
	ClickHandler helpClickListener = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			String url = helpUrl;
			int width = Window.getClientWidth();
			int height = Window.getClientHeight();
			int winWidth = (int) (Window.getClientWidth() * 0.8);
			int winHeight = (int) (Window.getClientHeight() * 0.7);
			int left = (width - winWidth) / 2;
			int top = (height - winHeight) / 2;
			Window.open(url, null,"left=" + left + "top" + top + ", width=" + winWidth + ", height=" + winHeight + ", resizable=yes, scrollbars=yes, status=yes");						
		}
		
	};
}
