/**
 * 
 */
package org.gcube.data.spd.obisplugin.pool;

import java.util.Enumeration;
import java.util.Hashtable;

import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Adapted from http://sourcemaking.com/design_patterns/object_pool/java
 */
public abstract class ObjectPool<T> {
	
	protected static GCUBELog logger = new GCUBELog(ObjectPool.class);
	
	protected long expirationTime;

	protected Hashtable<T, Long> locked, unlocked;
	protected boolean closed = false;
	protected String name;

	public ObjectPool(String name, long expirationTime) {
		this.name = name;
		this.expirationTime = expirationTime;
		locked = new Hashtable<T, Long>();
		unlocked = new Hashtable<T, Long>();
	}

	protected abstract T create();

	protected abstract boolean validate(T o);

	protected abstract void expire(T o);

	public synchronized T checkOut() {
		long now = System.currentTimeMillis();
		T t;
		if (unlocked.size() > 0) {
			Enumeration<T> e = unlocked.keys();
			while (e.hasMoreElements()) {
				t = e.nextElement();
				if ((now - unlocked.get(t)) > expirationTime) {
					// object has expired
					unlocked.remove(t);
					expire(t);
					t = null;
				} else {
					if (validate(t)) {
						unlocked.remove(t);
						locked.put(t, now);
						return t;
					} else {
						// object failed validation
						unlocked.remove(t);
						expire(t);
						t = null;
					}
				}
			}
		}
		
		logger.trace("no objects available, create a new one, status: "+this);
		// no objects available, create a new one
		t = create();
		locked.put(t, now);
		return (t);
	}

	public synchronized void checkIn(T t) {
		locked.remove(t);
		if (!closed) unlocked.put(t, System.currentTimeMillis());
		else expire(t);
		logger.trace("pool status: "+this);
	}
	
	public synchronized void shutdown(boolean force)
	{
		closed = true;
		expireAllUnlocked();
		if (force) expireAllLocked();
	}
	
	public synchronized void expireAllUnlocked()
	{
		Enumeration<T> e = unlocked.keys();
		while (e.hasMoreElements()) {
			T t = e.nextElement();
			unlocked.remove(t);
			expire(t);
		}
	}
	
	public synchronized void expireAllLocked()
	{
		Enumeration<T> e = locked.keys();
		while (e.hasMoreElements()) {
			T t = e.nextElement();
			locked.remove(t);
			expire(t);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ObjectPool [name=");
		builder.append(name);
		builder.append(", expirationTime=");
		builder.append(expirationTime);
		builder.append(", locked=");
		builder.append(locked.size());
		builder.append(", unlocked=");
		builder.append(unlocked.size());
		builder.append(", closed=");
		builder.append(closed);
		builder.append("]");
		return builder.toString();
	}

}
