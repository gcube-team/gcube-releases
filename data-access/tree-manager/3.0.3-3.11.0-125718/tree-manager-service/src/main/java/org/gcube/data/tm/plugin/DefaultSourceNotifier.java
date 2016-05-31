package org.gcube.data.tm.plugin;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.gcube.data.tmf.api.SourceConsumer;
import org.gcube.data.tmf.api.SourceEvent;
import org.gcube.data.tmf.api.SourceLifecycle;
import org.gcube.data.tmf.api.SourceNotifier;
import org.gcube.data.tmf.api.SourceReader;
import org.gcube.data.tmf.api.SourceWriter;

/**
 * A {@link SourceNotifier} based on {@link GCUBEProducer}s and {@link GCUBEConsumer}s.
 * 
 * @author Fabio Simeoni
 *
 */
@SuppressWarnings("all")
public class DefaultSourceNotifier implements SourceNotifier {

	private static final long serialVersionUID = 1L;
	
	transient GCUBEProducer<SourceEventAdapter,Void> producer;
	
	public DefaultSourceNotifier() {
		setProducer();
	}
	
	private void setProducer() {
		producer=new GCUBEProducer<SourceEventAdapter, Void>();
	}
	
	private static class ConsumerAdapter implements GCUBEConsumer<SourceEventAdapter,Void> {
		
		private SourceConsumer consumer;
		
		ConsumerAdapter(SourceConsumer consumer) {
			this.consumer=consumer;
		}
		
		public <T1 extends SourceEventAdapter, P1 extends Void> void onEvent(GCUBEEvent<T1,P1> ... events) {
			for (GCUBEEvent<T1,P1> event : events)
				consumer.onEvent(event.getTopic().inner());
		};
	}
	
	@Override
	public void subscribe(SourceConsumer consumer, SourceEvent ... topics) {
		List<SourceEventAdapter> events = new ArrayList<SourceEventAdapter>();
		for (SourceEvent topic : topics)
			events.add(new SourceEventAdapter(topic));
		producer.subscribe(new ConsumerAdapter(consumer), events.toArray(new SourceEventAdapter[0]));
	};
	
	@Override
	public void notify(SourceEvent topic) {
		producer.notify(new SourceEventAdapter(topic),new GCUBEEvent<SourceEventAdapter,Void>());
	}
	
	/**
	 * @serialData the {@link SourceLifecycle}, the {@link SourceReader} and the {@link SourceWriter}.
	 */
	 private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	 }
	
    //invoked upon deserialisation, resets non-serializable defaults
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		 
		 in.defaultReadObject();
		 
		 setProducer();
		 
	 }
}
