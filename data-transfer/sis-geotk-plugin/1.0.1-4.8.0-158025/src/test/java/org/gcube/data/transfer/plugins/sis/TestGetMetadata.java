package org.gcube.data.transfer.plugins.sis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.UnsupportedStorageException;
import org.gcube.data.transfer.model.TransferTicket.Status;

public class TestGetMetadata {

	public static void main(String[] args) throws UnsupportedStorageException, DataStoreException, IOException, NoSuchAlgorithmException {
		
		File temp=File.createTempFile("temp", ".temp");
		File original=new File("/home/fabio/Downloads/oscar_vel2011_180.nc");
		File copied=File.createTempFile("copied", ".nc");
		transferStream(new FileInputStream(original),new FileOutputStream(temp));
		System.out.println("copied. Moving..");	
		
		System.out.println("Checksum original : "+getChecksum(original));
		System.out.println("Checksum temp : "+getChecksum(temp));
		
		
		
		Files.move(temp.toPath(), copied.toPath(),StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);
		if(copied.length()<original.length()) throw new RuntimeException("Different size after moving");
		System.out.println(SisPlugin.getMetaFromFile(copied));

	}


	
	private static String getChecksum(File datafile) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		 FileInputStream fis = new FileInputStream(datafile);
		    byte[] dataBytes = new byte[1024];

		    int nread = 0;

		    while ((nread = fis.read(dataBytes)) != -1) {
		      md.update(dataBytes, 0, nread);
		    };

		    byte[] mdbytes = md.digest();

		    //convert the byte to hex format
		    StringBuffer sb = new StringBuffer("");
		    for (int i = 0; i < mdbytes.length; i++) {
		    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		    }

		return sb.toString();
	}
	

	private static void transferStream(InputStream in, OutputStream out){

		long receivedTotal=0l;

		try{
			byte[] internalBuf=new byte[1024];
			int received=0;
			while ((received=in.read(internalBuf))!=-1){
				out.write(internalBuf,0,received);
				receivedTotal+=received;
								            
			}
			out.flush();
		}catch(IOException e){			
			throw new RuntimeException("Unable to read from source.");
		}		
	}
	
}
