package org.gcube.portlets.user.td.codelistmappingimportwidget.client.dataresource;



import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


public interface ResourceBundle extends ClientBundle {
	
	public static final ResourceBundle INSTANCE=GWT.create(ResourceBundle.class);
	
	@Source("resources/CodelistMappingImportWizardTD.css")
	CodelistMappingImportCSS importCss();
	
	@Source("resources/arrow-refresh.png")
	ImageResource refresh();
	
	@Source("resources/arrow-refresh_16.png")
	ImageResource refresh_16();
	
	@Source("resources/arrow-refresh_32.png")
	ImageResource refresh32();
	
	@Source("resources/accept.png")
	ImageResource csvCheckSuccess();

	@Source("resources/error.png")
	ImageResource csvCheckFailure();

	@Source("resources/loading.gif")
	ImageResource loading();
	
	@Source("resources/information.png")
	ImageResource information();
	
	@Source("resources/delete.png")
	ImageResource delete();
	
	@Source("resources/delete_32.png")
	ImageResource delete32();
}
 