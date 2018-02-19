/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.manager;


import org.gcube.portlets.user.td.taskswidget.client.TdTaskController;

import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Dec 2, 2013
 *
 */
public class TdPagingToolBar extends ToolBar {
	

	protected static final String DISPLAY_TEXT = "Displaying {0} of {1}";
	protected static final String DISPLAY_TEXT_COUNTING = "Displaying {0} - {1} of counting...";
	protected static final String PAGE_TEXT = "Page {0} of {1}";

	protected LabelToolItem pageText, displayText;

	protected int currentPage = 0;
	protected int numPages = 0;
	
	protected boolean counting = false;
	protected int currentStartItem = 0;
	protected int currentEndItem = 0;
	protected int numItems = 0;
	
	protected int pageSize;
	

	public TdPagingToolBar(int pageSize2) {
		this.pageSize = pageSize2;
		init();
		update();
		updateNumItems();
	}
	
	
	public void reset()
	{
		currentPage = 0;
		numPages = 0;
		counting = true;
		currentStartItem = 0;
		currentEndItem = 0;
		numItems = 0;
		update();
		updateNumItems();
	}
	
	public void setStreamSize(int count)
	{
		numItems = count;
		update();
	}


	public void setCurrent(int currentStartItem, int currentEndItem)
	{
		this.currentStartItem = currentStartItem;
		this.currentEndItem = currentEndItem;
		update();
	}

	protected void init()
	{

		add(new FillToolItem());

		displayText = new LabelToolItem();
		displayText.setId(getId() + "-display");
		displayText.setStyleName("my-paging-display");

		add(displayText);
		
		add(new FillToolItem());
	}
	
	protected void update()
	{
		updateSizes();
		updateText();
	}
	
	/**
	 * 
	 */
	public void updateNumItems() {
		
		counting = true;
		
		TdTaskController.tdTaskService.countTdTasksFromCache(new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				numItems = 0;
				update();
			}

			@Override
			public void onSuccess(Integer result) {
				counting = false;
				numItems = result;
				update();
				
			}
			
		});
		
	}


	protected void updateSizes()
	{
		currentPage = (int) Math.ceil((double) (currentStartItem + pageSize) / pageSize);
		numPages = numItems < pageSize ? 1 : (int) Math.ceil((double) numItems / pageSize);
	}


	protected void updateText()
	{
		Object[] params = new Object[]{currentEndItem, numItems};
		String display = Format.substitute(counting?DISPLAY_TEXT_COUNTING:DISPLAY_TEXT, params);
		displayText.setLabel(display);
	}



	public int getCurrentStartItem() {
		return currentStartItem;
	}

	public int getCurrentEndItem() {
		return currentEndItem;
	}

}