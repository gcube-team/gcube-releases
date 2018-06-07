package org.gcube.data.analysis.wps.repository;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AlgorithmUpdater {

	private static final Logger log = LoggerFactory.getLogger(AlgorithmUpdater.class);

	private String algorithmDirectory;

	private boolean mustUpdate = false;

	private WatcherThread watcherThread = null;

	public AlgorithmUpdater(String algorithmDirectory) {
		super();
		this.algorithmDirectory = algorithmDirectory;
	}

	protected synchronized boolean mustUpdate(){
		return mustUpdate;
	}

	protected synchronized void reset(){
		mustUpdate = false;
	}

	public boolean isStarted(){
		return watcherThread!=null;
	}

	protected void init(){
		watcherThread = new WatcherThread(Thread.currentThread().getContextClassLoader(), algorithmDirectory);
	}

	protected void startWhatcher(){
		watcherThread.start();
	}


	public ClassLoader getLoader() {
		return this.watcherThread.getLoader();
	}


	private class WatcherThread extends Thread {

		/*private Map<String, Long> justCreated = new WeakHashMap<String, Long>();
		private static final long ENTRY_MAX_TIME = 300000;*/
		private WatchService watcher;
		private ClassLoader loader;
		private ClassLoader parentLoader;
		private Path dir;
		private List<String> installedURLS;

		public WatcherThread(ClassLoader parentLoader, String algorithmDirectory) {
			super();
			try {
				log.debug("Watcher Thread created");
				watcher = FileSystems.getDefault().newWatchService();
				this.parentLoader = parentLoader;
				log.debug("parent class loader is {}", parentLoader.getClass().getSimpleName());
				dir = Paths.get(algorithmDirectory);
				installedURLS = updateClassLoader();
				dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE );
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}


		protected ClassLoader getLoader() {
			return loader;
		}




		@SuppressWarnings("unchecked")
		public void run(){
			for (;;) {
				log.info("direcotry watcher is running");	
				// wait for key to be signaled
				WatchKey key;
				try {
					key = watcher.take();
				} catch (InterruptedException x) {
					log.error("DIRECTORY WATCHER IS INTERRUPTED",x);
					return;
				}

				for (WatchEvent<?> event: key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();

					// This key is registered only
					// for ENTRY_CREATE events,
					// but an OVERFLOW event can
					// occur regardless if events
					// are lost or discarded.
					if (kind == OVERFLOW) {
						continue;
					}

					// The filename is the
					// context of the event.
					WatchEvent<Path> ev = (WatchEvent<Path>)event;
					Path filename = ev.context();

					log.trace("new event thrown for directory watcher with filename {} and kind {}", filename, kind);

					if (filename.getFileName().toString().endsWith("_interface.jar") ){
						try{
							if (installedURLS.contains(filename.getFileName().toString())){
								log.debug("{} an already installed algorithm",(kind==ENTRY_CREATE?"modifying":"removing"));
								installedURLS = updateClassLoader();
							} else if (kind==ENTRY_CREATE){
								log.debug("installing new algorithm");
								final Class<URLClassLoader> sysclass = URLClassLoader.class;
								// TODO some kind of a hack. Need to invent better solution.
								final Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
								method.setAccessible(true);
								URL realjarURL = null;
								try{
									String realJarName =filename.getFileName().toString().replaceFirst("_interface", "");
									log.debug("real jar name is {} ",realJarName);
									if (dir.resolve(realJarName).toFile().exists()){
										realjarURL = dir.resolve(realJarName).toFile().toURI().toURL();
										log.debug("real jar url  is {} ",realjarURL);
										method.invoke(loader, new URL[] {realjarURL});
									}
								}catch(Throwable ipe){
									log.warn("only {} have been found",filename, ipe);
								}
								method.invoke(loader, new URL[] {dir.resolve(filename).toFile().toURI().toURL() });

								log.info("filename added in loader {}",filename, loader.getClass().getName());
								installedURLS.add(filename.getFileName().toString());
							}
							mustUpdate = true;
						}catch(Exception e){
							log.error("filename {} cannot be added to classpath",filename,e);
						}

					} else log.info("filename {} is not an algorithm interface",filename);	

				}

				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		}

		private List<String> updateClassLoader(){
			log.debug("getting the stream from directoy {}",dir.getFileName());
			List<URL> urls = new ArrayList<URL>();
			List<String> toReturn = new ArrayList<String>(urls.size());

			DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
				public boolean accept(Path file) throws IOException {
					return (file.getFileName().toString().endsWith(".jar"));
				}
			}; 

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter )){
				for (Path file: stream) {
					log.debug("loading url {}",file.getFileName());
					urls.add(file.toUri().toURL());
					toReturn.add(file.getFileName().toString());
				}
			} catch (IOException | DirectoryIteratorException x) {
				log.error("error reading config dir",x);
			}				
			this.loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), parentLoader);
			log.debug("loader object is {}", loader);
			return toReturn;
		}
	}



	protected void shutdown(){
		if (isStarted()){
			//TODO : kill the watcherThread
			watcherThread = null;
		}
	}
}
