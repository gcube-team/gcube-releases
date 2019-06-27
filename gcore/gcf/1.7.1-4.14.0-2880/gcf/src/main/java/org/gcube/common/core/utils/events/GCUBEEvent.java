package org.gcube.common.core.utils.events;

import java.util.Calendar;

/**
 * A base implementation for events handled by {@link GCUBEProducer}s and consumed by {@link GCUBEConsumer}s.
 * An event is about a {@link GCUBETopic}, carries a payload, has a creation timestamp, and is handled by a producer.
 *  
 * <p>Through type instantiation, events may be constrained to be about {@link GCUBETopic}s and/or 
 * carry given payloads. For example:
 * 
 * <p>a <code>GCUBEEvent&lt;MyTopic,Object&gt; </code> is about a <code>MyTopic</code> but can carry any payload.
 * 
 * <br>a <code>GCUBEEvent&lt;GCUBETopic,MyPayload&gt;</code> is about any {@link GCUBETopic} but carries
 * a <code>MyPayload</code>.
 * 
 * <br>a <code>GCUBEEvent&lt;MyTopic,MyPayload&gt;</code> is about a <code>MyTopic</code> and carries
 * a <code>MyPayload</code>.
 * 
 * <p> Type constraints may be specified when the event is created, e.g.: 
 * 
 * <p><code>GCUBEEvent&lt;MyTopic,MyPayload&gt; myEvent = new GCUBEEvent&lt;MyTopic,MyPayload&gt;();</code>
 * 
 * <p> or else by subclassing, e.g.
 * <p><code>public class MyEvent extends GCUBEEvent&lt;MyTopic,MyPayload&gt; {...}
 * <br>...
 * <br> MyEvent myEvent = new MyEvent();</code> 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 * @see GCUBETopic
 * @see GCUBEProducer
 * @see GCUBEConsumer
 * @param <T> the type of the event topic.
 * @param <P> the type of the event payload.
 */
public class GCUBEEvent<T extends GCUBETopic, P> {

	/** The creation timestamp of the event.*/
	protected final Calendar timeStamp = Calendar.getInstance();
	
	/** The topic of the event. */
	protected T topic;
	
	/** The payload of the event. */
	protected P payload;
	
	/** The producer of the event. */
	protected GCUBEProducer<? super T,? super P> producer;
	
	/**
	 * Sets the topic of the event.
	 * @param topic the topic.
	 */
	protected void setTopic(T topic) {this.topic=topic;}
	
	/**
	 * Returns the topic of the event.
	 * @return the topic.
	 */
	public T getTopic() {return topic;}

	/**
	 * Returns the payload of the event.
	 * @return the payload.
	 */
	public P getPayload() {return payload;}
	
	/**
	 * Sets the payload of the event.
	 * @param payload
	 */
	public void setPayload(P payload) {this.payload = payload;}
	
	/**
	 * Returns the producer which handled the event.
	 * @return the producer.
	 */
	public GCUBEProducer<? super T, ? super P> getProducer() {return producer;}
	
	/**
	 * Sets the producer which handles the event.
	 * @param producer the producer.
	 */
	protected void setProducer(GCUBEProducer<? super T,? super P> producer) {this.producer = producer;}

	/**
	 * Returns the timestamp of the event.
	 * @return the timestamp.
	 */
	public Calendar getTimeStamp() {
		return timeStamp;
	}
	
	/** The 'echo' left by the occurrence of events. */
	private static final int ECHO_IN_SECS = 60;

	/**
	 * Indicates whether the event has expired at a given time (typically the current time).
	 * @param time the time.
	 * @return <code>true</code> if it has, <code>false</code> otherwise.
	 */
	public boolean isExpired(Calendar time) {
		Calendar expirationDate = (Calendar) this.timeStamp.clone();
		expirationDate.add(Calendar.SECOND,ECHO_IN_SECS);
		return expirationDate.before(time);
	}
}
