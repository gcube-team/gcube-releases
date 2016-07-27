package org.gcube.common.core.utils.events;

/**
 * Interface for consumers of notification of {@link GCUBEEvent}s about {@link GCUBETopic}
 * A consumer receives notifications from a {@link GCUBEProducer}s with which it has previously subscribed. 
 * Through type instantiation, consumers may be constrained to handle only {@link GCUBEEvent}s about given topics and/or 
 * carrying given payloads. For example:
 * 
 * <p><code>a GCUBEConsumer&lt;MyTopic,Object&gt;</code> consumes only events 
 * about <code>MyTopic</code>s but carrying any payload.
 * 
 * <br>a <code>GCUBEConsumer&lt;GCUBETopic,MyPayload&gt;</code> consumer events about any {@link GCUBETopic} as long
 * as they carry <code>MyPayload</code>s.
 *
 * <br>a <code>GCUBEConsumer&lt;MyTopic,MyPayload&gt;</code> consumer only events about <code>MyTopic</code>s which
 * carry <code>MyPayload</code>s.
 
 * <p> Type constraints may be specified when the consumer interface is implemented, either explicitly:
 
 * <p><code>public class MyConsumer implements GCUBEConsumer&lt;MyTopic,MyPayload&gt; {...}
 * <br>...
 * <br> MyConsumer myConsumer = new MyConsumer();</code>
 * 
 * <p> or else implicitly, using anonymous inner classes, e.g.
 * 
 * <p><code>GCUBEConsumer&lt;MyTopic,MyPayload&gt; myConsumer = new GCUBEConsumer&lt;MyTopic,MyPayload&gt;(){...};</code>
 * 
 * <p> A consumer which consumes events associated with different topics and/or payloads performs
 * a type-based analysis on the events in input to {@link #onEvent(GCUBEEvent[])}. For example, 
 * a consumer <code>MyConsumer</code> which implements the <code>GCUBEConsumer&lt;MyTopic,MyPayload&gt;</code>
 * interface would implement {@link #onEvent(GCUBEEvent[])} along the following lines:
 * 
 * <p> public <T1 extends MyTopic, P1 extends MyPayload> void onEvent(GCUBEEvent<T1,P1> ... event) {
 * <br>&nbsp;&nbsp;for (GCUBEEvent<T1,P1> event : events) {
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;if (event instanceof MyEvent) {...}
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;else if (event instanceof MyOtherEvent) {...}
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;...
 * <br>&nbsp;&nbsp;}
 * <br>}
 * </code>
 * 
 * <p> For convenience, the type-based analysis of <code>MyConsumer</code> may dispatch to
 * event-specific methods, e.g.:
 * 
 * <p> public <T1 extends MyTopic, P1 extends MyPayload> void onEvent(GCUBEEvent<T1,P1> ... event) {
 * <br>&nbsp;&nbsp;for (GCUBEEvent<T1,P1> event : events) {
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;if (event instanceof MyEvent) {this.onEvent((MyEvent) event);}
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;else if (event instanceof MyOtherEvent) {this.onEvent((MyOtherEvent) event);}
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;...
 * <p> public void onEvent(MyEvent event){...}
 * <br> public void onEvent(MyOtherEvent event){...}
 * <br>&nbsp;&nbsp;}
 * <br>}
 * </code>
 * 
 * <p>This approach suits complex type-based analyses, or else circumstances in which a concrete implementations
 * of the consumers is best delegated to subclasses. In the latter case, the event-specific methods 
 * (and thus <code>MyProducer</code>) may be declared as <code>abstract</code> method. 
 * 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 * @see GCUBEEvent
 * @see GCUBETopic
 * @see GCUBEProducer
 * @param <TOPIC> the type of the topics associated with the events managed by the producer.
 * @param <PAYLOAD> the type of the payload of the events managed by the producer.
 */
public interface GCUBEConsumer<TOPIC extends GCUBETopic, PAYLOAD> {

	/**
	 * Notifies the consumer of one of more events about topics for which the consumer
	 * has previously subscribed with a {@link GCUBEProducer}  
	 * @param <T1> the topic type.
	 * @param <P1> the payload type.
	 * @param event the events.
	 */
	public <T1 extends TOPIC, P1 extends PAYLOAD> void onEvent(GCUBEEvent<T1,P1> ... event);
}
