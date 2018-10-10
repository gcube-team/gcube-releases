package org.gcube.common.core.utils.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Base implementation for producers of event notifications. A producer notifies {@link GCUBEConsumer}s 
 * which have previously subscribed with it of the occurrence of {@link GCUBEEvent}s about given {@link GCUBETopic}s
 * and carrying given payloads.
 * 
 * <p>Through type instantiation, producers may be constrained to handle only {@link GCUBEEvent}s about given topics and/or 
 * carrying given payloads. For example:
 * 
 * <p>a <code>GCUBEProducer&lt;MyTopic,Object&gt; </code> handles only events 
 * about <code>MyTopic</code>s but carrying any payload.
 * 
 * <br>a <code>GCUBEProducer&lt;GCUBETopic,MyPayload&gt;</code> handles events about any {@link GCUBETopic} as long
 * as they carry <code>MyPayload</code>s.
 *
 * <br>a <code>GCUBEProducer&lt;MyTopic,MyPayload&gt;</code> handles only events about <code>MyTopic</code>s which
 * carry <code>MyPayload</code>s.
 * 
 * <p> Type constraints may be specified when the producer is created, e.g.: 
 * <p><code>GCUBEProducer&lt;MyTopic,MyPayload&gt; myProducer = new GCUBEProducer&lt;MyTopic,MyPayload&gt;();</code>
 * 
 * <p> or else by subclassing, e.g.
 * <p><code>public class MyProducer extends GCUBEProducer&lt;MyTopic,MyPayload&gt; {...}
 * <br>...
 * <br> MyProducer myProducer = new MyProducer();</code> 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 * @see GCUBEEvent
 * @see GCUBETopic
 * @see GCUBEConsumer
 * @param <TOPIC> the type of the event topics.
 * @param <PAYLOAD> the type of the event payload.
 */

public class GCUBEProducer<TOPIC extends GCUBETopic,PAYLOAD> {
	 
	/** Object logger. */
	protected GCUBELog logger = new GCUBELog(this);
	/**Subscribed consumers, indexed by topic.*/
	protected Map<TOPIC,List<GCUBEConsumer<TOPIC,PAYLOAD>>> consumers = new HashMap<TOPIC,List<GCUBEConsumer<TOPIC,PAYLOAD>>>();
	/**Notified events, indexed by topic.*/
	protected Map<TOPIC,List<GCUBEEvent<? extends TOPIC,? extends PAYLOAD>>> events = new HashMap<TOPIC,List<GCUBEEvent<? extends TOPIC,? extends PAYLOAD>>>();
		 
	/** The class extent. */
	protected static List<GCUBEProducer<?,?>> classExtent = new ArrayList<GCUBEProducer<?,?>>();
	 
	 /**
	  * Centralised sweeper for expired events.
	  * @author Fabio Simeoni (University of Strathclyde)
	  *
	  */
	 private static class EventSweeper extends GCUBEScheduledHandler<Object> {
		
		 //default sweeper delay
		private static final int SWEEPER_DELAY = 60;

		/** Creates a new instance. */ 
		protected EventSweeper() {
			super(SWEEPER_DELAY,GCUBEScheduledHandler.Mode.LAZY,new GCUBEHandler<Object>() {
				/** {@inheritDoc */ public void run() throws Exception {
				int totalRemoved=0;
				synchronized (EventSweeper.class) {
					for (GCUBEProducer<?,?> producer : classExtent)  totalRemoved += producer.sweepEvents();
				}
				if (totalRemoved>0) logger.debug("sweeped "+totalRemoved+" expired events");
			}});
			this.getScheduled().setName(this.getName());
			try {this.run();}catch(Exception e){throw new RuntimeException("could not start event sweeper",e);}
		}
			
		 /** {@inheritDoc} */
		 @Override protected boolean repeat(Exception exception, int exceptionCount) {
			if (exception!=null) logger.warn("Event sweeper failed ("+exceptionCount+")");
			return true;
		}
			
	 }
	
	 /** The sweeper scheduler */
	protected static EventSweeper sweeper;
		
	static {
		//avoid class init problems (as a handler, sweeper is also a producer)
		new Thread() {public void run() {sweeper = new EventSweeper();}}.start();
	}
	
	/** Creates a new instance. */ 
	public GCUBEProducer() {classExtent.add(this);}	
	 
	 /**
	  * Used internally to return all the consumers subscribed for a given topic.
	  * @param topic the topic.
	  * @return the consumers.
	  */
	 protected List<GCUBEConsumer<TOPIC,PAYLOAD>> getConsumers(TOPIC topic) {
		 if (this.consumers.get(topic)==null) this.consumers.put(topic, new ArrayList<GCUBEConsumer<TOPIC,PAYLOAD>>());
		 return this.consumers.get(topic);
	 }
	 
	 /**
	  * Used internally to return all the events recorded for a given topic.
	  * @param topic the topic.
	  * @return the events.
	  */
	 protected List<GCUBEEvent<? extends TOPIC,? extends PAYLOAD>> getEvents(TOPIC topic) {
		 if (this.events.get(topic)==null) this.events.put(topic, new ArrayList<GCUBEEvent<? extends TOPIC,? extends PAYLOAD>>());
		 return this.events.get(topic);
	 }
		 
	 /**
	  * Subscribes a consumer to events about one or more topics.
	  * @param TOPIC the topic type.
	  * @param PAYLOAD the event payload type.
	  * @param consumer the consumer.
	  * @param topics the topics.
	  * @throws IllegalArgumentException if invoked with no topics.
	  */
	 public synchronized void subscribe(GCUBEConsumer<TOPIC,PAYLOAD> consumer,TOPIC ... topics) throws IllegalArgumentException  {
		 if (topics==null || topics.length==0) throw new IllegalArgumentException("no topics specified");//one or more please
		 for (TOPIC topic : topics) {
			 List<GCUBEConsumer<TOPIC,PAYLOAD>> consumers = this.getConsumers(topic);
			 if (consumers.contains(consumer)) return;
			 
			 consumers.add(consumer);
	
 			 List<GCUBEEvent<? extends TOPIC,? extends PAYLOAD>> events = this.getEvents(topic);
 			 
 			 if (events.size()>0) 
 				  this.notifyConsumer(consumer,events.toArray((GCUBEEvent<TOPIC,PAYLOAD>[]) new GCUBEEvent[events.size()]));//trick the compiler to let his pass
		 }
	 }
		 
	 /**
	  * Unsubscribes a consumer from events about one or more topics.
	  * @param <TOPIC> the topic type.
	  * @param <PAYLOAD> the event payload type.
	  * @param consumer the consumer.
	  * @param topics the topics.
	  * @throws IllegalArgumentException if invoked with null or empty inputs.
	  */
	 public synchronized void unsubscribe(GCUBEConsumer<TOPIC,PAYLOAD> consumer,TOPIC ... topics) throws IllegalArgumentException {
		 if (topics==null || topics.length==0) throw new IllegalArgumentException("no topics specified");//one or more please
		 for (TOPIC topic : topics) {
			 List<GCUBEConsumer<TOPIC,PAYLOAD>> consumers = this.getConsumers(topic);
			 consumers.remove(consumer);
		 }
	 }
	 
	 /**
	  * Notifies asynchronously a given consumer of the occurrence of one or more events.
	  * @param <TOPIC> the topic type.
	  * @param <PAYLOAD> the event payload type.
	  * @param consumer the consumer.
	  * @param events the events.
	  */
	 protected <T1 extends TOPIC, P1 extends PAYLOAD> void notifyConsumer(final GCUBEConsumer<TOPIC,PAYLOAD> consumer, final GCUBEEvent<T1,P1> ... events) {
		 if (consumer instanceof GCUBESynchronousConsumer) consumer.onEvent(events);
		 else 
			 new Thread(){
				 public void run() {
					 Thread.currentThread().setName(consumer.getClass().isAnonymousClass()?
							 consumer.getClass().getSuperclass().getSimpleName()+"$<anon>":
							 consumer.getClass().getSimpleName());
					 consumer.onEvent(events);
				 }
			 }.start();
	 }
	 
	 /**
	  * Notifies consumers of the occurrence of one or more events about a topic for which they subscribed.
	  * 
	  * @param <T1> the topic type.
	  * @param <P1> the payload type.
	  * @param topic the topic.
	  * @param events the events.
	  * @throws IllegalArgumentException if invoked with null or empty inputs.
	  */
	 public synchronized <T1 extends TOPIC, P1 extends PAYLOAD> void notify(T1 topic, GCUBEEvent<T1,P1> ... events) throws IllegalArgumentException {
		 
		 if (topic==null || events==null || events.length==0) throw new IllegalArgumentException("no topic or events");
		 
		 List<GCUBEEvent<? extends TOPIC,? extends PAYLOAD>> eventList = this.getEvents(topic);
		 	 
		//sets events fields
		 for (GCUBEEvent<T1,P1> event : events) {
			 event.setProducer(this);
			 event.setTopic(topic);
			 eventList.add(event);
		 }
		
		 for (GCUBEConsumer<TOPIC,PAYLOAD> consumer : this.getConsumers(topic)) this.notifyConsumer(consumer,events);
	 }
	 
	 /** Sweeps expires events. */
	 private synchronized long sweepEvents() {
		 Calendar now =Calendar.getInstance();
		 long totalRemoved=0;
		 List<GCUBEEvent<? extends TOPIC,? extends PAYLOAD>> toRemove = new ArrayList<GCUBEEvent<? extends TOPIC,? extends PAYLOAD>>();
		 for (TOPIC topic : this.events.keySet()) {
			 for (GCUBEEvent<? extends TOPIC,? extends PAYLOAD> event : events.get(topic))
				if (event.isExpired(now)) toRemove.add(event);
			 if (toRemove.size()>0) {
				 events.get(topic).removeAll(toRemove);
				 totalRemoved = totalRemoved + toRemove.size();
				 toRemove.clear();
			 }
		 }
		 return totalRemoved;
	 }
		
}