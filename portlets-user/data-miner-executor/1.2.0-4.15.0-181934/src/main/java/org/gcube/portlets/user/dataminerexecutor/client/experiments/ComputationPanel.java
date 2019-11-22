package org.gcube.portlets.user.dataminerexecutor.client.experiments;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.portlets.user.dataminerexecutor.client.DataMinerExecutor;
import org.gcube.portlets.user.dataminerexecutor.client.events.ComputationReadyEvent;
import org.gcube.portlets.user.dataminerexecutor.client.events.ComputationReadyEvent.ComputationReadyEventHandler;
import org.gcube.portlets.user.dataminerexecutor.client.events.ComputationReadyEvent.HasComputationReadyEventHandler;
import org.gcube.portlets.user.dataminerexecutor.client.experiments.ComputationParametersPanel.ComputationParametersPanelHandler;
import org.gcube.portlets.user.dataminerexecutor.shared.process.InvocationModel;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ComputationPanel extends FramedPanel implements HasComputationReadyEventHandler
		 {
	//private static final String DELETE_ALL_BUTTON_TOOLTIP = "Remove All Operators";
	//private TextButton removeAllButton;
	//private ToolBar toolBar;
	private VerticalLayoutContainer topV;
	private VerticalLayoutContainer v;
	private ComputationParametersPanel computationParametersPanel;
	 
	public ComputationPanel() {
		super();
		init();
		create();
		bind();
	}


	private void init() {
		setHeaderVisible(false);
		setBodyStyle("backgroundColor:white;");
	}

	private void create() {
		computationParametersPanel=null;
		topV = new VerticalLayoutContainer();
		topV.setScrollMode(ScrollMode.NONE);
		//setToolBar();
		//topV.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		SimpleContainer operator = new SimpleContainer();
		v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTO);
		operator.add(v);
		topV.add(operator, new VerticalLayoutData(1, 1, new Margins(0)));
		add(topV);
		//emptyPanel();
	}

	private void bind() {

		/*EventBusProvider.INSTANCE.addHandler(RemoveSelectedOperatorEvent.TYPE,
				new RemoveSelectedOperatorEvent.RemoveSelectedOperatorEventHandler() {
					
					@Override
					public void onSelect(RemoveSelectedOperatorEvent event) {
						Log.debug("Catch RemoveSelectedOperatorEvent");
						emptyPanel();
						computationParametersPanel = null;
						
					}
				});*/
	}
	
	
	/*private void setToolBar() {
		toolBar = new ToolBar();
		toolBar.add(new LabelToolItem("Tools:"));

		removeAllButton = new TextButton(DELETE_ALL_BUTTON_TOOLTIP);
		removeAllButton.setToolTip(DELETE_ALL_BUTTON_TOOLTIP);
		removeAllButton.setIcon(DataMinerExecutor.resources.removeAll());
		removeAllButton.setEnabled(false);
		removeAllButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				emptyPanel();
				computationParametersPanel = null;
			}
		});

		toolBar.add(removeAllButton);
	}

	private void emptyPanel() {
		removeAllButton.setEnabled(false);
		v.clear();
		CenterLayoutContainer centerContainer = new CenterLayoutContainer();
		centerContainer.add(new HTML(
				"<span align='center'>Select an operator.</span>"));
		v.add(centerContainer, new VerticalLayoutData(1, 1, new Margins(0)));
		forceLayout();

	}*/

	
	private void addComputationParametersPanel(
			ComputationParametersPanel computationParametersPanel) {
		v.clear();
		HBoxLayoutContainer hBox = new HBoxLayoutContainer();
		hBox.setPack(BoxLayoutPack.CENTER);
		hBox.add(new Image(DataMinerExecutor.resources.workflowConnector1()),
				new BoxLayoutData(new Margins(0)));
		v.add(hBox, new VerticalLayoutData(1, -1, new Margins(10,0,0,0)));
		v.add(computationParametersPanel, new VerticalLayoutData(1, -1,
				new Margins(0)));
		//removeAllButton.setEnabled(true);
		forceLayout();
	}

	@Override
	public HandlerRegistration addComputationReadyEventHandler(
			ComputationReadyEventHandler handler) {
		return addHandler(handler, ComputationReadyEvent.getType());

	}

	public void addInvocation(InvocationModel invocationModel) {
		computationParametersPanel = new ComputationParametersPanel(invocationModel.getOperator());
		if(invocationModel.getInvocationAction()==null){
			setReadyHandler();
		} else {
			switch(invocationModel.getInvocationAction()){
			case EDIT:
				setReadyHandler();
				break;
			case RUN:
				automaticRun();
				break;
			case SHOW:
				setReadyHandler();
				break;
			default:
				setReadyHandler();
				break;
			
			}
		}
	}
	
	
	private void automaticRun() {
		addComputationParametersPanel(computationParametersPanel);
		forceLayout();
		Operator op = computationParametersPanel.getOperator();
		ComputationReadyEvent event = new ComputationReadyEvent(op);
		fireEvent(event);
	}


	private void setReadyHandler(){
		computationParametersPanel.setHandler(new ComputationParametersPanelHandler() {
			@Override
			public void startComputation() {
				if (computationParametersPanel != null) {
					forceLayout();
					Operator op = computationParametersPanel.getOperator();
					ComputationReadyEvent event = new ComputationReadyEvent(op);
					fireEvent(event);
				}
				
			}
		});

	    addComputationParametersPanel(computationParametersPanel);
	}

	

}
