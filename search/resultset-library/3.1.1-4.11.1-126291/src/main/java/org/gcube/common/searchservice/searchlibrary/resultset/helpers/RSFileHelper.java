package org.gcube.common.searchservice.searchlibrary.resultset.helpers;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Vector;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.resultset.ResultSet;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.HeaderRef;
import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultSetRef;
import org.gcube.common.searchservice.searchlibrary.resultset.security.Mnemonic;


/**
 * Helper class of the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} element
 * 
 * @author UoA
 */
public class RSFileHelper {
	/**
	 * The Logger this class uses
	 */
	private static Logger log = Logger.getLogger(RSFileHelper.class);

	/**
	 * Computes a hash of the provided string
	 * 
	 * @param key The key to hash
	 * @return The hash result
	 */
	private static String hash(String key){
		int hash=0;
		for(int i=0;i<key.length();i+=1){
			try{
				hash+=key.charAt(i);
			}catch(Exception e){}
		}
		return Integer.toString(hash);
	}
	
	/**
	 * Generates a name of the type that is provided which can either be unique or have the unique 
	 * value part equal to the provided one
	 * 
	 * @param type The type of filename to generate. This can be one of {@link RSConstants#CONTENT}, 
	 * {@link RSConstants#HEADER}, {@link RSConstants#PAGEDCONTENT}
	 * @param seed If a non <code>null</code> value is provided, The unique part of the generated file
	 * will have the supplied value. Otherwise, a unique value is generated and used
	 * @return The generated file name
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static String generateName(short type,String seed) throws Exception{
		try {
			File base=new File(RSConstants.baseDirectory);
			if(!base.exists()){
				boolean succeeded=base.mkdirs();
				Runtime.getRuntime().exec("chmod 0777 "+base.getAbsolutePath());
				if(!succeeded){
					log.error("Could not create base directory "+RSConstants.baseDirectory+" Throwing Exception");
					throw new Exception("Could not create base directory "+RSConstants.baseDirectory);
				}
			}
			String unique=null;
			if(seed==null) unique=RSConstants.nextUUID();
			else unique=seed;
			String subdir=RSFileHelper.hash(unique);
			File subBase=new File(RSConstants.baseDirectory+subdir);
			if(!subBase.exists()){
				boolean succeeded=subBase.mkdirs();
				Runtime.getRuntime().exec("chmod 0777 "+subBase.getAbsolutePath());
				if(!succeeded){
					if(!subBase.exists()){
						log.error("Could not create subase directory "+RSConstants.baseDirectory+subdir+" Throwing Exception");
						throw new Exception("Could not create subase directory "+RSConstants.baseDirectory+subdir);
					}
				}
			}
			String genbase=RSConstants.baseDirectory+subdir+File.separatorChar;
			if(type==RSConstants.CONTENT){
				return genbase+unique+RSConstants.cextention;
			}
			else if(type==RSConstants.HEADER){
				return genbase+unique+RSConstants.hextention;
			}
			else if(type==RSConstants.PAGEDCONTENT){
				return genbase+unique+RSConstants.pextention;
			}
			else{
				log.error("Unrecognized type "+type+" Throwing Exception");
				throw new Exception("Unrecognized type "+type);
			}
		}catch(Exception e){
			log.error("Could not create base directory "+RSConstants.baseDirectory+" Throwing Exception",e);
			throw new Exception("Could not create base directory "+RSConstants.baseDirectory);
		}
	}
	
	/**
	 * Constructs the flag used for on demand production file that corresponds to the specfic head part
	 * 
	 * @param head The head file of the RS
	 * @return the flag file
	 */
	public static String headToFlowControl(String head){
		return head+RSConstants.dfextention;
	}
	
	/**
	 * Constructs the temporary flag used for on demand production file that corresponds to the specfic head part
	 * 
	 * @param head The head file of the RS
	 * @return the flag file
	 */
	public static String headToFlowControlTmp(String head){
		return head+RSConstants.dfextention+RSConstants.textention;
	}
	
	/**
	 * blocks waiting for notifications on result production for the specified time period
	 * 
	 * @param headname The head file
	 * @param time The timeout
	 * @return The status of the request
	 */
	public static RSConstants.CONTROLFLOW waitOnFlowNotification(String headname,long time){
		File head=new File(headname);
		if(!head.exists()) return RSConstants.CONTROLFLOW.STOP;
		long startTime=Calendar.getInstance().getTimeInMillis();
		while(true){
			if(Calendar.getInstance().getTimeInMillis()-startTime>=time){
				log.error("Maximum waiting ammount of time reached. Returning false");
				return RSConstants.CONTROLFLOW.TIMEOUT;
			}
			try{
				synchronized(RSConstants.controlFlowOnIt){
					RSConstants.controlFlowOnIt.wait(RSConstants.sleepTime);
				}
			}catch(Exception ee){}
			File flag=new File(RSFileHelper.headToFlowControl(headname));
			if(flag.exists()) return RSConstants.CONTROLFLOW.MORE;
		}
	}
	
	/**
	 * notidies the consumer that more records were created
	 * 
	 * @param headname The head name of the RS that needed more results
	 */
	public static void notifyOnFlowCreation(String headname){
		synchronized(RSConstants.controlFlowOnIt){
			File flag=new File(RSFileHelper.headToFlowControl(headname));
			try{
				flag.delete();
			}catch(Exception e){
				log.error("Could not delete flag file "+RSFileHelper.headToFlowControl(headname)+". Continuing",e);
			}
//			RSConstants.controlFlowOnIt.notifyAll();
		}
	}
	
	/**
	 * notifies the producer of an RS that more records are needed
	 * 
	 * @param headname The head name of the RS that needs more results
	 */
	public static void requestFlowCreation(String headname){
		synchronized(RSConstants.controlFlowOnIt){
			try{
				File flag=new File(RSFileHelper.headToFlowControlTmp(headname));
				flag.createNewFile();
				flag.renameTo(new File(RSFileHelper.headToFlowControl(headname)));
				RSConstants.controlFlowOnIt.notifyAll();
			}catch(Exception e){
				log.error("Could not create flag file "+RSFileHelper.headToFlowControl(headname)+". Continuing",e);
			}
		}
	}
	
	/**
	 * Converts a public file name to a private temparary one
	 * 
	 * @param pubName The file name to convert
	 * @return The temporary name
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static String publicToTmp(String pubName) throws Exception{
		try{
			return pubName+RSConstants.textention;
		}catch(Exception e){
			log.error("Could not transform public "+pubName+" to tmp. Throwing Exception",e);
			throw new Exception("Could not transform public "+pubName+" to tmp");
		}
	}
	
	/**
	 * Converts a tmp file name to a public one
	 * 
	 * @param tmpName the temporary name
	 * @return the public name
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static String tmpToPublic(String tmpName) throws Exception{
		try{
			return tmpName.substring(0,tmpName.lastIndexOf(RSConstants.textention));
		}catch(Exception e){
			log.error("Could not transform tmp "+tmpName+" to public. Throwing Exception",e);
			throw new Exception("Could not transform tmp "+tmpName+" to public");
		}
	}
	
	/**
	 * Converts a header file name to the respective content one.
	 * 
	 * @param headerName THe header name to convert
	 * @return The respective content one
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static String headerToContent(String headerName) throws Exception{
		try{
			String seed=headerName.substring(
					(headerName.lastIndexOf(File.separatorChar)+(Character.toString(File.separatorChar)).length()),
							headerName.lastIndexOf(RSConstants.hextention));
			return RSFileHelper.generateName(RSConstants.CONTENT,seed);
		}catch(Exception e){
			log.error("Could not transform header name "+headerName+" to content name. Throwing Exception");
			throw new Exception("Could not transform header name "+headerName+" to content name");
		}
	}
	
	/**
	 * Converts a content file name to the respective header one.
	 * 
	 * @param contentName The content name to convert
	 * @return The respective header one
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static String contentToHeader(String contentName) throws Exception{
		try{
			String seed=contentName.substring((contentName.lastIndexOf(File.separatorChar)+(Character.toString(File.separatorChar)).length()),contentName.lastIndexOf(RSConstants.cextention));
			return RSFileHelper.generateName(RSConstants.HEADER,seed);
		}catch(Exception e){
			log.error("Could not transform content name "+contentName+" to header name. Throwing Exception");
			throw new Exception("Could not transform content name "+contentName+" to header name");
		}
	}
	
	/**
	 * Persists the content available in the provided vector o a file whose name is given. The number of
	 * content records to persist is given as a parameter
	 * 
	 * @param filename The filename to perist the content in
	 * @param results The vector holding the payload
	 * @param mne the encryption class
	 * @param count The number of records to persist. If this is negative, all the available records are persisted 
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static void persistContent(String filename, Vector<String> results, int count, Mnemonic mne) throws Exception{
		FileOutputStream fw=null;
		CipherOutputStream ci = null;
		BufferedWriter bw=null;
		OutputStreamWriter out = null;
		log.trace("persistContent of vectors called with file " + filename);
		try{
//			fw=new FileWriter(RSFileHelper.publicToTmp(filename));
			fw = new FileOutputStream(filename);
			if (mne!=null){ //secure
				ci = new CipherOutputStream(fw, mne.getCipher());
				out = new OutputStreamWriter(ci);
			}else{
				out = new OutputStreamWriter(fw);
			}
			bw=new BufferedWriter(out);
			bw.write("<"+RSConstants.BodyTag+">\n");
			if(results!=null){
				int copy=results.size();
				if(count>=0) copy=count;
				for(int i=0;i<copy && i<results.size();i+=1){
					bw.write(results.get(i));
					bw.write("\n");
				}
			}
			bw.write("</"+RSConstants.BodyTag+">\n");
			bw.close();
			bw=null;
			out.close();
			out = null;
			if (mne!=null){
				ci.close();
				ci =null;
			}
			fw.close();
			fw=null;
			File tmpFile=new File(RSFileHelper.publicToTmp(filename));
			tmpFile.renameTo(new File(filename));
		}catch(Exception e){
			log.error("Could not persist content to file "+filename+". Throwing Exception",e);
			if(bw!=null) bw.close();
			if(fw!=null) fw.close();
			throw new Exception("Could not persist content to file "+filename);
		}
	}
	
	/**
	 * forces the filesystem to remain consistent by using metadata changing functions
	 * 
	 * @param filename the filename
	 * @return whether or not the forcing was successful
	 * @throws Exception An error has occured
	 */
	public static boolean forceConsistency(String filename) throws Exception{
		File pub=new File(filename);
		String tmp=RSFileHelper.publicToTmp(filename);
		File tmpFile=new File(tmp);
		boolean success=true;
		if(pub.renameTo(tmpFile)){
			success=tmpFile.renameTo(pub);
		}
		else success=false;
		log.debug("consistency forcing was successful : "+success);
		return success; 
	}

	/**
	 * Persists the content of the provided source file to the provided target file
	 * 
	 * @param filename The target file
	 * @param inWrap The source file
	 * @param mne the encryption class
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public static void persistContent(String filename, String inWrap, Mnemonic mne) throws Exception{
		log.trace("persistContent of wrap called with file " + filename + " and wrap " + inWrap);
		try{
			if (mne != null){
				log.info("Encrypting " + inWrap +" into "+ filename);
				FileInputStream source = new FileInputStream(inWrap);
				FileOutputStream fw = new FileOutputStream(filename);
				CipherOutputStream ci = new CipherOutputStream(fw, mne.getCipher());
				 int i;
				 byte[] b = new byte[1024];
				 while((i=source.read(b))!=-1) {
				      ci.write(b, 0, i);
				    }
				source.close();
				ci.close();
				fw.close();
				File s=new File(inWrap);
				s.delete();
			}else{
				log.info("Not Encrypting " + inWrap +" into "+ filename);
				File source=new File(inWrap);
				int retry=0;
				while(true){
					if(source.renameTo(new File(filename))) break;
					if(retry>=5) break;
					log.error("renaming failed, retrying");
					retry+=1;
				}
			}
		}catch(Exception e){
			log.error("Could not persist content to file "+filename+" from file "+inWrap+". Throwing Exception",e);
			throw new Exception("Could not persist content to file "+filename+" from file "+inWrap);
		}
	}
	
	/**
	 * Persists a header file with the information available in its argument 
	 * 
	 * @param header The header info
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static void persistHeader(HeaderRef header) throws Exception{
		FileWriter fw=null;
		BufferedWriter bw=null;
		try{
			StringBuilder buf=new StringBuilder();
			buf.append(RSConstants.localFileName+":"+header.getLocalName()+"\n");
			buf.append(RSConstants.isHead+":"+header.getIsHead()+"\n");
			buf.append(RSConstants.nextLink+":"+header.getNext()+"\n");
			buf.append(RSConstants.previousLink+":"+header.getPrev()+"\n");
			fw=new FileWriter(new File(RSFileHelper.publicToTmp(header.getLocalName())));
			bw=new BufferedWriter(fw);
			bw.write(buf.toString());
			bw.flush();
			bw.close();
			bw=null;
			fw.close();
			fw=null;
			File tmpFile=new File(RSFileHelper.publicToTmp(header.getLocalName()));
			tmpFile.renameTo(new File(header.getLocalName()));
		}catch(Exception e){
			if(fw!=null) fw.close();
			if(bw!=null) bw.close();
			log.error("Could not persist header to file. Throwing Exception",e);
			throw new Exception("Could not persist header to file");
		}
	}
	
	/**
	 * Populates a header info class instance from the content of the provided file
	 * 
	 * @param filename The file holding the header payload
	 * @return The Header info
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static HeaderRef populateHeader(String filename) throws Exception {
		FileReader fr=null;
		BufferedReader br=null;
		try{
			RSFileHelper.isReady(filename);
			String localFilename=null;
			String isHead=null;
			String nextLink=null;
			String previousLink=null;
			fr=new FileReader(new File(filename));
			br=new BufferedReader(fr);
			String line=br.readLine();
			while(line!=null){
				int index=line.indexOf(":");
				String key=line.substring(0,index);
				String value=line.substring((index+":".length()));
				if(key.equalsIgnoreCase(RSConstants.localFileName)){
					localFilename=value;
				}
				else if(key.equalsIgnoreCase(RSConstants.isHead)){
					isHead=value;
				}
				else if(key.equalsIgnoreCase(RSConstants.nextLink)){
					nextLink=value;
				}
				else if(key.equalsIgnoreCase(RSConstants.previousLink)){
					previousLink=value;
				}
				line=br.readLine();
			}
			br.close();
			br=null;
			fr.close();
			fr=null;
			return new HeaderRef(isHead,localFilename,previousLink,nextLink);
		}catch(Exception e){
			if(fr!=null) fr.close();
			if(br!=null) br.close();
			log.error("Could not populate header from file "+filename+". Throwing Exception",e);
			throw new Exception("Could not populate header from file "+filename);
		}
	}
	
	/**
	 * Populates a header info class instance from the content of the provided file
	 * 
	 * @param filename The file holding the header to touch
	 */
	public static void touchHeader(String filename){
		long time = System.currentTimeMillis();
		log.debug("Touching file: "+filename+" last modified time: "+time);
		File file = new File(filename);
		file.setLastModified(time);
	}
	
	/**
	 * Waits until the provided file is ready to be consumed or a maximum amount of time has been reached.
	 * The maximum amount of time is the one declared in {@link RSConstants#sleepMax} in milliseconds
	 * 
	 * @param filename The filename to wait for
	 * @param flowControl whether or not the RS supports on demand production of results
	 * @param headname The head file name of this RS
	 * @return whether or not the filename if ready to be read
	 */
	public static boolean waitForIt(String filename,boolean flowControl,String headname){
		return RSFileHelper.waitForIt(filename,RSConstants.sleepMax,flowControl,headname);
//		if(RSFileHelper.isReady(filename)){
//			return true;
//		}
//		long startTime=Calendar.getInstance().getTimeInMillis();
//		while(true){
//			if(Calendar.getInstance().getTimeInMillis()-startTime>=RSConstants.sleepMax){
//				log.error("Maximum waiting ammount of time reached. Returning false");
//				return false;
//			}
//			try{
//				synchronized(RSConstants.sleepOnIt){
//					RSConstants.sleepOnIt.wait(RSConstants.sleepTime);
//				}
//			}catch(Exception ee){}
//			if(RSFileHelper.isReady(filename)){
//				return true;
//			}
//		}
	}

	/**
	 * Waits until the provided file is ready to be consumed or a maximum amount of time has been reached.
	 * The maximum amount of time is the one provided in milliseconds
	 * 
	 * @param filename The filename to wait for
	 * @param time The maximum time in milliseconds to wait for the file
	 * @param flowControl whether or not the RS supports on demand production of results
	 * @param headname The head file name of this RS
	 * @return whether or not the filename if ready to be read
	 */
	public static boolean waitForIt(String filename,long time,boolean flowControl,String headname){
		if(RSFileHelper.isReady(filename)){
			return true;
		}
		long startTime=Calendar.getInstance().getTimeInMillis();
		while(true){
			if(Calendar.getInstance().getTimeInMillis()-startTime>=time){
				log.error("Maximum waiting ammount of time reached. Returning false");
				return false;
			}
			try{
				if(flowControl) RSFileHelper.requestFlowCreation(headname);
				synchronized(RSConstants.sleepOnIt){
					RSConstants.sleepOnIt.wait(RSConstants.sleepTime);
				}
			}catch(Exception ee){}
			if(RSFileHelper.isReady(filename)){
				return true;
			}
		}
	}

	/**
	 * Checks if the provided file is ready for consumption
	 * 
	 * @param filename The name of the file to check if it is ready
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 */
	public static boolean isReady(String filename){
		try{
			File tmp=new File(filename);
			if(tmp.exists() && tmp.length()>0){
				return true;
			}
//			if(tmp.exists() && tmp.length()<=0) System.out.println("GOT YOU");
			return false;
		}catch(Exception e){
			log.error("Caught exception while examining file "+filename+". Returning false",e);
			return false;
		}
	}
	
	/**
	 * Populates a record vecror with the records available in the provided file
	 * 
	 * @param filename The file to retrieve the records from
	 * @param mne the encryption class
	 * @return The populated vector
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static Vector<String> populateResults(String filename, Mnemonic mne) throws Exception{
		log.trace("populateResults called");
		FileInputStream fr =null;
		CipherInputStream ci = null;
		InputStreamReader in = null;
		BufferedReader br=null;
		try{
			Vector<String> results=new Vector<String>();
			fr = new FileInputStream(filename);
			if (mne!=null){ //secure
				ci = new CipherInputStream(fr, mne.getDeCipher());
				in = new InputStreamReader(ci);
				log.trace("Decrypting File: "+ filename);
			}else{
				in = new InputStreamReader(fr);
				log.trace("NOT Decrypting File: "+ filename);
			}
			br=new BufferedReader(in);
			RecordParser parse=new RecordParser();
			StringBuffer buf=new StringBuffer();
			boolean inRecord=false;
			String line=br.readLine();
			while(line!=null){
				Vector<Integer> count=parse.containsRecord(line);
				if(count.size()==0){
					if(inRecord){
						buf.append(line);
					}
				}
				else if(count.size()==1){
					if(inRecord){
						buf.append(parse.getHead(line,count.get(0)));
						results.add(buf.toString());
					}
					buf=new StringBuffer();
					buf.append(parse.getTail(line,count.get(0)));
					inRecord=true;
				}
				else{
					if(inRecord){
						buf.append(parse.getHead(line,count.get(0)));
						results.add(buf.toString());
					}
					results.addAll(parse.getFullRecords(line,count));
					buf=new StringBuffer();
					buf.append(parse.getTail(line,count.get(count.size()-1)));
					inRecord=true;
				}
				line =br.readLine();
			}
			if(inRecord) results.add(buf.toString());
			if(results.size()>0){
				results.set(results.size()-1,results.get(results.size()-1).substring(0,results.get(results.size()-1).lastIndexOf("</"+RSConstants.BodyTag+">")));
			}
			boolean cont=true;
			boolean found=false;
			while(cont){
				found=false;
				for(int i=0;i<results.size();i+=1){
					if(results.get(i).trim().length()==0){
						found=true;
						results.remove(i);
						break;
					}
				}
				if(found) cont=true;
				else cont=false;
			}
			br.close();
			br=null;
			in.close();
			in = null;
			if (mne!=null){
				ci.close();
				ci =null;
			}
			fr.close();
			fr=null;
			return results;
		}catch(Exception e){
			log.error("Could not populate results. Throwing Exception",e);
			if(br!=null) br.close();
			if(fr!=null) fr.close();
			throw new Exception("Could not populate results");
		}
	}

	/**
	 * Retrieves the full content of the provided file
	 * 
	 * @param file The file whose content must be retrieved
	 * @param mne the encryption class
	 * @return The full file content
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public static String getContent(String file, Mnemonic mne) throws Exception{
		log.trace("getContent of file : " +file+ " called");
		FileInputStream fr =null;
		CipherInputStream ci = null;
		InputStreamReader in = null;
		BufferedReader br=null;
		try{
			StringBuffer ret=new StringBuffer("");
			fr = new FileInputStream(file);
			if (mne!=null && !file.endsWith(RSConstants.pextention)){ //secure
				ci = new CipherInputStream(fr, mne.getDeCipher());
				in = new InputStreamReader(ci);
				log.info("Decrypting File: "+ file);
			}else{
				in = new InputStreamReader(fr);
				log.info("NOT Decrypting File: "+ file);
			}
			br = new BufferedReader(in);
			String record = null;
			while ( (record=br.readLine()) != null ) {
				ret.append(record);
			}
			br.close();
			br=null;
			fr.close();
			fr=null;
			return ret.toString();
		}catch(Exception e){
			if(fr!=null) fr.close();
			if(br!=null) br.close();
			log.error("Content of file "+file+" could not be retrived Throwing Exception",e);
			throw new Exception("Content of file "+file+" could not be retrived");
		}
	}

	/**
	 * Retrieves the full content of the provided file
	 * 
	 * @param file The file whose content must be retrieved
	 * @param mne the encryption class
	 * @return The full file content
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public static DataInputStream getBinaryContent(String file, Mnemonic mne) throws Exception{
		log.trace("getBinaryContent of file : " +file+ " called");
		FileInputStream fr =null;
		CipherInputStream ci = null;
		DataInputStream in = null;
		try{
			fr = new FileInputStream(file);
			if (mne!=null && !file.endsWith(RSConstants.pextention)){ //secure
				ci = new CipherInputStream(fr, mne.getDeCipher());
				in = new DataInputStream(ci);
				log.info("Decrypting File: "+ file);
			}else{
				in = new DataInputStream(fr);
				log.info("NOT Decrypting File: "+ file);
			}
			
			return in;
		}catch(Exception e){
			if(fr!=null) fr.close();
			log.error("Content of file "+file+" could not be retrived Throwing Exception",e);
			throw new Exception("Content of file "+file+" could not be retrived");
		}
	}

	/**
	 * Splits the provided file in smaller parts as is dictated by {@link RSConstants#partSize}
	 * 
	 * @param filename The file to split
	 * @return A vector holding the names of the created parts
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static String []splitFile(String filename) throws Exception{
		FileInputStream in=null;
		try{
			log.trace("ToSplit:"+filename);
			long length=new File(filename).length();
			long parts=length/RSConstants.partSize;
			if(length%RSConstants.partSize!=0) parts+=1;
			in=new FileInputStream(filename);
			Vector<String> partsNames=new Vector<String>();
			for(int i=0;i<parts;i+=1){
				partsNames.add(RSFileHelper.generateName(RSConstants.PAGEDCONTENT,null));
				FileOutputStream out=new FileOutputStream(new File(RSFileHelper.publicToTmp(partsNames.get(i))));
				int count=0;
				int read=0;
				while(count<RSConstants.partSize && read!=-1){
					byte []buf=new byte[RSConstants.blockSize];
					read=in.read(buf);
					if(read!=-1) out.write(buf,0,read);
				}
				out.close();
				File tmpFile=new File(RSFileHelper.publicToTmp(partsNames.get(i)));
				tmpFile.renameTo(new File(partsNames.get(i)));
			}
			in.close();
			in=null;
			log.trace("AfterSplit:"+partsNames.get(0)+" of "+partsNames.size());
			return partsNames.toArray(new String [0]);
		}catch(Exception e){
			if(in!=null)in.close();
			log.error("Could not split file "+filename+". Throwing Exception",e);
			throw new Exception("Could not split file "+filename);
		}
	}
	
	/**
	 * Copies the source file to an output stream
	 * 
	 * @param src The source file
	 * @param dst The destination stream
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static void copy(String src, DataOutputStream dst) throws Exception {
		InputStream in =null;
		try{
			in = new FileInputStream(new File(src));
			byte[] buf = new byte[RSConstants.transportBlockSize];
			int len;
			while ((len = in.read(buf)) > 0) {
				dst.writeInt(len);
				dst.write(buf, 0, len);
				log.info("Sending part (" +len+ ") : " + new String(buf));
			}
			in.close();
			in=null;
		}catch(Exception e){
			if(in!=null) in.close();
			log.error("Could not copy file "+src+" to output stream. Throwing Exception",e);
			throw new Exception("Could not copy file "+src+" to output stream",e);
		}
	}
	
	/**
	 * Copies the source file to a new file
	 * 
	 * @param src The source file
	 * @param dst The destination file
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static void copy(String src, String dst) throws Exception {
		InputStream in =null;
		OutputStream out =null;
		try{
			in = new FileInputStream(new File(src));
			out = new FileOutputStream(new File(RSFileHelper.publicToTmp(dst)));
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			in=null;
			out.close();
			out=null;
			File tmpFile=new File(RSFileHelper.publicToTmp(dst));
			tmpFile.renameTo(new File(dst));
		}catch(Exception e){
			if(in!=null) in.close();
			if(out!=null) out.close();
			log.error("Could not copy file "+src+" to "+dst+". Throwing Exception",e);
			throw new Exception("Could not copy file "+src+" to "+dst,e);
		}
	}
	
	
	/**
	 * Copies the source file to a new file
	 * @param rs the RS
	 * @param src The source file
	 * @param dst The destination file
	 * @throws Exception An unrecoverable for the operation error occurred
	 */
	public static void copy(ResultSet rs, String src, DataOutputStream dst) throws Exception  {
		
		log.trace("Copy file "+src+" to output stream.");
		try{
			ResultSetRef ref = rs.getRSRef();
			byte[] buf = new byte[RSConstants.transportBlockSize];
			DataInputStream instring = getBinaryContent(src, ref.getMnemonic());
			
			int len;
			while ((len = instring.read(buf)) > 0) {
//				log.trace("Content send to the other side: "+new String(buf));				
				dst.writeInt(len);
				dst.write(buf, 0, len);
			}
			instring.close();
		}catch(Exception e){
			log.error("Could not copy file "+src+" to output stream. Throwing Exception",e);
			throw new Exception("Could not copy file "+src+" to output stream",e);
		}		
	}

	
	/**
	 * persists the stream in a file and returns the filename
	 * 
	 * @param content the stream to persist
	 * @return the filename
	 * @throws Exception an unrecoverable for the operation error occurred
	 */
	public static String persistStream(InputStream content) throws Exception{
		log.trace("persistStream called");
		DataInputStream in = null;
		OutputStream out=null;
		try{
			in = new DataInputStream(content);
			String file=RSFileHelper.generateName(RSConstants.PAGEDCONTENT,null);
			log.trace("persist stream produces this file "+file);
			out=new FileOutputStream(new File(file));
			byte[] buf = new byte[4096];
			int len;
			int sum=0;
			while ((len = in.read(buf)) >= 0) {
				sum+=len;
				out.write(buf, 0, len);
			}
			in.close();
			in=null;
			out.close();
			out=null;
			return file;
		}catch(Exception e){
			if(in!=null) in.close();
			if(out!=null) out.close();
			log.error("Could not persist stream. Throwing Exception",e);
			throw new Exception("Could not persist stream",e);
		}
	}
	
	/**
	 * Retrives the number of results in the provided file that has been structured as a text
	 * payload part
	 * 
	 * @param filename The file to retrieve the records from
	 * @return The number of records
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public static int getNumberOfResultsInTextFile(String filename) throws Exception{
		FileReader fr=null;
		BufferedReader br=null;
		try{
			fr=new FileReader(new File(filename));
			br=new BufferedReader(fr);
			String line=br.readLine();
			line=br.readLine();
			int count=0;
			if(line.indexOf(RSConstants.HeadTag)>=0){
				count=Integer.parseInt(line.substring(("<"+RSConstants.HeadTag+">").length(),line.indexOf(("</"+RSConstants.HeadTag+">"))));
			}
			else{
				if(line.indexOf(RSConstants.BodyTag)>=0) count=0;
				else count=Integer.parseInt(line);
			}
			br.close();
			br=null;
			fr.close();
			fr=null;
			return count;
		}catch(Exception e){
			if(fr!=null) fr.close();
			if(br!=null) br.close();
			log.error("Could not retrieve number of records from file "+filename+". Returning 0",e);
			return 0;
		}
	}
	
	/**
	 * Read file into a byte array
	 * @param file The File handler
	 * @return A byte array that stores the file content
	 * @throws Exception in case of error
	 */
	public static byte[] getBytesFromFile(File file) throws Exception
	{
		log.trace("getBytesFromFile called");
		InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
        	throw new IOException("File is too large.");
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();

        return bytes;
    }

	/**
	 * Attempt to call the garbage collector 
	 */
	public static void runGC (){
		try{
		for (int r = 0; r < 4; ++ r) forceGC ();
		}catch(Exception e){
			log.error("Caught exception while trying to force garbage collector. Continuing",e);
		}
	}
	
	/**
	 * Attempt to call the garbage collector 
	 */
	private static void forceGC(){
		long usedMem1 = usedMemory();
		long usedMem2 = Long.MAX_VALUE;
		for (int i = 0; (usedMem1<usedMem2) && (i<500); i+=1)
		{
			s_runtime.runFinalization();
			s_runtime.gc();
			Thread.yield();
			usedMem2=usedMem1;
			usedMem1=usedMemory();
		}
	}
	
	/**
	 * The ammount of used memory
	 * 
	 * @return The amount of used memory
	 */
	private static long usedMemory (){
		return s_runtime.totalMemory()-s_runtime.freeMemory();
	}
	
	/**
	 * The runntime environment
	 */
	private static final Runtime s_runtime=Runtime.getRuntime();

}
