package org.gcube.portlets.user.td.monitorwidget.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ResourceBundle extends ClientBundle {

	public static final ResourceBundle INSTANCE = GWT
			.create(ResourceBundle.class);

	@Source("MonitorWidgetTD.css")
	MonitorCSS monitorCss();

	@Source("error.png")
	ImageResource error();

	@Source("error_32.png")
	ImageResource error32();

	@Source("ok.png")
	ImageResource ok();

	@Source("ok_32.png")
	ImageResource ok32();

	@Source("table-validation.png")
	ImageResource tableValidation();

	@Source("table-validation_32.png")
	ImageResource tableValidation32();

	@Source("cog.png")
	ImageResource cog();

	@Source("cog_32.png")
	ImageResource cog32();
	
	@Source("cog-preprocessing.png")
	ImageResource cogPreprocessing();
	
	@Source("cog-preprocessing_32.png")
	ImageResource cogPreprocessing32();
	
	@Source("cog-postprocessing.png")
	ImageResource cogPostprocessing();
	
	@Source("cog-postprocessing_32.png")
	ImageResource cogPostprocessing32();
	
	@Source("cog-datavalidation.png")
	ImageResource cogDataValidation();
	
	@Source("cog-datavalidation_32.png")
	ImageResource cogDataValidation32();
	
	@Source("basket.png")
	ImageResource basket();

	@Source("basket_32.png")
	ImageResource basket32();

	@Source("magnifier-zoom-in_32.png")
	ImageResource magnifierZoomIn32();

	@Source("magnifier-zoom-in.png")
	ImageResource magnifierZoomIn();

	@Source("magnifier-zoom-out_32.png")
	ImageResource magnifierZoomOut32();

	@Source("magnifier-zoom-out.png")
	ImageResource magnifierZoomOut();
	
	@Source("basket-delete.png")
	ImageResource basketDelete();

	@Source("basket-delete_32.png")
	ImageResource basketDelete32();
	
	@Source("basket-remove.png")
	ImageResource basketRemove();

	@Source("basket-remove_32.png")
	ImageResource basketRemove32();
	
	@Source("information.png")
	ImageResource information();

	@Source("information_32.png")
	ImageResource  information32();
	
}
