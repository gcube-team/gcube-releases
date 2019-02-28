package org.gcube.spatial.data.sdi.engine.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.TemporaryPersistence;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class TemporaryPersistenceImpl implements TemporaryPersistence {

	private final static String UPLOADING_FILE_SUFFIX=".part";

	private final static FileFilter TO_CHECK_FILES_FILTER=new FileFilter() {

		@Override
		public boolean accept(File pathname) {
			return !pathname.isDirectory()&&!pathname.getName().endsWith(UPLOADING_FILE_SUFFIX);
		}
	};


	// *************** CHECKER THREAD

	@AllArgsConstructor
	private static class CleanUpThread implements Runnable{
		private Long TTL;
		private File persistenceLocation;
		private FileFilter toCheckFiles;


		@Override
		public void run() {
			try{
				log.debug("Executing cleanup..");
				long count=0l;
				for(File found:persistenceLocation.listFiles(toCheckFiles))
					if(found.lastModified()-System.currentTimeMillis()>=TTL){
						try{
							Files.delete(found.toPath());
						}catch(Throwable t){
							log.warn("Unable to delete {} ",found.getAbsolutePath(),t);
						}
					}				
				log.debug("Cleaned up {} files.",count);
			}catch(Throwable t){
				log.error("Unexpected error.",t);
			}
		}
	}


	//	private static TemporaryPersistenceImpl singleton=null;

	// *************** INSTANCE LOGIC

	private File persistenceLocation=null;

	private ScheduledExecutorService service=null;



	@Override
	@PostConstruct
	public void init() {
		try {
			persistenceLocation=Files.createTempDirectory("SDI").toFile();
			System.out.println("************************************** TEMPORARY PERSISTENCE INIT **************************");
			System.out.println("SDI-Service - Temporary persistence location is "+persistenceLocation.getAbsolutePath());
			System.out.println("**************************************");

			log.trace("Temporary persistence is "+persistenceLocation.getAbsolutePath());

			// init check thread 
			service = new ScheduledThreadPoolExecutor (1);


			long TTL=Long.parseLong(LocalConfiguration.getProperty(LocalConfiguration.TEMPORARY_PERSISTENCE_TTL, "120000"));
			log.debug("Temp TTL is {} ",TTL);
			long delay=TTL/4;

			service.scheduleWithFixedDelay(new CleanUpThread(TTL, persistenceLocation, TO_CHECK_FILES_FILTER), delay, delay, TimeUnit.MILLISECONDS);
		}catch(Throwable t) {
			throw new RuntimeException("Unable to init persistence ",t);
		}
	}

	@Override
	public File getById(String id) throws FileNotFoundException {
		File toReturn=new File(persistenceLocation,id);
		if(!toReturn.exists()) throw new FileNotFoundException();
		return toReturn;
	}

	@Override
	public String store(InputStream is) throws FileNotFoundException, IOException {
		String partUUID=getUUID()+".part";
		log.debug("Storing file "+partUUID);
		File created=transferStream(is, new File(persistenceLocation,partUUID));
		String toReturn=created.getName().substring(0, created.getName().lastIndexOf(".")-1);
		created.renameTo(new File(persistenceLocation,toReturn));
		log.debug("Completed. Part renamed to "+toReturn);
		return toReturn;
	}

	@Override
	@PreDestroy
	public void clean(String id){
		try{
			System.out.println("*************************************** TEMPORARY PERSISTENCE PRE DESTROY ******************************");
		Files.delete(Paths.get(persistenceLocation.getAbsolutePath(), id));
		}catch(Throwable t) {
			throw new RuntimeException("Unable to clean up temporary persistence. ",t);
		}
	}

	@Override
	public void shutdown() {
		log.debug("Shutting down persistence..");
		service.shutdownNow();
		log.debug("Clearing persistence folder..");
		for(File f:persistenceLocation.listFiles())
			try{
				if(!f.delete())	f.deleteOnExit();
			}catch(Throwable t){
				log.warn("Exception while clearing persistence.. ",t);
			}
	}

	@Override
	public void update(String id, InputStream is) throws FileNotFoundException, IOException {
		File toUpdate=getById(id);		
		transferStream(is,toUpdate);
	}

	private static File transferStream(InputStream in, File destination) throws FileNotFoundException, IOException{

		FileOutputStream out=null;
		try{
			;
			out=new FileOutputStream(destination,false);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			return destination;
		}finally{
			if(out!=null) IOUtils.closeQuietly(out);
		}
	}

	private static String getUUID(){
		return UUID.randomUUID().toString().replace(" ", "_");
	}
}
