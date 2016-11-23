/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.panel.result;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.taskswidget.client.ConstantsTdTasks;
import org.gcube.portlets.user.td.taskswidget.client.TdTaskController;
import org.gcube.portlets.user.td.taskswidget.client.event.OpenResultEvent;
import org.gcube.portlets.user.td.taskswidget.shared.TdTabularResourceModel;
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
public class ResultCollateralTablePanel extends LayoutContainer {

	private List<TdTabularResourceModel> listCollateralTRModel;
	
	private List<Label> listName = new ArrayList<Label>();
	
	private List<Anchor> listAnchorCTD = new ArrayList<Anchor>();
	
	private FlexTable flex = new FlexTable();

	private long tabularReosourceId;
	
	public ResultCollateralTablePanel(List<TdTabularResourceModel> listCollateralTRModel, long tabularReosourceId) {
		
		this.listCollateralTRModel = listCollateralTRModel;
		
		this.tabularReosourceId = tabularReosourceId;

		initPanel();
		
		if(listCollateralTRModel==null)
			return;
		
		upateFormFields(listCollateralTRModel);
	}


	/**
	 * 
	 */
	public ResultCollateralTablePanel() {
		initPanel();
	}


	private void initPanel() {
		setBorders(true);
		setStyleAttribute("margin", "5px");
		setHeight(ConstantsTdTasks.RESULT_PANELS_HEIGHT);
		
		flex.setCellPadding(10);
		flex.setCellSpacing(10);
		flex.setWidget(0, 0, new Label("Name"));
		flex.setWidget(0, 1, new Label("Open"));
		flex.setStyleName("job-table");

		add(flex);
		
		setScrollMode(Scroll.AUTOY);
		
	}

	public void upateFormFields(List<TdTabularResourceModel>  listCTRM){
		
		this.listCollateralTRModel = listCTRM;
		
		if(this.listCollateralTRModel==null)
			return;

		reset();
		
		for (int i=0; i<listCollateralTRModel.size(); i++) {
			
			//FIX Anchors
			Anchor ac = new Anchor("Load Collateral Table");
			
			final TdTabularResourceModel tbr = listCollateralTRModel.get(i);
			
			ac.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
//					Window.alert("New event Open this id: "+tbr.getId());
					TdTaskController.getInternalBus().fireEvent(new OpenResultEvent(OpenResultEvent.ResultType.COLLATERALTABLE, new TRId(""+tabularReosourceId)));
					
				}
			});
			
			listAnchorCTD.add(ac);
			
			//Fix Names
			
			flex.setWidget(i+1, 0, new Label(tbr.getName()));
			flex.setWidget(i+1, 1, ac);
		}
		
		this.layout();
	}
	
	private void reset(){
		
		flex.removeAllRows();
		listAnchorCTD.clear();
		listName.clear();
		flex.setWidget(0, 0, new Label("Name"));
		flex.setWidget(0, 1, new Label("Open"));
	}
}
