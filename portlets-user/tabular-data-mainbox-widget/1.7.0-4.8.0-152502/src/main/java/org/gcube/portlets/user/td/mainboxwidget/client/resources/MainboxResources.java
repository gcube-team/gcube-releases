/**
 * 
 */
package org.gcube.portlets.user.td.mainboxwidget.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface MainboxResources extends ClientBundle {

	public static final MainboxResources INSTANCE = GWT
			.create(MainboxResources.class);

	@Source("column-type_32.png")
	ImageResource columnType32();

	@Source("column-type.png")
	ImageResource columnType();

	@Source("column-label_32.png")
	ImageResource columnLabel32();

	@Source("column-label.png")
	ImageResource columnLabel();

	@Source("column-filter_32.png")
	ImageResource columnFilter32();

	@Source("column-filter.png")
	ImageResource columnFilter();

	@Source("column-edit_32.png")
	ImageResource columnEdit32();

	@Source("column-edit.png")
	ImageResource columnEdit();

	@Source("column-delete_32.png")
	ImageResource columnDelete32();

	@Source("column-delete.png")
	ImageResource columnDelete();

	@Source("column-add_32.png")
	ImageResource columnAdd32();

	@Source("column-add.png")
	ImageResource columnAdd();

	@Source("column-reorder_32.png")
	ImageResource columnReorder32();

	@Source("column-reorder.png")
	ImageResource columnReorder();

	@Source("column-replace.png")
	ImageResource columnReplace();

	@Source("column-replace_32.png")
	ImageResource columnReplace32();

	@Source("column-replace-all.png")
	ImageResource columnReplaceAll();

	@Source("column-replace-all_32.png")
	ImageResource columnReplaceAll32();

	@Source("column-replace-batch.png")
	ImageResource columnReplaceBatch();

	@Source("column-replace-batch_32.png")
	ImageResource columnReplaceBatch32();

	@Source("pencil_32.png")
	ImageResource rowEdit32();

	@Source("pencil.png")
	ImageResource rowEdit();

	@Source("table-row-insert_32.png")
	ImageResource rowInsert32();

	@Source("table-row-insert.png")
	ImageResource rowInsert();

	@Source("table-row-delete_32.png")
	ImageResource tableRowDelete32();

	@Source("table-row-delete.png")
	ImageResource tableRowDelete();

	@Source("table-row-delete-selected_32.png")
	ImageResource tableRowDeleteSelected32();

	@Source("table-row-delete-selected.png")
	ImageResource tableRowDeleteSelected();

	@Source("column-split_32.png")
	ImageResource columnSplit32();

	@Source("column-split.png")
	ImageResource columnSplit();

	@Source("column-merge_32.png")
	ImageResource columnMerge32();

	@Source("column-merge.png")
	ImageResource columnMerge();

	@Source("column-replace-by-expression_32.png")
	ImageResource columnReplaceByExpression32();

	@Source("column-replace-by-expression.png")
	ImageResource columnReplaceByExpression();

	@Source("page-white_32.png")
	ImageResource tabularResource32();

	@Source("page-white.png")
	ImageResource tabularResource();

	@Source("table-replace-by-external-col_32.png")
	ImageResource columnReplaceByExternal32();

	@Source("table-replace-by-external-col.png")
	ImageResource columnReplaceByExternal();

	@Source("downscale-csquare_32.png")
	ImageResource downscaleCSquare32();

	@Source("downscale-csquare.png")
	ImageResource downscaleCSquare();

	@Source("resources_32.png")
	ImageResource resources32();

	@Source("resources.png")
	ImageResource resources();
	
	@Source("table_32.png")
	ImageResource table32();

	@Source("table.png")
	ImageResource table();

}
