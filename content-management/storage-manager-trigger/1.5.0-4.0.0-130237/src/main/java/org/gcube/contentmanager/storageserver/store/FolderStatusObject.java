package org.gcube.contentmanager.storageserver.store;

import java.util.Date;
import com.mongodb.DBObject;

public class FolderStatusObject {
	
	private String folder;
	
	private String originalFolder;
	
	private long volume;
	
	private int count;
	
	private String lastUpdate;

	private String id;
	
	private DBObject dbo;

	
	public FolderStatusObject(String folderPath, long volume, int count, String lastUpdate, String from){
		this.folder=folderPath;
		this.volume=volume;
		this.count=count;
		this.lastUpdate=lastUpdate;
		this.originalFolder=from;
	}
	
	public FolderStatusObject(String id, String folderPath, long volume, int count, String lastUpdate, String from, DBObject obj){
		this.id=id;
		this.folder=folderPath;
		this.volume=volume;
		this.count=count;
		this.lastUpdate=lastUpdate;
		this.originalFolder=from;
		this.dbo=obj;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOriginalFolder() {
		return originalFolder;
	}

	public void setOriginalFolder(String originalFolder) {
		this.originalFolder = originalFolder;
	}
	
}
