package gr.cite.geoanalytics.functions.output.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileWriter implements FileWriterI {

	
	private static Logger logger = LoggerFactory.getLogger(LocalFileWriter.class);
	
	/**
	 *  <font color="red" size="6"> Please don't forget to close the stream! </font>
	 */
	@Override
	public OutputStream getStreamOutputForPath(String fullDestFilePath) throws IOException {
		return new FileOutputStream(new File(fullDestFilePath));
	}

	
	@Override
	public void writeFile(File fileToWrite, String fullDestFilePath) throws IOException {
		Files.copy(Paths.get(fileToWrite.getAbsolutePath()), Paths.get(fullDestFilePath), StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public void writeBytesAtPathIfNotExist(String fullDestFilePath, byte[] data) throws IOException {
		Files.write(Paths.get(fullDestFilePath), data, StandardOpenOption.CREATE_NEW);
	}

	@Override
	public void writeBytesAtPath(String fullDestFilePath, byte[] data) throws IOException {
		Files.write(Paths.get(fullDestFilePath), data, StandardOpenOption.CREATE);
	}

	@Override
	public void close() {
		// nothing to be done in this implementation
	}
	
	
	
	
}
