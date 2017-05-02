/**
 * 
 */
package org.gcube.portlets.user.td.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface TabularDataResources extends ClientBundle {

	public static final TabularDataResources INSTANCE = GWT
			.create(TabularDataResources.class);
	
	@Source("star_32.png")
	ImageResource test32();

	@Source("star.png")
	ImageResource test();

	
	@Source("arrow-refresh_32.png")
	ImageResource refresh32();

	@Source("arrow-refresh.png")
	ImageResource refresh();

	@Source("page-white-add.png")
	ImageResource trOpen();
	
	@Source("page-white-add_32.png")
	ImageResource trOpen32();

	@Source("page-white-close_32.png")
	ImageResource trClose32();

	@Source("page-white-close.png")
	ImageResource trClose();

	@Source("page-white-share_32.png")
	ImageResource trShare32();

	@Source("page-white-share.png")
	ImageResource trShare();

	@Source("disk.png")
	ImageResource save();

	@Source("disk_32.png")
	ImageResource save32();
	
	@Source("close.png")
	ImageResource close();

	@Source("close_32.png")
	ImageResource close32();

	@Source("properties.png")
	ImageResource properties();

	@Source("sdmx.png")
	ImageResource sdmx();

	@Source("sdmx_32.png")
	ImageResource sdmx32();

	@Source("gis.png")
	ImageResource gis();

	@Source("gis_32.png")
	ImageResource gis32();

	@Source("csv.png")
	ImageResource csv();
	
	@Source("csv_32.png")
	ImageResource csv32();

	@Source("json.png")
	ImageResource json();
	
	@Source("json_32.png")
	ImageResource json32();

	@Source("chart-bar.png")
	ImageResource chart();

	@Source("chart-bar_32.png")
	ImageResource chartBar32();

	@Source("chart-bulls.png")
	ImageResource chartBulls();

	@Source("chart-bulls_32.png")
	ImageResource chartBulls32();

	@Source("chart-curve.png")
	ImageResource chartCurve();

	@Source("chart-curve_32.png")
	ImageResource chartCurve32();

	@Source("chart-pie.png")
	ImageResource chartPie();

	@Source("chart-pie_32.png")
	ImageResource chartPie32();

	@Source("RStudio.png")
	ImageResource rstudio();

	@Source("RStudio_32.png")
	ImageResource rstudio32();

	@Source("statistical.png")
	ImageResource statistical();

	@Source("statistical_32.png")
	ImageResource statistical32();

		
	@Source("table-validation_32.png")
	ImageResource validation32();

	@Source("table-validation.png")
	ImageResource validation();
	
	@Source("table-validation-delete_32.png")
	ImageResource validationDelete32();

	@Source("table-validation-delete.png")
	ImageResource validationDelete();
	
	
	@Source("rule-add_32.png")
	ImageResource ruleAdd32();

	@Source("rule-add.png")
	ImageResource ruleAdd();
	
	@Source("rule-edit_32.png")
	ImageResource ruleEdit32();

	@Source("rule-edit.png")
	ImageResource ruleEdit();
	
	@Source("rule-close_32.png")
	ImageResource ruleClose32();

	@Source("rule-close.png")
	ImageResource ruleClose();

	@Source("rule-open_32.png")
	ImageResource ruleOpen32();

	@Source("rule-open.png")
	ImageResource ruleOpen();

	@Source("rule-delete.png")
	ImageResource ruleDelete();

	@Source("rule-delete_32.png")
	ImageResource ruleDelete32();

	@Source("rule-apply.png")
	ImageResource ruleApply();

	@Source("rule-apply_32.png")
	ImageResource ruleApply32();

	@Source("rule-share.png")
	ImageResource ruleShare();

	@Source("rule-share_32.png")
	ImageResource ruleShare32();

	@Source("rule-column-add.png")
	ImageResource ruleColumnAdd();
	
	@Source("rule-column-add_32.png")
	ImageResource ruleColumnAdd32();
	
	@Source("rule-column-apply.png")
	ImageResource ruleColumnApply();
	
	@Source("rule-column-apply_32.png")
	ImageResource ruleColumnApply32();
	
	@Source("rule-column-detach.png")
	ImageResource ruleColumnDetach();
	
	@Source("rule-column-detach_32.png")
	ImageResource ruleColumnDetach32();
	
	@Source("rule-table-add.png")
	ImageResource ruleTableAdd();
	
	@Source("rule-table-add_32.png")
	ImageResource ruleTableAdd32();
	
	@Source("rule-table-apply.png")
	ImageResource ruleTableApply();
	
	@Source("rule-table-apply_32.png")
	ImageResource ruleTableApply32();
	
	@Source("rule-tabularresource.png")
	ImageResource ruleTabularResource();
	
	@Source("rule-tabularresource_32.png")
	ImageResource ruleActive32();
	

	@Source("table-filter_32.png")
	ImageResource filter32();

	@Source("table-union_32.png")
	ImageResource union32();

	@Source("table-denormalize_32.png")
	ImageResource tableDenormalize32();

	@Source("table-denormalize.png")
	ImageResource tableDenormalize();

	@Source("table-normalize_32.png")
	ImageResource tableNormalize32();

	@Source("table-normalize.png")
	ImageResource tableNormalize();

	@Source("table-expand_32.png")
	ImageResource tableExpand32();

	@Source("table-expand.png")
	ImageResource tableExpand();

	@Source("table-group_32.png")
	ImageResource group32();

	@Source("table-aggregate.png")
	ImageResource aggregate();

	@Source("table-aggregate_32.png")
	ImageResource aggregate32();
	
	@Source("table-time-aggregate.png")
	ImageResource timeAggregate();

	@Source("table-time-aggregate_32.png")
	ImageResource timeAggregate32();

	@Source("history_32.png")
	ImageResource history32();

	@Source("arrow-undo_32.png")
	ImageResource discard32();

	@Source("arrow-undo.png")
	ImageResource discard();

	@Source("arrow-undo-all_32.png")
	ImageResource discardAll32();

	@Source("arrow-undo-all.png")
	ImageResource discardAll();

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

	@Source("column-values_32.png")
	ImageResource columnValues32();

	@Source("column-values.png")
	ImageResource columnValues();

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

	@Source("column_32.png")
	ImageResource column32();

	@Source("column.png")
	ImageResource column();
	
	@Source("column-validation-delete_32.png")
	ImageResource columnValidationDelete32();

	@Source("column-validation-delete.png")
	ImageResource columnValidationDelete();

	@Source("cog_32.png")
	ImageResource cog32();

	@Source("cog.png")
	ImageResource cog();

	@Source("delete_32.png")
	ImageResource delete32();

	@Source("delete.png")
	ImageResource delete();

	@Source("validate-add_32.png")
	ImageResource validateAdd32();

	@Source("validate-add.png")
	ImageResource validateAdd();

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
	
	@Source("table-row-delete-byexpression_32.png")
	ImageResource tableRowDeleteByExpression32();

	@Source("table-row-delete-byexpression.png")
	ImageResource tableRowDeleteByExpression();
	
	@Source("table-replace-rows_32.png")
	ImageResource tableReplaceRows32();

	@Source("table-replace-rows.png")
	ImageResource tableReplaceRows();

	@Source("help_32.png")
	ImageResource help32();

	@Source("help.png")
	ImageResource help();

	@Source("information_32.png")
	ImageResource information32();

	@Source("information.png")
	ImageResource information();
	
	@Source("logs_32.png")
	ImageResource logs32();

	@Source("logs.png")
	ImageResource logs();

	@Source("template-add_32.png")
	ImageResource templateAdd32();

	@Source("template-add.png")
	ImageResource templateAdd();
	
	@Source("template-close_32.png")
	ImageResource templateClose32();

	@Source("template-close.png")
	ImageResource templateClose();

	@Source("template-edit_32.png")
	ImageResource templateEdit32();

	@Source("template-edit.png")
	ImageResource templateEdit();

	@Source("template-delete.png")
	ImageResource templateDelete();

	@Source("template-delete_32.png")
	ImageResource templateDelete32();

	@Source("template-apply.png")
	ImageResource templateApply();

	@Source("template-apply_32.png")
	ImageResource templateApply32();

	@Source("template-share.png")
	ImageResource templateShare();

	@Source("template-share_32.png")
	ImageResource templateShare32();

	@Source("timeline_32.png")
	ImageResource timeline32();

	@Source("timeline.png")
	ImageResource timeline();

	@Source("table-duplicate-rows_32.png")
	ImageResource tableDuplicateRows32();

	@Source("table-duplicate-rows.png")
	ImageResource tableDuplicateRows();

	@Source("table-duplicate-rows-remove_32.png")
	ImageResource tableDuplicateRowsRemove32();

	@Source("table-duplicate-rows-remove.png")
	ImageResource tableDuplicateRowsRemove();

	@Source("table-type_32.png")
	ImageResource tableType32();

	@Source("table-type.png")
	ImageResource tableType();

	@Source("codelist_32.png")
	ImageResource codelist32();

	@Source("codelist.png")
	ImageResource codelist();

	@Source("summary_32.png")
	ImageResource summary32();

	@Source("summary.png")
	ImageResource summary();

	@Source("tag-blue-add_32.png")
	ImageResource annotationAdd32();

	@Source("tag-blue-add.png")
	ImageResource annotationAdd();

	@Source("tag-blue-delete_32.png")
	ImageResource annotationDelete32();

	@Source("tag-blue-delete.png")
	ImageResource annotationDelete();

	@Source("table-clone_32.png")
	ImageResource tableClone32();

	@Source("table-clone.png")
	ImageResource tableClone();

	@Source("tabular-resource-clone_32.png")
	ImageResource clone32();

	@Source("tabular-resource-clone.png")
	ImageResource clone();

	@Source("codelistmapping_32.png")
	ImageResource codelistMapping32();

	@Source("codelistmapping.png")
	ImageResource codelistMapping();

	@Source("column-split_32.png")
	ImageResource columnSplit32();

	@Source("column-split.png")
	ImageResource columnSplit();

	@Source("column-merge_32.png")
	ImageResource columnMerge32();

	@Source("column-merge.png")
	ImageResource columnMerge();

	@Source("basket-background_32.png")
	ImageResource basketBackground32();

	@Source("basket-background.png")
	ImageResource basketBackground();

	@Source("column-replace-by-expression_32.png")
	ImageResource columnReplaceByExpression32();

	@Source("column-replace-by-expression.png")
	ImageResource columnReplaceByExpression();

	@Source("table-replace-by-external-col_32.png")
	ImageResource replaceByExternalCol32();

	@Source("table-replace-by-external-col.png")
	ImageResource replaceByExternalCol();

	
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
	
	
	@Source("downscale-csquare_32.png")
	ImageResource downscaleCSquare32();

	@Source("downscale-csquare.png")
	ImageResource downscaleCSquare();
	
	@Source("point_32.png")
	ImageResource geometryPoint32();

	@Source("point.png")
	ImageResource geometryPoint();
	
	@Source("flag_gb.png")
	ImageResource flagEN();
	
	@Source("flag_it.png")
	ImageResource flagIT();
	
	@Source("flag_es.png")
	ImageResource flagES();
	
	@Source("sflag_en.png")
	ImageResource sflagEN();
	
	@Source("sflag_it.png")
	ImageResource sflagIT();
	
	@Source("sflag_es.png")
	ImageResource sflagES();
	
	
	@Source("language_32.png")
	ImageResource language32();
	
	@Source("language.png")
	ImageResource language();
	
}
