/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.view;

import org.gcube.portlets.admin.gcubereleases.client.resources.Icons;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class LoaderIcon.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class LoaderIcon extends HorizontalPanel{
	

	private Image imgLoading = new Image(Icons.ICONS.loading());
	private HTML txtLoading = new HTML("");
	
	/**
	 * Instantiates a new loader icon.
	 *
	 * @param txtHTML the txt html
	 */
	public LoaderIcon(String txtHTML) {
		this();
		setText(txtHTML);
	}
	
	/**
	 * Instantiates a new loader icon.
	 */
	public LoaderIcon() {
		setStyleName("marginTop20");
		add(imgLoading);
		add(txtLoading);
	}
	
	/**
	 * Sets the text.
	 *
	 * @param txtHTML the new text
	 */
	public void setText(String txtHTML){
		txtLoading.setHTML("<span style=\"margin-left:5px; vertical-align:middle;\">"+txtHTML+"</span>");
	}
	
	/**
	 * Show.
	 *
	 * @param bool the bool
	 */
	public void show(boolean bool){
		this.setVisible(bool);
	}

}
