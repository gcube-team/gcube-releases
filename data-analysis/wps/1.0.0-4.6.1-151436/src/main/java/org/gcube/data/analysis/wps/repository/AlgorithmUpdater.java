package org.gcube.data.analysis.wps.repository;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

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
	
	protected void startWhatcher(){
		watcherThread = new WatcherThread(Thread.currentThread().getContextClassLoader(), algorithmDirectory);
		watcherThread.start();
	}
	
	protected void init(){
		
	}
	
	private class WatcherThread extends Thread {

		private WatchService watcher;
		private ClassLoader loader;
		private Path dir;
		
		public WatcherThread(ClassLoader loader, String algorithmDirectory) {
			super();
			try {
				watcher = FileSystems.getDefault().newWatchService();
				this.loader = loader;
				//TODO: change with something from configuration
				dir = Paths.get(algorithmDirectory);
				dir.register(watcher, ENTRY_CREATE);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@SuppressWarnings("unchecked")
		public void run(){
			for (;;) {

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
					
					if (filename.toString().endsWith(".jar")){
						log.debug("found filename {} ",filename.toString());
						try{
							final Class<URLClassLoader> sysclass = URLClassLoader.class;
							// TODO some kind of a hack. Need to invent better solution.
							final Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
							method.setAccessible(true);
							method.invoke(loader, new URL[] { dir.resolve(filename).toFile().toURI().toURL() });
							log.info("filename added is {} in loader {}",filename, loader.getClass().getName());
							mustUpdate = true;
						}catch(Exception e){
							log.error("filename {} cannot be added to classpath",e,filename);
						}
					} else log.info("filename {} is not a jar",filename);	

				}

				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		}
	}
	
	protected void shutdown(){
		if (isStarted()){
			//TODO : kill the watcherThread
			watcherThread = null;
		}
	}
}
