/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.panel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 13, 2013
 *
 */
import org.gcube.portlets.user.td.taskswidget.client.ConstantsTdTasks;
import org.gcube.portlets.user.td.taskswidget.client.TdTaskController;
import org.gcube.portlets.user.td.taskswidget.client.event.SeeMoreTaskEvent;
import org.gcube.portlets.user.td.taskswidget.client.manager.TdPagingToolBar;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class TdTaskManagerMainPanel extends ContentPanel{
	
	
	private int currentEndTask = 0;
	private LayoutContainer lcSeeMore = new LayoutContainer();
	private Button buttonSeeMore = new Button("See More");
	private LayoutContainer lcTasks = new LayoutContainer();
	private int pageSize;

	private final TdTaskManagerMainPanel INSTANCE = this;
	private int currentStartItem;

	private TdPagingToolBar pagingToolbar;

	/**
	 * 
	 */
	public TdTaskManagerMainPanel(int currentStartItem, int pageSize) {
		this.pageSize = pageSize;
		this.currentStartItem = currentStartItem;
		
		this.pagingToolbar = new TdPagingToolBar(pageSize);
		this.pagingToolbar.setCurrent(currentStartItem, currentStartItem);
		
		
		
//		this.setBottomComponent(this.pagingToolbar);
		
		
		setHeaderVisible(false);
//		setHeading("Task Monitor");
		setSize(ConstantsTdTasks.MAINWIDTH+"px", ConstantsTdTasks.MAINHEIGHT+"px");
		setScrollMode(Scroll.AUTOY);
		
		buttonSeeMore.setStyleName("button-hyperlink");
		
		int sizeHpSeeMore = ConstantsTdTasks.MAINWIDTH-30;
		lcSeeMore.setWidth(sizeHpSeeMore+"px");
//		lcSeeMore.setHorizontalAlign(HorizontalAlignment.CENTER);
		lcSeeMore.setLayout(new CenterLayout());
		lcSeeMore.setVisible(false);
		lcSeeMore.add(buttonSeeMore);
		
		add(lcTasks);
		add(lcSeeMore);
		
		
		buttonSeeMore.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				seeMoreIsFired();
			}
		});
		
		addListener(Events.Scroll, new Listener<ComponentEvent>() {

			@Override
			public void handleEvent(ComponentEvent be) {

				int scrollPosition = INSTANCE.getVScrollPosition() + ConstantsTdTasks.MAINHEIGHT;
				int end = lcTasks.getHeight();
//				int scrollPositionOffset = lcTasks.getHeight()-60;
				int difference = end-scrollPosition;
//				System.out.println("scrollPosition: "+ scrollPosition + " lc.heigth: "+lcTasks.getHeight() +" difference"+difference);
				
				if(end-scrollPosition <=2 && lcSeeMore.isVisible()){
//					Info.display("Info","End is raggiunto");
					seeMoreIsFired();
				}
			}
		
		});

	}
	
	private void seeMoreIsFired(){
		setVisibleSeeMore(false);
		int newStart = currentEndTask;
		
		TdTaskController.getInternalBus().fireEvent(new SeeMoreTaskEvent(newStart));
		
	}
	
	public void reset(){
		lcTasks.removeAll();
		currentEndTask = 0;
		this.pagingToolbar.reset();
	}

	/**
	 * @param taskPanel
	 */
	public void addTaskPanel(TaskPanel taskPanel) {
		currentEndTask++;
		lcTasks.add(taskPanel);
		this.pagingToolbar.setCurrent(currentStartItem, currentEndTask);
		
	}
	
	public void updateDispalyingAndSeeMore(){
		
		this.pagingToolbar.updateNumItems();
		
		TdTaskController.tdTaskService.countTdTasksFromCache(new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				
				GWT.log("Loaded size serve cache: "+result);
			
				if(currentEndTask<result){
					setVisibleSeeMore(true);
				}else{
					setVisibleSeeMore(false);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error", "Sorry an error occurred on contacting the server");
				
			}
		});
	}
	
	private void setVisibleSeeMore(boolean bool){
		lcSeeMore.setVisible(bool);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public TdPagingToolBar getPagingToolbar() {
		return pagingToolbar;
	}
	
}
