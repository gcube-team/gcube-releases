package org.gcube.portal.social.networking.caches;


/**
 * A generic cache bean entry with an object and a insert-time value
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CacheBean {

	private Long timestamp;
	private Object object;

	public CacheBean(Long timestamp, Object object) {
		super();
		this.timestamp = timestamp;
		this.object = object;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "CacheBean [timestamp=" + timestamp + ", object=" + object + "]";
	}

}
