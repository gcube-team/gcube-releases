package org.gcube.spatial.data.sdi.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface TemporaryPersistence {

	public void init() throws IOException;
	public String store(InputStream is) throws FileNotFoundException, IOException;
	public void clean(String id) throws IOException;
	public void update(String id, InputStream is) throws FileNotFoundException, IOException;
	public File getById(String id) throws FileNotFoundException;
	public void shutdown();
}
