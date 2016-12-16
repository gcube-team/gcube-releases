package org.gcube.portlet.user.my_vres.client.widgets;

import org.gcube.portlet.user.my_vres.shared.VRE;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * @author Massimiliano Assante - ISTI CNR
 *
 */
public class ClickableVRE extends HTML {

	private final static int WIDTH = 90;
	private final static int HEIGHT = 100;

	private String name;
	private String imageUrl;
	private int imageWidth = 0;

	public static final String LOADING_IMAGE = GWT.getModuleBaseURL() + "../images/loading.gif";
	public static final String VLAB_IMAGE = GWT.getModuleBaseURL() + "../images/vlab.png";
	private  	String html = "";


	public ClickableVRE() {
		super();
	}

	public ClickableVRE(final VRE vre, final boolean showImage) {
		super.setPixelSize(WIDTH, HEIGHT);
		setPixelSize(WIDTH, HEIGHT);
		if (vre.getName() == null || vre.getName().compareTo("") == 0) {
			html = "<div class=\"more-vre\"></div>";
		} else {
			imageWidth = WIDTH - 12;
			name = (vre.getName().length() > 15) ? vre.getName().substring(0, 13) + ".." : vre.getName();
			imageUrl = vre.getImageURL();
			html = "<div class=\"vreCaption\">" +name + "</div>";
			if (showImage) {
				html +=  "<div style=\"display: table; text-align:center; width: 100%; height: 75px;\">" +
						"<span style=\"vertical-align:middle; display: table-cell;\"><img style=\"width: " + imageWidth + "px;\" src=\"" +imageUrl + "\" /></span>" +
						"</div>";
			} else {
				html += "<div style=\"display: table; text-align:center; width: 100%; height: 75px;\" class=\"vlab-default\"></div>";
			}
		}
		this.setTitle("Enter");

		setHTML(html);
		setStyleName("vreButton");

		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String html =  "<div style=\"display: table; text-align:center; width: 100%; height: 75px;\">" +
						"<span style=\"vertical-align:middle; display: table-cell;\">redirecting ...</span>" +
						"</div>";
				setHTML(html);
				Timer timer = new Timer() {
					@Override
					public void run() {
						Location.assign(vre.getFriendlyURL());
					}
				};
				timer.schedule(50);
			}					
		}); 

		addMouseOverHandler(new MouseOverHandler() {			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if (!showImage) {
					int imageWidth = WIDTH - 12;
					String name = (vre.getName().length() > 15) ? vre.getName().substring(0, 13) + ".." : vre.getName();
					String imageUrl = vre.getImageURL();
					String html = "<div class=\"vreCaption\">" +name + "</div>";

					html +=  "<div style=\"display: table; text-align:center; width: 100%; height: 75px;\">" +
							"<span style=\"vertical-align:middle; display: table-cell;\"><img style=\"width: " + imageWidth + "px;\" src=\"" +imageUrl + "\" /></span>" +
							"</div>";
					setHTML(html);
				} 

			}
		});
	}

}
