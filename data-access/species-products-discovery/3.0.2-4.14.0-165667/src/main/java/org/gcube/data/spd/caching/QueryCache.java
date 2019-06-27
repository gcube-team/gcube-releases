package org.gcube.data.spd.caching;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueryCache<T> implements Serializable{

	public static Lock lock = new ReentrantLock(true);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(QueryCache.class);

	private String tableId;
	private boolean valid;
	private boolean tableCreated;
	private boolean empty;
	private boolean error;
	private File file;	
	private File persistencePath;
	private transient ObjectOutputStream writer;

	public QueryCache(String pluginName, String persistencePath) {
		this.persistencePath = new File(persistencePath);
		this.tableId = pluginName+"_"+UUID.randomUUID().toString().replace("-", "_");
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public boolean isTableCreated() {
		return tableCreated;
	}

	public void setTableCreated(boolean tableCreated) {
		this.tableCreated = tableCreated;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}


	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean store( T obj){
		if (file==null){
			file = new File(this.persistencePath, tableId);
			FileOutputStream fos = null;
			DeflaterOutputStream deflater = null;
			try {
				file.createNewFile();
				fos = new FileOutputStream(file);
				deflater = new DeflaterOutputStream(fos, new Deflater(Deflater.BEST_COMPRESSION, true));
				writer = new ObjectOutputStream(deflater);
				logger.debug("file created {}",file.getAbsolutePath());
			} catch (Exception e) {
				if (file!=null)
					file.delete();
				if (deflater!=null)
					try {
						deflater.close();
					} catch (IOException e1) {	}
				if (fos!=null)
					try {
						fos.close();
					} catch (IOException e1) {	}
				this.error = true;
				logger.error("error initializing storage ",e);
				return false;
			}
		}
		try{
			writer.writeObject(Bindings.toXml(obj));
			return true;
		}catch (Exception e) {
			logger.warn(" error storing cache ",e);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public void getAll(ObjectWriter<T> writer){
		logger.debug("file to open is {} ",file==null?null:file.getAbsolutePath());
		try(FileInputStream fis = new FileInputStream(file); InflaterInputStream iis = new InflaterInputStream(fis, new Inflater(true)); ObjectInputStream ois =new ObjectInputStream(iis) ){
			String obj = null;
			while (( obj = (String)ois.readObject())!=null && writer.isAlive())
				writer.write((T)Bindings.fromXml(obj));
		}catch (EOFException eof) {
			logger.debug("EoF erorr reading the cache, it should not be a problem",eof);
		}catch (Exception e) {
			logger.warn(" error gettIng element form cache",e);
		}
	}

	public void closeStore(){
		try {
			if (writer!=null) writer.close();
			if (file==null) empty =true;
			this.valid= true;
		} catch (IOException e) {
			logger.warn(" error closing outputStream ",e);
		}
		
	}

	public boolean isValid(){
		return this.valid;
	}


	public boolean isEmpty() {
		return empty;
	}

	public void dispose(){
		try{
			this.valid=false;
			this.file.delete();
		}catch (Exception e) {
			logger.warn(" error disposing cache ",e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (empty ? 1231 : 1237);
		result = prime * result + (tableCreated ? 1231 : 1237);
		result = prime * result + ((tableId == null) ? 0 : tableId.hashCode());
		result = prime * result + (valid ? 1231 : 1237);
		return result;
	}

	@Override
	public String toString() {
		return "QueryCache [tableId=" + tableId + ", valid=" + valid
				+ ", empty=" + empty + ", error=" + error + "]";
	}



}
