package org.gcube.common.core.utils.handlers.events;

import org.gcube.common.core.utils.events.GCUBETopic;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.events.Event.LifetimeEvent;

/** 
 * Partial implementation of {@link GCUBETopic} for topics associated with and handled by {@link GCUBEHandler GCUBEHandlers}.
 * <p>
 * For convenience of its subclasses, {@link Topic} redefines {@link #equals(Object)} and {@link #hashCode()} to equate different instances of
 * the same subclass (cf. {@link GCUBETopic}). Alternatively, subclasses can be defined as singleton classes, as exemplified by {@link LifetimeTopic}. 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 * */
public abstract class Topic implements GCUBETopic {
	/**{@inheritDoc}*/public boolean equals(Object o) {return this.getClass().equals(o.getClass());}
	/**{@inheritDoc}*/public int hashCode() {return this.getClass().hashCode();}
	
	/**
	 * Specialises {@link Topic} for {@link LifetimeEvent LifetimeEvents}.
	 * <p>
	 * {@link LifetimeTopic} is a singleton class (cf. {@link #INSTANCE}).
	 */
	public static class LifetimeTopic extends Topic {
		/**Convenience singleton instance for pre-defined events.*/public static final LifetimeTopic INSTANCE = new LifetimeTopic();
		protected LifetimeTopic(){};
	};
}