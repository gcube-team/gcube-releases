package org.gcube.portlets.user.workspace.client.view.grids;

import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.ConstantsPortlet;
import org.gcube.portlets.user.workspace.client.gridevent.GridElementSelectedEvent;
import org.gcube.portlets.user.workspace.client.gridevent.GridElementUnSelectedEvent;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.ListStoreModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;


/**
 * The Class GxtGridFilterPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 15, 2016
 */
public class GxtGridFilterPanel extends LayoutContainer {

	private ContentPanel cp = new ContentPanel();
	private ListStore<FileGridModel> store =  ListStoreModel.getInstance().getStore();
	private FileGridModel currenItemSelected = null;
	private FileModel currentFolderView = null;

	/**
	 * Instantiates a new gxt grid filter panel.
	 */
	public GxtGridFilterPanel() {
		setLayout(new FitLayout());
		ColumnConfig name = new ColumnConfig(FileGridModel.NAME, FileGridModel.NAME, 400);
		ColumnConfig type = new ColumnConfig(FileGridModel.TYPE, FileGridModel.TYPE, 100);
		ColumnConfig creationDate = new ColumnConfig(FileGridModel.GRIDCOLUMNCREATIONDATE, FileGridModel.GRIDCOLUMNCREATIONDATE, 100);
		ColumnConfig size = new ColumnConfig(FileGridModel.SIZE, FileGridModel.SIZE, 50);

		ColumnModel cm = new ColumnModel(Arrays.asList(name, type, creationDate, size));

		cp.setBodyBorder(false);
		cp.setHeading(ConstantsPortlet.RESULT);
		cp.setHeaderVisible(true);
		cp.setLayout(new FitLayout());

		GridFilters filters = new GridFilters();
		filters.setLocal(true);

		StringFilter nameFilter = new StringFilter(FileGridModel.NAME);
		StringFilter authorFilter = new StringFilter(FileGridModel.TYPE);
		StringFilter sizeFilter = new StringFilter(FileGridModel.SIZE);

		filters.addFilter(nameFilter);
		filters.addFilter(authorFilter);
		filters.addFilter(sizeFilter);

		final Grid<FileGridModel> grid = new Grid<FileGridModel>(store, cm);

		grid.getView().setAutoFill(true);

		setAlphanumericStoreSorter(grid);

		grid.setAutoExpandColumn(FileGridModel.NAME);
		grid.setBorders(false);
		grid.setStripeRows(true);
		grid.setColumnLines(true);
		grid.addPlugin(filters);


		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<FileGridModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<FileGridModel> se) {
				System.out.println(" SelectionChangedListener selection grid change");

				ModelData target = se.getSelectedItem();

				if(target!=null){
					currenItemSelected = (FileGridModel) target;

					boolean isMultiselection = false;

					if(se.getSelection()!=null && se.getSelection().size()>1)
						isMultiselection = true;
					AppController.getEventBus().fireEvent(new GridElementSelectedEvent(target, isMultiselection));
				}
				else{
					currenItemSelected = null;
					AppController.getEventBus().fireEvent(new GridElementUnSelectedEvent());
				}
			}
		});


		grid.addListener(Events.RowDoubleClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
//				FileGridModel fileModel = (FileGridModel) be.getSource();
//				if(fileModel.isDirectory())
			}

        });

		cp.add(grid);
		add(cp);
	}

	/**
	 * Reset store.
	 */
	private void resetStore(){
		store.removeAll();
	}


	/**
	 * Update store.
	 *
	 * @param result the result
	 * @return true, if successful
	 */
	public boolean updateStore(List<FileGridModel> result){

		resetStore();
		if(result!= null){
			store.add(result);
			return true;
		}
		return false;
	}

	/**
	 * Gets the selected item.
	 *
	 * @return the selected item
	 */
	public FileGridModel getSelectedItem(){

		return currenItemSelected;

	}


	/**
	 * Delete item.
	 *
	 * @param identifier (MANDATORY)
	 * @return true, if successful
	 */
	public boolean deleteItem(String identifier) {

//		FileGridModel fileTarget = (FileGridModel) store.findModel("identifier", identifier);
		FileGridModel fileTarget =  getFileGridModelByIdentifier(identifier);

		if(fileTarget!=null){
			Record record = store.getRecord(fileTarget);
			store.remove((FileGridModel) record.getModel());
			return true;
		}
		else
			System.out.println("Delete Error: file target with " + identifier + " identifier not exist in store" );

		return false;
	}


	/**
	 * Gets the current folder view.
	 *
	 * @return the current folder view
	 */
	public FileModel getCurrentFolderView() {
		return currentFolderView;
	}


	/**
	 * Sets the current folder view.
	 *
	 * @param currentFolderView the new current folder view
	 */
	public void setCurrentFolderView(FileModel currentFolderView) {
		this.currentFolderView = currentFolderView;
	}


	/**
	 * Rename item.
	 *
	 * @param itemIdentifier the item identifier
	 * @param newName the new name
	 * @param extension the extension
	 * @return true, if successful
	 */
	public boolean renameItem(String itemIdentifier, String newName, String extension) {

		if(itemIdentifier!=null){
//			FileGridModel fileTarget = (FileGridModel) store.findModel(ConstantsExplorer.IDENTIFIER, itemIdentifier);
			FileGridModel fileTarget =  getFileGridModelByIdentifier(itemIdentifier);
			if(fileTarget!=null){
				Record record = store.getRecord(fileTarget);
				if(record!=null){
					if(extension!= null)
						record.set(FileGridModel.NAME, newName+extension);
					else
						record.set(FileGridModel.NAME, newName);

					return true;
				}
			}
			else
				System.out.println("Record Error: file target not exist in store" );
		}
		else
			System.out.println("Rename Error: file target with is null" );

		return false;

	}

	/**
	 * Sets the alphanumeric store sorter.
	 *
	 * @param grid the new alphanumeric store sorter
	 */
	private void setAlphanumericStoreSorter(Grid<FileGridModel> grid){

		// Sorting files
		grid.getStore().setStoreSorter(new StoreSorter<FileGridModel>() {

			@Override
			public int compare(Store<FileGridModel> store, FileGridModel m1, FileGridModel m2, String property) {
				boolean m1Folder = m1.isDirectory();
				boolean m2Folder = m2.isDirectory();

				if (m1Folder && !m2Folder) {
					return -1;
				} else if (!m1Folder && m2Folder) {
					return 1;
				}

				return m1.getName().compareTo(m2.getName());
			}
		});
	}

	/**
	 * Gets the file grid model by identifier.
	 *
	 * @param id the id
	 * @return the file grid model by identifier
	 */
	public FileGridModel getFileGridModelByIdentifier(String id){
		return store.findModel(FileGridModel.IDENTIFIER, id);
	}

	/**
	 * Gets the store.
	 *
	 * @return the store
	 */
	public ListStore<FileGridModel> getStore(){
		return store;
	}
}