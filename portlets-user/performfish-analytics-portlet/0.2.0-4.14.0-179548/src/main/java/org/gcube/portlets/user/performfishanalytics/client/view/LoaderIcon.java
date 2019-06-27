/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.view;

import org.gcube.portlets.user.performfishanalytics.client.resources.PerformFishResources;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class LoaderIcon.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class LoaderIcon extends FocusPanel{


	private Image imgLoading = new Image(PerformFishResources.INSTANCE.loading());
	private HTML txtLoading = new HTML("");

	private HorizontalPanel hp = new HorizontalPanel();

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
		hp.add(imgLoading);
		hp.add(txtLoading);
		add(hp);
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
