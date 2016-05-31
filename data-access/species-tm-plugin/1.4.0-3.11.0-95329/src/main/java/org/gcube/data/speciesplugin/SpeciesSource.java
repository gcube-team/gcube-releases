package org.gcube.data.speciesplugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.namespace.QName;

import org.gcube.data.speciesplugin.store.SpeciesStore;
import org.gcube.data.tmf.api.Property;
import org.gcube.data.tmf.impl.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 *
 */
public class SpeciesSource extends AbstractSource {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(SpeciesSource.class);

	protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	protected Lock readLock = lock.readLock();
	protected Lock writeLock = lock.writeLock();

	private final SpeciesStore store;
	protected final List<Property> properties;

	/**
	 * Creates an instance with a given identifier.
	 * @param id the identifier
	 */
	public SpeciesSource(String id, SpeciesStore store, List<Property> properties) {
		super(id);
		this.store=store;
		this.properties = properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Property> properties() {
		return properties;
	}

	/**
	 * Returns the underlying tree store
	 * @return the store
	 */
	public SpeciesStore store() {
		//FIXME avoid new "read" during swap but not lock it if the store instance is already took
		readLock.lock();
		try {
			return this.store;
		} finally {
			readLock.unlock();
		}
	}


	public File switchStore(File newStoreLocation) throws IOException {
		log.trace("switchStore newStoreLocation: {}", newStoreLocation);
		
		writeLock.lock();
		log.trace("write locked");
		try {
			File oldStoreLocation = store.location();
			log.trace("oldStoreLocation {}",oldStoreLocation);
			
			log.trace("stopping current store");
			store.stop();
			
			// give time to shutdown
			try {
				TimeUnit.MILLISECONDS.sleep(3000);
			} catch (InterruptedException e) {
				log.warn("could not wait for shutdown to complete", e);
			}
			
			log.trace("store stopped");

			//tmp folder for the old store
			File tmpLocation = Utils.createTempDirectory();
			log.trace("tmpLocation for the current store {}",tmpLocation);

			//move old store to the tmp location
			boolean moved = oldStoreLocation.renameTo(tmpLocation);
			log.trace("moved old store to the tmp location. success? {}", moved);

			//move new store to the old store location
			moved = newStoreLocation.renameTo(oldStoreLocation.getParentFile());
			log.trace("moved new store to the old store location. success? {}", moved);

			log.trace("starting store in {}",oldStoreLocation);
			store.start(oldStoreLocation.getParentFile());		
			log.trace("store ready with {} elements",store.cardinality());
			
			return tmpLocation;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Long cardinality() {
		return store.cardinality();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.toString()+"["+store+"]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QName> types() {
		//for simplicity, we use the simple name of the data model as its name
		return Collections.singletonList(new QName("http://org.gcube.data.spd","SPD"));
	}
}
