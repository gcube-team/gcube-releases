/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util.stream;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class StreamPagingToolBar extends ToolBar {
	
	protected static final String DISPLAY_TEXT_COUNTING = "Displaying {0} - {1} of {2} and counting...";
	protected static final String DISPLAY_TEXT = "Displaying {0} - {1} of {2}";
	protected static final String PAGE_TEXT = "Page {0} of {1}";

	protected Button prev, next;
	protected LabelToolItem pageText, displayText;

	protected AbstractImagePrototype prevImage = GXT.isHighContrastMode? IconHelper.create("gxt/themes/access/images/grid/page-prev.gif") : GXT.IMAGES.paging_toolbar_prev();
	protected AbstractImagePrototype prevImageDisabled = GXT.IMAGES.paging_toolbar_prev_disabled();

	protected AbstractImagePrototype nextImage = GXT.isHighContrastMode ? IconHelper.create("gxt/themes/access/images/grid/page-next.gif") : GXT.IMAGES.paging_toolbar_next();
	protected AbstractImagePrototype nextImageDisabled = GXT.IMAGES.paging_toolbar_next_disabled();

	protected String nextText = GXT.MESSAGES.pagingToolBar_nextText();
	protected String prevText = GXT.MESSAGES.pagingToolBar_prevText();
	
	protected int currentPage = 0;
	protected int numPages = 0;
	
	protected boolean counting = false;
	protected int currentStartItem = 0;
	protected int currentEndItem = 0;
	protected int numItems = 0;
	
	protected int pageSize;
	
	protected StreamPagingLoader loader;

	protected Listener<ComponentEvent> componentListener = new Listener<ComponentEvent>() {

		public void handleEvent(ComponentEvent be) {
			Component c = be.getComponent();
			if (be.getType() == Events.Disable) {
				if (c == prev) {
					prev.setIcon(prevImageDisabled);
				} else if (c == next) {
					next.setIcon(nextImageDisabled);
				}
			} else {
				if (c == prev) {
					prev.setIcon(prevImage);
				} else if (c == next) {
					next.setIcon(prevImageDisabled);
				}
			}
		}
	};

	public StreamPagingToolBar()
	{
		init();
		update();
	}
	
	public void bind(StreamPagingLoader loader)
	{
		this.loader = loader;
		this.pageSize = loader.getPageSize();
		loader.addListener(new StreamPagingLoaderListener() {
			
			@Override
			public void onStreamUpdate(int streamSize, int currentStartItem, int currentEndItem) {
				setStreamSize(streamSize);
				setCurrent(currentStartItem, currentEndItem);				
			}
			
			@Override
			public void onStreamLoadingComplete() {
				setStreamLoadingComplete(false);
			}

			@Override
			public void onStreamStartLoading() {
				reset();			
			}
		});
		loader.getStore().addStoreListener(new StoreListener<ModelData>(){

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void storeBeforeDataChanged(StoreEvent<ModelData> se) {
				beforeDataChange();
			}
		
		});
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
	}
	
	protected void beforeDataChange()
	{
		prev.setEnabled(false);
		next.setEnabled(false);
	}
	
	public void setStreamSize(int count)
	{
		numItems = count;
		update();
	}

	/**
	 * @param counting the counting to set
	 */
	public void setStreamLoadingComplete(boolean counting) {
		this.counting = counting;
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
		prev = new Button();
		prev.setToolTip(prevText);
		prev.addListener(Events.Disable, componentListener);
		prev.addListener(Events.Enable, componentListener);
		prev.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				previous();
			}
		});
		add(prev);

		add(new SeparatorToolItem());

		pageText = new LabelToolItem();
		pageText.setStyleName("my-paging-text");
		add(pageText);

		add(new SeparatorToolItem());

		next = new Button();
		next.setToolTip(nextText);
		next.addListener(Events.Disable, componentListener);
		next.addListener(Events.Enable, componentListener);
		next.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				next();
			}
		});
		add(next);

		add(new FillToolItem());

		displayText = new LabelToolItem();
		displayText.setId(getId() + "-display");
		displayText.setStyleName("my-paging-display");

		add(displayText);
	}
	
	protected void update()
	{
		updateSizes();
		updateButtons();
		updateImages();
		updateText();
	}
	
	protected void updateSizes()
	{
		currentPage = (int) Math.ceil((double) (currentStartItem + pageSize) / pageSize);
		numPages = numItems < pageSize ? 1 : (int) Math.ceil((double) numItems / pageSize);
	}
	
	protected void updateButtons()
	{
		next.setEnabled(currentPage != numPages);
		prev.setEnabled((currentPage != 1) && (currentPage != 0)); //TODO Modified by Francesco
	}

	protected void updateImages() {

		prev.setIcon(prev.isEnabled() ? prevImage : prevImageDisabled);
		next.setIcon(next.isEnabled() ? nextImage : nextImageDisabled);
	}

	protected void updateText()
	{
		Object[] params = new Object[]{currentPage, numPages};
		String page = Format.substitute(PAGE_TEXT, params);
		pageText.setLabel(page);
		
		params = new Object[]{currentStartItem, currentEndItem, numItems};
		String display = Format.substitute(counting?DISPLAY_TEXT_COUNTING:DISPLAY_TEXT, params);
		displayText.setLabel(display);
	}
	
	protected void previous()
	{
		if (loader!=null) loader.prevPage();
	}

	protected void next()
	{
		if (loader!=null) loader.nextPage();
	}

}
