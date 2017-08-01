/**
 *
 */
package org.gcube.portlets.user.gisviewer.client.util;

import com.extjs.gxt.ui.client.widget.Label;


/**
 * The Class SizedLabel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 10, 2016
 */
public class SizedLabel extends Label {

	/**
	 * Instantiates a new sized label.
	 *
	 * @param text the text
	 * @param size the size
	 */
	public SizedLabel(String text, int size) {
		super(text);
		this.setStyleAttribute("font-size", ""+size+"px");
	}

	/**
	 * Instantiates a new sized label.
	 *
	 * @param text the text
	 * @param size the size
	 * @param maxChar the max char
	 */
	public SizedLabel(String text, int size, int maxChar) {
		super(text);
		if (text.length()>maxChar)
			this.setText(text.substring(0, maxChar) + "...");
		this.setStyleAttribute("font-size", ""+size+"px");
	}
}