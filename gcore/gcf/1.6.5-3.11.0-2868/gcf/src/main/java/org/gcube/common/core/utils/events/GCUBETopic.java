package org.gcube.common.core.utils.events;

/**
 * A tagging interface for topics of {@link GCUBEEvent}s handled by {@link GCUBEProducer}s and 
 * consumed by {@link org.gcube.common.core.utils.events.GCUBEConsumer}s.
 * 
 * <p>The interface may be implemented in different ways, two of which are worth
 * illustrating. The simplest one is with a Java <code>enum</code>:
 * 
 * <p><code>
 * enum MyTopic implements GCUBETopic{TOPIC1,TOPIC2;...};
 * </code>
 * 
 * <p>With an enum-based model, {@link GCUBEProducer}s and {@link GCUBEEvent}s
 *  cannot be constrained to, respectively, handle or be about only individual topics. 
 * Furthermore, the model does not support the dynamic 
 * definition of topics (though the lack of a static documentation for dynamic topics 
 * restricts their applicability). 
 * 
 * <p> A more general model maps instead different topics onto different and potentially empty classes, all derived from an abstract base 
 * class,e.g:
 * 
 * <p><code>
 * abstract class MyTopic implements GCUBETopic{};
 * <br>class Topic1 extends MyTopic{}
 * <br>class Topic2 extends MyTopic{}
 * <br>...
 * </code>
 * 
 * <p> The abstract root of this typically shallow hierarchy may then be used to constrain {@link GCUBEProducer}s
 * to operate against all the derived classes/topics. Equally, {@link GCUBEProducer}s may operate over
 * individual topics. New topics can be created dynamically through anonymous inner classes (but
 * see note above). In this class-based model of topics, however, any two objects 
 * of any given class of the hierarchy model the same topic and thus {@link GCUBEProducer}s expect them to be equated
 * with suitable implementations of {@link java.lang.Object#equals(java.lang.Object)} and {@link java.lang.Object#hashCode()}, 
 * e.g.: 
 * 
 * <p><code>
 * abstract class MyTopic implements GCUBETopic{
 * <br>&nbsp;&nbsp;public int hashCode() {
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;return this.getClass().hashCode();
 * <br>&nbsp;&nbsp;&nbsp;}
 * <br>&nbsp;&nbsp;public boolean equals(Object obj) {
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;return (obj.getClass() == this.getClass());
 * <br>&nbsp;&nbsp;}
 * <br>};
 * </code> 
 * 
 * 
 * 
 *  @author Fabio Simeoni (University of Strathclyde)
 *
 * @see GCUBEEvent
 * @see GCUBEProducer
 * @see org.gcube.common.core.utils.events.GCUBEConsumer
 */
public interface GCUBETopic {}
