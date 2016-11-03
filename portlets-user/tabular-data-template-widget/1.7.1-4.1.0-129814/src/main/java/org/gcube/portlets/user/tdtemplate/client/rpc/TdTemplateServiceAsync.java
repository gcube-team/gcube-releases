package org.gcube.portlets.user.tdtemplate.client.rpc;

import java.util.List;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdtemplate.shared.ClientReportTemplateSaved;
import org.gcube.portlets.user.tdtemplate.shared.TdBehaviourModel;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TdFlowModel;
import org.gcube.portlets.user.tdtemplate.shared.TdLicenceModel;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType;
import org.gcube.portlets.user.tdtemplate.shared.TdTTimePeriod;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateUpdater;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;
import org.gcube.portlets.user.tdtemplate.shared.validator.ViolationDescription;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataActionDescription;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TdTemplateServiceAsync{

	void getTemplateTypes(AsyncCallback<List<TdTTemplateType>> callback);

	void submitTemplate(List<TdColumnDefinition> listColumns,
			TdFlowModel flowAttached, boolean save, List<TabularDataAction> actions, AsyncCallback<ClientReportTemplateSaved> callback);

	void getColumnCategoryByTdTemplateDefinition(
			TdTemplateDefinition templateDefinition, boolean isValidTemplate,
			AsyncCallback<List<TdTColumnCategory>> callback);

	void getOnErrorValues(AsyncCallback<List<String>> callback);

	void getConstraintForTemplateType(TdTTemplateType type,
			AsyncCallback<String> callback);

	void resolveColumnForDimension(TRId trId,
			AsyncCallback<List<ColumnData>> callback);

	void getTemplateUpdaterForTemplateId(long templateId,
			AsyncCallback<TdTemplateUpdater> callback);

	void updateTemplate(List<TdColumnDefinition> listColumns, boolean save,
			List<TabularDataAction> actions,
			TdFlowModel flowAttached, AsyncCallback<ClientReportTemplateSaved> callback);

	void getAllowedLocales(AsyncCallback<List<String>> callback);

	void isValidTemplate(List<TdTColumnCategory> columns,
			AsyncCallback<Boolean> callback);

	void getTemplateConstraintsViolations(
			AsyncCallback<List<ViolationDescription>> callback);

	void getTemplateHelper(AsyncCallback<String> callback);

	void getTimeDimensionPeriodTypes(AsyncCallback<List<TdTTimePeriod>> asyncCallback);

	void getLicences(AsyncCallback<List<TdLicenceModel>> callback);
	
	void getBehaviours(AsyncCallback<List<TdBehaviourModel>> callback);
	
	void getFlowByTemplateId(long templateId, AsyncCallback<TdFlowModel> flow);

	void executeTabularDataAction(TabularDataAction action,
			AsyncCallback<List<TdColumnDefinition>> callback);

	void removeLastAction(AsyncCallback<List<TdColumnDefinition>> callback);

	void saveTemplate(boolean isUpdated, AsyncCallback<Void> callback);

	/**
	 * 
	 */
	void getAppliedActionsOnTemplate(AsyncCallback<List<TabularDataActionDescription>> callback);

	void reloadColumns(AsyncCallback<List<TdColumnDefinition>> callback);
	
	void addColumnAction(TdColumnDefinition column,
			TemplateExpression templateExpression, AsyncCallback<List<TdColumnDefinition>> callback);

	/**
	 * @param column
	 * @param asyncCallback
	 */
	void deleteColumnAction(TdColumnDefinition column, AsyncCallback<List<TdColumnDefinition>> asyncCallback);

	void changeLabel(int columnIndex, String newLabel, AsyncCallback<Boolean> callback);

	/**
	 * 
	 */
	void removeAllActions(AsyncCallback<Boolean> callback);

	/**
	 * @param templateExpression
	 * @param asyncCallback
	 */
	void tableRuleAction(TemplateExpression templateExpression, AsyncCallback<List<TdColumnDefinition>> asyncCallback);

	void saveTemplateAs(String newTemplateName, AsyncCallback<Void> callback);
}
