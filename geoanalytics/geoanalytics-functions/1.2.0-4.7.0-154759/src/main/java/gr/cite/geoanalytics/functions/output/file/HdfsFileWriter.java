package gr.cite.geoanalytics.functions.output.file;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HdfsFileWriter implements FileWriterI {

	private FileSystem hdfs;
	
	
	private static Logger logger = LoggerFactory.getLogger(HdfsFileWriter.class);
	
	/**
	 * @param hdfsHost is the namenode host of the hdfs. e.g: hdfs://node1.madgik.di.uoa.gr:50050
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public HdfsFileWriter(String hdfsHost) throws IOException, URISyntaxException{
		this(hdfsHost, System.getenv("HADOOP_USER_NAME"));
	}
	
	/**
	 * @param hdfsHost is the namenode host of the hdfs. e.g: hdfs://node1.madgik.di.uoa.gr:50050
	 * @param asUser is the username for the hadoop. e.g: sys-hcv
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public HdfsFileWriter(String hdfsHost, String asUser) throws IOException, URISyntaxException {
		if(asUser==null || asUser.isEmpty()){
			logger.error("Environment variable 'HADOOP_USER_NAME' is not set... Cannot access the HDFS to write");
			throw new IOException("Environment variable 'HADOOP_USER_NAME' is not set... Cannot access the HDFS to write");
		}
		System.setProperty("HADOOP_USER_NAME", asUser); //this is used by the underlying hadoop library
		Configuration configuration = new Configuration();
		configuration.set("dfs.replication", "1");
		hdfs = FileSystem.get( new URI(hdfsHost), configuration );
	}
	
	
	/**
	 * Note that it overwrites the destination file, if present
	 * 
	 * @param fullDestFilePath the full file path to write on hdfs. It should start with a / , as in unix. i.e. /Myfolder/file1.png
	 * @throws IOException
	 */
	@Override
	public void writeFile(File fileToWrite, String fullDestFilePath) throws IOException{
		Path path = new Path(fullDestFilePath);
		if (hdfs.exists(path))
			hdfs.delete(path, false);
		FSDataOutputStream out = hdfs.create(path);
		InputStream in = new BufferedInputStream(new FileInputStream(fileToWrite));
		byte[] b = new byte[1024];
		int numBytes = 0;
		while ((numBytes = in.read(b)) > 0)
			out.write(b, 0, numBytes);
		out.hflush();
		in.close();
		out.close();
	}
	
	/**
	 * Note that it overwrites the destination file, if present
	 * 
	 * @param fullDestFilePath gets an output stream for writing on the specified fullDestFilePath
	 * @return
	 * @throws IOException
	 */
	@Override
	public OutputStream getStreamOutputForPath(String fullDestFilePath) throws IOException {
		Path path = new Path(fullDestFilePath);
		if (hdfs.exists(path))
			hdfs.delete(path, false);
		return hdfs.create(path);
	}
	
	@Override
	public void writeBytesAtPath(String fullDestFilePath, byte[] data) throws IOException {
		Path path = new Path(fullDestFilePath);
		FSDataOutputStream out = hdfs.create(path);
		out.write(data);
		out.hflush();
		out.close();
	}
	
	
	@Override
	public void writeBytesAtPathIfNotExist(String fullDestFilePath, byte[] data) throws IOException {
		Path path = new Path(fullDestFilePath);
		if (hdfs.exists(path))
			return;
		writeBytesAtPath(fullDestFilePath, data);
	}
	
	
	@Override
	public void close(){
		try{
			this.hdfs.close();
		}
		catch(IOException ex){
			logger.debug("Could not close an HDFS writer...");
		}
	}
	
//	public static void main (String [] args) throws IOException, URISyntaxException{
//		HdfsFileWriter fw = new HdfsFileWriter("hdfs://datanode1.cluster2.madgik.di.uoa.gr:50050", "sys-hcv");
//		fw.writeBytesAtPath("/aaa.txt", "mytext".getBytes());
//	}
	
}


