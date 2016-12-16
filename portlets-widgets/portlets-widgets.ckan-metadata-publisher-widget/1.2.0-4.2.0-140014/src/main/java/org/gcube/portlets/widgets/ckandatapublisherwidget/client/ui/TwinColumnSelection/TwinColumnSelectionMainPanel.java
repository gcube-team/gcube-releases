package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.TwinColumnSelection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceElementBean;

import com.github.gwtbootstrap.client.ui.Breadcrumbs;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * The twin column panels for selection of the files to attach to the catalague product.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class TwinColumnSelectionMainPanel extends Composite{

	@UiField 
	VerticalPanel leftContainer;
	@UiField 
	VerticalPanel rightContainer;
	@UiField
	VerticalPanel buttonsPanel;
	@UiField
	Button allToRightButton;
	@UiField
	Button toRightButton;
	@UiField
	Button toLeftButton;
	@UiField
	Button allToLeftButton;
	//	@UiField
	//	Popover popoverResourceSelection;
	//	@UiField
	//	Button resourceInfoButton;
	@UiField
	Breadcrumbs breadcrumbs;
	@UiField
	FluidContainer mainContainerResourcesSelection;
	//	@UiField
	//	Button getResources;
	@UiField
	public static SimplePanel detailContainer;

	/**
	 * The breadcrumb subpath with the linked folder
	 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
	 */
	private class PathBean{
		NavLink link;
		ResourceElementBean resourceFolder;

		PathBean(NavLink link, ResourceElementBean resourceFolder){
			this.link = link;
			this.resourceFolder = resourceFolder;
		}
	}

	public static boolean freezed = false;
	private List<PathBean> pathListBeans = new ArrayList<PathBean>();
	private static final String PANEL_BORDER_COLOR = "#8899a6";
	private static final String PANEL_HEIGHT = "400px";
	private ShowMorePagerPanel showMorePanelLeft = new ShowMorePagerPanel();
	private ShowMorePagerPanel showMorePanelRight = new ShowMorePagerPanel();
	private CellList<ResourceElementBean> cellListLeft;
	private CellList<ResourceElementBean> cellListRight;
	private ListDataProvider<ResourceElementBean> dataProviderLeft = new ListDataProvider<ResourceElementBean>();
	private ListDataProvider<ResourceElementBean> dataProviderRight = new ListDataProvider<ResourceElementBean>();
	private MultiSelectionModel<ResourceElementBean> selectionModelRight;
	private MultiSelectionModel<ResourceElementBean> selectionModelLeft;
	private final ResourceElementBean initialBean;
	//	private final static HTML aboutHeader = new HTML("<b>Resource Manager</b>");
	//	private final static HTML aboutBody = new HTML("<p style='text-align:justify;'>Move the files you want to attach to the product on the right panel below."
	//			+ " Please consider that any complex hierarchy structure you may have will be flatten.</p>");

	//	private static final short PATH_THRESHOLD = 1; // TODO

	private static TwinColumnSelectionMainPanelUiBinder uiBinder = GWT
			.create(TwinColumnSelectionMainPanelUiBinder.class);

	interface TwinColumnSelectionMainPanelUiBinder extends
	UiBinder<Widget, TwinColumnSelectionMainPanel> {
	}

	public TwinColumnSelectionMainPanel(ResourceElementBean initialBean) {
		initWidget(uiBinder.createAndBindUi(this));

		this.initialBean = initialBean;

		buttonsPanel.getElement().getStyle().setMarginTop(50, Unit.PCT);
		allToRightButton.getElement().getStyle().setMarginBottom(4, Unit.PX);
		toRightButton.getElement().getStyle().setMarginBottom(4, Unit.PX);
		toLeftButton.getElement().getStyle().setMarginBottom(4, Unit.PX);
		allToLeftButton.getElement().getStyle().setMarginBottom(4, Unit.PX);

		buttonsPanel.getElement().setAttribute("align", "center");
		//		popoverResourceSelection.setPlacement(Placement.LEFT);
		//		popoverResourceSelection.setHeading(aboutHeader.getHTML());
		//		popoverResourceSelection.setText(aboutBody.getHTML());
		//		resourceInfoButton.getElement().getStyle().setFloat(Float.RIGHT);
		//		resourceInfoButton.getElement().getStyle().setPaddingRight(0, Unit.PX);
		mainContainerResourcesSelection.getElement().getStyle().setPadding(10, Unit.PX);

		breadcrumbs.getElement().getStyle().setBackgroundColor("white");
		breadcrumbs.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		breadcrumbs.getElement().getStyle().setMarginLeft(0, Unit.PX);
		mainContainerResourcesSelection.getElement().getStyle().setMarginLeft(10, Unit.PX);
		mainContainerResourcesSelection.getElement().getStyle().setMarginBottom(20, Unit.PX);
		mainContainerResourcesSelection.getElement().getStyle().setMarginTop(0, Unit.PX);

		prepareHandlers();
		initLeftSidePanel(initialBean);
		initRightSidePanel();
	}

	/**
	 * Initialize the left side panel
	 */
	private void initLeftSidePanel(final ResourceElementBean initialBean) {

		// initialize the left side list
		ResourceCellLeft cell = new ResourceCellLeft();

		// Set a key provider that provides a unique key for each object. 
		cellListLeft = new CellList<ResourceElementBean>(cell, ResourceElementBean.KEY_PROVIDER);
		cellListLeft.setPageSize(initialBean.getChildren().size());
		cellListLeft.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE); 

		// Add a selection model so we can select cells.
		selectionModelLeft = new MultiSelectionModel<ResourceElementBean>(ResourceElementBean.KEY_PROVIDER);
		cellListLeft.setSelectionModel(selectionModelLeft);

		// perform an action on selection
		selectionModelLeft.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			public void onSelectionChange(SelectionChangeEvent event) {

				if(freezed)
					return;

				Iterator<ResourceElementBean> selectedObjectsIterator = selectionModelLeft.getSelectedSet().iterator();

				while (selectedObjectsIterator.hasNext()) {
					ResourceElementBean selectedBean = (ResourceElementBean) selectedObjectsIterator.next();
					if(selectedBean.isFolder()){

						// a single folder selected
						if(selectionModelLeft.getSelectedSet().size() == 1){

							// update path
							final NavLink navElem = new NavLink(selectedBean.getName());
							navElem.getElement().getStyle().setFontWeight(FontWeight.BOLD);
							final PathBean pathBean = new PathBean(navElem, selectedBean);

							navElem.addClickHandler(new ClickHandler() {

								@Override
								public void onClick(ClickEvent event) {

									removeStartingFromBreadcrumbs(navElem, pathBean);
									GWT.log("Clicked on element " + pathBean.resourceFolder.getName());
									//breadcrumbsUpdater();
									ResourceElementBean folder = pathBean.resourceFolder;
									Collections.sort(folder.getChildren());
									dataProviderLeft.setList(folder.getChildren());
									dataProviderLeft.refresh();

								}
							});

							pathListBeans.add(pathBean);
							breadcrumbs.add(navElem);
							//breadcrumbsUpdater();

							Collections.sort(selectedBean.getChildren());
							dataProviderLeft.setList(selectedBean.getChildren());
							dataProviderLeft.refresh();
						}
						selectionModelLeft.setSelected(selectedBean, false);
					}
				}
				// enable the buttons that allows to move the objects to the right
				enableMoveToRightButtons(selectionModelLeft.getSelectedSet());
			}
		});

		// set the list into the provider
		Collections.sort(this.initialBean.getChildren());
		dataProviderLeft.setList(this.initialBean.getChildren());

		// add root to breadcrumb
		final NavLink root = new NavLink(initialBean.getName());
		final PathBean pathBean = new PathBean(root, initialBean);
		root.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		pathListBeans.add(pathBean);
		breadcrumbs.add(root);

		root.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				removeStartingFromBreadcrumbs(root, pathBean);
				//breadcrumbsUpdater();

				// set back the root content list
				dataProviderLeft.setList(initialBean.getChildren());
				dataProviderLeft.refresh();
			}
		});

		// set the cell list into the provider
		dataProviderLeft.addDataDisplay(cellListLeft);

		// manage showMorePanelLeft
		showMorePanelLeft.setDisplay(cellListLeft);
		showMorePanelLeft.setHeight(PANEL_HEIGHT);
		showMorePanelLeft.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		showMorePanelLeft.getElement().getStyle().setBorderWidth(2, Unit.PX);
		showMorePanelLeft.getElement().getStyle().setBorderColor(PANEL_BORDER_COLOR);

		// add the list to the leftContainerPanel
		leftContainer.add(showMorePanelLeft);
	}

	/**
	 * Update the path
	 * @param navElem
	 * @param pathBean
	 */
	public void removeStartingFromBreadcrumbs(NavLink navElem, PathBean pathBean){

		// remove data after
		Iterator<Widget> iteratorBreadcrumb = breadcrumbs.iterator();
		Iterator<PathBean> iteratorListPath = pathListBeans.iterator();

		boolean delete = false;
		while(iteratorBreadcrumb.hasNext()){
			Widget current = iteratorBreadcrumb.next();

			if(delete){
				current.removeFromParent();
				iteratorBreadcrumb.remove();
			}

			if(!delete && navElem.equals(current))
				delete = true;
		}

		delete = false;
		while(iteratorListPath.hasNext()){
			PathBean current = iteratorListPath.next();

			if(delete)
				iteratorListPath.remove();

			if(!delete && pathBean.equals(current))
				delete = true;
		}

	}

	/**
	 * Initialize the left side panel
	 */
	private void initRightSidePanel() {

		// initialize the left side list
		ResourceCellRight cell = new ResourceCellRight();

		// Set a key provider that provides a unique key for each object. 
		cellListRight = new CellList<ResourceElementBean>(cell, ResourceElementBean.KEY_PROVIDER);
		cellListRight.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE); 
		cellListRight.setValueUpdater(new ValueUpdater<ResourceElementBean>() {

			@Override
			public void update(ResourceElementBean value) {

				// just redraw the list
				cellListRight.redraw();

			}
		});

		// Add a selection model so we can select cells.
		selectionModelRight = new MultiSelectionModel<ResourceElementBean>(ResourceElementBean.KEY_PROVIDER);
		cellListRight.setSelectionModel(selectionModelRight);

		// perform an action on selection
		selectionModelRight.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {

				if(freezed)
					return;

				enableMoveToLeftButtons(selectionModelRight.getSelectedSet());
			}
		});

		// set the cell list into the provider
		dataProviderRight.addDataDisplay(cellListRight);

		// manage showMorePanelRight
		showMorePanelRight.setDisplay(cellListRight);
		showMorePanelRight.setHeight(PANEL_HEIGHT);
		showMorePanelRight.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		showMorePanelRight.getElement().getStyle().setBorderWidth(2, Unit.PX);
		showMorePanelRight.getElement().getStyle().setBorderColor(PANEL_BORDER_COLOR);

		// add the list to the leftContainerPanel
		rightContainer.add(showMorePanelRight);

	}

	/**
	 * Enable/disable the buttons to move objects from left to right properly.
	 * @param setselectedItemsLeft
	 */
	private void enableMoveToRightButtons(Set<ResourceElementBean> setselectedItemsLeft){

		if(setselectedItemsLeft == null || setselectedItemsLeft.isEmpty()){
			allToRightButton.setEnabled(false);
			toRightButton.setEnabled(false);
		}
		else if(setselectedItemsLeft.size() > 1){
			allToRightButton.setEnabled(true);
			toRightButton.setEnabled(false);
		}
		else{
			allToRightButton.setEnabled(false);
			toRightButton.setEnabled(true);
		}
	}

	/**
	 * Enable/disable the buttons to move objects from right to left properly.
	 * @param setselectedItemsRight
	 */
	private void enableMoveToLeftButtons(Set<ResourceElementBean> setselectedItemsRight){

		if(setselectedItemsRight == null || setselectedItemsRight.isEmpty()){
			allToLeftButton.setEnabled(false);
			allToLeftButton.setEnabled(false);
		}
		else if(setselectedItemsRight.size() > 1){
			allToLeftButton.setEnabled(true);
			toLeftButton.setEnabled(false);
		}
		else{
			allToLeftButton.setEnabled(false);
			toLeftButton.setEnabled(true);
		}
	}

	/**
	 * Prepare the buttons' handlers
	 */
	private void prepareHandlers() {

		allToRightButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				moveToRight(selectionModelLeft.getSelectedSet());
			}
		});

		toRightButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				moveToRight(selectionModelLeft.getSelectedSet());
			}
		});

		allToLeftButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				moveToLeft(selectionModelRight.getSelectedSet());
			}
		});

		toLeftButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				moveToLeft(selectionModelRight.getSelectedSet());
			}
		});
	}

	/**
	 * Move to right elements
	 * @param set the elements to move
	 */
	private void moveToRight(Set<ResourceElementBean> setSelected){

		if(setSelected == null || setSelected.isEmpty())
			return;

		Iterator<ResourceElementBean> iterator = setSelected.iterator();
		while (iterator.hasNext()) {
			ResourceElementBean resourceElementBean = (ResourceElementBean) iterator
					.next();
			resourceElementBean.setToBeAdded(true);	

			int indexRight = dataProviderRight.getList().indexOf(resourceElementBean);
			if(indexRight >= 0)
				dataProviderRight.getList().set(indexRight, resourceElementBean);
			else{
				dataProviderRight.getList().add(resourceElementBean);
				Collections.sort(dataProviderRight.getList());
				dataProviderRight.refresh();
			}

			int indexLeft = dataProviderLeft.getList().indexOf(resourceElementBean);
			dataProviderLeft.getList().set(indexLeft, resourceElementBean);
		}
	}

	/**
	 * Move to left elements
	 * @param setSelected the elements to move
	 */
	private void moveToLeft(Set<ResourceElementBean> setSelected){

		if(setSelected == null || setSelected.isEmpty())
			return;

		Iterator<ResourceElementBean> iterator = setSelected.iterator();
		while (iterator.hasNext()) {
			ResourceElementBean resourceElementBean = (ResourceElementBean) iterator.next();
			resourceElementBean.setToBeAdded(false);	
		}

		Collections.sort(dataProviderLeft.getList());
		dataProviderLeft.refresh();
		dataProviderRight.refresh();
	}

	/**
	 * Freeze the panel
	 */
	public void freeze() {

		freezed = true;

		Iterator<Widget> iteratorOverPath = breadcrumbs.iterator();
		while (iteratorOverPath.hasNext()) {
			Widget widget = (Widget) iteratorOverPath.next();
			if(widget instanceof NavLink)
				((NavLink)widget).setActive(false);
		}

		allToRightButton.setEnabled(false);
		toRightButton.setEnabled(false);
		allToLeftButton.setEnabled(false);
		toLeftButton.setEnabled(false);
	}

	/**
	 * Returns the list of files to save
	 * @return the resources to save
	 */
	public List<ResourceElementBean> getResourcesToPublish(){
		List<ResourceElementBean> current = dataProviderRight.getList();
		List<ResourceElementBean> toReturn = new ArrayList<ResourceElementBean>();

		for (ResourceElementBean resource : current) {
			if(resource.isToBeAdded() && !resource.isFolder()){ // be sure ...
				ResourceElementBean beanWithoutChildren = new ResourceElementBean(resource);
				beanWithoutChildren.setName(resource.getEditableName());
				toReturn.add(beanWithoutChildren);
			}
		}
		return toReturn;
	}

	//	@UiHandler("getResources")
	//	void getResources(ClickEvent ce){
	//		getResourcesToPublish();
	//	}
	//
	//	/**
	//	 * Short the current path if needed
	//	 */
	//	private void breadcrumbsUpdater(){
	//
	//		// ignore first and last elem
	//		boolean reduce = (pathListBeans.size() - 2) > PATH_THRESHOLD;
	//
	//		GWT.log("Is to reduce? " + reduce);
	//		
	//		GWT.log("Full size is " + pathListBeans.size());
	//
	//		if(!reduce){
	//			GWT.log("Restore");
	//			for(int i = 0; i < pathListBeans.size(); i++){
	//				PathBean bean = pathListBeans.get(i);
	//				GWT.log("Elem is " + bean.resourceFolder);
	//				bean.link.setText(bean.resourceFolder.getName());
	//				bean.link.setVisible(true);
	//			}
	//		}else{
	//			for(int i = 1; i < pathListBeans.size(); i++){
	//				PathBean bean = pathListBeans.get(i);
	//
	//				if(i == (pathListBeans.size() - 1)){
	//					bean.link.setText(bean.resourceFolder.getName());
	//					bean.link.setVisible(true);
	//				}else if(i == (pathListBeans.size() - 2)){
	//					GWT.log("The last to modify ****" + bean.resourceFolder.getName());
	//					bean.link.setText("...");
	//					bean.link.setVisible(true);
	//				}else{
	//					bean.link.setText(bean.resourceFolder.getName());
	//					bean.link.setVisible(false);
	//				}
	//			}
	//		}
	//		GWT.log("Updated list is " + pathListBeans.toString());
	//	}
}