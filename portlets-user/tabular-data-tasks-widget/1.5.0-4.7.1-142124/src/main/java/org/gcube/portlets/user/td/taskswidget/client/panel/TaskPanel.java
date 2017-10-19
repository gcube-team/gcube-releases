/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.panel;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.taskswidget.client.ConstantsTdTasks;
import org.gcube.portlets.user.td.taskswidget.client.manager.ResultsLoaderInterface;
import org.gcube.portlets.user.td.taskswidget.client.panel.result.JobInfoPanel;
import org.gcube.portlets.user.td.taskswidget.client.panel.result.ResultCollateralTablePanel;
import org.gcube.portlets.user.td.taskswidget.client.panel.result.ResultTabularDataPanel;
import org.gcube.portlets.user.td.taskswidget.client.panel.result.TaskInfoPanel;
import org.gcube.portlets.user.td.taskswidget.client.resources.Resources;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 19, 2013
 *
 */
public class TaskPanel extends ContentPanel implements TaskPanelUpdaterInterface, ResultsLoaderInterface {

	boolean jobInfoVisible = false;
	
	private LayoutContainer baseTaskLC = new LayoutContainer();
	private TaskInfoPanel taskInfoContainer;
	private JobInfoPanel jobInfo;
	private ResultTabularDataPanel resultTabularData;
	private ResultCollateralTablePanel resultCollateral;
	private static final int MARGIN = 5;
	private HorizontalPanel baseHp = new HorizontalPanel();
	
	private final AbstractImagePrototype iconDown = AbstractImagePrototype.create(Resources.INSTANCE.getArrowDown());
	private final AbstractImagePrototype iconRight = AbstractImagePrototype.create(Resources.INSTANCE.getArrowRight());
	
	final ToggleButton buttonToggleJobInfo = new ToggleButton("Job Info");
	final ToggleButton buttonToggleResultTabular = new ToggleButton("Result Tabular");
	final ToggleButton buttonToggleResultCollateral = new ToggleButton("Result Collateral");
	
	private List<ToggleButton> listToogleButton = new ArrayList<ToggleButton>();
	
	private CardLayout cardLayout = new CardLayout();

	private int taskInfoContainerHeight;

	/**
	 * 
	 */
	public TaskPanel() {
		initButtons();
		initPanel();
	}
	
	/**
	 * 
	 */
	private void initButtons() {
		
//		baseTaskLC.addStyleName("taskpanel");
		baseHp.addStyleName("taskpanel");
		
		listToogleButton.add(buttonToggleJobInfo);
		listToogleButton.add(buttonToggleResultTabular);
		listToogleButton.add(buttonToggleResultCollateral);
		
		buttonToggleJobInfo.toggle(false);
		buttonToggleResultTabular.toggle(false);
		buttonToggleResultCollateral.toggle(false);
		
		setButtonIcon(buttonToggleJobInfo, false);
		setButtonIcon(buttonToggleResultTabular, false);
		setButtonIcon(buttonToggleResultCollateral, false);
		
		buttonToggleJobInfo.setToggleGroup("tab");
		buttonToggleJobInfo.setStyleName("button-link");
		
		buttonToggleResultTabular.setToggleGroup("tab");
		buttonToggleResultTabular.setStyleName("button-link");
		
		buttonToggleResultCollateral.setToggleGroup("tab");
		buttonToggleResultCollateral.setStyleName("button-link");
		
	}


	private void toogleWasSelected(){
		for (ToggleButton toogle : listToogleButton)
			setButtonIcon(toogle, toogle.isPressed());
	}
	
	private void activeDetails(boolean bool){
		
		if(bool){
			updateNorthSize(this.taskInfoContainerHeight+30+ConstantsTdTasks.RESULT_PANELS_HEIGHT+10);
		}
		else{
			updateNorthSize(this.taskInfoContainerHeight+30);
		}
	}
	
	/**
	 * 
	 */
	private void initPanel() {
		setStyleAttribute("margin", MARGIN+"px");
		setStyleAttribute("margin-bottom", "15px");
		setHeaderVisible(false);
//		setLayout(new FitLayout());
//		lc.setLayout(new FlowLayout());
		add(baseTaskLC);
		add(baseHp);
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
//		setLayout(new FitLayout());
	
		ContentPanel cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		cp.setLayout(cardLayout);

//		cp.setStyleName("taskpanel");
		
		cp.add(jobInfo);
		cp.add(resultTabularData);
		cp.add(resultCollateral);
		
		add(cp);
	};
	


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.taskswidget.client.panel.TaskPanelUpdaterInterface#addTaskInfoContainer(org.gcube.portlets.user.td.taskswidget.client.panel.result.TdBasicLayoutContainer)
	 */
	@Override
	public void addTaskInfoContainer(TaskInfoPanel lc, boolean setAsVisible) {
		taskInfoContainer = lc;
		taskInfoContainer.setWidth(ConstantsTdTasks.MAINWIDTH-25);
//		taskInfoContainer.setWidth(this.getWidth()-MARGIN);
		
		taskInfoContainer.addListener(Events.Resize, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
//				Window.alert(taskInfoContainer.getHeight()+"");
				taskInfoContainerHeight = taskInfoContainer.getHeight();
				
			}
		});
		
		taskInfoContainer.addListener(Events.Render, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
//				Window.alert(taskInfoContainer.getHeight()+"");
				taskInfoContainerHeight = taskInfoContainer.getHeight();
			}
		});
//		lc.add(taskInfoContainer);
		baseTaskLC.add(taskInfoContainer);
		

//		add(taskInfoContainer);
//		this.layout();
	}
	
	public void refreshTaskInfoContainer(){
		taskInfoContainer.layout(true);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.taskswidget.client.panel.TaskPanelUpdaterInterface#addJobInfoContainer(org.gcube.portlets.user.td.taskswidget.client.panel.result.TdBasicLayoutContainer)
	 */
	@Override
	public void addJobInfoContainer(JobInfoPanel lc, boolean setAsVisible) {
		jobInfo = lc;
		jobInfo.setVisible(setAsVisible);

		buttonToggleJobInfo.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if (buttonToggleJobInfo.isPressed()) {
//					jobInfo.el().slideOut(Direction.UP, FxConfig.NONE);
					jobInfo.setVisible(true);
					cardLayout.setActiveItem(jobInfo);
				} else {
//					jobInfo.el().slideIn(Direction.DOWN, FxConfig.NONE);
					jobInfo.setVisible(false);
//					cardLayout.setActiveItem(jobInfo);
			    }  
				activeDetails(buttonToggleJobInfo.isPressed());
				toogleWasSelected();
			}
		});
		
		jobInfo.setScrollMode(Scroll.AUTOY);
		
		baseHp.add(buttonToggleJobInfo);
	}
	
	private void setButtonIcon(Button button, boolean setAsVisible){
		
		if(setAsVisible)
			button.setIcon(iconDown);
		else
			button.setIcon(iconRight);
	}
//
//
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.taskswidget.client.panel.TaskPanelUpdaterInterface#addResultTabularContainer(org.gcube.portlets.user.td.taskswidget.client.panel.result.TdBasicLayoutContainer)
	 */
	@Override
	public void addResultTabularContainer(ResultTabularDataPanel lc) {
		resultTabularData = lc;

//		resultTabularData.setVisible(setAsVisible);

		buttonToggleResultTabular.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {

				if (buttonToggleResultTabular.isPressed()) {
//					jobInfo.el().slideOut(Direction.UP, FxConfig.NONE);
					resultTabularData.setVisible(true);
					cardLayout.setActiveItem(resultTabularData);
				} else {
//					jobInfo.el().slideIn(Direction.DOWN, FxConfig.NONE);
					resultTabularData.setVisible(false);
//					cardLayout.setActiveItem(jobInfo);
			    }  
				
				activeDetails(buttonToggleResultTabular.isPressed());
				toogleWasSelected();
			}
		});
		
		baseHp.add(buttonToggleResultTabular);
	}
	
	public void updateNorthSize(int height){
		this.setHeight(height);
		layout(true);	
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.td.taskswidget.client.panel.TaskPanelUpdaterInterface#addResultCollateralContainer(org.gcube.portlets.user.td.taskswidget.client.panel.result.ResultCollateralTablePanel, boolean)
	 */
	@Override
	public void addResultCollateralContainer(ResultCollateralTablePanel lc) {
		resultCollateral = lc;

//		resultCollateral.setVisible(setAsVisible);

		buttonToggleResultCollateral.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {

				if (buttonToggleResultCollateral.isPressed()) {
//					jobInfo.el().slideOut(Direction.UP, FxConfig.NONE);
					resultCollateral.setVisible(true);
					cardLayout.setActiveItem(resultCollateral);
				} else {
//					jobInfo.el().slideIn(Direction.DOWN, FxConfig.NONE);
					resultCollateral.setVisible(false);
//					cardLayout.setActiveItem(jobInfo);
			    }  
				
				activeDetails(buttonToggleResultCollateral.isPressed());
				toogleWasSelected();
			}
		});
		
		baseHp.add(buttonToggleResultCollateral);
	}

	@Override
	public void onResulTabulartUpdated(boolean visible) {
		buttonToggleResultTabular.setVisible(visible);

	}

	@Override
	public void onResulCollateralUpdated(boolean visible) {
		buttonToggleResultCollateral.setVisible(visible);
	}
	
	
}
