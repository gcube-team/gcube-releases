package org.gcube.data.tm.testplugin;

import static org.gcube.data.tm.TestUtils.serializableMock;
import static org.mockito.Mockito.when;

import java.util.*;

import org.gcube.data.tm.plugin.*;
import org.gcube.data.tmf.api.*;

public class SourceBuilder {

	private String name = "testsource-"+UUID.randomUUID().toString();

	private SourceLifecycle lifecycle;
	private SourceReader reader;
	private SourceWriter writer;
	
	public Source build() {
		
		Source source = serializableMock(Source.class);
		
		if (lifecycle==null)
			lifecycle = serializableMock(SourceLifecycle.class);
		
		when(source.id()).thenReturn(name);
		when(source.lifecycle()).thenReturn(lifecycle);
		when(source.reader()).thenReturn(reader);
		when(source.writer()).thenReturn(writer);
		when(source.notifier()).thenReturn(new DefaultSourceNotifier());
		when(source.environment()).thenReturn(new PluginEnvironment());
		return source;
	}
	
	public SourceBuilder called(String name) {
		this.name=name;
		return this;
	}
	
	public SourceBuilder with(SourceLifecycle lifecycle) {
		this.lifecycle=lifecycle;
		return this;
	}
	
	public SourceBuilder with(SourceReader reader) {
		this.reader=reader;
		return this;
	}
	
	public SourceBuilder with(SourceWriter writer) {
		this.writer=writer;
		return this;
	}
}
