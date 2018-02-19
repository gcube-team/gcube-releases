package org.gcube.portlets.user.td.replacebyexternalwidget.client.resources;



import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


public interface ReplaceByExternalResourceBundle extends ClientBundle {
	
	public static final ReplaceByExternalResourceBundle INSTANCE=GWT.create(ReplaceByExternalResourceBundle.class);
	
	@Source("ReplaceByExternal.css")
	ReplaceByExternalCSS replaceByExternalCss();
	
	@Source("arrow-refresh.png")
	ImageResource refresh();
	
	@Source("arrow-refresh_16.png")
	ImageResource refresh_16();
	
	@Source("arrow-refresh_32.png")
	ImageResource refresh32();
	
	@Source("accept.png")
	ImageResource success();

	@Source("error.png")
	ImageResource failure();

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
	
	@Source("table-replace-by-external-col.png")
	ImageResource tableReplaceByExternal();
	
	@Source("table-replace-by-external-col_32.png")
	ImageResource tableReplaceByExternal32();

}
 