package org.gcube.portlets.user.td.unionwizardwidget.client.resources;



import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface UnionResourceBundle extends ClientBundle {
	
	public static final UnionResourceBundle INSTANCE=GWT.create(UnionResourceBundle.class);
	
	@Source("UnionWizardTD.css")
	UnionCSS unionCss();
	
	@Source("arrow-refresh.png")
	ImageResource refresh();
	
	@Source("arrow-refresh_16.png")
	ImageResource refresh_16();
	
	@Source("arrow-refresh_32.png")
	ImageResource refresh32();
	
	@Source("accept.png")
	ImageResource csvCheckSuccess();

	@Source("error.png")
	ImageResource csvCheckFailure();

	@Source("loading.gif")
	ImageResource loading();
	
	@Source("information.png")
	ImageResource information();
	
	@Source("add.png")
	ImageResource add();
	
	@Source("add_32.png")
	ImageResource add32();
	
	@Source("delete.png")
	ImageResource delete();
	
	@Source("delete_32.png")
	ImageResource delete32();

}
 