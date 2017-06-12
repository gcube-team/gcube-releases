/**
 * 
 */
package org.gcube.datatransfer.scheduler.test;

import static org.gcube.common.core.contexts.GCUBEServiceContext.Status.READIED;
import static org.gcube.datatransfer.scheduler.impl.constants.Constants.FACTORY_NAME;
import static org.gcube.datatransfer.scheduler.impl.constants.Constants.MANAGEMENT_NAME;
import static org.gcube.datatransfer.scheduler.impl.constants.Constants.SCHEDULER_NAME;
import static org.gcube.datatransfer.scheduler.impl.context.ServiceContext.getContext;
import static org.gcube.datatransfer.scheduler.test.TestUtils.SAMPLE_GAR;
import static org.junit.Assert.assertTrue;

import javax.inject.Named;
import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.common.agent.Types.CancelTransferMessage;
import org.gcube.datatransfer.scheduler.impl.porttype.Factory;
import org.gcube.datatransfer.scheduler.impl.porttype.Management;
import org.gcube.datatransfer.scheduler.library.obj.InfoCancelSchedulerMessage;
import org.gcube.datatransfer.scheduler.stubs.datatransferscheduler.SchedulerPortType;
import org.gcube.datatransfer.scheduler.stubs.datatransferscheduler.service.SchedulerServiceAddressingLocator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis.wsrf.properties.GetResourcePropertyResponse;

@RunWith(MyContainerTestRunner.class)
public class SampleITTest {

	@Deployment
	static Gar serviceGar=SAMPLE_GAR();

	@Named(MANAGEMENT_NAME)
	static Management management;

	@Named(FACTORY_NAME)
	static Factory factory;

	@Named(SCHEDULER_NAME)
	static EndpointReferenceType schedulerEpr;

	@Named(MANAGEMENT_NAME)
	static EndpointReferenceType managementEpr;

	@BeforeClass
	public static void serviceHasStarted() {
		assertTrue(getContext().getStatus()==READIED);
	}

	@Test 
	public void uselessTest() throws Exception {
		//nothing
	}

	//@Test 
	public void schedulerTest() throws Exception {
		String name1="kostas";

		//-----------------------------------------//			
		// Access the Factory and get the Resource //
		//-----------------------------------------//

		// name1 is trying to log on again but he's already
		// registered so he is receiving the address for reusing 
		// the same resource 
		ScopeProvider.instance.set("/gcube/devsec"); 
		schedulerEpr= factory.create(name1);
		ScopeProvider.instance.set("/gcube/devsec"); 
		SchedulerPortType schedulerPT= 
				new SchedulerServiceAddressingLocator().getSchedulerPortTypePort(schedulerEpr);
		ScopeProvider.instance.set("/gcube/devsec"); 

		
		String transferId=null;
		
		//cancel this scheduled transfer
		//this.cancel(transferId, schedulerPT);	

		//monitor this scheduled transfer
		this.monitor(transferId, schedulerPT);

		//sleeping ..........
		try {Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.out.println("SampleITTest (schedulerTest) - InterruptedException: Unable to sleep");
			e.printStackTrace();
		}

		//get outcomes of this scheduled transfer
		this.getOutcomes(transferId, schedulerPT);

		//sleeping ..........
		try {				
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.out.println("SampleITTest (schedulerTest) - InterruptedException: Unable to sleep");
			e.printStackTrace();
		}

	}


	/*
	 * cancel
	 * input: String with the transferId of schedulerDB & specific SchedulerPortType
	 * return: Nothing
	 */
	public void cancel(String transferId, SchedulerPortType schedulerPT) throws Exception {	
		String result=null;

		InfoCancelSchedulerMessage infoCancelSchedulerMessage= new InfoCancelSchedulerMessage();

		CancelTransferMessage cancelTransferMessage = new CancelTransferMessage();
		cancelTransferMessage.setForceStop(true);
		cancelTransferMessage.setTransferId(transferId);

		infoCancelSchedulerMessage.setCancelTransferMessage(cancelTransferMessage);

		try{			
			String msgStr=infoCancelSchedulerMessage.toXML();
			result = schedulerPT.cancelScheduledTransfer(msgStr);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("SampleITTest (cancel) - Exception in calling the cancelScheduledTransfer(msgStr)");
		}		
		System.out.println("\nSampleITTest (cancel) - After canceling.. Cancel:\n"+result);
	}



	/*
	 * monitor
	 * input: String with the transferId of schedulerDB & specific SchedulerPortType
	 * return: Nothing
	 */
	public void monitor(String transferId, SchedulerPortType schedulerPT) throws Exception {
		String result=null;
		try{			
			result = schedulerPT.monitorScheduledTransfer(transferId);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("SampleITTest (monitor) - Exception in calling the monitorScheduledTransfer(transferId)");
		}

		System.out.println("\nSampleITTest (monitor) - After monitoring.. Monitor:\n"+result);
	}

	/*
	 * getOutcomes
	 * input: String with the transferId of schedulerDB & specific SchedulerPortType
	 * return: Nothing
	 */
	public void getOutcomes(String transferId, SchedulerPortType schedulerPT) throws Exception {
		String result=null;
		try{			
			result = schedulerPT.getScheduledTransferOutcomes(transferId);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("SampleITTest (getOutcomes) - Exception in calling the getScheduledTransferOutcomes(transferId)");
		}

		System.out.println("\nSampleITTest (getOutcomes) - After getOutcomes.. Outcomes:\n"+result);
	}

	/*
	 * retriveResource
	 * input: String with the name of resource(key)
	 * return: Nothing
	 */
	public void retriveResource(String name) throws Exception {
		String name1=name;

		//-----------------------------------------/			
		// Access the Factory and get the Resource /
		//-----------------------------------------/

		// name1 is trying to log on again but he's already
		// registered so he is receiving the address for reusing 
		// the same resource 
		ScopeProvider.instance.set("/gcube/devsec"); 
		schedulerEpr= factory.create(name1);
		ScopeProvider.instance.set("/gcube/devsec"); 
		SchedulerPortType schedulerPT= 
				new SchedulerServiceAddressingLocator().getSchedulerPortTypePort(schedulerEpr);
		ScopeProvider.instance.set("/gcube/devsec"); 


		//---------------------------------/			
		// Retrieve Info from the Resource /
		//---------------------------------/

		try{
			//we retrieve the info back from the resource as a xml String but
			//then we get the object (InfoSchedulerMessage) from the xml String
			QName temp= new QName("http://gcube-system.org/namespaces/datatransfer/scheduler/datatransferscheduler","SchedulerMessage");
			GetResourcePropertyResponse resourcePropResp=new GetResourcePropertyResponse();
			String returnedMsg = new String();

			ScopeProvider.instance.set("/gcube/devsec"); 
			resourcePropResp=schedulerPT.getResourceProperty(temp);
			ScopeProvider.instance.set("/gcube/devsec");
			returnedMsg=(String)resourcePropResp.get_any()[0].getValue();

			returnedMsg=returnedMsg.replaceAll("&lt;", "<");
			returnedMsg=returnedMsg.replaceAll("&gt;", ">");
			System.out.println("SampleITTest (retriveResource) - ReturnedXstream"+returnedMsg);
		}
		catch(Exception e){
			e.printStackTrace();
		}			

	}


}


