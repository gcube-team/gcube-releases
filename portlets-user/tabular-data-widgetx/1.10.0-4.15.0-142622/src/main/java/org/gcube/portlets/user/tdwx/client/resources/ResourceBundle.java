package org.gcube.portlets.user.tdwx.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface ResourceBundle extends ClientBundle {

	public static final ResourceBundle INSTANCE = GWT
			.create(ResourceBundle.class);

	@Source("TDGrid.css")
	TDGridCSS tdGridCSS();
	
	
	@Source("text-s-contains_32.png")
	ImageResource textContains32();

	@Source("text-s-contains.png")
	ImageResource textContains();

	@Source("text-s-begins_32.png")
	ImageResource textBegins32();

	@Source("text-s-begins.png")
	ImageResource textBegins();

	@Source("text-s-ends_32.png")
	ImageResource textEnds32();

	@Source("text-s-ends.png")
	ImageResource textEnds();
	
	@Source("text-soundex_32.png")
	ImageResource textSoundex32();

	@Source("text-soundex.png")
	ImageResource textSoundex();
	
}