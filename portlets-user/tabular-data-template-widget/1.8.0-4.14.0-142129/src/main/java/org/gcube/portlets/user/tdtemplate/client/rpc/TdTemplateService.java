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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("tdtemplate")
public interface TdTemplateService extends RemoteService {

	/**
	 * @return
	 */
	List<TdTTemplateType> getTemplateTypes();

	List<TdTColumnCategory> getColumnCategoryByTdTemplateDefinition(TdTemplateDefinition templateDefinition, boolean isValidTemplate) throws Exception;

	ClientReportTemplateSaved submitTemplate(
			List<TdColumnDefinition> listColumns, TdFlowModel flowAttached,
			boolean save, List<TabularDataAction> actions) throws Exception;

	/**
	 * @return
	 */
	List<String> getOnErrorValues();

	/**
	 * @param type
	 * @return
	 */
	String getConstraintForTemplateType(TdTTemplateType type);

	/**
	 * @param trId
	 * @return
	 * @throws Exception
	 */
	List<ColumnData> resolveColumnForDimension(TRId trId) throws Exception;

	/**
	 * @param templateId
	 * @return
	 * @throws Exception
	 */
	TdTemplateUpdater getTemplateUpdaterForTemplateId(long templateId)
			throws Exception;

	ClientReportTemplateSaved updateTemplate(
			List<TdColumnDefinition> listColumns, boolean save,
			List<TabularDataAction> actions, TdFlowModel flowAttached) throws Exception;

	/**
	 * @return
	 */
	List<String> getAllowedLocales();

	/**
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	boolean isValidTemplate(List<TdTColumnCategory> columns) throws Exception;

	/**
	 * @return
	 */
	List<ViolationDescription> getTemplateConstraintsViolations();

	/**
	 * @return
	 */
	String getTemplateHelper();

	/**
	 * @return
	 */
	List<TdTTimePeriod> getTimeDimensionPeriodTypes();

	/**
	 * @return
	 * @throws Exception
	 */
	List<TdLicenceModel> getLicences() throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	List<TdBehaviourModel> getBehaviours() throws Exception;

	/**
	 * @param templateId
	 * @return
	 * @throws Exception
	 */
	TdFlowModel getFlowByTemplateId(long templateId) throws Exception;

	/**
	 * @param listColumns
	 * @param flowAttached
	 * @param action
	 * @return
	 * @throws Exception
	 */
	List<TdColumnDefinition> executeTabularDataAction(TabularDataAction action) throws Exception;

	List<TdColumnDefinition> removeLastAction() throws Exception;

	void saveTemplate(boolean isUpdated) throws Exception;

	List<TabularDataActionDescription> getAppliedActionsOnTemplate() throws Exception;

	List<TdColumnDefinition> reloadColumns() throws Exception;

	List<TdColumnDefinition> addColumnAction(TdColumnDefinition column,
			TemplateExpression templateExpression) throws Exception;

	List<TdColumnDefinition> deleteColumnAction(TdColumnDefinition column) throws Exception;

	/**
	 * @param columnIndex
	 * @param newLabel
	 * @return
	 * @throws Exception
	 */
	boolean changeLabel(int columnIndex, String newLabel) throws Exception;

	boolean removeAllActions() throws Exception;

	List<TdColumnDefinition> tableRuleAction(TemplateExpression templateExpression) throws Exception;

	/**
	 * @param newTemplateName
	 * @throws Exception
	 */
	void saveTemplateAs(String newTemplateName) throws Exception;
}
