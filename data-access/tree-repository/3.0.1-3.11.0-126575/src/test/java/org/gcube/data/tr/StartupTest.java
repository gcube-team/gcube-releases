package org.gcube.data.tr;

import static org.junit.Assert.*;

import java.util.ServiceLoader;

import org.gcube.data.tmf.api.Plugin;
import org.junit.Test;

public class StartupTest {

	
	@Test
	public void pluginCanBeFound() {
		
		ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
		assertTrue(loader.iterator().hasNext());
		assertTrue(loader.iterator().next() instanceof TreeRepository);
	}
}
