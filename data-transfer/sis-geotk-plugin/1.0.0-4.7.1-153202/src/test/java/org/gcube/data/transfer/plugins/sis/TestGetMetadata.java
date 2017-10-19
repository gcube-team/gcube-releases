package org.gcube.data.transfer.plugins.sis;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.UnsupportedStorageException;

public class TestGetMetadata {

	public static void main(String[] args) throws UnsupportedStorageException, MalformedURLException, DataStoreException {
		System.out.println(SisPlugin.getMetaFromFile(new File("/home/fabio/oscar_vel_1992-1992_180.nc")));

	}

}
