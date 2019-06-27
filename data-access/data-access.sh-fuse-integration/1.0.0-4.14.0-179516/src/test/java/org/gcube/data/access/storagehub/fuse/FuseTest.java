package org.gcube.data.access.storagehub.fuse;

import java.nio.file.Paths;

import org.gcube.data.access.storagehub.fs.StorageHubFS;
import org.junit.Test;

import jnr.ffi.Platform;

public class FuseTest {

	@Test
	public void mount() {
		StorageHubFS memfs = new StorageHubFS("7c26a682-f47b-4e6e-90e0-6d101a4314cd-980114272","/pred4s/preprod/preVRE");
		try {
			String path;
			switch (Platform.getNativePlatform().getOS()) {
			case WINDOWS:
				path = "J:\\";
				break;
			default:
				path = "/home/lucio/javaMount/mnt";
			}
			memfs.mount(Paths.get(path), true, true);
		} finally {
			memfs.umount();
		}
	}
	
}
