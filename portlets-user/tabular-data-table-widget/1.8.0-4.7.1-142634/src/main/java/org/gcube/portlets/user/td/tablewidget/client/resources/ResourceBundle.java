package org.gcube.portlets.user.td.tablewidget.client.resources;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resource Bundle
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface ResourceBundle extends ClientBundle {
	
	public static final ResourceBundle INSTANCE=GWT.create(ResourceBundle.class);
	
	@Source("template-apply.png")
	ImageResource templateApply();
	
	@Source("template-apply_32.png")
	ImageResource templateApply32();
	
	@Source("template-delete.png")
	ImageResource templateDelete();
	
	@Source("template-apply_32.png")
	ImageResource templateDelete32();
	
	@Source("close-red.png")
	ImageResource close();
	
	@Source("close-red_32.png")
	ImageResource close32();
	
	@Source("disk.png")
	ImageResource save();
	
	@Source("disk_32.png")
	ImageResource save32();
	
	@Source("arrow-refresh.png")
	ImageResource refresh();
	
	@Source("arrow-refresh_32.png")
	ImageResource refresh32();
	
	@Source("exit.png")
	ImageResource exit();
	
	@Source("exit_32.png")
	ImageResource exit32();
	
	@Source("error.png")
	ImageResource error();
	
	@Source("error_32.png")
	ImageResource error32();
	
	@Source("ok.png")
	ImageResource ok();
	
	@Source("ok_32.png")
	ImageResource ok32();
	
	@Source("arrow-undo.png")
	ImageResource undo();
	
	@Source("arrow-undo_32.png")
	ImageResource undo32();
	
	@Source("arrow-undo-all.png")
	ImageResource undoAll();
	
	@Source("arrow-undo-all_32.png")
	ImageResource undoAll32();
	
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
	
	
	@Source("pencil.png")
	ImageResource rowEdit();
	
	@Source("pencil_32.png")
	ImageResource rowEdit32();
	
	@Source("table-row-insert_32.png")
	ImageResource rowInsert32();
	
	@Source("table-row-insert.png")
	ImageResource rowInsert();
	
	@Source("plaster_32.png")
	ImageResource plaster32();
	
	@Source("plaster.png")
	ImageResource plaster();

	@Source("magnifier-zoom-in_32.png")
	ImageResource magnifierZoomIn32();
	
	@Source("magnifier-zoom-in.png")
	ImageResource magnifierZoomIn();
	
	@Source("magnifier-zoom-out_32.png")
	ImageResource magnifierZoomOut32();
	
	@Source("magnifier-zoom-out.png")
	ImageResource magnifierZoomOut();
	
	@Source("table-normalize.png")
	ImageResource tableNormalize();
	
	@Source("table-normalize_32.png")
	ImageResource tableNormalize32();
	
	@Source("table-denormalize.png")
	ImageResource tableDenormalize();
	
	@Source("table-denormalize_32.png")
	ImageResource tableDenormalize32();
	
	@Source("table-duplicate-rows.png")
	ImageResource tableDuplicateRows();
	
	@Source("table-duplicate-rows_32.png")
	ImageResource tableDuplicateRows32();
	
	@Source("table-duplicate-rows-remove.png")
	ImageResource tableDuplicateRowsRemove();
	
	@Source("table-duplicate-rows-remove_32.png")
	ImageResource tableDuplicateRowsRemove32();
	
	@Source("table-type.png")
	ImageResource tableType();
	
	@Source("table-type_32.png")
	ImageResource tableType32();
	
	@Source("flag-red_32.png")
	ImageResource geospatialCSquare32();

	@Source("flag-red.png")
	ImageResource geospatialCSquare();
	
	@Source("flag-blue_32.png")
	ImageResource geospatialOceanArea32();

	@Source("flag-blue.png")
	ImageResource geospatialOceanArea();
	
	
	@Source("flag-green_32.png")
	ImageResource geospatialCoordinates32();

	@Source("flag-green.png")
	ImageResource geospatialCoordinates();
	
	@Source("point_32.png")
	ImageResource geometryPoint32();

	@Source("point.png")
	ImageResource geometryPoint();
	

	@Source("downscale-csquare_32.png")
	ImageResource downscaleCSquare32();

	@Source("downscale-csquare.png")
	ImageResource downscaleCSquare();

	

}