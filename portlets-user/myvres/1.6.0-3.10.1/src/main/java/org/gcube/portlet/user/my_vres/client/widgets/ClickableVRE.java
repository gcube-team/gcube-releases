package org.gcube.portlet.user.my_vres.client.widgets;

import org.gcube.portlet.user.my_vres.client.MyVREsServiceAsync;
import org.gcube.portlet.user.my_vres.shared.VRE;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author Massimiliano Assante - ISTI CNR
 * @version 1.0 Jun 2012
 *
 */
public class ClickableVRE extends HTML {

	private final static int WIDTH = 85;
	private final static int HEIGHT = 95;

	private String name;
	private String imageUrl;
	private int imageWidth = 0;

	public static final String LOADING_IMAGE = GWT.getModuleBaseURL() + "../images/loading.gif";
	Image img = new Image(LOADING_IMAGE);

	public ClickableVRE() {
		super();
	}

	public ClickableVRE(final VRE vre, final MyVREsServiceAsync service) {
		super.setPixelSize(WIDTH, HEIGHT);
		setPixelSize(WIDTH, HEIGHT);
		imageWidth = WIDTH - 12;
		name = (vre.getName().length() > 15) ? vre.getName().substring(0, 13) + ".." : vre.getName();
		imageUrl = vre.getImageURL();
		this.setTitle("Enter");
		String html = "<div class=\"vreCaption\">" + name + "</div>";
		html +=  "<div style=\"display: table; text-align:center; width: 100%; height: 75px;\">" +
				"<span style=\"vertical-align:middle; display: table-cell;\"><img style=\"width: " + imageWidth + "px;\" src=\"" +imageUrl + "\" /></span>" +
				"</div>";
		setHTML(html);
		setStyleName("vreButton");

		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Location.assign(vre.getFriendlyURL());
			}					
		}); 			
	}

}
