/**
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.client.view.binder;

import java.util.List;

import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.portlets.widgets.wstaskexecutor.client.WsTaskExecutorWidget;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.PerformRunTaskEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.ShowCreateTaskConfigurationDialogEvent;
import org.gcube.portlets.widgets.wstaskexecutor.client.event.ShowCreateTaskConfigurationDialogEvent.Operation;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 9, 2018
 */
public abstract class ShowTaskConfigurationsView extends Composite {

	private static ShowTaskConfigurationViewUiBinder uiBinder =
		GWT.create(ShowTaskConfigurationViewUiBinder.class);

	interface ShowTaskConfigurationViewUiBinder
		extends UiBinder<Widget, ShowTaskConfigurationsView> {
	}

	@UiField
	Button butt_CreateNewConfiguration;

	@UiField
	FlexTable flex_table_configurations;

	private WSItem wsItem;

	private TaskConfiguration selectedConfiguration;

	/**
	 * Submit handler.
	 */
	public abstract void submitHandler();

	/**
	 * Sets the confirm.
	 *
	 * @param visible the visible
	 * @param msg the msg
	 */
	public abstract void setConfirm(boolean visible, String msg);


	public static int MAX_LENGHT_TEXT = 28;


	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public ShowTaskConfigurationsView(final WSItem wsItem, List<TaskConfiguration> listTaskConfigurations) {
		this.wsItem = wsItem;
		initWidget(uiBinder.createAndBindUi(this));

		butt_CreateNewConfiguration.getElement().getStyle().setMarginTop(20, Unit.PX);

		flex_table_configurations.setCellSpacing(4);
		flex_table_configurations.getElement().getStyle().setMarginTop(10, Unit.PX);
		flex_table_configurations.addStyleName("table-fixed");
		//flex_table_configurations.setWidget(0, 0, new HTML("<b>Config Id<b>"));
		HTML h0 = new HTML("<b>Algorithm Id<b>");
		h0.setTitle("The Algorithm Id");
		flex_table_configurations.setWidget(0, 0, h0);
		HTML h1 = new HTML("<b>N.Par.<b>");
		h1.setTitle("Number of parameters used by the Algorithm");
		flex_table_configurations.setWidget(0, 1, h1);
		HTML h2 = new HTML("<b>VRE<b>");
		flex_table_configurations.setWidget(0, 2, h2);
		flex_table_configurations.setWidget(0, 3, new HTML("<b>Owner<b>"));
		flex_table_configurations.setWidget(0, 4, new HTML("<b>Run<b>"));
		flex_table_configurations.setWidget(0, 5, new HTML("<b>Edit<b>"));
		flex_table_configurations.setWidget(0, 6, new HTML("<b>Del.<b>"));
		//flex_table_configurations.getColumnFormatter().setWidth(0, "10%");
		flex_table_configurations.getColumnFormatter().setWidth(0, "31%");
		flex_table_configurations.getColumnFormatter().setWidth(1, "6%");
		flex_table_configurations.getColumnFormatter().setWidth(2, "25%");
		flex_table_configurations.getColumnFormatter().setWidth(3, "20%");
		flex_table_configurations.getColumnFormatter().setWidth(4, "6%");
		flex_table_configurations.getColumnFormatter().setWidth(5, "6%");
		flex_table_configurations.getColumnFormatter().setWidth(6, "6%");
		//flex_table_configurations.getColumnFormatter().setWidth(5, "7%");
//		flt.setWidget(0, 1, new HTML("<b>Description<b>"));
		for (int i = 0; i<listTaskConfigurations.size(); i++) {
			final TaskConfiguration taskConfiguration = listTaskConfigurations.get(i);
			GWT.log("Showing: "+taskConfiguration);
			//flex_table_configurations.setWidget(i+1, 0, new HTML(taskConfiguration.getConfigurationKey()));
			String operatorID = taskConfiguration.getTaskId();
			if(operatorID.length()>MAX_LENGHT_TEXT){
				int algNameIndexStart = operatorID.lastIndexOf(".")+1;
				String algName = operatorID.substring(algNameIndexStart, operatorID.length());
				if(algName.length()>MAX_LENGHT_TEXT)
					operatorID = "..."+operatorID.substring(operatorID.length()-MAX_LENGHT_TEXT, operatorID.length());
				else
					operatorID = algName;
			}
			HTML aId=new HTML(operatorID);
			aId.setTitle(taskConfiguration.getTaskId());
			flex_table_configurations.setWidget(i+1, 0,aId);
			String countParameters = taskConfiguration.getListParameters()!=null?taskConfiguration.getListParameters().size()+"":"0";
			HTML params = new HTML(countParameters);
			params.setTitle(countParameters + " Parameter/s used by this configuration");
			flex_table_configurations.setWidget(i+1, 1, params);
			HTML VREName = new HTML(taskConfiguration.getScope().substring(taskConfiguration.getScope().lastIndexOf("/")+1, taskConfiguration.getScope().length()));
			VREName.setTitle(taskConfiguration.getScope());
			flex_table_configurations.setWidget(i+1, 2, VREName);
			flex_table_configurations.setWidget(i+1, 3, new HTML(taskConfiguration.getOwner()));

			Button buttRunTask = new Button();
			buttRunTask.setIcon(IconType.PLAY_SIGN);
			//buttEdit.setType(ButtonType.LINK);
			buttRunTask.setTitle("Run this Task Configuration");
			buttRunTask.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					WsTaskExecutorWidget.eventBus.fireEvent(new PerformRunTaskEvent(wsItem,taskConfiguration));
				}
			});

			flex_table_configurations.setWidget(i+1, 4, buttRunTask);

			Button buttEdit = new Button();
			buttEdit.setIcon(IconType.EDIT_SIGN);
			//buttEdit.setType(ButtonType.LINK);
			buttEdit.setTitle("Edit/Show the Configuration");
			buttEdit.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					WsTaskExecutorWidget.eventBus.fireEvent(new ShowCreateTaskConfigurationDialogEvent(wsItem, taskConfiguration, Operation.EDIT_EXISTING));
				}
			});

			flex_table_configurations.setWidget(i+1, 5, buttEdit);

			Button buttRemove = new Button();
			buttRemove.setIcon(IconType.REMOVE_SIGN);
			//buttRemove.setType(ButtonType.LINK);
			buttRemove.setTitle("Delete the Configuration");
			buttRemove.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					selectedConfiguration = taskConfiguration;
					setConfirm(true, "<div style='font-size:14px; font-weight:bold;'>Deleting che configuration: </div><div><br/>"+taskConfiguration+".<br/><br/><div style='font-size:14px; font-weight:bold;'>Confirm?</div></div>");

				}
			});
			flex_table_configurations.setWidget(i+1, 6, buttRemove);
		}

	}


	/**
	 * Gets the selected configuration.
	 *
	 * @return the selected configuration
	 */
	public TaskConfiguration getSelectedConfiguration(){
		return selectedConfiguration;
	}

	/**
	 * Adds the custom field event.
	 *
	 * @param e the e
	 */
	@UiHandler("butt_CreateNewConfiguration")
	void addCustomFieldEvent(ClickEvent e){

		WsTaskExecutorWidget.eventBus.fireEvent(new ShowCreateTaskConfigurationDialogEvent(wsItem, null, Operation.CREATE_NEW));

	}


	/**
	 * @return the wsItem
	 */
	public WSItem getWsItem() {

		return wsItem;
	}

}
