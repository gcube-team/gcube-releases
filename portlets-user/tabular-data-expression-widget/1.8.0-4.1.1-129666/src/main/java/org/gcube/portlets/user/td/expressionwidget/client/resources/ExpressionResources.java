package org.gcube.portlets.user.td.expressionwidget.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ExpressionResources extends ClientBundle {

	public static final ExpressionResources INSTANCE = GWT
			.create(ExpressionResources.class);

	@Source("add.png")
	ImageResource add();

	@Source("delete.png")
	ImageResource delete();

	@Source("wrench-database.png")
	ImageResource save();

	@Source("close-red.png")
	ImageResource close();

	@Source("rule-add.png")
	ImageResource ruleAdd();

	@Source("rule-add_32.png")
	ImageResource ruleAdd32();

	@Source("rule-edit_32.png")
	ImageResource ruleEdit32();

	@Source("rule-edit.png")
	ImageResource ruleEdit();

	@Source("rule-column-add.png")
	ImageResource ruleColumnAdd();

	@Source("rule-column-add_32.png")
	ImageResource ruleColumnAdd32();

	@Source("rule-table-add.png")
	ImageResource ruleTableAdd();

	@Source("rule-table-add_32.png")
	ImageResource ruleTableAdd32();

	@Source("rule-table-apply.png")
	ImageResource ruleTableApply();

	@Source("rule-table-apply_32.png")
	ImageResource ruleTableApply32();

	@Source("wrench-go.png")
	ImageResource apply();

	@Source("wrench-manage.png")
	ImageResource search();

	@Source("column-filter.png")
	ImageResource filter();

	@Source("column-filter-go.png")
	ImageResource applyFilter();

	@Source("column-replace-by-expression_32.png")
	ImageResource columnReplaceByExpression32();

	@Source("column-replace-by-expression.png")
	ImageResource columnReplaceByExpression();

	@Source("table-row-delete-byexpression_32.png")
	ImageResource tableRowDeleteByExpression32();

	@Source("table-row-delete-byexpression.png")
	ImageResource tableRowDeleteByExpression();

}