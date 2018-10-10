/**
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.client;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;


/**
 * The Class HTML5StorageUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 17, 2018
 */
public class HTML5StorageUtil {

	private Storage localStorage;
	private StorageMap localStorageMap;


	/**
	 * Instantiates a new HTM l5 storage util.
	 */
	public HTML5StorageUtil() {
		initLocalStorage();
		initStorageMap();
	}

	/**
	 * Inits the local storage.
	 */
	private void initLocalStorage(){
		if(localStorage==null)
			localStorage = Storage.getSessionStorageIfSupported();
	}

	/**
	 * Inits the storage map.
	 *
	 * @return the storage map
	 */
	private StorageMap initStorageMap(){
		if(localStorageMap==null)
			localStorageMap = new StorageMap(localStorage);

		return localStorageMap;

	}

	/**
	 * Checks if is supported.
	 *
	 * @return true, if is supported
	 */
	public boolean isSupported(){

		return localStorage!=null?true:false;

	}

	/**
	 * Sets the item.
	 *
	 * @param key the key
	 * @param data the data
	 */
	public void setItem(String key, String data){

		if(localStorage==null)
			return;

		localStorage.setItem(key, data);
	}


	/**
	 * Gets the item.
	 *
	 * @param key the key
	 * @return the item
	 */
	public String getItem(String key){
		if(localStorage==null)
			return "";

		String data = localStorage.getItem(key);

		return data==null?"":data;
	}

	/**
	 * Append value.
	 *
	 * @param key the key
	 * @param data the value
	 */
	public void appendValue(String key, String data){

		if(localStorageMap==null)
			return;

		String newData = "";
		if (localStorageMap.containsKey(key)!= true){
			newData = localStorage.getItem(key); //adding old data at start
		}

		newData += data;
		setItem(key, newData);
	}


	/**
	 * Gets the local storage.
	 *
	 * @return the localStorage
	 */
	public Storage getLocalStorage() {

		return localStorage;
	}


	/**
	 * Gets the local storage map.
	 *
	 * @return the localStorageMap
	 */
	public StorageMap getLocalStorageMap() {

		return localStorageMap;
	}

}
