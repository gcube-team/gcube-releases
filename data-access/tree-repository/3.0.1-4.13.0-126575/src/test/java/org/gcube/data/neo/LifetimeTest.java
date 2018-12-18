package org.gcube.data.neo;

import static org.junit.Assert.*;

import java.io.File;

import org.gcube.data.TestUtils;
import org.gcube.data.tr.Store;
import org.gcube.data.tr.neo.NeoStore;
import org.junit.Test;

public class LifetimeTest {

	
	@Test
	public void storesStartsSerializesAndStopsCorrectly() throws Exception {
		
		String storeId="id";
		
		Store store = new NeoStore(storeId);
		
		File tempLocation = File.createTempFile("prefix", "suffix").getParentFile();
		
		store.start(tempLocation);
		
		assertNotNull(store.location());
		
		store.stop();
		
		TestUtils.roundtrip(store);
		
		store.delete();
		
		assertFalse(store.location().exists());
	}
}
