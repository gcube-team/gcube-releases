package org.gcube.data.tm.testplugin;

import static java.util.Arrays.*;
import static org.gcube.data.tm.TestUtils.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.message.MessageElement;
import org.gcube.data.tm.plugin.PluginManager;
import org.gcube.data.tm.stubs.BindParameters;
import org.gcube.data.tm.stubs.Payload;
import org.gcube.data.tmf.api.Plugin;
import org.gcube.data.tmf.api.Source;
import org.gcube.data.tmf.api.SourceBinder;
import org.gcube.data.tmf.api.SourceReader;
import org.gcube.data.tmf.api.SourceWriter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PluginBuilder {

	private static final String name = "testplugin";
	
	private static final String requestName = "request";
	public static final BindParameters request;
	public static final BindParameters broadcastRequest;
	
	static Matcher<Element> elementMatcher = new BaseMatcher<Element>() {
		@Override
		public boolean matches(Object item) {
			return (item instanceof Element) && (((Element)item).getLocalName().equals(requestName));
		}

		@Override
		public void describeTo(Description description) {}
	};
	
	static {
		try {
		     Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		     Element e = d.createElement(requestName);
		     Payload payload = new Payload();
		     payload.set_any(new MessageElement[]{new MessageElement(e)});
		     request = new BindParameters(false,payload, name);
		     broadcastRequest = new BindParameters(true,payload,name);
		}
		catch(Exception e) {
			throw new RuntimeException();
		}
	}
	
	private SourceBinder binder;
	private List<Source> sources = new ArrayList<Source>();
	
	public static PluginBuilder plugin() {
		return new PluginBuilder();
	}
	
	public static SourceBuilder source() {
		return new SourceBuilder();
	}
	
	public static SourceReader reader() {
		return serializableMock(SourceReader.class);
	}
	
	public static SourceWriter writer() {
		return serializableMock(SourceWriter.class);
	}
	
	
	public PluginBuilder with(SourceBinder binder) {
		this.binder=binder;
		return this;
	}
	
	public PluginBuilder with(Source ... sources) {
		this.sources.addAll(asList(sources));
		return this;
	}
	
	public void install() {
		
		try {
			if (sources.size()==0)
				with(source().with(reader()).with(writer()).build());
		
			if (binder==null)
				with(binder(sources));
		
			new PluginManager().register(plugin(binder));
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@SuppressWarnings("all")
	private SourceBinder binder(List<? extends Source> sources) throws Exception {
		SourceBinder binder = mock(SourceBinder.class);
		when(binder.bind(argThat(elementMatcher))).thenReturn((List)sources);
		return binder;
	}
	
	private Plugin plugin(SourceBinder binder) {
		Plugin plugin = mock(Plugin.class);
		when(plugin.name()).thenReturn(name);
		when(plugin.binder()).thenReturn(binder);
		return plugin;
	}
}
