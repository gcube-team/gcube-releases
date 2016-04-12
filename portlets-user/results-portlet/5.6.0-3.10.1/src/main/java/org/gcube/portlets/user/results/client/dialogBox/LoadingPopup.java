package org.gcube.portlets.user.results.client.dialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;


/**
 * @author massimiliano.assante@isti.cnr.it
 *
 */
public class LoadingPopup extends DialogBox implements ClickHandler {
	
	/**
	 * @param autoHide auto hide
	 */
	public LoadingPopup(boolean autoHide) {

		super(autoHide);

		HTML msg = new HTML(setToDisplay(), true);

		setWidget(msg);
	}

	/**
	 * @return inner html
	 */
	protected static String setToDisplay() {
		return 
		"<center><table border=\"0\">"+
		"<tr>"+
		"<td>"+
		"<img src=\""+GWT.getModuleBaseURL()+"../images/loading-bar.gif\" >"+
		"</td></tr>"+
		"</table></center>" ;
	}

	public void onClick(ClickEvent event) {
		hide();
	}
}