package gr.cite.geoanalytics.functions.output.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface FileWriterI {

	OutputStream getStreamOutputForPath(String fullDestFilePath) throws IOException;

	void writeFile(File fileToWrite, String fullDestFilePath) throws IOException;

	void writeBytesAtPathIfNotExist(String fullDestFilePath, byte[] data) throws IOException;

	void writeBytesAtPath(String fullDestFilePath, byte[] data) throws IOException;

	void close();
	
	
}
