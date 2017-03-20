package gr.uoa.di.madgik.searchlibrary.operatorlibrary.indexfuse;

import gr.uoa.di.madgik.grs.record.Record;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to wrap a file buffer for the records that are beeing processed
 * 
 * @author UoA
 */
public class DiskBuffer {
	/**
	 * The Logger used by the class
	 */
	private static Logger logger = LoggerFactory.getLogger(DiskBuffer.class.getName());
	/**
	 * File writer
	 */
	private RandomAccessFile ptr=null;
	/**
	 * The name of the file used as buffer
	 */
	private String filename=null;
	/**
	 * Time it took to write
	 */
	public long wtime=0;
	/**
	 * Time it took to read
	 */
	public long rtime=0;
	
	/**
	 * Creates a new {@link DiskBuffer}
	 * 
	 * @param filename The name of the file to use
	 * @param flag if<code>true</code> The file is opened for writing, otherwise for reading
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public DiskBuffer(String filename,boolean flag) throws Exception{
		this.filename=filename;
//		if(flag) ptr=new RandomAccessFile(new File(filename),"rwd");
		if(flag) ptr=new RandomAccessFile(new File(filename),"rw");
		else ptr=new RandomAccessFile(new File(filename),"r");
	}
	
	/**
	 * Persists the provided record
	 * 
	 * @param rec The record to persist
	 * @return The position in the file buffer
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public FilePosition persist(Record rec) throws Exception{
		try{
			long now=Calendar.getInstance().getTimeInMillis();
			long start=ptr.getFilePointer();
			ptr.writeUTF(rec.getClass().getName());
			rec.deflate(ptr);
			long end=ptr.getFilePointer();
			wtime+=Calendar.getInstance().getTimeInMillis()-now;
			return new FilePosition(start,end);
		}catch(Exception e){
			logger.error("Could not perist record. throwing Exception",e);
			throw new Exception("Could not perist record");
		}
	}
	
	/**
	 * Closes the underlying file stream
	 * 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void close() throws Exception{
		try{
			ptr.close();
			ptr=null;
		}catch(Exception e){
			logger.error("Could not close buffer file. Throwing Exception",e);
			throw new Exception("Could not close buffer file");
		}
	}
	
	/**
	 * Retrieves the records in teh given possitions
	 * 
	 * @param pos Teh possition to read from
	 * @return Teh records retrieved
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public Vector<Record> retrieve(Vector<FilePosition> pos) throws Exception{
		Vector<Record> res=new Vector<Record>();
		for(int i=0;i<pos.size();i+=1){
			try{
				res.add(this.retrieve(pos.get(i)));
			}catch(Exception e){
				logger.error("Could not retrieve position.Continuing",e);
			}
		}
		return res;
	}
	
	/**
	 * Retrieves the record from the given position
	 * 
	 * @param pos The position to retrieve the record from
	 * @return The retrieved record
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public Record retrieve(FilePosition pos) throws Exception{
		try{
			long now=Calendar.getInstance().getTimeInMillis();
			ptr.seek(pos.getStartOffset());
//			int length=(int)(pos.getStopOffset()-pos.getStartOffset());
//			byte []buf=new byte[length];
//			int count=0;
//			while(count<length){
//				int read=ptr.read(buf,count,length-count);
//				if(read<0){
//					logger.error("Premature End of File reached. Throwing Exception");
//					throw new Exception("Premature End of File reached");
//				}
//				count+=read;
//			}
//			Record rec = RecordUtils.UpgradeFromXML(new String(buf));
			Record rec = (Record)Class.forName(ptr.readUTF()).newInstance();
			rec.inflate(ptr);
			rtime+=Calendar.getInstance().getTimeInMillis()-now;
			return rec;
		}catch(Exception e){
			logger.error("Could not retrieve record. throwing Exception",e);
			throw new Exception("Could not retrieve record");
		}
	}
	
	/**
	 * Clears the underlying structs
	 */
	public void clear(){
		try{
			if(ptr!=null){
				ptr.close();
			}
			if(filename!=null){
				new File(filename).delete();
			}
		}catch(Exception e){
			logger.error("Could not clear internal structures. Continuing",e);
		}
	}
}
