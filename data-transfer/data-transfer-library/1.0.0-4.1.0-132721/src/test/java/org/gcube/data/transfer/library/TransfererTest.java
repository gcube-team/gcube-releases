package org.gcube.data.transfer.library;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.data.transfer.library.faults.DestinationNotSetException;
import org.gcube.data.transfer.library.faults.FailedTransferException;
import org.gcube.data.transfer.library.faults.HostingNodeNotFoundException;
import org.gcube.data.transfer.library.faults.InitializationException;
import org.gcube.data.transfer.library.faults.InvalidDestinationException;
import org.gcube.data.transfer.library.faults.InvalidSourceException;
import org.gcube.data.transfer.library.faults.ServiceNotFoundException;
import org.gcube.data.transfer.library.faults.SourceNotSetException;
import org.gcube.data.transfer.library.faults.UnreachableNodeException;
import org.gcube.data.transfer.library.utils.StorageUtils;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TransfererTest {

	static String hostname="http://node3-d-d4s.d4science.org:80";
	static String nodeId="462b68c5-463f-4295-86da-37d6c0abc7ea";
	static String scope="/gcube/devsec";

	static DataTransferClient client;

	@BeforeClass
	public static void init() throws UnreachableNodeException, ServiceNotFoundException, HostingNodeNotFoundException{
		ScopeProvider.instance.set(scope);
		client=DataTransferClient.getInstanceByEndpoint(hostname);
		//		client=DataTransferClient.getInstanceByNodeId(nodeId);
	}

	@Test
	public void localFile() throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		for(int i=0;i<3;i++){
			String localFile="/home/fabio/Dropbox/Mindless/01- COMA - Mindless.mp3";
			String transferredFileName="Mindless.mp3";
			Destination dest=new Destination(transferredFileName);
			dest.setOnExistingFileName(DestinationClashPolicy.REWRITE);
			TransferResult res=client.localFile(localFile,dest);
			String remotePath=res.getRemotePath();		
			Assert.assertEquals(transferredFileName, remotePath.substring(remotePath.lastIndexOf(File.separatorChar)+1));
			System.out.println(res);
		}
	}


	@Test
	public void httpUrl() throws MalformedURLException, InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		String link="http://goo.gl/oLP7zG";
		System.out.println(client.httpSource(link,new Destination("Some import")));
	}


	@Test
	public void storage() throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, RemoteBackendException, FileNotFoundException, InvalidDestinationException, DestinationNotSetException{
		ScopeProvider.instance.set(scope);
		String toUpload="/home/fabio/Documents/Personal/DND/Incantesimi 3.5 - Mago e Stregone.pdf";
		String id=StorageUtils.putOntoStorage(new File(toUpload));
		Destination dest=new Destination("some/where","My Pdf.pdf");
		TransferResult res=client.storageId(id,dest);
		Assert.assertTrue(res.getRemotePath().contains(dest.getSubFolder()+File.separatorChar+dest.getDestinationFileName()));
		System.out.println(client.storageId(id,dest));
	}

	@Test(expected=InvalidSourceException.class)
	public void wrongStorage() throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		System.out.println(client.storageId("13245780t",new Destination("my file")));
	}

	@Test(expected=InvalidSourceException.class)
	public void wrongLocal() throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		String localFile="/home/fabio/Downloads/123045689.mp3";
		System.out.println(client.localFile(localFile,new Destination("12345")));
	}

	@Test(expected=InvalidSourceException.class)
	public void wrongUrl() throws MalformedURLException, InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		String link="https://www.some.where.com/over/theRainbow.txt";
		System.out.println(client.httpSource(link,new Destination("oz")));
	}
}
