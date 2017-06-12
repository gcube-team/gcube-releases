package org.gcube.portlets.user.workspace.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.store.ListStore;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * This class is not used
 */
public class ListStoreModel implements StoreOperationsInterface{

	public static ListStoreModel instance;
	private ListStore<FileGridModel>  store;
	
	public static ListStoreModel getInstance()
	  {
	    if (instance == null)
	    	instance = new ListStoreModel();

	    return instance;
	  }
	
	public ListStoreModel() {
		setStore(new ListStore<FileGridModel>());
	}
	
	private void setStore(ListStore<FileGridModel> store){
		this.store = store;
	}
	
	public ListStore<FileGridModel> getStore(){
		return this.store;
	}
	
	@Override
	public List<FileGridModel> getListModel() {
		return this.store.getModels();
	}

	@Override
	public void setListModel(List<FileGridModel> listModel) {
		this.store.removeAll();
		this.store.add(listModel);
		
	}
}
