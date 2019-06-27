package org.gcube.portlets.user.workspace.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.store.GroupingStore;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * This class is not used
 */
public class GroupingStoreModel implements StoreOperationsInterface{

	public static GroupingStoreModel instance;
	private GroupingStore<FileGridModel>  store;
	
	public static GroupingStoreModel getInstance()
	  {
	    if (instance == null)
	    	instance = new GroupingStoreModel();

	    return instance;
	  }
	
	public GroupingStoreModel() {
		setStore(new GroupingStore<FileGridModel>());
	}

	
	private void setStore(GroupingStore<FileGridModel> store){
		this.store = store;
	}
	
	public GroupingStore<FileGridModel> getStore(){
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
