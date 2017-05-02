/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.event.operation.AddColumnOperationEvent;
import org.gcube.portlets.user.tdtemplate.client.event.operation.AddColumnOperationEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.operation.AggregateByTimeOperationEvent;
import org.gcube.portlets.user.tdtemplate.client.event.operation.AggregateByTimeOperationEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.operation.DeleteColumnOperationEvent;
import org.gcube.portlets.user.tdtemplate.client.event.operation.DeleteColumnOperationEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.operation.TableRuleOperationEvent;
import org.gcube.portlets.user.tdtemplate.client.event.operation.TableRuleOperationEventHandler;
import org.gcube.portlets.user.tdtemplate.client.event.operation.UndoLastOperationEvent;
import org.gcube.portlets.user.tdtemplate.client.event.operation.UndoLastOperationEventHandler;
import org.gcube.portlets.user.tdtemplate.client.templateactions.TemplatePanelActionEdit;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnDefinitionView;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;
import org.gcube.portlets.user.tdtemplateoperation.client.AggregateByTimeColumnDialog;
import org.gcube.portlets.user.tdtemplateoperation.client.event.ActionCompletedEvent;
import org.gcube.portlets.user.tdtemplateoperation.client.event.ActionCompletedEventHandler;
import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;
import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectType;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.CreateTimeDimensionColumnAction;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.NormalizeColumnsAction;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TimeAggregationColumnAction;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.SimpleEventBus;


/**
 * The Class TdTemplateControllerActions.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 10, 2015
 */
public class TdTemplateControllerActions {

	private List<TabularDataAction> actions = new ArrayList<TabularDataAction>();
	private SimpleEventBus actionsEventBus = new SimpleEventBus();
	private TdTemplateController templateController;
	
	/**
	 * Gets the template controller.
	 *
	 * @return the templateController
	 */
	public TdTemplateController getTemplateController() {
		return templateController;
	}

	private TemplatePanelActionEdit templatePanelActionUpdater;
	
	private AggregateByTimeColumnDialog aggregateByTimeDialog;
	
	/**
	 * Instantiates a new td template controller actions.
	 *
	 * @param baseController the base controller
	 * @param tPanelActionUpdater the t panel action updater
	 */
	public TdTemplateControllerActions(TdTemplateController baseController, TemplatePanelActionEdit tPanelActionUpdater) {
		this.templateController = baseController;
		this.templatePanelActionUpdater = tPanelActionUpdater;

		actionsEventBus.addHandler(ActionCompletedEvent.TYPE, new ActionCompletedEventHandler() {
	
				@Override
				public void onActionCompleted(final ActionCompletedEvent actionCompletedEvent) {
				
					if (actionCompletedEvent != null && actionCompletedEvent.getAction()!=null) {
	
						TabularDataAction action = actionCompletedEvent.getAction();
						
						if (action instanceof TimeAggregationColumnAction) {
							
							aggregateByTimeDialog.hide();
							
							final TimeAggregationColumnAction aggreg = (TimeAggregationColumnAction) action;
//							List<TdColumnDefinition> list = templateController.getTdColumnDefintions();
//							org.gcube.portlets.user.tdtemplate.shared.TdFlowModel flowAttached = TdTemplateController.getTdGeneretor().getTemplatePanel().getFlow();
							TdTemplateController.tdTemplateServiceAsync.executeTabularDataAction(aggreg, new AsyncCallback<List<TdColumnDefinition>>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
								}

								@Override
								public void onSuccess(List<TdColumnDefinition> result) {
									
									GWT.log("executeTabularDataAction return :");
//									for (TdColumnDefinition tdColumnDefinition : result) {
//										GWT.log(tdColumnDefinition.toString());
//									}
								
									templatePanelActionUpdater.updateColumns(result);
									addCompletedAction(aggreg);
//									GWT.log("getTimeDimensionCls return :");
//									GWT.log(templatePanelActionUpdater.getTimeDimensionCls().toString());
//									
//									GWT.log("getOthersColumns return :");
//									GWT.log(templatePanelActionUpdater.getOthersColumns().toString());
								}
							});
						}else if(action instanceof CreateTimeDimensionColumnAction){
							
							final CreateTimeDimensionColumnAction group =  (CreateTimeDimensionColumnAction) action; 
							
							TdTemplateController.tdTemplateServiceAsync.executeTabularDataAction(group, new AsyncCallback<List<TdColumnDefinition>>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
								}

								@Override
								public void onSuccess(List<TdColumnDefinition> result) {
									GWT.log("executeTabularDataAction return :");
									templatePanelActionUpdater.updateColumns(result);
									addCompletedAction(group);
								}
							});
						}else if(action instanceof NormalizeColumnsAction){
							
							final NormalizeColumnsAction normalize =  (NormalizeColumnsAction) action; 
							
							TdTemplateController.tdTemplateServiceAsync.executeTabularDataAction(normalize, new AsyncCallback<List<TdColumnDefinition>>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
								}

								@Override
								public void onSuccess(List<TdColumnDefinition> result) {
									GWT.log("executeTabularDataAction return :");
									templatePanelActionUpdater.updateColumns(result);
									addCompletedAction(normalize);
								}
							});
						}		
					}
				}
		});
		
		actionsEventBus.addHandler(AggregateByTimeOperationEvent.TYPE, new AggregateByTimeOperationEventHandler() {
			
			@Override
			public void onAggregateByTimeOp(AggregateByTimeOperationEvent aggregateByTimeOperationEvent) {
				
				if(aggregateByTimeOperationEvent.getTimeColumns()==null){
					GWT.log("aggregateByTimeOperationEvent.getTimeColumns()==null");
					return;
				}
				
				if(aggregateByTimeOperationEvent.getOtherColumns()==null || aggregateByTimeOperationEvent.getOtherColumns().size()==0){
					GWT.log("aggregateByTimeOperationEvent is null or empty");
					return;
				}
				//CONVERTING TIME COLUMNS
				List<TdColumnData> listTimeCL = new ArrayList<TdColumnData>(1);
				TdColumnDefinition cdef = aggregateByTimeOperationEvent.getTimeColumns();
//				ServerObjectId serId = new ServerObjectId(cdef.getIndex(), null, null, ServerObjectType.TEMPLATE);
//				TdColumnData td = new TdColumnData(serId, cdef.getIndex()+"", cdef.getColumnName(), cdef.getColumnName(), cdef.getDataType().getName());
//				td.setPeriodType(cdef.getTimePeriod().getName());
				listTimeCL.add(convertColumnToTdColumnData(cdef));
				
				List<TdColumnDefinition> others = aggregateByTimeOperationEvent.getOtherColumns();
				List<TdColumnData> listGroupCls = new ArrayList<TdColumnData>(others.size());
				
				for (TdColumnDefinition cdef2 : others) {
					listGroupCls.add(convertColumnToTdColumnData(cdef2));
				}
				
				for (TdColumnData tdColumnData : listTimeCL) {
					GWT.log("TIME "+tdColumnData);
				}
				for (TdColumnData tdColumnData : listGroupCls) {
					GWT.log("OTHERS "+ tdColumnData);
				}
				
				showAggregateByTime(listTimeCL, cdef.getColumnName(), listGroupCls);
			}
		});
		
		actionsEventBus.addHandler(UndoLastOperationEvent.TYPE, new UndoLastOperationEventHandler() {
			
			@Override
			public void onUndoLastOperation(UndoLastOperationEvent undoLastOperationEvent) {
				
				if(actions.size()>0){
					removeLastAction();
				}
			}
		});

		actionsEventBus.addHandler(DeleteColumnOperationEvent.TYPE, new DeleteColumnOperationEventHandler() {
			
			@Override
			public void onDeleteColumnOperation(DeleteColumnOperationEvent deleteColumnOperationEvent) {
				
				if(deleteColumnOperationEvent.getColumn()!=null){
					TdTemplateController.tdTemplateServiceAsync.deleteColumnAction(deleteColumnOperationEvent.getColumn(), new AsyncCallback<List<TdColumnDefinition>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							
						}

						@Override
						public void onSuccess(List<TdColumnDefinition> result) {
							GWT.log("deleteColumnAction return :");
//							for (TdColumnDefinition tdColumnDefinition : result) {
//								GWT.log(tdColumnDefinition.toString());
//							}
						
							templatePanelActionUpdater.updateColumns(result);
							
						}
					});
				}
			}
		});
		
		actionsEventBus.addHandler(TableRuleOperationEvent.TYPE, new TableRuleOperationEventHandler() {
			
			@Override
			public void onAddTableRule(final TableRuleOperationEvent tableRuleOperationEvent) {
				if(tableRuleOperationEvent.getTemplateExpression()!=null){
					TdTemplateController.tdTemplateServiceAsync.tableRuleAction(tableRuleOperationEvent.getTemplateExpression(), new AsyncCallback<List<TdColumnDefinition>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							
						}

						@Override
						public void onSuccess(List<TdColumnDefinition> result) {
							templatePanelActionUpdater.updateColumns(result);
							TabularDataAction action = new TabularDataAction() {
								
								@Override
								public String getId() {
									return null;
								}
								
								@Override
								public String getDescription() {
									return "Table rule "+tableRuleOperationEvent.getTemplateExpression().getClientExpression().getReadableExpression();
								}
							};
							addCompletedAction(action);
						}
					});
				}
			}
		});
		
		actionsEventBus.addHandler(AddColumnOperationEvent.TYPE, new AddColumnOperationEventHandler() {
			
			@Override
			public void onAddColumnOperation(AddColumnOperationEvent addColumnOperationEvent) {
				
				if(addColumnOperationEvent.getColumn()!=null){
					doAddColumnAction(addColumnOperationEvent.getColumn(), addColumnOperationEvent.getTemplateExpression());
				}
			}

			private void doAddColumnAction(ColumnDefinitionView column, final TemplateExpression templateExpression) {
				final TdColumnDefinition columnDefiniton = TdTemplateController.createTdColumnDefinitionFromView(column);
				
				TdTemplateController.tdTemplateServiceAsync.addColumnAction(columnDefiniton, templateExpression, new AsyncCallback<List<TdColumnDefinition>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(List<TdColumnDefinition> result) {
						GWT.log("addColumnAction return :");
						templatePanelActionUpdater.updateColumns(result);
						
						TabularDataAction action = new TabularDataAction() {
							
							@Override
							public String getId() {
								return null;
							}
							
							@Override
							public String getDescription() {
								String descr = "Add Column "+columnDefiniton.getColumnName();
								if(templateExpression!=null)
									descr+=", with initial value: "+templateExpression.getHumanDescription();
								return descr;
							}
						};
						addCompletedAction(action);
					}
				});
			}
		});
		
		
		templatePanelActionUpdater.enableUndoLastOperation(false);
	}
	
	

	/**
	 * Convert column to td column data.
	 *
	 * @param cdef the cdef
	 * @return the td column data
	 */
	public static TdColumnData convertColumnToTdColumnData(TdColumnDefinition cdef){

		ServerObjectId serId = new ServerObjectId(cdef.getIndex(), null, null, ServerObjectType.TEMPLATE);
		TdColumnData td = new TdColumnData(serId, cdef.getIndex()+"", cdef.getColumnName(), cdef.getColumnName(), cdef.getDataType().getName(), cdef.isBaseColumn());
		
		if(cdef.getTimePeriod()!=null)
			td.setPeriodType(cdef.getTimePeriod().getName());
		
		return td;
	}
	
	/**
	 * Adds the completed action.
	 *
	 * @param action the action
	 */
	private void addCompletedAction(TabularDataAction action){
		actions.add(action);
		refreshLastOperationLabel(TdTemplateConstants.LATEST_OPERATION+ ": performed operation \""+action.getDescription()+"\"","");
		refreshSuggestion(TdTemplateConstants.ACTION_COMPLETED, TdTemplateConstants.DO_YOU_WANT_ADD_ANOTHER_ACTION);

		templatePanelActionUpdater.enableUndoLastOperation(actions.size()>0);
	}
	
	/**
	 * Gets the completed action.
	 *
	 * @return the completed action
	 */
	public List<TabularDataAction> getCompletedAction(){
		return actions;
	}
	
	protected void setActions(List<TabularDataAction> actions){
		this.actions = actions;
	}
	
	/**
	 * Reset actions.
	 */
	private void resetActions(){
		actions.clear();
		templatePanelActionUpdater.enableUndoLastOperation(false);
	}
	
	/**
	 * Refresh suggestion.
	 *
	 * @param title the title
	 * @param text the text
	 */
	public void refreshSuggestion(String title, String text){
		templatePanelActionUpdater.refreshSuggestion(title, text);
	}
	
	/**
	 * Refresh last operation label.
	 *
	 * @param text the text
	 * @param subText the sub text
	 */
	public void refreshLastOperationLabel(String text, String subText){
		templatePanelActionUpdater.refreshLastOperationLabel(text,subText);
	}
	
	
	/**
	 * Undo latest actions.
	 *
	 * @return true, if successful
	 */
	public boolean undoLatestActions(){
		int num = actions.size();
		boolean removed = false;
		if(num>0){
			TabularDataAction action = actions.get(num-1);
			refreshLastOperationLabel(TdTemplateConstants.LATEST_OPERATION+": removed latest action '"+action.getDescription()+"'","");
			refreshSuggestion(TdTemplateConstants.ACTION_COMPLETED, TdTemplateConstants.DO_YOU_WANT_ADD_ANOTHER_ACTION);
			actions.remove(num-1);			
			removed = true;
		}
		templatePanelActionUpdater.enableUndoLastOperation(actions.size()>0);
		return removed;
	}

	/**
	 * Gets the actions.
	 *
	 * @return the actions
	 */
	public List<TabularDataAction> getActions() {
		return actions;
	}

	/**
	 * Gets the event bus.
	 *
	 * @return the eventBus
	 */
	public SimpleEventBus getEventBus() {
		return actionsEventBus;
	}

	

	/**
	 * Show aggregate by time.
	 *
	 * @param listTimeCL the list time cl
	 * @param columnName the column name
	 * @param listOthers the list others
	 */
	protected void showAggregateByTime(List<TdColumnData> listTimeCL, String columnName, List<TdColumnData> listOthers) {
		aggregateByTimeDialog = new AggregateByTimeColumnDialog(null, columnName, actionsEventBus);
		aggregateByTimeDialog.loadTimeDimensionColumns(listTimeCL);
		aggregateByTimeDialog.loadOtherColumns(listOthers);
		int tmX = templateController.getWindowPositionX() - 50;
		int x = (tmX>0)?tmX:0;
		int y = templateController.getWindowPositionY();
		aggregateByTimeDialog.show(templateController.getWindowZIndex(), x, y, true);
	}
	
	/**
	 * Removes the last action.
	 */
	protected void removeLastAction(){
		
		TdTemplateController.tdTemplateServiceAsync.removeLastAction(new AsyncCallback<List<TdColumnDefinition>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("caught "+caught.getMessage());
				MessageBox.alert("Error", caught.getMessage(), null);
				
			}

			@Override
			public void onSuccess(List<TdColumnDefinition> result) {
				
				GWT.log("executeTabularDataAction return :");
				for (TdColumnDefinition tdColumnDefinition : result) {
					GWT.log(tdColumnDefinition.toString());
				}
			
				templatePanelActionUpdater.updateColumns(result);
				undoLatestActions();
			}
		});
	}

	/**
	 * Reset.
	 */
	public void reset() {
		if(templatePanelActionUpdater!=null)
			templatePanelActionUpdater.reset();
		resetActions();
	}
}
