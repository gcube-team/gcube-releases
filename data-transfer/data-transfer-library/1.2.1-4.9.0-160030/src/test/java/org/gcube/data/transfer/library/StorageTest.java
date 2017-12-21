package org.gcube.data.transfer.library;

import java.io.File;
import java.io.FileNotFoundException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.data.transfer.library.utils.StorageUtils;

public class StorageTest {

	static String scope="/gcube/devsec";

	public static void main(String[] args) throws RemoteBackendException, FileNotFoundException {
		ScopeProvider.instance.set(scope);
		String toUpload="/home/fabio/Documents/Personal/DND/Incantesimi 3.5 - Mago e Stregone.pdf";
		String id=StorageUtils.putOntoStorage(new File(toUpload));
		System.out.println(StorageUtils.getUrlById(id));
	}

}
