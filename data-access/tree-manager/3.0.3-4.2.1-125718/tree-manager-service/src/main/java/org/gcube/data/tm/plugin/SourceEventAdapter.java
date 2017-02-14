package org.gcube.data.tm.plugin;

import org.gcube.common.core.utils.events.GCUBETopic;
import org.gcube.data.tmf.api.SourceEvent;

public class SourceEventAdapter implements GCUBETopic {

	private final SourceEvent event;
	
	SourceEventAdapter(SourceEvent event) {
		this.event=event;
	}
	
	public SourceEvent inner() {
		return event;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceEventAdapter other = (SourceEventAdapter) obj;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		return true;
	}
	
	
}
