package org.gcube.common.events;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.gcube.common.events.Observes.Kind;

/**
 * Mediates between producers and observers of events.
 * <p>
 * Producers fire events through the hub ((cf. {@link #fire(Object, String...)})), which routes them to all the
 * observers that have previously subscribed to receive them ({@link #subscribe(Object)})).
 * <p>
 * <b>Producers, Observers, and Events</b>
 * <p>
 * Producers, observers, and events are arbitrary objects. Observers are objects with one or more methods marked with
 * the {@link Observes} annotation and taking events as their only parameter. {@link Observes} methods can have any name
 * and access modifier, and may throw any kind of exception. Any object value may serve as an event. The following
 * example illustrates:
 * <P>
 * 
 * <pre>
 * {@code
 * class Observer {
 * 
 *   {@literal @}Observes
 *   void someMethod(int event) {...}
 * 
 * }
 * 
 *  Hub hub = ....;
 * 
 *  hub.subscribe(new Observer());
 * 
 *  hub.fire(10);
 * 
 * }
 * </pre>
 * 
 * In general, events and {@link Observes} methods <em>match</em> events when the type of events is a subtype of the
 * type of the single parameter of the observer methods. Normal java subtyping rule apply, with the following
 * exceptions:
 * <p>
 * 
 * <ul>
 * <li>observers <em>cannot</em> use primitive types and should use wrapper types instead.
 * <li>parametric types are not directly supported, as Java does not make available type parameters at runtime. Possible solutions are discussed below.
 * </ul>
 * 
 * <b>Qualifiers</b>
 * <p>
 * The type matching algorithm can be refined by qualifying events and event types with one ore more labels that
 * observers declare in {@link Observes} annotations and produce specify when firing events. An example of using
 * <em>qualifiers</em> is the following:
 * 
 * <pre>
 * {@code
 * class Observer {
 * 
 *      {@literal @}Observes({"currency","dollars"}) 
 *      void onDollarPayment(Integer amount) {...}
 *      
 *      {@literal @}Observes({"currency","euro"}) 
 *      void onEuroPayment(Integer amount) {...}
 *      
 *      {@literal @}Observes({"currency"}) 
 *      void onAnyPayment(Integer amount) {...} 
 * }
 * 
 *  Hub hub = ....;
 * 
 *  hub.subscribe(new Observer());
 * 
 *  hub.fire(10, "currency", "dollars");
 * 
 * }
 * </code>
 * </pre>
 * 
 * Here the methods <code>onDollarPayment()</code> and <code>onAnyPayment()</code> receive the event, while the method
 * <code>onEuroPayment()</code> does not. In general, {@link Observes} methods are notified if they specify a subset of
 * the qualifiers associated with events, including no qualifiers at all.
 * 
 * <p>
 * <b>Event Grouping</b>
 * <p>
 * Observers that perform costly operations may wish to process rapid bursts of events at once. {@link Observes} methods
 * may then specify the minimal delay in milliseconds between two successive notifications {cf. @link Observes#every()}.
 * All the events produced within this delay are grouped and delivered in a collection when the delay expires and in the
 * order in which they were produced. Observers process the collections as they see fit (e.g. consume the most recent,
 * merge all the events, aggregate data in the events, etc). For example:
 * 
 * <pre>
 * {@code
 * 
 *   {@literal @}Observes(value="change",every=1000) 
 *   void onPayments(List&lt;Integer> payments) {...}
 * 
 * }
 * </code>
 * </pre>
 * 
 * Any {@link Collection} type can be used for the delivery of the events.
 * 
 * <p>
 * <b>Critical, Safe, and Resilient Consumers</b>
 * <p>
 * Firing events blocks producers until all matching {@link Observes} methods that are marked {@link Kind#critical} have
 * been executed (cf. {@link Observes#kind()}). Critical {@link Observes} methods execute sequentially in an
 * unpredictable order, and any exception they raise is reported to producers. Producers do not block instead for the
 * execution of {@link Observes} methods that are marked {@link Kind#safe} or {@link Kind#resilient}. Safe and resilient
 * {@link Observes} methods execute in parallel and any exception they raise is logged. Finally, the difference between
 * {@link Kind#safe} and {@link Kind#resilient} handlers is that the former execute if and only if there are no critical
 * failures, while the latter execute in all cases.
 * 
 *  * 
 * <p>
 * <b>Parametric Observers and Events</b>
 * <p>
 * 
 * Parametric observers and events can be still be used in either one of two ways:
 * <p>
 * 
 * <ul>
 * <li>Qualifiers can be used to differentiate different instantiations of a parametric type (qualifiers are discussed
 * below). Like with any other use of qualifiers, convenience is traded off for compile-time safety.
 * 
 * <li>Events can be wrapped as instances of the <code>Event</code> class, which is provided to capture type information
 * which is otherwise lost at runtime due to type erasure. This approach is more verbose but also safer.
 * 
 * Consider the following example of event wrapping:
 * 
 * <pre>
 * {@code
 * class Observer {
 * 
 *   {@literal @}Observes
 *   void someMethod(MyType<Integer> event) {...}
 * 
 * }
 * 
 *  Hub hub = ....;
 * 
 *  hub.subscribe(new Observer());
 * 
 *  MyType<Integer> event = ...
 *  
 *  hub.fire(new Event<MyType<Integer>>(event){});
 * 
 * }
 * </pre>
 * 
 * where <code>new Event<MyType>(event){}</code> instantiates an anonymous <code>Event</code> subclass. The idiom is
 * hardly palatable, but it does circumvent the limitation of type erasure in Java. Currently, the follow limitations
 * apply:
 * 
 * <p>
 * 
 * <ul>
 * <li>type variables cannot be used in both events and observers' parameters;
 * <li>wildcards are supported, but only with upper bounds (<code>? extends ...</code>). Lower bounds are not currently supported.
 * </ul>
 * 
 * @see Observes
 * @author Fabio Simeoni
 * 
 */
public interface Hub {

	/**
	 * Subscribes an observer for notification of events of a given type.
	 * <p>
	 * The single parameter of any method of the observer annotated with {@link Observes} identifies the type of the
	 * events observed by the observer.
	 * 
	 * @param observer the observer
	 * @throws IllegalArgumentException if the observer declares no methods annotated with {@link Observes}, or such
	 *             methods do not declare a single parameter
	 * 
	 * @see Observes
	 */
	void subscribe(Object observer);

	/**
	 * Unsubscribes an observer.
	 * 
	 * @param observer the observer
	 * @return <code>true</code> if the observer is found and unsubscribed.
	 */
	boolean unsubscribe(Object observer);

	/**
	 * Fires an event to all observers which have subscribed for them, blocking the client until all the critical
	 * observers have executed.
	 * 
	 * @param event the event
	 * @param qualifiers optional event qualifiers
	 */
	void fire(Object event, String... qualifiers);

	/**
	 * Blocks the caller until an event of a given type occurs.
	 * 
	 * @param eventType the event type
	 * @throws RuntimeException if the wait is interrupted
	 */
	void waitFor(Class<?> eventType);

	/**
	 * Blocks the caller until an event of a given type occurs or a given length of time elapses.
	 * 
	 * @param eventType the event type
	 * @param duration the length of time to wait for
	 * @param unit the time unit of the duration
	 * 
	 * @throws IllegalArgumentException if the inputs are null or the duration is less or equal than zero
	 * @throws RuntimeException if the wait is interrupted
	 */
	void waitFor(Class<?> eventType, long duration, TimeUnit unit);

	/**
	 * Signals that this hub is no longer needed and can release its resources.
	 */
	void stop();

}