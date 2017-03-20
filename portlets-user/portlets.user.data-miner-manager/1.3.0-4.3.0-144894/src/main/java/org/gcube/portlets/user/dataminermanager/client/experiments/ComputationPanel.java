package org.gcube.portlets.user.dataminermanager.client.experiments;

import org.gcube.portlets.user.dataminermanager.client.DataMinerManager;
import org.gcube.portlets.user.dataminermanager.client.events.ComputationReadyEvent;
import org.gcube.portlets.user.dataminermanager.client.events.ComputationReadyEvent.ComputationReadyEventHandler;
import org.gcube.portlets.user.dataminermanager.client.events.ComputationReadyEvent.HasComputationReadyEventHandler;
import org.gcube.portlets.user.dataminermanager.client.experiments.ComputationParametersPanel.ComputationParametersPanelHandler;
import org.gcube.portlets.user.dataminermanager.shared.process.Operator;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationPanel extends FramedPanel implements HasComputationReadyEventHandler
		 {
	private static final String DELETE_ALL_BUTTON_TOOLTIP = "Remove All Operators";
	private TextButton removeAllButton;
	private ToolBar toolBar;
	private VerticalLayoutContainer topV;
	private VerticalLayoutContainer v;
	private ComputationParametersPanel computationParametersPanel;
	 
	public ComputationPanel() {
		super();
		init();
		create();
	}


	private void init() {
		setHeaderVisible(false);
		setBodyStyle("backgroundColor:white;");
	}

	private void create() {
		computationParametersPanel=null;
		topV = new VerticalLayoutContainer();
		topV.setScrollMode(ScrollMode.NONE);
		setToolBar();
		topV.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		SimpleContainer operator = new SimpleContainer();
		v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTO);
		operator.add(v);
		topV.add(operator, new VerticalLayoutData(1, 1, new Margins(0)));
		add(topV);
		emptyPanel();
	}

	private void setToolBar() {
		toolBar = new ToolBar();
		toolBar.add(new LabelToolItem("Tools:"));

		removeAllButton = new TextButton(DELETE_ALL_BUTTON_TOOLTIP);
		removeAllButton.setToolTip(DELETE_ALL_BUTTON_TOOLTIP);
		removeAllButton.setIcon(DataMinerManager.resources.removeAll());
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

	}

	
	private void addComputationParametersPanel(
			ComputationParametersPanel computationParametersPanel) {
		v.clear();
		HBoxLayoutContainer hBox = new HBoxLayoutContainer();
		hBox.setPack(BoxLayoutPack.CENTER);
		hBox.add(new Image(DataMinerManager.resources.workflowConnector1()),
				new BoxLayoutData(new Margins(0)));
		v.add(hBox, new VerticalLayoutData(1, -1, new Margins(10,0,0,0)));
		v.add(computationParametersPanel, new VerticalLayoutData(1, -1,
				new Margins(0)));
		removeAllButton.setEnabled(true);
		forceLayout();
	}

	@Override
	public HandlerRegistration addComputationReadyEventHandler(
			ComputationReadyEventHandler handler) {
		return addHandler(handler, ComputationReadyEvent.getType());

	}

	public void addOperator(Operator operator) {
		computationParametersPanel = new ComputationParametersPanel(operator);
		computationParametersPanel.setHandler(new ComputationParametersPanelHandler() {
			@Override
			public void startComputation() {
				if (computationParametersPanel != null) {
					forceLayout();
					computationParametersPanel.updateOperatorParametersValues();
					Operator op = computationParametersPanel.getOperator();
					ComputationReadyEvent event = new ComputationReadyEvent(op);
					fireEvent(event);
				}
				
			}
		});

	    addComputationParametersPanel(computationParametersPanel);
		
	}

	

}
