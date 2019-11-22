/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.view;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

// TODO: Auto-generated Javadoc
/**
 * The Class BodyPage.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class BodyPanel extends VerticalPanel{

	/** The loader. */
	private LoaderIcon loader = new LoaderIcon();
	
	/** The panel error. */
	private FlowPanel panelError = new FlowPanel();
	
	/** The hp. */
	private HorizontalPanel hp = new HorizontalPanel();

	/**
	 * Instantiates a new body page.
	 */
	public BodyPanel() {
		this.init();
		add(panelError);
		getElement().getStyle().setProperty("width", "98%");
		add(hp);
		hp.addStyleName("the-central-table");
		
	}

	/**
	 * Inits the.
	 */
	private void init(){
		add(loader);
		loader.setVisible(false);
	}
	
	/**
	 * Adds the widget.
	 *
	 * @param w the w
	 */
	public void addWidget(Widget w){
		hp.add(w);
	}

	/**
	 * Sets the loading.
	 *
	 * @param bool the bool
	 * @param text the text
	 */
	public void setLoading(boolean bool, String text){
		loader.setVisible(bool);
		loader.setText(text);
	}

	/**
	 * Reset.
	 */
	public void reset() {
		this.clear();
		this.init();
	}

	/**
	 * Show alert.
	 *
	 * @param txt the txt
	 * @param alertType the alert type
	 */
	public void showAlert(String txt, AlertType alertType) {
		panelError.clear();
		panelError.setVisible(true);
		Alert msg = new Alert(txt);
		msg.setAnimation(true);
		msg.setClose(false);
		msg.setType(alertType);
		panelError.add(msg);
	}

	/**
	 * Hide error.
	 */
	public void hideError(){
		panelError.setVisible(false);
	}

	/**
	 * Show message.
	 *
	 * @param txt the txt
	 */
	public void showMessage(String txt) {
		FlowPanel panel = new FlowPanel();
		String html = "<div style=\"color:blue\">";
		if(txt!=null && txt.length()>0)
			html+=txt;
//			panel.add(new HTML(label));
		else
			html+="No data";
//			panel.add(new HTML("No data"));
		html+="</div>";

		panel.add(new HTML(html));
		add(panel);

	}
}
