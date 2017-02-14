/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.panel.result;

import org.gcube.portlets.user.td.taskswidget.client.ConstantsTdTasks;
import org.gcube.portlets.user.td.taskswidget.client.TdTaskController;
import org.gcube.portlets.user.td.taskswidget.client.event.OpenResultEvent;
import org.gcube.portlets.user.td.taskswidget.shared.TdTableModel;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TabResourceType;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 21, 2013
 * 
 */
public class ResultTabularDataPanel extends LayoutContainer {

	private TdTableModel tableModel;
	private Anchor linkTabularData = new Anchor("Load Tabular Data");
	private String tabularDataId = null;
	private FlexTable flex = new FlexTable();
//	private Label tdName = new Label("");
	private long tabularResourceId;

	public ResultTabularDataPanel(TdTableModel tdTableModel, long tabularResourceId) {
		this.tableModel = tdTableModel;
		this.tabularResourceId = tabularResourceId;
		initPanel();
		upateFormFields(tdTableModel);
	}
	

	/**
	 * 
	 */
	public ResultTabularDataPanel() {
		initPanel();
	}


	private void initPanel() {
		setBorders(true);
		setStyleAttribute("margin", "5px");
		setHeight(ConstantsTdTasks.RESULT_PANELS_HEIGHT);
		flex.setCellPadding(10);
		flex.setCellSpacing(10);
		flex.setStyleName("job-table");
//		flex.setWidget(0, 0, new Label("Name"));
		flex.setWidget(0, 0, new Label("Open"));
		linkTabularData.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TdTaskController.getInternalBus().fireEvent(new OpenResultEvent(OpenResultEvent.ResultType.COLLATERALTABLE, new TRId(""+tabularResourceId)));
			}
		});
		
		add(flex);
		
		setScrollMode(Scroll.AUTOY);
		
	}

	public void upateFormFields(TdTableModel tdTableModel){
		this.tableModel = tdTableModel;
		
		if(this.tableModel==null)
			return;

		tabularDataId = tdTableModel.getId();
//		tdName.setText(tdTableModel.getName());
		
//		flex.setWidget(1, 0, tdName);
		flex.setWidget(1, 0, linkTabularData);
		
		this.layout();
	}


	public TdTableModel getTableModel() {
		return tableModel;
	}
}
