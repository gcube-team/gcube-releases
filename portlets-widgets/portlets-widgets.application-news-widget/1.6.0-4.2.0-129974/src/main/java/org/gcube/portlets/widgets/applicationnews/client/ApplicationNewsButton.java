package org.gcube.portlets.widgets.applicationnews.client;

import org.gcube.portlets.widgets.applicationnews.client.bundles.CssAndImages;
import org.gcube.portlets.widgets.applicationnews.shared.LinkPreview;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Dec 2012
 *
 * display a button to share updates from within your application, the update will be published in the Users News Feed belonging to the VRE your applicationProfile runs into 
 */
public class ApplicationNewsButton extends Button {
	static {
		CssAndImages.INSTANCE.css().ensureInjected();
	}
	/**
	 * 
	 * @param portletClassName your servlet class name will be used ad unique identifier for your applicationProfile
	 * @param textToShow description for the update you are sharing
	 */
	public ApplicationNewsButton(final String portletClassName, final String feedtext) {
		this(portletClassName, feedtext, "", null);		
	}
	/**
	 * 
	 * @param portletClassName your servlet class name will be used ad unique identifier for your applicationProfile
	 * @param textToShow description for the update you are sharing
	 * @param uriGETparams additional parameters if your application supports the direct opening of of this update's object  e.g. id=12345&type=foo
	 */
	public ApplicationNewsButton(final String portletClassName, final String feedtext, String uriGETparams) {
		this(portletClassName, feedtext, uriGETparams, null);		
	}
	/**
	 * 
	 * @param portletClassName your servlet class name will be used ad unique identifier for your applicationProfile
	 * @param textToShow description for the update you are sharing
	 * @param uriGETparams additional parameters if your application supports the direct opening of of this update's object  e.g. id=12345&type=foo
	 * @param linkPreview the linkPreview object
	 */
	public ApplicationNewsButton(final String portletClassName, final String textToShow, final String uriGETparams, final LinkPreview linkPreview) {
		super("Post App News", new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				if (uriGETparams == null || uriGETparams.isEmpty())
					new PostAppNewsDialog(portletClassName, textToShow);	
				else if (linkPreview == null)
					new PostAppNewsDialog(portletClassName, textToShow, uriGETparams);	
				else
					new PostAppNewsDialog(portletClassName, textToShow, uriGETparams, linkPreview);	
			}
		});
		this.addStyleName("buttonBackground");
		this.addStyleName("newsbutton");
	}
}
