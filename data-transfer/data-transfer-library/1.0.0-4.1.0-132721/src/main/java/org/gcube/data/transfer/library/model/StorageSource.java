package org.gcube.data.transfer.library.model;

import org.gcube.data.transfer.library.faults.InvalidSourceException;
import org.gcube.data.transfer.library.utils.StorageUtils;

public class StorageSource extends Source<String> {

	private String id=null;
	
	
	
	
	
	public StorageSource(String id) throws InvalidSourceException {
		super();
		if(id==null) throw new InvalidSourceException("Storage id cannot be null");
		this.id = id;
	}

	@Override
	public boolean validate() throws InvalidSourceException {
		try{
			if(!StorageUtils.checkStorageId(id)) throw new Exception("Not valid");
		}catch(Exception e){
			throw new InvalidSourceException("Invalid storage ID "+id);
		}
		return true;
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clean() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTheSource() {
		return id;
	}
	
}
