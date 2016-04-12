package org.gcube.common.queueManager.test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import org.gcube.data.access.queueManager.model.CallBackItem;
import org.gcube.data.access.queueManager.model.LogItem;
import org.gcube.data.access.queueManager.model.RemoteExecutionStatus;
import org.gcube.data.access.queueManager.model.RequestItem;
import org.gcube.data.access.queueManager.utils.QueueXStream;
import org.junit.Test;



public class XstreamTest {

	@Test
	public void deserialization() {
		System.out.println("*************REQUEST************");		
		RequestItem request=new RequestItem("some/script", Arrays.asList(new String[]{
				"inputFile1","inputFile2"
		}), new HashMap<String, Serializable>());
		request.getParameters().put("Arg1", "Val1");
		System.out.println("Object : "+request);
		String xml=QueueXStream.get().toXML(request);
		System.out.println(xml);
		System.out.println("Read Object : "+QueueXStream.get().fromXML(xml));
		
		
		
		System.out.println("*************CALLBACK************");		
		CallBackItem callback=new CallBackItem(request.getId(), Arrays.asList(new String[]{
				"outFile1","outFile2"}), RemoteExecutionStatus.COMPLETED, "execution complete");
		System.out.println("Object : "+callback);
		String xmlCall=QueueXStream.get().toXML(callback);
		System.out.println(xmlCall);
		System.out.println("Read Object : "+QueueXStream.get().fromXML(xmlCall));
		
		
		System.out.println("*************LOG************");		
		LogItem log=new LogItem(request.getId(),RemoteExecutionStatus.STARTED, "current operation : dummy operation");
		System.out.println("Object : "+log);
		String xmlLog=QueueXStream.get().toXML(log);
		System.out.println(xmlLog);
		System.out.println("Read Object : "+QueueXStream.get().fromXML(xmlLog));
	}

	
}
